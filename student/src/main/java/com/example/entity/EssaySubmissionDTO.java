package com.example.entity;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EssaySubmissionDTO {
    private String title;
    private int pid;
    private String content;
    private int sid;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer num;
}