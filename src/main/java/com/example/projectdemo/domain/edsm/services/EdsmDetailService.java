package com.example.projectdemo.domain.edsm.services;


import com.example.projectdemo.domain.edsm.dao.EdsmDAO;
import com.example.projectdemo.domain.edsm.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // 1. 현재 결재자 상태 업데이트
        int result = edao.updateApprovalStatus(approvalLineDTO);

        // 2. 업데이트가 성공했고, 상태가 '승인'인 경우 다음 결재자 처리
        if (result == 1 && "승인".equals(approvalLineDTO.getStatus())) {
            // 다음 결재자의 approval_no 계산
            int nextApprovalNo = approvalLineDTO.getApprovalNo() + 1;

            // 다음 결재자 상태를 '예정'에서 '대기'로 변경
            String newStatus = "대기";
            String currentStatus = "예정";
            Map<String,Object> param = new HashMap<>();
            param.put("approvalNo",nextApprovalNo);
            param.put("documentId",approvalLineDTO.getDocumentId());
            param.put("newStatus",newStatus);
            param.put("currentStatus",currentStatus);

            edao.updateNextApproverStatus(param);
        }

        return result == 1;
    }

    //사유 가져오기

    public String getRejectionReason(ApprovalLineDTO approvalLineDTO) {

        return edao.getRejectionReason(approvalLineDTO);

    }

}
