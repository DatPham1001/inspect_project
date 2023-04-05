package com.imedia.service.order.enums;

import javax.xml.ws.WebServiceException;

public enum SellingFeeEnum {

    PORTER_FEE("PORTER_FEE", "Phí bốc dỡ hàng", 2, "isPorter"),
    HANDOVER_FEE("HANDOVER_FEE", "Phí giao tận tay", 11, "isDoorDeliver"),
    TRANSPORT_FEE("TRANSPORT_FEE", "Phí vận chuyển", 0, ""),
    REDELIVERY_FEE("REDELIVERY_FEE", "Phí giao lại", 3, ""),
    PICKUP_FEE("PICKUP_FEE", "Phí tới lấy hàng", 1, ""),
    STORAGE_FEE("STORAGE_FEE", "Phí lưu kho", 4, ""),
    COD_FEE("COD_FEE", "Phí thu hộ", 5, ""),
    INSURANCE_FEE("INSURANCE_FEE", "Phí khai giá", 6, ""),
    REFUND_FEE("REFUND_FEE", "Phí hoàn", 8, ""),
    OTHER_FEE("OTHER_FEE", "Phí khác", 9, ""),
    UPDATE_FEE("UPDATE_FEE", "Phí cập nhật đơn hàng", 7, ""),
    PARTIAL_FEE("PARTIAL_FEE", "Phí giao hàng một phần", 10, ""),
    DISCOUNT_FEE("DISCOUNT_FEE", "Phí giảm giá", 0, "");
    public final String code;
    public final String message;
    public final Integer type;
    public final String field;

    SellingFeeEnum(String code, String message, Integer type, String field) {
        this.code = code;
        this.message = message;
        this.type = type;
        this.field = field;
    }
    public static SellingFeeEnum codeOf(String code) throws IllegalArgumentException {
        for (SellingFeeEnum val : values()) {
            if (val.code.equalsIgnoreCase(code))
                return val;
        }
        throw new WebServiceException("Unknown" + code);
    }

    public static SellingFeeEnum typeOf(Integer type) throws IllegalArgumentException {
        for (SellingFeeEnum val : values()) {
            if (val.type.equals(type))
                return val;
        }
        throw new WebServiceException("Unknown" + type);
    }

    public static SellingFeeEnum fieldOf(String field) throws IllegalArgumentException {
        for (SellingFeeEnum val : values()) {
            if (val.field.equalsIgnoreCase(field))
                return val;
        }
        throw new WebServiceException("Unknown" + field);
    }
}
