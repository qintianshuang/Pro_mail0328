package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UserAddress;

import java.util.List;

public interface UserAddressService {
    public void saveUserAddress(UserAddress userAddress);

    public List<UserAddress> userAddressList();

    void deleteUserAddress(String id);

    UserAddress getUserAddress(String id);

    void updateUserAddress(UserAddress userAddress);
}
