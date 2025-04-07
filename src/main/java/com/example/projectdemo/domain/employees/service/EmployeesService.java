package com.example.projectdemo.domain.employees.service;

import com.example.projectdemo.config.PasswordEncoder;
import com.example.projectdemo.domain.auth.service.EmailService;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.dto.EmployeesInfoUpdateDTO;
import com.example.projectdemo.domain.employees.mapper.DepartmentsMapper;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.mapper.PositionsMapper;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
    private PasswordEncoder passwordEncoder;

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String SPECIAL_CHARS = "^$*.[]{}()?-\"!@#%&/\\,><':;|_~`+=";
    private static final String ALL_CHARS = CHAR_LOWER + CHAR_UPPER + NUMBER + SPECIAL_CHARS;
//private static final String ALL_CHARS = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private static final SecureRandom random = new SecureRandom();
    @Autowired
    private PositionsMapper positionsMapper;

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

        // 직원 dto 부서 이름 설정
        String depName = departmentsMapper.findById(employee.getDepId()).getName();
        employee.setDepartmentName(depName);

        // 직원 dto 직급 이름 설정
        String posTitle = positionsMapper.findById(employee.getPosId()).getTitle();
        employee.setPositionTitle(posTitle);

        return employee;
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

        int updated = employeeMapper.updateRegistrationStatus(
                empNum,
                true, // registered
                encodedPassword,
                profileImgUrl,
                phone,
                gender,
                true // tempPassword
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

}