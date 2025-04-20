package com.example.projectdemo.domain.leave.scheduler;

import com.example.projectdemo.domain.leave.service.LeavesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LeaveScheduler {
    @Autowired
    private LeavesService leavesService;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void runLeaveUpdate() {
        leavesService.updateByLeaves();
    }
}
