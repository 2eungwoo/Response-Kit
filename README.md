# Response-Kit  

> Spring Boot 애플리케이션에서 일관된 API 응답 구조와 표준화된 검증 오류 포맷을 손쉽게 구현할 수 있도록 설계된 경량 유틸리티 라이브러리입니다.

[![](https://jitpack.io/v/2eungwoo/Response-Kit.svg)](https://jitpack.io/#2eungwoo/Response-Kit)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)]()
[![Spring Boot](https://img.shields.io/badge/SpringBoot-3.x-brightgreen.svg)]()

<br/>

## 언제 쓰면 좋은지?
> 프로젝트 초기 단계에서 응답 포맷, 예외 처리, 검증 로직을 빠르게 통일하고 싶은 경우 아래 상황에 특히 유용합니다.

| 상황 | Response-Kit이 해결하는 문제 |
|------|---------------------------|
| `@Valid` 검증 실패 시 JSON 파싱 오류 | 자동으로 표준화된 `ErrorResponse` 반환 |
| 도메인별 응답코드 관리 어려움 | `ResponseCode` 인터페이스 기반으로 확장 가능 |
| 각 컨트롤러마다 try-catch 반복 | 전역 `GlobalExceptionHandler` 기본 제공 |
| 팀원 간 응답 포맷 불일치 | 통합된 `ApiResponse`로 일관성 확보 |
<br/>

## 이 라이브러리를 쓰면 이런 변화가 있습니다
> 🫨 before : Spring Boot 기본 응답
```json
{
  "timestamp": "2025-10-24T04:00:00",
  "status": 400,
  "error": "Bad Request",
  "path": "/api/signup"
}
```
> ✅ after : Response-Kit 응답
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

## 설치 및 구조

### **JitPack 설정**
JitPack을 통해 라이브러리를 사용하려면,
settings.gradle 또는 build.gradle에 다음 repository를 추가해야 합니다.
> `settings.gradle` 설정
```gradle
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```
> `build.gradle` 설정
```gradle
dependencies {
    implementation 'com.github.2eungwoo:Response-Kit:{version}'
    // {version}에 해당하는 값은 본 README 최상단에 명시된 JitPack version 입니다.
}


```

### 제공 클래스 구조
```
response-kit
 ㄴ ApiResponse.java         → 성공/실패 응답 통합 클래스 (success/fail)
 ㄴ ErrorResponse.java       → Validation 실패 시 응답 구조 포함
 ㄴ FieldError.java          → 필드 단위 검증 실패 정보
 ㄴ ResponseCode.java        → 공통 인터페이스 (도메인별 enum으로 확장 가능)
 ㄴ CommonResponseCode.java  → 기본 응답코드 (OK, BAD_REQUEST 등)
 ㄴ CustomException.java     → 베이스 예외 클래스  (도메인별 예외로 상속 가능)
 ㄴ GlobalExceptionHandler.java → 선택적 전역 예외 처리 핸들러
```

## 사용 예제
#### Controller 응답 사용 예시
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

#### 도메인별 응답 코드 확장 예시
> ResponseCode
> - `ResponseCode`는 인터페이스 기반이므로 각 도메인별 `enum`으로 확장하여 유지보수가 용이하게 설계되었습니다.
> - 기본 제공 `CommonResponseCode` 외에도 `UserResponseCode`, `OrderResponseCode` 등 자유롭게 확장 가능합니다.
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
<br/>

#### 도메인별 커스텀 예외 클래스 작성 예시
> CustomException
> - `CustomException`은 `ResponseCode`를 생성자에서 주입받아 도메인별 예외를 선언적으로 관리할 수 있도록 설계되었습니다.
> - 예를 들어, `DuplicateEmailException`은 `AuthResponseCode.DUPLICATE_EMAIL`를 전달받아 코드/메시지를 자동 설정합니다.  
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

#### ExceptionHandler 수정 예시
> - 해당 라이브러리에는 GlobalExceptionHandler 클래스가 내부에 포함되어 있습니다. 
> - 필요 시 프로젝트 내에서 오버라이드하거나 복사 수정해 커스터마이징할 수 있습니다.
```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 직접 작성한 오버라이드 핸들러 메소드 예시
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse.of(CommonResponseCode.FORBIDDEN));
    }
}
```


## License  
MIT License © 2025 [Seungwoo Lee](https://github.com/2eungwoo)

## Feedback  
오류 제보 및 지적 환영, [Issues](https://github.com/2eungwoo/response-kit/issues)에 남겨주시면 정말 정말 감사하겠습니다.
