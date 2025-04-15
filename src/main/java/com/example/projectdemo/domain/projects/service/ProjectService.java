package com.example.projectdemo.domain.projects.service;

import com.example.projectdemo.domain.projects.dto.ProjectDTO;
import com.example.projectdemo.domain.projects.dto.ProjectMemberDTO;
import com.example.projectdemo.domain.projects.dto.TaskDTO;
import com.example.projectdemo.domain.projects.dto.TaskSummaryDTO;
import com.example.projectdemo.domain.projects.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private TaskService taskService;

    public List<ProjectDTO> getAllProjects() {
        List<ProjectDTO> projects = projectMapper.findAllProjects();
        // 각 프로젝트에 추가 정보를 설정
        projects.forEach(this::enrichProjectData);
        return projects;
    }

    public ProjectDTO getProjectById(Integer id) {
        ProjectDTO project = projectMapper.findProjectById(id);
        if (project != null) {
            enrichProjectData(project);
        }
        return project;
    }

    public List<ProjectDTO> getAccessibleProjects(String empNum) {
        List<ProjectDTO> projects = projectMapper.getPublicProjects();
        projects.forEach(this::enrichProjectData);
        return projects;
    }

    public List<ProjectDTO> getProjectsByDepartment(Integer depId) {
        List<ProjectDTO> projects = projectMapper.getProjectsByDepartment(depId);
        projects.forEach(this::enrichProjectData);
        return projects;
    }

    public List<ProjectDTO> getProjectsByEmployee(String empNum) {
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
                    .map(ProjectMemberDTO::getName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            project.setMemberNames(memberNames);
        }

        // 프로젝트 업무 정보 조회
        List<TaskDTO> tasks = taskService.getTasksByProject(project.getId());
        int totalTasks = tasks.size();
        int completedTasks = (int) tasks.stream()
                .filter(task -> "완료".equals(task.getStatus()))
                .count();

        // 업무 정보 설정
        project.setTaskCount(totalTasks);
        project.setCompletedTaskCount(completedTasks);

        // 완료 상태이면 무조건 진행률 100%로 설정
        if ("완료".equals(project.getStatus())) {
            project.setProgress(100);
        } else {
            // 진행률 계산
            int progress = calculateProjectProgress(project, tasks);
            project.setProgress(progress);
        }

        // 프로젝트 상태 자동 업데이트 (완료 상태가 아닌 경우만)
        if (!"완료".equals(project.getStatus())) {
            updateProjectStatusBasedOnProgress(project);
        }
    }

    /**
     * 프로젝트 진행률을 계산합니다.
     * 다음을 고려합니다:
     * 1. 업무 완료율
     * 2. 프로젝트 일정 진행률
     */
    private int calculateProjectProgress(ProjectDTO project, List<TaskDTO> tasks) {
        // 이미 완료된 프로젝트는 100%
        if ("완료".equals(project.getStatus())) {
            return 100;
        }

        int progress = 0;

        // 1. 업무 기반 진행률 (70% 반영)
        int taskProgress = 0;
        if (!tasks.isEmpty()) {
            // 방법 1: 완료된 업무 수 기준
            int completedTasks = (int) tasks.stream()
                    .filter(task -> "완료".equals(task.getStatus()))
                    .count();
            int taskBasedProgress = tasks.isEmpty() ? 0 : (completedTasks * 100) / tasks.size();

            // 방법 2: 각 업무의 진행률 평균 (업무 진행률이 있는 경우)
            int sumProgress = tasks.stream()
                    .mapToInt(task -> task.getProgress() != null ? task.getProgress() : 0)
                    .sum();
            int averageTaskProgress = tasks.isEmpty() ? 0 : sumProgress / tasks.size();

            // 두 방식의 평균으로 업무 진행률 결정
            taskProgress = (taskBasedProgress + averageTaskProgress) / 2;
        }

        // 2. 일정 기반 진행률 (30% 반영) - 시작일부터 종료일까지의 경과 일수 비율
        int scheduleProgress = 0;
        LocalDate now = LocalDate.now();

        if (project.getStartDate() != null && project.getEndDate() != null) {
            long totalDays = ChronoUnit.DAYS.between(project.getStartDate(), project.getEndDate());
            if (totalDays > 0) {
                long elapsedDays = ChronoUnit.DAYS.between(project.getStartDate(), now);
                if (elapsedDays < 0) {
                    // 프로젝트가 아직 시작하지 않음
                    scheduleProgress = 0;
                } else if (elapsedDays > totalDays) {
                    // 프로젝트 기한이 지남
                    scheduleProgress = 100;
                } else {
                    scheduleProgress = (int) ((elapsedDays * 100) / totalDays);
                }
            }
        }

        // 종합 진행률 계산 (업무:일정 = 7:3 비율)
        progress = (int) (taskProgress * 0.7 + scheduleProgress * 0.3);

        return progress;
    }

    /**
     * 진행률에 따라 프로젝트 상태를 업데이트합니다.
     */
    private void updateProjectStatusBasedOnProgress(ProjectDTO project) {
        // 현재 상태가 완료면 변경하지 않음
        if ("완료".equals(project.getStatus())) {
            return;
        }

        LocalDate now = LocalDate.now();

        // 프로젝트가 지연되었는지 확인
        if (project.getEndDate() != null && now.isAfter(project.getEndDate()) && project.getProgress() < 100) {
            // 기한이 지났고 100% 완료되지 않은 경우
            if (!"지연".equals(project.getStatus())) {
                projectMapper.updateProjectStatus(project.getId(), "지연");
                project.setStatus("지연");
            }
        }
        // 진행률에 따른 상태 업데이트
        else {
            String newStatus;
            if (project.getProgress() == 0) {
                newStatus = "준비중";
            } else if (project.getProgress() == 100) {
                newStatus = "완료";
                // 실제 완료일 설정
                if (project.getActualEndDate() == null) {
                    projectMapper.updateProjectActualEndDate(project.getId(), LocalDate.now());
                    project.setActualEndDate(LocalDate.now());
                }
            } else {
                newStatus = "진행중";
            }

            // 기존 상태와 다를 경우에만 업데이트
            if (!newStatus.equals(project.getStatus())) {
                projectMapper.updateProjectStatus(project.getId(), newStatus);
                project.setStatus(newStatus);
            }
        }
    }

    /**
     * 프로젝트 업무 요약 정보를 가져옵니다.
     */
    public TaskSummaryDTO getProjectTaskSummary(Integer projectId) {
        List<TaskDTO> tasks = taskService.getTasksByProject(projectId);

        int totalTasks = tasks.size();
        int notStartedTasks = 0;
        int inProgressTasks = 0;
        int completedTasks = 0;
        int delayedTasks = 0;
        int highPriorityTasks = 0;
        int mediumPriorityTasks = 0;
        int lowPriorityTasks = 0;

        for (TaskDTO task : tasks) {
            // 상태별 카운트
            switch (task.getStatus()) {
                case "미시작":
                    notStartedTasks++;
                    break;
                case "진행중":
                    inProgressTasks++;
                    break;
                case "완료":
                    completedTasks++;
                    break;
                default:
                    break;
            }

            // 지연 업무
            if (task.isOverdue()) {
                delayedTasks++;
            }

            // 우선순위별 카운트
            if (task.getPriority() != null) {
                switch (task.getPriority()) {
                    case "높음":
                        highPriorityTasks++;
                        break;
                    case "중간":
                        mediumPriorityTasks++;
                        break;
                    case "낮음":
                        lowPriorityTasks++;
                        break;
                    default:
                        break;
                }
            }
        }

        // 완료율 계산
        double completionRate = totalTasks > 0 ? ((double) completedTasks / totalTasks) * 100 : 0;

        return TaskSummaryDTO.builder()
                .totalTasks(totalTasks)
                .notStartedTasks(notStartedTasks)
                .inProgressTasks(inProgressTasks)
                .completedTasks(completedTasks)
                .delayedTasks(delayedTasks)
                .completionRate(completionRate)
                .highPriorityTasks(highPriorityTasks)
                .mediumPriorityTasks(mediumPriorityTasks)
                .lowPriorityTasks(lowPriorityTasks)
                .build();
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

            return getProjectById(project.getId()); // 보강된 데이터 포함하여 반환
        }
        throw new RuntimeException("프로젝트를 등록할 수 없습니다.");
    }

    @Transactional
    public ProjectDTO updateProject(ProjectDTO project) {
        if(projectMapper.updateProject(project) > 0) {
            return getProjectById(project.getId()); // 보강된 데이터 포함하여 반환
        }
        throw new RuntimeException("프로젝트를 수정할 수 없습니다.");
    }

    /**
     * 프로젝트의 완료 상태를 확인합니다.
     */
    public boolean isProjectCompleted(Integer projectId) {
        ProjectDTO project = projectMapper.findProjectById(projectId);
        if (project == null) {
            throw new RuntimeException("프로젝트를 찾을 수 없습니다: " + projectId);
        }
        return "완료".equals(project.getStatus());
    }

    /**
     * 프로젝트 완료 처리
     */
    /**
     * 프로젝트 완료 처리
     */
    @Transactional
    public ProjectDTO completeProject(Integer id) {
        // 이미 완료된 프로젝트인지 확인
        if (isProjectCompleted(id)) {
            return getProjectById(id);
        }

        // 현재 날짜를 실제 종료일로 설정
        projectMapper.updateProjectActualEndDate(id, LocalDate.now());

        // 완료 처리
        if (projectMapper.updateProjectStatus(id, "완료") > 0) {
            // 약간의 지연을 주어 DB 업데이트가 확실히 반영되도록 함
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return getProjectById(id);
        }
        throw new RuntimeException("프로젝트 완료 처리 실패");
    }

    @Transactional
    public void addProjectMember(Integer projectId, String empNum, String role) {
        ProjectMemberDTO member = new ProjectMemberDTO();
        member.setProjectId(projectId);
        member.setEmpNum(empNum);
        member.setRole(role);
        member.setJoinedAt(LocalDateTime.now());  // 현재 시간으로 설정

        if (projectMapper.insertProjectMember(member) <= 0) {
            throw new RuntimeException("프로젝트 멤버 추가 실패");
        }
    }

    @Transactional
    public void updateProjectMemberRole(Integer projectId, String empNum, String role) {
        if (projectMapper.updateProjectMemberRole(projectId, empNum, role) <= 0) {
            throw new RuntimeException("프로젝트 멤버 역할 수정 실패");
        }
    }

    /**
     * 프로젝트 상태 업데이트
     */
    @Transactional
    public ProjectDTO updateProjectStatus(Integer id, String status) {
        // 이미 완료된 프로젝트의 상태를 변경하려는 경우
        if (isProjectCompleted(id) && !"완료".equals(status)) {
            throw new RuntimeException("이미 완료된 프로젝트의 상태는 변경할 수 없습니다.");
        }

        // 상태가 완료로 변경되는 경우 실제 종료일 설정
        if ("완료".equals(status)) {
            projectMapper.updateProjectActualEndDate(id, LocalDate.now());
        }

        if (projectMapper.updateProjectStatus(id, status) > 0) {
            // 진행률은 enrichProjectData에서 동적으로 계산됨
            // 더 이상 DB에 저장하지 않음

            // 약간의 지연을 주어 DB 업데이트가 확실히 반영되도록 함
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return getProjectById(id);
        }
        throw new RuntimeException("프로젝트 상태 수정 실패");
    }


    @Transactional
    public void removeProjectMember(Integer projectId, String empNum) {
        if (projectMapper.deleteProjectMember(projectId, empNum) <= 0) {
            throw new RuntimeException("프로젝트 멤버 제거 실패");
        }
    }

    public List<ProjectMemberDTO> getProjectMembers(Integer projectId) {
        return projectMapper.getProjectMembers(projectId);
    }
}