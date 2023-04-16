package com.imedia.service.user.model;

import com.imedia.oracle.reportentity.AppContractReport;
import com.imedia.service.pickupaddress.model.ShopAddressDTO;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class UserInfoResponse {
    private String name;
    private String username;
    private Integer isUpdated = 0;
    private String phoneOTP;
    private String avatar;
    private String birthday;
    private String company;
    private String email;
    private UserInfoAddressData address;
    private String identityCard;
    private String dateIdCard;
    private String placeIdCard;
    private String accessToken;
    private Integer partner;
    private Integer point;
    private Integer profileLevel;
    private Integer rating;
    private Integer sex;
    private Integer ship;
    private Integer shop;
    private String types;
    private Integer userGroupId;
    private Timestamp utimestamp;
    private Object walletInfo;
    private List<ShopAddressDTO> pickupAddress;
    private UserContractResponse contract;

}
