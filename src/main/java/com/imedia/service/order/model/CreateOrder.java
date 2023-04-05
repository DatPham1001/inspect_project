package com.imedia.service.order.model;

import com.imedia.service.pickupaddress.model.ShopAddressDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrder {
    private String requestId;
    private String packCode;
    private String packName;
    private String shopOrderCode = "";
    private Integer paymentType;
    //    private String pickPhone;
    private Integer pickupAddressId;
    //Custom
    private ShopAddressDTO shopAddress;
    //    private String pickAddress;
//    private String pickProvince;
//    private String pickDistrict;
//    private String pickWard;
    private Integer packType;
    private String expectShipperPhone = "";
    private Integer pickupType;
    private List<CreateOrderDetail> deliveryPoint;
}
