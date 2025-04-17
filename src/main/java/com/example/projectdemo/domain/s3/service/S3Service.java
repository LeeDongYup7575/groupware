package com.example.projectdemo.domain.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Autowired
    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        // Generate a unique file name to prevent conflicts
        String fileName = generateUniqueFileName(file);

        // Set metadata
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // Upload to S3 without ACL
        try {
            amazonS3.putObject(new PutObjectRequest(
                    bucketName,
                    "profiles/" + fileName,
                    file.getInputStream(),
                    metadata
            ));

            logger.info("File uploaded successfully to S3: {}", fileName);

            // Return direct S3 URL
            return amazonS3.getUrl(bucketName, "profiles/" + fileName).toString();

        } catch (Exception e) {
            logger.error("Error uploading file to S3: {}", e.getMessage());
            throw new IOException("Failed to upload file to S3", e);
        }
    }

    private String generateUniqueFileName(MultipartFile file) {
        // Extract file extension
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // Generate UUID + timestamp to ensure uniqueness
        return UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + extension;
    }

    // Optional: Add a method to delete files if needed
    public void deleteFile(String fileUrl) {
        try {
            // Extract key from URL
            String key = fileUrl;
            if (fileUrl.contains(bucketName)) {
                key = fileUrl.substring(fileUrl.indexOf(bucketName) + bucketName.length() + 1);
            }

            amazonS3.deleteObject(bucketName, key);
            logger.info("File deleted successfully from S3: {}", key);
        } catch (Exception e) {
            logger.error("Error deleting file from S3: {}", e.getMessage());
        }
    }

    /**
     * 요청 ID를 포함한 고유한 파일명으로 S3에 파일을 업로드합니다.
     */
    public String uploadFileWithRequestId(MultipartFile file, String requestId) throws IOException {
        // 파일 확장자 추출
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // 요청 ID를 포함한 고유한 파일명 생성
        String fileName = "profiles/" + requestId + extension;

        // 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // S3에 업로드
        try {
            // 동일한 키가 이미 존재하는지 확인
            if (amazonS3.doesObjectExist(bucketName, fileName)) {
                logger.info("동일한 키의 파일이 이미 S3에 존재합니다: {}", fileName);
                // 이미 존재하는 경우 URL만 반환
                return amazonS3.getUrl(bucketName, fileName).toString();
            }

            // 업로드 진행
            amazonS3.putObject(new PutObjectRequest(
                    bucketName,
                    fileName,
                    file.getInputStream(),
                    metadata
            ));

            logger.info("파일이 S3에 성공적으로 업로드되었습니다: {}", fileName);

            // URL 반환
            return amazonS3.getUrl(bucketName, fileName).toString();

        } catch (Exception e) {
            logger.error("S3 업로드 중 오류 발생: {}", e.getMessage());
            throw new IOException("S3 업로드 실패", e);
        }
    }

    /**
     * S3에 해당 키의 객체가 존재하는지 확인합니다.
     */
    public boolean doesObjectExist(String key) {
        return amazonS3.doesObjectExist(bucketName, key);
    }

    /**
     * S3 객체의 URL을 반환합니다.
     */
    public String getObjectUrl(String key) {
        return amazonS3.getUrl(bucketName, key).toString();
    }
}