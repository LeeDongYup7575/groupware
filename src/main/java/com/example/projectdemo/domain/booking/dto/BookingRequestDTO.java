package com.example.projectdemo.domain.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    // 회의실 예약 필드
    private Integer roomId;
    private String title;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private Integer attendees;
    private String purpose;
    private List<SupplyItem> supplies;

    // 비품 예약 필드
    private Integer supplyId;
    private Integer quantity;

    // 내부 클래스로 비품 항목 정의
    public static class SupplyItem {
        private Integer id;
        private Integer quantity;

        // getters and setters
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    // List<SupplyItem> supplies 필드에 대한 getter 및 setter
    public List<SupplyItem> getSupplies() {
        return supplies;
    }

    public void setSupplies(List<SupplyItem> supplies) {
        this.supplies = supplies;
    }
}