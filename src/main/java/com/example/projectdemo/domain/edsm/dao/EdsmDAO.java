package com.example.projectdemo.domain.edsm.dao;


import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EdsmDAO {


    @Autowired
    private SqlSession mybatis;



}
