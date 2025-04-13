package com.example.projectdemo.domain.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.projectdemo.domain.board.entity.Comments;

//댓글(Comments) 데이터를 데이터베이스와 연동하기 위한 MyBatis 매퍼 인터페이스
@Mapper
public interface CommentsMapper {

    // 댓글 추가
    int insertComment(Comments comment);

    // 게시글의 모든 댓글 조회
    List<Comments> findByPostId(int postId);

    // 특정 댓글 조회
    Comments findById(int id);

    // 댓글 수정
    int updateComments(Comments comment);

    // 댓글 삭제 (논리적 삭제 - is_deleted 플래그 사용)
    int deleteComment(int id);
    
}
