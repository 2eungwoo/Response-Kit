package com.github.seungwoo.responsekit.example;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
    @NotBlank(message = "이름은 필수입니다.")
    String name,

    @Email(message = "이메일 형식이 아닙니다.")
    String email,

    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    String password
) {}