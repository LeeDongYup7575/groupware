package com.example.projectdemo.domain.edsm.dto;

public class EdsmBusinessContactDTO {

    private int id;
    private int edsmDocumentId;
    private String drafterId;
    private String title;
    private String content;

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

    public void setTitie(String titie) {
        this.title = titie;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
