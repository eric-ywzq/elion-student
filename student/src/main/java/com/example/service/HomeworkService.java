package com.example.service;

import com.example.mapper.HomeworkMapper;
import com.example.mapper.StudentMapper;
import com.example.entity.Essay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class HomeworkService {
    private final HomeworkMapper homeworkMapper;
    private final StudentMapper studentMapper;
    private static final Logger logger = LoggerFactory.getLogger(HomeworkService.class);

    @Autowired
    public HomeworkService(HomeworkMapper homeworkMapper, StudentMapper studentMapper) {
        this.homeworkMapper = homeworkMapper;
        this.studentMapper = studentMapper;
    }

    public Essay getHomeworkById(UUID homeworkId) {
        Essay essay = homeworkMapper.selectHomeworkBypid(homeworkId);
        if (essay == null) {
            logger.error("Homework not found with ID: {}", homeworkId);
            throw new RuntimeException("Homework not found");
        }
        // 手动加载班级历史（可选优化：使用 JOIN 查询）
        essay.getPeerCommentList().forEach(student ->
                student.setClassHistories(
                        studentMapper.selectClassByStudentId(student.getId())
                )
        );
        logger.info("Successfully retrieved homework with ID: {}", homeworkId);
        return essay;
    }

    public Map<String, Object> buildHomeworkResponse(UUID id) {
        Essay essay = homeworkMapper.selectEssayWithid(id);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("errcode", 0);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("entry", convertHomeworkToMap(essay));
        response.put("data", data);
        return response;
    }

    public List<Essay> getHomeworkList() {
         return homeworkMapper.getHomeworkList();
    }

    private Map<String, Object> convertHomeworkToMap(Essay essay) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("pid", essay.getId().toString());
        entry.put("homeworkTitle", essay.getTitle());
        entry.put("num", essay.getNum());
        entry.put("finishedNum", essay.getFinishedNum());
        entry.put("createdTime", formatDateTime(essay.getCreatedTime()));
        entry.put("deadline", formatDateTime(essay.getDeadline()));
        entry.put("lastModifiedTime", formatDateTime(essay.getLastModifiedTime()));

        // 自动映射items和peerCommentList（无需手动转换）
        entry.put("items", essay.getItems());
        entry.put("peerCommentList", essay.getPeerCommentList());
        return entry;
    }

    // 时间格式化工具方法
    private String formatDateTime(ZonedDateTime dateTime) {
        return dateTime != null ?
                dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ?
                dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }
}