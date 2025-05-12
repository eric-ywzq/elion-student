package com.example.mapper;

import com.example.entity.Essay;
import com.example.entity.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface HomeworkMapper {
    @Select("SELECT pid, id, type, title, content FROM homework WHERE pid = #{homeworkId}")
    Essay selectHomeworkBypid(UUID homeworkId);

    @Select("SELECT id, name,class FROM student WHERE id IN (SELECT commenter_id FROM homework_peer_comment WHERE homework_id = #{homeworkId})")
    List<Student> selectPeerCommentListByHomeworkId(UUID homeworkId);

    @Select("SELECT e.* FROM essay e WHERE e.sid = #{studentId} ORDER BY e.created_time DESC LIMIT #{offset}, #{pageSize}")
    List<Essay> selectStudentEssaysByPage(@Param("studentId") int studentId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM essay WHERE sid = #{studentId}")
    int countStudentEssays(@Param("studentId") int studentId);

    // 查询Essay基本信息
    @Select("SELECT * FROM essay WHERE pid = #{id}")
    @Results(id = "essayResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "num", column = "num"),
            @Result(property = "finishedNum", column = "finished_num"),
            @Result(property = "createdTime", column = "created_time"),
            @Result(property = "deadline", column = "deadline"),
            @Result(property = "lastModifiedTime", column = "last_modified_time"),
            // 嵌套查询items
            @Result(property = "items", column = "id",
                    many = @Many(select = "selectItemsByEssayId")),
            // 嵌套查询peerCommentList
            @Result(property = "peerCommentList", column = "id",
                    many = @Many(select = "selectStudentsByEssayId"))
    })
    Essay selectEssayWithId(UUID id);

    // 查询Essay的题目项
    @Select("SELECT pid, sid, title, created_time, last_modified_time FROM essay WHERE pid = #{essayId}")
    Essay selectEssayByEssayId(UUID essayId);

    // 学生提交作业
    @Insert("INSERT INTO essay (pid, sid, title, created_time, last_modified_time) " +
            "VALUES (#{pid}, #{sid}, #{title}, NOW(6), NOW(6))")
    @Options(useGeneratedKeys = true, keyProperty = "pid")
    int insertEssay(Essay essay);

    // 关联学生与作业到中间表
    @Insert("INSERT INTO student_essay (student_id, essay_id) VALUES (#{studentId}, #{essayId})")
    int linkStudentEssay(@Param("studentId") UUID studentId, @Param("essayId") UUID essayId);

    // 查询学生所有提交的作业
    @Select("SELECT * FROM essay WHERE sid = #{studentId}")
    List<Essay> selectEssaysByStudentId(int studentId);

    @Select("SELECT e.* FROM essay e " +
            "INNER JOIN student_essay se ON e.pid = se.essay_id " +
            "WHERE se.student_id IN (SELECT sid FROM class_history WHERE class_id = #{classId})")
    List<Essay> selectClassEssays(int classId);
}
