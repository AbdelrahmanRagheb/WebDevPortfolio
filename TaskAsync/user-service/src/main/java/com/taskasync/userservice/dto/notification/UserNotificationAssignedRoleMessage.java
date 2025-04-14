package com.taskasync.userservice.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNotificationAssignedRoleMessage {
    private Long id;
    private String role;
    private String msg;


    @Override
    public String toString() {
        return "CustomNotification{" +
                "id=" + id +
                ", role='" + role + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}