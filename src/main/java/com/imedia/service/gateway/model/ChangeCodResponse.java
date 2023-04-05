package com.imedia.service.gateway.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeCodResponse {
    //    {
//        "id_account_partner": 0,
//            "status": 1,
//            "message": "Tạo đơn thành công V2",
//            "order_id": "210828420221"
//    }
    private Integer id_account_partner;
    private Integer status;
    private String message;
    private String order_id;
}
