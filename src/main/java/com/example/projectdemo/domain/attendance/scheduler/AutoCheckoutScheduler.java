package com.example.projectdemo.domain.attendance.scheduler;

import com.example.projectdemo.domain.attendance.entity.Attendance;
import com.example.projectdemo.domain.attendance.enums.AttendanceStatus;
import com.example.projectdemo.domain.attendance.mapper.AttendanceMapper;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoCheckoutScheduler {

    private final AttendanceMapper attendanceMapper;
    private final EmployeesMapper employeesMapper;

    private static final LocalTime STANDARD_END_TIME = LocalTime.of(18, 0); // 오후 6시

    /**
     * 매일 오후 6시 5분에 실행되는 스케줄러
     * 출근했지만 퇴근 기록이 없는 직원들에게 자동으로 퇴근 처리
     */
    @Scheduled(cron = "0 5 18 * * ?")
    @Transactional
    public void processAutoCheckout() {
        log.info("자동 퇴근 처리 작업 시작");
        LocalDate today = LocalDate.now();
        LocalTime checkoutTime = STANDARD_END_TIME;

        try {
            // 전체 직원 ID 목록 가져오기
            List<Integer> allEmployeeIds = employeesMapper.findAllEmployeeIds();

            for (Integer empId : allEmployeeIds) {
                // 오늘의 출근 기록 조회
                List<Attendance> todayRecords = attendanceMapper.getAttendanceListByEmployeeAndDate(empId, today);

                boolean hasCheckIn = todayRecords.stream().anyMatch(a -> a.getCheckIn() != null);
                boolean hasCheckOut = todayRecords.stream().anyMatch(a -> a.getCheckOut() != null);

                if (hasCheckIn && !hasCheckOut) {
                    // 출근은 했지만 퇴근 안 한 경우 → 퇴근 기록 insert
                    Attendance checkInRecord = todayRecords.stream()
                            .filter(a -> a.getCheckIn() != null)
                            .findFirst()
                            .orElse(null);

                    if (checkInRecord != null) {
                        long minutes = checkInRecord.getCheckIn().until(checkoutTime, ChronoUnit.MINUTES);
                        BigDecimal workHours = BigDecimal.valueOf(minutes / 60.0);

                        Attendance checkoutRecord = Attendance.builder()
                                .empId(empId)
                                .workDate(today)
                                .checkOut(checkoutTime)
                                .status(AttendanceStatus.CHECKOUT.getStatus())
                                .workHours(workHours)
                                .build();

                        attendanceMapper.insertAttendance(checkoutRecord);
                        employeesMapper.updateAttendStatus(empId, AttendanceStatus.CHECKOUT.getStatus());

                        log.debug("자동 퇴근 insert 완료: ID={}, 시간={}", empId, checkoutTime);
                    }
                }

                if (!hasCheckIn) {
                    // 출근 기록 없음 → 결근 insert
                    Attendance absentRecord = Attendance.builder()
                            .empId(empId)
                            .workDate(today)
                            .status(AttendanceStatus.ABSENT.getStatus()) // "결근"
                            .workHours(BigDecimal.ZERO)
                            .build();

                    attendanceMapper.insertAttendance(absentRecord);
                    employeesMapper.updateAttendStatus(empId, AttendanceStatus.ABSENT.getStatus());

                    log.debug("결근 처리 완료: ID={}", empId);
                }
            }

            log.info("자동 퇴근/결근 처리 완료");
        } catch (Exception e) {
            log.error("자동 퇴근 처리 오류", e);
            throw e;
        }
    }

}