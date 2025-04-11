package com.example.projectdemo.domain.attendance.scheduler;

import com.example.projectdemo.domain.attendance.enums.AttendanceStatus;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttendanceResetScheduler {

    private final EmployeesMapper employeesMapper;

    /**
     * 매일 자정에 실행되는 스케줄러
     * 모든 직원의 출결 상태를 "미출근"으로 초기화
     */
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    @Transactional
    public void resetAttendanceStatus() {
        log.info("근태 상태 초기화 작업 시작 - 모든 직원의 상태를 '미출근'으로 변경합니다.");

        try {
            // 모든 활성화된 직원 목록 조회
            List<EmployeesDTO> employees = employeesMapper.selectEmpAll();
            int updatedCount = 0;

            for (EmployeesDTO employee : employees) {
                // 직원이 활성화 상태인 경우만 업데이트
                if (employee.isEnabled()) {
                    // 출결 상태를 "미출근"으로 변경
                    employeesMapper.updateAttendStatus(employee.getId(), AttendanceStatus.BEFORE_WORK.getStatus());
                    updatedCount++;
                }
            }

            log.info("근태 상태 초기화 작업 완료 - 총 {}명의 직원 상태가 '미출근'으로 변경되었습니다.", updatedCount);
        } catch (Exception e) {
            log.error("근태 상태 초기화 작업 중 오류 발생", e);
            throw e;
        }
    }
}