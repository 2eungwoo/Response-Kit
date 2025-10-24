package com.github.seungwoo.responsekit.response;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
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