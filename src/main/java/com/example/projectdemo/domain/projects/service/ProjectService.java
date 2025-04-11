package com.example.projectdemo.domain.projects.service;

import com.example.projectdemo.domain.projects.dto.ProjectDTO;
import com.example.projectdemo.domain.projects.dto.ProjectMemberDTO;

import java.util.List;

public interface ProjectService {

    /**
     * 모든 프로젝트 목록 조회
     */
    List<ProjectDTO> getAllProjects();

    /**
     * 특정 프로젝트 조회
     */
    ProjectDTO getProjectById(Integer id);

    /**
     * 직원이 참여 중인 프로젝트 목록 조회
     */
    List<ProjectDTO> getProjectsByEmployee(String empNum);

    /**
     * 직원이 접근 가능한 프로젝트 목록 조회 (관찰자 포함)
     */
    List<ProjectDTO> getAccessibleProjects(String empNum);

    /**
     * 부서별 프로젝트 목록 조회
     */
    List<ProjectDTO> getProjectsByDepartment(Integer depId);

    /**
     * 신규 프로젝트 등록
     */
    ProjectDTO createProject(ProjectDTO project);

    /**
     * 프로젝트 정보 업데이트
     */
    ProjectDTO updateProject(ProjectDTO project);

    /**
     * 프로젝트 상태 업데이트
     */
    ProjectDTO updateProjectStatus(Integer id, String status);

    /**
     * 프로젝트 종료 처리
     */
    ProjectDTO completeProject(Integer id);

    /**
     * 프로젝트에 멤버 추가
     */
    void addProjectMember(Integer projectId, String empNum, String role);

    /**
     * 프로젝트 멤버 역할 변경
     */
    void updateProjectMemberRole(Integer projectId, String empNum, String role);

    /**
     * 프로젝트에서 멤버 제거
     */
    void removeProjectMember(Integer projectId, String empNum);

    /**
     * 프로젝트 멤버 목록 조회
     */
    List<ProjectMemberDTO> getProjectMembers(Integer projectId);
}