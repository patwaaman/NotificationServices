package com.example.notificationService.Controller;

import com.example.notificationService.Dto.Request.*;
import com.example.notificationService.Entities.BlacklistRedis;
import com.example.notificationService.Entities.SmsRequest;
import com.example.notificationService.Services.ElasticSearchService;
import com.example.notificationService.Services.MyServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(path = "/v1")
public class Controller {
    @Autowired
    private MyServices myServices;
    @Autowired
    private ElasticSearchService elasticSearchService;

    Logger logger = LoggerFactory.getLogger(Controller.class);


    @PostMapping("/send/sms")
    public ResponseEntity<Map<String, String>> addSmsRequest(@Valid @RequestBody AddSmsRequest addSmsRequest) throws ExecutionException, InterruptedException {
        return myServices.addSmsRequest(addSmsRequest);
    }

    @PostMapping(path="/blacklist/add")
    public ResponseEntity<Map<String,String>> addBlacklistUser(@Valid @RequestBody BlacklistRequest blacklistRequest){
        return myServices.addBlacklistNumber(blacklistRequest);
    }

    @PostMapping(path="/blacklist/delete")
    public ResponseEntity<Map<String,String>> deleteBlacklistUser(@Valid @RequestBody RemoveBlacklistNo removeBlacklistNo){
        return myServices.deleteBlacklistNumber(removeBlacklistNo);
    }

    @GetMapping(path="/sms")
    public ResponseEntity<Map<String,Object>> getSmsDetailViaRequestID(@Valid @RequestBody SmsDetailViaID smsDetailViaID){
        return myServices.getSmsDetailViaRequestID(smsDetailViaID);
    }

    /* ------- View BLacklist Number in Database ----- */
    @GetMapping(path="/blacklist/view")
    public ResponseEntity<Map<String, Object>> getAllBlacklistNumber(){
        return myServices.getAllBlacklistNumber();
    }


    @GetMapping("/request/time")
    public ResponseEntity<Map<String, Object>> getTimeIndexed(@Valid @RequestBody StartEndTime startEndTime){
        logger.info("StartEndTime {}",startEndTime);
        return elasticSearchService.findByStartEndTime(startEndTime);
    }

    @GetMapping("/request/message")
    public ResponseEntity<Map<String, Object>> getSmsIndexed(@Valid @RequestBody TextSearchRequest textSearchRequest){
        logger.info("TextSearchRequest {} ",textSearchRequest);
        return elasticSearchService.findSmsContainingText(textSearchRequest);
    }


    /*------ Extra API's To make sure different tools are working properly --------*/

    @GetMapping(path="/view/sms")
    public Iterable<SmsRequest> getAllSMS() {
        return myServices.getAllSMS();
    }

    /* ------- View BlacklistNumber in Redis --------*/
    @GetMapping(path="/blacklist/redis/view")
    public Map<String, BlacklistRedis> getAllBlacklistUser(){
       return myServices.getAllBlacklistContact();
    }

    @GetMapping(path="/blacklist/redis/delete")
    public String deleteFromRedisOnly(@RequestParam String id){
        return myServices.deleteFromRedisOnly(id);
    }

}
