package com.example.notificationService.Dao;


import com.example.notificationService.Entities.BlacklistRedis;

import java.util.Map;

public interface BlacklistRedisRepository {

    void saveToBlacklist(BlacklistRedis blk);
    void deleteFromBlacklist(String phoneNumber);
    Boolean isPresent(String phoneNumber);

    Map<String, BlacklistRedis> getAllBlacklistContact();
//    void saveAllEmployees(Map<Integer, Blacklist> map);
//    void updateEmployee(Blacklist emp);
}
