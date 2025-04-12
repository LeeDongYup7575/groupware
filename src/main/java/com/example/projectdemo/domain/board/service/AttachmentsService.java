package com.example.projectdemo.domain.board.service;

import com.example.projectdemo.domain.board.dto.AttachmentsDTO;
import com.example.projectdemo.domain.board.mapper.AttachmentsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    @Value("${file.attachments-dir}")
    private String uploadDir;

    /**
     * 다중 파일을 업로드하고 저장합니다.
     *
     * @param files 업로드할 파일 목록
     * @param postId 연결할 게시글 ID
     * @return 저장된 첨부 파일 목록
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    public List<AttachmentsDTO> uploadFiles(List<MultipartFile> files, int postId) throws IOException {
        List<AttachmentsDTO> savedAttachments = new ArrayList<>();

        // 파일이 없는 경우 빈 목록 반환
        if (files == null || files.isEmpty()) {
            return savedAttachments;
        }

        // 오늘 날짜 기반 저장 디렉토리 생성
        String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        Path savePath = Paths.get(uploadDir, datePath); // 경로 생성
        Files.createDirectories(savePath); // 디렉토리 없으면 생성

        System.out.println("savePath: " + savePath);

        // 디렉토리 생성
        File directory = savePath.toFile();

        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 각 파일 저장
        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                System.out.println("Skipping empty file");
                continue;
            }


            if (file.isEmpty()) continue;

            // 원본 파일명과 확장자 추출
            String originalFilename = file.getOriginalFilename();
            System.out.println("Processing file: " + file.getOriginalFilename());

            // 고유한 파일명 생성
            String newFilename = UUID.randomUUID().toString();
            if (originalFilename != null && originalFilename.contains(".")) {
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                newFilename += fileExtension;
            }

            // 파일 저장 경로
            Path targetPath = savePath.resolve(newFilename);

            // 파일 저장
            Files.copy(file.getInputStream(), targetPath);
            System.out.println("File saved at: " + targetPath);

            // 첨부 파일 정보 생성
            AttachmentsDTO attachment = AttachmentsDTO.builder()
                    .postId(postId)
                    .originName(originalFilename)
                    .sysName(datePath + File.separator + newFilename) // 날짜 경로를 포함한 시스템 파일명 저장
                    .build();

            // DB에 저장
            attachmentsMapper.insertAttachment(attachment);
            savedAttachments.add(attachment);
        }

        return savedAttachments;
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
     * 첨부 파일을 리소스로 로드
     *
     * @param attachment 첨부 파일 정보
     * @return 파일 리소스
     * @throws IOException 파일 로드 중 오류 발생 시
     */
    public Resource loadFileAsResource(AttachmentsDTO attachment) throws IOException {
        try {
            Path filePath = Paths.get(uploadDir).resolve(attachment.getSysName());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                logger.info("파일 리소스 로드 성공: {}", filePath);
                return resource;
            } else {
                logger.error("파일이 존재하지 않음: {}", filePath);
                throw new IOException("파일을 찾을 수 없습니다: " + attachment.getOriginName());
            }
        } catch (MalformedURLException e) {
            logger.error("파일 URL 생성 오류: {}", e.getMessage());
            throw new IOException("파일 URL 생성 오류: " + attachment.getOriginName());
        }
    }

    /**
     * 첨부 파일 삭제
     *
     * @param id 첨부 파일 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteAttachment(int id) {
        logger.info("첨부 파일 삭제 시작: id={}", id);

        // 파일 정보 조회
        AttachmentsDTO attachment = attachmentsMapper.findAttachmentById(id);
        if (attachment == null) {
            logger.warn("삭제할 첨부 파일 정보 없음: id={}", id);
            return false;
        }

        try {
            // 파일 삭제
            Path filePath = Paths.get(uploadDir).resolve(attachment.getSysName());
            boolean fileDeleted = Files.deleteIfExists(filePath);

            if (fileDeleted) {
                logger.info("파일 삭제 성공: {}", filePath);
            } else {
                logger.warn("파일이 이미 삭제됨: {}", filePath);
            }

            // DB에서 정보 삭제
            int result = attachmentsMapper.deleteAttachment(id);
            logger.info("DB 첨부 파일 정보 삭제: id={}, 결과={}", id, result > 0);

            return result > 0;
        } catch (IOException e) {
            logger.error("파일 삭제 중 오류: {}", e.getMessage(), e);
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
        logger.info("게시글 첨부 파일 삭제 시작: postId={}", postId);

        // 게시글 첨부 파일 조회
        List<AttachmentsDTO> attachments = attachmentsMapper.selectAttachmentsByPostId(postId);
        int deletedCount = 0;

        for (AttachmentsDTO attachment : attachments) {
            try {
                // 파일 삭제
                Path filePath = Paths.get(uploadDir).resolve(attachment.getSysName());
                Files.deleteIfExists(filePath);
                deletedCount++;
            } catch (IOException e) {
                logger.error("파일 삭제 중 오류: id={}, {}", attachment.getId(), e.getMessage());
            }
        }

        // DB에서 정보 삭제
        int result = attachmentsMapper.deleteAttachmentsByPostId(postId);
        logger.info("게시글 첨부 파일 삭제 완료: postId={}, 삭제된 파일 수={}, DB 삭제 결과={}",
                postId, deletedCount, result);

        return result;
    }

}
