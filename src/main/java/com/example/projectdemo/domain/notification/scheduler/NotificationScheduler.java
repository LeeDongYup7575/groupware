package com.example.projectdemo.domain.notification.service;

import com.example.projectdemo.domain.booking.entity.MeetingRoomBooking;
import com.example.projectdemo.domain.booking.entity.SuppliesBooking;
import com.example.projectdemo.domain.booking.service.MeetingRoomService;
import com.example.projectdemo.domain.booking.service.SuppliesService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationEventHandler notificationEventHandler;
    private final MeetingRoomService meetingRoomService;
    private final SuppliesService suppliesService;

    /**
     * 회의실 예약 시작 1시간 전 알림
     * 매 10분마다 실행
     */
    @Scheduled(cron = "0 */10 * * * *")
    public void sendMeetingRoomBookingNotifications() {
        // 현재 시간으로부터 50분~70분 사이에 시작하는 회의실 예약 조회
        LocalDateTime startTimeFrom = LocalDateTime.now().plusMinutes(50).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime startTimeTo = LocalDateTime.now().plusMinutes(70).truncatedTo(ChronoUnit.MINUTES);

        List<MeetingRoomBooking> upcomingBookings = meetingRoomService.getBookingsStartingBetween(startTimeFrom, startTimeTo);

        for (MeetingRoomBooking booking : upcomingBookings) {
            notificationEventHandler.handleBookingStartingSoonNotification(booking);
        }
    }

    /**
     * 비품 예약 시작 1시간 전 알림
     * 매 10분마다 실행
     */
    @Scheduled(cron = "0 */10 * * * *")
    public void sendSuppliesBookingNotifications() {
        // 현재 시간으로부터 50분~70분 사이에 시작하는 비품 예약 조회
        LocalDateTime startTimeFrom = LocalDateTime.now().plusMinutes(50).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime startTimeTo = LocalDateTime.now().plusMinutes(70).truncatedTo(ChronoUnit.MINUTES);

        List<SuppliesBooking> upcomingBookings = suppliesService.getBookingsStartingBetween(startTimeFrom, startTimeTo);

        for (SuppliesBooking booking : upcomingBookings) {
            notificationEventHandler.handleSuppliesBookingStartingSoonNotification(booking);
        }
    }
}