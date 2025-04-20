package com.example.projectdemo.domain.notification.service;

import com.example.projectdemo.domain.board.entity.Comments;
import com.example.projectdemo.domain.board.entity.Posts;
import com.example.projectdemo.domain.board.service.CommentsService;
import com.example.projectdemo.domain.booking.entity.MeetingRoomBooking;
import com.example.projectdemo.domain.booking.entity.SuppliesBooking;
import com.example.projectdemo.domain.edsm.dto.EdsmDocumentDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.example.projectdemo.domain.notification.enums.NotificationType;
import com.example.projectdemo.domain.projects.dto.ProjectDTO;
import com.example.projectdemo.domain.projects.dto.ProjectMemberDTO;
import com.example.projectdemo.domain.projects.dto.TaskDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationService notificationService;
    private final EmployeesService employeesService;
    private final CommentsService commentsService;

    /**
     * 댓글 알림 처리
     */
    public void handleCommentNotification(Comments comment, Integer postAuthorId) {
        // 자신의 게시글에 본인이 댓글을 달면 알림 발생 안함
        if (postAuthorId.equals(comment.getEmpId())) {
            return;
        }

        EmployeesDTO postAuthor = employeesService.findById(postAuthorId);
        if (postAuthor == null) {
            return;
        }

        EmployeesDTO writer = employeesService.findById(comment.getEmpId());
        String writerName = writer != null ? writer.getName() : "(알 수 없음)";

        String content = writerName + "님이 회원님의 게시글에 댓글을 남겼습니다: " +
                truncateContent(comment.getContent());

        String link = "/board/post/" + comment.getPostId();

        notificationService.createNotification(
                postAuthor.getEmpNum(),
                content,
                link,
                NotificationType.COMMENT,
                comment.getId()
        );
    }

    /**
     * 대댓글 알림 처리
     */
    public void handleReplyNotification(Comments reply) {
        if (reply.getParentId() == null) {
            return;
        }

        Comments parentComment = commentsService.getCommentsById(reply.getParentId());
        if (parentComment == null || parentComment.getEmpId().equals(reply.getEmpId())) {
            return;
        }

        EmployeesDTO commentAuthor = employeesService.findById(parentComment.getEmpId());
        if (commentAuthor == null) {
            return;
        }

        EmployeesDTO writer = employeesService.findById(reply.getEmpId());
        String writerName = writer != null ? writer.getName() : "(알 수 없음)";

        String content = writerName+ "님이 회원님의 댓글에 답글을 남겼습니다: " +
                truncateContent(reply.getContent());
        String link = "/board/post/" + reply.getPostId();

        notificationService.createNotification(
                commentAuthor.getEmpNum(),
                content,
                link,
                NotificationType.REPLY,
                reply.getId()
        );
    }

    /**
     * 프로젝트 생성 알림 처리
     */
    public void handleProjectCreationNotification(ProjectDTO project, List<ProjectMemberDTO> members) {
        for (ProjectMemberDTO member : members) {
            // 프로젝트 매니저는 알림 받지 않음
            if (member.getEmpNum().equals(project.getManagerEmpNum())) {
                continue;
            }

            String content = "새 프로젝트에 참여하게 되었습니다: " + project.getName();
            String link = "/projects/" + project.getId();

            notificationService.createNotification(
                    member.getEmpNum(),
                    content,
                    link,
                    NotificationType.PROJECT,
                    project.getId()
            );
        }
    }

    /**
     * 프로젝트 완료 알림 처리
     */
    public void handleProjectCompletionNotification(ProjectDTO project, List<ProjectMemberDTO> members) {
        for (ProjectMemberDTO member : members) {
            String content = "참여한 프로젝트가 완료되었습니다: " + project.getName();
            String link = "/projects/" + project.getId();

            notificationService.createNotification(
                    member.getEmpNum(),
                    content,
                    link,
                    NotificationType.PROJECT,
                    project.getId()
            );
        }
    }

    /**
     * 업무 생성 알림 처리
     */
    public void handleTaskCreationNotification(TaskDTO task) {
        // 자신이 생성하고 자신이 담당하는 업무면 알림 발생 안함
        if (task.getAssigneeEmpNum() == null ||
                task.getReporterEmpNum() == null ||
                task.getReporterEmpNum().equals(task.getAssigneeEmpNum())) {
            return;
        }

        String content = "새 업무가 할당되었습니다: " + task.getTitle();
        String link = "/projects/" + task.getProjectId() + "/tasks/" + task.getId();

        notificationService.createNotification(
                task.getAssigneeEmpNum(),
                content,
                link,
                NotificationType.TASK,
                task.getId()
        );
    }

    /**
     * 업무 상태 변경 알림 처리
     */
    public void handleTaskUpdateNotification(TaskDTO task, String oldStatus, String newStatus) {
        // 본인이 생성한 업무는 알림 발생 안함
        if (task.getReporterEmpNum().equals(task.getAssigneeEmpNum())) {
            return;
        }

        // 업무 생성자에게 알림
        if (!task.getReporterEmpNum().equals(task.getAssigneeEmpNum())) {
            String content = "담당 업무의 상태가 '" + oldStatus + "'에서 '" + newStatus + "'로 변경되었습니다: " + task.getTitle();
            String link = "/projects/" + task.getProjectId() + "/tasks/" + task.getId();

            notificationService.createNotification(
                    task.getReporterEmpNum(),
                    content,
                    link,
                    NotificationType.TASK,
                    task.getId()
            );
        }
    }

    /**
     * 예약 시작 알림 처리 (예약 시작 1시간 전)
     */
    public void handleBookingStartingSoonNotification(MeetingRoomBooking booking) {
        String content = "회의실 예약이 1시간 후에 시작됩니다: " + booking.getTitle();
        String link = "/booking/meeting-room";

        notificationService.createNotification(
                booking.getEmpNum(),
                content,
                link,
                NotificationType.BOOKING,
                booking.getId()
        );
    }

    /**
     * 비품 예약 시작 알림 처리 (예약 시작 1시간 전)
     */
    public void handleSuppliesBookingStartingSoonNotification(SuppliesBooking booking) {
        String content = "비품 예약이 1시간 후에 시작됩니다: " + booking.getSupplies().getName() + " " + booking.getQuantity() + "개";
        String link = "/booking/supplies";

        notificationService.createNotification(
                booking.getEmpNum(),
                content,
                link,
                NotificationType.BOOKING,
                booking.getId()
        );
    }

    /**
     * 결재 문서 알림 처리 (결재자에게 알림)
     */
    public void handleApprovalDocumentNotification(EdsmDocumentDTO document, String approverEmpNum) {
        String content = "새 결재 문서가 도착했습니다: " + document.getTitle();
        String link = "/edsm/document/" + document.getId();

        notificationService.createNotification(
                approverEmpNum,
                content,
                link,
                NotificationType.APPROVAL,
                document.getId()
        );
    }

    /**
     * 문자열 길이 제한
     */
    private String truncateContent(String content) {
        if (content == null) {
            return "";
        }
        return content.length() > 30 ? content.substring(0, 27) + "..." : content;
    }
}
