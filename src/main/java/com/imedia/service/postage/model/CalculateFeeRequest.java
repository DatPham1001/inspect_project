package com.imedia.service.postage.model;

import com.imedia.service.order.model.CreateOrderDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CalculateFeeRequest {
    private String packCode;
    private String shopOrderCode;
    private String pickName;
    private String pickPhone;
    private String pickAddress;
    private String pickProvince;
    private String pickDistrict;
    private String pickWard;
    private String expectShipperPhone;
    private Integer pickupType;
    private List<CreateOrderDetail> deliveryPoint;
}
