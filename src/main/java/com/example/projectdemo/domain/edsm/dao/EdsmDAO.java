package com.example.projectdemo.domain.edsm.dao;


import com.example.projectdemo.domain.edsm.dto.EdsmBcDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EdsmDAO {


    @Autowired
    private SqlSession mybatis;


    public int insertByBc(EdsmBcDTO edsmBcDTO) {

       return mybatis.insert("Edsm.InsertByBc", edsmBcDTO);

    }

}
