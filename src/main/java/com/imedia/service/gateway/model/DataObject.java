package com.imedia.service.gateway.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DataObject {
    private String partnerOrderId;
    private String orderId;
    private String status;
    private String description;
    private Integer serviceFee= 0;
    private Integer insuranceFee = 0;
    private Integer returnFee= 0;
    private Integer plusFee= 0;
    private BigDecimal codAmount;
    private Integer codFee= 0;
    private Integer weight;
    private Long documentLinkId;
    private String serviceName;
    public String shipperPhone;
    public String shipperName;


}
