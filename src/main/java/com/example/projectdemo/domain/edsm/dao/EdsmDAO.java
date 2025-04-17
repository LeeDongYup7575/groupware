package com.example.projectdemo.domain.edsm.dao;


import com.example.projectdemo.domain.edsm.dto.*;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //지출결의서 정보 테이블 입력
    public int insertByCashDisbuVoucher(EdsmCashDisbuVoucherDTO edsmCashDisbuVoucherDTO) {
        return mybatis.insert("Edsm.InsertByCdv", edsmCashDisbuVoucherDTO);
    }

    //품의서 정보 테이블 입력
    public int insertByLetterOfApproval(EdsmLetterOfApprovalDTO letterOfApprovalDTO) {
        return mybatis.insert("Edsm.InsertByLac", letterOfApprovalDTO);
    }

    // 전자결재 라인 입력
    public int insertByApprovalLine(ApprovalLineDTO aldto) {

        return mybatis.insert("Edsm.InsertByBcApproval", aldto);

    }
    //프로시즈
    public void callUpdateDocumentStatusProcedure(int documentId, String status) {
        Map<String, Object> params = new HashMap<>();
        params.put("documentId", documentId);
        params.put("status", status);

        // 절차적으로 MyBatis를 통해 저장 프로시저 호출
        mybatis.update("Edsm.callUpdateDocumentStatusProcedure", params);
    }





    // 기안자를 기준으로 모든 문서 출력
    public List<EdsmDocumentDTO> selectByAllDocument(String empNum) {
        return mybatis.selectList("Edsm.SelectByAllDocument", empNum);

    }

    //로그인한 자의 사번을 기준으로 결재권이 있는 업무연락 문서 출력
    public List<EdsmDocumentDTO> SelectByAllBusinessDocument (String empNum) {
        return mybatis.selectList("Edsm.SelectByAllBusinessDocument",empNum);
    }

    //로그인한 자의 사번을 기준으로 결재권이 있는 지출결의서 문서 출력
    public List<EdsmDocumentDTO> SelectByAllCashDocument (String empNum) {
        return mybatis.selectList("Edsm.SelectByAllCashDocument",empNum);
    }

    //로그인한 자의 사번을 기준으로 결재권이 있는 품의서 문서 출력
    public List<EdsmDocumentDTO> SelectByAllLetterDocument (String empNum) {
        return mybatis.selectList("Edsm.SelectByAllLetterDocument",empNum);
    }

    //로그인한 자의 사번을 기준으로 결재권이 있는 휴가신청서 문서 출력
    public List<EdsmDocumentDTO> SelectByAllLeavesDocument (String empNum) {
        return mybatis.selectList("Edsm.SelectByAllLeavesDocument",empNum);
    }

    //로그인한 자의 사번을 기준으로 결재권 있는 업무연장 신청서 문서 출력
    public List<EdsmDocumentDTO> SelectByAllOvertimeDocument(String empNum) {
        return mybatis.selectList("Edsm.SelectByAllOvertimeDocument",empNum);
    }

    // 로그인된 사번을 기준으로 로그인자가 결재권이 대기인 문서 리스트 출력
    public List<EdsmDocumentDTO> selectByAllApprovalFromIdWait(String empNum) {
        return mybatis.selectList("Edsm.selectByAllApprovalFromId_wait", empNum);

    }

    // 로그인된 사번을 기준으로 로그인자가 결재권이 대기인 문서 리스트 출력
    public List<EdsmDocumentDTO> selectByAllApprovalFromIdExpected(String empNum) {
        return mybatis.selectList("Edsm.selectByAllApprovalFromId_expected", empNum);

    }
    
    // 문서번호를 통한 기안 정보 출력
    public List<EdsmDocumentDTO> selectByDocumentId(int id) {

        return mybatis.selectList("Edsm.SelectByDocumentId", id);
    }

    // 문서번호를 통한 업무연락 Detail 정보 출력
    public List<EdsmBusinessContactDTO> selectByBusinessContactFromDocId(int id) {
        return mybatis.selectList("Edsm.SelectByBusinessContactFromDocId", id);
    }

    // 문서번호를 통한 지출결의서 Detail 정보 출력
    public List<EdsmCashDisbuVoucherDTO> selectByCashDisbuVoucherFromDocId(int id) {
        return mybatis.selectList("Edsm.SelectByCashDisbuVoucherFromDocId",id);
    }

    // 문서번호를 통한 품의서 Detail 정보 출력
    public List<EdsmLetterOfApprovalDTO> selectByLetterOfApprovalFromDocId(int id) {
        return mybatis.selectList("Edsm.SelectByLetterOfApprovalFromDocId",id);
    }

    // 문서번호를 통한 결재라인 출력
    public List<ApprovalLineDTO> selectByDocumentIdFromApprovalLine(int id) {
        return mybatis.selectList("Edsm.SelectByDocumentIdFromApprovalLine", id);
    }

    //결재라인 상태 업데이트(대기->승인 or 반려)
    public int updateApprovalStatus(ApprovalLineDTO approvalLineDTO) {
        return mybatis.update("Edsm.UpdateApprovalStatus", approvalLineDTO);
    }
    //결재라인 상태 업데이스(예정->대기)
    public int updateNextApproverStatus(Map<String,Object> param) {
        return mybatis.update("Edsm.UpdateNextApproverStatus",param);
    }
//--------------------------------------------------------------------------------//
    //나의 기안문서 보기(전체 - 업무연락)
    public List<EdsmDocumentDTO> selectByAllMyWrittenDocumentBc(String empNum) {


        return mybatis.selectList("Edsm.SelectByAllMyWrittenDocumentBc", empNum);
    }

    //나의 기안문서 보기(전체 - 지출결의서)
    public List<EdsmDocumentDTO> selectByAllMyWrittenDocumentCdv(String empNum) {


        return mybatis.selectList("Edsm.SelectByAllMyWrittenDocumentCdv", empNum);
    }

    //나의 기안문서 보기(전체 - 품의서)
    public List<EdsmDocumentDTO> selectByAllMyWrittenDocumentLoa(String empNum) {


        return mybatis.selectList("Edsm.SelectByAllMyWrittenDocumentLoa", empNum);
    }
    //나의 기안문서 보기(전체 - 휴가신청서)
    public List<EdsmDocumentDTO> selectByAllMyWrittenDocumentLeaves(String empNum) {


        return mybatis.selectList("Edsm.SelectByAllMyWrittenDocumentLeaves", empNum);
    }

    //나의 기안문서 보기(전체 - 연장근무신청서)
    public List<EdsmDocumentDTO> selectByAllMyWrittenDocumentOvertime(String empNum) {


        return mybatis.selectList("Edsm.SelectByAllMyWrittenDocumentOvertime", empNum);
    }



   //--------------------------//



    //나의 기안문서 보기(승인 - 업무연락)
    public List<EdsmDocumentDTO> selectByAllMyApprovalDocumentBc(String empNum) {


        return mybatis.selectList("Edsm.SelectByAllMyApprovalDocumentBc", empNum);
    }

    //나의 기안문서 보기(승인 - 지출결의서)
    public List<EdsmDocumentDTO> selectByAllMyApprovalDocumentCdv(String empNum) {


        return mybatis.selectList("Edsm.SelectByAllMyApprovalDocumentCdv", empNum);
    }

    //나의 기안문서 보기(승인 - 품의서)
    public List<EdsmDocumentDTO> selectByAllMyApprovalDocumentLoa(String empNum) {


        return mybatis.selectList("Edsm.SelectByAllMyApprovalDocumentLoa", empNum);
    }
    //나의 기안문서 보기(승인 - 휴가신청서)
    public List<EdsmDocumentDTO> selectByAllMyApprovalDocumentLeaves(String empNum) {


        return mybatis.selectList("Edsm.SelectByAllMyApprovalDocumentLeaves", empNum);
    }

    //나의 기안문서 보기(승인 - 연장근무신청서)
    public List<EdsmDocumentDTO> selectByAllMyApprovalDocumentOvertime(String empNum) {


        return mybatis.selectList("Edsm.SelectByAllMyApprovalDocumentOvertime", empNum);
    }



//------------------------------------------//
//나의 기안문서 보기(반려 - 업무연락)
public List<EdsmDocumentDTO> selectByAllMyRejectedDocumentBc(String empNum) {


    return mybatis.selectList("Edsm.SelectByAllMyRejectedDocumentBc", empNum);
}

    //나의 기안문서 보기(반려 - 지출결의서)
    public List<EdsmDocumentDTO> selectByAllMyRejectedDocumentCdv(String empNum) {


        return mybatis.selectList("Edsm.SelectByAllMyRejectedDocumentCdv", empNum);
    }

    //나의 기안문서 보기(반려 - 품의서)
    public List<EdsmDocumentDTO> selectByAllMyRejectedDocumentLoa(String empNum) {


        return mybatis.selectList("Edsm.SelectByAllMyRejectedDocumentLoa", empNum);
    }
    //나의 기안문서 보기(반려 - 휴가신청서)
    public List<EdsmDocumentDTO> selectByAllMyRejectedDocumentLeaves(String empNum) {


        return mybatis.selectList("Edsm.SelectByAllMyRejectedDocumentLeaves", empNum);
    }

    //나의 기안문서 보기(반려 - 연장근무신청서)
    public List<EdsmDocumentDTO> selectByAllMyRejectedDocumentOvertime(String empNum) {


        return mybatis.selectList("Edsm.SelectByAllMyRejectedDocumentOvertime", empNum);
    }

    //사유 가져오기
    public String getRejectionReason(ApprovalLineDTO approvalLineDTO) {

        ApprovalLineDTO approvalLine = mybatis.selectOne("Edsm.SelectByRejectionReason", approvalLineDTO);


        if(approvalLine != null){
            return approvalLine.getReason();
        }

            return "사유가 없습니다.";


    }


}
