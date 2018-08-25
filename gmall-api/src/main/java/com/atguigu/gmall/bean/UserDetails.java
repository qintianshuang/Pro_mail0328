package com.atguigu.gmall.bean;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class UserDetails implements Serializable {
    @Id
    private String id;
    @Column
    private String idCard;
    @Column
    private String userId;
    @Column
    private String userPhone;
    @Column
    private String hometown;
    @Column
    private String addressId;
    @Column
    private String sex;
    @Column
    private String createTime;

    public UserDetails() {

    }

    public UserDetails(String id, String idCard, String userId, String userPhone,
                       String hometown, String addressId, String sex, String createTime) {
        this.id = id;
        this.idCard = idCard;
        this.userId = userId;
        this.userPhone = userPhone;
        this.hometown = hometown;
        this.addressId = addressId;
        this.sex = sex;
        this.createTime = createTime;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCard() {
        return this.idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhone() {
        return this.userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getHometown() {
        return this.hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getAddressId() {
        return this.addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "id='" + id + '\'' +
                ", idCard='" + idCard + '\'' +
                ", userId='" + userId + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", hometown='" + hometown + '\'' +
                ", addressId='" + addressId + '\'' +
                ", sex='" + sex + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
