package com.example.projectdemo.domain.board.mapper;

import com.example.projectdemo.domain.board.dto.PostsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    // 특정 게시판 게시글 중 최신순으로 4개만 가져오는 쿼리
    List<PostsDTO> findTop4ByBoardId(Integer boardId);

    // 게시판 ID로 게시글 수 조회
    int countPostsByBoardId(Integer boardId);

    // 게시글 상세 조회
    PostsDTO getPostById(Integer id);

    // 게시글 수정 메서드 추가
    int updatePost(PostsDTO post);

    // 하드 삭제 (실제 데이터베이스에서 삭제)
    int deletePost(int id);

    // 내가 쓴 글 조회
    List<PostsDTO> findPostsByEmpId(@Param("empId") Integer empId);

    // 게시글 다중 삭제
    void deletePostsByIds(@Param("list") List<Integer> ids);

    // 조회수 증가 메소드 추가
    void incrementViewCount(@Param("postId") int postId);

}
