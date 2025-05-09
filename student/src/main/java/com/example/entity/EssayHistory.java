package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EssayHistory {
    private UUID sid;
    private UUID pid;
    private String title;
    private String content;
    private String images; // JSON存储图片路径
    private String ocrResults; // JSON存储OCR结果
    private LocalDateTime createTime;
}
