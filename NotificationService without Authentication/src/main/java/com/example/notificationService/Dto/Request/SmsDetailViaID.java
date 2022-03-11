package com.example.notificationService.Dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.Positive;


@Getter
public class SmsDetailViaID {
    @Positive
    @JsonProperty("request_id")
    private Integer requestId;
}
