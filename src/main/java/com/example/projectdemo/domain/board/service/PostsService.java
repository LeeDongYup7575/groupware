package com.example.projectdemo.domain.board.service;

import com.example.projectdemo.domain.board.dto.PostsDTO;
import com.example.projectdemo.domain.board.mapper.PostsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    // 게시글 목록 조회
    public List<PostsDTO> getAllPosts() {
        return postsMapper.getAllPosts();
    }

    // 게시글 상세 조회
    public PostsDTO getPostById(int id) {
        PostsDTO post = postsMapper.getPostById(id);
        if (post == null) {
            throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id);
        }
        return post;
    }

}
