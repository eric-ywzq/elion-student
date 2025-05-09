package com.example.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

// 作业详情响应体
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EssayDetailVO {
    private Integer pid;
    private String title;
    private LocalDateTime createdTime;
    private List<Student> peerComments; // 互评学生列表
    private String content; // 作文内容
    private String answer; // 作文答案
}