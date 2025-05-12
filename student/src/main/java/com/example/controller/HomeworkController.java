package com.example.controller;

import com.example.entity.Essay;
import com.example.entity.EssayDetailVO;
import com.example.entity.EssaySubmissionDTO;
import com.example.entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.service.HomeworkService;
import org.springframework.web.server.ResponseStatusException;

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
    public void submitHomework(@RequestBody Map<String, Object> request) {
        String homeworkIdStr = (String) request.get("homeworkId");
        UUID homeworkId = UUID.fromString(homeworkIdStr);
        homeworkService.submitEssay(homeworkId,new EssaySubmissionDTO());
    }

    @PostMapping("/api/homework/submit")
    public Essay getHomeworkById(@RequestBody Map<String, Object> request) {
        String homeworkIdStr = (String) request.get("homeworkId");
        UUID homeworkId = UUID.fromString(homeworkIdStr);
        Essay essay = homeworkService.getHomeworkById(homeworkId);
        if (essay == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Homework not found");
        }
        return essay;
    }

    @GetMapping("/api/homework/list")
    public ResponseEntity<PageResult<Essay>> getStudentEssays(
            @RequestParam int studentId,
            @RequestParam(defaultValue = "4") int page) {
        PageResult<Essay> result = homeworkService.getStudentEssaysByPage(studentId, page);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/submit")
    public ResponseEntity<?> submitEssay(
            @RequestHeader("studentId") UUID studentId,
            @RequestBody EssaySubmissionDTO dto) {
        Essay essay = homeworkService.submitEssay(studentId, dto);
        return ResponseEntity.created(URI.create("/api/homeworks/" + essay.getPid()))
                .body(essay);
    }

    @PostMapping("/my")
    public ResponseEntity<List<Essay>> getMyEssays(@RequestHeader("studentId") int studentId) {
        List<Essay> essays = homeworkService.getStudentEssays(studentId);
        return ResponseEntity.ok(essays);
    }

    @PostMapping("/{essayId}")
    public ResponseEntity<EssayDetailVO> getEssayDetail(@PathVariable UUID essayId) {
        EssayDetailVO detail = homeworkService.getEssayDetail(essayId);
        return ResponseEntity.ok(detail);
    }
}
