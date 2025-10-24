package com.github.seungwoo.responsekit;

import com.github.seungwoo.responsekit.response.ErrorResponse;
import com.github.seungwoo.responsekit.example.UserNotFoundException;
import com.github.seungwoo.responsekit.example.UserResponseCode;
import org.junit.jupiter.api.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(99) // 라이브러리 기본 핸들러보다 높은 우선순위 테스트
public class CustomExceptionHandlerTest {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.of(UserResponseCode.USER_NOT_FOUND));
    }
}