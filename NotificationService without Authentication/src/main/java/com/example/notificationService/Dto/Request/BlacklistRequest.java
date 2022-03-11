package com.example.notificationService.Dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

@Getter
public class BlacklistRequest implements Serializable {
     @JsonProperty("phone_numbers")
     @NotEmpty(message = "PhoneNumber list Must Not Be Empty !!")
     List<  @NotBlank @Pattern(regexp="[6-9][0-9]{9}",message = "PhoneNumber must be of valid 10 digit number") String> phoneNumbers;
}
