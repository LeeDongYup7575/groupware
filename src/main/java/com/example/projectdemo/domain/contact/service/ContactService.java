package com.example.projectdemo.domain.contact.service;

import com.example.projectdemo.domain.contact.dto.EmployeeContactDTO;
import com.example.projectdemo.domain.contact.mapper.ContactMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {
    @Autowired
    private ContactMapper contactMapper;

    /**
     * 모든 사원의 연락처 정보 조회
     */
    public List<EmployeeContactDTO> findAllEmpContacts() {
        return contactMapper.findAllEmpContacts();
    }
}
