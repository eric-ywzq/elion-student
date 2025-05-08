package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    private UUID id;
    private String name;
    private String gender;
    private String phone;
    private List<ClassHistory> classHistories;
    private List<String> comment;
    private Class currentClass;
    private List<Homework> HomeworkList;
}