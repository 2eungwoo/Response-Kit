# 🗃️ Response-Kit  
스프링 부트 프로젝트에서 **일관된 API 응답 구조**와 **검증 실패 시 표준화된 에러 포맷**을 제공하는 경량 유틸리티 라이브러리입니다.  
서비스 전반에 `ApiResponse`, `ErrorResponse`, `FieldError`, `ResponseCode` 등 통합 응답 포맷을 쉽게 적용할 수 있습니다.


### 주요 특징
| 기능 | 설명 |
|------|------|
| **성공/실패 응답 통일화** | `ApiResponse.success()`, `ApiResponse.fail()`로 일관된 구조 제공 |
| **Spring Validation 완전 지원** | `@Valid`, `@Validated` 실패 시 `ErrorResponse` 자동 반환 |
| **도메인별 확장 가능한 응답 코드** | `ResponseCode` 인터페이스로 커스텀 응답코드 확장 가능 |
| **간단한 통합 구조** | 별도의 설정 없이 import만으로 사용 가능 |
| **선택적 전역 예외 처리 제공** | `GlobalExceptionHandler` 기본 제공 (선택적으로 수정/제외 가능) |
<br/>

# 🎃 이 라이브러리를 쓰면 이런 변화가 있습니다
### before
```json
{
  "timestamp": "2025-10-24T04:00:00",
  "status": 400,
  "error": "Bad Request",
  "path": "/api/signup"
}
```
### after
```json
{
  "success": false,
  "code": "BAD_REQUEST",
  "message": "요청이 잘못되었습니다.",
  "errors": [
    {
      "field": "name",
      "rejectedValue": "",
      "reason": "이름은 필수입니다."
    },
    {
      "field": "email",
      "rejectedValue": "wrongemail",
      "reason": "이메일 형식이 아닙니다."
    },
    {
      "field": "password",
      "rejectedValue": "123",
      "reason": "비밀번호는 8자 이상이어야 합니다."
    }
  ]
}
```

</br>

# 🍪 설치 및 사용 방법

### **JitPack 설정**

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

### 제공 클래스 구조
```
response-kit
 ㄴ ApiResponse.java         → 성공/실패 응답 통합 클래스
 ㄴ ErrorResponse.java       → Validation 실패 시 응답 구조
 ㄴ FieldError.java          → 필드 단위 검증 실패 정보
 ㄴ ResponseCode.java        → 공통 인터페이스 (도메인별 확장용)
 ㄴ CommonResponseCode.java  → 기본 응답코드 (OK, BAD_REQUEST 등)
 ㄴ CustomException.java     → 서비스 전역 커스텀 예외 베이스 클래스
```

## 🔹 사용 예시 1. - Controller 응답
```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(
            @Valid @RequestBody SignupRequest request) {
        AuthResponse response = authService.signup(request);
        return ApiResponse.success(AuthResponseCode.OK, response); 
    }
}
```
<br/>

## 🔹 사용 예시 2. - 예외 커스텀
### CustomException & ResponseCode 확장성
- `CustomException`은 `ResponseCode`를 생성자에서 주입받아 도메인별 예외를 선언적으로 관리할 수 있도록 설계되었습니다.
- 예를 들어, `DuplicateEmailException`은 `AuthResponseCode.DUPLICATE_EMAIL`를 전달받아 코드/메시지를 자동 설정합니다.  
- `ResponseCode`는 인터페이스 기반이므로 각 도메인별 `enum`으로 확장하여 유지보수가 용이합니다.
- 기본 제공 `CommonResponseCode` 외에도 `UserResponseCode`, `OrderResponseCode` 등 자유롭게 확장 가능합니다.

```java
public class DuplicateEmailException extends CustomException {
    public DuplicateEmailException() {
        super(AuthrResponseCode.DUPLICATE_EMAIL);
    }
}

// ...

if (userRepository.existsByEmail(request.getEmail())) {
    throw new DuplicateEmailException();
    }
```

<br/>

## 🔹 사용 예시 3. - ExceptionHandler
> 해당 라이브러리에는 GlobalExceptionHandler 클래스가 내부에 포함되어 있습니다.
> 
> 필요 시 프로젝트 내에서 오버라이드하거나 복사 수정해 사용할 수 있습니다.
```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        log.warn("[CustomException] {}", ex.getResponseCode().message());
        return ResponseEntity
                .status(ex.getResponseCode().status())
                .body(ErrorResponse.of(ex.getResponseCode(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex) {
        var errors = FieldError.from(ex.getBindingResult());
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(CommonResponseCode.BAD_REQUEST, errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownError(Exception ex) {
        log.error("[Unhandled Exception]", ex);
        return ResponseEntity
                .internalServerError()
                .body(ErrorResponse.of(CommonResponseCode.INTERNAL_ERROR));
    }
}
```

<br/>

## 🔹 사용 예시 4. - 도메인별 응답 코드 확장
```java
@Getter
@RequiredArgsConstructor
public enum AuthResponseCode implements ResponseCode {
    SIGNUP_SUCCESS(HttpStatus.CREATED, "AUTH001", "회원가입 성공"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "AUTH409", "이미 존재하는 이메일입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
```

