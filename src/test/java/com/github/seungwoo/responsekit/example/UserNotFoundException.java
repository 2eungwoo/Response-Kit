package com.github.seungwoo.responsekit.example;

import com.github.seungwoo.responsekit.shared.CustomException;

/*
 * Copyright (c) 2025 seungwoo
 * Licensed under the MIT License. See the LICENSE file for details.
 */
public class UserNotFoundException extends CustomException {
    public UserNotFoundException() {
        super(UserResponseCode.USER_NOT_FOUND, "해당 사용자를 찾을 수 없습니다.");
    }
}