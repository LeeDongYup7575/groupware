package com.example.projectdemo.domain.s3.service;

import com.amazonaws.services.s3.AmazonS3;
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

    /**
     * 파일 타입별 S3 업로드를 처리합니다.
     *
     * @param file 업로드할 파일
     * @param requestId 고유 요청 ID
     * @param fileType 파일 타입 경로 (profiles, boards, edsm 등)
     * @return S3 URL
     */
    public String uploadFileByType(MultipartFile file, String requestId, String fileType) throws IOException {
        // 파일 확장자 추출
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        // 파일 타입에 따른 경로 생성
        String fileName = fileType + "/" + requestId + extension;

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
     * S3 객체에 대한 프리 사인드 URL을 생성합니다.
     *
     * @param objectKey S3 객체 키
     * @param expirationMinutes URL 유효 시간(분)
     * @return 프리 사인드 URL
     */
    public String generatePresignedUrl(String objectKey, int expirationMinutes) {
        try {
            // 전체 URL에서 객체 키 추출
            if (objectKey.startsWith("http")) {
                String urlPath = new java.net.URL(objectKey).getPath();
                // URL 경로에서 버킷 이름 이후의 부분을 객체 키로 사용
                if (urlPath.startsWith("/")) {
                    urlPath = urlPath.substring(1); // 맨 앞의 '/' 제거
                }

                // 버킷 이름이 URL 경로에 있으면 그 이후 부분만 추출
                if (urlPath.startsWith(bucketName + "/")) {
                    objectKey = urlPath.substring(bucketName.length() + 1);
                } else {
                    objectKey = urlPath;
                }

                logger.info("Extracted S3 object key: {}", objectKey);
            }

            // 만료 시간 설정
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * expirationMinutes; // 분 단위 변환
            expiration.setTime(expTimeMillis);

            // 프리 사인드 URL 생성
            java.net.URL presignedUrl = amazonS3.generatePresignedUrl(bucketName, objectKey, expiration);
            logger.info("Generated presigned URL: {}", presignedUrl);
            return presignedUrl.toString();
        } catch (Exception e) {
            logger.error("Error generating presigned URL: {}", e.getMessage());
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    /**
     * 기존 메서드 - 프로필 이미지 업로드 (기본 폴더 사용)
     */
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
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        return UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + extension;
    }

    /**
     * 버킷 이름을 반환합니다.
     */
    public String getBucketName() {
        return this.bucketName;
    }

    /**
     * AmazonS3 클라이언트를 반환합니다.
     */
    public AmazonS3 getAmazonS3() {
        return this.amazonS3;
    }

    /**
     * 파일 삭제 처리
     */
    public void deleteFile(String fileUrl) {
        try {
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
     * 기존 메서드 (uploadFileWithRequestId) - 프로필 이미지용
     */
    public String uploadFileWithRequestId(MultipartFile file, String requestId) throws IOException {
        return uploadProfileImage(file, requestId);
    }

    /**
     * 프로필 이미지 업로드 (새 메서드)
     */
    public String uploadProfileImage(MultipartFile file, String requestId) throws IOException {
        return uploadFileByType(file, requestId, "profiles");
    }

    /**
     * 게시판 첨부파일 업로드
     */
    public String uploadBoardAttachment(MultipartFile file, String requestId) throws IOException {
        return uploadFileByType(file, requestId, "boards");
    }

    /**
     * 전자결재 문서 파일 업로드
     */
    public String uploadEdsmFile(MultipartFile file, String requestId) throws IOException {
        return uploadFileByType(file, requestId, "edsm");
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