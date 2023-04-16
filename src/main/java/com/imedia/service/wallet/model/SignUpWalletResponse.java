package com.imedia.service.wallet.model;

public class SignUpWalletResponse {
    //    {
//        "account_epurse_id": 973,
//            "acc_identify": "0933045686",
//            "display_name": "Amokachi",
//            "acc_pwd": "***",
//            "cell_phone": "0933045686",
//            "acc_email": "vietda1@imediatech.com.vn",
//            "login_from": 0,
//            "acc_status": 0,
//            "receive_notify": 1,
//            "session_key": "dca3770d051c495b90b91d88171c20ad"
//    }
    private Integer account_epurse_id;
    private Integer login_from;
    private Integer acc_status;
    private Integer receive_notify;
    private String acc_identify;
    private String acc_email;
    private String display_name;
    private String acc_pwd;
    private String cell_phone;
    private String session_key;

    public Integer getAccount_epurse_id() {
        return account_epurse_id;
    }

    public void setAccount_epurse_id(Integer account_epurse_id) {
        this.account_epurse_id = account_epurse_id;
    }

    public Integer getLogin_from() {
        return login_from;
    }

    public void setLogin_from(Integer login_from) {
        this.login_from = login_from;
    }

    public Integer getAcc_status() {
        return acc_status;
    }

    public void setAcc_status(Integer acc_status) {
        this.acc_status = acc_status;
    }

    public Integer getReceive_notify() {
        return receive_notify;
    }

    public void setReceive_notify(Integer receive_notify) {
        this.receive_notify = receive_notify;
    }

    public String getAcc_identify() {
        return acc_identify;
    }

    public void setAcc_identify(String acc_identify) {
        this.acc_identify = acc_identify;
    }

    public String getAcc_email() {
        return acc_email;
    }

    public void setAcc_email(String acc_email) {
        this.acc_email = acc_email;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getAcc_pwd() {
        return acc_pwd;
    }

    public void setAcc_pwd(String acc_pwd) {
        this.acc_pwd = acc_pwd;
    }

    public String getCell_phone() {
        return cell_phone;
    }

    public void setCell_phone(String cell_phone) {
        this.cell_phone = cell_phone;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }
}
