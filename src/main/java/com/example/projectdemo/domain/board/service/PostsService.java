package com.example.projectdemo.domain.board.service;

import com.example.projectdemo.domain.board.dto.PostsDTO;
import com.example.projectdemo.domain.board.mapper.PostsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostsService {

    @Autowired
    private PostsMapper postsMapper;

    // 게시글 작성
    @Transactional
    public PostsDTO createPost(int empId, PostsDTO postsDTO) {
        // 게시글 작성자 설정
        postsDTO.setEmpId(empId);

        // 현재 시간으로 작성일시 설정
        postsDTO.setCreatedAt(LocalDateTime.now());

        // 조회수 초기화
        postsDTO.setViews(0);

        // 게시글 저장
        postsMapper.insertPost(postsDTO);

        // 저장된 게시글 반환
        return postsDTO;
    }

    // 통합 게시판 - 사용자가 접근 가능한 모든 게시판의 게시글 조회
    public List<PostsDTO> getAccessiblePosts(Integer empId) {
        return postsMapper.getAccessiblePostsByEmpId(empId);
    }

    // 특정 게시판의 게시글 목록 조회
    public List<PostsDTO> getPostsByBoardId(Integer boardId) {
        return postsMapper.getPostsByBoardId(boardId);
    }

    // 게시글 상세 조회
    public PostsDTO getPostById(int id) {
        PostsDTO post = postsMapper.getPostById(id);
        if (post == null) {
            throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id);
        }
        return post;
    }

    // 게시글 수정
    public boolean updatePost(PostsDTO post) {
        // 게시글 존재 여부 확인
        PostsDTO existingPost = getPostById(post.getId());
        if (existingPost == null) {
            return false;
        }

        // 수정 작업 수행
        int result = postsMapper.updatePost(post);
        return result > 0;
    }

    // 게시글 수정 권한 확인
    public boolean canModifyPost(int empId, int postId) {
        PostsDTO post = getPostById(postId);
        // 작성자만 수정 가능 또는 관리자 권한 확인
        return post != null && post.getEmpId() == empId;
    }

    // 게시글 삭제 메서드
    public int deletePost(int id) {
        // 실제 삭제(하드 삭제)
        return postsMapper.deletePost(id);
    }



}
