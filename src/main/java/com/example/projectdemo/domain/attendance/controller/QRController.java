package com.example.projectdemo.domain.attendance.controller;

import com.example.projectdemo.domain.auth.jwt.JwtTokenUtil;
import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
import com.example.projectdemo.domain.employees.service.EmployeeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;


@Controller
@RequestMapping("/qr")
public class QRController {
    private static final Logger logger = LoggerFactory.getLogger(QRController.class);

    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private EmployeeService employeeService;

    private static final int QR_CODE_SIZE = 250;


    @GetMapping("/qrcheck")
    public String showQRCodePage(
            Model model,
            HttpServletRequest request
    ) {
        try {
            // JWT 필터에서 설정한 사원번호 추출
            String empNum = (String) request.getAttribute("empNum");

            if (empNum == null) {
                logger.warn("No employee number found in request attributes");
                return "redirect:/auth/login";
            }

            // 사원번호로 직원 정보 조회
            EmployeesDTO employee = employeeService.findByEmpNum(empNum);

            if (employee == null) {
                logger.warn("No employee found for empNum: {}", empNum);
                return "redirect:/auth/login";
            }

            model.addAttribute("employee", employee);
            model.addAttribute("currentTime", LocalDateTime.now());
            return "attendance/qrcheck";
        } catch (Exception e) {
            logger.error("Error accessing QR check page", e);
            return "error";
        }
    }


    @GetMapping(value = "/generate-qr", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> generateQRCode(
            @RequestParam(required = false, defaultValue = "NORMAL") String type,
            HttpServletRequest request) {
        try {
            logger.debug("Authorization header: {}", request.getHeader("Authorization"));

            String empNum = (String) request.getAttribute("empNum");
            logger.debug("Extracted empNum: {}", empNum);

            if (empNum == null && request.getParameter("token") != null) {
                String token = request.getParameter("token");
                try {
                    if (jwtUtil.validateToken(token)) {
                        empNum = jwtUtil.getEmpNumFromToken(token);
                        logger.debug("Extracted empNum from request parameter token: {}", empNum);
                    }
                } catch (Exception e) {
                    logger.error("Error validating token from parameter", e);
                }
            }

            if (empNum == null) {
                logger.warn("No employee number found. Authentication required.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // 사원번호로 직원 정보 조회
            EmployeesDTO employee = employeeService.findByEmpNum(empNum);
            logger.debug("Retrieved employee info: {}", employee != null ? employee.getEmpNum() : "none");

            if (employee == null) {
                logger.warn("No employee found for empNum: {}", empNum);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            if (type.contains(":")) {
                type = type.split(":")[0];
            }

            String qrToken = jwtUtil.generateQRToken(employee.getEmpNum(), type);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrToken, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(pngData);

        } catch (WriterException | IOException e) {
            logger.error("Error generating QR code", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/select-attendance-type")
    public String selectAttendanceType(
            @RequestParam String type,
            Model model,
            HttpServletRequest request
    ) {
        model.addAttribute("selectedType", type);
        return showQRCodePage(model, request);
    }
}