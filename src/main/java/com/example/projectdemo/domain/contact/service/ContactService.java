package com.example.projectdemo.domain.contact.service;

import com.example.projectdemo.domain.contact.dto.EmployeeContactDTO;
import com.example.projectdemo.domain.contact.dto.PersonalContactDTO;
import com.example.projectdemo.domain.contact.dto.RoundcubeContactDTO;
import com.example.projectdemo.domain.contact.mapper.ContactMapper;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.mapper.DepartmentsMapper;
import com.example.projectdemo.domain.employees.mapper.EmployeesMapper;
import com.example.projectdemo.domain.employees.mapper.PositionsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContactService {
    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private PositionsMapper positionsMapper;

    @Autowired
    private DepartmentsMapper departmentsMapper;

    /**
     * 공유주소록(사원연락처) 조회
     */
    public List<EmployeeContactDTO> getSharedContacts() {
        return contactMapper.findAllEmpContacts();
    }

    /**
     * 부서별 공유주소록(사원연락처) 조회
     */
    public List<EmployeeContactDTO> getSharedContactsByDepartment(String depName){
        return contactMapper.findEmpContactsByDepartment(depName);
    }

    /**
     * 개인 주소록(사원 연락처) 조회
     */
    public List<PersonalContactDTO> getPersonalContactsByEmpId(Integer empId) {
        return contactMapper.findPersonalContactsByEmpId(empId);
    }

    /**
     * 개인 주소록에 연락처 추가
     */
    @Transactional
    public void addPersonalContact(Integer empId, PersonalContactDTO contact) {
        contact.setEmpId(empId);
        contactMapper.insertPersonalContact(contact);

        // Roundcube 주소록에도 등록 (이름만 필수, 나머지는 선택)
        registerToPersonalRoundcubeAddressBook(contact);
    }

    /**
     * Roundcube 개인 주소록에 추가
     */
    public void registerToPersonalRoundcubeAddressBook(PersonalContactDTO dto) {

        String vcard = String.join("\n",
                "BEGIN:VCARD",
                "VERSION:3.0",
                "N:;" + dto.getName() + ";;;",
                "FN:" + dto.getName(),
                dto.getEmail() != null ? "EMAIL:" + dto.getEmail() : null,
                dto.getPhone() != null ? "TEL:" + dto.getPhone() : null,
                dto.getMemo() != null ? "NOTE:" + dto.getMemo() : null,
                "END:VCARD"
        ).replaceAll("(?m)^null\\n?", ""); // null 줄 제거

        String words = dto.getName() + " " +
                (dto.getEmail() != null ? dto.getEmail() : "") + " " +
                (dto.getPhone() != null ? dto.getPhone().replace("-", "") : "");

        RoundcubeContactDTO contactDTO = RoundcubeContactDTO.builder()
                    .name(dto.getName())
                    .email(dto.getEmail() != null ? dto.getEmail() : "")
                    .firstname(dto.getName())
                    .vcard(vcard)
                    .words(words.trim())
                    .empId(dto.getEmpId()) // 그룹웨어 사용자 ID
                    .build();

        contactMapper.insertPersonalRoundcubeContact(contactDTO);


    }


    /**
     * 개인 주소록 연락처 삭제
     */
    public void deletePersonalContacts(List<Integer> ids) {
        contactMapper.deleteContactsByIds(ids);
    }

    /**
     * 개인 주소록 연락처 수정
     */
    public void updatePersonalContact(PersonalContactDTO dto) {
        contactMapper.updatePersonalContact(dto);
    }

    /**
     * 연락처 검색
     */
    public Map<String, Object> searchContacts(Integer empId, String query) {
        // 검색어를 LIKE 패턴으로 가공 (예: "%검색어%")
        String queryPattern = "%" + query + "%";
        // 공유 주소록 검색 (사원 연락처)
        List<EmployeeContactDTO> sharedResults = contactMapper.searchSharedContacts(queryPattern);
        // 개인 주소록 검색 (로그인한 사용자)
        List<PersonalContactDTO> personalResults = contactMapper.searchPersonalContacts(empId, queryPattern);

        Map<String, Object> result = new HashMap<>();
        result.put("shared", sharedResults);
        result.put("personal", personalResults);
        return result;
    }

    /**
     * 사원 연락처 정보 roundcube 공유 주소록에 추가
     */
    public void registerToSharedAddressBook(String name, String email, String phone, String positionTitle, String departmentName) {

        String vcard = String.join("\n",
                "BEGIN:VCARD",
                "VERSION:3.0",
                "N:;" + name + ";;;",
                "FN:" + name,
                "EMAIL;TYPE=work:" + email,
                "TEL;TYPE=cell:" + phone,
                "TITLE:" + positionTitle,
                "X-DEPARTMENT:" + departmentName,
                "END:VCARD"
        );


        String words = name + " " + email + " " + phone.replace("-", "");

        RoundcubeContactDTO contact = RoundcubeContactDTO.builder()
                .name(name)
                .email(email)
                .firstname(name)
                .vcard(vcard)
                .words(words)
                .build();

        contactMapper.insertSharedContact(contact);

    }
}
