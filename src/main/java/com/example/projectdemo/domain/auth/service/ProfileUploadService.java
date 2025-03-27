package com.example.projectdemo.domain.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ProfileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileUploadService.class);

    @Value("${file.upload-dir:uploads/profiles}")
    private String uploadDir;

    @Value("${file.profile-access-path:/uploads/profiles}")
    private String profileAccessPath;

    /**
     * 프로필 이미지를 로컬 파일 시스템에 업로드합니다.
     *
     * @param file 업로드할 프로필 이미지 파일
     * @return 업로드된 이미지의 URL 경로
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    public String uploadProfileImage(MultipartFile file) throws IOException {
        // 파일이 비어있는지 확인
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        // 파일 타입 체크
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }

        // 업로드 디렉토리 생성
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            if (!uploadDirectory.mkdirs()) {
                throw new IOException("업로드 디렉토리를 생성할 수 없습니다.");
            }
        }

        // 고유한 파일명 생성 (UUID + 원본 확장자)
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String filename = UUID.randomUUID() + fileExtension;

        // 파일 저장
        Path targetPath = Paths.get(uploadDir, filename);
        Files.copy(file.getInputStream(), targetPath);

        logger.info("프로필 이미지 업로드 성공: {}", targetPath);

        // 접근 가능한 URL 경로 반환
        return profileAccessPath + "/" + filename;
    }
}