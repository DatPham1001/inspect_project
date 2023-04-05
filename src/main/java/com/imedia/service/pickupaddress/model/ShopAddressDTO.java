package com.imedia.service.pickupaddress.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ShopAddressDTO {
    private Long id;
    private Long shopId;
    private String name;
    private String phone;
    private String senderName;
    private String address;
    private String provinceCode;
    private Integer districtId;
    private Integer wardId;
    private String provinceName;
    private String districtName;
    private String wardName;
    private Integer status;
    private Integer isDefault;
}
