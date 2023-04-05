package com.imedia.service.postage.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublicCalculateFeeRequest {
    private String senderProvinceCode;
    private String senderDistrictCode;
    private String receiverProvinceCode;
    private String receiverDistrictCode;
    private Integer weight;
}
