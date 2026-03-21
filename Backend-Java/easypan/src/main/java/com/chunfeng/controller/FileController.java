package com.chunfeng.controller;

import com.chunfeng.common.page.PageResult;
import com.chunfeng.dto.request.file.LoadFileListRequest;
import com.chunfeng.dto.request.file.UploadFileRequest;
import com.chunfeng.dto.response.file.LoadFileListResponse;
import com.chunfeng.dto.response.file.UploadResponse;
import com.chunfeng.entity.po.UserFilePO;
import com.chunfeng.entity.query.UserFileQuery;
import com.chunfeng.entity.vo.UserFileVO;
import com.chunfeng.enums.FileCategoryEnums;
import com.chunfeng.enums.FileTypeEnums;
import com.chunfeng.security.JwtUtil;
import com.chunfeng.service.file.FileService;
import com.chunfeng.utils.CommonUtils;
import com.chunfeng.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

/**
 * @ClassName FileController
 * @Author chunfeng
 * @Description
 * @date 2026/3/18 17:11
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/file")
@Tag(name = "文件管理", description = "文件上传、下载、列表等接口")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CommonUtils commonUtils;

    // 文件上传接口
    @PostMapping("/uploadFile")
    @Operation(summary = "文件上传")
    public Result<UploadResponse> uploadFile(@Valid UploadFileRequest request, HttpServletRequest httpServletRequest) {
        Long userId = jwtUtil.getUserIdFromAccessToken(commonUtils.extractToken(httpServletRequest));
        UploadResponse response = fileService.uploadFile(request, userId);
        return Result.success(response);
    }

    // 获取文件列表
    @PostMapping("/loadDataList")
    @Operation(summary = "获取文件列表")
    public Result<LoadFileListResponse> loadDataList(
            @RequestBody(required = false) LoadFileListRequest loadRequest,
            HttpServletRequest httpServletRequest) {

        String accessToken = commonUtils.extractToken(httpServletRequest);
        Long userId = jwtUtil.getUserIdFromAccessToken(accessToken);

        if (userId == null) {
            return Result.fail("未登录");
        }

        if (loadRequest == null) {
            loadRequest = new LoadFileListRequest();
        }

        // 转换为查询条件
        UserFileQuery query = new UserFileQuery();
        query.setPageNum(loadRequest.getPageNo());
        query.setPageSize(loadRequest.getPageSize());
        query.setUserId(userId.toString());
        query.setDelFlag(2); // 只查询正常文件

        // 处理分类和父目录
        if ("all".equals(loadRequest.getCategory())) {
            // 全部文件模式，使用 filePid 查询指定目录
            query.setFilePid(loadRequest.getFilePid());
            log.info("全部文件模式，filePid: {}", loadRequest.getFilePid());
        } else if (loadRequest.getCategory() != null && !loadRequest.getCategory().isEmpty()) {
            // 分类筛选模式（video/music/image/doc），查询所有该分类的文件
            query.setFilePid(null);
            query.setFolderType(0); // 只查文件，不包含文件夹
            query.setFileCategory(FileCategoryEnums.getByCode(loadRequest.getCategory()).getCategory());
        } else {
            // 默认模式，使用 filePid
            query.setFilePid(loadRequest.getFilePid());
        }

        // 文件名模糊搜索
        if (loadRequest.getFileNameFuzzy() != null && !loadRequest.getFileNameFuzzy().isEmpty()) {
            query.setFileNameLike(loadRequest.getFileNameFuzzy());
        }

        PageResult<UserFileVO> result = fileService.loadFileList(query);
        LoadFileListResponse response = convertToFrontFormat(result);

        return Result.success(response);
    }

    /**
     * 转换为前端需要的响应格式
     */
    private LoadFileListResponse convertToFrontFormat(PageResult<UserFileVO> pageResult) {
        LoadFileListResponse response = new LoadFileListResponse();
        response.setTotal(pageResult.getTotal());
        response.setPageNo(pageResult.getPageNum());
        response.setPageSize(pageResult.getPageSize());

        List<LoadFileListResponse.FileItemVO> list = pageResult.getRecords().stream()
            .map(this::convertToFileItemVO)
            .toList();
        response.setList(list);

        return response;
    }

    /**
     * 转换单个文件项
     */
    private LoadFileListResponse.FileItemVO convertToFileItemVO(UserFileVO vo) {
        LoadFileListResponse.FileItemVO item = new LoadFileListResponse.FileItemVO();
        item.setId(vo.getId());
        item.setFileId(vo.getFileId());
        item.setFileName(vo.getFileName());
        item.setFileSize(vo.getFileSize());
        item.setFolderType(vo.getFolderType());

        log.info("====== 转换文件信息 ======");
        log.info("文件名：{}, fileType: {}, folderType: {}, fileCategory: {}", 
            vo.getFileName(), vo.getFileType(), vo.getFolderType(), vo.getFileCategory());

        // 根据文件夹类型和 fileType 设置
        if (vo.getFolderType() == 1) {
            // 文件夹
            item.setFileType(5); // 其他
            item.setFileSuffix("");
            log.info("文件夹类型，设置 fileType=5");
        } else {
            // 文件 - 直接使用 VO 中的 fileType 字段
            item.setFileType(vo.getFileType());
            log.info("文件类型，使用原始 fileType: {}", vo.getFileType());

            // 从文件名提取后缀
            String fileName = vo.getFileName();
            int lastDotIndex = fileName.lastIndexOf(".");
            if (lastDotIndex > 0) {
                item.setFileSuffix(fileName.substring(lastDotIndex + 1).toLowerCase());
            } else {
                item.setFileSuffix("");
            }
        }

        // 封面图和状态（目前先设默认值）
        // 如果是图片，设置 fileCover 为 id（用户文件主键 ID），用于预览
        if (vo.getFileType() != null && vo.getFileType() == 3) {
            item.setFileCover(String.valueOf(vo.getId()));
            log.info("图片文件，设置 fileCover 为 id: {}", vo.getId());
        } else {
            item.setFileCover("");
        }
        item.setStatus(2); // 正常

        // 前端控制字段
        item.setShowOp(false);
        item.setShowEdit(false);
        item.setFileCategory(vo.getFileCategory());

        // 转换时间格式
        if (vo.getLastUpdateTime() != null) {
            item.setLastUpdateTime(vo.getLastUpdateTime().toString().replace("T", " "));
        }

        log.info("====== 转换完成，最终 fileType: {}, fileCover: {} ======", 
            item.getFileType(), item.getFileCover());

        return item;
    }

    // 获取文件内容
    @GetMapping("/getFile/{id}")
    @Operation(summary = "获取文件内容")
    public void getFile(@PathVariable Long id, 
                       @RequestParam(value = "type", defaultValue = "original") String type,
                       HttpServletRequest request, HttpServletResponse response) {
        Long userId = jwtUtil.getUserIdFromAccessToken(commonUtils.extractToken(request));
        if (userId == null) {
            response.setStatus(401);
            return;
        }
        
        fileService.getFile(id, userId, response);
    }

    // 获取图片
    @GetMapping("/getImage/{id}")
    @Operation(summary = "获取图片")
    public void getImage(@PathVariable Long id, 
                        HttpServletRequest request, HttpServletResponse response) {
        Long userId = jwtUtil.getUserIdFromAccessToken(commonUtils.extractToken(request));
        if (userId == null) {
            response.setStatus(401);
            return;
        }
        
        fileService.getFile(id, userId, response);
    }


    @GetMapping("/ts/getVideoInfo/{id}")
    @Operation(summary = "获取视频分片信息")
    public void getVideoInfo(@PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
        Long userId = jwtUtil.getUserIdFromAccessToken(commonUtils.extractToken(request));
        if (userId == null) {
            response.setStatus(401);
            return;
        }
        
        fileService.getVideoInfo(id, userId, response);
    }

    // 创建下载链接
    @GetMapping("/createDownloadUrl/{id}")
    @Operation(summary = "创建下载链接")
    public Result<String> createDownloadUrl(@PathVariable Long id, HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromAccessToken(commonUtils.extractToken(request));
        if (userId == null) {
            return Result.fail("未登录");
        }
        
        String downloadCode = fileService.createDownloadUrl(id, userId);
        return Result.success(downloadCode);
    }

    // 执行文件下载
    @GetMapping("/download/{downloadCode}")
    @Operation(summary = "执行文件下载")
    public void download(@PathVariable String downloadCode, HttpServletRequest request, HttpServletResponse response) {
        fileService.download(downloadCode, response);
    }

}
