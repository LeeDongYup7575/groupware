package com.example.projectdemo.domain.edsm.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;

@Controller
@RequestMapping("/edsmFiles")
public class EdsmFilesController {

    @Autowired
    HttpSession session;

    @Autowired
    HttpServletResponse response;

    @RequestMapping("/download/{sysName}/{oriName}")
    public void filesDownload(@PathVariable("sysName") String sysName, @PathVariable("oriName") String oriName) throws IOException {

        String realPath = session.getServletContext().getRealPath("upload");
        File target = new File(realPath + "/" + sysName);
        System.out.println(realPath);
        System.out.println(target);
        // 파일 존재 여부 체크
        if (!target.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 한글 파일명을 위한 인코딩 처리
        oriName = new String(oriName.getBytes("UTF-8"), "ISO-8859-1");

        response.reset();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + oriName + "\"");
        response.setContentLength((int) target.length());

        // 스트리밍 방식으로 파일 전송 (메모리 부담을 줄이기 위해 버퍼 사용)
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(target));
             BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {

            byte[] buffer = new byte[8192]; // 8KB 버퍼
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush();
        }
    }


}
