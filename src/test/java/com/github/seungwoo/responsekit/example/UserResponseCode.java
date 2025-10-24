package com.github.seungwoo.responsekit.example;

import com.github.seungwoo.responsekit.shared.BaseResponseCode;
import org.springframework.http.HttpStatus;

/*
 * Copyright (c) 2025 seungwoo
 * Licensed under the MIT License. See the LICENSE file for details.
 */
public enum UserResponseCode implements BaseResponseCode {
    SIGNUP_SUCCESS("AUTH-001", "회원가입에 성공했습니다.", HttpStatus.OK),
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