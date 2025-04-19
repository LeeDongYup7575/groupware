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

    public List<Notice> getCachedNotices() {
        try {
            return noticeCrawler.getNotices();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
