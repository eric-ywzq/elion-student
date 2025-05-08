package com.example.service;

import com.example.mapper.HomeworkMapper;
import com.example.mapper.StudentMapper;
import com.example.entity.Homework;
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

    public Homework getHomeworkById(UUID homeworkId) {
        Homework homework = homeworkMapper.selectHomeworkBypid(homeworkId);
        if (homework == null) {
            logger.error("Homework not found with ID: {}", homeworkId);
            throw new RuntimeException("Homework not found");
        }
        // 手动加载班级历史（可选优化：使用 JOIN 查询）
        homework.getPeerCommentList().forEach(student ->
                student.setClassHistories(
                        studentMapper.selectClassByStudentId(student.getId())
                )
        );
        logger.info("Successfully retrieved homework with ID: {}", homeworkId);
        return homework;
    }

    public Map<String, Object> buildHomeworkResponse(Homework homework) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("errcode", 0);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("entry", convertHomeworkToMap(homework));
        response.put("data", data);

        return response;
    }

    public List<Homework> getHomeworkList() {
         return homeworkMapper.getHomeworkList();
    }

    private Map<String, Object> convertHomeworkToMap(Homework homework) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("pid", homework.getId().toString()); // UUID 转为字符串
        entry.put("homeworkTitle", homework.getTitle());
        entry.put("num", homework.getNum());
        entry.put("finishedNum", homework.getFinishedNum());
        entry.put("createdTime", formatDateTime(homework.getCreatedTime())); // 时间格式化
        entry.put("deadline", formatDateTime(homework.getDeadline()));
        entry.put("lastModifiedTime", formatDateTime(homework.getLastModifiedTime()));

        // 转换题目项
        List<Map<String, Object>> items = homework.getItems().stream()
                .map(item -> {
                    Map<String, Object> itemMap = new LinkedHashMap<>();
                    itemMap.put("type", item.getType());
                    itemMap.put("title", item.getTitle());
                    itemMap.put("content", item.getContent());
                    return itemMap;
                })
                .collect(Collectors.toList());
        entry.put("items", items);

        // 转换学生评价列表
        List<Map<String, Object>> comments = homework.getPeerCommentList().stream()
                .map(student -> {
                    Map<String, Object> studentMap = new LinkedHashMap<>();
                    studentMap.put("sid", student.getId().toString());
                    studentMap.put("name", student.getName());
                    studentMap.put("gender", student.getGender());
                    studentMap.put("phone", student.getPhone());

                    // 转换班级历史
                    List<Map<String, Object>> histories = student.getClassHistories().stream()
                            .map(history -> {
                                Map<String, Object> historyMap = new LinkedHashMap<>();
                                historyMap.put("classId", history.getClassId());
                                historyMap.put("joinedTime", formatDateTime(history.getJoinedTime()));
                                historyMap.put("quitedTime", formatDateTime(history.getQuitedTime()));
                                return historyMap;
                            })
                            .collect(Collectors.toList());
                    studentMap.put("classHistories", histories);

                    return studentMap;
                })
                .collect(Collectors.toList());
        entry.put("peerCommentList", comments);
        return entry;
    }

    // 时间格式化工具方法
    private String formatDateTime(ZonedDateTime dateTime) {
        return dateTime != null ?
                dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) :
                null;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ?
                dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) :
                null;
    }
}