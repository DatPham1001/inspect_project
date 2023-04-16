package com.imedia.service.order.enums;

public enum BuyingFeeEnum {
    SERVICE("SERVICE_FEE", "Phí vận chuyển"),
    INSURANCE("INSURANCE_FEE", "Phí khai giá"),
    COD_FEE("COD_FEE", "Phí thu hộ"),
    PLUS_FEE("SERVICE_FEE", "Phí thêm"),
    RETURN("RETURN_FEE", "Phí chuyển hoàn");


    public final String code;
    public final String message;

    BuyingFeeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
