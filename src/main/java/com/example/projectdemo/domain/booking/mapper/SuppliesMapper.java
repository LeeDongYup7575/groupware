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

    boolean isSupplyAvailable(
            @Param("supplyId") Integer supplyId,
            @Param("quantity") Integer quantity,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeBookingId") Integer excludeBookingId);

    int getBookedQuantityAtTime(
            @Param("supplyId") Integer supplyId,
            @Param("dateTime") LocalDateTime dateTime);

    int getAvailableQuantityInPeriod(
            @Param("supplyId") Integer supplyId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 특정 시간 범위 내에 시작하는 비품 예약 조회
     */
    List<SuppliesBooking> findBookingsStartingBetween(
            @Param("startTimeFrom") LocalDateTime startTimeFrom,
            @Param("startTimeTo") LocalDateTime startTimeTo
    );
}