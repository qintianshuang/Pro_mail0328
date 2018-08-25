package com.atguigu.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.service.UserAddressService;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserAddressSerciceImpl implements UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public void saveUserAddress(UserAddress userAddress) {
        userAddressMapper.insert(userAddress);
    }

    @Override
    public List<UserAddress> userAddressList() {
        return userAddressMapper.selectAll();
    }

    @Override
    public void deleteUserAddress(String id) {
        userAddressMapper.deleteByPrimaryKey(id);
    }

    @Override
    public UserAddress getUserAddress(String id) {
        return userAddressMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateUserAddress(UserAddress userAddress) {
        userAddressMapper.updateByPrimaryKey(userAddress);
    }

}

