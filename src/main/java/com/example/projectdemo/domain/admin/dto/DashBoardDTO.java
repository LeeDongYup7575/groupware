package com.example.projectdemo.domain.admin.dto;

import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashBoardDTO {
    private int totalEmployees;
    private int todayMeetingRoomBookings;
    private int todaySuppliesBookings;
    private List<RecentLoginDTO> recentEmployees;
}
