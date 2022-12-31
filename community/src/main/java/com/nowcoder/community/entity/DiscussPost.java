package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

/**
* Description: 帖子实体类
* date: 2022/12/31 21:50
 * 
* @author: Deng
* @since JDK 1.8
*/
@Data
public class DiscussPost {
    private int id ;
    private int userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;
}
