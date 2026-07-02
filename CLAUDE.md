# lxpnext Claude Code Guide

## 프로젝트 개요

이 프로젝트는 LXP(Learning eXperience Platform) 백엔드 프로젝트이다.

기술 스택:

- Java 17
- Spring Boot 3.3.5
- Gradle
- Spring Web
- Spring Data JPA
- Validation
- MySQL
- No Lombok

## Claude Code 사용 목적

Claude Code는 이 프로젝트에서 전체 구조를 마음대로 바꾸는 도구가 아니다.

Claude Code는 기존 구조를 이해하고, 작은 기능 단위로 안전하게 구현하는 보조 도구로 사용한다.

기본 작업 단위는 아래 중 하나이다.

- Controller API 하나
- Service 메서드 하나
- Repository 조회 메서드 하나
- DTO 하나
- 테스트 하나

## 현재 우선 적용 범위

우선 Claude Code 적용 범위는 `lecture` 도메인으로 제한한다.

대상 패키지:

```text
src/main/java/wanted/misojigi/lxpnext/lecture
```

예상 구조:

```text
lecture/
├─ controller
├─ service
├─ repository
├─ domain
└─ dto
```

## lecture 도메인 기준

Lecture 엔티티 기준:

- 테이블명: lectures
- id: Long, PK
- instructorId: Long, not null
- title: String, not null
- description: TEXT, nullable
- status: LectureStatus, enum string
- createdAt: BaseEntity에서 관리

LectureStatus:

- PUBLIC
- PRIVATE
- DELETED

기본 조회 정책:

- 공개 강의만 조회한다.
- 삭제된 강의는 조회하지 않는다.
- 목록 조회는 최신순 createdAt desc를 기본으로 한다.

## 구현 규칙

Claude는 코드를 수정하기 전에 반드시 아래 내용을 먼저 요약한다.

- 구현할 기능
- 요청 URL
- 요청 방식
- 요청/응답 형식
- 수정 예상 파일
- 테스트 방식

바로 코드부터 수정하지 않는다.

## 수정 범위 제한

기본적으로 한 번에 수정하는 파일은 3개에서 5개 이하로 제한한다.

사용자가 명시적으로 요청하지 않으면 아래 작업은 하지 않는다.

- 전체 패키지 구조 변경
- 대규모 리팩터링
- DB 스키마 전체 변경
- Security 설정 변경
- 인증/인가 기능 추가
- 프론트엔드 코드 수정
- Git commit
- Git push

## 코드 스타일

- Lombok을 사용하지 않는다.
- 생성자, getter는 직접 작성한다.
- 불필요한 setter를 만들지 않는다.
- Entity를 API 응답으로 직접 노출하지 않는다.
- 응답에는 DTO를 사용한다.
- Controller는 얇게 유지한다.
- 비즈니스 로직은 Service에 둔다.
- Repository는 조회 책임만 가진다.
- 기존 패키지 구조와 네이밍을 따른다.

## 강의 생성 API 1차 MVP 기준

강의 생성 API를 구현할 경우 1차 범위는 아래로 제한한다.

요청:

```http
POST /lectures
```

요청 JSON 예시:

```json
{
  "instructorId": 1,
  "title": "Spring Boot 기초",
  "description": "Spring Boot 입문 강의입니다."
}
```

응답 JSON 예시:

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
- 생성 시 status는 PUBLIC으로 저장한다.
- Entity를 응답으로 직접 반환하지 않는다.
- DTO를 사용한다.
- 회원, 수강, 후기, 질문, 답변 도메인은 건드리지 않는다.

## 테스트 규칙

가능하면 기능 구현 후 테스트를 추가한다.

테스트 실행 명령어:

```powershell
.\gradlew test
```

테스트가 실패하면 완료했다고 말하지 않는다.

실패 로그를 읽고, 가장 작은 수정으로 다시 통과시킨다.

## application 설정 주의

로컬 실행 설정은 아래 파일을 사용한다.

```text
src/main/resources/application.yml
src/main/resources/application-local.yml
```

`application-local.yml`에는 DB 비밀번호가 들어갈 수 있으므로 Git에 올리지 않는다.

`.gitignore`에 아래 항목이 포함되어야 한다.

```text
application-local.yml
application-*.yml
```

## Git 규칙

Claude는 사용자가 명시적으로 요청하지 않으면 commit하지 않는다.

Claude는 사용자가 명시적으로 요청하지 않으면 push하지 않는다.

작업 완료 후에는 아래 내용만 보고한다.

- 수정한 파일
- 구현한 기능
- 테스트 결과
- 남은 주의사항
- 추천 commit message

## 금지 사항

- 관련 없는 파일 수정 금지
- 불필요한 리팩터링 금지
- 사용자가 요청하지 않은 기능 추가 금지
- application-local.yml 내용 출력 금지
- DB 비밀번호 출력 금지
- Git push 금지
- 테스트 없이 완료 선언 금지