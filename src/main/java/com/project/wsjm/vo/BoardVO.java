package com.project.wsjm.vo;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BoardVO {

    private String writer;
    private int num;
    private String title;
    private String content;
    private Date regdate;

}