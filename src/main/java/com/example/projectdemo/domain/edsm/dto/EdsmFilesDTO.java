package com.example.projectdemo.domain.edsm.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
public class EdsmFilesDTO {

private int id;
private int edsmDocumentId;
private int documentType;
private String oriName;
private String sysName;
private String path;
private long size;
private Timestamp uploadDate;


}
