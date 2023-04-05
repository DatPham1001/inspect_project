package com.imedia.service.wallet.model;

public class WalletRegisterVA {
    //    {
//        "username": "0908310776",
//            "session_key": "[session_key]",
//            "customer_name": "[customer name]"
//    }
    private String username;
    private String session_key = "";
    private String customer_name;
    private String request_id;

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }
}
