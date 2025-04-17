package com.example.projectdemo.domain.contact.mapper;

import com.example.projectdemo.domain.contact.dto.EmployeeContactDTO;
import com.example.projectdemo.domain.contact.dto.PersonalContactDTO;
import com.example.projectdemo.domain.contact.dto.RoundcubeContactDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContactMapper {
    // 주소록 id로 roundcube_contact_id 조회
    Integer findRoundcubeContactIdById(@Param("id") int id);

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

    // personal_contacts.id 리스트 → roundcube_contact_id 리스트 조회
    List<Integer> findRoundcubeContactIdsByPersonalIds(@Param("ids") List<Integer> ids);

    // roundcube.contacts 삭제
    void deleteRoundcubeContactsByIds(@Param("ids") List<Integer> rcContactIds);

    // 개인주소록 연락처 삭제
    void deleteContactsByIds(@Param("ids") List<Integer> ids);

    // 개인주소록 연락처 수정
    void updatePersonalContact(PersonalContactDTO contact);

    // roundcube 연락처 수정
    void updateRoundcubeContact(RoundcubeContactDTO contact);

    // 공유 주소록(사원 연락처) 검색
    List<EmployeeContactDTO> searchSharedContacts(@Param("queryPattern") String queryPattern);

    // 개인 주소록 검색 (로그인한 사원의 개인 주소록)
    List<PersonalContactDTO> searchPersonalContacts(@Param("empId") Integer empId,
                                                    @Param("queryPattern") String queryPattern);

    // 사원 연락처 정보 roundcube 공유 주소록에 추가
    int insertSharedContact(RoundcubeContactDTO contact);

    // 사원정보 변경 시 Roundcube 글로벌 주소록을 업데이트
    void updateSharedContact(RoundcubeContactDTO contact);

}
