package com.example.mapper;

import com.example.entity.Homework;
import com.example.entity.HomeworkItem;
import com.example.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.UUID;

@Mapper
public interface HomeworkMapper {
    @Select("SELECT pid, id, type, title, content FROM homework WHERE pid = #{homeworkId}")
    Homework selectHomeworkBypid(UUID homeworkId);
    @Select("SELECT pid,title,submitted,total_score FROM essay_history WHERE pid = #{homeworkId}")
    List<Homework> selecthistorysBypid(UUID homeworkId);
    @Select("SELECT id, name,class FROM student WHERE id IN (SELECT commenter_id FROM homework_peer_comment WHERE homework_id = #{homeworkId})")
    List<Student> selectPeerCommentListByHomeworkId(UUID homeworkId);
    @Select("SELECT pid, id, type, title, content from homework limit 8")
    List<Homework> getHomeworkList();
}
