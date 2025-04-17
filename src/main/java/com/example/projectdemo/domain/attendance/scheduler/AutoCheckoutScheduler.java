package com.example.projectdemo.domain.attendance.scheduler;

import com.example.projectdemo.domain.attendance.entity.Attendance;
import com.example.projectdemo.domain.attendance.enums.AttendanceStatus;
import com.example.projectdemo.domain.attendance.mapper.AttendanceMapper;
import com.example.projectdemo.domain.attendance.service.HolidayService;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
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
    private final HolidayService holidayService;

    private static final LocalTime STANDARD_END_TIME = LocalTime.of(18, 0); // 오후 6시

    /**
     * 매일 오후 6시 5분에 실행되는 스케줄러
     * 출근했지만 퇴근 기록이 없는 직원들에게 자동으로 퇴근 처리
     */
    @Scheduled(cron = "0 5 18 * * ?")
    // 예: 현재 시간으로부터 1분 후로 설정
//    @Scheduled(fixedDelay = 60000)  // 60초 마다 실행 (테스트용)
    @Transactional
    public void processAutoCheckout() {
        log.info("자동 퇴근 처리 작업 시작");
        LocalDate today = LocalDate.now();
        LocalTime checkoutTime = STANDARD_END_TIME;

        // ✅ 공휴일 체크
        if (holidayService.isHoliday(today)) {
            log.info("공휴일이므로 자동 퇴근/결근 처리 스킵");
            return;
        }

        // ✅ 주말 체크
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            log.info("주말이므로 자동 퇴근/결근 처리 스킵");
            return;
        }

        try {
            List<Integer> allEmployeeIds = employeesMapper.findAllEmployeeIds();

            for (Integer empId : allEmployeeIds) {
                List<Attendance> todayRecords = attendanceMapper.getAttendanceListByEmployeeAndDate(empId, today);

                boolean hasCheckIn = todayRecords.stream().anyMatch(a -> a.getCheckIn() != null);
                boolean hasCheckOutOrLeftEarly = todayRecords.stream().anyMatch(a ->
                        a.getCheckOut() != null ||
                                a.getStatus().equals(AttendanceStatus.EARLY_LEAVE.getStatus())
                );

                if (hasCheckIn && hasCheckOutOrLeftEarly) {
                    // 출근했고 이미 퇴근 또는 조퇴 처리된 경우 → 스킵
                    log.debug("이미 퇴근 또는 조퇴 상태. 자동 퇴근 스킵: ID={}", empId);
                    continue;
                }

                if (hasCheckIn) {
                    // 출근은 했지만 퇴근 안 한 경우 → 자동 퇴근 처리
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

                        log.debug("자동 퇴근 처리 완료: ID={}, 시간={}", empId, checkoutTime);
                    }
                } else {
                    // 출근 기록 없음 → 결근 처리
                    Attendance absentRecord = Attendance.builder()
                            .empId(empId)
                            .workDate(today)
                            .status(AttendanceStatus.ABSENT.getStatus())
                            .workHours(BigDecimal.ZERO)
                            .build();

                    attendanceMapper.insertAttendance(absentRecord);
                    employeesMapper.updateAttendStatus(empId, AttendanceStatus.ABSENT.getStatus());

                    log.debug("결근 처리 완료: ID={}", empId);
                }
            }

            log.info("자동 퇴근/결근 처리 완료");
        } catch (Exception e) {
            log.error("자동 퇴근 처리 중 오류 발생", e);
            throw e;
        }
    }
}
