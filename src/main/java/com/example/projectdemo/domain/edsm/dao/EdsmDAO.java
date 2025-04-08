package com.example.projectdemo.domain.edsm.dao;


import com.example.projectdemo.domain.edsm.dto.ApprovalLineDTO;
import com.example.projectdemo.domain.edsm.dto.EdsmBusinessContactDTO;
import com.example.projectdemo.domain.edsm.dto.EdsmDocumentDTO;
import com.example.projectdemo.domain.edsm.dto.EdsmLetterOfApprovalDTO;
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

    //품의서 정보 테이블 입력
    public int insertByLetterOfApproval(EdsmLetterOfApprovalDTO letterOfApprovalDTO) {
        return mybatis.insert("Edsm.InsertByLac", letterOfApprovalDTO);
    }

    // 전자결재 라인 입력
    public int insertByApprovalLine(ApprovalLineDTO aldto) {

        return mybatis.insert("Edsm.InsertByBcApproval", aldto);

    }
    // 기안자를 기준으로 모든 문서 출력
    public List<EdsmDocumentDTO> selectByAllDocument(String empNum) {
        return mybatis.selectList("Edsm.SelectByAllDocument", empNum);

    }

    // 로그인된 사번을 기준으로 로그인자가 결재권 있는 문서 리스트 출력
    public List<EdsmDocumentDTO> selectByAllApprovalFromId(String empNum) {
        return mybatis.selectList("Edsm.selectByAllApprovalFromId", empNum);

    }
    
    // 문서번호를 통한 Detail 출력
    public List<EdsmDocumentDTO> selectByDocumentId(int id) {

        return mybatis.selectList("Edsm.SelectByDocumentId", id);
    }

    // 문서번호를 통한 결재라인 출력
    public List<ApprovalLineDTO> selectByDocumentIdFromApprovalLine(int id) {
        return mybatis.selectList("Edsm.SelectByDocumentIdFromApprovalLine", id);
    }
    
    //결재라인 상태 업데이트
    public int updateApprovalStatus(ApprovalLineDTO approvalLineDTO) {
        return mybatis.update("Edsm.UpdateApprovalStatus", approvalLineDTO);
    }

}
