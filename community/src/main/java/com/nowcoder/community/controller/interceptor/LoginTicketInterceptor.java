package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Date;


@Slf4j
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    //controller 之前执行
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.debug("进拦截器了");
        // 从cookie中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");

        log.debug("用户的ticket是{}",ticket);

        if (ticket != null) {
            // 查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 检查凭证是否有效
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 在本次请求中持有用户
                log.debug("本次登录用户信息为{}",user);
                hostHolder.setUser(user);//存到HostHolder

                // 构建用户认证的结果,并存入SecurityContext,以便于Security进行授权.
                //token里面存用户，用户密码，用户权限
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, user.getPassword(), userService.getAuthorities(user.getId()));
                Collection<? extends GrantedAuthority> authorities = userService.getAuthorities(user.getId());
                log.info("获取用户的权限是：{}",authorities);
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
                log.info("构建用户的认证结果完成");
            }
        }
        return true;
    }

    // 在Controller之后执行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();//获取到当前登录凭证对应的用户
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);//将用户对象返回给前端展示
        }
    }

    // 在TemplateEngine之后执行 清理
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
       SecurityContextHolder.clearContext();//清理数据
    }
}
