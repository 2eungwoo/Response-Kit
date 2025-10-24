package com.github.seungwoo.responsekit.response;

import com.github.seungwoo.responsekit.shared.BaseResponseCode;
import lombok.Getter;

/**
 * Copyright (c) 2025 seungwoo
 * Licensed under the MIT License. See the LICENSE file for details.
 *
 * 모든 성공 응답의 표준 구조를 정의하는 클래스
 * <p>
 * 이 클래스를 통해 통일된 응답 포맷을 반환할 수 있습니다. 컨트롤러 메소드 사용 예시 return
 * ApiResponse.success(CommonResponseCode.SUCCESS, data);
 */
@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final String code;
    private final String message;
    private final T data;

    private ApiResponse(boolean success, BaseResponseCode code, T data) {
        this.success = success;
        this.code = code.getCode();
        this.message = code.getMessage();
        this.data = data;
    }

    public static <T> ApiResponse<T> success(BaseResponseCode code, T data) {
        return new ApiResponse<>(true, code, data);
    }

    public static ApiResponse<Void> success(BaseResponseCode code) {
        return new ApiResponse<>(true, code, null);
    }

    public static ApiResponse<Void> fail(BaseResponseCode code) {
        return new ApiResponse<>(false, code, null);
    }
}