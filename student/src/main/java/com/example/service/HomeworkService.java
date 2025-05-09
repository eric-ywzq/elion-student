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
        Essay essay = homeworkMapper.selectEssayWithId(id);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("errcode", 0);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("entry", convertHomeworkToMap(essay));
        response.put("data", data);
        return response;
    }

    public PageResult<Essay> getStudentEssaysByPage(int studentId, int pageNum) {
        int offset = (pageNum - 1) * pageNum;
        List<Essay> essays = homeworkMapper.selectStudentEssaysByPage(studentId, offset, pageNum);
        int total = homeworkMapper.countStudentEssays(studentId);
        int totalPages = (int) Math.ceil((double) total / pageNum);
        return new PageResult<>(essays, pageNum, pageNum, total, totalPages);
    }

    private Map<String, Object> convertHomeworkToMap(Essay essay) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("pid", essay.getPid().toString());
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

    // 提交作业
    public Essay submitEssay(UUID studentId, EssaySubmissionDTO dto){
        // 构建 Essay 对象
        Essay essay = new Essay();
        essay.setSid(studentId);
        essay.setTitle(dto.getTitle());
        essay.setNum(dto.getNum());
        essay.setCreatedTime(ZonedDateTime.now());
        essay.setContent(dto.getContent());
        // 其他字段设置...

        // 插入作业记录
        homeworkMapper.insertEssay(essay);

        // 关联学生与作业（中间表）
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
        List<Essay> essay = homeworkMapper.selectEssaysByEssayId(essayId);

        // 转换VO对象（根据需求补充字段）
        EssayDetailVO vo = new EssayDetailVO();
        BeanUtils.copyProperties(essay, vo);
        vo.setPeerComments(homeworkMapper.selectPeerCommentListByHomeworkId(essayId));
        return vo;
    }
}