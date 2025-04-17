package com.example.projectdemo.domain.videoconf.scheduler;

import com.example.projectdemo.domain.videoconf.service.VideoConfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoRoomCleanScheduler {

    private final VideoConfService videoConfService;

    /**
     * 매 5분마다 실행되어 빈 화상 회의실을 정리
     */
    @Scheduled(fixedRate = 300000) // 5분(300,000 밀리초)마다 실행
    public void cleanupEmptyVideoRooms() {
        log.info("빈 화상 회의실 정리 스케줄러 실행");
        videoConfService.cleanupEmptyRooms();
    }

    /**
     * 매 3분마다 실행되어 비활성 참가자를 정리
     */
    @Scheduled(fixedRate = 180000) // 3분(180,000 밀리초)마다 실행
    public void cleanupInactiveParticipants() {
        log.info("비활성 참가자 정리 스케줄러 실행");
        videoConfService.cleanupInactiveParticipants();
    }
}