package com.imedia.service.userwallet.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterWalletLogRequest {
    private String username;
    private String orderId;
    private Integer exportExcel;
    private String fromDate;
    private String toDate;
    private Integer type;
    private Integer page;
    private Integer size;
    private Integer status;

}
