package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PeerComment {
    private int id;
    private int homeworkid;
    private int commenterid;
    private int commenteeid;
    private String comment;
    private String score;
    private String time;
}
