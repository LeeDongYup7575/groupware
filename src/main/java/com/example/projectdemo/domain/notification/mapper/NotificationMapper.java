package com.example.projectdemo.domain.notification.mapper;

import com.example.projectdemo.domain.notification.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    void insertNotification(Notification notification);

    List<Notification> getUnreadNotifications(String empNum);

    List<Notification> getAllNotifications(@Param("empNum") String empNum,
                                           @Param("limit") int limit,
                                           @Param("offset") int offset);

    void markAsRead(Integer id);

    void markAllAsRead(String empNum);

    int countUnreadNotifications(String empNum);
}