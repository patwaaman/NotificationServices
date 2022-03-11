package com.example.notificationService.Entities;

import com.example.notificationService.Constant.Const;
import lombok.*;

import javax.persistence.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = Const.BLACKLIST_DB)
public class BlacklistDB {
    @Id
    @Column(name ="id",unique = true)
    private String id;

    @Column(name="phone_number")
    private String phoneNumber;
}
