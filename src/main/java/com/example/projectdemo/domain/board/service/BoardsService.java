package com.example.projectdemo.domain.board.service;

import com.example.projectdemo.domain.board.dto.BoardPermissionsDTO;
import com.example.projectdemo.domain.board.dto.BoardsDTO;
import com.example.projectdemo.domain.board.mapper.BoardPermissionsMapper;
import com.example.projectdemo.domain.board.mapper.BoardsMapper;
import com.example.projectdemo.domain.board.mapper.PostsMapper;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BoardsService {

    @Autowired
    private PostsMapper postsMapper; // 게시글 전용 매퍼

    @Autowired
    private BoardsMapper boardsMapper;// 게시판 전용 매퍼

    @Autowired
    private BoardPermissionsMapper boardPermissionsMapper;

    @Autowired
    private EmployeesMapper employeesMapper;


    // 모든 게시판 목록 조회
    public List<BoardsDTO> getAllBoards() {
        return boardsMapper.getAllBoards();
    }

    // ID로 게시판 조회
    public BoardsDTO getBoardById(Integer id) {
        return boardsMapper.getBoardById(id);
    }

    // 사용자에게 접근 권한이 있는 게시판 조회
    public List<BoardsDTO> getAccessibleBoards(Integer empId) {
        // 1. 공개 게시판(is_global=1) 조회
        List<BoardsDTO> globalBoards = boardsMapper.getGlobalBoards();

        // 2. 개인 권한이 있는 게시판 조회
        List<BoardsDTO> permissionBoards = boardsMapper.getBoardsByEmpId(empId);

        // 3. 중복 제거 후 합치기
        Map<Integer, BoardsDTO> boardMap = new HashMap<>();

        for (BoardsDTO board : globalBoards) {
            boardMap.put(board.getId(), board);
        }

        for (BoardsDTO board : permissionBoards) {
            if (!boardMap.containsKey(board.getId())) {
                boardMap.put(board.getId(), board);
            }
        }

        return new ArrayList<>(boardMap.values());
    }

    // 게시판 접근 권한 확인
    public boolean hasAccess(Integer empId, Integer boardId) {
        BoardsDTO board = getBoardById(boardId);

        // 공개 게시판은 모두 접근 가능
        if (board.isGlobal()) {
            return true;
        }

        // 개인 권한이 있는 게시판인지 확인
        List<BoardsDTO> permissionBoards = boardsMapper.getBoardsByEmpId(empId);
        return permissionBoards.stream()
                .anyMatch(b -> b.getId().equals(boardId));
    }

    // 게시판 관리 권한 확인 (쓰기 권한이 있는지)
    public boolean hasManagePermission(Integer empId, Integer boardId) {
        return boardPermissionsMapper.hasManagePermission(boardId, empId);
    }

    // 관리할 수 있는 게시판 목록 조회
    public List<BoardsDTO> getManageableBoards(Integer empId) {
        return boardsMapper.getManageableBoardsByEmpId(empId);
    }

    // 게시판 생성 메소드
    public void createBoard(BoardsDTO boardsDto, Integer currentEmployeeId, List<Integer> memberIds) {
        // 1. 게시판 생성
        boardsMapper.insertBoard(boardsDto);

        // 2. 그룹 게시판인 경우 권한 추가
        if (!boardsDto.isGlobal()) {
            // 생성자를 자동으로 멤버로 추가
            BoardPermissionsDTO creatorPermission = new BoardPermissionsDTO();
            creatorPermission.setBoardId(boardsDto.getId());
            creatorPermission.setEmpId(currentEmployeeId);
            creatorPermission.setPermissionType("멤버"); // 단일 멤버십 타입으로 통일
            creatorPermission.setCreatedAt(LocalDateTime.now());
            boardPermissionsMapper.insertPermission(creatorPermission);

            // 선택된 멤버들 추가
            if (memberIds != null && !memberIds.isEmpty()) {
                for (Integer memberId : memberIds) {
                    BoardPermissionsDTO memberPermission = new BoardPermissionsDTO();
                    memberPermission.setBoardId(boardsDto.getId());
                    memberPermission.setEmpId(memberId);
                    memberPermission.setPermissionType("멤버"); // 단일 멤버십 타입으로 통일
                    memberPermission.setCreatedAt(LocalDateTime.now());
                    boardPermissionsMapper.insertPermission(memberPermission);
                }
            }
        }
    }

    // 게시판 접근 권한 체크 메소드
    public boolean hasAccessToBoard(Integer boardId, Integer empId) {
        // 글로벌 게시판은 모든 사용자 접근 가능
        if (boardsMapper.isGlobalBoard(boardId)) {
            return true;
        }

        // 그룹 게시판은 권한 테이블 체크
        return boardPermissionsMapper.hasManagePermission(boardId, empId);
    }

    // 게시판 업데이트
    @Transactional
    public BoardsDTO updateBoard(Integer boardId, BoardsDTO requestDTO) {
        // 기존 게시판 정보 확인
        BoardsDTO existingBoard = boardsMapper.getBoardById(boardId);
        if (existingBoard == null) {
            throw new IllegalArgumentException("존재하지 않는 게시판입니다.");
        }

        // 이름 변경 시 중복 체크
        if (!existingBoard.getName().equals(requestDTO.getName()) &&
                boardsMapper.existsByName(requestDTO.getName())) {
            throw new IllegalArgumentException("이미 같은 이름의 게시판이 존재합니다.");
        }

        // 게시판 정보 업데이트
        BoardsDTO updatedBoard = BoardsDTO.builder()
                .id(boardId)
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .isGlobal(requestDTO.isGlobal())
                .isActive(requestDTO.isActive())
                .sortOrder(requestDTO.getSortOrder())
                .build();

        boardsMapper.updateBoard(updatedBoard);

        // 그룹 게시판인 경우 권한 업데이트
        if (!requestDTO.isGlobal()) {
            // 기존 권한 모두 삭제 (소유자 권한 제외)
            List<BoardPermissionsDTO> existingPermissions = boardPermissionsMapper.getPermissionsByBoardId(boardId);
            for (BoardPermissionsDTO permission : existingPermissions) {
                if (!"쓰기".equals(permission.getPermissionType())) {
                    boardPermissionsMapper.deletePermission(boardId, permission.getEmpId());
                }
            }

            // 새로운 권한 추가
            if (requestDTO.getMemberIds() != null && requestDTO.getPermissions() != null) {
                int size = Math.min(requestDTO.getMemberIds().size(), requestDTO.getPermissions().size());

                List<BoardPermissionsDTO> permissions = new ArrayList<>();

                for (int i = 0; i < size; i++) {
                    Integer memberId = requestDTO.getMemberIds().get(i);
                    String permission = requestDTO.getPermissions().get(i);

                    // 기존 소유자와 중복되지 않도록 체크
                    boolean isOwner = false;
                    for (BoardPermissionsDTO existingPerm : existingPermissions) {
                        if (existingPerm.getEmpId().equals(memberId) && "쓰기".equals(existingPerm.getPermissionType())) {
                            isOwner = true;
                            break;
                        }
                    }

                    if (!isOwner) {
                        permissions.add(BoardPermissionsDTO.builder()
                                .boardId(boardId)
                                .empId(memberId)
                                .permissionType(permission)
                                .createdAt(LocalDateTime.now())
                                .build());
                    }
                }

                if (!permissions.isEmpty()) {
                    boardPermissionsMapper.batchInsertPermissions(permissions);
                }
            }
        }

        return updatedBoard;
    }

    // 게시판 삭제 (비활성화)
    @Transactional
    public void deleteBoard(Integer boardId) {
        boardsMapper.deleteBoard(boardId);
    }
}
