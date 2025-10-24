package com.github.seungwoo.responsekit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.seungwoo.responsekit.example.SignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("api 응답 success의 경우 success필드에 true값, data에 email값이 나온다")
    void valid_success() throws Exception {
        SignupRequest req = new SignupRequest("홍길동", "test@example.com", "password123");

        mvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value("test@example.com"));
    }

    @Test
    @DisplayName("api 응답 fail 경우 success필드에 false값이 나온다")
    void invalid_request_returns_error() throws Exception {
        SignupRequest req = new SignupRequest("", "wrongemail", "123");

        mvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("유효하지 않은 요청 시 FieldError 목록이 정확히 보여진다.")
    void invalid_request_returns_field_errors() throws Exception {
        SignupRequest req = new SignupRequest("", "invalidemail", "123");

        mvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors[0].field").value("email"));
    }
}