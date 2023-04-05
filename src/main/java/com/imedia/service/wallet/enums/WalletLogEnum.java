package com.imedia.service.wallet.enums;

public enum WalletLogEnum {
    ADD_CMS(1, "Cộng tiền qua CMS", "ACS"),

    ADD_COD(2, "Cộng tiền cho đơn hàng : ", "ACD"),

    ADD_VA(3, "Cộng tiền vào tài khoản VA : ", "AVA"),

    SUB_FEE(10, "Trừ phí cho đơn hàng : ", "SFE"),

    SUB_WDR(11, "Rút tiền vào tài khoản ngân hàng : ", "WDR"),

    SUB_CMS(12, "", ""),

    HOLD_ADD(20, "Cộng tạm giữ cho đơn hàng : ", "AHD"),

    HOLD_RVT(21, "Hủy tạm giữ cho đơn hàng : ", "RVH"),

    DEBT_ADD(30, "Không đủ số dư,ghi nợ cho đơn hàng : ", "ADE"),

    DEBT_SUB(31, "Trừ nợ cho đơn hàng : ", "SDE"),

    ;

    public final Integer code;
    public final String message;
    public final String prefix;

    WalletLogEnum(Integer code, String message, String prefix) {
        this.code = code;
        this.message = message;
        this.prefix = prefix;
    }
}
