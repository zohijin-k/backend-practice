# Backend Practice — 도서 관리 REST API

## 1. 프로젝트 소개

Spring Boot를 활용하여 도서 등록, 조회, 수정, 삭제 및 대출·반납 기능을 구현한 REST API 실습 프로젝트입니다.

## 2. 개발 환경

- Java 21
- Spring Boot 3.5.6
- Gradle
- Spring Web
- In-memory 데이터 저장소 (ConcurrentHashMap)

## 3. 실행 방법

1. Java 21 환경을 준비합니다.
2. 프로젝트를 IntelliJ IDEA로 엽니다.
3. `BackendPracticeApplication.java`를 실행합니다.
4. 서버 주소: `http://localhost:8080`

## 4. API 목록

| HTTP 메서드 | 경로 | 기능 |
|---|---|---|
| POST | `/api/books` | 도서 한 권 등록 |
| POST | `/api/books/bulk` | 여러 도서 등록 |
| GET | `/api/books` | 전체 도서 조회 |
| GET | `/api/books/{id}` | 특정 도서 조회 |
| PUT | `/api/books/{id}` | 도서 정보 수정 |
| PUT | `/api/books/{id}/borrow` | 도서 대출 |
| DELETE | `/api/books/{id}` | 도서 삭제 |
| DELETE | `/api/books/{id}/borrow` | 도서 반납 |

## 5. 공통 응답 형식

```json
{
  "status": 200,
  "message": "도서 목록 조회 성공",
  "data": []
}
```

모든 응답은 `status`, `message`, `data` 필드를 사용합니다.

## 6. HTTP 상태 코드 테스트

- `200 OK`: 조회, 수정, 대출, 반납, 삭제 성공
- `201 Created`: 도서 등록 성공
- `400 Bad Request`: 필수 입력값 누락 또는 잘못된 요청
- `404 Not Found`: 존재하지 않는 도서 조회
- `500 Internal Server Error`: 서버 오류 응답 테스트
- `503 Service Unavailable`: 서비스 이용 불가 응답 테스트

## 7. 미들웨어 및 예외 처리

- `RequestLoggingFilter`: HTTP 메서드, 요청 경로, 응답 코드, 처리 시간 기록
- `GlobalExceptionHandler`: 잘못된 요청, 리소스 없음, 일반 예외를 공통 JSON 형식으로 반환

## 8. 테스트

`curl` 명령어를 사용해 POST, GET, PUT, DELETE 요청과 HTTP 상태 코드별 응답을 확인했습니다.

도서 데이터는 메모리에 저장되므로 서버를 재시작하면 초기화됩니다.