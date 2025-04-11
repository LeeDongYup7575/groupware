
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
   │  │  │        │  ├─ HmailserverPasswordEncoder.java
   │  │  │        │  ├─ LoginTrackerInterceptor.java
   │  │  │        │  ├─ PasswordEncoder.java
   │  │  │        │  ├─ SidebarInterceptor.java
   │  │  │        │  ├─ TempPasswordInterceptor.java
   │  │  │        │  ├─ WebConfig.java
   │  │  │        │  ├─ WebMvcConfig.java
   │  │  │        │  ├─ WebSecurityConfig.java
   │  │  │        │  ├─ WebSocketConfig.java
   │  │  │        │  └─ info.txt
   │  │  │        ├─ domain
   │  │  │        │  ├─ admin
   │  │  │        │  │  └─ controller
   │  │  │        │  │     ├─ AdminApiController.java
   │  │  │        │  │     ├─ AdminBookingApiController.java
   │  │  │        │  │     ├─ AdminController.java
   │  │  │        │  │     └─ AdminEmpApiController.java
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
   │  │  │        │  │  ├─ scheduler
   │  │  │        │  │  │  ├─ AttendanceResetScheduler.java
   │  │  │        │  │  │  └─ AutoCheckoutScheduler.java
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
   │  │  │        │  │  │  ├─ MembershipController.java
   │  │  │        │  │  │  └─ UnreadMessageController.java
   │  │  │        │  │  ├─ dao
   │  │  │        │  │  │  ├─ ChatRoomDAO.java
   │  │  │        │  │  │  └─ MembershipDAO.java
   │  │  │        │  │  ├─ dto
   │  │  │        │  │  │  ├─ ChatMessageDTO.java
   │  │  │        │  │  │  ├─ ChatRoomDTO.java
   │  │  │        │  │  │  ├─ ChatRoomRequestDTO.java
   │  │  │        │  │  │  ├─ ChatUserDTO.java
   │  │  │        │  │  │  ├─ MemberShipDTO.java
   │  │  │        │  │  │  ├─ UnreadMessage.java
   │  │  │        │  │  │  └─ UnreadNotificationDTO.java
   │  │  │        │  │  ├─ info.txt
   │  │  │        │  │  └─ service
   │  │  │        │  │     ├─ ChatMessageService.java
   │  │  │        │  │     ├─ ChatRoomService.java
   │  │  │        │  │     ├─ MembershipService.java
   │  │  │        │  │     └─ UnreadMessageService.java
   │  │  │        │  ├─ contact
   │  │  │        │  │  ├─ controller
   │  │  │        │  │  │  ├─ ContactApiController.java
   │  │  │        │  │  │  └─ ContactController.java
   │  │  │        │  │  ├─ dto
   │  │  │        │  │  │  ├─ EmployeeContactDTO.java
   │  │  │        │  │  │  └─ PersonalContactDTO.java
   │  │  │        │  │  ├─ mapper
   │  │  │        │  │  │  └─ ContactMapper.java
   │  │  │        │  │  └─ service
   │  │  │        │  │     └─ ContactService.java
   │  │  │        │  ├─ edsm
   │  │  │        │  │  ├─ controller
   │  │  │        │  │  │  ├─ EdsmController.java
   │  │  │        │  │  │  ├─ EdsmDetailController.java
   │  │  │        │  │  │  ├─ EdsmFilesController.java
   │  │  │        │  │  │  └─ EdsmFormController.java
   │  │  │        │  │  ├─ dao
   │  │  │        │  │  │  ├─ EdsmDAO.java
   │  │  │        │  │  │  └─ EdsmFilesDAO.java
   │  │  │        │  │  ├─ dto
   │  │  │        │  │  │  ├─ ApprovalLineDTO.java
   │  │  │        │  │  │  ├─ EdsmBusinessContactDTO.java
   │  │  │        │  │  │  ├─ EdsmCashDisbuVoucherDTO.java
   │  │  │        │  │  │  ├─ EdsmDocumentDTO.java
   │  │  │        │  │  │  ├─ EdsmFilesDTO.java
   │  │  │        │  │  │  └─ EdsmLetterOfApprovalDTO.java
   │  │  │        │  │  ├─ enums
   │  │  │        │  │  │  ├─ ApprovalStatus.java
   │  │  │        │  │  │  └─ EdsmStatus.java
   │  │  │        │  │  ├─ services
   │  │  │        │  │  │  ├─ EdsmDetailService.java
   │  │  │        │  │  │  ├─ EdsmFilesService.java
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
   │  │  │        │  │     ├─ EmployeesService.java
   │  │  │        │  │     ├─ PositionsService.java
   │  │  │        │  │     └─ TmpEmployeesService.java
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
   │  │  │        │  │  ├─ controller
   │  │  │        │  │  │  └─ MailController.java
   │  │  │        │  │  ├─ dto
   │  │  │        │  │  │  └─ MailAccountDTO.java
   │  │  │        │  │  ├─ mapper
   │  │  │        │  │  │  └─ MailMapper.java
   │  │  │        │  │  └─ service
   │  │  │        │  │     └─ MailService.java
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
   │  │  │        │  ├─ projects
   │  │  │        │  │  └─ entity
   │  │  │        │  │     ├─ ProjectMembers.java
   │  │  │        │  │     ├─ Projects.java
   │  │  │        │  │     ├─ Schedules.java
   │  │  │        │  │     ├─ SubTasks.java
   │  │  │        │  │     ├─ TaskLogs.java
   │  │  │        │  │     ├─ Tasks.java
   │  │  │        │  │     └─ TodoList.java
   │  │  │        │  ├─ videoconf
   │  │  │        │  │  ├─ controller
   │  │  │        │  │  │  ├─ VideoConfApiController.java
   │  │  │        │  │  │  ├─ VideoConfController.java
   │  │  │        │  │  │  ├─ VideoConfHeartbeatController.java
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
   │  │  │        │  │  ├─ scheduler
   │  │  │        │  │  │  └─ VideoRoomCleanScheduler.java
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
   │  │  │        │     ├─ ChatMessageRepository.java
   │  │  │        │     └─ UnreadMessageRepository.java
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
   │     │  ├─ MailMapper.xml
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
   │     │     │  │  ├─ edsmCashDisbuVoucherDetail.css
   │     │     │  │  ├─ edsmInput.css
   │     │     │  │  ├─ edsmInputCdv.css
   │     │     │  │  ├─ edsmLeavesDetail.css
   │     │     │  │  ├─ edsmLetterOfApproval.css
   │     │     │  │  ├─ edsmLetterOfApprovalDetail.css
   │     │     │  │  ├─ edsmMain.css
   │     │     │  │  └─ edsmOvertimeDetail.css
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
   │        │  ├─ edsmDetail
   │        │  │  ├─ businessContactDetail.html
   │        │  │  ├─ cashDisbuVoucherDetail.html
   │        │  │  ├─ leavesDetail.html
   │        │  │  ├─ letterOfApprovalDetail.html
   │        │  │  └─ overtimeDetail.html
   │        │  ├─ edsmForm
   │        │  │  ├─ businessContact.html
   │        │  │  ├─ cashDisbuVoucher.html
   │        │  │  └─ letterOfApproval.html
   │        │  ├─ error.html
   │        │  ├─ expected.html
   │        │  ├─ input.html
   │        │  ├─ main.html
   │        │  └─ wait.html
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