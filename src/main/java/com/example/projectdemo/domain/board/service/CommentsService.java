package com.example.projectdemo.domain.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.projectdemo.domain.board.entity.Comments;
import com.example.projectdemo.domain.board.mapper.CommentsMapper;

import lombok.RequiredArgsConstructor;

//댓글의 추가, 조회, 수정, 삭제 기능을 처리하는 서비스
@Service
@RequiredArgsConstructor
public class CommentsService {

    @Autowired
    private CommentsMapper commentsMapper;

    // 댓글 추가
    public Comments addComments(Comments comments) {
        commentsMapper.insertComment(comments); //댓글 추가
        return commentsMapper.findById(comments.getId()); //저장된 댓글 반환
    }

    // 게시글의 모든 댓글 조회
    public List<Comments> getCommentsByPostId(int postId) {
        return commentsMapper.findByPostId(postId);
    }

    // 특정 댓글 조회
    public Comments getCommentsById(int id) {
        return commentsMapper.findById(id);
    }

    // 댓글 수정
    public Comments updateComments(Comments comments) {
        commentsMapper.updateComments(comments);
        return commentsMapper.findById(comments.getId());
    }

    // 댓글 삭제
    public boolean deleteComment(int id) {
        return commentsMapper.deleteComment(id) > 0;
    }
}