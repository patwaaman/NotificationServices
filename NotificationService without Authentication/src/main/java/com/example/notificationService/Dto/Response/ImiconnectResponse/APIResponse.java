package com.example.notificationService.Dto.Response.ImiconnectResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class APIResponse {
    @JsonProperty("response")
    private List<Response> response;
}
