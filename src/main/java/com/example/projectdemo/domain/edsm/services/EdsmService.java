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


    //--------------------------------------------------------------------------------//
    //나의 기안문서 보기(전체 - 업무연락)
    public List<EdsmDocumentDTO> selectByAllMyWrittenDocumentBc(String empNum) {


        return edao.selectByAllMyWrittenDocumentBc(empNum);
    }

    //나의 기안문서 보기(전체 - 지출결의서)
    public List<EdsmDocumentDTO> selectByAllMyWrittenDocumentCdv(String empNum) {


        return edao.selectByAllMyWrittenDocumentCdv(empNum);
    }

    //나의 기안문서 보기(전체 - 품의서)
    public List<EdsmDocumentDTO> selectByAllMyWrittenDocumentLoa(String empNum) {


        return edao.selectByAllMyWrittenDocumentLoa(empNum);
    }
    //나의 기안문서 보기(전체 - 휴가신청서)
    public List<EdsmDocumentDTO> selectByAllMyWrittenDocumentLeaves(String empNum) {


        return edao.selectByAllMyWrittenDocumentLeaves(empNum);
    }

    //나의 기안문서 보기(전체 - 연장근무신청서)
    public List<EdsmDocumentDTO> selectByAllMyWrittenDocumentOvertime(String empNum) {


        return edao.selectByAllMyWrittenDocumentOvertime(empNum);
    }



    //--------------------------//



    //나의 기안문서 보기(승인 - 업무연락)
    public List<EdsmDocumentDTO> selectByAllMyApprovalDocumentBc(String empNum) {


        return edao.selectByAllMyApprovalDocumentBc(empNum);
    }

    //나의 기안문서 보기(승인 - 지출결의서)
    public List<EdsmDocumentDTO> selectByAllMyApprovalDocumentCdv(String empNum) {


        return edao.selectByAllMyApprovalDocumentCdv(empNum);
    }

    //나의 기안문서 보기(승인 - 품의서)
    public List<EdsmDocumentDTO> selectByAllMyApprovalDocumentLoa(String empNum) {


        return edao.selectByAllMyApprovalDocumentLoa(empNum);
    }
    //나의 기안문서 보기(승인 - 휴가신청서)
    public List<EdsmDocumentDTO> selectByAllMyApprovalDocumentLeaves(String empNum) {


        return edao.selectByAllMyApprovalDocumentLeaves(empNum);
    }

    //나의 기안문서 보기(승인 - 연장근무신청서)
    public List<EdsmDocumentDTO> selectByAllMyApprovalDocumentOvertime(String empNum) {


        return edao.selectByAllMyApprovalDocumentOvertime(empNum);
    }



    //------------------------------------------//
//나의 기안문서 보기(반려 - 업무연락)
    public List<EdsmDocumentDTO> selectByAllMyRejectedDocumentBc(String empNum) {


        return edao.selectByAllMyRejectedDocumentBc(empNum);
    }

    //나의 기안문서 보기(반려 - 지출결의서)
    public List<EdsmDocumentDTO> selectByAllMyRejectedDocumentCdv(String empNum) {


        return edao.selectByAllMyRejectedDocumentCdv(empNum);
    }

    //나의 기안문서 보기(반려 - 품의서)
    public List<EdsmDocumentDTO> selectByAllMyRejectedDocumentLoa(String empNum) {


        return edao.selectByAllMyRejectedDocumentLoa(empNum);
    }
    //나의 기안문서 보기(반려 - 휴가신청서)
    public List<EdsmDocumentDTO> selectByAllMyRejectedDocumentLeaves(String empNum) {


        return edao.selectByAllMyRejectedDocumentLeaves(empNum);
    }

    //나의 기안문서 보기(반려 - 연장근무신청서)
    public List<EdsmDocumentDTO> selectByAllMyRejectedDocumentOvertime(String empNum) {


        return edao.selectByAllMyRejectedDocumentOvertime(empNum);
    }







}
