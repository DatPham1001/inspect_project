package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class OrderTrackingResponse {
    private long id;
    private Integer toStatus;
    private Integer fromStatus;
    private String statusName;
    private String note;
    private BigDecimal svcOrderCode;
    private BigDecimal svcOrderDetailCode;
    private List<String> images;
    private Timestamp createdDate;
}
