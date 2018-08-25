package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UserDetails;

import java.util.List;

public interface UserDetailsService {
    public void saveUserDetails(UserDetails userDetails);

    public List<UserDetails> userDetailsList();

    void deleteUserDetails(String id);

    UserDetails getUserDetails(String id);

    void updateUserDetails(UserDetails userDetails);
}
