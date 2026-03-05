package com.chunfeng.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.chunfeng.enums.DateTimePatternEnum;
import com.chunfeng.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 
 *
 * @Author: chunfeng
 * @Date: 2025_09_16
 */public class UserInfo implements Serializable {

	/**
	 * 用户id主键
	 */
	private Long userId;

	/**
	 * 用户名称
	 */
	private String nickName;

	/**
	 * 用户邮件
	 */
	private String email;

	/**
	 * qq号
	 */
	private String qqOpenId;

	/**
	 * qq个人简介
	 */
	private String qqAvatar;

	/**
	 * 用户密码
	 */
	private String password;

	/**
	 * 用户创建时间
	 */
	@JsonFormat(pattern = "yyyy_MM_dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy_MM_dd HH:mm:ss")
	private Date joinTime;

	/**
	 * 最后一次登录时间
	 */
	@JsonFormat(pattern = "yyyy_MM_dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy_MM_dd HH:mm:ss")
	private Date lastLoginTime;

	/**
	 * 账号状态，0表示禁用，1表示使用
	 */
	private Integer status;

	/**
	 * 该账号已使用空间
	 */
	private Long userSpace;

	/**
	 * 该账号全部空间
	 */
	private Long totalSpace;

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getNickName() {
		return this.nickName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return this.email;
	}

	public void setQqOpenId(String qqOpenId) {
		this.qqOpenId = qqOpenId;
	}

	public String getQqOpenId() {
		return this.qqOpenId;
	}

	public void setQqAvatar(String qqAvatar) {
		this.qqAvatar = qqAvatar;
	}

	public String getQqAvatar() {
		return this.qqAvatar;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return this.password;
	}

	public void setJoinTime(Date joinTime) {
		this.joinTime = joinTime;
	}

	public Date getJoinTime() {
		return this.joinTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Date getLastLoginTime() {
		return this.lastLoginTime;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setUserSpace(Long userSpace) {
		this.userSpace = userSpace;
	}

	public Long getUserSpace() {
		return this.userSpace;
	}

	public void setTotalSpace(Long totalSpace) {
		this.totalSpace = totalSpace;
	}

	public Long getTotalSpace() {
		return this.totalSpace;
	}

	@Override
	public String toString() {
		return "用户id主键:" + (userId == null ? "空" : userId) + ",用户名称:" + (nickName == null ? "空" : nickName) + ",用户邮件:" + (email == null ? "空" : email) + ",qq号:" + (qqOpenId == null ? "空" : qqOpenId) + ",qq个人简介:" + (qqAvatar == null ? "空" : qqAvatar) + ",用户密码:" + (password == null ? "空" : password) + ",用户创建时间:" + (joinTime == null ? "空" : DateUtils.format(joinTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",最后一次登录时间:" + (lastLoginTime == null ? "空" : DateUtils.format(lastLoginTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",账号状态，0表示禁用，1表示使用:" + (status == null ? "空" : status) + ",该账号已使用空间:" + (userSpace == null ? "空" : userSpace) + ",该账号全部空间:" + (totalSpace == null ? "空" : totalSpace);
	}
}