package com.example.notificationService.Dto.Response;

import com.example.notificationService.Entities.SmsRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class RequestIDSuccess {
    private SmsRequest smsRequestByID;
}
