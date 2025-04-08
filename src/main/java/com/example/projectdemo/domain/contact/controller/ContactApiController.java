package com.example.projectdemo.domain.contact.controller;

import com.example.projectdemo.domain.contact.dto.EmployeeContactDTO;
import com.example.projectdemo.domain.contact.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
public class ContactApiController {
    private final ContactService contactService;

    @Autowired
    public ContactApiController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * 공유 주소록(사원 연락처) 조회
     */
    @GetMapping("/shared")
    public ResponseEntity<List<EmployeeContactDTO>> getSharedContacts(@RequestParam(required = false) String dept) {
        try {
            List<EmployeeContactDTO> sharedContacts;

            // 'all'도 null처럼 간주하여 전체 조회
            if (dept != null && !dept.isEmpty() && !dept.equals("all")) {
                sharedContacts = contactService.getSharedContactsByDepartment(dept);
            } else {
                sharedContacts = contactService.getSharedContacts();
            }

            if (sharedContacts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(sharedContacts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * 개인 주소록(사원 연락처) 조회
     */
//    @GetMapping("/personal")
}
