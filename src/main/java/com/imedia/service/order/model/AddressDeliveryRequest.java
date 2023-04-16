package com.imedia.service.order.model;

public class AddressDeliveryRequest {
    private String address;
    private String provinceName;
    private String provinceCode;
    private String districtName;
    private String districtCode;
    private String wardName;
    private String wardCode;

    public AddressDeliveryRequest() {
    }

    public AddressDeliveryRequest(String address, String provinceName, String provinceCode, String districtName, String districtCode, String wardName, String wardCode) {
        this.address = address;
        this.provinceName = provinceName;
        this.provinceCode = provinceCode;
        this.districtName = districtName;
        this.districtCode = districtCode;
        this.wardName = wardName;
        this.wardCode = wardCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public String getWardCode() {
        return wardCode;
    }

    public void setWardCode(String wardCode) {
        this.wardCode = wardCode;
    }
}
