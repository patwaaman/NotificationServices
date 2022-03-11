package com.example.notificationService.Entities;

import com.example.notificationService.Constant.Const;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.Date;

@Document(indexName = Const.ELASTICSEARCH_INDEX)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElasticSearch {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Field(type = FieldType.Keyword, name = "phone_number")
    private String phoneNumber;

    @Field(type = FieldType.Text, name = "message")
    private String message;

    @Field(type = FieldType.Text , name = "status")
    private String status;

    @Field(type = FieldType.Date, name = "time")
    private Date createdAt;

}
