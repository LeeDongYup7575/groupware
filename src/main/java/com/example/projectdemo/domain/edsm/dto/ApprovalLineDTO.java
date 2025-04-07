package com.example.projectdemo.domain.edsm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApprovalLineDTO {

    private int documentId;

    // 기안자 관련 정보
    private String drafterId;
    private String drafterName;
    private String drafterPosition; // 기안자 직급

    // 결재자 관련 정보
    private String approverId;
    private String approverName;
    private String approverPosition; // 결재자 직급

    // 부서 정보 (예: 기안자의 부서)
    private String departmentName;

    private int approvalNo;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String status;

    private Timestamp approvalAt;
    private String reason;

    public int getDocumentId() {
        return documentId;
    }
    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }
    public String getDrafterId() {
        return drafterId;
    }
    public void setDrafterId(String drafterId) {
        this.drafterId = drafterId;
    }
    public String getDrafterName() {
        return drafterName;
    }
    public void setDrafterName(String drafterName) {
        this.drafterName = drafterName;
    }
    public String getDrafterPosition() {
        return drafterPosition;
    }
    public void setDrafterPosition(String drafterPosition) {
        this.drafterPosition = drafterPosition;
    }
    public String getApproverId() {
        return approverId;
    }
    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }
    public String getApproverName() {
        return approverName;
    }
    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }
    public String getApproverPosition() {
        return approverPosition;
    }
    public void setApproverPosition(String approverPosition) {
        this.approverPosition = approverPosition;
    }
    public String getDepartmentName() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    public int getApprovalNo() {
        return approvalNo;
    }
    public void setApprovalNo(int approvalNo) {
        this.approvalNo = approvalNo;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Timestamp getApprovalAt() {
        return approvalAt;
    }
    public void setApprovalAt(Timestamp approvalAt) {
        this.approvalAt = approvalAt;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
}
