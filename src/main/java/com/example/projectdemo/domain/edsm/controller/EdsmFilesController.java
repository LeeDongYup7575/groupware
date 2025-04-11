package com.example.projectdemo.domain.edsm.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/edsmFiles")
public class EdsmFilesController {

    @Autowired
    private HttpSession session;

    @GetMapping("/download/{sysName}/{oriName}")
    public ResponseEntity<Resource> filesDownload(@PathVariable("sysName") String sysName,
                                                  @PathVariable("oriName") String oriName) throws IOException {
        // 서버의 실제 저장 경로를 가져옵니다.
        String realPath = session.getServletContext().getRealPath("edsmUpload");
        File target = new File(realPath, sysName);
        System.out.println("실제 경로: " + realPath);
        System.out.println("타겟 파일: " + target);

        // 파일 존재 여부를 체크합니다.
        if (!target.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 한글 파일명 인코딩 처리 (HTTP Header에 안전하게 넣기 위해)
        String encodedFileName = new String(oriName.getBytes("UTF-8"), "ISO-8859-1");

        // FileSystemResource를 사용하여 파일을 Resource 객체로 감쌉니다.
        Resource resource = new FileSystemResource(target);

        // ResponseEntity를 구성하여 파일을 다운로드 응답으로 반환합니다.
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .contentLength(target.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
