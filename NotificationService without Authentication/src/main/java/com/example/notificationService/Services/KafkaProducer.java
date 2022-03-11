package com.example.notificationService.Services;

import com.example.notificationService.Constant.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;


import java.util.concurrent.ExecutionException;

@Service
public class KafkaProducer {

    private String TOPIC = Const.KAFKA_TOPIC;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemp;
    Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    public Boolean publishToTopic(String message) throws ExecutionException, InterruptedException {

        logger.info("Publishing to topic " + message);
        ListenableFuture<SendResult<String, String>> future = this.kafkaTemp.send(TOPIC, message);

        SendResult<String, String> result = future.get();
        if (result.getRecordMetadata().hasOffset())
            return true;
        else {
            logger.info("Kafka Consumer Error From Kafka Producer ");
            return false;
        }

//        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
//            @Override
//            public void onSuccess(SendResult<String, String> result) {
//                System.out.println("Sent message=[" + message +
//                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
//            }
//
//            @Override
//            public void onFailure(Throwable ex) {
//                System.out.println("Unable to send message=["
//                        + message + "] due to : " + ex.getMessage());
//            }
//        });

    }
}