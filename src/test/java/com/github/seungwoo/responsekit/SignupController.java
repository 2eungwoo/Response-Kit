package com.github.seungwoo.responsekit;

import com.github.seungwoo.responsekit.response.CommonResponseCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/signup")
public class SignupController {

    @PostMapping
    public ApiResponse<String> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.success(CommonResponseCode.SUCCESS, "회원가입 완료");
    }
}
