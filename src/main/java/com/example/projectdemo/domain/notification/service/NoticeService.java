package com.example.projectdemo.domain.notification.service;

import com.example.projectdemo.domain.notification.crawler.NoticeCrawler;
import com.example.projectdemo.domain.notification.model.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class NoticeService {

    private final NoticeCrawler noticeCrawler;

    @Autowired
    public NoticeService(NoticeCrawler noticeCrawler) {
        this.noticeCrawler = noticeCrawler;
    }

    /**
     * 캐시된 공지사항을 가져오는 메소드
     * MainPageFacade에서 호출됨
     */
    public List<Notice> getCachedNotices() {
        try {
            return noticeCrawler.getNotices();
        } catch (Exception e) {
            System.out.println("공지사항 로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
