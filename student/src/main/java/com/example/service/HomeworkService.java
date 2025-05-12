package com.example.service;

import com.example.entity.EssayDetailVO;
import com.example.entity.EssaySubmissionDTO;
import com.example.entity.PageResult;
import com.example.mapper.HomeworkMapper;
import com.example.mapper.StudentMapper;
import com.example.entity.Essay;
import org.springframework.beans.BeanUtils;
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
import org.springframework.transaction.annotation.Transactional;

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
        essay.getPeerCommentList().forEach(student ->
                student.setClassHistories(
                        studentMapper.selectClassByStudentId(student.getId())
                )
        );
        logger.info("Successfully retrieved homework with ID: {}", homeworkId);
        return essay;
    }

    public Map<String, Object> buildHomeworkResponse(UUID id) {
        Essay essay = homeworkMapper.selectEssayWithId(id);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("errcode", 0);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("entry", convertHomeworkToMap(essay));
        response.put("data", data);
        return response;
    }

    public PageResult<Essay> getStudentEssaysByPage(int studentId, int pageNum) {
        int offset = (pageNum - 1) * 4;
        List<Essay> essays = homeworkMapper.selectStudentEssaysByPage(studentId, offset, 4);
        int total = homeworkMapper.countStudentEssays(studentId);
        int totalPages = (int) Math.ceil((double) total / 4);
        return new PageResult<>(essays, pageNum, 4, total, totalPages);
    }

    private Map<String, Object> convertHomeworkToMap(Essay essay) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("pid", essay.getPid().toString());
        entry.put("homeworkTitle", essay.getTitle());
        entry.put("num", essay.getNum());
        entry.put("finishedNum", essay.getFinishedNum());
        entry.put("createdTime", formatDateTime(essay.getCreatedTime()));
        entry.put("lastModifiedTime", formatDateTime(essay.getLastModifiedTime()));
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

    // 提交作业
    public Essay submitEssay(UUID studentId, EssaySubmissionDTO dto){
        // 构建 Essay 对象
        Essay essay = new Essay();
        essay.setSid(studentId);
        essay.setPid(UUID.randomUUID());
        essay.setTitle(dto.getTitle());
        essay.setNum(dto.getNum());
        essay.setFinishedNum(0);
        essay.setCreatedTime(ZonedDateTime.now());
        essay.setLastModifiedTime(ZonedDateTime.now());
        essay.setContent(dto.getContent());

        homeworkMapper.insertEssay(essay);
        homeworkMapper.linkStudentEssay(studentId, essay.getPid());
        return essay;
    }

    // 获取学生作业列表
    @Transactional(readOnly = true)
    public List<Essay> getStudentEssays(int studentId) {
        return homeworkMapper.selectEssaysByStudentId(studentId);
    }

    // 获取作业详情（带互评信息）
    @Transactional(readOnly = true)
    public EssayDetailVO getEssayDetail(UUID essayId) {
        Essay essay = homeworkMapper.selectEssayByEssayId(essayId); // ← 修改：返回单个 Essay

        EssayDetailVO vo = new EssayDetailVO();
        BeanUtils.copyProperties(vo, essay); // 注意方向：目标在前
        vo.setPeerComments(homeworkMapper.selectPeerCommentListByHomeworkId(essayId));

        return vo;
    }
}