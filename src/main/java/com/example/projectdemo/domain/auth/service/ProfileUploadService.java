package com.example.projectdemo.domain.auth.service;

import com.example.projectdemo.domain.s3.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@Service
public class ProfileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileUploadService.class);

    @Autowired
    private S3Service s3Service;

    /**
     * 프로필 이미지를 S3에 업로드하고 URL을 반환합니다.
     *
     * @param profileImage 업로드할 프로필 이미지
     * @param requestId 고유 요청 ID
     * @return S3에 업로드된 이미지 URL
     * @throws IOException 파일 업로드 중 오류 발생 시
     */
    public String uploadProfileImageWithRequestId(MultipartFile profileImage, String requestId) throws IOException {
        if (profileImage == null || profileImage.isEmpty()) {
            throw new IOException("업로드할 프로필 이미지가 없습니다.");
        }

        // 이미지 파일 검증
        validateImageFile(profileImage);

        // S3에 업로드
        return s3Service.uploadProfileImage(profileImage, requestId);
    }

    /**
     * 프로필 이미지 파일을 검증합니다.
     *
     * @param file 검증할 파일
     * @throws IOException 유효하지 않은 파일인 경우
     */
    private void validateImageFile(MultipartFile file) throws IOException {
        // 파일 크기 검증 (10MB 이하)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IOException("파일 크기는 10MB 이하여야 합니다.");
        }

        // 파일 타입 검증
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("이미지 파일만 업로드 가능합니다.");
        }

        // 파일 확장자 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            if (!Arrays.asList(".jpg", ".jpeg", ".png", ".gif").contains(extension)) {
                throw new IOException("지원하지 않는 파일 형식입니다. JPG, PNG, GIF 파일만 업로드 가능합니다.");
            }
        }
    }

    /**
     * S3에서 프로필 이미지를 삭제합니다.
     *
     * @param imageUrl 삭제할 이미지 URL
     */
    public void deleteProfileImage(String imageUrl) {
        // 기본 이미지는 삭제하지 않음
        if (imageUrl == null || imageUrl.contains("default-profile")) {
            return;
        }

        try {
            s3Service.deleteFile(imageUrl);
            logger.info("프로필 이미지 삭제 완료: {}", imageUrl);
        } catch (Exception e) {
            logger.error("프로필 이미지 삭제 중 오류: {}", e.getMessage());
        }
    }
}