package com.chippy.core.common.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 默认API层返回结果类
 *
 * @author chippy
 */
@Data
public class DefaultResultImpl<T> implements Result<T>, Serializable {

    private int code;
    private String errorMsg;
    private T data;

    public static <T> DefaultResultImpl<T> success() {
        return new DefaultResultImpl<>();
    }

    public static <T> DefaultResultImpl<T> success(T data) {
        return new DefaultResultImpl<>(data);
    }

    public static <T> DefaultResultImpl<T> fail(int code, String errorMsg) {
        return new DefaultResultImpl<>(code, errorMsg);
    }

    @Override
    public int definitionSuccessCode() {
        return 0;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public T getData() {
        return this.data;
    }

    @SuppressWarnings("unchecked")
    private DefaultResultImpl() {
        this.code = this.definitionSuccessCode();
        this.data = (T)Boolean.TRUE;
        this.errorMsg = null;
    }

    private DefaultResultImpl(int code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
        this.data = null;
    }

    private DefaultResultImpl(T data) {
        this.code = this.definitionSuccessCode();
        this.data = data;
        this.errorMsg = null;
    }

}
