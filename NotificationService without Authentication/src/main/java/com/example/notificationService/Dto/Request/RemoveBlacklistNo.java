package com.example.notificationService.Dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
public class RemoveBlacklistNo {
    @JsonProperty("phone_number")
    @Pattern(regexp="[6-9][0-9]{9}",message = "PhoneNumber must be of valid 10 digit number")
    private String phoneNumber;
}
