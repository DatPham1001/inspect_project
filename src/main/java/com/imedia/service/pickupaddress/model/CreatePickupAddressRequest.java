package com.imedia.service.pickupaddress.model;

import com.imedia.model.BaseRequest;

public class CreatePickupAddressRequest extends BaseRequest {
    private String username;
    private Long shopAddressId;
    private String shopName;
    private String senderName;
    private String shopPhone;
    private String provinceCode;
    private String districtCode;
    private Integer status;
    private String wardCode;
    private String addressDetail;
    private Integer isDefault;

    public String getSenderName() {
        return senderName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Long getShopAddressId() {
        return shopAddressId;
    }

    public void setShopAddressId(Long shopAddressId) {
        this.shopAddressId = shopAddressId;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getWardCode() {
        return wardCode;
    }

    public void setWardCode(String wardCode) {
        this.wardCode = wardCode;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }
}
