package com.example.notificationService.Services;

import com.example.notificationService.Constant.Const;

import com.example.notificationService.Dao.SmsRequestRepository;
import com.example.notificationService.Entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class KafkaConsumer {
    @Autowired
    private MyServices myServices;
    @Autowired
    private SmsRequestRepository smsRequestRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ImiConnectMessageService imiConnectMessageService;
    @Autowired
    private ElasticSearchService elasticSearchService;

    Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final String TOPIC = Const.KAFKA_TOPIC;


    @KafkaListener(topics = TOPIC, groupId = "mygroup")
    public void consumeFromTopic(String request_id) {
        logger.info("Consumed message " + request_id);

        int counter =0;

        /* --------- Get details From Database With the Request ID-------- */
        Optional<SmsRequest> optional= smsRequestRepository.findById(Integer.valueOf(request_id));
        SmsRequest smsRequest = new SmsRequest();
        if(optional.isPresent()) {
            smsRequest = optional.get();
            logger.info("Consuming Sms Request Details : {}", smsRequest);

        }else{
            logger.error("Consuming Sms Details is not found with request ID");
        }

        /*------ Check Number is blacklisted via Redis -------*/
        if(Boolean.TRUE.equals(myServices.isBlacklisted(smsRequest.getPhoneNumber()))){
            logger.warn("Consuming Phone Number is blacklisted");
            myServices.updateDBOnFailure(smsRequest,"Blacklisted" );
        }
        else{

            /*-------- Call To 3rd Party API----------*/
            if(Boolean.TRUE.equals(imiConnectMessageService.CallAPI(smsRequest))){
                logger.info("Msg sent suceessfully to imiconnect ");
                myServices.updateDBOnSucceess(smsRequest);
            }
            else{
                ++counter;
                logger.info("Something went wrong on sending sms to imiconnect");
                //retryUnsuccessfuls(smsRequest,counter);
                myServices.updateDBOnFailure(smsRequest, "Connection Failed to IMICONNECT ");
            }
        }

        /*-------- Index SMS to Elastic Search -----------*/

        ElasticSearch elasticSearch = ElasticSearch.builder()
                                        .phoneNumber(smsRequest.getPhoneNumber())
                                        .message(smsRequest.getMessage())
                                        .status(smsRequest.getStatus())
                                        .createdAt(smsRequest.getCreatedAt())
                                        .build();
        try {
            elasticSearchService.createElasticSearchIndex(elasticSearch);
            logger.info("ADDED TO ELASTIC SEARCH {}", elasticSearch);
        }
        catch(Exception e){
            logger.error(String.valueOf(e));
            logger.error("Cannot Add SMS to ElasticSearch");
        }

    }

//    private boolean retryUnsuccessfuls(SmsRequest smsRequest, int counter){
//
//    }



}
