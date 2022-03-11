package com.example.notificationService.Entities;

import com.example.notificationService.Constant.Const;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import javax.persistence.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = Const.SMS_REQUEST)
public class SmsRequest {
    @Id
    @Column(name="id",unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name="phone_number",nullable = false)
    private String phoneNumber;

    @Column(name = "message",nullable = false)
    private String message;

    @Column(name="status")
    private String status;

    @Column(name = "failure_code")
    private Integer failureCode;

    @Column(name="failure_comment")
    private String failureComment;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;

}
