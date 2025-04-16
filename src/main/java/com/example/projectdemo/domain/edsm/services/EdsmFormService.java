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

    /**
     * 현재 시간을 형식화된 문자열로 반환
     * @return 형식화된 현재 날짜 (yyyy.MM.dd)
     */
    public String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return now.format(formatter);
    }

    /**
     * 모든 사원 정보 리스트를 반환
     * @return 사원 정보 리스트
     */
    public List<EmployeesDTO> allEmployeesList() {
        List<EmployeesDTO> list = empMapper.selectEmpAll();
        List<EmployeesDTO> empList = new ArrayList<>();

        for (EmployeesDTO emp : list) {
            String empNum = emp.getEmpNum();
            EmployeesDTO employeeDetails = empService.findByEmpNum(empNum);
            empList.add(employeeDetails);
        }

        return empList;
    }

    /**
     * 기본 문서 정보와 결재라인을 생성하는 공통 메서드
     * @param edsmFormId 양식 ID
     * @param drafterId 기안자 ID
     * @param title 제목
     * @param content 내용
     * @param retentionPeriod 보존 기간
     * @param securityGrade 보안 등급
     * @param approvalLine 결재라인 JSON 문자열
     * @param fileAttachment 첨부파일
     * @return 생성된 문서 ID와 결과 상태를 담은 객체
     * @throws Exception 파일 처리나 JSON 파싱 오류
     */
    private DocumentProcessResult createBaseDocument(
            int edsmFormId, String drafterId, String title, String content,
            String retentionPeriod, String securityGrade, String approvalLine,
            MultipartFile[] fileAttachment) throws Exception {

        // 문서 DTO 생성 및 값 설정
        EdsmDocumentDTO edsmDocumentDTO = new EdsmDocumentDTO();
        edsmDocumentDTO.setEdsmFormId(edsmFormId);
        edsmDocumentDTO.setTitle(title);
        edsmDocumentDTO.setContent(content);
        edsmDocumentDTO.setRetentionPeriod(retentionPeriod);
        edsmDocumentDTO.setSecurityGrade(securityGrade);
        edsmDocumentDTO.setDrafterId(drafterId);
        edsmDocumentDTO.setStatus(EdsmStatus.PROGRESS.getLabel());

        // 문서 삽입 후 자동 생성된 id 획득
        int result = edao.insertByedsm_document(edsmDocumentDTO);
        int edsmDocumentId = edsmDocumentDTO.getId();

        // 첨부파일 저장
        edsmFilesService.getFilesInsert(edsmDocumentId, edsmFormId, fileAttachment);

        // 결재라인 처리
        boolean approvalResult = processApprovalLine(edsmDocumentId, drafterId, approvalLine);

        return new DocumentProcessResult(edsmDocumentId, result, approvalResult);
    }

    /**
     * 결재라인을 처리하는 공통 메서드
     * @param documentId 문서 ID
     * @param drafterId 기안자 ID
     * @param approvalLine 결재라인 JSON 문자열
     * @return 처리 성공 여부
     */
    private boolean processApprovalLine(int documentId, String drafterId, String approvalLine) {
        try {
            // 결재라인 리스트 준비
            List<ApprovalLineDTO> finalApprovalList = new ArrayList<>();

            // 1) 기안자는 결재라인의 첫번째에 추가 (approvalNo=1, status="승인")
            ApprovalLineDTO drafterApproval = new ApprovalLineDTO();
            drafterApproval.setDocumentId(documentId);
            drafterApproval.setDrafterId(drafterId);
            drafterApproval.setApproverId(drafterId);
            drafterApproval.setApprovalNo(1);
            drafterApproval.setStatus(ApprovalStatus.APPROVED.getLabel());
            finalApprovalList.add(drafterApproval);

            // 2) JSON 문자열을 파싱하여 추가 결재자 목록 처리 (순번 2부터 부여)
            ObjectMapper mapper = new ObjectMapper();
            List<ApprovalLineDTO> additionalApprovals = mapper.readValue(approvalLine,
                    new com.fasterxml.jackson.core.type.TypeReference<List<ApprovalLineDTO>>() {});

            int seq = 2;
            for (ApprovalLineDTO dto : additionalApprovals) {
                dto.setDocumentId(documentId);
                dto.setDrafterId(drafterId);
                dto.setApprovalNo(seq++);
                dto.setStatus(ApprovalStatus.PENDING.getLabel());
                finalApprovalList.add(dto);
            }

            // 최종 결재라인 리스트 전체를 DB에 저장
            for (ApprovalLineDTO alDto : finalApprovalList) {
                edao.insertByApprovalLine(alDto);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 업무연락 저장 서비스
     * @param edsmFormId 양식 ID
     * @param draftId 기안자 ID
     * @param title 제목
     * @param content 내용
     * @param retentionPeriod 보존 기간
     * @param securityGrade 보안 등급
     * @param writerPosition 작성자 직위
     * @param writerName 작성자 이름
     * @param approvalLine 결재라인 JSON 문자열
     * @param fileAttachment 첨부파일
     * @return 저장 성공 여부
     * @throws Exception 파일 처리나 JSON 파싱 오류
     */
    @Transactional
    public boolean insertByEdsmDocument(
            int edsmFormId, String draftId, String title, String content,
            String retentionPeriod, String securityGrade, String writerPosition,
            String writerName, String approvalLine, MultipartFile[] fileAttachment) throws Exception {

        // 기본 문서 생성
        DocumentProcessResult result = createBaseDocument(
                edsmFormId, draftId, title, content, retentionPeriod,
                securityGrade, approvalLine, fileAttachment);

        // 업무연락 테이블에 정보 저장
        EdsmBusinessContactDTO edsmBusinessContactDTO = new EdsmBusinessContactDTO();
        edsmBusinessContactDTO.setEdsmDocumentId(result.getDocumentId());
        edsmBusinessContactDTO.setDrafterId(draftId);
        edsmBusinessContactDTO.setTitie(title);
        edsmBusinessContactDTO.setContent(content);
        int contactResult = edao.insertBybusinessContact(edsmBusinessContactDTO);

        return result.isSuccess() && contactResult == 1;
    }

    /**
     * 지출결의서 저장 서비스
     * @param edsmFormId 양식 ID
     * @param draftId 기안자 ID
     * @param title 제목
     * @param content 내용
     * @param retentionPeriod 보존 기간
     * @param securityGrade 보안 등급
     * @param writerPosition 작성자 직위
     * @param writerName 작성자 이름
     * @param accountingDate 회계 날짜
     * @param bank 은행
     * @param bankAccount 계좌번호
     * @param spenderId 지출자 ID
     * @param approvalLine 결재라인 JSON 문자열
     * @param fileAttachment 첨부파일
     * @return 저장 성공 여부
     * @throws Exception 파일 처리나 JSON 파싱 오류
     */
    @Transactional
    public boolean insertByCash(
            int edsmFormId, String draftId, String title, String content,
            String retentionPeriod, String securityGrade, String writerPosition, String writerName,
            String accountingDate, String bank, String bankAccount, String spenderId,
            String approvalLine, MultipartFile[] fileAttachment) throws Exception {

        // 기본 문서 생성
        DocumentProcessResult result = createBaseDocument(
                edsmFormId, draftId, title, content, retentionPeriod,
                securityGrade, approvalLine, fileAttachment);

        // 지출결의서 테이블에 정보 저장
        EdsmCashDisbuVoucherDTO edsmCashDisbuVoucherDTO = new EdsmCashDisbuVoucherDTO();
        edsmCashDisbuVoucherDTO.setEdsmDocumentId(result.getDocumentId());
        edsmCashDisbuVoucherDTO.setDrafterId(draftId);
        edsmCashDisbuVoucherDTO.setTitle(title);
        edsmCashDisbuVoucherDTO.setContent(content);
        edsmCashDisbuVoucherDTO.setAccountingDate(accountingDate);
        edsmCashDisbuVoucherDTO.setSpenderId(spenderId);
        edsmCashDisbuVoucherDTO.setBank(bank);
        edsmCashDisbuVoucherDTO.setBankAccount(bankAccount);
        int cashResult = edao.insertByCashDisbuVoucher(edsmCashDisbuVoucherDTO);

        return result.isSuccess() && cashResult == 1;
    }

    /**
     * 품의서 저장 서비스
     * @param edsmFormId 양식 ID
     * @param drafterId 기안자 ID
     * @param title 제목
     * @param content 내용
     * @param retentionPeriod 보존 기간
     * @param securityGrade 보안 등급
     * @param writerPosition 작성자 직위
     * @param writerName 작성자 이름
     * @param expectedCost 예상 비용
     * @param approvalLine 결재라인 JSON 문자열
     * @param fileAttachment 첨부파일
     * @return 저장 성공 여부
     * @throws Exception 파일 처리나 JSON 파싱 오류
     */
    @Transactional
    public boolean insertLetterOfApproval(
            int edsmFormId, String drafterId, String title, String content,
            String retentionPeriod, String securityGrade, String writerPosition, String writerName,
            String expectedCost, String approvalLine, MultipartFile[] fileAttachment) throws Exception {

        // 기본 문서 생성
        DocumentProcessResult result = createBaseDocument(
                edsmFormId, drafterId, title, content, retentionPeriod,
                securityGrade, approvalLine, fileAttachment);

        // 품의서 테이블에 정보 저장
        EdsmLetterOfApprovalDTO edsmLetterOfApprovalDTO = new EdsmLetterOfApprovalDTO();
        edsmLetterOfApprovalDTO.setEdsmDocumentId(result.getDocumentId());
        edsmLetterOfApprovalDTO.setDrafterId(drafterId);
        edsmLetterOfApprovalDTO.setTitle(title);
        edsmLetterOfApprovalDTO.setContent(content);
        edsmLetterOfApprovalDTO.setExpectedCost(expectedCost);
        int letterResult = edao.insertByLetterOfApproval(edsmLetterOfApprovalDTO);

        return result.isSuccess() && letterResult == 1;
    }

    /**
     * 문서 처리 결과를 담는 내부 클래스
     */
    private class DocumentProcessResult {
        private final int documentId;
        private final int documentResult;
        private final boolean approvalResult;

        public DocumentProcessResult(int documentId, int documentResult, boolean approvalResult) {
            this.documentId = documentId;
            this.documentResult = documentResult;
            this.approvalResult = approvalResult;
        }

        public int getDocumentId() {
            return documentId;
        }

        public boolean isSuccess() {
            return documentResult == 1 && approvalResult;
        }
    }
}