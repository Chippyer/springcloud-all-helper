package com.ejoy.core.common.exception;

import lombok.Data;

/**
 * 响应异常，包含响应状态
 *
 * @author: chippy
 * @datetime 2020-12-28 16:34
 */
@Data
public class ResponseStatusException extends RuntimeException {

    private static final long serialVersionUID = -7482893367270488685L;

    private int status = 200;

    public ResponseStatusException(String message, int status) {
        super(message);
        this.status = status;
    }

    public ResponseStatusException(String message) {
        super(message);
    }

    public ResponseStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseStatusException(Throwable cause) {
        super(cause);
    }

    public ResponseStatusException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
