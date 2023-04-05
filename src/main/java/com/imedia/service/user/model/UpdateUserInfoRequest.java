package com.imedia.service.user.model;

import com.imedia.model.BaseRequest;

public class UpdateUserInfoRequest extends BaseRequest {
    private String username;
    private String name;
    private String avatar;
    private String birthday;
    private String company;
    private String phoneOTP;
    private String email;
    private String identityCard;
    private String dateIdCard;
    private String placeIdCard;
    private Integer sex;
    private UserInfoAddressData address;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneOTP() {
        return phoneOTP;
    }

    public void setPhoneOTP(String phoneOTP) {
        this.phoneOTP = phoneOTP;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDateIdCard() {
        return dateIdCard;
    }

    public void setDateIdCard(String dateIdCard) {
        this.dateIdCard = dateIdCard;
    }

    public String getPlaceIdCard() {
        return placeIdCard;
    }

    public void setPlaceIdCard(String placeIdCard) {
        this.placeIdCard = placeIdCard;
    }

    public UserInfoAddressData getAddress() {
        return address;
    }

    public void setAddress(UserInfoAddressData address) {
        this.address = address;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

}
