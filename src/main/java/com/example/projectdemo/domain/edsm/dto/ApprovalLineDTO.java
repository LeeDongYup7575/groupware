package com.example.projectdemo.domain.edsm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApprovalLineDTO {


    private int documentId;
    private int drafterId;
    private int approverId;
    private int approvalNo;
    private String status;
    private Timestamp approvalAt;
    private String reason;

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public int getDrafterId() {
        return drafterId;
    }

    public void setDrafterId(int drafterId) {
        this.drafterId = drafterId;
    }

    public int getApproverId() {
        return approverId;
    }

    public void setApproverId(int approverId) {
        this.approverId = approverId;
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
