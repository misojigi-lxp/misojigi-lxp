---
name: spring-api
description: lxpnext 프로젝트에서 Spring Boot REST API 기능을 작은 단위로 구현할 때 사용하는 Skill입니다. 기존 구조를 확인하고, DTO/Service/Controller/Test 순서로 안전하게 작업합니다.
disable-model-invocation: true
---

# Spring API Skill

## 목표

이 Skill은 lxpnext 프로젝트에서 Spring Boot REST API 기능을 작은 단위로 안전하게 구현하기 위한 작업 절차이다.

Claude는 큰 기능을 한 번에 구현하지 않는다.
기존 구조를 먼저 확인하고, 하나의 API 기능 단위로만 작업한다.

## 우선 적용 범위

우선 적용 범위는 `lecture` 도메인이다.

대상 패키지:

```text
src/main/java/wanted/misojigi/lxpnext/lecture
```

관련 없는 도메인은 수정하지 않는다.

수정 금지 도메인:

- member
- enrollment
- learninggoal
- question
- answer
- review

사용자가 명시적으로 요청하지 않는 한 위 도메인은 건드리지 않는다.

## 필수 작업 순서

### 1. 요구사항 정리

코드를 수정하기 전에 먼저 아래 내용을 짧게 정리한다.

- 구현할 기능
- 요청 URL
- HTTP Method
- 요청 JSON
- 응답 JSON
- 수정 예상 파일
- 테스트 방식

바로 코드부터 수정하지 않는다.

### 2. 기존 코드 확인

수정 전에 반드시 기존 구조를 확인한다.

확인 대상:

- 기존 Lecture Entity
- 기존 LectureStatus
- 기존 LectureRepository
- 기존 LectureService
- 기존 LectureController
- 기존 DTO
- 기존 테스트 구조

패키지명, 클래스명, 메서드명을 추측해서 새로 만들지 않는다.
이미 비슷한 클래스가 있으면 기존 구조를 따른다.

### 3. 수정 범위 제한

기본 수정 범위는 3개에서 5개 파일 이내로 제한한다.

필요한 경우에만 아래 파일을 수정한다.

- Request DTO
- Response DTO
- Service
- Controller
- Test

아래 작업은 하지 않는다.

- 전체 패키지 구조 변경
- 대규모 리팩터링
- 인증/인가 구현
- Security 설정 변경
- DB 스키마 전체 변경
- 프론트엔드 수정
- Git commit
- Git push

## 구현 규칙

- Lombok을 사용하지 않는다.
- Entity를 API 응답으로 직접 반환하지 않는다.
- 요청/응답에는 DTO를 사용한다.
- Controller는 얇게 유지한다.
- 비즈니스 로직은 Service에 둔다.
- Repository는 조회/저장 책임만 가진다.
- 기존 코드 스타일을 따른다.
- 불필요한 setter를 만들지 않는다.
- 생성자와 getter는 직접 작성한다.

## 강의 생성 API 기준

강의 생성 API를 구현할 경우 아래 기준을 따른다.

요청:

```http
POST /lectures
```

요청 JSON:

```json
{
  "instructorId": 1,
  "title": "Spring Boot 기초",
  "description": "Spring Boot 입문 강의입니다."
}
```

응답 JSON:

```json
{
  "lectureId": 1,
  "instructorId": 1,
  "title": "Spring Boot 기초",
  "description": "Spring Boot 입문 강의입니다.",
  "status": "PUBLIC"
}
```

규칙:

- 지금은 인증/인가를 구현하지 않는다.
- instructorId는 요청값으로 받는다.
- title은 필수이며 blank면 안 된다.
- description은 null 가능하다.
- 생성 시 LectureStatus.PUBLIC로 저장한다.
- createdAt은 BaseEntity가 처리한다.
- Entity를 직접 응답하지 않는다.

## 테스트 규칙

구현 후 가능한 경우 테스트를 추가한다.

테스트 실행 명령어:

```powershell
.\gradlew test
```

테스트가 실패하면 완료했다고 말하지 않는다.

실패 시:

1. 실패 로그를 읽는다.
2. 가장 작은 원인을 찾는다.
3. 필요한 부분만 수정한다.
4. 다시 테스트를 실행한다.

## 최종 보고 형식

작업 완료 후 아래 내용을 짧게 보고한다.

```text
[수정한 파일]

[구현한 기능]

[테스트 결과]

[남은 주의사항]

[추천 커밋 메시지]
```

## 금지 사항

- 요청하지 않은 기능 추가 금지
- 관련 없는 도메인 수정 금지
- 전체 구조 리팩터링 금지
- application-local.yml 내용 출력 금지
- DB 비밀번호 출력 금지
- 테스트 없이 완료 선언 금지
- 사용자가 요청하지 않은 Git commit 금지
- Git push 금지