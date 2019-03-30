package com.jinhyuk.todolistapi.exception;

public class InvalidArgumentApiException extends ApiException {
    public InvalidArgumentApiException(ErrorCode errorCode) {
        super(errorCode);
    }
}
