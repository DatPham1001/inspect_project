package com.imedia.service.wallet.model;

public class WalletRegisterVAResponse {
    //    {
//        "bank_code": "[mã ngân hàng]",
//            "bank_name": "[tên ngân hàng]",
//            "account_no": "[VA number]",
//            "account_name": "[tên tài khoản VA]"
//    }
    private String bank_code;
    private String bank_name;
    private String account_no;
    private String account_name;

    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }
}
