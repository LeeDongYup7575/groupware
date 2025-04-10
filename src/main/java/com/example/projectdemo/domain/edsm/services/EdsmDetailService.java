package com.example.projectdemo.domain.edsm.services;


import com.example.projectdemo.domain.edsm.dao.EdsmDAO;
import com.example.projectdemo.domain.edsm.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EdsmDetailService {


    @Autowired
    private EdsmDAO edao;

    // 결재문서 정보 가져오기
    public List<EdsmDocumentDTO> getEdsmDocumentListFromDocId(int id) {

        return edao.selectByDocumentId(id);

    }

    //결재문서 해당 결재라인 가져오기
    public List<ApprovalLineDTO> getEdsmApprovalLineListFromDocId(int id) {

        return edao.selectByDocumentIdFromApprovalLine(id);
    }

    // 업무연락 Detail 정보 가져오기
    public List<EdsmBusinessContactDTO> getEdsmBusinessContactListFromDocId(int id) {

        return edao.selectByBusinessContactFromDocId(id);
    }

    // 지출결의서 Detail 정보 가져오기
    public List<EdsmCashDisbuVoucherDTO> getEdsmCashDisbuVoucherListFromDocId(int id) {
        return edao.selectByCashDisbuVoucherFromDocId(id);

    }

    //품의서 Detail 정보 가져오기
    public List<EdsmLetterOfApprovalDTO> getEdsmLetterOfApprovalListFromDocId(int id) {
        return edao.selectByLetterOfApprovalFromDocId(id);
    }

    // 결재권자 결재상태 업데이트
    public boolean updateApprovalStatus(ApprovalLineDTO approvalLineDTO) {
        int result = edao.updateApprovalStatus(approvalLineDTO);

        if(result == 1) {
            return true;
        }
       return false;
    }




}
