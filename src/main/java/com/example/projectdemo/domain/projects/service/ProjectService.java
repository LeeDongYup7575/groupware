package com.example.projectdemo.domain.projects.service;

import com.example.projectdemo.domain.projects.dto.ProjectDTO;
import com.example.projectdemo.domain.projects.dto.ProjectMemberDTO;
import com.example.projectdemo.domain.projects.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    public List<ProjectDTO> getAllProjects(){
        return projectMapper.findAllProjects();
    }

    public ProjectDTO getProjectById(Integer id){
        return projectMapper.findProjectById(id);
    }

    public List<ProjectDTO> getProjectsByEmployee(String empNum){
        return projectMapper.getProjectsByEmpNum(empNum);
    }

    public List<ProjectDTO> getAccessibleProjects(String empNum){
        return projectMapper.getPublicProjects();
    }

    public List<ProjectDTO> getProjectsByDepartment(Integer depId){
        return projectMapper.getProjectsByDepartment(depId);
    }

    @Transactional
    public ProjectDTO createProject(ProjectDTO project) {
        // 프로젝트 등록
        if(projectMapper.insertProject(project) > 0) {
            // 현재 사용자(매니저)를 프로젝트 멤버로 자동 추가
            ProjectMemberDTO member = new ProjectMemberDTO();
            member.setProjectId(project.getId());
            member.setEmpNum(project.getManagerEmpNum());
            member.setRole("매니저");
            member.setJoinedAt(LocalDateTime.now());

            projectMapper.insertProjectMember(member);

            return project;
        }
        throw new RuntimeException("프로젝트를 등록할 수 없습니다.");
    }

    @Transactional
    public ProjectDTO updateProject(ProjectDTO project){
        if(projectMapper.updateProject(project) > 0){
            return project;
        }
        throw new RuntimeException("프로젝트를 수정할 수 없습니다.");
    }

    @Transactional
    public ProjectDTO updateProjectStatus(Integer id, String status){
        if(projectMapper.updateProjectStatus(id, status) > 0){
            return projectMapper.findProjectById(id);
        }
        throw new RuntimeException("프로젝트 상태 수정 실패");
    }

    @Transactional
    public ProjectDTO completeProject(Integer id){
        if(projectMapper.updateProjectStatus(id, "complete") > 0){
            return projectMapper.findProjectById(id);
        }
        throw new RuntimeException("프로젝트 완료 처리 실패");
    }

    @Transactional
    public void addProjectMember(Integer projectId, String empNum, String role){
        ProjectMemberDTO member = new ProjectMemberDTO();
        member.setProjectId(projectId);
        member.setEmpNum(empNum);
        member.setRole(role);
        member.setJoinedAt(LocalDateTime.now()); // 필요 시 설정

        if (projectMapper.insertProjectMember(member) <= 0){
            throw new RuntimeException("프로젝트 멤버 추가 실패");
        }
    }

    @Transactional
    public void updateProjectMemberRole(Integer projectId, String empNum, String role){
        if (projectMapper.updateProjectMemberRole(projectId, empNum, role) <= 0){
            throw new RuntimeException("프로젝트 멤버 역할 수정 실패");
        }
    }

    @Transactional
    public void removeProjectMember(Integer projectId, String empNum){
        if (projectMapper.deleteProjectMember(projectId, empNum) <= 0){
            throw new RuntimeException("프로젝트 멤버 제거 실패");
        }
    }

    public List<ProjectMemberDTO> getProjectMembers(Integer projectId){
        return projectMapper.getProjectMembers(projectId);
    }
}
