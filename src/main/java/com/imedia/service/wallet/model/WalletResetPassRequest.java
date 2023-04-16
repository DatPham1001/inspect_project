package com.imedia.service.wallet.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletResetPassRequest {
    //    {
//        "username": "0908310776",
//            "password": "[md5password]",
//            "newpassword": "[new pasword clear]",
//            "client_identity_str": "53fd7d34-117e-435b-99de-0bd1647b2134",
//            "session_key": "*****",
//            "account_epurse_id": 124,
//            "otp_code": "[otp number]"
//    }
    private String username;
    private String phone;
    private String password = "";
    private String newpassword;
    private String client_identity_str = "";
    private String session_key;
    private Long account_epurse_id;
    private String otp_code;

}
