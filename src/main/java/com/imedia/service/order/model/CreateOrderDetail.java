package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderDetail {
    private String address;
    private String province;
    private String provinceName;
    private String district;
    private String districtName;
    private String ward;
    private String wardName;
    private List<CreateOrderReceiver> receivers;
}
