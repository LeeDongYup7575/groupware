package com.example.projectdemo.domain.s3.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("${upload.profile.directory}")
    private String uploadDirectory;

    @Value("${upload.profile.url.prefix}")
    private String urlPrefix;

    @Value("${upload.profile.use-s3:false}")
    private boolean useS3;

    private final S3Service s3Service;

    @Autowired
    public ProfileUploadService(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    /**
     * 프로필 이미지를 로컬과 S3에 업로드합니다.
     * useS3 설정에 따라 S3 업로드가 활성화되며, 로컬 저장소는 항상 사용됩니다.
     */
    public String uploadProfileImage(MultipartFile file) throws IOException {
        // 로컬 업로드 수행
        String localUrl = uploadToLocalStorage(file);

        // S3 업로드가 활성화된 경우
        if (useS3) {
            try {
                // S3에 업로드하고 URL 반환
                String s3Url = s3Service.uploadFile(file);
                logger.info("프로필 이미지가 S3에 성공적으로 업로드되었습니다: {}", s3Url);
                return s3Url; // S3 URL을 우선적으로 사용
            } catch (Exception e) {
                logger.warn("S3 업로드 실패, 로컬 URL을 대신 사용합니다: {}", e.getMessage());
                // S3 업로드에 실패하면 로컬 URL 사용
                return localUrl;
            }
        }

        // S3 업로드가 비활성화된 경우 로컬 URL 반환
        return localUrl;
    }

    /**
     * 프로필 이미지를 로컬 저장소에 업로드합니다.
     */
    private String uploadToLocalStorage(MultipartFile file) throws IOException {
        // 디렉토리 생성
        File directory = new File(uploadDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 파일명 생성 (UUID + 원본 파일 확장자)
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString() + extension;

        // 로컬 파일 시스템에 저장
        Path path = Paths.get(uploadDirectory, newFilename);
        Files.write(path, file.getBytes());

        logger.info("프로필 이미지가 로컬에 성공적으로 업로드되었습니다: {}", path);

        // URL 형식으로 반환 (웹에서 접근 가능한 경로)
        return urlPrefix + "/" + newFilename;
    }

    /**
     * 요청 ID를 포함한 고유한 파일명으로 프로필 이미지를 업로드합니다.
     */
    public String uploadProfileImageWithRequestId(MultipartFile file, String requestId) throws IOException {
        // 디렉토리 생성
        File directory = new File(uploadDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 파일명 생성 (요청 ID + 원본 파일 확장자)
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 요청 ID를 포함한 고유한 파일명 생성
        String newFilename = requestId + extension;

        // 로컬 파일 시스템에 저장
        Path path = Paths.get(uploadDirectory, newFilename);
        Files.write(path, file.getBytes());

        logger.info("프로필 이미지가 로컬에 성공적으로 업로드되었습니다: {}", path);

        // S3 업로드가 활성화된 경우
        if (useS3) {
            try {
                // S3 업로드 시 요청 ID를 포함하여 중복 방지
                String s3Url = s3Service.uploadFileWithRequestId(file, requestId);
                logger.info("프로필 이미지가 S3에 성공적으로 업로드되었습니다: {}", s3Url);
                return s3Url; // S3 URL을 우선적으로 사용
            } catch (Exception e) {
                logger.warn("S3 업로드 실패, 로컬 URL을 대신 사용합니다: {}", e.getMessage());
                // S3 업로드에 실패하면 로컬 URL 사용
                return urlPrefix + "/" + newFilename;
            }
        }

        // S3 업로드가 비활성화된 경우 로컬 URL 반환
        return urlPrefix + "/" + newFilename;
    }
}