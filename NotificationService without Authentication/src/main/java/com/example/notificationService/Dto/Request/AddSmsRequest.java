package com.example.notificationService.Dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
public class AddSmsRequest {
    @JsonProperty("phone_number")
    @Pattern(regexp="[6-9][0-9]{9}",message = "PhoneNumber must be of valid 10 digit number")
    private String phoneNumber;

    @JsonProperty("message")
    @NotBlank(message = "Message Field cannot be Empty")
    private String message;
}
