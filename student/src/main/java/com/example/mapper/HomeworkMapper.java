package com.example.mapper;

import com.example.entity.ClassHistory;
import com.example.entity.Essay;
import com.example.entity.Homework;
import com.example.entity.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface HomeworkMapper {
    @Select("SELECT pid, id, type, title, content FROM homework WHERE pid = #{homeworkId}")
    Essay selectHomeworkBypid(UUID homeworkId);
    @Select("SELECT pid,title,submitted,total_score FROM essay_history WHERE pid = #{homeworkId}")
    List<Essay> selecthistorysBypid(UUID homeworkId);
    @Select("SELECT id, name,class FROM student WHERE id IN (SELECT commenter_id FROM homework_peer_comment WHERE homework_id = #{homeworkId})")
    List<Student> selectPeerCommentListByHomeworkId(UUID homeworkId);
    @Select("SELECT pid, id, type, title, content from homework limit 8")
    List<Essay> getHomeworkList();

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
    List<Essay> selectEssaysByEssayId(UUID essayId);

    // 查询Essay的学生评价列表
    @Select("SELECT s.* FROM student s JOIN homework_peer_comment esc ON s.id = esc.commentee_id WHERE esc.homework_id = #{essayId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "gender", column = "gender"),
            @Result(property = "phone", column = "phone"),
            // 嵌套查询classHistories
            @Result(property = "classHistories", column = "id",
                    many = @Many(select = "selectClassHistoriesByStudentId"))
    })
    List<Student> selectStudentsByEssayId(UUID essayId);

    // 学生提交作业
    @Insert("INSERT INTO essay (pid, sid, title, created_time, last_modified_time) " +
            "VALUES (#{pid}, #{sid}, #{title}, NOW(6), NOW(6))")
    @Options(useGeneratedKeys = true, keyProperty = "pid")
    int insertEssay(Essay essay);

    // 关联学生与作业到中间表
    @Insert("INSERT INTO student_essay (student_id, essay_id) VALUES (#{studentId}, #{essayId})")
    int linkStudentEssay(@Param("studentId") UUID studentId, @Param("essayId") UUID essayId);

    // 更新作业修改时间
    @Update("UPDATE essay SET title = #{title}, last_modified_time = NOW(6) WHERE pid = #{pid}")
    int updateEssayTitle(@Param("pid") int pid, @Param("title") String title);

    // 查询学生所有提交的作业
    @Select("SELECT * FROM essay WHERE sid = #{studentId}")
    List<Essay> selectEssaysByStudentId(int studentId);

    // 检查是否存在重复提交（根据业务需求）
    @Select("SELECT COUNT(*) FROM essay WHERE sid = #{studentId} AND title = #{title}")
    int countDuplicateSubmission(@Param("studentId") int studentId, @Param("title") String title);

    // 删除作业提交（根据权限）
    @Delete("DELETE FROM essay WHERE pid = #{pid} AND sid = #{studentId}")
    int deleteEssay(@Param("pid") int pid, @Param("studentId") int studentId);

    // ----------- 其他常用扩展操作 -----------

    // 查询作业模板详情（带关联的学生提交记录）
    @Select("SELECT h.* FROM homework h WHERE h.pid = #{homeworkId}")
    @Results({
            @Result(property = "pid", column = "pid"),
            @Result(property = "submittedEssays", column = "pid",
                    many = @Many(select = "selectEssaysByHomeworkId"))
    })
    Homework selectHomeworkWithSubmissions(int homeworkId);

    @Select("SELECT e.* FROM essay e " +
            "INNER JOIN student_essay se ON e.pid = se.essay_id " +
            "WHERE se.student_id IN (SELECT sid FROM class_history WHERE class_id = #{classId})")
    List<Essay> selectClassEssays(int classId);
}
