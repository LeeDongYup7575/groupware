package com.example.projectdemo.domain.board.service;

import com.example.projectdemo.domain.board.dto.AttachmentsDTO;
import com.example.projectdemo.domain.board.mapper.AttachmentsMapper;
import com.example.projectdemo.domain.s3.service.S3Service;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 첨부 파일 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
public class AttachmentsService {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentsService.class);

    @Autowired
    private AttachmentsMapper attachmentsMapper;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private HttpSession session;

    /**
     * 다중 파일을 S3에 업로드하고 저장합니다.
     *
     * @param files 업로드할 파일 목록
     * @param postId 연결할 게시글 ID
     * @return 저장된 첨부 파일 목록
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    public List<AttachmentsDTO> uploadFiles(List<MultipartFile> files, int postId) throws IOException {
        List<AttachmentsDTO> attachments = new ArrayList<>();

        // 각 파일 저장
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            // 원본 파일명 추출
            String originalFilename = file.getOriginalFilename();

            // 고유한 요청 ID 생성
            String requestId = "post_" + postId + "_" + UUID.randomUUID().toString();

            // S3에 파일 업로드
            String fileUrl = s3Service.uploadBoardAttachment(file, requestId);

            // 첨부 파일 정보 생성
            AttachmentsDTO attachment = AttachmentsDTO.builder()
                    .postId(postId)
                    .originName(originalFilename)
                    .sysName(fileUrl) // S3 URL을 시스템 이름으로 저장
                    .build();

            // DB에 저장
            attachmentsMapper.insertAttachment(attachment);
            attachments.add(attachment);
        }

        return attachments;
    }

    /**
     * 첨부 파일 ID로 파일을 조회합니다.
     *
     * @param id 첨부 파일 ID
     * @return 첨부 파일 정보
     */
    public AttachmentsDTO getAttachmentById(int id) {
        return attachmentsMapper.findAttachmentById(id);
    }

    /**
     * 게시글 ID로 첨부 파일을 조회합니다.
     *
     * @param postId 게시글 ID
     * @return 첨부 파일 리스트
     */
    public List<AttachmentsDTO> selectAttachmentsByPostId(int postId) {
        return attachmentsMapper.selectAttachmentsByPostId(postId);
    }

    /**
     * 첨부 파일을 리소스로 로드 (S3 URL에서 직접 다운로드)
     *
     * @param attachment 첨부 파일 정보
     * @return 파일 리소스
     * @throws IOException 파일 로드 중 오류 발생 시
     */
    public Resource loadFileAsResource(AttachmentsDTO attachment) throws IOException {
        try {
            // S3 URL인지 확인
            if (attachment.getSysName().startsWith("http")) {
                // S3 URL을 통해 파일 내용 가져오기
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<byte[]> response = restTemplate.exchange(
                        attachment.getSysName(),
                        HttpMethod.GET,
                        null,
                        byte[].class
                );

                if (response.getBody() != null) {
                    return new ByteArrayResource(response.getBody());
                } else {
                    throw new IOException("파일 내용을 가져올 수 없습니다: " + attachment.getOriginName());
                }
            } else {
                // 로컬 파일인 경우 기존 방식으로 처리
                String realPath = session.getServletContext().getRealPath("uploads/attachments");
                File file = new File(realPath, attachment.getSysName());

                // FileSystemResource를 사용하여 파일을 Resource 객체로 변환
                Resource resource = new FileSystemResource(file);

                if (resource.exists()) {
                    return resource;
                } else {
                    throw new IOException("파일을 찾을 수 없습니다: " + attachment.getOriginName());
                }
            }
        } catch (Exception e) {
            throw new IOException("파일 리소스 로드 오류: " + attachment.getOriginName(), e);
        }
    }

    /**
     * 첨부 파일 삭제
     *
     * @param id 첨부 파일 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteAttachment(int id) {
        // 파일 정보 조회
        AttachmentsDTO attachment = attachmentsMapper.findAttachmentById(id);
        if (attachment == null) {
            return false;
        }

        try {
            // S3 URL인지 확인
            if (attachment.getSysName().startsWith("http")) {
                // S3에서 파일 삭제
                s3Service.deleteFile(attachment.getSysName());
            } else {
                // 로컬 파일 삭제
                String realPath = session.getServletContext().getRealPath("uploads/attachments");
                File file = new File(realPath, attachment.getSysName());
                file.delete();
            }

            // DB에서 정보 삭제
            int result = attachmentsMapper.deleteAttachment(id);

            return result > 0;
        } catch (Exception e) {
            logger.error("파일 삭제 중 오류: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 게시글에 연결된 모든 첨부 파일 삭제
     *
     * @param postId 게시글 ID
     * @return 삭제된 파일 수
     */
    public int deleteAttachmentsByPostId(int postId) {
        // 게시글 첨부 파일 조회
        List<AttachmentsDTO> attachments = attachmentsMapper.selectAttachmentsByPostId(postId);
        int deletedCount = 0;

        for (AttachmentsDTO attachment : attachments) {
            try {
                // S3 URL인지 확인
                if (attachment.getSysName().startsWith("http")) {
                    // S3에서 파일 삭제
                    s3Service.deleteFile(attachment.getSysName());
                } else {
                    // 로컬 파일 삭제
                    String realPath = session.getServletContext().getRealPath("uploads/attachments");
                    File file = new File(realPath, attachment.getSysName());
                    file.delete();
                }
                deletedCount++;
            } catch (Exception e) {
                logger.error("파일 삭제 중 오류: id={}, {}", attachment.getId(), e.getMessage());
            }
        }

        // DB에서 정보 삭제
        int result = attachmentsMapper.deleteAttachmentsByPostId(postId);

        return result;
    }

    /**
     * 로컬 파일 시스템에서 첨부 파일을 리소스로 로드 (기존 방식 지원)
     *
     * @param attachment 첨부 파일 정보
     * @return 파일 다운로드 응답
     * @throws IOException 파일 로드 중 오류 발생 시
     */
    public ResponseEntity<Resource> loadLocalFileAsResource(AttachmentsDTO attachment) throws IOException {
        try {
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
            logger.error("로컬 파일 로드 중 오류: {}", e.getMessage());
            throw new IOException("파일 리소스 로드 오류: " + attachment.getOriginName(), e);
        }
    }
}