package com.example.projectdemo.domain.projects.mapper;

import com.example.projectdemo.domain.projects.dto.ScheduleDTO;
import com.example.projectdemo.domain.projects.dto.ScheduleParticipantDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ScheduleMapper {

    List<ScheduleDTO> selectSchedulesByProject(@Param("projectId") Integer projectId);

    List<ScheduleDTO> selectSchedulesByEmployee(@Param("empNum") String empNum);

    List<ScheduleDTO> selectSchedulesByDateRange(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    ScheduleDTO selectScheduleById(@Param("id") Integer id);

    void insertSchedule(ScheduleDTO schedule);

    void updateSchedule(ScheduleDTO schedule);

    void deleteSchedule(@Param("id") Integer id);

    List<ScheduleParticipantDTO> selectScheduleParticipants(@Param("scheduleId") Integer scheduleId);

//    void insertScheduleParticipant(@Param("scheduleId") Integer scheduleId,
//                                   @Param("empNum") String empNum);
//
//    void updateParticipantStatus(@Param("scheduleId") Integer scheduleId,
//                                 @Param("empNum") String empNum,
//                                 @Param("status") String status);

    void insertScheduleParticipant(ScheduleParticipantDTO dto);

    void updateParticipantStatus(@Param("scheduleId") Integer scheduleId,
                                 @Param("empNum") String empNum,
                                 @Param("status") String status);

    void deleteScheduleParticipant(@Param("scheduleId") Integer scheduleId,
                                   @Param("empNum") String empNum);

    ScheduleParticipantDTO selectScheduleParticipant(@Param("scheduleId") Integer scheduleId,
                                                     @Param("empNum") String empNum);
}
