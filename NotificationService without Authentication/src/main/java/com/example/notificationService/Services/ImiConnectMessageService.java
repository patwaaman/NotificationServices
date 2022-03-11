package com.example.notificationService.Services;


import com.example.notificationService.Dao.SmsRequestRepository;
import com.example.notificationService.Dto.Request.ImiConnectPOJO.Channels;
import com.example.notificationService.Dto.Request.ImiConnectPOJO.DeliveryMsg;
import com.example.notificationService.Dto.Request.ImiConnectPOJO.Sms;
import com.example.notificationService.Dto.Request.ImiConnectPOJO.Destination;
import com.example.notificationService.Dto.Response.ImiconnectResponse.APIResponse;
import com.example.notificationService.Dto.Response.ImiconnectResponse.Response;
import com.example.notificationService.Entities.SmsRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImiConnectMessageService {
    @Autowired
    private SmsRequestRepository smsRequestRepository;

    Logger logger = LoggerFactory.getLogger(ImiConnectMessageService.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${URL}")
    private String URL;

    @Value("${API.KEY}")
    private String API_KEY;

    public Boolean CallAPI(SmsRequest smsRequest)throws RestClientException {

            DeliveryMsg deliveryMsg = callApiHandler(smsRequest);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("key", API_KEY );

            HttpEntity<DeliveryMsg> request = new HttpEntity<>(deliveryMsg, headers);
                APIResponse res = restTemplate.postForObject(URL, request, APIResponse.class);
                logger.info("Response From 3rd Party Api : {}", res );
                return validate(res);
    }

    private Boolean validate( APIResponse res){
        List<Response> list = res.getResponse();
        for(Response response : list){
            logger.info("Response code == {}", response.getCode());
            if(response.getCode().equalsIgnoreCase("1001")){
                logger.info("3rd Party Response Validation Pass");
                return true;
            }
        }
        logger.info("3rd Party Response Validation Failed");
        return false;
    }

    private DeliveryMsg callApiHandler(SmsRequest smsRequest){

        DeliveryMsg deliveryMsg = new DeliveryMsg();
        Channels channels = new Channels();
        Sms sms = new Sms();
        Destination destination = new Destination();

        deliveryMsg.setDeliverychannel("sms");
        sms.setText(smsRequest.getMessage());
        channels.setSms(sms);
        deliveryMsg.setChannels(channels);
        destination.setCorrelationid(smsRequest.getId().toString());
        ArrayList<String> sendingNumber = new ArrayList<>();
        sendingNumber.add("+91" + smsRequest.getPhoneNumber());
        destination.setMsisdn(sendingNumber);

        ArrayList<Destination> sendingDestination = new ArrayList<>();
        sendingDestination.add(destination);
        deliveryMsg.setDestination(sendingDestination);

        logger.info("delivery Message Object Creation Done {}", deliveryMsg);
        return deliveryMsg;
    }
}

