package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Description: 首页 返回用户名及其对应的帖子 map类型
* date: 2022/12/23 15:14
 *
* @author: Deng
* @since JDK 1.8
*/
@Controller
public class HomeController implements CommunityConstant {
    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private UserService userService;

    @Resource
    private LikeService likeService;


    @ApiOperation("获取首页内容")
    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(@ApiParam("视图") Model  model, Page page,
                               @RequestParam(name = "orderMode", defaultValue = "0") int orderMode) {
        //方法调用前，SpringMVC会自动实例化Model和Page,并将Page注入Model.
        //所以，在thymeleaf中可以直接访问Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode="+orderMode);

        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit(),orderMode);//前10条数据 优化再替换
        //再创建一个集合存放map
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (list != null){
            //遍历list帖子
            for(DiscussPost post:list){
                //每次遍历放进map里面
                Map<String, Object> map = new HashMap<>();//先实例化一个map
                map.put("post",post);
                //根据帖子去找用户 从帖子里面post 获得用户的id
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);

                //点赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);//把map一个个丢进list集合
            }
        }
        //装到model
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode", orderMode);
        return "/index";
    }
    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }

    /**
    * Description: 权限不足的时候跳转到此路径的页面
    * date: 2023/1/10 15:22
     * 返回的也是404页面
    * @author: Deng
    * @since JDK 1.8
    */
    @RequestMapping(path = "/denied", method = RequestMethod.GET)
    public String getDeniedPage() {
        return "/error/404";
    }


}
