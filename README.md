# 🌱 Response-Kit  
스프링 부트 프로젝트에서 **일관된 API 응답 구조**와 **검증 실패 시 표준화된 에러 포맷**을 제공하는 경량 유틸리티 라이브러리입니다.  
서비스 전반에 `ApiResponse`, `ErrorResponse`, `FieldError`, `ResponseCode` 등 통합 응답 포맷을 쉽게 적용할 수 있습니다.

</br>

## 주요 특징
| 기능 | 설명 |
|------|------|
| **성공/실패 응답 통일화** | `ApiResponse.success()`, `ApiResponse.fail()`로 일관된 구조 제공 |
| **Spring Validation 완전 지원** | `@Valid`, `@Validated` 실패 시 `ErrorResponse` 자동 반환 |
| **도메인별 확장 가능한 응답 코드** | `ResponseCode` 인터페이스로 커스텀 응답코드 확장 가능 |
| **간단한 통합 구조** | 별도의 설정 없이 import만으로 사용 가능 |
| **선택적 전역 예외 처리 제공** | `GlobalExceptionHandler` 기본 제공 (선택적으로 수정/제외 가능) |
</br>

## 🍪 설치 방법

### 1. **JitPack 설정**

`build.gradle`
```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.2eungwoo:response-kit:1.0.0'
}
```
</br>

## 📦 제공 클래스 구조
```
📦 response-kit
 ┣ 📜 ApiResponse.java         → 성공/실패 응답 통합 클래스
 ┣ 📜 ErrorResponse.java       → Validation 실패 시 응답 구조
 ┣ 📜 FieldError.java          → 필드 단위 검증 실패 정보
 ┣ 📜 ResponseCode.java        → 공통 인터페이스 (도메인별 확장용)
 ┣ 📜 CommonResponseCode.java  → 기본 응답코드 (OK, BAD_REQUEST 등)
 ┗ 📜 CustomException.java     → 서비스 전역 커스텀 예외 베이스 클래스
```

## 사용 예시 - Controller 응답
```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signup(
            @Valid @RequestBody SignupRequest request) {
        UserResponse response = userService.signup(request);
        return ApiResponse.success(CommonResponseCode.OK, response);
    }
}
```
## 사용 예시 - 예외 커스텀
```java
```

## 사용 예시 - GlobalExceptionHandler

