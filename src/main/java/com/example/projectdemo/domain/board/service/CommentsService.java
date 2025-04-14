package com.example.projectdemo.domain.board.service;

import com.example.projectdemo.domain.board.entity.Comments;
import com.example.projectdemo.domain.board.mapper.CommentsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    // 게시글의 모든 댓글을 계층 구조(대댓글 포함)로 조회하는 메서드
    public List<Comments> getHierarchicalCommentsByPostId(int postId) {

        // 1. 해당 게시글의 모든 댓글을 DB에서 한 번에 가져온다.
        List<Comments> allComments = commentsMapper.findByPostId(postId);

        // 2. 그중에서 '부모가 없는 댓글'만 골라낸다 (즉, 최상위 댓글만).
        List<Comments> rootComments = allComments.stream()
                .filter(comment -> comment.getParentId() == null)
                .collect(Collectors.toList());

        // 3. 댓글을 빠르게 찾기 위해 id를 key로 하는 Map을 만든다.
        Map<Integer, Comments> commentMap = new HashMap<>();
        allComments.forEach(comment -> commentMap.put(comment.getId(), comment));

        // 4. 모든 댓글을 순회하면서, 대댓글이면 부모 댓글의 replies 리스트에 추가한다.
        allComments.forEach(comment -> {
            if (comment.getParentId() != null) { // 대댓글인 경우
                Comments parent = commentMap.get(comment.getParentId()); // 부모 댓글 찾기
                if (parent != null) {
                    if (parent.getReplies() == null) {
                        parent.setReplies(new ArrayList<>()); // 대댓글 리스트가 없으면 생성
                    }
                    parent.getReplies().add(comment); // 부모 댓글에 대댓글 추가
                }
            }
        });

        // 5. 최상위 댓글들을 반환 (각 댓글 안에 대댓글들이 계층 구조로 들어 있음)
        return rootComments;
    }

    // 댓글 수정
    public Comments updateComments(Comments comments) {
        commentsMapper.updateComments(comments);
        return commentsMapper.findById(comments.getId());
    }

    // 댓글 삭제
    public boolean deleteComment(int id) {
        // 이 댓글의 모든 대댓글 찾기
        List<Comments> replies = commentsMapper.findByParentId(id);

        // 대댓글이 있으면 모두 삭제 처리
        for (Comments reply : replies) {
            commentsMapper.deleteComment(reply.getId());
        }

        // 원 댓글 삭제
        return commentsMapper.deleteComment(id) > 0;
    }


}