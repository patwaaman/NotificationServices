package com.example.notificationService.Dto.Response.ImiconnectResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Response {
    @JsonProperty("code")
    private String code;
    @JsonProperty("transid")
    private String transid;
    @JsonProperty("description")
    private String description;
    @JsonProperty("correlationid")
    private String correlationid;
}
