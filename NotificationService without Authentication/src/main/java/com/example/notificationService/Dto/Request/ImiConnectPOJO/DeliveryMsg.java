package com.example.notificationService.Dto.Request.ImiConnectPOJO;


import lombok.Data;

import java.util.ArrayList;
@Data

public class DeliveryMsg {
    private String deliverychannel;
    private Channels channels;
    private ArrayList<Destination> destination;
}
