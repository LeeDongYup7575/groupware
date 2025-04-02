package com.example.projectdemo.domain.booking.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRoom {
    private Integer id;
    private String name;
    private Integer capacity;
    private String location;
    private String description;
    private Boolean isAvailable;
}