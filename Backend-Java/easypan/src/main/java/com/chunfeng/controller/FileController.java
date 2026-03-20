package com.chunfeng.controller;

import com.chunfeng.common.page.PageResult;
import com.chunfeng.dto.request.file.LoadFileListRequest;
import com.chunfeng.dto.request.file.UploadFileRequest;
import com.chunfeng.dto.response.file.LoadFileListResponse;
import com.chunfeng.dto.response.file.UploadResponse;
import com.chunfeng.entity.query.UserFileQuery;
import com.chunfeng.entity.vo.UserFileVO;
import com.chunfeng.security.JwtUtil;
import com.chunfeng.service.file.FileService;
import com.chunfeng.utils.CommonUtils;
import com.chunfeng.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @ClassName FileController
 * @Author chunfeng
 * @Description
 * @date 2026/3/18 17:11
 * @Version 1.0
 */
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
        
        // 处理父目录
        if ("all".equals(loadRequest.getCategory())) {
            // 全部文件模式，使用 filePid
            query.setFilePid(loadRequest.getFilePid());
        } else {
            // 分类筛选模式，不传 filePid，按文件类型筛选
            query.setFilePid(null);
            // 根据 category 设置 folderType（只查文件）
            query.setFolderType(0);
            // TODO: 根据 category 设置具体的文件类型过滤
        }
        
        // 文件名模糊搜索
        if (loadRequest.getFileNameFuzzy() != null && !loadRequest.getFileNameFuzzy().isEmpty()) {
            query.setFileNameLike(loadRequest.getFileNameFuzzy());
        }
        
        PageResult<UserFileVO> result = fileService.loadFileList(query);
        
        // 转换为前端需要的格式
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
        item.setFileId(vo.getFileId());
        item.setFileName(vo.getFileName());
        item.setFileSize(vo.getFileSize());
        item.setFolderType(vo.getFolderType());
        
        // 根据文件名解析文件类型和后缀
        if (vo.getFolderType() == 1) {
            // 文件夹
            item.setFileType(5); // 其他
            item.setFileSuffix("");
        } else {
            // 文件
            String fileName = vo.getFileName();
            int lastDotIndex = fileName.lastIndexOf(".");
            if (lastDotIndex > 0) {
                String suffix = fileName.substring(lastDotIndex + 1).toLowerCase();
                item.setFileSuffix(suffix);
                item.setFileType(determineFileType(suffix));
            } else {
                item.setFileSuffix("");
                item.setFileType(5); // 其他
            }
        }
        
        // 封面图和状态（目前先设默认值）
        item.setFileCover("");
        item.setStatus(2); // 正常
        
        // 前端控制字段
        item.setShowOp(false);
        item.setShowEdit(false);
        
        // 转换时间格式
        if (vo.getLastUpdateTime() != null) {
            item.setLastUpdateTime(vo.getLastUpdateTime().toString().replace("T", " "));
        }
        
        return item;
    }
    
    /**
     * 根据文件后缀判断文件类型
     * 1-视频，2-音频，3-图片，4-文档，5-其他
     */
    private Integer determineFileType(String suffix) {
        if (suffix == null) {
            return 5;
        }
        
        // 视频
        if (Set.of("mp4", "avi", "mkv", "flv", "wmv", "mov").contains(suffix)) {
            return 1;
        }
        
        // 音频
        if (Set.of("mp3", "wav", "flac", "aac", "ogg", "wma").contains(suffix)) {
            return 2;
        }
        
        // 图片
        if (Set.of("jpg", "jpeg", "png", "gif", "bmp", "webp", "svg").contains(suffix)) {
            return 3;
        }
        
        // 文档
        if (Set.of("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "md").contains(suffix)) {
            return 4;
        }
        
        return 5; // 其他
    }
}
