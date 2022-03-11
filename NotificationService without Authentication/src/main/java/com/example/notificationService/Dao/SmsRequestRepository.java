package com.example.notificationService.Dao;

import com.example.notificationService.Entities.SmsRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsRequestRepository extends JpaRepository<SmsRequest, Integer> { }
