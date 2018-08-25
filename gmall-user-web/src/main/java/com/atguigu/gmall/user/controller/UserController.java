package com.atguigu.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class UserController {

    @Reference
    private com.atguigu.gmall.service.UserService userService;

    @RequestMapping("/userInfoList")
    public List<com.atguigu.gmall.bean.UserInfo> userInfoList(HttpServletRequest request){
        List<com.atguigu.gmall.bean.UserInfo> userInfoList = userService.userInfoList();
        return userInfoList;
    }

}
