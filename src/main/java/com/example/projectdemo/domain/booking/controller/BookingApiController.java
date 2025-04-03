package com.example.projectdemo.domain.booking.controller;

import com.example.projectdemo.domain.booking.dto.BookingRequestDTO;
import com.example.projectdemo.domain.booking.dto.MeetingRoomBookingDTO;
import com.example.projectdemo.domain.booking.dto.MeetingRoomDTO;
import com.example.projectdemo.domain.booking.dto.SuppliesBookingDTO;
import com.example.projectdemo.domain.booking.dto.SuppliesDTO;
import com.example.projectdemo.domain.booking.service.MeetingRoomService;
import com.example.projectdemo.domain.booking.service.SuppliesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booking")
public class BookingApiController {

    @Autowired
    private MeetingRoomService meetingRoomService;

    @Autowired
    private SuppliesService suppliesService;

    // === 회의실 관련 API ===

    // 모든 회의실 목록 조회
    @GetMapping("/meeting-rooms")
    public ResponseEntity<List<MeetingRoomDTO>> getAllMeetingRooms() {
        return ResponseEntity.ok(meetingRoomService.getAllMeetingRooms());
    }

    // 특정 회의실 정보 조회
    @GetMapping("/meeting-rooms/{id}")
    public ResponseEntity<MeetingRoomDTO> getMeetingRoomById(@PathVariable Integer id) {
        return ResponseEntity.ok(meetingRoomService.getMeetingRoomById(id));
    }

    // 회의실 예약 가능 여부 확인
    @GetMapping("/meeting-rooms/{roomId}/available")
    public ResponseEntity<Map<String, Boolean>> checkMeetingRoomAvailability(
            @PathVariable Integer roomId,
            @RequestParam String startDate,
            @RequestParam String startTime,
            @RequestParam String endDate,
            @RequestParam String endTime) {

        LocalDateTime startDateTime = parseDateTime(startDate, startTime);
        LocalDateTime endDateTime = parseDateTime(endDate, endTime);

        boolean isAvailable = meetingRoomService.isRoomAvailable(roomId, startDateTime, endDateTime);

        Map<String, Boolean> response = new HashMap<>();
        response.put("available", isAvailable);

        return ResponseEntity.ok(response);
    }

    // 회의실 예약 목록 조회
    @GetMapping("/meeting-room-bookings")
    public ResponseEntity<List<MeetingRoomBookingDTO>> getMeetingRoomBookings(
            @RequestParam(required = false) String empNum,
            @RequestParam(required = false) Integer roomId,
            @RequestParam(required = false) String date) {

        if (empNum != null) {
            return ResponseEntity.ok(meetingRoomService.getBookingsByEmpNum(empNum));
        } else if (roomId != null) {
            return ResponseEntity.ok(meetingRoomService.getBookingsByRoomId(roomId));
        } else if (date != null) {
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDateTime startOfDay = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(localDate, LocalTime.MAX);
            return ResponseEntity.ok(meetingRoomService.getBookingsByDateRange(startOfDay, endOfDay));
        } else {
            return ResponseEntity.ok(meetingRoomService.getAllBookings());
        }
    }

    // 회의실 예약 등록
    @PostMapping("/meeting-room-bookings")
    public ResponseEntity<MeetingRoomBookingDTO> createMeetingRoomBooking(
            @RequestBody BookingRequestDTO requestDTO,
            HttpServletRequest request) {

        // 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // 날짜와 시간 문자열을 LocalDateTime으로 변환
            LocalDateTime startDateTime = parseDateTime(requestDTO.getStartDate(), requestDTO.getStartTime());
            LocalDateTime endDateTime = parseDateTime(requestDTO.getEndDate(), requestDTO.getEndTime());

            // 시간 유효성 검증
            validateTimeRange(startDateTime, endDateTime);

            // 예약 가능 여부 재확인
            boolean isAvailable = meetingRoomService.isRoomAvailable(requestDTO.getRoomId(), startDateTime, endDateTime);
            if (!isAvailable) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // 예약 생성
            MeetingRoomBookingDTO booking = meetingRoomService.createBooking(empNum, requestDTO);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            // 유효성 검증 실패
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 회의실 예약 수정
    @PutMapping("/meeting-room-bookings/{id}")
    public ResponseEntity<MeetingRoomBookingDTO> updateMeetingRoomBooking(
            @PathVariable Integer id,
            @RequestBody BookingRequestDTO requestDTO) {

        try {
            MeetingRoomBookingDTO booking = meetingRoomService.updateBooking(id, requestDTO);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 회의실 예약 취소
    @DeleteMapping("/meeting-room-bookings/{id}")
    public ResponseEntity<Map<String, Boolean>> cancelMeetingRoomBooking(@PathVariable Integer id) {
        boolean result = meetingRoomService.cancelBooking(id);

        Map<String, Boolean> response = new HashMap<>();
        response.put("success", result);

        return ResponseEntity.ok(response);
    }

    // === 비품 관련 API ===

    // 모든 비품 목록 조회
    @GetMapping("/supplies")
    public ResponseEntity<List<SuppliesDTO>> getAllSupplies() {
        return ResponseEntity.ok(suppliesService.getAllSupplies());
    }

    // 특정 비품 정보 조회
    @GetMapping("/supplies/{id}")
    public ResponseEntity<SuppliesDTO> getSuppliesById(@PathVariable Integer id) {
        return ResponseEntity.ok(suppliesService.getSuppliesById(id));
    }

    // 비품 예약 가능 여부 확인
    @GetMapping("/supplies/{supplyId}/available")
    public ResponseEntity<Map<String, Boolean>> checkSuppliesAvailability(
            @PathVariable Integer supplyId,
            @RequestParam Integer quantity,
            @RequestParam String startDate,
            @RequestParam String startTime,
            @RequestParam String endDate,
            @RequestParam String endTime) {

        LocalDateTime startDateTime = parseDateTime(startDate, startTime);
        LocalDateTime endDateTime = parseDateTime(endDate, endTime);

        // 디버깅을 위한 로그 출력
        SuppliesDTO supply = suppliesService.getSuppliesById(supplyId);
        System.out.println("Checking availability for supply ID: " + supplyId);
        System.out.println("Supply name: " + supply.getName());
        System.out.println("Available quantity: " + supply.getAvailableQuantity());
        System.out.println("Requested quantity: " + quantity);
        System.out.println("Start time: " + startDateTime);
        System.out.println("End time: " + endDateTime);

        boolean isAvailable = suppliesService.isSupplyAvailable(supplyId, quantity, startDateTime, endDateTime);
        System.out.println("Is available: " + isAvailable);

        Map<String, Boolean> response = new HashMap<>();
        response.put("available", isAvailable);

        return ResponseEntity.ok(response);
    }

    // 비품 예약 목록 조회
    @GetMapping("/supplies-bookings")
    public ResponseEntity<List<SuppliesBookingDTO>> getSuppliesBookings(
            @RequestParam(required = false) String empNum,
            @RequestParam(required = false) Integer supplyId,
            @RequestParam(required = false) String date) {

        if (empNum != null) {
            return ResponseEntity.ok(suppliesService.getBookingsByEmpNum(empNum));
        } else if (supplyId != null) {
            return ResponseEntity.ok(suppliesService.getBookingsBySupplyId(supplyId));
        } else if (date != null) {
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDateTime startOfDay = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(localDate, LocalTime.MAX);
            return ResponseEntity.ok(suppliesService.getBookingsByDateRange(startOfDay, endOfDay));
        } else {
            return ResponseEntity.ok(suppliesService.getAllBookings());
        }
    }

    // 비품 예약 등록
    @PostMapping("/supplies-bookings")
    public ResponseEntity<SuppliesBookingDTO> createSuppliesBooking(
            @RequestBody BookingRequestDTO requestDTO,
            HttpServletRequest request) {

        // 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // 날짜와 시간 문자열을 LocalDateTime으로 변환
            LocalDateTime startDateTime = parseDateTime(requestDTO.getStartDate(), requestDTO.getStartTime());
            LocalDateTime endDateTime = parseDateTime(requestDTO.getEndDate(), requestDTO.getEndTime());

            // 시간 유효성 검증
            validateTimeRange(startDateTime, endDateTime);

            // 요청에 여러 비품 항목이 포함된 경우 처리
            if (requestDTO.getSupplies() != null && !requestDTO.getSupplies().isEmpty()) {
                // 모든 비품에 대해 예약 가능 여부 확인
                boolean allAvailable = true;
                for (BookingRequestDTO.SupplyItem item : requestDTO.getSupplies()) {
                    boolean available = suppliesService.isSupplyAvailable(
                            item.getId(), item.getQuantity(), startDateTime, endDateTime);
                    if (!available) {
                        allAvailable = false;
                        break;
                    }
                }

                if (!allAvailable) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(null); // 충돌 상태 반환
                }

                // 모든 비품이 가용 가능한 경우, 첫 번째 비품으로 예약 생성
                // 실제로는 여러 비품을 한 번에 예약하는 로직을 구현해야 함
                BookingRequestDTO.SupplyItem firstItem = requestDTO.getSupplies().get(0);
                requestDTO.setSupplyId(firstItem.getId());
                requestDTO.setQuantity(firstItem.getQuantity());
            } else {
                // 기존 방식대로 단일 비품 예약 가능 여부 확인
                boolean isAvailable = suppliesService.isSupplyAvailable(
                        requestDTO.getSupplyId(), requestDTO.getQuantity(), startDateTime, endDateTime);

                if (!isAvailable) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(null); // 충돌 상태 반환
                }
            }

            // 예약 생성
            SuppliesBookingDTO booking = suppliesService.createBooking(empNum, requestDTO);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            // 유효성 검증 실패
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 비품 예약 수정
    @PutMapping("/supplies-bookings/{id}")
    public ResponseEntity<SuppliesBookingDTO> updateSuppliesBooking(
            @PathVariable Integer id,
            @RequestBody BookingRequestDTO requestDTO) {

        try {
            // 날짜와 시간 문자열을 LocalDateTime으로 변환
            LocalDateTime startDateTime = parseDateTime(requestDTO.getStartDate(), requestDTO.getStartTime());
            LocalDateTime endDateTime = parseDateTime(requestDTO.getEndDate(), requestDTO.getEndTime());

            // 시간 유효성 검증
            validateTimeRange(startDateTime, endDateTime);

            SuppliesBookingDTO booking = suppliesService.updateBooking(id, requestDTO);
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            // 유효성 검증 실패
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 비품 예약 취소
    @DeleteMapping("/supplies-bookings/{id}")
    public ResponseEntity<Map<String, Boolean>> cancelSuppliesBooking(@PathVariable Integer id) {
        boolean result = suppliesService.cancelBooking(id);

        Map<String, Boolean> response = new HashMap<>();
        response.put("success", result);

        return ResponseEntity.ok(response);
    }

    // 시간 범위 유효성 검증
    private void validateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // 현재 시간
        LocalDateTime now = LocalDateTime.now();

        // 과거 시간 검증
        if (startDateTime.isBefore(now)) {
            throw new IllegalArgumentException("과거 시간으로 예약할 수 없습니다.");
        }

        // 시작/종료 시간 순서 검증
        if (startDateTime.isEqual(endDateTime) || startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 늦어야 합니다.");
        }

        // 최소 예약 시간 검증 (예: 30분)
        long durationMinutes = java.time.Duration.between(startDateTime, endDateTime).toMinutes();
        if (durationMinutes < 30) {
            throw new IllegalArgumentException("최소 예약 시간은 30분입니다.");
        }

        // 업무 시간 확인 (예: 9:00 ~ 18:00)
        LocalTime startTime = startDateTime.toLocalTime();
        LocalTime endTime = endDateTime.toLocalTime();
        LocalTime workStart = LocalTime.of(9, 0);
        LocalTime workEnd = LocalTime.of(18, 0);

        if (startTime.isBefore(workStart) || endTime.isAfter(workEnd)) {
            throw new IllegalArgumentException("예약은 업무 시간 내에만 가능합니다 (9:00 ~ 18:00).");
        }

        // 10분 단위 시간 검증
        if (startTime.getMinute() % 10 != 0 || endTime.getMinute() % 10 != 0) {
            throw new IllegalArgumentException("시간은 10분 단위로만 선택 가능합니다.");
        }
    }

    // 날짜 및 시간 문자열을 LocalDateTime으로 변환하는 유틸리티 메서드
    private LocalDateTime parseDateTime(String dateString, String timeString) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate date = LocalDate.parse(dateString, dateFormatter);
        LocalTime time = LocalTime.parse(timeString, timeFormatter);

        return LocalDateTime.of(date, time);
    }
}