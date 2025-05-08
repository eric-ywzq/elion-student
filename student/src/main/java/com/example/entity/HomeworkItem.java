package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HomeworkItem {
    private UUID id;
    private String type;
    private String title;
    private String content;
    private UUID homeworkId; // 直接存储关联ID，不再使用对象引用
}
