package com.taskasync.notificationservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TaskCommentNotificationDto implements TaskNotification {
    private Long taskId;
    private String title;
    private Long creatorId; // Task creator, not commenter
    private String creatorUsername;
    private LocalDateTime notificationDateTime;
    private Map<Long, List<CommentMessage>> userNotifications = new HashMap<>();

    @Override
    public EventType getEventType() {
        return EventType.TASK_COMMENT_ADDED;
    }

    public void addComment(Long userId, CommentMessage comment) {
        userNotifications.computeIfAbsent(userId, k -> new ArrayList<>()).add(comment);
    }

    @Data
    public static class CommentMessage {
        private Long commenterId;
        private String commenterUsername;
        private String commentText;
        private LocalDateTime commentDateTime;

        public CommentMessage(Long commenterId, String commenterUsername, String commentText, LocalDateTime commentDateTime) {
            this.commenterId = commenterId;
            this.commenterUsername = commenterUsername;
            this.commentText = commentText;
            this.commentDateTime = commentDateTime;
        }
    }
}