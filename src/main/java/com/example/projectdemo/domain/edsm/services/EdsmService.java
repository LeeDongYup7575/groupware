package com.example.projectdemo.domain.edsm.services;


import com.example.projectdemo.domain.edsm.dao.EdsmDAO;
import com.example.projectdemo.domain.edsm.dto.ApprovalLineDTO;
import com.example.projectdemo.domain.edsm.dto.EdsmDocumentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EdsmService {

    @Autowired
    private EdsmDAO edao;

    // 전자결재 메인화면
    // 모든 문서 가져오기(로그인한 사원번호를 기준으로)
    public List<EdsmDocumentDTO> selectByAllDocument(String empNum) {

        return edao.selectByAllDocument(empNum);
    }

    // 업무연락 문서 리스트 출력하기
    public List<EdsmDocumentDTO> selectByAllBusinessDocument(String empNum) {
        return edao.SelectByAllBusinessDocument(empNum);
    }

    // 지출결의서 문서 리스트 출력하기
    public List<EdsmDocumentDTO> selectByAllCashDocument(String empNum) {
        return edao.SelectByAllCashDocument(empNum);
    }

    // 품의서 문서 리스트 출력하기
    public List<EdsmDocumentDTO> selectByAllLetterDocument(String empNum) {
        return edao.SelectByAllLetterDocument(empNum);
    }


    // 휴가 문서 리스트 가져오기
    public List<EdsmDocumentDTO> selectByAllLeavesDocument(String empNum) {
        return edao.SelectByAllLeavesDocument(empNum);
    }

    // 연장근무 문서 리스트 가져오기
    public List<EdsmDocumentDTO> selectByAllOvertimeDocument(String empNum) {
        return edao.SelectByAllOvertimeDocument(empNum);
    }


    //결재 상태 <대기>
    //로그인한 사원이 결재권이 있는경우 아직 결재순서가 되지않은 리스트 띄우기
    public List<EdsmDocumentDTO> selectByAllApprovalFromIdWait(String empNum) {
        return edao.selectByAllApprovalFromIdWait(empNum);
    }

    //결재 상태 <예정>
    //로그인한 사원이 결재권이 있는경우 아직 결재순서가 되지않은 리스트 띄우기
    public List<EdsmDocumentDTO> selectByAllApprovalFromIdExpected(String empNum) {
        return edao.selectByAllApprovalFromIdExpected(empNum);
    }

    //결재 상태 <진행>
    //로그인한 사원이 결재권자이거나 본인이 기안한문서가 진행중인경우


    //결재 상태<완료>

}
