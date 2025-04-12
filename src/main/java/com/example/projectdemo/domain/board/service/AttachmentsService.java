package com.example.projectdemo.domain.board.service;

import com.example.projectdemo.domain.board.dto.AttachmentsDTO;
import com.example.projectdemo.domain.board.mapper.AttachmentsMapper;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
    private HttpSession session;

    /**
     * 다중 파일을 업로드하고 저장합니다.
     *
     * @param files 업로드할 파일 목록
     * @param postId 연결할 게시글 ID
     * @return 저장된 첨부 파일 목록
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    public List<AttachmentsDTO> uploadFiles(List<MultipartFile> files, int postId) throws IOException {
        List<AttachmentsDTO> attachments = new ArrayList<>();

        // 실제 경로 가져오기
        String realPath = session.getServletContext().getRealPath("uploads/attachments");

        // 경로 로그 출력
        logger.info("첨부파일 업로드 경로: {}", realPath);

        // 디렉토리가 없으면 생성
        File saveDir = new File(realPath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        // 각 파일 저장
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            // 원본 파일명과 확장자 추출
            String originalFilename = file.getOriginalFilename();

            // 고유한 파일명 생성
            String newFilename = UUID.randomUUID().toString();
            if (originalFilename != null && originalFilename.contains(".")) {
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                newFilename += fileExtension;
            }

            // 파일 저장 경로
            File targetFile = new File(realPath, newFilename);

            // 파일 저장
            file.transferTo(targetFile);

            // 첨부 파일 정보 생성
            AttachmentsDTO attachment = AttachmentsDTO.builder()
                    .postId(postId)
                    .originName(originalFilename)
                    .sysName(newFilename) // 시스템 파일명만 저장
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
     * 첨부 파일을 리소스로 로드
     *
     * @param attachment 첨부 파일 정보
     * @return 파일 리소스
     * @throws IOException 파일 로드 중 오류 발생 시
     */
    public Resource loadFileAsResource(AttachmentsDTO attachment) throws IOException {
        try {
            // 실제 경로 가져오기
            String realPath = session.getServletContext().getRealPath("uploads/attachments");
            File file = new File(realPath, attachment.getSysName());

            // FileSystemResource를 사용하여 파일을 Resource 객체로 변환
            Resource resource = new FileSystemResource(file);

            if (resource.exists()) {
                return resource;
            } else {
                throw new IOException("파일을 찾을 수 없습니다: " + attachment.getOriginName());
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
            // 실제 경로 가져오기
            String realPath = session.getServletContext().getRealPath("uploads/attachments");
            File file = new File(realPath, attachment.getSysName());

            // 파일 삭제
            boolean fileDeleted = file.delete();

            // DB에서 정보 삭제
            int result = attachmentsMapper.deleteAttachment(id);

            return result > 0;
        } catch (Exception e) {
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

        // 실제 경로 가져오기
        String realPath = session.getServletContext().getRealPath("uploads/attachments");

        for (AttachmentsDTO attachment : attachments) {
            try {
                // 파일 삭제
                File file = new File(realPath, attachment.getSysName());
                if (file.delete()) {
                    deletedCount++;
                }
            } catch (Exception e) {
                logger.error("파일 삭제 중 오류: id={}, {}", attachment.getId(), e.getMessage());
            }
        }

        // DB에서 정보 삭제
        int result = attachmentsMapper.deleteAttachmentsByPostId(postId);

        return result;
    }

}
