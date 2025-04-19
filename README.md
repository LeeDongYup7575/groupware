# 🏢 Groupware Project - 실시간 협업 시스템

![GitHub last commit](https://img.shields.io/github/last-commit/username/repo-name)  
![GitHub repo size](https://img.shields.io/github/repo-size/username/repo-name)  
![GitHub license](https://img.shields.io/github/license/username/repo-name)  
![Spring Boot](https://img.shields.io/badge/SpringBoot-2.7-green?logo=springboot&logoColor=white)  
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.0-brightgreen?logo=thymeleaf&logoColor=white)  
![Flutter](https://img.shields.io/badge/Flutter-Mobile-blue?logo=flutter&logoColor=white)  
![WebRTC](https://img.shields.io/badge/WebRTC-RealTime-red?logo=webrtc&logoColor=white)  
![WebSocket](https://img.shields.io/badge/STOMP-WebSocket-blue?logo=websocket)  
![MyBatis](https://img.shields.io/badge/MyBatis-Mapper-orange?logo=java)  
![MongoDB](https://img.shields.io/badge/MongoDB-NoSQL-darkgreen?logo=mongodb)  
![AWS](https://img.shields.io/badge/AWS-EC2%20%7C%20S3-orange?logo=amazonaws&logoColor=white)

> 이메일, 메시지(실시간 채팅), 게시판, 화상회의, 전자결재, 출결관리 등 조직 내 전반적인 협업 기능을 통합한 풀스택 그룹웨어 시스템입니다.

---

## 📸 Preview 
(이미지 추가 예정)

| Web (PC) 버전 | 모바일 앱 (Flutter) |
|---------------|---------------------|
| ![web-preview](https://via.placeholder.com/400x250?text=Web+Main) | ![app-preview](https://via.placeholder.com/200x400?text=QR+Check-in) |
---
### 메인 페이지
### 조직도
### 이메일
### 메시지(실시간 채팅)
### 근태관리
### 전자결재
### 업무관리
### 회의실/비품 예약
### 주소록
### 게시판
### 화상회의
### 그 외
| QR체크                            | 마이페이지                                   | FAQs |
|---------------------------------|-----------------------------------------|------|
| ![main](./screenshots/main.png) | ![approval](./screenshots/approval.png) | 하이   |

---

## 📂 프로젝트 구조 및 흐름도

### 📘 시스템 아키텍처 (System Flow)

```
[사용자]
   ↓
[Web/Mobile Client]
   ↓
+---------------------------+
|  Spring Boot Application |
+---------------------------+
        ↓            ↘
     [MySQL]      [MongoDB]
        ↓               ↓
    업무/출결/전자결재   채팅/화상/WebRTC 로그 등

        ↑
        ↕
[WebSocket | STOMP]
   ↕                        ↘
실시간 채팅, 알림        WebRTC 화상회의

        ↓
   외부 연동 서비스
      ↘
 +--------------------+          +------------------------+
 |     hMailServer    | ⇆ SMTP ⇆ | RoundCube Web Client  |
 +--------------------+          +------------------------+

사용자 RoundCube 접속 → 메일 송수신 (SMTP/IMAP) 처리
```

> 📍 **WebRTC 및 STOMP 채널**은 STOMP 브로커와 직접 연결되어 실시간 메시징/화상 데이터 송수신을 처리

### 📐 ERD (MySQL 기반)

```
최종본 추가 예정
```

> MongoDB는 실시간 채팅 기록에 사용됩니다.

---

## 🧑‍💻 팀 구성 및 역할

| 이름  | 역할                               | 주요 작업                                                                                        |
|-----|----------------------------------|----------------------------------------------------------------------------------------------|
| 이종훈 | 팀장, 프로젝트 일정 관리, 공식 문서 작업, 풀스택 개발 | 전자결재 (지출결의서, 품의서, 업무연락 등 문서 양식 지원 및 결재선 지정 기능)                                               |
| 강윤진 | DB 관리자, 풀스택 개발                   | 이메일 서버 시스템 구축/클라이언트 연동, 그룹웨어와 이메일 서버(외부) 주소록 연동, 마이페이지                                       |
| 김미랑 | 회의록 작성/관리, UI/UX 디자인, 풀스택 개발     | 커뮤니티 게시판, 조직도, 반응형 웹 개발, 공통 헤더/사이드바, intro/main 페이지                                          |
| 박주혁 | Figma 관리/지도, 풀스택 개발              | 근태관리 (출퇴근 기록, 연장근무/휴가 신청, 캘린더 제공, 스케줄러 활용 연차 생성/결근/퇴근 로직 자동화)                                |
| 이동엽 | Mongo DB 관리자, 풀스택 개발, 웹 배포       | 실시간 채팅, 관리자 페이지 구현(대시보드-근태 및 신청 현황 분석, 게시판/부서 관리 등)                                          |
| 이미르 | Git 관리, 풀스택 개발                   | Auth 인증/인가/보안 로직 구현, 회의실/비품 예약, QR출퇴근, QR근태 앱 개발, 화상회의, 업무관리(works), FAQ 챗봇, 관리자 페이지(임직원 관리) |

---

## ⚙️ 주요 기술 스택

### 📌 백엔드
- **Spring Boot (v2.7+)**
- **JWT 인증 / 보안**
- **MyBatis** - Mapper 기반 쿼리
- **MongoDB** - 채팅 로그 저장용 NoSQL
- **Scheduler (근태처리, 자동 알림)**
- **PHP** 활용 주소록-외부 메일서버 연동
- **Rest API** 서버 구축

### 🌐 프론트엔드
- **Thymeleaf** (사내 시스템용)
- **React + Zustand** (관리자 페이지)
- **TailwindCSS / Vanilla JS**

### 📲 모바일
- **Flutter** (QR 출결 앱)
- **Dart**, 카메라/QR API 사용

### ☁️ 인프라
- **AWS EC2 / S3(이미지, 파일 관리)**
- **Hmail Server - RoundCube (외부 이메일 시스템 구축)**

### 🔄 실시간 통신
- **STOMP/WebSocket** - 채팅, 알림 전송
- **WebRTC** - 화상회의

### 💎활용 API
- **Full Calendar API**
- **공공기관 공휴일 API**
- **Summernote API**

---

## 💡 주요 기능

- 🧑‍💼 **JWT 기반 로그인/회원가입**
- 💬 **STOMP 기반 채팅** (로그인 유저만 입력 가능)
- 🎥 **WebRTC 화상회의** (자동 재접속, 참여자 실시간 갱신)
- 📋 **전자결재 시스템** (기안, 결재, 알림)
- 📆 **회의실/비품 예약 기능**
- 📮 **메일 기능 (RoundCube 연동)** (우리 회사 고유 도메인 발급)
- 📋 **기업 공지사항 스크래핑 (Jsoup 기반 자동화)**
- 🏢 **조직도/주소록 기능 (직위/부서 기반 필터링)** (주소록은 메일 서버와 연동)
- 📱 **Flutter 기반 QR 출결 앱** (스캐너 UI, 서버 연동)
- 🤖 **FAQ 챗봇 (정의된 응답 자동화)**

---

## 📁 프로젝트 설치 및 실행

```bash
# React Chatting System
"groupware-chat" repository 에서 확인할 수 있습니다.

# React Admin Frontend
"groupware-admin" repository 에서 확인할 수 있습니다.

cd admin/
npm install
npm install react-router-dom axios zustand lucid-react 
npm install -D tailwindcss postcss autoprefixer

# Flutter App
"groupware-app" repository에서 확인할 수 있습니다.
```

---

## 🧪 테스트

- Spring Boot 통합 테스트
- Postman 기반 REST 테스트
- WebRTC 연결 테스트는 브라우저 콘솔 기반 디버깅

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.

