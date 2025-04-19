package com.example.projectdemo.domain.notification.crawler;

import com.example.projectdemo.domain.notification.model.Notice;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NoticeCrawler {

    private static final ConcurrentHashMap<String, CacheEntry<List<Notice>>> CACHE = new ConcurrentHashMap<>();
    private static final long CACHE_EXPIRY_TIME_MS = 3600000; // 1시간(밀리초)

    @Value("${notice.url:https://www.hrdit.co.kr/renew_community/notice/list.php}")
    private String noticeUrl;

    // 공지사항 URL 기본값
    private static final String NOTICE_BASE_URL = "https://www.hrdit.co.kr/renew_community/notice/view.php?id=";

    // ID 추출을 위한 정규식 패턴
    private static final Pattern ID_PATTERN = Pattern.compile("javascript:view\\('(\\d+)'\\)");

    @Cacheable("notices")
    public List<Notice> getNotices() {
        String cacheKey = "notices";

        // 캐시에서 데이터 확인
        CacheEntry<List<Notice>> cachedNotices = CACHE.get(cacheKey);

        // 캐시가 유효한지 확인
        if (isValidCache(cachedNotices)) {
            return cachedNotices.getData();
        }

        // 캐시가 없거나 만료되었으면 크롤링 실행
        List<Notice> freshNotices = crawlNotices();

        // 결과를 캐시에 저장
        CACHE.put(cacheKey, new CacheEntry<>(freshNotices));

        return freshNotices;
    }

    /**
     * JSoup을 사용하여 공지사항을 크롤링하는 메소드
     */
    public List<Notice> crawlNotices() {
        List<Notice> noticeList = new ArrayList<>();

        try {
            // JSoup을 사용하여 페이지 로드
            Document document = Jsoup.connect(noticeUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36")
                    .timeout(10000)  // 10초 타임아웃
                    .get();

            // 테이블에서 모든 행(tr) 찾기 - tbody 내의 tr들을 찾음
            Elements rows = document.select("tbody > tr");

            // 각 행에서 공지사항 정보 추출
            int count = 0;
            for (Element row : rows) {
                // 첫 번째 헤더 행은 건너뛰기
                if (row.select("th").size() > 0) {
                    continue;
                }

                Elements tds = row.select("td");
                if (tds.size() < 4) continue; // 최소한 번호, 제목, 등록일, 조회수 열이 있어야 함

                Element titleElement = row.select("td.subject a").first();
                if (titleElement == null) continue;

                String href = titleElement.attr("href");
                String title = titleElement.text();

                // 중요 공지사항 여부 확인 (스타일 속성에 color:#f66602 포함 여부)
                boolean isHighlighted = titleElement.hasAttr("style") &&
                        titleElement.attr("style").contains("color:#f66602");

                // 강조된 공지사항만 수집하고 최대 4개로 제한
                if (isHighlighted && count < 4) {
                    // ID 추출
                    Matcher matcher = ID_PATTERN.matcher(href);
                    if (matcher.find()) {
                        String id = matcher.group(1);
                        String postUrl = NOTICE_BASE_URL + id;

                        // 등록일 추출
                        String dateStr = tds.get(2).text().trim();

                        // 조회수 추출
                        String viewCountStr = tds.get(3).text().trim();
                        int viewCount = 0;
                        try {
                            viewCount = Integer.parseInt(viewCountStr);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        // 확장된 Notice 객체 생성 및 추가
                        Notice notice = new Notice(id, title, postUrl, dateStr, viewCount, isHighlighted);
                        noticeList.add(notice);
                        count++;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

            if (noticeList.isEmpty()) {
                noticeList.add(new Notice("0", "현재 공지사항을 불러올 수 없습니다.", "#", "2025-04-17", 0, false));
            }
        }

        return noticeList;
    }

    /**
     * 캐시가 유효한지 확인하는 메소드
     */
    private boolean isValidCache(CacheEntry<List<Notice>> cacheEntry) {
        // 캐시가 존재하고 만료되지 않았는지 확인
        return cacheEntry != null &&
                (System.currentTimeMillis() - cacheEntry.getCreatedTime() < CACHE_EXPIRY_TIME_MS);
    }

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