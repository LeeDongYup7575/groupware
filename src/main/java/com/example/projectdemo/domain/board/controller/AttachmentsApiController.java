package com.example.projectdemo.domain.board.controller;

import com.example.projectdemo.domain.board.dto.AttachmentsDTO;
import com.example.projectdemo.domain.board.service.AttachmentsService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.charset.StandardCharsets;


/**
 * 첨부 파일 다운로드 및 관리를 위한 REST 컨트롤러
 */
@RestController
@RequestMapping("/api/attachments")
public class AttachmentsApiController {

    @Autowired
    private AttachmentsService attachmentsService;

    @Autowired
    private HttpSession session;

    private static final Logger logger = LoggerFactory.getLogger(AttachmentsApiController.class);


    /**
     * 첨부 파일 다운로드
     *
     * @param id 첨부 파일 ID
     * @return 파일 다운로드 응답
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable int id) {
        try {
            // 첨부 파일 정보 조회
            AttachmentsDTO attachment = attachmentsService.getAttachmentById(id);
            if (attachment == null) {
                return ResponseEntity.notFound().build();
            }

            // 서버의 실제 경로를 가져온다
            String realPath = session.getServletContext().getRealPath("uploads/attachments");
            File target = new File(realPath, attachment.getSysName());

            // 파일이 존재하는지 확인
            if (!target.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 한글 파일명 인코딩 처리
            String encodedFileName = new String(attachment.getOriginName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);


            // 파일 리소스 생성
            Resource resource = new FileSystemResource(target);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                    .contentLength(target.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            logger.error("파일 다운로드 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("파일 다운로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 첨부 파일 삭제 - GET 메서드
     *
     * @param id 첨부 파일 ID
     * @return 삭제 결과
     */
    @GetMapping("/delete/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable int id) {
        try {
            boolean deleted = attachmentsService.deleteAttachment(id);

            if (deleted) {
                return ResponseEntity.ok().body("파일이 성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("파일을 찾을 수 없거나 삭제할 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 첨부 파일 삭제 - DELETE 메서드 (추가)
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFileByDelete(@PathVariable int id) {
        // 기존 deleteFile 메서드와 동일한 로직
        return deleteFile(id);
    }

}
