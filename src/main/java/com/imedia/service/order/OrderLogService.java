package com.imedia.service.order;

import com.google.gson.Gson;
import com.imedia.oracle.entity.OrderDetail;
import com.imedia.oracle.entity.OrderLog;
import com.imedia.oracle.entity.SvcOrderDetail;
import com.imedia.oracle.entity.SvcOrderLog;
import com.imedia.oracle.repository.OrderDetailRepository;
import com.imedia.oracle.repository.OrderLogRepository;
import com.imedia.oracle.repository.SvcOrderLogRepository;
import com.imedia.service.order.model.FilterOrderDetailResponse;
import com.imedia.service.order.model.FilterOrderResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderLogService {
    static final Logger logger = LogManager.getLogger(OrderLogService.class);
    static final Gson gson = new Gson();
    private final SvcOrderLogRepository orderLogRepository;
    private final OrderLogRepository shipOrderLogRepository;
    private final OrderDetailRepository shipOrderDetailRepository;

    @Autowired
    public OrderLogService(SvcOrderLogRepository orderLogRepository, OrderLogRepository shipOrderLogRepository, OrderDetailRepository shipOrderDetailRepository) {
        this.orderLogRepository = orderLogRepository;
        this.shipOrderLogRepository = shipOrderLogRepository;
        this.shipOrderDetailRepository = shipOrderDetailRepository;
    }

    public void insertOrderLog(List<SvcOrderDetail> svcOrderDetails, String note, int status) {
        for (SvcOrderDetail svcOrderDetail : svcOrderDetails) {
            try {
                SvcOrderLog orderLog = new SvcOrderLog();
                orderLog.setCreatedBy(BigDecimal.valueOf(svcOrderDetail.getShopId()));
                orderLog.setSvcOrderCode(svcOrderDetail.getSvcOrderId());
                orderLog.setSvcOrderDetailCode(svcOrderDetail.getSvcOrderDetailCode());
                orderLog.setFromStatus(svcOrderDetail.getStatus());
                orderLog.setToStatus(status);
                orderLog.setCreatedByType(2);
                orderLog.setRollbackFlag(0);
                orderLog.setNote(note);
                SvcOrderLog svcOrderLog = orderLogRepository.save(orderLog);
                logger.info("=======ORDER TRACKING SAVE DB========" + gson.toJson(svcOrderLog));
            } catch (Exception e) {
                logger.info("=======ORDER INSERT TRACKING EXCEPTION======= :  " + svcOrderDetail.getSvcOrderDetailCode(), e);
            }
        }
    }

    public void insertOrderLog(SvcOrderDetail svcOrderDetail, String note, int status) {
        try {
            SvcOrderLog orderLog = new SvcOrderLog();
            orderLog.setCreatedBy(BigDecimal.valueOf(svcOrderDetail.getShopId()));
            orderLog.setSvcOrderCode(svcOrderDetail.getSvcOrderId());
            orderLog.setSvcOrderDetailCode(svcOrderDetail.getSvcOrderDetailCode());
            orderLog.setFromStatus(svcOrderDetail.getStatus());
            orderLog.setToStatus(status);
            orderLog.setCreatedByType(2);
            orderLog.setRollbackFlag(0);
            orderLog.setNote(note);
            SvcOrderLog svcOrderLog = orderLogRepository.save(orderLog);
            logger.info("======= ORDER TRACKING SAVE DB========" + gson.toJson(svcOrderLog));
        } catch (Exception e) {
            logger.info("======= ORDER INSERT TRACKING EXCEPTION======= :  " + svcOrderDetail.getSvcOrderDetailCode(), e);
        }
    }

    //Insert log for confirm
    public void insertOrderLog(FilterOrderResponse orderResponse, List<FilterOrderDetailResponse> svcOrderDetails,
                               int status, String note) {
        for (FilterOrderDetailResponse svcOrderDetail : svcOrderDetails) {
            try {
                SvcOrderLog orderLog = new SvcOrderLog();
                orderLog.setCreatedBy(BigDecimal.valueOf(orderResponse.getShopId()));
                orderLog.setSvcOrderCode(orderResponse.getSvcOrderId());
                orderLog.setSvcOrderDetailCode(svcOrderDetail.getSvcOrderDetailCode());
                orderLog.setFromStatus(svcOrderDetail.getStatus().intValue());
                orderLog.setToStatus(status);
                orderLog.setCreatedByType(2);
                orderLog.setRollbackFlag(0);
                orderLog.setNote(note);

                SvcOrderLog svcOrderLog = orderLogRepository.save(orderLog);
                logger.info("======= ORDER TRACKING SAVE DB========" + gson.toJson(svcOrderLog));
            } catch (Exception e) {
                logger.info("======= ORDER INSERT TRACKING EXCEPTION======= :  " + svcOrderDetail.getSvcOrderDetailCode(), e);
            }
        }
    }

    public void insertShipOrderLog(SvcOrderDetail svcOrderDetail, String note, int fromStatus, int toStatus) {
        try {
            OrderDetail orderDetail = shipOrderDetailRepository.findByOrderDetailCode(svcOrderDetail.getSvcOrderDetailCode());
            OrderLog orderLog = new OrderLog();
            orderLog.setToStatus(toStatus);
            orderLog.setFromStatus(fromStatus);
            orderLog.setCreateBy(svcOrderDetail.getShopId());
            orderLog.setOrderDetailId(orderDetail.getId());
            orderLog.setOrderId(orderDetail.getOrderId());
            orderLog.setShipId(orderDetail.getShipId());
            orderLog.setNote(note);
            orderLog.setRollbackFlag(0);
            if (orderLog.getToStatus().equals(orderLog.getFromStatus()))
                orderLog.setSuccessFlag(2);
            else orderLog.setSuccessFlag(0);
            orderLog.setCreateByType(3);
            OrderLog result = shipOrderLogRepository.save(orderLog);
            logger.info("======INSERT SHIP LOG======" + svcOrderDetail.getSvcOrderDetailCode() + "||" + gson.toJson(orderLog));
        } catch (Exception e) {
            logger.info("======INSERT SHIP LOG EXCEPTION======" + svcOrderDetail.getSvcOrderDetailCode(), e);
        }
    }

    public void insertShipOrderLog(SvcOrderDetail svcOrderDetail, OrderDetail shipOrderDetail, String note, int toStatus) {
        try {
            OrderLog orderLog = new OrderLog();
            orderLog.setToStatus(toStatus);
            orderLog.setFromStatus(shipOrderDetail.getStatus());
            orderLog.setCreateBy(svcOrderDetail.getShopId());
            orderLog.setOrderDetailId(shipOrderDetail.getId());
            orderLog.setOrderId(shipOrderDetail.getOrderId());
            orderLog.setShipId(shipOrderDetail.getShipId());
            orderLog.setNote(note);
            orderLog.setRollbackFlag(0);
            if (orderLog.getToStatus().equals(orderLog.getFromStatus()))
                orderLog.setSuccessFlag(2);
            else orderLog.setSuccessFlag(0);
            orderLog.setCreateByType(3);
            OrderLog result = shipOrderLogRepository.save(orderLog);
            logger.info("======INSERT SHIP LOG======" + svcOrderDetail.getSvcOrderDetailCode() + "||" + gson.toJson(orderLog));
        } catch (Exception e) {
            logger.info("======INSERT SHIP LOG EXCEPTION======" + svcOrderDetail.getSvcOrderDetailCode(), e);
        }
    }
}
