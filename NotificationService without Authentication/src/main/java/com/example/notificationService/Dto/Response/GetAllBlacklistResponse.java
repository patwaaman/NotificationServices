package com.example.notificationService.Dto.Response;

import com.example.notificationService.Entities.BlacklistDB;
import lombok.Data;

@Data
public class GetAllBlacklistResponse {
    private Iterable<BlacklistDB> allBlacklistResponse;
}
