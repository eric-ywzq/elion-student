package com.example.entity;

import lombok.*;

import java.util.List;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> list;      // 当前页数据
    private int pageNum;       // 当前页码
    private int pageSize;      // 每页记录数
    private long total;         // 总记录数
    private int totalPages;    // 总页数
}