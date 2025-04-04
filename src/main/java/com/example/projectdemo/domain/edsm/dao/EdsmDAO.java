package com.example.projectdemo.domain.edsm.dao;


import com.example.projectdemo.domain.edsm.dto.ApprovalLineDTO;
import com.example.projectdemo.domain.edsm.dto.EdsmBusinessContactDTO;
import com.example.projectdemo.domain.edsm.dto.EdsmDocumentDTO;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EdsmDAO {


    @Autowired
    private SqlSession mybatis;

    //업무연락 전자결재 테이블 입력
    public int insertByedsm_document(EdsmDocumentDTO edsmDocumentDTO) {

       return mybatis.insert("Edsm.InsertByEd", edsmDocumentDTO);

    }
    //업무연락 정보 테이블 입력
    public int insertBybusinessContact(EdsmBusinessContactDTO edsmBusinessContactDTO) {
        return mybatis.insert("Edsm.InsertByBc", edsmBusinessContactDTO);
    }

    // 전자결재 라인 입력
    public int insertByBcApprovalLine(ApprovalLineDTO aldto) {

        return mybatis.insert("Edsm.InsertByBcApproval", aldto);

    }
    // 기안자를 기준으로 모든 문서 출력
    public List<EdsmDocumentDTO> selectByAllDocument(String empNum) {
        return mybatis.selectList("Edsm.SelectByAllDocument", empNum);

    }
    
    
    
    

}
