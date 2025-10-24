package com.github.seungwoo.responsekit.example;

import com.github.seungwoo.responsekit.response.BaseResponseCode;
import org.springframework.http.HttpStatus;

public enum UserResponseCode implements BaseResponseCode {
    USER_NOT_FOUND("USER_NOT_FOUND", "존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND),
    DUPLICATED_EMAIL("DUPLICATED_EMAIL", "이미 가입된 이메일입니다.", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    UserResponseCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override public String getCode() { return code; }
    @Override public String getMessage() { return message; }
    @Override public HttpStatus getHttpStatus() { return httpStatus; }
}