package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

// Homework.java
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Essay {
    private UUID sid;
    private UUID pid;
    private String title;
    private Integer num;
    private Integer finishedNum;
    private ZonedDateTime createdTime;
    private ZonedDateTime lastModifiedTime;
    private List<Student> peerCommentList;
    private String content;
}

