package com.github.seungwoo.responsekit.shared;

import com.github.seungwoo.responsekit.response.ErrorResponse;
import com.github.seungwoo.responsekit.response.FieldError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Copyright (c) 2025 seungwoo
 * Licensed under the MIT License. See the LICENSE file for details.
 *
 * 전역 예외 처리기
 *
 * <p>기본 검증(@Valid, @Validated)과 커스텀 예외(CustomException), 일반 예외(Exception)를
 * 각각 ErrorResponse로 변환하여 응답합니다.</p>
 * <p>
 * 사용자의 필요에 따라 직접 @RestControllerAdvice + @Order(1) 핸들러를 정의하면, 이 기본 핸들러보다 높은 우선순위로 적용되어 쉽게 오버라이드할 수 있습니다.
 */
@Slf4j
@RestControllerAdvice
@Order(100)
public class GlobalExceptionHandler {

    /**
     * @Valid @RequestBody 검증 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex) {
        List<FieldError> errors = FieldError.from(ex.getBindingResult());
        log.warn("[Validation] Body validation failed: {}", errors);
        return ResponseEntity
            .status(CommonResponseCode.BAD_REQUEST.getHttpStatus())
            .body(ErrorResponse.of(CommonResponseCode.BAD_REQUEST, errors));
    }

    /**
     * @Validated @ModelAttribute 검증 실패
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
        List<FieldError> errors = FieldError.from(ex.getBindingResult());
        log.warn("[Validation] Parameter validation failed: {}", errors);
        return ResponseEntity
            .status(CommonResponseCode.BAD_REQUEST.getHttpStatus())
            .body(ErrorResponse.of(CommonResponseCode.BAD_REQUEST, errors));
    }

    /**
     * @RequestParam, @PathVariable 등 ConstraintViolation 발생
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
        ConstraintViolationException ex) {
        List<FieldError> errors = new ArrayList<>();
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            errors.add(new FieldError(
                v.getPropertyPath().toString(),
                v.getInvalidValue() == null ? "" : v.getInvalidValue().toString(),
                v.getMessage()
            ));
        }
        log.warn("[Validation] Constraint violation: {}", errors);
        return ResponseEntity
            .status(CommonResponseCode.BAD_REQUEST.getHttpStatus())
            .body(ErrorResponse.of(CommonResponseCode.BAD_REQUEST, errors));
    }

    /**
     * HTTP Method 불일치 (ex: GET만 지원하는데 POST 요청)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("[HTTP Method Not Supported] {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(ErrorResponse.of(CommonResponseCode.METHOD_NOT_ALLOWED));
    }

    /**
     * Content-Type 불일치 (ex: application/json이 아닌 경우)
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        log.warn("[Unsupported Media Type] {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .body(ErrorResponse.of(CommonResponseCode.UNSUPPORTED_MEDIA_TYPE));
    }

    /**
     * 필수 요청 파라미터 누락
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestParameter(MissingServletRequestParameterException ex) {
        log.warn("[Missing Request Parameter] {}", ex.getParameterName());
        return ResponseEntity
            .status(CommonResponseCode.BAD_REQUEST.getHttpStatus())
            .body(ErrorResponse.of(CommonResponseCode.BAD_REQUEST));
    }

    /**
     * PathVariable 누락
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErrorResponse> handleMissingPathVariable(MissingPathVariableException ex) {
        log.warn("[Missing Path Variable] {}", ex.getVariableName());
        return ResponseEntity
            .status(CommonResponseCode.BAD_REQUEST.getHttpStatus())
            .body(ErrorResponse.of(CommonResponseCode.BAD_REQUEST));
    }

    /**
     * 요청 파라미터 타입 불일치 (ex: String → Integer 매핑 실패)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("[Type Mismatch] param: {}, requiredType: {}", ex.getName(), ex.getRequiredType());
        return ResponseEntity
            .status(CommonResponseCode.BAD_REQUEST.getHttpStatus())
            .body(ErrorResponse.of(CommonResponseCode.BAD_REQUEST));
    }

    /**
     * JSON 파싱 실패 (ex: JSON 형식 오류, 타입 불일치 등)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(HttpMessageNotReadableException ex) {
        log.warn("[JSON Parse Error] {}", ex.getMessage());
        return ResponseEntity
            .status(CommonResponseCode.BAD_REQUEST.getHttpStatus())
            .body(ErrorResponse.of(CommonResponseCode.BAD_REQUEST));
    }

    /**
     * IllegalArgumentException / IllegalStateException 등 비즈니스 로직 위반
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleIllegalState(Exception ex) {
        log.warn("[Illegal State] {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse.of(CommonResponseCode.CONFLICT));
    }

    /**
     * 사용자 정의 예외 (CustomException)
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        log.warn("[CustomException] {}: {}", ex.getResponseCode().getCode(), ex.getMessage());
        return ResponseEntity
            .status(ex.getResponseCode().getHttpStatus())
            .body(ErrorResponse.of(ex.getResponseCode()));
    }

    /**
     * 예상치 못한 서버 오류
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("[Server Error]", ex);
        return ResponseEntity
            .status(CommonResponseCode.INTERNAL_ERROR.getHttpStatus())
            .body(ErrorResponse.of(CommonResponseCode.INTERNAL_ERROR));
    }
}