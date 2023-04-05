package com.imedia.service.order.model;

import com.imedia.service.postage.model.CalculateFeeResponseDetailAPI;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateOrderFileFee {
    private String packageCode;
    private String packageName;
    private Integer priceSettingId;
    private BigDecimal totalFee;
    //Fee
    CalculateFeeResponseDetailAPI feeDetail;

}
