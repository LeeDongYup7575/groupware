package com.example.projectdemo.domain.contact.mapper;

import com.example.projectdemo.domain.contact.dto.EmployeeContactDTO;
import com.example.projectdemo.domain.contact.dto.PersonalContactDTO;
import com.example.projectdemo.domain.contact.dto.RoundcubeContactDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContactMapper {
    // 모든 사원의 연락처 정보 조회
    List<EmployeeContactDTO> findAllEmpContacts();

    // 부서별 공유주소록(사원연락처) 조회
    List<EmployeeContactDTO> findEmpContactsByDepartment(@Param("depName") String depName);

    // 개인 주소록(사원 연락처) 조회
    List<PersonalContactDTO> findPersonalContactsByEmpId(@Param("empId") Integer empId);

    // 개인주소록에 연락처 추가
    void insertPersonalContact(@Param("contact") PersonalContactDTO contact);

    //  개인 주소록에 추가된 연락처 roundcube 주소록에 추가
    void insertPersonalRoundcubeContact(RoundcubeContactDTO contact);

    // 개인주소록 연락처 삭제
    void deleteContactsByIds(@Param("ids") List<Integer> ids);

    // 개인주소록 연락처 수정
    void updatePersonalContact(@Param("contact") PersonalContactDTO dto);

    // 공유 주소록(사원 연락처) 검색
    List<EmployeeContactDTO> searchSharedContacts(@Param("queryPattern") String queryPattern);

    // 개인 주소록 검색 (로그인한 사원의 개인 주소록)
    List<PersonalContactDTO> searchPersonalContacts(@Param("empId") Integer empId,
                                                    @Param("queryPattern") String queryPattern);

    // 사원 연락처 정보 roundcube 공유 주소록에 추가
    int insertSharedContact(RoundcubeContactDTO contact); // prefix 안 줌

}
