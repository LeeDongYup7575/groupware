package com.example.projectdemo.domain.board.mapper;

import com.example.projectdemo.domain.board.dto.BoardsDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

// BoardMapper.java
@Mapper
public interface BoardsMapper {

    // 모든 게시판 목록 조회
    List<BoardsDTO> getAllBoards();

    // ID로 게시판 조회
    BoardsDTO getBoardById(Integer id);

    // 사용자에게 접근 권한이 있는 게시판 조회 (board_permissions 테이블 활용)
    List<BoardsDTO> getBoardsByEmpId(Integer empId);

    // 공개 게시판 조회 (is_global = 1)
    List<BoardsDTO> getGlobalBoards();

    // 새 게시판 생성
    void insertBoard(BoardsDTO board);

    // 게시판 정보 업데이트
    void updateBoard(BoardsDTO board);

    // 게시판 삭제 (실제로는 is_active = 0으로 설정)
    int deleteBoard(Integer id);

    // 게시판 정렬 순서 업데이트
    void updateBoardSortOrder(Integer id, Integer sortOrder);

    // 가장 높은 정렬 순서 값 조회
    Integer getMaxSortOrder();

    // 특정 이름의 게시판이 이미 존재하는지 체크
    boolean existsByName(String name);

    // 관리 권한이 있는 게시판 목록 조회
    List<BoardsDTO> getManageableBoardsByEmpId(Integer empId);

    // 해당 게시판이 공개(전체) 게시판인지 여부를 반환
    boolean isGlobalBoard(Integer boardId);
}

