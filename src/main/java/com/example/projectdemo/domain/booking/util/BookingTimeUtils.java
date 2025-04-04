package com.example.projectdemo.domain.booking.util;

import com.example.projectdemo.domain.booking.dto.MeetingRoomBookingDTO;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class BookingTimeUtils {

    /**
     * 예약 시간대에 따른 너비 계산
     */
    public static int calculateWidth(MeetingRoomBookingDTO booking) {
        // 시작 시간과 종료 시간 사이의 시간 차이 계산 (5시간 표시 중 차지하는 비율)
        long duration = ChronoUnit.MINUTES.between(booking.getStart(), booking.getEnd());
        return (int) (duration * 100 / 300); // 5시간(300분) 기준으로 퍼센트 계산
    }

    /**
     * 예약 시작 시간에 따른 위치 계산
     */
    public static int calculatePosition(MeetingRoomBookingDTO booking) {
        // 09:00부터 시작하는 위치 계산
        LocalTime start = LocalTime.of(9, 0);
        LocalTime bookingStart = booking.getStart().toLocalTime();

        long minutesFromStart = ChronoUnit.MINUTES.between(start, bookingStart);
        return (int) (minutesFromStart * 100 / 300); // 5시간(300분) 기준으로 퍼센트 계산
    }

    /**
     * 예약에 따른 색상 결정
     */
    public static String getBookingColor(MeetingRoomBookingDTO booking) {
        // 회의실 ID 기준으로 색상 결정
        switch (booking.getRoomId() % 4) {
            case 0: return "#90caf9"; // 파란색
            case 1: return "#a5d6a7"; // 녹색
            case 2: return "#fff59d"; // 노란색
            case 3: return "#ffcc80"; // 주황색
            default: return "#e0e0e0"; // 기본 회색
        }
    }

    /**
     * 예약 시간 표시 형식 변환
     */
    public static String formatBookingTime(MeetingRoomBookingDTO booking) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTime = booking.getStart().toLocalTime();
        LocalTime endTime = booking.getEnd().toLocalTime();

        String startTimeStr = startTime.format(timeFormatter);
        String endTimeStr = endTime.format(timeFormatter);

        if (startTime.getHour() < 12) {
            return "오전 " + startTimeStr + " - " + endTimeStr;
        } else {
            return "오후 " + startTimeStr + " - " + endTimeStr;
        }
    }
}