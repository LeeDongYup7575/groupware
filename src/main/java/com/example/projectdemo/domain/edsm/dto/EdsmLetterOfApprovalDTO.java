package com.example.projectdemo.domain.edsm.dto;

public class EdsmLetterOfApprovalDTO {



    private int id;
    private int edsmDocumentId;
    private String drafterId;
    private String title;
    private String content;
    private String expectedCost;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEdsmDocumentId() {
        return edsmDocumentId;
    }

    public void setEdsmDocumentId(int edsmDocumentId) {
        this.edsmDocumentId = edsmDocumentId;
    }

    public String getDrafterId() {
        return drafterId;
    }

    public void setDrafterId(String drafterId) {
        this.drafterId = drafterId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExpectedCost() {
        return expectedCost;
    }

    public void setExpectedCost(String expectedCost) {
        this.expectedCost = expectedCost;
    }
}
