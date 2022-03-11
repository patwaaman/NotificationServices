package com.example.notificationService.Dao;

import com.example.notificationService.Entities.BlacklistDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BlacklistDBRepository extends JpaRepository<BlacklistDB, String> {
}