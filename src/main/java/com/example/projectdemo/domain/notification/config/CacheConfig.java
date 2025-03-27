package com.example.projectdemo.domain.notification.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.example.projectdemo.domain.notification.crawler.NoticeCrawler;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

    private final NoticeCrawler noticeCrawler;

    public CacheConfig(NoticeCrawler noticeCrawler) {
        this.noticeCrawler = noticeCrawler;
    }

    @Bean
    public CacheManager cacheManager() {
        // 간단한 메모리 기반 캐시 매니저
        // 프로덕션 환경에서는 Redis나 다른 분산 캐시 솔루션을 사용하는 것을 권장
        return new ConcurrentMapCacheManager("notices");
    }

    @Scheduled(fixedRate = 3600000)
    public void refreshNoticesCache() {
        System.out.println("스케줄링된 작업: 공지사항 캐시 갱신 중...");
        noticeCrawler.getNotices(); // 캐시 갱신을 위해 크롤링 실행
        System.out.println("공지사항 캐시가 갱신되었습니다.");
    }
}
