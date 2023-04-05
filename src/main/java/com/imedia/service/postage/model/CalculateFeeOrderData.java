package com.imedia.service.postage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalculateFeeOrderData {
    private static final long serialVersionUID = 1L;
    private String packCode;
    private String shopOrderCode;
    private String shopID;
    private String pickName;
    private String pickPhone;
    private String pickAddress;
    private String pickProvince;
    private String pickDistrict;
    private String pickWard;
    private String expectShipperPhone;
    private String pickupType;
    private List<CalculateFeeDeliveryPoint> deliveryPoint;
}
