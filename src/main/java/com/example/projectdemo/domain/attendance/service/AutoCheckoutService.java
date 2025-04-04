package com.example.projectdemo.domain.attendance.service;

import com.example.projectdemo.domain.attendance.entity.Attendance;
import com.example.projectdemo.domain.attendance.enums.AttendanceStatus;
import com.example.projectdemo.domain.attendance.mapper.AttendanceMapper;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoCheckoutService {

    private final AttendanceMapper attendanceMapper;
    private final EmployeesMapper employeesMapper;

    private static final LocalTime STANDARD_END_TIME = LocalTime.of(18, 0); // 6:00 PM

    /**
     * 매일 오후 6시 5분에 실행되는 스케줄러
     * 출근했지만 퇴근 기록이 없는 직원들에게 자동으로 퇴근 처리
     */
    @Scheduled(cron = "0 5 18 * * ?") // 매일 오후 6시 5분에 실행
    @Transactional
    public void processAutoCheckout() {
        log.info("자동 퇴근 처리 작업 시작");

        // 오늘 날짜
        LocalDate today = LocalDate.now();

        try {
            // 오늘 출근했지만 퇴근 기록이 없는 직원들의 출근 기록을 조회
            List<Attendance> attendancesToUpdate = attendanceMapper.findCheckedInWithoutCheckout(today);

            log.info("자동 퇴근 처리 대상 직원 수: {}", attendancesToUpdate.size());

            for (Attendance attendance : attendancesToUpdate) {
                // 기본 퇴근 시간을 오후 6시로 설정
                LocalTime checkoutTime = STANDARD_END_TIME;

                // 근무 시간 계산 (분 단위로 계산 후 시간으로 변환)
                BigDecimal workHours = BigDecimal.ZERO;
                if (attendance.getCheckIn() != null) {
                    long minutes = attendance.getCheckIn().until(checkoutTime, ChronoUnit.MINUTES);
                    double hours = minutes / 60.0;
                    workHours = BigDecimal.valueOf(hours);
                }

                // 퇴근 상태 업데이트
                attendance.setCheckOut(checkoutTime);
                attendance.setStatus(AttendanceStatus.CHECKOUT.getStatus());
                attendance.setWorkHours(workHours);
                attendanceMapper.updateAttendance(attendance);

                // 직원 상태 업데이트
                employeesMapper.updateAttendStatus(attendance.getEmpId(), AttendanceStatus.CHECKOUT.getStatus());

                log.debug("자동 퇴근 처리 완료: 직원 ID={}, 퇴근시간={}", attendance.getEmpId(), checkoutTime);
            }

            log.info("자동 퇴근 처리 작업 완료");
        } catch (Exception e) {
            log.error("자동 퇴근 처리 중 오류 발생", e);
            throw e;
        }
    }
}