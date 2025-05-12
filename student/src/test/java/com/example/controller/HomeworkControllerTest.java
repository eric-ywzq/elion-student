package com.example.controller;

import com.example.entity.Essay;
import com.example.entity.EssayDetailVO;
import com.example.entity.EssaySubmissionDTO;
import com.example.entity.PageResult;
import com.example.service.HomeworkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class HomeworkControllerTest {

    private MockMvc mockMvc;

    // 手动创建的 mock service
    private HomeworkService homeworkService;

    @BeforeEach
    void setUp() {
        // 初始化 mock service
        homeworkService = Mockito.mock(HomeworkService.class);

        // 构建 MockMvc 并手动注入 controller 及其依赖
        this.mockMvc = MockMvcBuilders.standaloneSetup(
                        new HomeworkController(homeworkService))
                .build();
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testReturnSuccess_WhenSubmitHomework() throws Exception {
        UUID homeworkId = UUID.randomUUID();
        Map<String, Object> mockResponse = Map.of(
                "errcode", 0,
                "data", Map.of("entry", "value")
        );

        when(homeworkService.buildHomeworkResponse(homeworkId)).thenReturn(mockResponse);

        mockMvc.perform(post("/api/homework")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"homeworkId\":\"" + homeworkId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errcode").value(0))
                .andExpect(jsonPath("$.data.entry").exists());
    }

    @Test
    public void testReturnEssay_WhenGetHomeworkById() throws Exception {
        UUID homeworkId = UUID.randomUUID();
        Essay mockEssay = new Essay();
        mockEssay.setPid(homeworkId);

        when(homeworkService.getHomeworkById(homeworkId)).thenReturn(mockEssay);

        mockMvc.perform(post("/api/homework/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"homeworkId\":\"" + homeworkId + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pid").value(homeworkId.toString()));
    }

    @Test
    public void testReturnPageResult_WhenGetStudentEssaysByPage() throws Exception {
        PageResult<Essay> pageResult = new PageResult<>(List.of(new Essay()), 1, 4, 1, 1);
        when(homeworkService.getStudentEssaysByPage(eq(123), eq(1))).thenReturn(pageResult);

        mockMvc.perform(get("/api/homework/list?studentId=123&page=1")) // 改为 get
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataList[0]").exists())
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.pageSize").value(4));
    }

    @Test
    public void testReturnCreated_WhenSubmitEssay() throws Exception {
        UUID studentId = UUID.randomUUID();
        Essay essay = new Essay();
        essay.setPid(UUID.randomUUID());

        EssaySubmissionDTO dto = new EssaySubmissionDTO();
        dto.setTitle("Test");

        when(homeworkService.submitEssay(any(), any())).thenReturn(essay);

        mockMvc.perform(post("/submit")
                        .header("studentId", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/homeworks/" + essay.getPid()))
                .andExpect(jsonPath("$.pid").value(essay.getPid().toString()));
    }

    @Test
    public void testReturnMyEssays_WhenGetMyEssays() throws Exception {
        List<Essay> essays = List.of(new Essay());
        when(homeworkService.getStudentEssays(123)).thenReturn(essays);

        mockMvc.perform(post("/my")
                        .header("studentId", 123))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    public void testReturnEssayDetail_WhenGetEssayDetail() throws Exception {
        UUID essayId = UUID.randomUUID();
        EssayDetailVO detail = new EssayDetailVO();

        when(homeworkService.getEssayDetail(essayId)).thenReturn(detail);

        mockMvc.perform(post("/" + essayId))
                .andExpect(status().isOk());
    }

    @Test
    public void testReturnNotFound_WhenHomeworkDoesNotExist() throws Exception {
        UUID homeworkId = UUID.randomUUID();
        when(homeworkService.getHomeworkById(homeworkId)).thenReturn(null);

        mockMvc.perform(post("/api/homework/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Map.of("homeworkId", homeworkId.toString()).toString()))
                .andExpect(status().isNotFound());
    }

}
