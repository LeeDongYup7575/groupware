package com.example.projectdemo.domain.attendance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayService {

    private final RestTemplate restTemplate;

    private static final String API_URL = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";
    private static final String SERVICE_KEY = "JpUfXhfGf9vZkQS2T%2FvIfRbdnVRSJFw170R4O47dm1TLkOMV3WjL88xF92Ic%2B150fjg%2FCCobaebABguDNHNhtw%3D%3D";

    public Set<LocalDate> getHolidayList(int year, int month) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("serviceKey", SERVICE_KEY)
                .queryParam("solYear", year)
                .queryParam("solMonth", String.format("%02d", month));

        String xml = restTemplate.getForObject(builder.toUriString(), String.class);

        return parseHolidayDatesFromXml(xml);
    }

    private Set<LocalDate> parseHolidayDatesFromXml(String xml) {
        Set<LocalDate> holidays = new HashSet<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
            NodeList items = doc.getElementsByTagName("item");

            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                String locdate = item.getElementsByTagName("locdate").item(0).getTextContent(); // ex: 20250415
                LocalDate date = LocalDate.parse(locdate, DateTimeFormatter.ofPattern("yyyyMMdd"));
                holidays.add(date);
            }
        } catch (Exception e) {
            log.error("공휴일 파싱 중 오류 발생", e);
        }

        return holidays;
    }

    // ✅ 추가된 메서드
    public boolean isHoliday(LocalDate date) {
        Set<LocalDate> holidays = getHolidayList(date.getYear(), date.getMonthValue());
        return holidays.contains(date);
    }
}
