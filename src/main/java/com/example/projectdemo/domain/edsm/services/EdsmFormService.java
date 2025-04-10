package com.example.projectdemo.domain.edsm.services;


import com.example.projectdemo.domain.edsm.dao.EdsmDAO;
import com.example.projectdemo.domain.edsm.dto.*;
import com.example.projectdemo.domain.edsm.enums.ApprovalStatus;
import com.example.projectdemo.domain.edsm.enums.EdsmStatus;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class EdsmFormService {

    @Autowired
    private EdsmDAO edao;

    @Autowired
    private EmployeesMapper empMapper;

    @Autowired
    private EmployeesService empService;

    @Autowired
    private EdsmFilesService edsmFilesService;

    //현재시간 가져오기
    public String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return now.format(formatter);
    }

    //전체 사원 출력( 사원 번호를 통한 모든 값 )
    public List<EmployeesDTO> allEmployeesList() {

        List<EmployeesDTO> list = empMapper.selectEmpAll();

        List<EmployeesDTO> empList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String empNum1 = list.get(i).getEmpNum();
            EmployeesDTO list_emp = empService.findByEmpNum(empNum1);
            empList.add(list_emp);
        }

        return empList;

    }
    //업무연락 저장 서비스
    @Transactional
    public boolean insertByEdsmDocument(int edsmFormId, String draftId, String title, String content, String retentionPeriod, String securityGrade, String writerPosition, String writerName, String approvalLine, MultipartFile[] fileAttachment) throws Exception {
    //전자결재 테이블 저장
        // 문서 DTO 생성 및 값 설정
        EdsmDocumentDTO edsmDocumentDTO = new EdsmDocumentDTO();
        edsmDocumentDTO.setEdsmFormId(edsmFormId);
        edsmDocumentDTO.setTitle(title);
        edsmDocumentDTO.setContent(content);
        edsmDocumentDTO.setRetentionPeriod(retentionPeriod);
        edsmDocumentDTO.setSecurityGrade(securityGrade);
        edsmDocumentDTO.setDrafterId(draftId);
        edsmDocumentDTO.setStatus(EdsmStatus.PROGRESS.getLabel());

        // 문서 삽입 후 자동 생성된 id를 bcdto에 설정 (selectKey 사용)
        int result1 = edao.insertByedsm_document(edsmDocumentDTO);
        int edsmDocumentId = edsmDocumentDTO.getId();

        //첨부파일 저장
        edsmFilesService.getFilesInsert(edsmDocumentId,edsmFormId,fileAttachment);

        //업무연락 테이블에 정보 저장
        EdsmBusinessContactDTO edsmBusinessContactDTO = new EdsmBusinessContactDTO();
        edsmBusinessContactDTO.setEdsmDocumentId(edsmDocumentId);
        edsmBusinessContactDTO.setDrafterId(draftId);
        edsmBusinessContactDTO.setTitie(title);
        edsmBusinessContactDTO.setContent(content);
       int result2 = edao.insertBybusinessContact(edsmBusinessContactDTO);

        // 결재라인 리스트 준비 :
        // 1) 기안자(작성자)는 결재라인의 첫번째에 추가하며, approvalNo는 1, status는 무조건 "승인"
        List<ApprovalLineDTO> finalApprovalList = new ArrayList<>();
        ApprovalLineDTO drafterApproval = new ApprovalLineDTO();
        drafterApproval.setDocumentId(edsmDocumentId);
        drafterApproval.setDrafterId(draftId);
        drafterApproval.setApproverId(draftId); // 기안자 자신이 첫번째 결재자로 고정됨
        drafterApproval.setApprovalNo(1);
        drafterApproval.setStatus(ApprovalStatus.APPROVED.getLabel());
        finalApprovalList.add(drafterApproval);

        // 2) JSON 문자열을 파싱하여 추가 결재자 목록 처리 (순번 2부터 부여)
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ApprovalLineDTO> additionalApprovals = mapper.readValue(approvalLine,
                    new com.fasterxml.jackson.core.type.TypeReference<List<ApprovalLineDTO>>() {});
            int seq = 2;
            for (ApprovalLineDTO dto : additionalApprovals) {
                dto.setDocumentId(edsmDocumentId);
                dto.setDrafterId(draftId);
                dto.setApprovalNo(seq++);
                // 추가 결재자는 기본적으로 "대기" 상태로 둘 수 있으며,
                // 필요에 따라 여기서 status 값을 변경할 수 있습니다.
                dto.setStatus(ApprovalStatus.PENDING.getLabel());
                finalApprovalList.add(dto);
            }
            // 최종 결재라인 리스트 전체를 DB에 저장
            for (ApprovalLineDTO alDto : finalApprovalList) {
                edao.insertByApprovalLine(alDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(result1==1 && result2==1){
            return true;
        }
        return false;
    }

    //지출결의서 저장 서비스
    @Transactional
    public boolean insertByCash (int edsmFormId, String draftId, String title, String content, String retentionPeriod, String securityGrade, String writerPosition, String writerName,String accountingDate, String bank, String bankAccount, String spenderId, String approvalLine, MultipartFile[] fileAttachment) throws Exception {
        // 문서 DTO 생성 및 값 설정
        EdsmDocumentDTO edsmDocumentDTO = new EdsmDocumentDTO();
        edsmDocumentDTO.setEdsmFormId(edsmFormId);
        edsmDocumentDTO.setTitle(title);
        edsmDocumentDTO.setContent(content);
        edsmDocumentDTO.setRetentionPeriod(retentionPeriod);
        edsmDocumentDTO.setSecurityGrade(securityGrade);
        edsmDocumentDTO.setDrafterId(draftId);
        edsmDocumentDTO.setStatus(EdsmStatus.PROGRESS.getLabel());

        // 문서 삽입 후 자동 생성된 id를 bcdto에 설정 (selectKey 사용)
        int result1 = edao.insertByedsm_document(edsmDocumentDTO);
        int edsmDocumentId = edsmDocumentDTO.getId();

        //첨부파일 저장
        edsmFilesService.getFilesInsert(edsmDocumentId,edsmFormId,fileAttachment);


        //지출결의서 테이블에 정보 저장
        EdsmCashDisbuVoucherDTO edsmCashDisbuVoucherDTO = new EdsmCashDisbuVoucherDTO();

        edsmCashDisbuVoucherDTO.setEdsmDocumentId(edsmDocumentId);
        edsmCashDisbuVoucherDTO.setDrafterId(draftId);
        edsmCashDisbuVoucherDTO.setTitle(title);
        edsmCashDisbuVoucherDTO.setContent(content);
        edsmCashDisbuVoucherDTO.setAccountingDate(accountingDate);
        edsmCashDisbuVoucherDTO.setSpenderId(spenderId);
        edsmCashDisbuVoucherDTO.setBank(bank);
        edsmCashDisbuVoucherDTO.setBankAccount(bankAccount);
        int result2 = edao.insertByCashDisbuVoucher(edsmCashDisbuVoucherDTO);
        System.out.println("회계 날짜 :" + accountingDate);
        System.out.println("계좌번호 : "+bankAccount);
        // 결재라인 리스트 준비 :
        // 1) 기안자(작성자)는 결재라인의 첫번째에 추가하며, approvalNo는 1, status는 무조건 "승인"
        List<ApprovalLineDTO> finalApprovalList = new ArrayList<>();
        ApprovalLineDTO drafterApproval = new ApprovalLineDTO();
        drafterApproval.setDocumentId(edsmDocumentId);
        drafterApproval.setDrafterId(draftId);
        drafterApproval.setApproverId(draftId); // 기안자 자신이 첫번째 결재자로 고정됨
        drafterApproval.setApprovalNo(1);
        drafterApproval.setStatus(ApprovalStatus.APPROVED.getLabel());
        finalApprovalList.add(drafterApproval);

        // 2) JSON 문자열을 파싱하여 추가 결재자 목록 처리 (순번 2부터 부여)
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ApprovalLineDTO> additionalApprovals = mapper.readValue(approvalLine,
                    new com.fasterxml.jackson.core.type.TypeReference<List<ApprovalLineDTO>>() {});
            int seq = 2;
            for (ApprovalLineDTO dto : additionalApprovals) {
                dto.setDocumentId(edsmDocumentId);
                dto.setDrafterId(draftId);
                dto.setApprovalNo(seq++);
                // 추가 결재자는 기본적으로 "대기" 상태로 둘 수 있으며,
                // 필요에 따라 여기서 status 값을 변경할 수 있습니다.
                dto.setStatus(ApprovalStatus.PENDING.getLabel());
                finalApprovalList.add(dto);
            }
            // 최종 결재라인 리스트 전체를 DB에 저장
            for (ApprovalLineDTO alDto : finalApprovalList) {
                edao.insertByApprovalLine(alDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(result1==1 && result2==1){
            return true;
        }
        return false;
    }


    //품의서 저장 서비스
    @Transactional
    public boolean insertLetterOfApproval(int edsmFormId, String drafterId, String title, String content, String retentionPeriod, String securityGrade, String writerPosition, String writerName, String expectedCost, String approvalLine, MultipartFile[] fileAttachment) throws Exception {
        // 문서 DTO 생성 및 값 설정
        EdsmDocumentDTO edsmDocumentDTO = new EdsmDocumentDTO();
        edsmDocumentDTO.setEdsmFormId(edsmFormId);
        edsmDocumentDTO.setTitle(title);
        edsmDocumentDTO.setContent(content);
        edsmDocumentDTO.setRetentionPeriod(retentionPeriod);
        edsmDocumentDTO.setSecurityGrade(securityGrade);
        edsmDocumentDTO.setDrafterId(drafterId);
        edsmDocumentDTO.setStatus(EdsmStatus.PROGRESS.getLabel());

        // 문서 삽입 후 자동 생성된 id를 bcdto에 설정 (selectKey 사용)
        int result1 = edao.insertByedsm_document(edsmDocumentDTO);
        int edsmDocumentId = edsmDocumentDTO.getId();

        //첨부파일 저장
        edsmFilesService.getFilesInsert(edsmDocumentId,edsmFormId,fileAttachment);


        //품의서 테이블에 정보 저장
        EdsmLetterOfApprovalDTO edsmLetterOfApprovalDTO = new EdsmLetterOfApprovalDTO();
        edsmLetterOfApprovalDTO.setEdsmDocumentId(edsmDocumentId);
        edsmLetterOfApprovalDTO.setDrafterId(drafterId);
        edsmLetterOfApprovalDTO.setTitle(title);
        edsmLetterOfApprovalDTO.setContent(content);
        edsmLetterOfApprovalDTO.setExpectedCost(expectedCost);
        int result2 = edao.insertByLetterOfApproval(edsmLetterOfApprovalDTO);

        // 결재라인 리스트 준비 :
        // 1) 기안자(작성자)는 결재라인의 첫번째에 추가하며, approvalNo는 1, status는 무조건 "승인"
        List<ApprovalLineDTO> finalApprovalList = new ArrayList<>();
        ApprovalLineDTO drafterApproval = new ApprovalLineDTO();
        drafterApproval.setDocumentId(edsmDocumentId);
        drafterApproval.setDrafterId(drafterId);
        drafterApproval.setApproverId(drafterId); // 기안자 자신이 첫번째 결재자로 고정됨
        drafterApproval.setApprovalNo(1);
        drafterApproval.setStatus(ApprovalStatus.APPROVED.getLabel());
        finalApprovalList.add(drafterApproval);

        // 2) JSON 문자열을 파싱하여 추가 결재자 목록 처리 (순번 2부터 부여)
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ApprovalLineDTO> additionalApprovals = mapper.readValue(approvalLine,
                    new com.fasterxml.jackson.core.type.TypeReference<List<ApprovalLineDTO>>() {});
            int seq = 2;
            for (ApprovalLineDTO dto : additionalApprovals) {
                dto.setDocumentId(edsmDocumentId);
                dto.setDrafterId(drafterId);
                dto.setApprovalNo(seq++);
                // 추가 결재자는 기본적으로 "대기" 상태로 둘 수 있으며,
                // 필요에 따라 여기서 status 값을 변경할 수 있습니다.
                dto.setStatus(ApprovalStatus.PENDING.getLabel());
                finalApprovalList.add(dto);
            }
            // 최종 결재라인 리스트 전체를 DB에 저장
            for (ApprovalLineDTO alDto : finalApprovalList) {
                edao.insertByApprovalLine(alDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(result1==1 && result2==1){
            return true;
        }
        return false;
    }

}
