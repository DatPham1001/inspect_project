package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterOrderRequest {
    private String searchKey;
    private Long shopAddressId;
    private String packCode;
    private Integer exportExcel;
    private String fromDate;
    private String toDate;
    private Integer orderStatus;
    private Integer page;
    private Integer size;
    private Integer orderBy;
    private String targetStatuses;
}
