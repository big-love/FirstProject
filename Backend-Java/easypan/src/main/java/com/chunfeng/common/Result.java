package com.chunfeng.common;

import com.chunfeng.enums.ResultCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一响应结果类
 *
 * @author chunfeng
 * @date 2026/3/5
 * @param <T> 响应数据类型
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 私有构造函数
     */
    private Result() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 私有构造函数（完整参数）
     */
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // ==================== 成功响应 ====================

    /**
     * 成功响应（带数据和消息）
     */
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 成功响应（仅带数据）
     */
    public static <T> Result<T> success(T data) {
        return success(data, ResultCode.SUCCESS.getMessage());
    }

    /**
     * 成功响应（仅带消息）
     */
    public static <T> Result<T> success(String message) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, null);
    }

    /**
     * 成功响应（无数据无消息）
     */
    public static <T> Result<T> success() {
        return success(ResultCode.SUCCESS.getMessage());
    }

    // ==================== 失败响应 ====================

    /**
     * 失败响应（完整参数）
     */
    public static <T> Result<T> fail(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }

    /**
     * 失败响应（错误码和消息）
     */
    public static <T> Result<T> fail(Integer code, String message) {
        return fail(code, message, null);
    }

    /**
     * 失败响应（仅消息）
     */
    public static <T> Result<T> fail(String message) {
        return fail(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message);
    }

    /**
     * 失败响应（使用ResultCode枚举）
     */
    public static <T> Result<T> fail(ResultCode resultCode) {
        return fail(resultCode.getCode(), resultCode.getMessage());
    }

    /**
     * 失败响应（使用ResultCode枚举和自定义消息）
     */
    public static <T> Result<T> fail(ResultCode resultCode, String customMessage) {
        return fail(resultCode.getCode(), customMessage);
    }

    // ==================== 判断方法 ====================

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode().equals(this.code);
    }

    /**
     * 判断是否失败
     */
    public boolean isError() {
        return !isSuccess();
    }
}