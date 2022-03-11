package com.example.notificationService.Dto.Response;

import lombok.Data;

@Data
public class MessageSentSuccess {
    private Integer request_id;
    private String comments;
}
