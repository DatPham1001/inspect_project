package com.imedia.service.order.model;

import com.imedia.oracle.entity.DetailProduct;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PrintOrderResponse {
    private BigDecimal orderCode;
    private String carrierOrderCode;
    private String carrierShortCode;
    private String barCodePrint;
    private String shopOrderId;
    private String shopName;
    private String shopPhone;
    private String shopAddress;
    private String shopProvince;
    private String shopDistrict;
    private String shopWard;
    private String shopProvinceCode;
    private String shopDistrictCode;
    private String shopWardCode;
    private String deliveryName;
    private String deliveryPhone;
    private String deliveryAddress;
    private String deliveryProvince;
    private String deliveryDistrict;
    private String deliveryWard;
    private String deliveryProvinceCode;
    private String deliveryDistrictCode;
    private String deliveryWardCode;
    private List<PrintOrderProduct> products;
    private Long weight;
    private Long length;
    private Long width;
    private Long height;
    private BigDecimal moneyToCollect;
    private BigDecimal totalFee;
    private BigDecimal cod;
    private Long requireNote;
    private String note;

}
