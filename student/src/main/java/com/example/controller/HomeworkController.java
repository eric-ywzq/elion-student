package com.example.controller;

import com.example.entity.Homework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.service.HomeworkService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class HomeworkController {
    private final HomeworkService homeworkService;

    @Autowired
    public HomeworkController(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }

    @PostMapping("/api/homework")
    public Map<String, Object> submitHomework(@RequestBody Map<String, Object> request) {
        String homeworkIdStr = (String) request.get("homeworkId");
        UUID homeworkId = UUID.fromString(homeworkIdStr);
        Homework homework = homeworkService.getHomeworkById(homeworkId);
        return homeworkService.buildHomeworkResponse(homework);
    }
    @RequestMapping("/api/homework/list")
    public List<Homework> getHomeworkList() {
        return homeworkService.getHomeworkList();
    }
}
