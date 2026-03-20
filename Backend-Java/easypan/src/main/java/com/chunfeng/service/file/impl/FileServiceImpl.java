package com.chunfeng.service.file.impl;

import com.chunfeng.common.page.PageResult;
import com.chunfeng.dto.request.file.UploadFileRequest;
import com.chunfeng.dto.response.file.UploadResponse;
import com.chunfeng.entity.po.StorageFilePO;
import com.chunfeng.entity.po.UserFilePO;
import com.chunfeng.entity.query.UserFileQuery;
import com.chunfeng.entity.vo.UserFileVO;
import com.chunfeng.exception.BusinessException;
import com.chunfeng.mapper.StorageFileMapper;
import com.chunfeng.mapper.UserFileMapper;
import com.chunfeng.service.file.FileService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * @ClassName FileServiceImpl
 * @Author chunfeng
 * @Description
 * @date 2026/3/18 17:38
 * @Version 1.0
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private StorageFileMapper storageFileMapper;
    @Autowired
    private UserFileMapper userFileMapper;

    @Value("${file.upload.path}")
    private String uploadPath;


    @Override
    public PageResult<UserFileVO> loadFileList(UserFileQuery query) {
        query.validate();
        
        List<UserFilePO> poList = userFileMapper.selectByQuery(query);
        Long total = userFileMapper.countByQuery(query);
        
        List<UserFileVO> voList = poList.stream()
            .map(this::convertToVO)
            .toList();
        
        return PageResult.of(
            query.getPageNum(),
            query.getPageSize(),
            total,
            voList
        );
    }

    private UserFileVO convertToVO(UserFilePO po) {
        UserFileVO vo = new UserFileVO();
        vo.setId(po.getId());
        vo.setFileId(po.getFileId());
        vo.setUserId(po.getUserId().toString());
        vo.setStorageId(po.getStorageId());
        vo.setFilePid(po.getFilePid());
        vo.setFileName(po.getFileName());
        vo.setFileSize(po.getFileSize());
        vo.setFolderType(po.getFolderType());
        vo.setDelFlag(po.getDelFlag());
        vo.setCreateTime(po.getCreateTime());
        vo.setLastUpdateTime(po.getLastUpdateTime());
        return vo;
    }

    @Override
    public UploadResponse uploadFile(UploadFileRequest request, Long userId) {
        // todo 存在并发问题，minion上传，消息队列
        // 1.首次上传检查
        if (request.getChunkIndex() == 0) {
            // 1.1 检查同目录是否存在同名文件
            UserFilePO existUserFile = userFileMapper.selectByUserIdAndPidAndName(
                userId, request.getFilePid(), request.getFileName()
            );
            if (existUserFile != null) {
                throw new BusinessException("当前目录下已存在同名文件");
            }

            // 1.2 检查秒传
            StorageFilePO existStorage = storageFileMapper.selectByMd5(request.getFileMd5());
            if (existStorage != null) {
                storageFileMapper.updateRefCount(existStorage.getId(), 1);

                String fileId = RandomStringUtils.randomAlphanumeric(32);
                UserFilePO userFile = new UserFilePO();
                userFile.setFileId(fileId);
                userFile.setUserId(userId);
                userFile.setStorageId(existStorage.getId());
                userFile.setFilePid(request.getFilePid());
                userFile.setFileName(request.getFileName());
                userFile.setFileSize(existStorage.getFileSize());
                userFile.setFolderType(0);
                userFile.setDelFlag(2);
                userFileMapper.insert(userFile);
                return new UploadResponse(existStorage.getFilePath(), "upload_seconds");
            }
        }

        // 2.保存分片
        String tempDir = uploadPath + "/temp/" + request.getFileName();
        File dir = new File(tempDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            request.getFile().transferTo(new File(tempDir + "/" + request.getChunkIndex()));
        } catch (IOException e) {
            throw new BusinessException("分片保存失败");
        }

        // 3.最后一个分片，合并文件
        if (request.getChunkIndex().equals(request.getChunks() - 1)) {
            // 3.0 再次检查同目录是否存在同名文件（防止并发上传）
            UserFilePO existUserFile = userFileMapper.selectByUserIdAndPidAndName(
                userId, request.getFilePid(), request.getFileName()
            );
            if (existUserFile != null) {
                deleteDirectory(new File(tempDir));
                throw new BusinessException("当前目录下已存在同名文件");
            }

            String physicalPath = mergeChunks(request.getFileName(), request.getChunks());
            long fileSize = new File(physicalPath).length();

            StorageFilePO storageFile = new StorageFilePO();
            storageFile.setFileMd5(request.getFileMd5());
            storageFile.setFileSize(fileSize);
            storageFile.setFilePath(physicalPath);
            storageFile.setRefCount(1);
            storageFileMapper.insert(storageFile);

            UserFilePO userFile = new UserFilePO();
            userFile.setFileId(request.getFileId());
            userFile.setUserId(userId);
            userFile.setStorageId(storageFile.getId());
            userFile.setFilePid(request.getFilePid());
            userFile.setFileName(request.getFileName());
            userFile.setFileSize(fileSize);
            userFile.setFolderType(0);
            userFile.setDelFlag(2);
            userFileMapper.insert(userFile);

            deleteDirectory(new File(tempDir));

            return new UploadResponse(request.getFileId(), "upload_finish");
        }

        return new UploadResponse(request.getFileId(), "uploading");
    }
    private String mergeChunks(String fileName, int chunks) {
        String tempDir = uploadPath +"/temp/" + fileName;
        String finalDir = uploadPath + "/storage";
        new File(finalDir).mkdirs();

        // 使用MD5前两位作为子目录，避免单目录文件过多
        String subDir = fileName.substring(0, 2);
        String storageDir = finalDir + "/" + subDir;
        new File(storageDir).mkdirs();

        String finalPath = storageDir + "/" + fileName;

        try (FileOutputStream fos = new FileOutputStream(finalPath)) {
            for (int i = 0; i < chunks; i++) {
                Files.copy(new File(tempDir + "/" + i).toPath(), fos);
            }
        } catch (IOException e) {
            throw new BusinessException("文件合并失败");
        }

        return finalPath;
    }

    private void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
        dir.delete();
    }
}
