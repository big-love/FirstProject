package com.chunfeng.vo;

import com.chunfeng.enums.ResultCode;
import lombok.Data;

/**
 * @ClassName Result
 * @Author chunfeng
 * @Description
 * @date 2026/3/3 10:39
 * @Version 1.0
 */
@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public Result(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /* ========== 成功响应 ========== */

    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS, data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /* ========== 失败响应 ========== */

    public static <T> Result<T> fail() {
        return new Result<>(ResultCode.INTERNAL_SERVER_ERROR, null);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message, null);
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode, null);
    }

    public static <T> Result<T> fail(ResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message, null);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /* ========== 特定场景响应(保留你原有的方法) ========== */

    public static <T> Result<T> paramError(String message) {
        return new Result<>(ResultCode.BAD_REQUEST.getCode(), message, null);
    }

    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(ResultCode.UNAUTHORIZED.getCode(), message, null);
    }

    public static <T> Result<T> forbidden(String message) {
        return new Result<>(ResultCode.FORBIDDEN.getCode(), message, null);
    }


}
