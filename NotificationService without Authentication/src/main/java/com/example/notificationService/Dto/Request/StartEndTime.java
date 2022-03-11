package com.example.notificationService.Dto.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

import javax.validation.constraints.*;
import java.util.Date;

@Getter
public class StartEndTime {
    @JsonProperty(value = "start_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonProperty(value = "end_time")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @JsonProperty("page_no")
    @PositiveOrZero
    private int pageNo;

    @JsonProperty("page_size")
    @Positive
    private int pageSize;
}
