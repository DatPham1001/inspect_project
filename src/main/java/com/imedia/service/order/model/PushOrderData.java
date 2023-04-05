package com.imedia.service.order.model;

import com.imedia.oracle.entity.SvcOrder;
import com.imedia.oracle.entity.SvcOrderDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PushOrderData {
    private SvcOrder order;
    private List<SvcOrderDetail> orderDetails;
//    private List<PushKmOrderDetailRequest> orderDetails;

    public PushOrderData(SvcOrder order, List<SvcOrderDetail> orderDetails) {
//    public PushKmOrderData(SvcOrder order, List<PushKmOrderDetailRequest> orderDetails) {
        this.order = order;
        this.orderDetails = orderDetails;
    }
}
