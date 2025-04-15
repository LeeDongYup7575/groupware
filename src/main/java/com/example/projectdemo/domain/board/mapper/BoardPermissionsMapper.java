package com.example.projectdemo.domain.board.mapper;

import com.example.projectdemo.domain.board.dto.BoardPermissionsDTO;
import com.example.projectdemo.domain.board.dto.BoardsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 게시판과 관련된 권한 정보를 조회·추가·수정·삭제하는 MyBatis 매퍼 인터페이스
@Mapper
public interface BoardPermissionsMapper {

    // 게시판의 모든 권한 목록 조회
    List<BoardPermissionsDTO> getPermissionsByBoardId(Integer boardId);

    // 특정 사용자와 게시판의 권한 조회
    BoardsDTO getPermissionByBoardAndEmp(@Param("boardId") Integer boardId, @Param("empId") Integer empId);

    // 권한 추가
    void insertPermission(BoardPermissionsDTO permission);

    // 권한 업데이트
    void updatePermission(BoardsDTO permission);

    // 권한 삭제
    void deletePermission(@Param("boardId") Integer boardId, @Param("empId") Integer empId);

    // 게시판의 모든 권한 삭제
    void deleteAllPermissionsByBoardId(Integer boardId);

    // 사용자의 관리 권한이 있는 게시판 ID 목록 조회
    List<Integer> getManageableBoardIdsByEmpId(Integer empId);

    // 특정 게시판에서 사용자가 관리 권한(쓰기)이 있는지 확인
    boolean hasManagePermission(@Param("boardId") Integer boardId, @Param("empId") Integer empId);

    // 권한 일괄 추가 (다중 사용자 권한 추가 시)
    void batchInsertPermissions(List<BoardPermissionsDTO> permissions);
}
