package com.example.projectdemo.domain.edsm.dao;


import com.example.projectdemo.domain.edsm.dto.EdsmFilesDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EdsmFilesDAO {

    @Autowired
    private SqlSession mybatis;

    public int filesInsert(EdsmFilesDTO edsmFilesDTO) {

        return  mybatis.insert("Edsm.FilesInsert", edsmFilesDTO);


    }

    public List<EdsmFilesDTO> getFilesSelectFromDocId(int edsmDocumentId) {

       return mybatis.selectList("Edsm.FilesSelectFromDocId", edsmDocumentId);
    }


}
