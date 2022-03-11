package com.example.notificationService.Dao;


import com.example.notificationService.Constant.Const;
import com.example.notificationService.Entities.BlacklistRedis;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

import java.util.Map;

import org.springframework.data.redis.core.HashOperations;


@Repository
public class BlacklistRedisRepoImplementation implements BlacklistRedisRepository {

    private String hashReference = Const.HASH_REFERENCE;

    @Resource(name="redisTemplate")          // 'redisTemplate' is defined as a Bean
    private HashOperations<String, String, BlacklistRedis> hashOperations;

    @Override
    public void saveToBlacklist(BlacklistRedis blk) {
        hashOperations.putIfAbsent(hashReference, blk.getId(), blk);
    }


    @Override
    public void deleteFromBlacklist(String createdID) {
        hashOperations.delete(hashReference, createdID);
    }

    // isPresent search in HashMap using CreatedID = hashreference +"_" + blacklistNumber
    @Override
    public Boolean isPresent(String createdID) {
        return hashOperations.hasKey(hashReference, createdID);
    }

    @Override
    public Map<String, BlacklistRedis> getAllBlacklistContact() {
        return hashOperations.entries(hashReference);
    }


}
