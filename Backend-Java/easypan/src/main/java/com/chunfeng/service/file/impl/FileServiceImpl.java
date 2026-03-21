package com.chunfeng.service.file.impl;

import com.chunfeng.common.page.PageResult;
import com.chunfeng.componet.RedisComponent;
import com.chunfeng.dto.request.file.UploadFileRequest;
import com.chunfeng.dto.response.file.FilePreviewResponse;
import com.chunfeng.dto.response.file.UploadResponse;
import com.chunfeng.entity.constants.Constants;
import com.chunfeng.entity.dto.DownloadFileDto;
import com.chunfeng.entity.po.StorageFilePO;
import com.chunfeng.entity.po.UserFilePO;
import com.chunfeng.entity.query.UserFileQuery;
import com.chunfeng.entity.vo.UserFileVO;
import com.chunfeng.enums.FileCategoryEnums;
import com.chunfeng.enums.FileFolderTypeEnums;
import com.chunfeng.enums.FileTypeEnums;
import com.chunfeng.exception.BusinessException;
import com.chunfeng.mapper.StorageFileMapper;
import com.chunfeng.mapper.UserFileMapper;
import com.chunfeng.service.file.FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.time.LocalDateTime;

/**
 * @ClassName FileServiceImpl
 * @Author chunfeng
 * @Description
 * @date 2026/3/18 17:38
 * @Version 1.0
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private StorageFileMapper storageFileMapper;
    @Autowired
    private UserFileMapper userFileMapper;
    @Autowired
    private RedisComponent redisComponent;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Autowired
    private HttpServletRequest request;

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
        vo.setFileCategory(po.getFileCategory());
        vo.setFileType(po.getFileType());
        return vo;
    }

    @Override
    public UploadResponse uploadFile(UploadFileRequest request, Long userId) {
        // 从文件名获取文件类型
        FileTypeEnums fileType = FileTypeEnums.getFileTypeBySuffix(getFileSuffix(request.getFileName()));
        FileCategoryEnums fileCategory = fileType.getCategory();
        Integer fileTypeCode = fileType.getType();
        
        // todo 存在并发问题，minion 上传，消息队列
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

                UserFilePO userFile = new UserFilePO();
                userFile.setFileId(existStorage.getId());
                userFile.setUserId(userId);
                userFile.setStorageId(existStorage.getId());
                userFile.setFilePid(request.getFilePid());
                userFile.setFileName(request.getFileName());
                userFile.setFileSize(existStorage.getFileSize());
                userFile.setFolderType(0);
                userFile.setDelFlag(2);
                userFile.setFileCategory(fileCategory.getCategory());
                userFile.setFileType(fileTypeCode);
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
            storageFile.setFileCategory(fileCategory.getCategory());
            storageFile.setFileType(fileTypeCode);
            storageFileMapper.insert(storageFile);
            StorageFilePO storageFilePO = storageFileMapper.selectByMd5(storageFile.getFileMd5());

            UserFilePO userFile = new UserFilePO();
            userFile.setFileId(storageFilePO.getId());
            userFile.setUserId(userId);
            userFile.setStorageId(storageFile.getId());
            userFile.setFilePid(request.getFilePid());
            userFile.setFileName(request.getFileName());
            userFile.setFileSize(fileSize);
            userFile.setFolderType(0);
            userFile.setDelFlag(2);
            userFile.setFileCategory(fileCategory.getCategory());
            userFile.setFileType(fileTypeCode);
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

        // 第一级：年月（用于时间维度管理）
        String yearMonth = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        // 第二级：MD5 前两位（用于分散文件，避免单目录过大）
        String md5SubDir = fileName.substring(0, 2);
        
        String storageDir = finalDir + "/" + yearMonth + "/" + md5SubDir;
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

    private String getFileSuffix(String fileName) {
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot < 0) {
            return "";
        }
        return fileName.substring(lastDot).toLowerCase();
    }

    @Override
    public FilePreviewResponse getFilePreview(String fileId, Long userId) {
        // 1. 查询用户文件
        UserFilePO userFile = userFileMapper.selectByFileIdAndUserId(fileId, userId);
        if (userFile == null) {
            throw new BusinessException("文件不存在或无权限访问");
        }
        
        // 2. 检查文件状态
        if (!userFile.getDelFlag().equals(2)) {
            throw new BusinessException("文件已被删除或在回收站");
        }
        
        // 3. 查询物理文件信息
        StorageFilePO storageFile = storageFileMapper.selectById(userFile.getStorageId());
        if (storageFile == null) {
            throw new BusinessException("物理文件不存在");
        }
        
        // 4. 构建预览响应
        FilePreviewResponse response = new FilePreviewResponse();
        response.setFileId(fileId);
        response.setFileName(userFile.getFileName());
        response.setFileType(userFile.getFileType());
        response.setFileCategory(userFile.getFileCategory());
        response.setFileSize(userFile.getFileSize());
        
        // 5. 根据文件类型设置预览方式
        Integer fileType = userFile.getFileType();
        
        // 文本类型（7:普通文本，8:代码文件等）直接返回内容
        if (fileType == 7 || fileType == 8) {
            response.setPreviewType("content");
            try {
                response.setContent(readFileContent(storageFile.getFilePath()));
            } catch (IOException e) {
                throw new BusinessException("读取文件内容失败");
            }
        } else {
            // 其他类型（图片、视频、音频、PDF 等）返回 URL
            response.setPreviewType("url");
            // 使用绝对路径作为 URL（前端会通过后端接口访问）
            response.setPreviewUrl("/chunfeng/file/content/" + fileId);
        }
        
        return response;
    }

    @Override
    public UserFilePO getFileInfo(String fileId, Long userId) {
        return userFileMapper.selectByFileIdAndUserId(fileId, userId);
    }

    /**
     * 读取文本文件内容
     */
    private String readFileContent(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    @Override
    public void getFile(Long id, Long userId, HttpServletResponse response) {
        // 1. 根据主键 ID 和用户 ID 查询用户文件
        UserFilePO userFile = userFileMapper.selectById(id);
        if (userFile == null || !userFile.getUserId().equals(userId)) {
            response.setStatus(404);
            return;
        }
        
        // 2. 检查文件状态
        if (!userFile.getDelFlag().equals(2)) {
            response.setStatus(403);
            return;
        }
        
        // 3. 查询物理文件信息
        StorageFilePO storageFile = storageFileMapper.selectById(userFile.getStorageId());
        if (storageFile == null) {
            response.setStatus(404);
            return;
        }
        
        // 4. 读取文件并返回
        File file = new File(storageFile.getFilePath());
        if (!file.exists()) {
            response.setStatus(404);
            return;
        }
        
        try {
            // 设置 Content-Type
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            response.setContentType(contentType);
            
            // 设置 Content-Length
            response.setContentLength((int) file.length());
            
            // 流式传输文件
            try (var inputStream = Files.newInputStream(file.toPath());
                 var outputStream = response.getOutputStream()) {
                inputStream.transferTo(outputStream);
                outputStream.flush();
            }
        } catch (Exception e) {
            log.error("读取文件失败", e);
            response.setStatus(500);
        }
    }

    @Override
    public void getVideoInfo(Long id, Long userId, HttpServletResponse response) {
        // 1. 查询用户文件
        UserFilePO userFile = userFileMapper.selectById(id);
        if (userFile == null || !userFile.getUserId().equals(userId)) {
            response.setStatus(404);
            return;
        }
        
        // 2. 检查是否为视频
        if (userFile.getFileType() != 1) {
            response.setStatus(400);
            return;
        }
        
        // 3. 查询物理文件
        StorageFilePO storageFile = storageFileMapper.selectById(userFile.getStorageId());
        if (storageFile == null) {
            response.setStatus(404);
            return;
        }
        
        File videoFile = new File(storageFile.getFilePath());
        if (!videoFile.exists()) {
            response.setStatus(404);
            return;
        }
        
        // 4. 判断请求类型（m3u8 或 ts 分片）
        String range = request.getHeader("Range");
        if (range != null && range.startsWith("bytes=")) {
            // 支持断点续传
            handlePartialContent(videoFile, response, range);
        } else {
            // 返回完整文件或 m3u8 索引
            handleFullContent(videoFile, response, storageFile);
        }
    }

    /**
     * 处理部分内容请求（断点续传）
     */
    private void handlePartialContent(File videoFile, HttpServletResponse response, String range) {
        try {
            String[] ranges = range.replace("bytes=", "").split("-");
            long start = Long.parseLong(ranges[0]);
            long end = Math.min(start + 1024 * 1024, videoFile.length() - 1); // 每次 1MB
            
            response.setStatus(206);
            response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + videoFile.length());
            response.setHeader("Accept-Ranges", "bytes");
            response.setContentType("video/mp4");
            
            try (var inputStream = Files.newInputStream(videoFile.toPath());
                 var outputStream = response.getOutputStream()) {
                inputStream.skip(start);
                byte[] buffer = new byte[8192];
                long remaining = end - start + 1;
                
                while (remaining > 0) {
                    int read = inputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                    if (read == -1) break;
                    outputStream.write(buffer, 0, read);
                    remaining -= read;
                }
                outputStream.flush();
            }
        } catch (Exception e) {
            log.error("处理部分视频内容失败", e);
            response.setStatus(500);
        }
    }

    /**
     * 处理完整内容请求
     */
    private void handleFullContent(File videoFile, HttpServletResponse response, StorageFilePO storageFile) {
        try {
            // 对于 HLS 流媒体，应该生成 m3u8 索引文件
            // 这里简化处理，直接返回视频文件
            response.setContentType("video/mp4");
            response.setHeader("Accept-Ranges", "bytes");
            
            try (var inputStream = Files.newInputStream(videoFile.toPath());
                 var outputStream = response.getOutputStream()) {
                inputStream.transferTo(outputStream);
                outputStream.flush();
            }
        } catch (Exception e) {
            log.error("返回完整视频失败", e);
            response.setStatus(500);
        }
    }

    @Override
    public String createDownloadUrl(Long id, Long userId) {
        // 1. 验证文件权限
        UserFilePO userFile = userFileMapper.selectById(id);
        if (userFile == null || !userFile.getUserId().equals(userId)) {
            throw new BusinessException("文件不存在");
        }
        
        // 2. 生成下载码（存到 Redis）
        String downloadCode = java.util.UUID.randomUUID().toString().replace("-", "");
        
        // 3. 将文件信息存入 Redis（10 分钟有效期）
        DownloadFileDto downloadDto = new DownloadFileDto();
        downloadDto.setDownloadCode(downloadCode);
        downloadDto.setFileName(userFile.getFileName());
        
        StorageFilePO storageFile = storageFileMapper.selectById(userFile.getStorageId());
        downloadDto.setFilePath(storageFile.getFilePath());
        
        redisComponent.setex(Constants.REDIS_KEY_DOWNLOAD + downloadCode,
                            downloadDto,
                            Constants.REDIS_DOWNLOAD_TEN_MIN);
        
        return downloadCode;
    }

    @Override
    public void download(String downloadCode, HttpServletResponse response) {

    }

    @Override
    public void download(String downloadCode, Long userId, HttpServletResponse response) {
        // 1. 从 Redis 获取下载信息
        DownloadFileDto downloadDto = (DownloadFileDto) redisComponent.get(Constants.REDIS_KEY_DOWNLOAD + downloadCode);
        if (downloadDto == null) {
            response.setStatus(404);
            return;
        }
        
        // 2. 验证文件是否存在
        File file = new File(downloadDto.getFilePath());
        if (!file.exists()) {
            response.setStatus(404);
            return;
        }
        
        try {
            // 3. 设置下载头
            String fileName = URLEncoder.encode(downloadDto.getFileName(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName);
            response.setContentType("application/octet-stream");
            response.setContentLength((int) file.length());
            
            // 4. 流式传输
            try (var inputStream = Files.newInputStream(file.toPath());
                 var outputStream = response.getOutputStream()) {
                inputStream.transferTo(outputStream);
                outputStream.flush();
            }
            
            // 5. 删除下载码（一次性使用）
            redisComponent.del(Constants.REDIS_KEY_DOWNLOAD + downloadCode);
        } catch (Exception e) {
            log.error("文件下载失败", e);
            response.setStatus(500);
        }
    }

    @Override
    public UserFileVO rename(String fileId, String fileName, Long userId) {
        // 1. 查询文件
        UserFilePO userFile = userFileMapper.selectByFileId(fileId);
        if (userFile == null) {
            throw new BusinessException("文件不存在");
        }
        
        // 2. 验证权限
        if (!userFile.getUserId().equals(userId)) {
            throw new BusinessException("无权限操作该文件");
        }
        
        // 3. 检查同目录下是否已存在同名文件
        UserFilePO existFile = userFileMapper.selectByUserIdAndPidAndName(
            userId, userFile.getFilePid(), fileName
        );
        if (existFile != null && !existFile.getFileId().equals(fileId)) {
            throw new BusinessException("同目录下已存在同名文件");
        }
        
        // 4. 更新文件名
        String newFileName = fileName;
        // 如果是文件（不是文件夹），需要保留原后缀
        if (userFile.getFolderType() == 0) {
            String originalName = userFile.getFileName();
            int lastDotIndex = originalName.lastIndexOf(".");
            if (lastDotIndex > 0) {
                String suffix = originalName.substring(lastDotIndex);
                newFileName = fileName + suffix;
            }
        }
        
        userFile.setFileName(newFileName);
        userFileMapper.updateById(userFile);
        
        return convertToVO(userFile);
    }

    @Override
    public UserFileVO newFolder(String filePid, String fileName, Long userId) {
        // 1. 检查同目录下是否已存在同名文件夹
        UserFilePO existFile = userFileMapper.selectByUserIdAndPidAndName(
            userId, filePid, fileName
        );
        if (existFile != null) {
            throw new BusinessException("同目录下已存在同名文件夹");
        }
        
        // 2. 创建文件夹
        UserFilePO folder = new UserFilePO();
        folder.setUserId(userId);
        folder.setFileId(0L);
        folder.setStorageId(0L); // 文件夹没有物理存储
        folder.setFilePid(filePid);
        folder.setFileName(fileName);
        folder.setFileSize(0L);
        folder.setFolderType(FileFolderTypeEnums.FOLDER.getType()); // 1: 文件夹
        folder.setDelFlag(2); // 2: 正常
        folder.setFileCategory(FileCategoryEnums.OTHERS.getCategory()); // 文件夹没有分类
        folder.setFileType(0); // 文件夹类型为 0
        
        userFileMapper.insert(folder);
        
        return convertToVO(folder);
    }

    @Override
    public List<UserFileVO> getFolderInfo(String path, Long userId) {
        List<UserFileVO> folderList = new java.util.ArrayList<>();
        
        // 如果 path 为空或为 "0"，表示根目录，返回空列表
        if (path == null || path.isEmpty() || "0".equals(path)) {
            return folderList;
        }
        
        try {
            // 分割路径，获取所有文件夹 ID（这些 ID 是数据库主键 id）
            String[] folderIds = path.split("/");
            
            // 依次查询每个文件夹的信息
            for (String folderIdStr : folderIds) {
                if (folderIdStr == null || folderIdStr.isEmpty()) {
                    continue;
                }
                
                Long folderId = Long.parseLong(folderIdStr);
                
                // 直接通过主键 ID 查询
                UserFilePO folder = userFileMapper.selectById(folderId);
                
                if (folder == null) {
                    throw new BusinessException("文件夹不存在");
                }
                
                // 验证权限：确保该文件夹属于当前用户且是文件夹类型
                if (!folder.getUserId().equals(userId)) {
                    throw new BusinessException("无权限访问该文件夹");
                }
                
                if (!folder.getFolderType().equals(1)) {
                    throw new BusinessException("该文件不是文件夹");
                }
                
                if (!folder.getDelFlag().equals(2)) {
                    throw new BusinessException("文件夹已被删除或在回收站");
                }
                
                folderList.add(convertToVO(folder));
            }
        } catch (NumberFormatException e) {
            throw new BusinessException("路径格式错误");
        }
        
        return folderList;
    }

    /**
     * 生成文件 ID
     */
    private String generateFileId() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

}
