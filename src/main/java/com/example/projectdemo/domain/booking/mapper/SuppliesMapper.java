package com.example.projectdemo.domain.booking.mapper;

import com.example.projectdemo.domain.booking.entity.Supplies;
import com.example.projectdemo.domain.booking.entity.SuppliesBooking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SuppliesMapper {

    List<Supplies> findAllSupplies();

    Supplies findSuppliesById(Integer id);

    List<SuppliesBooking> findAllSuppliesBookings();

    List<SuppliesBooking> findSuppliesBookingsByEmpNum(String empNum);

    List<SuppliesBooking> findSuppliesBookingsBySupplyId(Integer supplyId);

    List<SuppliesBooking> findSuppliesBookingsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    int insertSuppliesBooking(SuppliesBooking booking);

    int updateSuppliesBooking(SuppliesBooking booking);

    int cancelSuppliesBooking(Integer id);

    /**
     * 특정 시간대에 비품 예약 가능 여부 확인
     * 해당 시간대에 이미 예약된 수량과 총 수량을 비교하여 결정
     */
    boolean isSupplyAvailable(
            @Param("supplyId") Integer supplyId,
            @Param("quantity") Integer quantity,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeBookingId") Integer excludeBookingId);

    /**
     * 특정 시점에 예약된 비품 수량 조회
     */
    int getBookedQuantityAtTime(
            @Param("supplyId") Integer supplyId,
            @Param("dateTime") LocalDateTime dateTime);

    /**
     * 특정 기간에 예약된 최대 비품 수량 조회
     */
    int getMaxBookedQuantityInPeriod(
            @Param("supplyId") Integer supplyId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeBookingId") Integer excludeBookingId);

    /**
     * 특정 시간대에 사용 가능한 비품 수량 조회
     * 해당 비품의 총 수량에서 해당 시간대에 이미 예약된 수량을 뺀 값
     */
    int getAvailableQuantityInPeriod(
            @Param("supplyId") Integer supplyId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}