package com.example.projectdemo.domain.employees.service;

import com.example.projectdemo.config.PasswordEncoder;
import com.example.projectdemo.domain.auth.service.EmailService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesInfoUpdateDTO;
import com.example.projectdemo.domain.employees.mapper.DepartmentsMapper;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.mapper.PositionsMapper;
import com.example.projectdemo.domain.mail.service.MailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import net.crizin.KoreanCharacter;
import net.crizin.KoreanRomanizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeesService {

    @Autowired
    private EmployeesMapper employeeMapper;

    @Autowired
    private DepartmentsMapper departmentsMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PositionsMapper positionsMapper;

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String SPECIAL_CHARS = "^$*.[]{}()?-\"!@#%&/\\,><':;|_~`+=";
    private static final String ALL_CHARS = CHAR_LOWER + CHAR_UPPER + NUMBER + SPECIAL_CHARS;
    private static final SecureRandom random = new SecureRandom();

    /**
     * 모든 직원 목록 조회
     * @return 모든 직원 DTO 목록
     */
    public List<EmployeesDTO> getAllEmployees() {
        try {
            List<EmployeesDTO> employees = employeeMapper.selectEmpAll();

            // 필요한 경우 부서와 직급 정보 추가적으로 설정
            for (EmployeesDTO employee : employees) {
                if (employee.getDepId() != null) {
                    String depName = departmentsMapper.findById(employee.getDepId()).getName();
                    employee.setDepartmentName(depName);
                }

                if (employee.getPosId() != null) {
                    String posTitle = positionsMapper.findById(employee.getPosId()).getTitle();
                    employee.setPositionTitle(posTitle);
                }
            }

            return employees;
        } catch (Exception e) {
            System.out.println("직원 전체 목록 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    /**
     * 필터를 적용하여 직원 목록 조회
     */
    public List<EmployeesDTO> getEmployeesWithFilters(int page, int size, String searchTerm, Integer deptId, Integer posId) {
        int offset = (page - 1) * size;

        Map<String, Object> params = new HashMap<>();
        params.put("offset", offset);
        params.put("limit", size);
        params.put("searchTerm", searchTerm);
        params.put("deptId", deptId);
        params.put("posId", posId);

        List<EmployeesDTO> employees = employeeMapper.selectEmployeesWithFilters(params);

        // Fetch additional data for each employee
        for (EmployeesDTO employee : employees) {
            enrichEmployeeData(employee);
        }

        return employees;
    }

    /**
     * 필터를 적용하여 직원 수 조회
     */
    public int countEmployeesWithFilters(String searchTerm, Integer deptId, Integer posId) {
        Map<String, Object> params = new HashMap<>();
        params.put("searchTerm", searchTerm);
        params.put("deptId", deptId);
        params.put("posId", posId);

        return employeeMapper.countEmployeesWithFilters(params);
    }

    /**
     * 이미 회원가입한 직원인지 확인
     */
    public boolean isAlreadyRegistered(String empNum) {
        EmployeesDTO employee = employeeMapper.findByEmpNum(empNum);
        return employee != null && employee.isRegistered();
    }

    /**
     * 직원 정보 검증 (회원가입 전 확인)
     */
    public EmployeesDTO verifyEmployeeForRegistration(String empNum, String name, String email, String ssn) {
        // 사원번호, 이름, 이메일, 주민번호로 직원 조회
        return employeeMapper.findByEmpNumAndNameAndEmailAndSsn(empNum, name, email, ssn);
    }

    /**
     * 사번으로 직원 찾기
     */
    public EmployeesDTO findByEmpNum(String empNum) {
        EmployeesDTO employee = employeeMapper.findByEmpNum(empNum);
        if (employee != null) {
            enrichEmployeeData(employee);
        }
        return employee;
    }

    /**
     * ID로 직원 찾기
     */
    public EmployeesDTO findById(Integer id) {
        EmployeesDTO employee = employeeMapper.findById(id);
        if (employee != null) {
            enrichEmployeeData(employee);
        }
        return employee;
    }

    /**
     * 직원 데이터 보강 (부서명, 직급명 등)
     */
    private void enrichEmployeeData(EmployeesDTO employee) {
        // 직원 dto 부서 이름 설정
        String depName = departmentsMapper.findById(employee.getDepId()).getName();
        employee.setDepartmentName(depName);

        // 직원 dto 직급 이름 설정
        String posTitle = positionsMapper.findById(employee.getPosId()).getTitle();
        employee.setPositionTitle(posTitle);
    }

    /**
     * 회원가입 및 임시 비밀번호 생성
     */
    @Transactional
    public String register(String empNum, String profileImgUrl, String phone, @NotBlank(message = "성별은 필수입니다") String gender) {
        EmployeesDTO employee = employeeMapper.findByEmpNum(empNum);
        if (employee == null) {
            throw new RuntimeException("등록되지 않은 직원입니다.");
        }

        // 임시 비밀번호 생성
        String tempPassword = generateTempPassword();
        String encodedPassword = passwordEncoder.encode(tempPassword);

        // 이메일 생성
        String internalEmail = generateInternalEmail(employee.getName());

        // 사내 이메일 계정 등록
        mailService.registerMailAccount(internalEmail, tempPassword);

        int updated = employeeMapper.updateRegistrationStatus(
                empNum,
                true, // registered
                encodedPassword,
                profileImgUrl,
                phone,
                gender,
                true, // tempPassword
                internalEmail
        );

        if (updated <= 0) {
            throw new RuntimeException("회원 정보 업데이트에 실패했습니다.");
        }

        // 이메일 발송
        emailService.sendTempPasswordEmail(employee.getEmail(), tempPassword);

        return tempPassword;
    }

    /**
     * 비밀번호 재설정
     */
    @Transactional
    public void resetPassword(String email) {
        EmployeesDTO employee = employeeMapper.findByEmail(email);
        if (employee == null) {
            throw new RuntimeException("해당 이메일을 가진 직원을 찾을 수 없습니다.");
        }

        // 임시 비밀번호 생성
        String tempPassword = generateTempPassword();
        String encodedPassword = passwordEncoder.encode(tempPassword);

        // 비밀번호 업데이트
        int updated = employeeMapper.updatePassword(employee.getEmpNum(), encodedPassword, true);

        // 메일 비밀번호 업데이트
        mailService.updateMailPassword(employee.getInternalEmail(), tempPassword);

        if (updated <= 0) {
            throw new RuntimeException("비밀번호 업데이트에 실패했습니다.");
        }
        // 이메일 발송
        emailService.sendTempPasswordEmail(employee.getEmail(), tempPassword);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void updatePassword(String empNum, String newPassword) {
        EmployeesDTO employee = employeeMapper.findByEmpNum(empNum);
        if (employee == null) {
            throw new RuntimeException("등록되지 않은 직원입니다.");
        }

        // 비밀번호 업데이트
        String encodedPassword = passwordEncoder.encode(newPassword);
        int updated = employeeMapper.updatePassword(empNum, encodedPassword, false);

        if (updated <= 0) {
            throw new RuntimeException("비밀번호 업데이트에 실패했습니다.");
        }

        // 메일 비밀번호 업데이트
        mailService.updateMailPassword(employee.getInternalEmail(), newPassword);
    }

    /**
     * 마지막 로그인 시간 업데이트
     */
    @Transactional
    public void updateLastLogin(String empNum) {
        EmployeesDTO employee = employeeMapper.findByEmpNum(empNum);
        if (employee == null) {
            throw new RuntimeException("등록되지 않은 직원입니다.");
        }

        int updated = employeeMapper.updateLastLogin(empNum, LocalDateTime.now());

        if (updated <= 0) {
            throw new RuntimeException("로그인 시간 업데이트에 실패했습니다.");
        }
    }

    /**
     * 이메일로 직원 찾기
     */
    public EmployeesDTO findByEmail(String email) {
        return employeeMapper.findByEmail(email);
    }

    /**
     * 임시 비밀번호 생성
     */
    public String generateTempPassword() {
        // 비밀번호 길이 10자로 설정
        int length = 10;
        StringBuilder result = new StringBuilder(length);

        // 각 문자 유형이 최소 1개씩 포함되도록 보장
        result.append(CHAR_LOWER.charAt(random.nextInt(CHAR_LOWER.length())));
        result.append(CHAR_UPPER.charAt(random.nextInt(CHAR_UPPER.length())));
        result.append(NUMBER.charAt(random.nextInt(NUMBER.length())));
        result.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        // 나머지 문자는 모든 문자 집합에서 랜덤으로 선택
        for (int i = 4; i < length; i++) {
            result.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // 순서를 랜덤하게 섞기
        char[] tempPassword = result.toString().toCharArray();
        for (int i = 0; i < tempPassword.length; i++) {
            int j = random.nextInt(tempPassword.length);
            char temp = tempPassword[i];
            tempPassword[i] = tempPassword[j];
            tempPassword[j] = temp;
        }

        return new String(tempPassword);
    }

    /**
     * 마지막 로그인 시간 가져오기
     */
    public Map<String, String> selectLastLogin(String empNum) {
        LocalDateTime lastLogin = employeeMapper.selectLastLogin(empNum);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String lastLoginStr = lastLogin.format(formatter);

        Map<String, String> response = new HashMap<>();
        response.put("lastLogin", lastLoginStr);

        return response;
    }

    /**
     * 마이페이지 사용자 정보(프로필이미지, 전화번호, 개인이메일) 업데이트
     */
    public void updateEmpInfo(String empNum, String phone, String email, String profileImgUrl){
        EmployeesInfoUpdateDTO updatedEmp = new EmployeesInfoUpdateDTO(empNum, phone, email, profileImgUrl);
        employeeMapper.updateEmpInfo(updatedEmp);
    }

    /**
     * 사내 이메일 생성
     */
    public String generateInternalEmail(String name) {
        String romanized = KoreanRomanizer.romanize(name, KoreanCharacter.Type.NameTypical); // 예: "강윤진" → "Kang Yunjin"
        String[] parts = romanized.split(" ");

        String lastName = parts[0].toLowerCase(); // 예: kang
        String firstName = parts[1].toLowerCase(); // 예: yunjin

        String emailPrefix = firstName.charAt(0) + lastName; // 예: ykang

        if (mailService.emailExists(emailPrefix + "@techx.kro.kr")) {
            emailPrefix = firstName + lastName; // 예: yunjinkang
        }

        return emailPrefix + "@techx.kro.kr";
    }

    /**
     * 직원 정보 업데이트 (관리자용)
     */
    @Transactional
    public EmployeesDTO updateEmployee(EmployeesDTO employeeDTO) {
        // 업데이트 전 기존 데이터 조회
        EmployeesDTO existingEmployee = employeeMapper.findById(employeeDTO.getId());
        if (existingEmployee == null) {
            throw new RuntimeException("해당 직원을 찾을 수 없습니다.");
        }

        // 필수 필드 검증
        if (employeeDTO.getName() == null || employeeDTO.getName().trim().isEmpty()) {
            throw new RuntimeException("이름은 필수 입력 항목입니다.");
        }

        try {
            // 주요 정보만 업데이트
            int result = employeeMapper.updateEmployee(employeeDTO);

            if (result <= 0) {
                throw new RuntimeException("직원 정보 업데이트에 실패했습니다.");
            }

            // 업데이트된 정보 반환
            return findById(employeeDTO.getId());
        } catch (Exception e) {
            throw new RuntimeException("직원 정보 업데이트 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 직원 비활성화 (관리자용)
     */
    @Transactional
    public boolean deactivateEmployee(Integer id) {
        // 직원 존재 여부 확인
        EmployeesDTO employee = employeeMapper.findById(id);
        if (employee == null) {
            throw new RuntimeException("해당 직원을 찾을 수 없습니다.");
        }

        try {
            int result = employeeMapper.deactivateEmployee(id);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("직원 비활성화 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 직원 활성화 (관리자용)
     */
    @Transactional
    public boolean activateEmployee(Integer id) {
        // 직원 존재 여부 확인
        EmployeesDTO employee = employeeMapper.findById(id);
        if (employee == null) {
            throw new RuntimeException("해당 직원을 찾을 수 없습니다.");
        }

        try {
            int result = employeeMapper.activateEmployee(id);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("직원 활성화 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

}