package com.example.notificationService.Services;

import com.example.notificationService.Constant.Const;


import com.example.notificationService.Dao.BlacklistDBRepository;
import com.example.notificationService.Dao.BlacklistRedisRepository;
import com.example.notificationService.Dao.SmsRequestRepository;
import com.example.notificationService.Dto.Request.AddSmsRequest;
import com.example.notificationService.Dto.Request.BlacklistRequest;
import com.example.notificationService.Dto.Request.RemoveBlacklistNo;
import com.example.notificationService.Dto.Request.SmsDetailViaID;
import com.example.notificationService.Dto.Response.Error;
import com.example.notificationService.Dto.Response.GetAllSmsResponse;

import com.example.notificationService.Dto.Response.RequestIDSuccess;
import com.example.notificationService.Entities.BlacklistDB;
import com.example.notificationService.Entities.BlacklistRedis;
import com.example.notificationService.Entities.SmsRequest;
import com.example.notificationService.Utils.PhoneNoValidator;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class MyServices {
    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private SmsRequestRepository smsRequestRepository;
    @Autowired
    private BlacklistDBRepository blacklistDBRepository;
    @Autowired
    private BlacklistRedisRepository blacklistRedisRepository;



    private String hashReference = Const.HASH_REFERENCE;

    Logger logger = LoggerFactory.getLogger(MyServices.class);

    private final String MsgSuccess = "Success";
    private final String MSgFailure = "Failure";


    private String CreatedID="";
    private String PhoneNumber="";
    private String Message="";
    private String ConsumerID="";

    public ResponseEntity<Map<String, String>> addSmsRequest(@NotNull AddSmsRequest addSmsRequest) throws ExecutionException,InterruptedException {
        PhoneNumber = addSmsRequest.getPhoneNumber().trim();
        Message = addSmsRequest.getMessage().trim();
        logger.info("Add Sms Request : {}", addSmsRequest);

        /* ------- Save to SMSRequest to  Repository --------*/

        SmsRequest smsRequest = SmsRequest.builder()
                                .phoneNumber(PhoneNumber)
                                .message(Message)
                                .build();

        smsRequest = smsRequestRepository.save(smsRequest);

        /*-------- Publish Request ID to Kafka Topic ---------*/
        ConsumerID = smsRequest.getId().toString();
        if (Boolean.TRUE.equals(kafkaProducer.publishToTopic(ConsumerID))) {
                logger.info("Published Successfully : Consumer ID = {}", ConsumerID);
        } else {
                logger.info("Kafka Topic Publishing Failed");
        }


        Map<String, String> response = new HashMap<>();
        response.put("Comments", "Successfully Sent");
        response.put("request_id", ConsumerID);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    public ResponseEntity<Map<String, String>> addBlacklistNumber(BlacklistRequest blacklistRequest)throws ConstraintViolationException, DataIntegrityViolationException {
        logger.info("Add To Blacklist: {}", blacklistRequest);
        Map<String, String> response = new HashMap<>();

        List<String> blacklist = blacklistRequest.getPhoneNumbers();
        for (String blacklistNumber : blacklist) {
            blacklistNumber = blacklistNumber.trim();

             //Redis Checked.
            if(!PhoneNoValidator.isValidPhoneNo(blacklistNumber) || Boolean.TRUE.equals(isBlacklisted(blacklistNumber)) ) continue;

            CreatedID = hashReference + "_" + blacklistNumber;
            if(blacklistDBRepository.existsById(CreatedID)){
                /*------ Add Blacklist Number to Redis--------*/
                BlacklistRedis blacklistRedis = BlacklistRedis.builder().id(CreatedID).phoneNumber(blacklistNumber).build();
                blacklistRedisRepository.saveToBlacklist(blacklistRedis);
                logger.info("New Number Blacklisted in Redis Only {}", blacklistNumber);
            }
            else{
                /*------ Add Blacklist Number to Database--------*/
                BlacklistDB blacklistDB = BlacklistDB.builder().id(CreatedID).phoneNumber(blacklistNumber).build();
                blacklistDBRepository.save(blacklistDB);               // Add to DB.
                /*------ Add Blacklist Number to Redis--------*/
                BlacklistRedis blacklistRedis = BlacklistRedis.builder().id(CreatedID).phoneNumber(blacklistNumber).build();
                blacklistRedisRepository.saveToBlacklist(blacklistRedis);
                logger.info("New Number Blacklisted in Database and Redis {}", blacklistNumber);

            }
        }

        logger.info("Successfully Blacklisted");

        response.put("data", "Successfully Blacklisted");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<Map<String,String>> deleteBlacklistNumber( RemoveBlacklistNo removeBlacklistNo){
        logger.info("Delete From Blacklist: {}", removeBlacklistNo);
        String blacklistNumber = removeBlacklistNo.getPhoneNumber().trim();
        CreatedID = hashReference + "_" + blacklistNumber;
        Map<String, String> response = new HashMap<>();

        // checked via Redis
        if(Boolean.TRUE.equals(isBlacklisted(blacklistNumber))){

            /*------ Delete Blacklist Number From Database ---------*/
            blacklistDBRepository.deleteById(CreatedID);
            /*------- Delete Blacklist Number From Redis -----------*/
            blacklistRedisRepository.deleteFromBlacklist(CreatedID);
            logger.info("Removed a Blacklisted Number from DB and Redis {}", blacklistNumber);

        }
        else if(blacklistDBRepository.existsById(CreatedID)){
            /*------ Delete Blacklist Number From Database ---------*/
            blacklistDBRepository.deleteById(CreatedID);
            logger.info("Removed a Blacklisted Number from Redis {}", blacklistNumber);

        }
        else{
            logger.info("Provided Number is not Blacklisted");
            response.put("Error","Provided Number is Not Blacklisted");
            return new ResponseEntity<>(response ,HttpStatus.NOT_FOUND);
        }

        response.put("data", "Deleted Successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*------ View Blacklisted Numbers From DataBase --------*/
    public ResponseEntity<Map<String, Object>> getAllBlacklistNumber(){
        logger.info("Database query for all blacklisted Number");

        List<String> allBlacklisted = new ArrayList<>();
        blacklistDBRepository.findAll().forEach(number->{
            allBlacklisted.add(number.getPhoneNumber());
        });

        Map<String, Object> response = new HashMap<>();
        response.put("TotalHits", allBlacklisted.size());
        response.put("data", allBlacklisted);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    public ResponseEntity<Map<String, Object>> getSmsDetailViaRequestID( SmsDetailViaID smsDetailViaID){
        logger.info("Get Sms Detail Via Request ID {}", smsDetailViaID);

        Optional<SmsRequest> optional = smsRequestRepository.findById(smsDetailViaID.getRequestId());
        if(optional.isPresent()){
            RequestIDSuccess requestIDSuccess = new RequestIDSuccess(optional.get());

            Map<String, Object> response = new HashMap<>();
            response.put("data", requestIDSuccess);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Error error = new Error("INVALID_REQUEST", "request_id not found");
        Map<String, Object> response = new HashMap<>();
        response.put("error",error);
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }

    public Iterable<SmsRequest> getAllSMS() {
        logger.info("Get All Sms details from Database");

        GetAllSmsResponse getAllSmsResponse = new GetAllSmsResponse();
        getAllSmsResponse.setAllSmsResponse(smsRequestRepository.findAll());
        return getAllSmsResponse.getAllSmsResponse();
    }



    public void updateDBOnFailure(SmsRequest smsRequest,String failureComment){

        smsRequest.setStatus(MSgFailure);
        smsRequest.setFailureComment(failureComment);
        smsRequestRepository.save(smsRequest);
        logger.info("updated DB on failure {} ",smsRequest);
    }


    public void updateDBOnSucceess(SmsRequest smsRequest){

        smsRequest.setStatus(MsgSuccess);
        SmsRequest smsRequest1 = smsRequestRepository.save(smsRequest);
        logger.info("updated DB on Success {} ",smsRequest);

    }

    /*------- Check No. is Blacklisted Using Redis ------*/
    public Boolean isBlacklisted(String phone_number){
        CreatedID = hashReference+"_"+phone_number.trim();
        return blacklistRedisRepository.isPresent(CreatedID);
    }


    /* ------- View BlacklistNumber in Redis Only--------*/
    public Map<String,BlacklistRedis> getAllBlacklistContact(){
        logger.info("Get All blacklisted Contacts from Redis Only");
       return blacklistRedisRepository.getAllBlacklistContact();
   }

   public String deleteFromRedisOnly(String id){
        logger.info("Remove Blacklist Number From Redis Only");
        blacklistRedisRepository.deleteFromBlacklist(id);
        return "Deleted From Redis Only";
   }

}
