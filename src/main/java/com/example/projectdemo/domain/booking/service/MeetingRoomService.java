package com.example.projectdemo.domain.booking.service;

import org.springframework.stereotype.Service;
import com.example.projectdemo.domain.booking.dto.MeetingRoomDTO;
import com.example.projectdemo.domain.booking.dto.MeetingRoomBookingDTO;
import com.example.projectdemo.domain.booking.dto.BookingRequestDTO;
import com.example.projectdemo.domain.booking.mapper.MeetingRoomMapper;
import com.example.projectdemo.domain.booking.entity.MeetingRoom;
import com.example.projectdemo.domain.booking.entity.MeetingRoomBooking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeetingRoomService {

    @Autowired
    private MeetingRoomMapper meetingRoomMapper;

    // 모든 회의실 조회
    public List<MeetingRoomDTO> getAllMeetingRooms() {
        return meetingRoomMapper.findAllMeetingRooms().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 특정 회의실 조회
    public MeetingRoomDTO getMeetingRoomById(Integer id) {
        MeetingRoom meetingRoom = meetingRoomMapper.findMeetingRoomById(id);
        return convertToDto(meetingRoom);
    }

    // 모든 회의실 예약 조회
    public List<MeetingRoomBookingDTO> getAllBookings() {
        return meetingRoomMapper.findAllMeetingRoomBookings().stream()
                .map(this::convertToBookingDto)
                .collect(Collectors.toList());
    }

    // 특정 사원의 회의실 예약 조회
    public List<MeetingRoomBookingDTO> getBookingsByEmpNum(String empNum) {
        LocalDateTime now = LocalDateTime.now();
        return meetingRoomMapper.findMeetingRoomBookingsByEmpNum(empNum).stream()
                .map(this::convertToBookingDto)
                .filter(dto -> dto.getStart() != null && dto.getStart().isAfter(now))
                .collect(Collectors.toList());
    }

    // 특정 회의실의 예약 조회
    public List<MeetingRoomBookingDTO> getBookingsByRoomId(Integer roomId) {
        return meetingRoomMapper.findMeetingRoomBookingsByRoomId(roomId).stream()
                .map(this::convertToBookingDto)
                .collect(Collectors.toList());
    }

    // 특정 날짜 범위의 회의실 예약 조회
    public List<MeetingRoomBookingDTO> getBookingsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return meetingRoomMapper.findMeetingRoomBookingsByDateRange(startDate, endDate).stream()
                .map(this::convertToBookingDto)
                .collect(Collectors.toList());
    }

    // 회의실 예약 가능 여부 확인
    public boolean isRoomAvailable(Integer roomId, LocalDateTime startTime, LocalDateTime endTime) {
        return meetingRoomMapper.isRoomAvailable(roomId, startTime, endTime, null);
    }

    // 회의실 예약 등록
    @Transactional
    public MeetingRoomBookingDTO createBooking(String empNum, BookingRequestDTO requestDTO) {
        // 문자열 날짜/시간을 LocalDateTime으로 변환
        LocalDateTime startDateTime = parseDateTime(requestDTO.getStartDate(), requestDTO.getStartTime());
        LocalDateTime endDateTime = parseDateTime(requestDTO.getEndDate(), requestDTO.getEndTime());

        // 회의실 예약 가능 여부 확인
        if (!isRoomAvailable(requestDTO.getRoomId(), startDateTime, endDateTime)) {
            throw new RuntimeException("해당 시간에 회의실을 예약할 수 없습니다.");
        }

        // 예약 객체 생성
        MeetingRoomBooking booking = new MeetingRoomBooking();
        booking.setRoomId(requestDTO.getRoomId());
        booking.setEmpNum(empNum);
        booking.setTitle(requestDTO.getTitle());
        booking.setStart(startDateTime);
        booking.setEnd(endDateTime);
        booking.setAttendees(requestDTO.getAttendees());
        booking.setPurpose(requestDTO.getPurpose());
        booking.setBookingStatus("CONFIRMED");

        // 예약 등록
        meetingRoomMapper.insertMeetingRoomBooking(booking);

        // 등록된 예약 반환
        return convertToBookingDto(booking);
    }

    // 회의실 예약 수정
    @Transactional
    public MeetingRoomBookingDTO updateBooking(Integer id, BookingRequestDTO requestDTO) {
        // 기존 예약 조회
        MeetingRoomBooking existingBooking = meetingRoomMapper.findAllMeetingRoomBookings().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("예약 정보를 찾을 수 없습니다."));

        // 문자열 날짜/시간을 LocalDateTime으로 변환
        LocalDateTime startDateTime = parseDateTime(requestDTO.getStartDate(), requestDTO.getStartTime());
        LocalDateTime endDateTime = parseDateTime(requestDTO.getEndDate(), requestDTO.getEndTime());

        // 룸 ID 또는 시간이 변경된 경우, 예약 가능 여부 확인
        if (!existingBooking.getRoomId().equals(requestDTO.getRoomId()) ||
                !existingBooking.getStart().equals(startDateTime) ||
                !existingBooking.getEnd().equals(endDateTime)) {

            if (!meetingRoomMapper.isRoomAvailable(requestDTO.getRoomId(), startDateTime, endDateTime, id)) {
                throw new RuntimeException("해당 시간에 회의실을 예약할 수 없습니다.");
            }
        }

        // 예약 정보 업데이트
        existingBooking.setRoomId(requestDTO.getRoomId());
        existingBooking.setTitle(requestDTO.getTitle());
        existingBooking.setStart(startDateTime);
        existingBooking.setEnd(endDateTime);
        existingBooking.setAttendees(requestDTO.getAttendees());
        existingBooking.setPurpose(requestDTO.getPurpose());

        // 예약 수정
        meetingRoomMapper.updateMeetingRoomBooking(existingBooking);

        // 수정된 예약 반환
        return convertToBookingDto(existingBooking);
    }

    // 회의실 예약 취소
    @Transactional
    public boolean cancelBooking(Integer id) {
        return meetingRoomMapper.cancelMeetingRoomBooking(id) > 0;
    }

    // 날짜 및 시간 문자열을 LocalDateTime으로 변환하는 유틸리티 메서드
    private LocalDateTime parseDateTime(String dateString, String timeString) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate date = LocalDate.parse(dateString, dateFormatter);
        LocalTime time = LocalTime.parse(timeString, timeFormatter);

        return LocalDateTime.of(date, time);
    }

    // 엔티티를 DTO로 변환하는 유틸리티 메서드
    private MeetingRoomDTO convertToDto(MeetingRoom meetingRoom) {
        if (meetingRoom == null) return null;

        return new MeetingRoomDTO(
                meetingRoom.getId(),
                meetingRoom.getName(),
                meetingRoom.getCapacity(),
                meetingRoom.getLocation(),
                meetingRoom.getDescription(),
                meetingRoom.getIsAvailable()
        );
    }

    // 예약 엔티티를 DTO로 변환하는 유틸리티 메서드
    private MeetingRoomBookingDTO convertToBookingDto(MeetingRoomBooking booking) {
        if (booking == null) return null;

        MeetingRoomBookingDTO dto = new MeetingRoomBookingDTO();
        dto.setId(booking.getId());
        dto.setRoomId(booking.getRoomId());
        dto.setEmpNum(booking.getEmpNum());
        dto.setTitle(booking.getTitle());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setAttendees(booking.getAttendees());
        dto.setPurpose(booking.getPurpose());
        dto.setBookingStatus(booking.getBookingStatus());
        dto.setCreatedAt(booking.getCreatedAt());

        // 회의실 정보가 있는 경우 설정
        if (booking.getMeetingRoom() != null) {
            dto.setRoomName(booking.getMeetingRoom().getName());
            dto.setRoomLocation(booking.getMeetingRoom().getLocation());
        }

        // 직원 정보가 있는 경우 설정
        dto.setEmpName(booking.getEmpName());
        dto.setDeptName(booking.getDeptName());

        return dto;
    }

    /**
     * 특정 시간 범위 내에 시작하는 회의실 예약 목록 조회
     * 알림을 위해 사용됨
     *
     * @param startTimeFrom 시작 시간 범위 (이 시간 이후)
     * @param startTimeTo 시작 시간 범위 (이 시간 이전)
     * @return 해당 시간 범위 내에 시작하는 회의실 예약 목록
     */
    public List<MeetingRoomBooking> getBookingsStartingBetween(LocalDateTime startTimeFrom, LocalDateTime startTimeTo) {
        return meetingRoomMapper.findBookingsStartingBetween(startTimeFrom, startTimeTo);
    }
}