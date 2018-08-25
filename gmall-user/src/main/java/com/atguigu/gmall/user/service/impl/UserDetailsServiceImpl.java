package com.atguigu.gmall.user.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.UserDetails;
import com.atguigu.gmall.service.UserDetailsService;
import com.atguigu.gmall.user.mapper.UserDetailsMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserDetailsMapper userDetailsMapper;

    @Override
    public void saveUserDetails(UserDetails userDetails) {
        userDetailsMapper.insert(userDetails);
    }

    @Override
    public List<UserDetails> userDetailsList() {
        return userDetailsMapper.selectAll();
    }

    @Override
    public void deleteUserDetails(String id) {
        userDetailsMapper.deleteByPrimaryKey(id);
    }

    @Override
    public UserDetails getUserDetails(String id) {
        return userDetailsMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateUserDetails(UserDetails userDetails) {
        userDetailsMapper.updateByPrimaryKey(userDetails);
    }
}
