package com.example.notificationService.Dto.Response;

import com.example.notificationService.Entities.SmsRequest;
import lombok.Data;

@Data
public class GetAllSmsResponse {
    private Iterable<SmsRequest> allSmsResponse;
}
