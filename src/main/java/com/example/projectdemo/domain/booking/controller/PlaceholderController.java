package com.example.projectdemo.domain.booking.controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PlaceholderController {

    @GetMapping("/placeholder/{width}/{height}")
    public ResponseEntity<byte[]> getPlaceholderImage(
            @PathVariable int width,
            @PathVariable int height) {

        // 간단한 회의실 이미지 생성
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 배경 색상
        g.setColor(new Color(230, 240, 250));
        g.fillRect(0, 0, width, height);

        // 테두리
        g.setColor(new Color(70, 130, 180));
        g.drawRect(0, 0, width-1, height-1);

        // 회의실 아이콘 비슷한 형태 그리기
        g.setColor(new Color(70, 130, 180));
        g.fillRect(width/4, height/4, width/2, height/2);

        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(baos.toByteArray());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}