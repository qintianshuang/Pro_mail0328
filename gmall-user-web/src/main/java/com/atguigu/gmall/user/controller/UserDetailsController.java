package com.atguigu.gmall.user.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class UserDetailsController {

    @Reference
    private com.atguigu.gmall.service.UserDetailsService userDetailsService;

    @RequestMapping("/saveUserDetails")
    public Object saveUserDetails(){
        com.atguigu.gmall.bean.UserDetails userDetails = new com.atguigu.gmall.bean.UserDetails("0","23423","5","235423452","西乡三组","45","12","2018.1.1");
        com.atguigu.gmall.bean.UserDetails userDetails1 = new com.atguigu.gmall.bean.UserDetails("0","678578","7","956785678567","西乡三组","13","15","2018.1.1");

        userDetailsService.saveUserDetails(userDetails);
        userDetailsService.saveUserDetails(userDetails1);

        return "添加完成";
    }

    @RequestMapping("/userDetailsList")
    public List<com.atguigu.gmall.bean.UserDetails> userDetailsList(HttpServletRequest request){
        List<com.atguigu.gmall.bean.UserDetails> userDetailsList = userDetailsService.userDetailsList();
        return userDetailsList;
    }

    @RequestMapping("/deleteUserDetails")
    public Object deleteUserDetails(){
        String id = "1";
        userDetailsService.deleteUserDetails(id);
        return "删除完成";
    }

    //@RequestMapping("UserDetails")
    public com.atguigu.gmall.bean.UserDetails getUserDetails(String id){
        com.atguigu.gmall.bean.UserDetails userDetails = userDetailsService.getUserDetails(id);
        return userDetails;
    }

    @RequestMapping("/updateUserDetails")
    public Object updateUserDetails(){
        String id = "2";
        com.atguigu.gmall.bean.UserDetails userDetails = getUserDetails(id);
        userDetails.setHometown("黄田村2323");
        userDetails.setSex("50");
        userDetailsService.updateUserDetails(userDetails);
        return "删除完成";
    }
}
