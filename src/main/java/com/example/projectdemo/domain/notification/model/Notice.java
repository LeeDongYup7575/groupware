package com.example.projectdemo.domain.notification.model;

import java.time.LocalDate;

public class Notice {
    private String id;
    private String title;
    private String url;
    private String date;  // 등록일
    private int viewCount;  // 조회수
    private boolean isHighlighted;  // 강조된 공지사항 여부

    // 기본 생성자
    public Notice(String id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    // 확장된 생성자
    public Notice(String id, String title, String url, String date, int viewCount, boolean isHighlighted) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.date = date;
        this.viewCount = viewCount;
        this.isHighlighted = isHighlighted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }
}