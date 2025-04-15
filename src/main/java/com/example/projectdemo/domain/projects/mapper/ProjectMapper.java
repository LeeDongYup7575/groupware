//package com.example.projectdemo.domain.projects.mapper;
//
//import com.example.projectdemo.domain.projects.dto.ProjectDTO;
//import com.example.projectdemo.domain.projects.dto.ProjectMemberDTO;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Param;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Mapper
//public interface ProjectMapper {
//    List<ProjectDTO> findAllProjects();
//
//    ProjectDTO findProjectById(Integer id);
//
//    List<ProjectDTO> getPublicProjects();
//
//    List<ProjectDTO> getProjectsByDepartment(Integer depId);
//
//    List<ProjectDTO> getProjectsByEmpNum(String empNum);
//
//    List<ProjectMemberDTO> getProjectMembers(Integer projectId);
//
//    int insertProject(ProjectDTO project);
//
//    int updateProject(ProjectDTO project);
//
//    int updateProjectStatus(@Param("id") Integer id, @Param("status") String status);
//
//    // 새로 추가된 메서드: 실제 종료일 업데이트
//    int updateProjectActualEndDate(@Param("id") Integer id, @Param("actualEndDate") LocalDate actualEndDate);
//
//    // 새로 추가된 메서드: 프로젝트 진행률 업데이트
////    int updateProjectProgress(@Param("id") Integer id, @Param("progress") Integer progress);
//
//    int insertProjectMember(ProjectMemberDTO member);
//
//    int updateProjectMemberRole(@Param("projectId") Integer projectId,
//                                @Param("empNum") String empNum,
//                                @Param("role") String role);
//
//    int deleteProjectMember(@Param("projectId") Integer projectId, @Param("empNum") String empNum);
//}

package com.example.projectdemo.domain.projects.mapper;

import com.example.projectdemo.domain.projects.dto.ProjectDTO;
import com.example.projectdemo.domain.projects.dto.ProjectMemberDTO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ProjectMapper {
    /**
     * 모든 프로젝트 조회
     */
    List<ProjectDTO> findAllProjects();

    /**
     * 특정 ID의 프로젝트 조회
     */
    ProjectDTO findProjectById(Integer id);

    /**
     * 공개 프로젝트 목록 조회
     */
    List<ProjectDTO> getPublicProjects();

    /**
     * 특정 부서의 프로젝트 조회
     */
    List<ProjectDTO> getProjectsByDepartment(Integer depId);

    /**
     * 특정 직원이 참여한 프로젝트 조회
     */
    List<ProjectDTO> getProjectsByEmpNum(String empNum);

    /**
     * 특정 프로젝트의 멤버 목록 조회
     */
    List<ProjectMemberDTO> getProjectMembers(Integer projectId);

    /**
     * 프로젝트 등록
     */
    int insertProject(ProjectDTO project);

    /**
     * 프로젝트 수정
     */
    int updateProject(ProjectDTO project);

    /**
     * 프로젝트 상태 변경
     */
    int updateProjectStatus(@Param("id") Integer id, @Param("status") String status);

    /**
     * 프로젝트 실제 종료일 설정
     */
    int updateProjectActualEndDate(@Param("id") Integer id, @Param("actualEndDate") LocalDate actualEndDate);

    /**
     * 프로젝트 멤버 추가
     */
    int insertProjectMember(ProjectMemberDTO member);

    /**
     * 프로젝트 멤버 역할 수정
     */
    int updateProjectMemberRole(@Param("projectId") Integer projectId,
                                @Param("empNum") String empNum,
                                @Param("role") String role);

    /**
     * 프로젝트 멤버 제거
     */
    int deleteProjectMember(@Param("projectId") Integer projectId, @Param("empNum") String empNum);
}