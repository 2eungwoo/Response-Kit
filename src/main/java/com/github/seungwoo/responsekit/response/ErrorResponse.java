package com.github.seungwoo.responsekit.response;

import java.util.Collections;
import java.util.List;
import lombok.Getter;

/**
 * 실패(에러) 응답의 표준 구조를 정의하는 클래스
 * <p>
 * 검증 실패(@Valid), 비즈니스 예외(CustomException), 시스템 예외(Exception) 모두 이 클래스로 변환하여 응답에 전달할 수 있습니다.
 */
@Getter
public class ErrorResponse {

    private final boolean success;
    private final String code;
    private final String message;
    private final List<FieldError> errors;

    private ErrorResponse(BaseResponseCode code, List<FieldError> errors) {
        this.success = false;
        this.code = code.getCode();
        this.message = code.getMessage();
        this.errors = errors;
    }

    public static ErrorResponse of(BaseResponseCode code) {
        return new ErrorResponse(code, Collections.emptyList());
    }

    public static ErrorResponse of(BaseResponseCode code, List<FieldError> errors) {
        return new ErrorResponse(code, errors);
    }
}