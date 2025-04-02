package com.example.projectdemo.domain.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRoomDTO {
    private Integer id;
    private String name;
    private Integer capacity;
    private String location;
    private String description;
    private Boolean isAvailable;
}
