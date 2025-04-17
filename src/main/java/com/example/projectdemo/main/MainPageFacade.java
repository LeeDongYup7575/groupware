package com.example.projectdemo.main;

import jakarta.servlet.http.HttpServletRequest;

public interface MainPageFacade {
    MainPageData prepareMainPageData(HttpServletRequest request, String token);
}

