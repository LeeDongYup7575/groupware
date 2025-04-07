package com.example.projectdemo.domain.contact.controller;

import com.example.projectdemo.domain.contact.dto.EmployeeContactDTO;
import com.example.projectdemo.domain.contact.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping
    public String contact(Model model) {
        List<EmployeeContactDTO> employeeContacts = contactService.findAllEmpContacts();
        model.addAttribute("employeeContacts", employeeContacts);

        return "contact/contact";
    }
}
