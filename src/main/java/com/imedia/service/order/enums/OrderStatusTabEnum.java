package com.imedia.service.order.enums;

public enum OrderStatusTabEnum {
    TOTAL(0, "Tất cả"),
    WAIT_TO_CONFIRM(900, "Chờ duyệt"),
    WAIT_TO_PICK(100, "Chờ lấy"),
    PICK_FAILED(102, "Lấy thất bại"),
    STORED(200, "Lưu kho"),
    DELIVERING(500, "Đang giao"),
    DELIVERY_FAILED(511, "Giao thất bại"),
    RETURN_WAIT(505, "Chờ hoàn"),
    DELIVERED(501, "Giao thành công"),
    RETURNING(502, "Đơn hoàn"),
    WAIT_DESTRUCTION(905, "Hoàn thất bại"),
    RETURNED(504, "Đã hoàn"),
    PROBLEM(2006, "Có vấn đề"),
    CANCEL_WAIT(901, "Chờ hủy"),
    CANCEL_CARRIER(924, "Hủy"),
    WEIGHT_WRONG(923, "Sai cân nặng"),
    DRAFT(1, "Lưu nháp"),

    ;
    public final Integer code;
    public final String message;

    OrderStatusTabEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public static OrderStatusTabEnum valueOf(int value){
        for (OrderStatusTabEnum val : values()) {
            if (val.code == value)
                return val;
        }
        return OrderStatusTabEnum.WAIT_TO_CONFIRM;
    }
}
