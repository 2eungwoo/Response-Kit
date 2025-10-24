package com.github.seungwoo.responsekit.response;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.BindingResult;


/**
 * Copyright (c) 2025 seungwoo
 * Licensed under the MIT License. See the LICENSE file for details.
 *
 * Validation 실패 시 어떤 필드에서 어떤 오류가 발생했는지 나타내는 DTO 클래스
 */
@Getter
@AllArgsConstructor
public class FieldError {

    private final String field;
    private final String rejectedValue;
    private final String reason;

    /**
     * BindingResult → FieldError 변환
     */
    public static List<FieldError> from(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
            .map(error -> new FieldError(
                error.getField(),
                error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                error.getDefaultMessage()
            ))
            .collect(Collectors.toList());
    }
}
