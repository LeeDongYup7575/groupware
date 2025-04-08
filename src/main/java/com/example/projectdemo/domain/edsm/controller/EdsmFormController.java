package com.example.projectdemo.domain.edsm.controller;


import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.edsm.dao.EdsmDAO;
import com.example.projectdemo.domain.edsm.dto.ApprovalLineDTO;
import com.example.projectdemo.domain.edsm.dto.EdsmBusinessContactDTO;
import com.example.projectdemo.domain.edsm.dto.EdsmDocumentDTO;
import com.example.projectdemo.domain.edsm.dto.EdsmLetterOfApprovalDTO;
import com.example.projectdemo.domain.edsm.enums.ApprovalStatus;
import com.example.projectdemo.domain.edsm.enums.EdsmStatus;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.service.EmployeesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/edsmForm")
public class EdsmFormController {


    @Autowired
    private HttpSession session;

    @Autowired
    private JwtTokenUtil jwt;

    @Autowired
    EdsmDAO edao;

    @Autowired
    private EmployeesService employeeService;

    @Autowired
    private EmployeesMapper employeesMapper;

    public String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return now.format(formatter);
    }

    // 기안문서 컨트롤러
    //작성하기 버튼 컨트롤러
    @RequestMapping("/input")
    public String create(Model model, HttpServletRequest request) {

        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");

        if (empNum == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        model.addAttribute("employee", employee);

        return "edsm/input";
    }
    //업무연락
    @RequestMapping("/1001")
    public String inputBc(Model model, HttpServletRequest request,@RequestParam(value="documentType", required=false) String documentType) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");

        if (empNum == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        model.addAttribute("currentTime", getCurrentTime());

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        model.addAttribute("employee", employee);


        // documentType 값이 없으면 기본값 설정
        if (documentType == null) {
            documentType = "1001"; // 예를 들어 "업무연락"에 해당하는 값
        }
        model.addAttribute("documentType", documentType);

        List<EmployeesDTO> list = employeesMapper.selectEmpAll();
        List<EmployeesDTO> empList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String empNum1 = list.get(i).getEmpNum();
            EmployeesDTO list_emp = employeeService.findByEmpNum(empNum1);
            empList.add(list_emp);
        }

        // 사번(empNum)을 기준으로 오름차순 정렬 (Java 8 이상 사용)
        empList.sort(Comparator.comparing(EmployeesDTO::getEmpNum).reversed());
        model.addAttribute("list_emp", empList);





        return "edsm/edsmForm/businessContact";
    }

    //지출결의서
    @RequestMapping("/1002")
    public String inputCdv(Model model, HttpServletRequest request,@RequestParam(value="documentType", required=false) String documentType) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");

        if (empNum == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        model.addAttribute("currentTime", getCurrentTime());

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { // 예외 처리
            return "redirect:/edsm/main";
        }
        model.addAttribute("currentTime", getCurrentTime());
        model.addAttribute("employee", employee);



        // documentType 값이 없으면 기본값 설정
        if (documentType == null) {
            documentType = "1002"; // 예를 들어 "업무연락"에 해당하는 값
        }
        model.addAttribute("documentType", documentType);

        List<EmployeesDTO> list = employeesMapper.selectEmpAll();
        List<EmployeesDTO> empList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String empNum1 = list.get(i).getEmpNum();
            EmployeesDTO list_emp = employeeService.findByEmpNum(empNum1);
            empList.add(list_emp);
        }

        // 사번(empNum)을 기준으로 오름차순 정렬 (Java 8 이상 사용)
        empList.sort(Comparator.comparing(EmployeesDTO::getEmpNum).reversed());
        model.addAttribute("list_emp", empList);

        return "edsm/edsmForm/cashDisbuVoucher";
    }


    //품의서
    @RequestMapping("/1003")
    public String inputLoa(Model model, HttpServletRequest request,@RequestParam(value="documentType", required=false) String documentType) {
        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");

        if (empNum == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        model.addAttribute("currentTime", getCurrentTime());

        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);

        if (employee == null) { // 예외 처리
            return "redirect:/edsm/main";
        }

        model.addAttribute("employee", employee);

        if (documentType == null) {
            documentType = "1003";
        }
        model.addAttribute("documentType", documentType);



        List<EmployeesDTO> list = employeesMapper.selectEmpAll();
        List<EmployeesDTO> empList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String empNum1 = list.get(i).getEmpNum();
            EmployeesDTO list_emp = employeeService.findByEmpNum(empNum1);
            empList.add(list_emp);
        }

        // 사번(empNum)을 기준으로 오름차순 정렬 (Java 8 이상 사용)
        empList.sort(Comparator.comparing(EmployeesDTO::getEmpNum).reversed());
        model.addAttribute("list_emp", empList);


        return "edsm/edsmForm/letterOfApproval";
    }

    //업무연락 제출 컨트롤러
    @PostMapping("/business_submit")
    public String bcSubmit(@RequestParam("documentType") int edsmFormId,
                           @RequestParam("drafterId") String draftId,
                           @RequestParam("title") String title,
                           @RequestParam("content") String content,
                           @RequestParam("retentionPeriod") String retentionPeriod,
                           @RequestParam("securityGrade") String securityGrade,
                           @RequestParam("drafterPosition") String writerPosition,
                           @RequestParam("drafterName") String writerName,
                           @RequestParam("approvalLine") String approvalLine, // JSON 문자열 (추가 결재자들)
                           @RequestParam("fileAttachment") MultipartFile[] fileAttachment,
                           Model model, HttpServletRequest request) {

        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return "redirect:/edsm/main";
        }
        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);
        if (employee == null) {
            return "redirect:/edsm/main";
        }
        model.addAttribute("employee", employee);

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
        edao.insertByedsm_document(edsmDocumentDTO);
        int edsmDocumentId = edsmDocumentDTO.getId();

        //업무연락 테이블에 정보 저장
        EdsmBusinessContactDTO edsmBusinessContactDTO = new EdsmBusinessContactDTO();
        edsmBusinessContactDTO.setEdsmDocumentId(edsmDocumentId);
        edsmBusinessContactDTO.setDrafterId(draftId);
        edsmBusinessContactDTO.setTitie(title);
        edsmBusinessContactDTO.setContent(content);
        edao.insertBybusinessContact(edsmBusinessContactDTO);


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



        return "redirect:/edsm/main";
    }


    //품의서 제출 컨트롤러
    @PostMapping("/letter_submit")
    public String letterSubmit(@RequestParam("documentType") int edsmFormId,
                                  @RequestParam("drafterId") String draftId,
                                  @RequestParam("title") String title,
                                  @RequestParam("content") String content,
                                  @RequestParam("retentionPeriod") String retentionPeriod,
                                  @RequestParam("securityGrade") String securityGrade,
                                  @RequestParam("drafterPosition") String writerPosition,
                                  @RequestParam("drafterName") String writerName,
                                  @RequestParam("expectedCost") String expectedCost,
                                  @RequestParam("approvalLine") String approvalLine, // JSON 문자열 (추가 결재자들)
                                  @RequestParam("fileAttachment") MultipartFile[] fileAttachment,
                                  Model model, HttpServletRequest request) {

        // JWT 필터에서 설정한 사원번호 추출
        String empNum = (String) request.getAttribute("empNum");
        if (empNum == null) {
            return "redirect:/edsm/main";
        }
        // 사원번호로 직원 정보 조회
        EmployeesDTO employee = employeeService.findByEmpNum(empNum);
        if (employee == null) {
            return "redirect:/edsm/main";
        }
        model.addAttribute("employee", employee);

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
        edao.insertByedsm_document(edsmDocumentDTO);
        int edsmDocumentId = edsmDocumentDTO.getId();

        //업무연락 테이블에 정보 저장
        EdsmLetterOfApprovalDTO edsmLetterOfApprovalDTO = new EdsmLetterOfApprovalDTO();
        edsmLetterOfApprovalDTO.setEdsmDocumentId(edsmDocumentId);
        edsmLetterOfApprovalDTO.setDrafterId(draftId);
        edsmLetterOfApprovalDTO.setTitle(title);
        edsmLetterOfApprovalDTO.setContent(content);
        edsmLetterOfApprovalDTO.setExpectedCost(expectedCost);

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

        return "redirect:/edsm/main";
    }

}
