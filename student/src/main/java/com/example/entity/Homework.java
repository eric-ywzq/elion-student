package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

// Homework.java
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Homework {
    private UUID id;
    private String title;
    private Integer num;
    private Integer finishedNum;
    private ZonedDateTime createdTime;
    private LocalDateTime deadline;
    private ZonedDateTime lastModifiedTime;
    private List<HomeworkItem> items;
    private List<Student> peerCommentList;
}

