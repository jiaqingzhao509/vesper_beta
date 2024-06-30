package com.example.common.utils;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * 统一定义请求响应数据格式
 *
 * @author tycoding
 * 
 */
@Data
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code = HttpStatus.OK.value();

    private String msg = HttpStatus.OK.name();

    private T result;

    public R() {
        super();
    }

    public R(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    protected R(T result) {
        this.result = result;
    }

    protected R(HttpStatus httpCode) {
        this.code = httpCode.value();
        this.msg = httpCode.name();
    }

    protected R(T result, HttpStatus httpStatus) {
        this.result = result;
        this.code = httpStatus.value();
        this.msg = httpStatus.name();
    }

    protected R(Throwable e) {
        super();
        this.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.msg = e.getMessage();
    }

    public static <T> R<T> ok(T result) {
        return new R(result);
    }

    public static <T> R<T> ok(T result, HttpStatus httpStatus) {
        return new R(result, httpStatus);
    }

    public static <T> R<T> ok() {
        return new R<>();
    }

    public static <T> R<T> ok(String msg) {
        return new R<>(HttpStatus.OK.value(), msg);
    }

    public static <T> R<T> ok(HttpStatus httpStatus) {
        return new R<>(httpStatus);
    }

    public static <T> R<T> fail(String msg) {
        return new R<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg);
    }

    public static <T> R<T> fail(HttpStatus httpStatus) {
        return new R<>(httpStatus);
    }

    public static <T> R<T> fail(Throwable e) {
        return new R<>(e);
    }
}
