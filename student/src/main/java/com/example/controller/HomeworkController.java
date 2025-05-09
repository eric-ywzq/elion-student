package com.example.controller;

import com.example.entity.Essay;
import com.example.entity.EssayDetailVO;
import com.example.entity.EssaySubmissionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.service.HomeworkService;

import java.net.URI;
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
        return homeworkService.buildHomeworkResponse(homeworkId);
    }

    @PostMapping("/api/homework/submit")
    public Essay getHomeworkById(@RequestBody Map<String, Object> request) {
        String homeworkIdStr = (String) request.get("homeworkId");
        UUID homeworkId = UUID.fromString(homeworkIdStr);
        return homeworkService.getHomeworkById(homeworkId);
    }

    @RequestMapping("/api/homework/list")
    public List<Essay> getHomeworkList() {
        return homeworkService.getHomeworkList();
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitEssay(
            @RequestAttribute("studentId") UUID studentId, // 从认证信息获取
            @RequestBody EssaySubmissionDTO dto) {

        Essay essay = homeworkService.submitEssay(studentId, dto);
        return ResponseEntity.created(URI.create("/api/homeworks/" + essay.getPid()))
                .body(essay);
    }

    // 查询学生自己的作业列表
    @GetMapping("/my")
    public ResponseEntity<List<Essay>> getMyEssays(
            @RequestAttribute("studentId") int studentId) {

        List<Essay> essays = homeworkService.getStudentEssays(studentId);
        return ResponseEntity.ok(essays);
    }

    // 获取作业详情（带互评信息）
    @GetMapping("/{essayId}")
    public ResponseEntity<EssayDetailVO> getEssayDetail(
            @PathVariable UUID essayId) {

        EssayDetailVO detail = homeworkService.getEssayDetail(essayId);
        return ResponseEntity.ok(detail);
    }
}
