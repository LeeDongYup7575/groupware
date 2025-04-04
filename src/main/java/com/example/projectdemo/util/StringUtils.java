package com.example.projectdemo.util;

import org.springframework.stereotype.Component;

@Component
public class StringUtils {
    public String removeFirstNChars(String str, int n) {
        return str.length() > n ? str.substring(n) : str;
    }
}
