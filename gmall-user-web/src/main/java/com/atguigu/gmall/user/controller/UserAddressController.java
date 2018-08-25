package com.atguigu.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class UserAddressController {

    @Reference
    private com.atguigu.gmall.service.UserAddressService userAddressService;

    @RequestMapping("/saveUserAddress")
    public Object saveUserAddress(){
        com.atguigu.gmall.bean.UserAddress userAddress = new com.atguigu.gmall.bean.UserAddress("0", "尚硅谷302","6","rrr","bbb","w");
        com.atguigu.gmall.bean.UserAddress userAddress1 = new com.atguigu.gmall.bean.UserAddress("0", "云立方302","6","rrr","bbb","w");

            userAddressService.saveUserAddress(userAddress);
            userAddressService.saveUserAddress(userAddress1);

        return "添加完成";
    }

    @RequestMapping("/userAddressList")
    public List<com.atguigu.gmall.bean.UserAddress> userAddressList(HttpServletRequest request){
        List<com.atguigu.gmall.bean.UserAddress> userAddressList = userAddressService.userAddressList();
        return userAddressList;
    }

    @RequestMapping("/deleteUserAddress")
    public Object deleteUserAddress(){
        String id = "1";
        userAddressService.deleteUserAddress(id);
        return "删除完成";
    }

    //@RequestMapping("UserAddress")
    public com.atguigu.gmall.bean.UserAddress getUserAddress(String id){
        com.atguigu.gmall.bean.UserAddress userAddress = userAddressService.getUserAddress(id);
        return userAddress;
    }

    @RequestMapping("/updateUserAddress")
    public Object updateUserAddress(){
        String id = "2";
        com.atguigu.gmall.bean.UserAddress userAddress = getUserAddress(id);
        userAddress.setUserAddress("黄田村");
        userAddress.setPhoneNum("12312312");
        userAddressService.updateUserAddress(userAddress);
        return "删除完成";
    }
}
