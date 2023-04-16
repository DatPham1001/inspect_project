package com.imedia.service.pickupaddress.model;

import org.springframework.stereotype.Component;

@Component
public interface AddressDataOM {
    Integer getWardId();
    Integer getProvinceId();
    Integer getDistrictId();
}
