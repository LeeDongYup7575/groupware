package com.example.projectdemo.domain.contact.controller;

import com.example.projectdemo.domain.contact.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    

}
