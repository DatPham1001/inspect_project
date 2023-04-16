package com.imedia.service.notify.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotifyRequest {
//    {
//        "title": "HELLO ANH DUY",
//            "message": "fffff",
//            "type": "ACCOUNT_APPROVED",
//            "payload": "{\"orderId\": \" + order.getId() + \", \"shipperIds\": [\" + sSelectedIds + \"]}"
//    }
    private String title;
    private String message;
    private String type;
    private String payload;

}
