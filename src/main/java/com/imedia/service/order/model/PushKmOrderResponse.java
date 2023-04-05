package com.imedia.service.order.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PushKmOrderResponse {
    private PushKmOrderDataResponse order;
    private List<PushKmOrderDetailDataResponse> orderDetails;
}
