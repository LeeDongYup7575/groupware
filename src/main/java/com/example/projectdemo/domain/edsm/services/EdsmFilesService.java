package com.example.projectdemo.domain.edsm.services;


import com.example.projectdemo.domain.edsm.dao.EdsmFilesDAO;
import com.example.projectdemo.domain.edsm.dto.EdsmFilesDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.checkerframework.checker.nullness.qual.AssertNonNullIfNonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class EdsmFilesService {

    @Autowired
    private EdsmFilesDAO edsmFilesDAO;

    @Autowired
    private HttpSession session;


    public void getFilesInsert(int edsmDocumentId, int edsmFormId, MultipartFile[] files)throws Exception {

        String realPath = session.getServletContext().getRealPath("edsmUpload");

        System.out.println(realPath);
        File realPathFile = new File(realPath);
        //파일경로폴더가 없으면 만들기.
        if (!realPathFile.exists()) realPathFile.mkdir();

        if (files != null) {
            for (MultipartFile file : files) {

                if (!file.isEmpty()) {

                    //클라이언트가 저장한 원본 파일명
                    String oriName = file.getOriginalFilename();

                    //중복된 파일 이름 방지.
                    String sysName = UUID.randomUUID() + "_" + oriName;

                    file.transferTo(new File(realPath + "/" + sysName));


                    long fileSize = file.getSize();


                    EdsmFilesDTO edsmFilesDTO = new EdsmFilesDTO();
                    edsmFilesDTO.setEdsmDocumentId(edsmDocumentId);
                    edsmFilesDTO.setDocumentType(edsmFormId);
                    edsmFilesDTO.setOriName(oriName);
                    edsmFilesDTO.setSysName(sysName);
                    edsmFilesDTO.setPath(realPath);
                    edsmFilesDTO.setSize(fileSize);

                    int result = edsmFilesDAO.filesInsert(edsmFilesDTO);

                }
            }
        }

    }

    public List<EdsmFilesDTO> getFilesSelectFromDocId(int edsmDocumentId) {

        return edsmFilesDAO.getFilesSelectFromDocId(edsmDocumentId);
    }
















}
