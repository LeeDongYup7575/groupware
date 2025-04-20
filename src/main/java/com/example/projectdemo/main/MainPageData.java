package com.example.projectdemo.main;

import com.example.projectdemo.domain.attend.dto.AttendDTO;
import com.example.projectdemo.domain.board.dto.PostsDTO;
import com.example.projectdemo.domain.booking.dto.MeetingRoomBookingDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.notification_scraping.model.Notice;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MainPageData {
    private EmployeesDTO employee;
    private List<Notice> notices;
    private List<PostsDTO> publicList;
    private List<MeetingRoomBookingDTO> meetingRoomBookings;
    private List<MeetingRoomBookingDTO> myMeetingRoomBookings;
    private int myBookingsCount;
    private LocalDateTime currentDate;
    private List<AttendDTO> attendanceListByDate;
    private int edsmCount;
    private String accessToken;
}
