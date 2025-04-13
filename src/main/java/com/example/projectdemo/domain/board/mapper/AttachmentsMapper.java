package com.example.projectdemo.domain.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.projectdemo.domain.board.dto.AttachmentsDTO;

/**
 * 첨부 파일 관련 데이터베이스 작업을 위한 Mapper 인터페이스
 */
@Mapper
public interface AttachmentsMapper {

    /**
     * 첨부 파일 정보를 데이터베이스에 저장합니다.
     *
     * @param attachment 저장할 첨부 파일 정보
     * @return 영향받은 행 수
     */
    int insertAttachment(AttachmentsDTO attachment);

    /**
     * ID로 특정 첨부 파일을 조회합니다.
     *
     * @param id 첨부 파일 ID
     * @return 첨부 파일 정보
     */
    AttachmentsDTO findAttachmentById(int id);

    /**
     * 특정 게시글의 모든 첨부 파일을 삭제합니다.
     *
     * @param postId 게시글 ID
     * @return 영향받은 행 수
     */
    int deleteAttachmentsByPostId(int postId);

    /**
     * 특정 첨부 파일을 삭제합니다.
     *
     * @param id 첨부 파일 ID
     * @return 영향받은 행 수
     */
    int deleteAttachment(int id);

    /**
     * 게시글 ID로 첨부파일 목록을 조회합니다.
     *
     * @param postId 게시글 ID
     * @return 첨부파일 DTO 목록
     */
    List<AttachmentsDTO> selectAttachmentsByPostId(int postId);
}