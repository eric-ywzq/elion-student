package com.example.mapper;

import com.example.entity.ClassHistory;
import com.example.entity.Essay;
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
    Essay selectEssayWithid(UUID id);

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
}
