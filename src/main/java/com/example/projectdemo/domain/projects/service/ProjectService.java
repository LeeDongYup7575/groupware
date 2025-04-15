package com.example.projectdemo.domain.projects.service;

import com.example.projectdemo.domain.projects.dto.ProjectDTO;
import com.example.projectdemo.domain.projects.dto.ProjectMemberDTO;
import com.example.projectdemo.domain.projects.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private TaskService taskService;

    public List<ProjectDTO> getAllProjects(){
        return projectMapper.findAllProjects();
    }

    public ProjectDTO getProjectById(Integer id){
        return projectMapper.findProjectById(id);
    }

    public List<ProjectDTO> getAccessibleProjects(String empNum){
        return projectMapper.getPublicProjects();
    }

    public List<ProjectDTO> getProjectsByDepartment(Integer depId){
        return projectMapper.getProjectsByDepartment(depId);
    }

    public List<ProjectDTO> getProjectsByEmployee(String empNum){
        List<ProjectDTO> projects = projectMapper.getProjectsByEmpNum(empNum);

        // 각 프로젝트에 대한 추가 정보 설정
        projects.forEach(this::enrichProjectData);

        return projects;
    }

    /**
     * 프로젝트에 추가 정보를 설정합니다.
     */
    private void enrichProjectData(ProjectDTO project) {
        // 프로젝트 멤버 이름 목록 조회
        List<ProjectMemberDTO> members = projectMapper.getProjectMembers(project.getId());
        if (members != null && !members.isEmpty()) {
            List<String> memberNames = members.stream()
                    .map(ProjectMemberDTO::getName) // getEmpNum -> getName으로 변경
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            project.setMemberNames(memberNames);
        }
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

    /**
     * 프로젝트의 완료 상태를 확인합니다.
     * @param projectId 확인할 프로젝트 ID
     * @return 프로젝트가 완료 상태이면 true, 그렇지 않으면 false
     * @throws RuntimeException 프로젝트가 존재하지 않을 경우
     */
    public boolean isProjectCompleted(Integer projectId) {
        ProjectDTO project = projectMapper.findProjectById(projectId);
        if (project == null) {
            throw new RuntimeException("프로젝트를 찾을 수 없습니다: " + projectId);
        }
        return "완료".equals(project.getStatus());
    }

    /**
     * 프로젝트 완료 처리 - 완료 상태 확인 로직 추가
     */
    @Transactional
    public ProjectDTO completeProject(Integer id) {
        // 이미 완료된 프로젝트인지 확인
        if (isProjectCompleted(id)) {
            return projectMapper.findProjectById(id);
        }

        // 완료 처리
        if (projectMapper.updateProjectStatus(id, "완료") > 0) {
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

    /**
     * 프로젝트 상태 업데이트 - 완료 상태 확인 로직 추가
     */
    @Transactional
    public ProjectDTO updateProjectStatus(Integer id, String status) {
        // 이미 완료된 프로젝트의 상태를 변경하려는 경우
        if (isProjectCompleted(id) && !"완료".equals(status)) {
            throw new RuntimeException("이미 완료된 프로젝트의 상태는 변경할 수 없습니다.");
        }

        if (projectMapper.updateProjectStatus(id, status) > 0) {
            return projectMapper.findProjectById(id);
        }
        throw new RuntimeException("프로젝트 상태 수정 실패");
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
