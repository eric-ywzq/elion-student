package com.example.service;

import com.example.entity.*;
import com.example.mapper.HomeworkMapper;
import com.example.mapper.StudentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HomeworkServiceTest {

    @Mock
    private HomeworkMapper homeworkMapper;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private HomeworkService homeworkService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetHomeworkById_ReturnsEssay() {
        UUID homeworkId = UUID.randomUUID();
        Essay essay = new Essay();
        essay.setPid(homeworkId);

        // 设置非空 peerCommentList 避免 NullPointerException
        essay.setPeerCommentList(List.of(new Student()));
        essay.getPeerCommentList().get(0).setId(homeworkId);  // 设置 studentId

        when(homeworkMapper.selectHomeworkBypid(homeworkId)).thenReturn(essay);
        Essay result = homeworkService.getHomeworkById(homeworkId);

        assertNotNull(result);
        assertEquals(homeworkId, result.getPid());
        assertNotNull(result.getPeerCommentList().get(0).getClassHistories());
        verify(homeworkMapper).selectHomeworkBypid(homeworkId);
        verify(studentMapper).selectClassByStudentId(homeworkId);
    }

    @Test
    void testBuildHomeworkResponse_ReturnsMap() {
        UUID id = UUID.randomUUID();
        Essay essay = new Essay();
        essay.setPid(id);
        essay.setTitle("Test Homework");

        when(homeworkMapper.selectEssayWithId(id)).thenReturn(essay);

        Map<String, Object> response = homeworkService.buildHomeworkResponse(id);

        assertNotNull(response);
        assertEquals(0, response.get("errcode"));

        Map<String, Object> data = (Map<String, Object>) response.get("data");
        assertNotNull(data);
        Map<String, Object> entry = (Map<String, Object>) data.get("entry");
        assertNotNull(entry);
        assertEquals(id.toString(), entry.get("pid"));
        assertEquals("Test Homework", entry.get("homeworkTitle"));
    }

    @Test
    void testGetStudentEssaysByPage_ReturnsPageResult() {
        int studentId = 123;
        int pageNum = 1;
        List<Essay> essays = List.of(new Essay());
        int total = 1;

        when(homeworkMapper.selectStudentEssaysByPage(studentId, 0, 4)).thenReturn(essays);
        when(homeworkMapper.countStudentEssays(studentId)).thenReturn(total);

        PageResult<Essay> result = homeworkService.getStudentEssaysByPage(studentId, pageNum);

        assertNotNull(result);
        assertEquals(essays, result.getList());
        assertEquals(1, result.getTotalPages());
        assertEquals(pageNum, result.getPageNum());
        assertEquals(4, result.getPageSize());
    }

    @Test
    void testSubmitEssay_CreatesAndReturnsEssay() {
        UUID studentId = UUID.randomUUID();
        EssaySubmissionDTO dto = new EssaySubmissionDTO();
        dto.setTitle("My Essay");
        dto.setContent("This is my essay content.");
        dto.setNum(1);

        Essay essay = new Essay();
        essay.setSid(studentId);
        essay.setTitle(dto.getTitle());
        essay.setContent(dto.getContent());
        essay.setCreatedTime(ZonedDateTime.now());

        when(homeworkMapper.insertEssay(any(Essay.class))).thenAnswer(invocation -> {
            Essay captured = invocation.getArgument(0);
            captured.setPid(UUID.randomUUID()); // 模拟插入数据库后生成主键
            return null;
        });

        Essay result = homeworkService.submitEssay(studentId, dto);

        assertNotNull(result);
        assertEquals(studentId, result.getSid());
        assertEquals(dto.getTitle(), result.getTitle());
        assertNotNull(result.getPid());
        assertNotNull(result.getCreatedTime());

        verify(homeworkMapper).insertEssay(argThat(argument -> {
            assertEquals(studentId, argument.getSid());
            assertEquals(dto.getTitle(), argument.getTitle());
            assertNotNull(argument.getCreatedTime());
            return true;
        }));
        verify(homeworkMapper).linkStudentEssay(eq(studentId), eq(result.getPid()));
    }


    @Test
    void testGetStudentEssays_ReturnsList() {
        int studentId = 123;
        List<Essay> essays = List.of(new Essay());

        when(homeworkMapper.selectEssaysByStudentId(studentId)).thenReturn(essays);

        List<Essay> result = homeworkService.getStudentEssays(studentId);

        assertNotNull(result);
        assertEquals(essays.size(), result.size());
        verify(homeworkMapper).selectEssaysByStudentId(studentId);
    }

    @Test
    void testGetEssayDetail_ReturnsEssayDetailVO() {
        UUID essayId = UUID.randomUUID();
        Essay essay = new Essay();
        List<PeerComment> comments = List.of(new PeerComment());

        when(homeworkMapper.selectEssayByEssayId(essayId)).thenReturn(essay);
        EssayDetailVO result = homeworkService.getEssayDetail(essayId);

        assertNotNull(result);
        verify(homeworkMapper).selectEssayByEssayId(essayId);
        verify(homeworkMapper).selectPeerCommentListByHomeworkId(essayId);
    }
}
