package com.github.seungwoo.responsekit.shared;

import org.springframework.http.HttpStatus;

/**
 * Copyright (c) 2025 seungwoo
 * Licensed under the MIT License. See the LICENSE file for details.
 *
 * 모든 도메인 ResponseCode Enum에서 구현해야 하는 인터페이스
 * <p>
 * 공통 코드(CommonResponseCode)와 도메인별 코드(UserResponseCode, OrderResponseCode 등) 모두 이 인터페이스를 구현하도록 설계되어
 * 있습니다.
 */
public interface BaseResponseCode {

    // 비즈니스 코드 문자열
    // (예: "USER_NOT_FOUND", "INTERNAL_ERROR")
    String getCode();

    // 사용자 또는 개발자에게 보여줄 메시지
    String getMessage();

    /// HTTP 상태 코드
    HttpStatus getHttpStatus();
}