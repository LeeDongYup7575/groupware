package com.example.projectdemo.domain.board.mapper;

import com.example.projectdemo.domain.board.dto.PostsDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostsMapper {
    /**
     * 새 게시글을 데이터베이스에 저장합니다.
     *
     * @param postsDTO 저장할 게시글 정보
     * @return 영향받은 행 수
     */
    void insertPost(PostsDTO postsDTO);

    // 사용자가 접근 가능한 모든 게시판의 게시글 조회
    List<PostsDTO> getAccessiblePostsByEmpId(int empId);

    // 특정 게시판의 게시글 조회
    List<PostsDTO> getPostsByBoardId(Integer boardId);

    // 게시글 상세 조회
    PostsDTO getPostById(Integer id);

    // 게시글 수정 메서드 추가
    int updatePost(PostsDTO post);


}
