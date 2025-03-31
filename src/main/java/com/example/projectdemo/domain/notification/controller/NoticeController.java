package com.example.projectdemo.domain.notification.controller;

import com.example.projectdemo.domain.notification.crawler.NoticeCrawler;
import com.example.projectdemo.domain.notification.model.Notice;
import com.example.projectdemo.tmp.TmpJwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class NoticeController {

    // 메모리 캐시
    private static final ConcurrentHashMap<String, CacheEntry<List<Notice>>> CACHE = new ConcurrentHashMap<>();
    // 캐시 유효 시간 (1시간 = 3600000ms)
    private static final long CACHE_EXPIRY_TIME_MS = 3600000;

    private final NoticeCrawler noticeCrawler;

    @Autowired
    public NoticeController(NoticeCrawler noticeCrawler) {
        this.noticeCrawler = noticeCrawler;
    }

    @GetMapping("/")
    public String home(Model model, HttpServletResponse response) {
        System.out.println("===== / 엔드포인트 요청 시작 =====");

        // 캐시에서 공지사항 데이터 가져오기 시도
        List<Notice> notices = getCachedNotices();

        // 크롤링한 공지사항을 모델에 추가
        model.addAttribute("notices", notices);
        System.out.println("모델에 'notices' 속성 추가 완료");

        System.out.println("'intro' 템플릿 반환 준비 완료");
        System.out.println("===== / 엔드포인트 요청 종료 =====");

        return "intro";
    }

    private List<Notice> getCachedNotices() {
        String cacheKey = "notices";

        // 캐시에서 데이터 확인
        CacheEntry<List<Notice>> cachedNotices = CACHE.get(cacheKey);

        // 캐시가 유효한지 확인
        if (isValidCache(cachedNotices)) {
            System.out.println("캐시에서 공지사항 데이터를 로드합니다.");
            return cachedNotices.getData();
        }

        // 캐시가 없거나 만료되었으면 크롤링 실행
        System.out.println("캐시가 없거나 만료되어 크롤링을 시작합니다.");
        List<Notice> freshNotices;

        try {
            // NoticeCrawler를 사용하여 공지사항 데이터 크롤링
            System.out.println("NoticeCrawler.crawlNotices() 호출 시작");
            freshNotices = NoticeCrawler.crawlNotices();
            System.out.println("NoticeCrawler.crawlNotices() 호출 완료");
            System.out.println("크롤링된 공지사항 수: " + freshNotices.size());

            // 결과를 캐시에 저장
            CACHE.put(cacheKey, new CacheEntry<>(freshNotices));
            System.out.println("공지사항 데이터가 캐시에 저장되었습니다.");
        } catch (Exception e) {
            System.out.println("공지사항 크롤링 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            // 오류 발생 시 빈 리스트로 초기화하여 계속 진행
            freshNotices = new ArrayList<>();
        }

        return freshNotices;
    }

    /**
     * 캐시가 유효한지 확인하는 메소드
     */
    private boolean isValidCache(CacheEntry<List<Notice>> cacheEntry) {
        // 캐시가 존재하고 만료되지 않았는지 확인
        return cacheEntry != null &&
                (System.currentTimeMillis() - cacheEntry.getCreatedTime() < CACHE_EXPIRY_TIME_MS);
    }

    /**
     * 캐시 데이터와 생성 시간을 저장하는 내부 클래스
     */
    private static class CacheEntry<T> {
        private final T data;
        private final long createdTime;

        public CacheEntry(T data) {
            this.data = data;
            this.createdTime = System.currentTimeMillis();
        }

        public T getData() {
            return data;
        }

        public long getCreatedTime() {
            return createdTime;
        }
    }




}