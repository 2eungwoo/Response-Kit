package com.github.seungwoo.responsekit.example;

import com.github.seungwoo.responsekit.response.CustomException;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException() {
        super(UserResponseCode.USER_NOT_FOUND, "해당 사용자를 찾을 수 없습니다.");
    }
}