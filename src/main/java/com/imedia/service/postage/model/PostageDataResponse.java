package com.imedia.service.postage.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostageDataResponse {
    private String code;
    private String name;
    private Integer status;
    private Integer type;
    private String time;
    private Integer settingId;
    private Integer maxDeliveryPoint;
    private Integer maxOrderPerDeliveryPoint;
}
