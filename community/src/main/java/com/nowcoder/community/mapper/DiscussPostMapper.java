package com.nowcoder.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
* Description: mapper接口
* date: 2022/12/23 13:43
 *
* @author: Deng
* @since JDK 1.8
*/

@Mapper
public interface DiscussPostMapper  {
    //根据userID查询，查看自己已发布的帖子，同时要支持分页
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);
    //@Param注解用于给参数取别名，如果只有一个参数，并且在动态sql中使用（<if>）则必须加别名。

    //查询有多少条帖子的方法
    int selectDiscussPostRows(@Param("userId") int userId);

    // 发布帖子
    int insertDiscussPosts(DiscussPost discussPost);

    //查看帖子详情
    DiscussPost selectDiscussPostById(int id);

    //更新帖子评论数量
    int updateCommentCount(int id, int commentCount);

    //更改类型
    int updateType(int id, int type);

    //更改帖子的状态
    int updateStatus(int id, int status);

    //更新帖子分数
    int updateScore(int id, double score);
}
