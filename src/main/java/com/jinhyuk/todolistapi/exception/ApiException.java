package com.jinhyuk.todolistapi.exception;

public class ApiException extends RuntimeException {
    private ErrorCode errorCode;

    ApiException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
