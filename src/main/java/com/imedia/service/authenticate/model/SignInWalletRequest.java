package com.imedia.service.authenticate.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInWalletRequest {
    //        "username": "0908310776",
//            "password": "e10adc3949ba59abbe56e057f20f883e",
//            "login_from": 0,
//            "client_identity_str": "8d19bea4-ed3e-47dd-a7e3-d5941585c7d5",
//            "session_key": "[login success session]",
//            "account_epurse_id": 0
//
    private String username;
    private String password;
    private Integer login_from;
    private String client_identity_str;
    private String session_key;
    private Integer account_epurse_id;


}
