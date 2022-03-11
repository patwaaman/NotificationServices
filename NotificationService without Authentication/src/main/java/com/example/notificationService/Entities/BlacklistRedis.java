package com.example.notificationService.Entities;

import java.io.Serializable;

import com.example.notificationService.Constant.Const;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@RedisHash(Const.HASH_REFERENCE)
public class BlacklistRedis implements Serializable {
    @Id
    private String id;
    private String phoneNumber;
}
