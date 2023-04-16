package com.imedia.service.order.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class PrintOrderRequest {
    private String orderCodes;
    @NonNull
    private Integer type;
    private String targetStatuses;
    private String searchKey;
    private Long shopAddressId;
    private String packCode;
    private String fromDate;
    private String toDate;
    private Integer orderStatus;
    private Integer orderBy;
}
