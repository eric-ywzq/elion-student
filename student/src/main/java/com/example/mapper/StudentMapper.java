package com.example.mapper;

import com.example.entity.ClassHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.UUID;

@Mapper
public interface StudentMapper {
    @Select("SELECT id,class,name FROM student WHERE id = #{studentId}")
    List<ClassHistory> selectClassByStudentId(UUID studentId);
}