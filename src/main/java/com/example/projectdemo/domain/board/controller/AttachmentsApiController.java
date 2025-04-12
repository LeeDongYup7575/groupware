package com.example.projectdemo.domain.board.controller;

import com.example.projectdemo.domain.board.dto.AttachmentsDTO;
import com.example.projectdemo.domain.board.service.AttachmentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 첨부 파일 다운로드 및 관리를 위한 REST 컨트롤러
 */
@RestController
@RequestMapping("/api/attachments")
public class AttachmentsApiController {

    @Autowired
    private AttachmentsService attachmentsService;

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
            logger.info("첨부 파일 다운로드 요청: ID={}", id);

            // 첨부 파일 정보 조회
            AttachmentsDTO attachment = attachmentsService.getAttachmentById(id);
            if (attachment == null) {
                logger.warn("첨부 파일을 찾을 수 없음: ID={}", id);
                return ResponseEntity.notFound().build();
            }

            // 파일 리소스 로드
            Resource resource = attachmentsService.loadFileAsResource(attachment);
            logger.info("파일 리소스 로드 완료: {}", resource.getFilename());

            // 파일명 인코딩 (한글 등 지원)
            String encodedFilename = URLEncoder.encode(attachment.getOriginName(), StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");

            // 컨텐츠 타입 설정
            String contentType = "application/octet-stream";

            logger.info("파일 다운로드 준비 완료: 파일명={}, 컨텐츠 타입={}", encodedFilename, contentType);

            // 응답 헤더 설정
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                    .body(resource);

        } catch (IOException e) {
            logger.error("파일 다운로드 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 다운로드 중 오류가 발생했습니다: " + e.getMessage());
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
        logger.info("첨부 파일 삭제 요청: ID={}", id);

        try {
            boolean deleted = attachmentsService.deleteAttachment(id);

            if (deleted) {
                logger.info("첨부 파일 삭제 성공: ID={}", id);
                return ResponseEntity.ok().body("파일이 성공적으로 삭제되었습니다.");
            } else {
                logger.warn("첨부 파일 삭제 실패: ID={}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("파일을 찾을 수 없거나 삭제할 수 없습니다.");
            }
        } catch (Exception e) {
            logger.error("첨부 파일 삭제 중 오류 발생: {}", e.getMessage(), e);
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
