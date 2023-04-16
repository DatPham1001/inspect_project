package com.imedia.service.wallet.enums;

public enum WalletSubTypeCode {
    SUB_TYPE_ADD_HOLDING_ORDER("8024", "Cộng tạm giữ vận đơn"),
    SUB_TYPE_ADD_HOLDING_WITHDRAW("8025", "Cộng tạm giữ rút tiền"),
    SUB_TYPE_SUBTRACT_HOLDING_ORDER("8026", "Trừ tạm giữ vận đơn "),
    SUB_TYPE_SUBTRACT_HOLDING_WITHDRAW("8027", "Trừ tạm giữ rút tiền"),
    SUB_TYPE_ADD_EPURSE_COD("8028", "Cộng tiền COD"),
    SUB_TYPE_ADD_EPURSE_VA("8029", " Cộng nạp tiền - VA"),
    SUB_TYPE_SUBTRACT_EPURSE_WITHDRAW("8031", " Trừ số dư rút tiền ví "),
    SUB_TYPE_SUBTRACT_EPURSE_FEE("8034", "Trừ phí đơn hàng");

    public final String code;
    public final String message;

    WalletSubTypeCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
