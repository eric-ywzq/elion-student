package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClassHistory {
    private UUID id;
    private String classId;
    private ZonedDateTime joinedTime;
    private ZonedDateTime quitedTime;
    private UUID studentId; // 直接存储关联ID
}
