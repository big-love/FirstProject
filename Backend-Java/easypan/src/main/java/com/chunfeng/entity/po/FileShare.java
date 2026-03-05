package com.chunfeng.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.chunfeng.enums.DateTimePatternEnum;
import com.chunfeng.utils.DateUtils;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 分享信息表
 *
 * @Author: chunfeng
 * @Date: 2025_11_18
 */public class FileShare implements Serializable {

     /*
     *
     * */
    String fileName;

    String fileCover;

    public String getFolderType() {
        return folderType;
    }

    public void setFolderType(String folderType) {
        this.folderType = folderType;
    }

    String folderType;

    public String getFileCategory() {
        return fileCategory;
    }

    public void setFileCategory(String fileCategory) {
        this.fileCategory = fileCategory;
    }

    public String getFileCover() {
        return fileCover;
    }

    public void setFileCover(String fileCover) {
        this.fileCover = fileCover;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    String fileType;
    String fileCategory;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
	 * 分享id
	 */
	private String shareId;

	/**
	 * 文件id
	 */
	private String fileId;

	/**
	 * 分享人id
	 */
	private String userId;

	/**
	 * 有效时间，0：1天，1：7天，2：30天，4：永久有效
	 */
	private Integer validType;

	/**
	 * 过期时间
	 */
	@JsonFormat(pattern = "yyyy_MM_dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy_MM_dd HH:mm:ss")
	private Date expireTime;

	/**
	 * 分享时间
	 */
	@JsonFormat(pattern = "yyyy_MM_dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy_MM_dd HH:mm:ss")
	private Date shareTime;

	/**
	 * 提取码
	 */
	private String code;

	/**
	 * 查看次数
	 */
	private Integer showCount;

	public void setShareId(String shareId) {
		this.shareId = shareId;
	}

	public String getShareId() {
		return this.shareId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileId() {
		return this.fileId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setValidType(Integer validType) {
		this.validType = validType;
	}

	public Integer getValidType() {
		return this.validType;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public Date getExpireTime() {
		return this.expireTime;
	}

	public void setShareTime(Date shareTime) {
		this.shareTime = shareTime;
	}

	public Date getShareTime() {
		return this.shareTime;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public void setShowCount(Integer showCount) {
		this.showCount = showCount;
	}

	public Integer getShowCount() {
		return this.showCount;
	}

	@Override
	public String toString() {
		return "分享id:" + (shareId == null ? "空" : shareId) + ",文件id:" + (fileId == null ? "空" : fileId) + ",分享人id:" + (userId == null ? "空" : userId) + ",有效时间，0：1天，1：7天，2：30天，4：永久有效:" + (validType == null ? "空" : validType) + ",过期时间:" + (expireTime == null ? "空" : DateUtils.format(expireTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",分享时间:" + (shareTime == null ? "空" : DateUtils.format(shareTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",提取码:" + (code == null ? "空" : code) + ",查看次数:" + (showCount == null ? "空" : showCount);
	}
}