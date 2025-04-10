# MyBatis + JWT 활용 Auth 구축 

## 개요
JPA 대신 MyBatis를 사용하여 데이터베이스 액세스를 구현했습니다.

## 구현 목록

### 1. Mapper
- **EmployeeMapper**: 직원 정보 조회 및 업데이트를 위한 MyBatis 매퍼 인터페이스

### 2. JWT 관련 클래스
- **JwtTokenUtil**: 토큰 생성, 검증, 파싱 기능 담당
- **JwtAuthenticationFilter**: API 요청에 대한 JWT 토큰 검증 필터

### 3. 인터셉터
- **AuthorizationInterceptor**: 권한 기반 접근 제어 인터셉터
- **TempPasswordInterceptor**: 임시 비밀번호 상태 관리 인터셉터
- **LoginTrackerInterceptor**: 마지막 로그인 시간 업데이트 인터셉터

### 4. 설정 클래스
- **WebSecurityConfig**: JWT 필터 등록 및 보안 설정
- **WebMvcConfig**: 인터셉터 등록 및 경로 설정

### 5. 컨트롤러 및 서비스
- **AuthController**: 로그인, 회원가입, 비밀번호 관리 등의 API 제공
- **EmployeeService**: 직원 관련 비즈니스 로직 처리

## 구현 상세

### JWT 토큰 생성 및 검증
```java
// 토큰 생성
String accessToken = jwtTokenUtil.generateToken(employee);
String refreshToken = jwtTokenUtil.generateRefreshToken(employee);

// 토큰 검증
boolean isValid = jwtTokenUtil.validateToken(token);
```

### 인증 필터 구성
```java
@Bean
public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration() {
    FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(jwtAuthenticationFilter());
    registrationBean.addUrlPatterns("/api/*");
    registrationBean.setOrder(1);
    return registrationBean;
}
```

### 인터셉터 등록
```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    // 로그인 추적 인터셉터 등록
    registry.addInterceptor(loginTrackerInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns("/api/auth/login", "/api/auth/register");
    
    // 나머지 인터셉터 등록...
}
```

## API 엔드포인트

### 1. 인증 관련 API
- `POST /api/auth/login`: 로그인 및 JWT 토큰 발급
- `POST /api/auth/refresh-token`: 리프레시 토큰으로 새 액세스 토큰 발급
- `POST /api/auth/verify-employee`: 직원 정보 확인 (회원가입 전)
- `POST /api/auth/register`: 회원가입
- `POST /api/auth/change-password`: 비밀번호 변경
- `POST /api/auth/reset-password`: 비밀번호 재설정

## 설정 가이드

### 1. 스프링 부트 설정 (application.properties/따로 전달예정)
```properties
# JWT 설정
jwt.secret=yourSecretKey
jwt.expiration=86400000
jwt.qr.expiration=120000

# MyBatis 설정
mybatis.mapper-locations=classpath:mappers/**/*.xml
mybatis.type-aliases-package=yourPakageDomain
mybatis.configuration.map-underscore-to-camel-case=true
```

### 2. 프론트엔드 JWT 토큰 사용
클라이언트 측에서는 다음과 같이 JWT 토큰을 사용합니다:

```javascript
// 로그인 후 토큰 저장
localStorage.setItem('accessToken', data.accessToken);
localStorage.setItem('refreshToken', data.refreshToken);

// API 요청 시 토큰 포함 
fetch('/api/employees/profile', { //요청 예시
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
  }
})
.then(response => {
  if (response.status === 401) {
    // 토큰 만료되었을 때 refresh token으로 갱신 필요
    return refreshToken();
  }
  return response.json();
});
```

## 참고사항
- 그룹웨어 메인 페이지는 아직 토대가 없으며, intro.html은 auth 기능 연결 + 그룹웨어 연결 페이지입니다.
- intro 페이지에는 '한국정보교육원' 사이트의 공지사항을 하나의 회사 대표 공지사항이라고 임의 설정하고 크롤링하여 출력하였습니다.
- 토대로 구현된 기능은 로그인, 회원가입, 비밀번호찾기(임시비밀번호 발급), 크롤링된 공지사항 출력, QR근태관리 입니다.
- spring secutiry대신 인터셉터/필터를 이용하여 보안 관리를 하고 있습니다.
- 개발 시작 후: 다른 기능 구현 시 위 내용에서 참고할 부분은 JWT 인증/인가가 필요한 페이지는 validation을 추가하는 것이고, 필요하지 않은 페이지는 filter에서 해제되도록 allowPath경로에 자신의 페이지를 추가해주셔야 합니다.

```
groupware-demo
├─ README.md
├─ pom.xml
└─ src
   ├─ main
   │  ├─ java
   │  │  ├─ com
   │  │  │  └─ example
   │  │  │     └─ projectdemo
   │  │  │        ├─ ProjectdemoApplication.java
   │  │  │        ├─ config
   │  │  │        │  ├─ AuthorizationInterceptor.java
   │  │  │        │  ├─ CORSConfig.java
   │  │  │        │  ├─ LoginTrackerInterceptor.java
   │  │  │        │  ├─ PasswordEncoder.java
   │  │  │        │  ├─ TempPasswordInterceptor.java
   │  │  │        │  ├─ WebConfig.java
   │  │  │        │  ├─ WebMvcConfig.java
   │  │  │        │  ├─ WebSecurityConfig.java
   │  │  │        │  ├─ WebSocketConfig.java
   │  │  │        │  └─ info.txt
   │  │  │        ├─ domain
   │  │  │        │  ├─ admin
   │  │  │        │  │  └─ controller
   │  │  │        │  │     └─ AdminController.java
   │  │  │        │  ├─ approval
   │  │  │        │  │  └─ info.txt
   │  │  │        │  ├─ attend
   │  │  │        │  │  ├─ controller
   │  │  │        │  │  │  └─ AttendController.java
   │  │  │        │  │  ├─ dao
   │  │  │        │  │  │  └─ AttendDAO.java
   │  │  │        │  │  ├─ dto
   │  │  │        │  │  │  └─ AttendDTO.java
   │  │  │        │  │  └─ service
   │  │  │        │  │     └─ AttendService.java
   │  │  │        │  ├─ attendance
   │  │  │        │  │  ├─ controller
   │  │  │        │  │  │  ├─ QRApiController.java
   │  │  │        │  │  │  └─ QRController.java
   │  │  │        │  │  ├─ dto
   │  │  │        │  │  │  ├─ ApiResponseDTO.java
   │  │  │        │  │  │  ├─ AttendanceDTO.java
   │  │  │        │  │  │  └─ QRScanRequestDTO.java
   │  │  │        │  │  ├─ entity
   │  │  │        │  │  │  └─ Attendance.java
   │  │  │        │  │  ├─ enums
   │  │  │        │  │  │  └─ AttendanceStatus.java
   │  │  │        │  │  ├─ info.txt
   │  │  │        │  │  ├─ mapper
   │  │  │        │  │  │  └─ AttendanceMapper.java
   │  │  │        │  │  ├─ service
   │  │  │        │  │  │  ├─ AttendanceService.java
   │  │  │        │  │  │  └─ AutoCheckoutService.java
   │  │  │        │  │  └─ util
   │  │  │        │  │     └─ QRTokenUtil.java
   │  │  │        │  ├─ auth
   │  │  │        │  │  ├─ controller
   │  │  │        │  │  │  └─ AuthController.java
   │  │  │        │  │  ├─ dto
   │  │  │        │  │  │  ├─ JwtResponseDTO.java
   │  │  │        │  │  │  ├─ LoginDTO.java
   │  │  │        │  │  │  ├─ PasswordChangeDTO.java
   │  │  │        │  │  │  ├─ PasswordResetDTO.java
   │  │  │        │  │  │  ├─ RefreshTokenDTO.java
   │  │  │        │  │  │  └─ SignupDTO.java
   │  │  │        │  │  ├─ jwt
   │  │  │        │  │  │  ├─ JwtAuthenticationFilter.java
   │  │  │        │  │  │  └─ JwtTokenUtil.java
   │  │  │        │  │  └─ service
   │  │  │        │  │     ├─ EmailService.java
   │  │  │        │  │     ├─ LogoutService.java
   │  │  │        │  │     └─ ProfileUploadService.java
   │  │  │        │  ├─ board
   │  │  │        │  │  ├─ controller
   │  │  │        │  │  │  ├─ BoardApiController.java
   │  │  │        │  │  │  ├─ BoardController.java
   │  │  │        │  │  │  └─ PostApiController.java
   │  │  │        │  │  ├─ dto
   │  │  │        │  │  │  ├─ AttachmentsDTO.java
   │  │  │        │  │  │  ├─ BoardPermissionsDTO.java
   │  │  │        │  │  │  ├─ BoardsDTO.java
   │  │  │        │  │  │  ├─ CommentsDTO.java
   │  │  │        │  │  │  ├─ PostStarsDTO.java
   │  │  │        │  │  │  └─ PostsDTO.java
   │  │  │        │  │  ├─ entity
   │  │  │        │  │  │  ├─ Attachments.java
   │  │  │        │  │  │  ├─ BoardPermissions.java
   │  │  │        │  │  │  ├─ Boards.java
   │  │  │        │  │  │  ├─ Comments.java
   │  │  │        │  │  │  ├─ PostStars.java
   │  │  │        │  │  │  └─ Posts.java
   │  │  │        │  │  ├─ info.txt
   │  │  │        │  │  ├─ mapper
   │  │  │        │  │  │  ├─ BoardsMapper.java
   │  │  │        │  │  │  └─ PostsMapper.java
   │  │  │        │  │  └─ service
   │  │  │        │  │     ├─ BoardsService.java
   │  │  │        │  │     └─ PostsService.java
   │  │  │        │  ├─ booking
   │  │  │        │  │  ├─ controller
   │  │  │        │  │  │  ├─ BookingApiController.java
   │  │  │        │  │  │  ├─ BookingController.java
   │  │  │        │  │  │  └─ PlaceholderController.java
   │  │  │        │  │  ├─ dto
   │  │  │        │  │  │  ├─ BookingRequestDTO.java
   │  │  │        │  │  │  ├─ MeetingRoomBookingDTO.java
   │  │  │        │  │  │  ├─ MeetingRoomDTO.java
   │  │  │        │  │  │  ├─ SuppliesBookingDTO.java
   │  │  │        │  │  │  └─ SuppliesDTO.java
   │  │  │        │  │  ├─ entity
   │  │  │        │  │  │  ├─ MeetingRoom.java
   │  │  │        │  │  │  ├─ MeetingRoomBooking.java
   │  │  │        │  │  │  ├─ Supplies.java
   │  │  │        │  │  │  └─ SuppliesBooking.java
   │  │  │        │  │  ├─ info.txt
   │  │  │        │  │  ├─ mapper
   │  │  │        │  │  │  ├─ MeetingRoomMapper.java
   │  │  │        │  │  │  └─ SuppliesMapper.java
   │  │  │        │  │  ├─ service
   │  │  │        │  │  │  ├─ MeetingRoomService.java
   │  │  │        │  │  │  └─ SuppliesService.java
   │  │  │        │  │  └─ util
   │  │  │        │  │     └─ BookingTimeUtils.java
   │  │  │        │  ├─ calendar
   │  │  │        │  │  └─ info.txt
   │  │  │        │  ├─ chat
   │  │  │        │  │  ├─ controller
   │  │  │        │  │  │  ├─ ChatRoomController.java
   │  │  │        │  │  │  ├─ ChatStompController.java
   │  │  │        │  │  │  └─ MembershipController.java
   │  │  │        │  │  ├─ dao
   │  │  │        │  │  │  ├─ ChatRoomDAO.java
   │  │  │        │  │  │  └─ MembershipDAO.java
   │  │  │        │  │  ├─ dto
   │  │  │        │  │  │  ├─ ChatMessageDTO.java
   │  │  │        │  │  │  ├─ ChatRoomDTO.java
   │  │  │        │  │  │  ├─ ChatRoomRequestDTO.java
   │  │  │        │  │  │  ├─ ChatUserDTO.java
   │  │  │        │  │  │  └─ MemberShipDTO.java
   │  │  │        │  │  ├─ info.txt
   │  │  │        │  │  └─ service
   │  │  │        │  │     ├─ ChatMessageService.java
   │  │  │        │  │     ├─ ChatRoomService.java
   │  │  │        │  │     └─ MembershipService.java
   │  │  │        │  ├─ contact
   │  │  │        │  │  ├─ controller
   │  │  │        │  │  │  ├─ ContactApiController.java
   │  │  │        │  │  │  └─ ContactController.java
   │  │  │        │  │  ├─ dto
   │  │  │        │  │  │  ├─ EmployeeContactDTO.java
   │  │  │        │  │  │  └─ personalContactDTO.java
   │  │  │        │  │  ├─ mapper
   │  │  │        │  │  │  └─ ContactMapper.java
   │  │  │        │  │  └─ service
   │  │  │        │  │     └─ ContactService.java
   │  │  │        │  ├─ edsm
   │  │  │        │  │  ├─ controller
   │  │  │        │  │  │  ├─ EdsmController.java
   │  │  │        │  │  │  ├─ EdsmDetailController.java
   │  │  │        │  │  │  └─ EdsmFormController.java
   │  │  │        │  │  ├─ dao
   │  │  │        │  │  │  └─ EdsmDAO.java
   │  │  │        │  │  ├─ dto
   │  │  │        │  │  │  ├─ ApprovalLineDTO.java
   │  │  │        │  │  │  ├─ EdsmBusinessContactDTO.java
   │  │  │        │  │  │  ├─ EdsmCashDisbuVoucherDTO.java
   │  │  │        │  │  │  ├─ EdsmDocumentDTO.java
   │  │  │        │  │  │  └─ EdsmLetterOfApprovalDTO.java
   │  │  │        │  │  ├─ enums
   │  │  │        │  │  │  ├─ ApprovalStatus.java
   │  │  │        │  │  │  └─ EdsmStatus.java
   │  │  │        │  │  ├─ services
   │  │  │        │  │  │  ├─ EdsmDetailService.java
   │  │  │        │  │  │  ├─ EdsmFormService.java
   │  │  │        │  │  │  └─ EdsmService.java
   │  │  │        │  │  └─ statics
   │  │  │        │  │     └─ NaviStatics.java
   │  │  │        │  ├─ employees
   │  │  │        │  │  ├─ controller
   │  │  │        │  │  │  └─ DepartmentsApiController.java
   │  │  │        │  │  ├─ dto
   │  │  │        │  │  │  ├─ DepartmentsDTO.java
   │  │  │        │  │  │  ├─ EmployeesDTO.java
   │  │  │        │  │  │  ├─ EmployeesInfoUpdateDTO.java
   │  │  │        │  │  │  └─ PositionsDTO.java
   │  │  │        │  │  ├─ entity
   │  │  │        │  │  │  ├─ Departments.java
   │  │  │        │  │  │  ├─ Employees.java
   │  │  │        │  │  │  └─ Positions.java
   │  │  │        │  │  ├─ info.txt
   │  │  │        │  │  ├─ mapper
   │  │  │        │  │  │  ├─ DepartmentsMapper.java
   │  │  │        │  │  │  ├─ EmployeesMapper.java
   │  │  │        │  │  │  └─ PositionsMapper.java
   │  │  │        │  │  └─ service
   │  │  │        │  │     ├─ DepartmentsService.java
   │  │  │        │  │     └─ EmployeesService.java
   │  │  │        │  ├─ faq
   │  │  │        │  │  └─ FaqController.java
   │  │  │        │  ├─ leave
   │  │  │        │  │  ├─ controller
   │  │  │        │  │  │  └─ LeaveController.java
   │  │  │        │  │  ├─ dao
   │  │  │        │  │  │  └─ LeavesDAO.java
   │  │  │        │  │  ├─ dto
   │  │  │        │  │  │  └─ LeavesDTO.java
   │  │  │        │  │  ├─ scheduler
   │  │  │        │  │  │  └─ LeaveScheduler.java
   │  │  │        │  │  └─ service
   │  │  │        │  │     └─ LeavesService.java
   │  │  │        │  ├─ mail
   │  │  │        │  │  └─ controller
   │  │  │        │  │     └─ MailController.java
   │  │  │        │  ├─ mypage
   │  │  │        │  │  └─ controller
   │  │  │        │  │     ├─ MypageApiController.java
   │  │  │        │  │     └─ MypageController.java
   │  │  │        │  ├─ notification
   │  │  │        │  │  ├─ config
   │  │  │        │  │  │  └─ CacheConfig.java
   │  │  │        │  │  ├─ crawler
   │  │  │        │  │  │  └─ NoticeCrawler.java
   │  │  │        │  │  ├─ info.txt
   │  │  │        │  │  └─ model
   │  │  │        │  │     └─ Notice.java
   │  │  │        │  ├─ videoconf
   │  │  │        │  │  ├─ controller
   │  │  │        │  │  │  ├─ VideoConfController.java
   │  │  │        │  │  │  ├─ VideoConfRestController.java
   │  │  │        │  │  │  └─ VideoConfWebSocketController.java
   │  │  │        │  │  ├─ dto
   │  │  │        │  │  │  ├─ VideoRoomCreateDTO.java
   │  │  │        │  │  │  ├─ VideoRoomDTO.java
   │  │  │        │  │  │  ├─ VideoRoomJoinDTO.java
   │  │  │        │  │  │  ├─ VideoRoomParticipantDTO.java
   │  │  │        │  │  │  └─ WebRTCMessageDTO.java
   │  │  │        │  │  ├─ entity
   │  │  │        │  │  │  ├─ VideoRoom.java
   │  │  │        │  │  │  └─ VideoRoomParticipant.java
   │  │  │        │  │  ├─ listener
   │  │  │        │  │  │  └─ WebSocketEventListener.java
   │  │  │        │  │  ├─ mapper
   │  │  │        │  │  │  ├─ VideoRoomMapper.java
   │  │  │        │  │  │  └─ VideoRoomParticipantMapper.java
   │  │  │        │  │  └─ service
   │  │  │        │  │     └─ VideoConfService.java
   │  │  │        │  ├─ web
   │  │  │        │  │  └─ controller
   │  │  │        │  │     ├─ WebApiController.java
   │  │  │        │  │     └─ WebController.java
   │  │  │        │  └─ work
   │  │  │        │     ├─ controller
   │  │  │        │     │  └─ WorkController.java
   │  │  │        │     ├─ dao
   │  │  │        │     │  └─ WorkDAO.java
   │  │  │        │     ├─ dto
   │  │  │        │     │  └─ OverTimeDTO.java
   │  │  │        │     └─ service
   │  │  │        │        └─ WorkService.java
   │  │  │        ├─ exception
   │  │  │        │  ├─ GlobalExceptionHandler.java
   │  │  │        │  └─ info.txt
   │  │  │        ├─ mongodb
   │  │  │        │  └─ repository
   │  │  │        │     └─ ChatMessageRepository.java
   │  │  │        └─ util
   │  │  │           ├─ JwtKeyGenerator.java
   │  │  │           ├─ StringUtils.java
   │  │  │           └─ info.txt
   │  │  └─ net
   │  │     └─ crizin
   │  │        ├─ KoreanCharacter.java
   │  │        └─ KoreanRomanizer.java
   │  └─ resources
   │     ├─ mappers
   │     │  ├─ AttendMapper.xml
   │     │  ├─ AttendanceMapper.xml
   │     │  ├─ BoardsMapper.xml
   │     │  ├─ ChatRoomMapper.xml
   │     │  ├─ ContactsMapper.xml
   │     │  ├─ DepartmentsMapper.xml
   │     │  ├─ EdsmMapper.xml
   │     │  ├─ EmployeesMapper.xml
   │     │  ├─ LeavesMapper.xml
   │     │  ├─ MeetingRoomBookingMapper.xml
   │     │  ├─ MembershipMapper.xml
   │     │  ├─ PositionsMapper.xml
   │     │  ├─ PostsMapper.xml
   │     │  ├─ SuppliesBookingMapper.xml
   │     │  ├─ VideoRoomMapper.xml
   │     │  ├─ VideoRoomParticipantMapper.xml
   │     │  └─ WorksMapper.xml
   │     ├─ static
   │     │  └─ assets
   │     │     ├─ css
   │     │     │  ├─ attend
   │     │     │  │  ├─ attendAnnualStatistics.css
   │     │     │  │  ├─ attendLeavesHistory.css
   │     │     │  │  └─ attendMain.css
   │     │     │  ├─ board
   │     │     │  │  └─ board-common.css
   │     │     │  ├─ booking
   │     │     │  │  └─ booking-main.css
   │     │     │  ├─ contact
   │     │     │  │  └─ contact.css
   │     │     │  ├─ edsm
   │     │     │  │  ├─ edsm.css
   │     │     │  │  ├─ edsmBusinessContact.css
   │     │     │  │  ├─ edsmBusinessDetail.css
   │     │     │  │  ├─ edsmInput.css
   │     │     │  │  ├─ edsmInputCdv.css
   │     │     │  │  ├─ edsmLetterOfApproval.css
   │     │     │  │  └─ edsmMain.css
   │     │     │  ├─ fragments
   │     │     │  │  └─ sidebar-common.css
   │     │     │  ├─ intro.css
   │     │     │  ├─ leave
   │     │     │  │  └─ leavesForm.css
   │     │     │  ├─ mail
   │     │     │  │  └─ mail.css
   │     │     │  ├─ main-page.css
   │     │     │  ├─ mypage
   │     │     │  │  └─ mypage.css
   │     │     │  └─ work
   │     │     │     ├─ overTimeForm.css
   │     │     │     ├─ workDetails.css
   │     │     │     └─ workSchedule.css
   │     │     ├─ images
   │     │     │  ├─ bigLogo.png
   │     │     │  ├─ default-profile.png
   │     │     │  ├─ expense-preview.jpg
   │     │     │  ├─ groupware-preview.jpg
   │     │     │  ├─ introbg.jpg
   │     │     │  ├─ meeting-rooms
   │     │     │  │  ├─ room1.jpg
   │     │     │  │  ├─ room2.jpg
   │     │     │  │  ├─ room3.jpg
   │     │     │  │  └─ room4.jpg
   │     │     │  ├─ stuff
   │     │     │  │  ├─ laptop-154091_640.png
   │     │     │  │  └─ mac-4942769_640.png
   │     │     │  └─ work-preview.jpg
   │     │     └─ js
   │     │        ├─ contact
   │     │        │  └─ contact.js
   │     │        ├─ fragments
   │     │        │  └─ sidebar-common.js
   │     │        ├─ mypage
   │     │        │  └─ mypage.js
   │     │        └─ work
   │     │           └─ workSchedule.js
   │     └─ templates
   │        ├─ attend
   │        │  ├─ attendAnnualStatistics.html
   │        │  ├─ attendLeavesHistory.html
   │        │  └─ attendMain.html
   │        ├─ attendance
   │        │  └─ qrcheck.html
   │        ├─ auth
   │        │  ├─ change-password.html
   │        │  ├─ forgot-password.html
   │        │  ├─ login.html
   │        │  ├─ privacy-agreement.html
   │        │  ├─ signup.html
   │        │  └─ temp-password.html
   │        ├─ board
   │        │  ├─ board-layout.html
   │        │  ├─ edit.html
   │        │  ├─ integrated-list.html
   │        │  ├─ list.html
   │        │  ├─ view.html
   │        │  └─ write.html
   │        ├─ booking
   │        │  ├─ booking-main.html
   │        │  ├─ booking-meeting-details.html
   │        │  ├─ booking-meeting-room.html
   │        │  ├─ booking-supplies-details.html
   │        │  └─ booking-supplies.html
   │        ├─ contact
   │        │  └─ contact.html
   │        ├─ edsm
   │        │  ├─ complete.html
   │        │  ├─ edsmDetail
   │        │  │  ├─ businessContactDetail.html
   │        │  │  ├─ cashDisbuVoucherDetail.html
   │        │  │  └─ letterOfApprovalDetail.html
   │        │  ├─ edsmForm
   │        │  │  ├─ businessContact.html
   │        │  │  ├─ cashDisbuVoucher.html
   │        │  │  └─ letterOfApproval.html
   │        │  ├─ error.html
   │        │  ├─ expected.html
   │        │  ├─ input.html
   │        │  ├─ main.html
   │        │  └─ progress.html
   │        ├─ faq
   │        │  └─ faq.html
   │        ├─ fragments
   │        │  ├─ footer.html
   │        │  ├─ header.html
   │        │  └─ sidebar
   │        │     ├─ board-sidebar.html
   │        │     └─ main-sidebar.html
   │        ├─ intro.html
   │        ├─ leave
   │        │  └─ leavesForm.html
   │        ├─ mail
   │        │  └─ mail.html
   │        ├─ main.html
   │        ├─ mypage
   │        │  └─ mypage.html
   │        ├─ videochat
   │        │  └─ videochat.html
   │        └─ work
   │           ├─ overTimeForm.html
   │           ├─ workDetails.html
   │           └─ workSchedule.html
   └─ test
      └─ java
         └─ com
            └─ example
               └─ projectdemo
                  └─ ProjectdemoApplicationTests.java

```


