package com.jinhyuk.todolistapi.exception;

public enum ErrorCode {
    PRE_TASKS_NOT_FOUND("참조한 할 일을 찾을 수 없습니다."),
    TASK_TITLE_IS_EMPTY("할 일 이름이 없습니다.");

    private String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
