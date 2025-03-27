package com.example.projectdemo.domain.notification.crawler;

import com.example.projectdemo.domain.notification.model.Notice;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NoticeCrawler {

    // 메모리 캐시 (실제 프로덕션에서는 Redis 등의 외부 캐시 서버 권장)
    private static final ConcurrentHashMap<String, CacheEntry<List<Notice>>> CACHE = new ConcurrentHashMap<>();
    private static final long CACHE_EXPIRY_TIME_MS = 3600000; // 1시간(밀리초)

    /**
     * 공지사항 크롤링 메소드 - 캐싱 기능 추가
     *
     * @return 공지사항 목록
     */
    public List<Notice> getNotices() {
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
        List<Notice> freshNotices = crawlNotices();

        // 결과를 캐시에 저장
        CACHE.put(cacheKey, new CacheEntry<>(freshNotices));

        return freshNotices;
    }

    /**
     * 실제 크롤링을 수행하는 메소드
     */
    public static List<Notice> crawlNotices() {
        List<Notice> noticeList = new ArrayList<>();

        System.out.println("===== 공지사항 크롤링 시작 =====");

        try {
            // WebDriver 경로 설정
            System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver");

            // Chrome 옵션 설정
            System.out.println("Chrome 옵션 설정 중...");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless"); // headless 모드로 실행
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*");

            // ChromeDriver 객체 생성
            System.out.println("ChromeDriver 객체 생성 시도...");
            WebDriver driver = new ChromeDriver(options);
            System.out.println("ChromeDriver 객체 생성 성공!");

            try {
                // 공지사항 페이지로 이동
                System.out.println("공지사항 페이지로 이동 중...");
                driver.get("https://www.keduit.com/renew_community/notice/list.php");
                System.out.println("페이지 로드 완료");

                // 게시글 목록 찾기
                System.out.println("게시글 목록 찾는 중...");
                List<WebElement> posts = driver.findElements(By.cssSelector("a[href^='javascript:view']"));
                System.out.println("찾은 게시글 수: " + posts.size());

                // 각 게시글 클릭하여 정보 추출
                for (WebElement post : posts) {
                    String postTitle = post.getText();
                    String postHref = post.getAttribute("href");
                    System.out.println("게시글 제목: " + postTitle);
                    System.out.println("게시글 href: " + postHref);

                    // 게시글 ID 추출
                    String postId = postHref.replaceAll("javascript:view\\('(\\d+)'\\)", "$1");
                    System.out.println("추출된 ID: " + postId);

                    // 게시글 URL 생성
                    String postUrl = "https://www.keduit.com/renew_community/notice/view.php?id=" + postId;
                    System.out.println("생성된 URL: " + postUrl);

                    // Notice 객체 생성하여 리스트에 추가
                    if (!postTitle.isEmpty() && !postUrl.isEmpty()) {
                        noticeList.add(new Notice(postId, postTitle, postUrl));
                        System.out.println("공지사항 객체 추가 완료");
                    }
                }
            } catch (Exception e) {
                System.out.println("크롤링 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // 웹 드라이버 종료
                System.out.println("웹 드라이버 종료 중...");
                driver.quit();
                System.out.println("웹 드라이버 종료 완료");
            }
        } catch (Exception e) {
            System.out.println("ChromeDriver 설정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("총 수집된 공지사항 수: " + noticeList.size());
        System.out.println("===== 공지사항 크롤링 종료 =====");

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