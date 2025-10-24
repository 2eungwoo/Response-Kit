package com.github.seungwoo.responsekit.shared;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Copyright (c) 2025 seungwoo
 * Licensed under the MIT License. See the LICENSE file for details.
 *
 * 공통적으로 사용되는 HTTP 상태 기반 응답 코드 Enum
 * <p>
 * 이 코드는 모든 프로젝트에서 바로 사용할 수 있으며, 필요한 경우 BaseResponseCode를 구현한 도메인별 Enum을 추가 정의할 수 있습니다.
 */
@Getter
@RequiredArgsConstructor
public enum CommonResponseCode implements BaseResponseCode {

    SUCCESS("SUCCESS", "요청이 성공적으로 처리되었습니다.", HttpStatus.OK),
    BAD_REQUEST("BAD_REQUEST", "요청이 잘못되었습니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("UNAUTHORIZED", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("FORBIDDEN", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    NOT_FOUND("NOT_FOUND", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTERNAL_ERROR("INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "허용되지 않은 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    CONFLICT("CONFLICT", "요청 충돌이 발생했습니다.", HttpStatus.CONFLICT),
    UNSUPPORTED_MEDIA_TYPE("UNSUPPORTED_MEDIA_TYPE", "지원하지 않는 Content-Type입니다.", HttpStatus.UNSUPPORTED_MEDIA_TYPE);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}