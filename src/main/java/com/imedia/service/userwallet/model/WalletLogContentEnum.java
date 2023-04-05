package com.imedia.service.userwallet.model;

public enum WalletLogContentEnum {
    DEPOSIT_VA("Cộng tiền vào tài khoản VA "),
    WITHDRAW_BANK("Rút tiền vào tài khoản ngân hàng ");

    public final String message;

    WalletLogContentEnum(String message) {
        this.message = message;
    }
}
