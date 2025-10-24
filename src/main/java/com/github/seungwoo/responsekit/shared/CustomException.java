package com.github.seungwoo.responsekit.shared;

import lombok.Getter;

/**
 * 도메인별 CustomException 상속용 추상 클래스.
 * <p>
 * 예시:
 * <pre>
 * public class UserNotFoundException extends CustomException {
 *     public UserNotFoundException() {
 *         super(CommonResponseCode.NOT_FOUND, "사용자를 찾을 수 없습니다.");
 *     }
 * }
 * </pre>
 */
@Getter
public abstract class CustomException extends RuntimeException {

    private final BaseResponseCode responseCode;

    protected CustomException(BaseResponseCode responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }
}