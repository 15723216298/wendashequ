package com.chowzzx.wendashequ.controller;

import com.chowzzx.wendashequ.dto.AccessTokenDTO;
import com.chowzzx.wendashequ.dto.GithubUser;
import com.chowzzx.wendashequ.mapper.UserMapper;
import com.chowzzx.wendashequ.model.User;
import com.chowzzx.wendashequ.provide.GithubProvide;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author Chowzzx
 * @date 2019/10/15 - 5:57 PM
 */
@Controller
public class AuthorizeController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    private GithubProvide githubProvide;

    @Value("${github.client.id}")  //在配置文件中注入，在这里使用value注解使用
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @GetMapping("/callback")
    public String callback(@RequestParam(name="code") String code,
                           @RequestParam(name="state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response){
        AccessTokenDTO tokenDTO = new AccessTokenDTO();
        tokenDTO.setClient_id(clientId);
        tokenDTO.setClient_secret(clientSecret);
        tokenDTO.setCode(code);
        tokenDTO.setRedirect_uri(redirectUri);
        tokenDTO.setState(state);
        String accessToken = githubProvide.getToken(tokenDTO);
        GithubUser githubUser = githubProvide.getUser(accessToken);
        if(githubUser != null){
           //登录成功
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAccountId(String.valueOf(githubUser.getId()));
            userMapper.insert(user);  //将"token"插入数据库这个过程就相当于写入session，所以不需要session了
            response.addCookie(new Cookie("token",token));
            // request.getSession().setAttribute("user",githubUser);  //将用户信息存在session中，框架会自动生成一个cookie
            return "redirect:/";
        }else{
            //登录失败，重新登录
            return "redirect:/";
        }
    }
}
