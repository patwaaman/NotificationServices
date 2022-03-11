package com.example.notificationService.Dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Getter
public class TextSearchRequest {

    @NotBlank(message = "Text Field Cannot be empty!! ")
    @JsonProperty("text")
    private String text;

    @PositiveOrZero
    @JsonProperty("page_no")
    private int pageNo;


    @Positive
    @JsonProperty("page_size")
    private int pageSize;
}
