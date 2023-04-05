package com.imedia.service.notify;

import com.google.gson.Gson;
import com.imedia.config.application.AppConfig;
import com.imedia.oracle.entity.OrderDetail;
import com.imedia.oracle.entity.OrderPartialRequest;
import com.imedia.oracle.entity.SvcOrderDetail;
import com.imedia.oracle.repository.OrderDetailRepository;
import com.imedia.service.notify.model.NotifyRequest;
import com.imedia.service.notify.model.NotifyResponse;
import com.imedia.service.notify.model.UpdateOrderPayload;
import com.imedia.util.AppUtil;
import com.imedia.util.CallServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class NotifyService {
    static final Logger logger = LogManager.getLogger(NotifyService.class);
    static final Gson gson = new Gson();
    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public NotifyService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    public NotifyResponse notifyChangeCodAppShip(SvcOrderDetail orderDetail, BigDecimal oldCod, BigDecimal newCod) {
        String response = null;
        try {
            OrderDetail shipOrderDetail = orderDetailRepository.findByOrderDetailCode(orderDetail.getSvcOrderDetailCode());
//            DecimalFormat decimalFormat = new DecimalFormat("###,###.###");
            NotifyRequest notifyRequest = new NotifyRequest();
            notifyRequest.setTitle("Đơn hàng thay đổi COD");
            String message = "Đơn hàng " + orderDetail.getSvcOrderDetailCode() + " đã thay đổi COD\n"
                    + "COD cũ : " + AppUtil.formatDecimal(oldCod) + " - COD mới : " + AppUtil.formatDecimal(newCod);
            notifyRequest.setMessage(message);
            notifyRequest.setType("CONFIRM_ORDER");
            String payLoad = gson.toJson(new UpdateOrderPayload(String.valueOf(shipOrderDetail.getOrderDetailCode()), shipOrderDetail.getShipId()));
            notifyRequest.setPayload(payLoad);
            response = CallServer.getInstance().post(AppConfig.getInstance().notify_ship_url, gson.toJson(notifyRequest));
        } catch (Exception e) {
            logger.info("======NOTIFY CHANGE COD EXCEPTION========" + orderDetail.getSvcOrderDetailCode(), e);
        }
        if (response != null) {
            NotifyResponse notifyResponse = gson.fromJson(response, NotifyResponse.class);
            return notifyResponse;
        }
        return null;
    }

    public NotifyResponse notifyCancelAppShip(SvcOrderDetail orderDetail) {
        String response = null;
        try {
            OrderDetail shipOrderDetail = orderDetailRepository.findByOrderDetailCode(orderDetail.getSvcOrderDetailCode());
            NotifyRequest notifyRequest = new NotifyRequest();
            notifyRequest.setTitle("Đơn hàng đã hủy");
            String message = "Đơn hàng " + orderDetail.getSvcOrderDetailCode() + " đã hủy bởi chủ shop";
            notifyRequest.setMessage(message);
            notifyRequest.setType("UPDATE_ORDER");
            String payLoad = gson.toJson(new UpdateOrderPayload(String.valueOf(shipOrderDetail.getOrderDetailCode()), shipOrderDetail.getShipId()));
            notifyRequest.setPayload(payLoad);
            response = CallServer.getInstance().post(AppConfig.getInstance().notify_ship_url, gson.toJson(notifyRequest));
        } catch (Exception e) {
            logger.info("======NOTIFY CANCEL EXCEPTION========" + orderDetail.getSvcOrderDetailCode(), e);
        }
        if (response != null) {
            NotifyResponse notifyResponse = gson.fromJson(response, NotifyResponse.class);
            return notifyResponse;
        }
        return null;
    }

    public NotifyResponse notifyRedeliveryAppShip(SvcOrderDetail orderDetail) {
        String response = null;
        try {
            OrderDetail shipOrderDetail = orderDetailRepository.findByOrderDetailCode(orderDetail.getSvcOrderDetailCode());
            NotifyRequest notifyRequest = new NotifyRequest();
            notifyRequest.setTitle("Yêu cầu giao lại đơn hàng");
            String message = "Đơn hàng " + orderDetail.getSvcOrderDetailCode() + " yêu cầu giao lại";
            notifyRequest.setMessage(message);
            notifyRequest.setType("REDELIVERY");
            String payLoad = gson.toJson(new UpdateOrderPayload(String.valueOf(shipOrderDetail.getOrderDetailCode()), shipOrderDetail.getShipId()));
            notifyRequest.setPayload(payLoad);
            response = CallServer.getInstance().post(AppConfig.getInstance().notify_ship_url, gson.toJson(notifyRequest));
        } catch (Exception e) {
            logger.info("======NOTIFY CANCEL EXCEPTION========" + orderDetail.getSvcOrderDetailCode(), e);
        }
        if (response != null) {
            NotifyResponse notifyResponse = gson.fromJson(response, NotifyResponse.class);
            return notifyResponse;
        }
        return null;
    }

    public NotifyResponse notifyPartialRequest(OrderPartialRequest partialRequest) {
        String response = null;
        try {
            OrderDetail shipOrderDetail = orderDetailRepository.findByOrderDetailCode(partialRequest.getOrderDetailCode());
            NotifyRequest notifyRequest = new NotifyRequest();
            String message = "";
            if (partialRequest.getIsConfirmed() == 1) {
                notifyRequest.setTitle("Chấp nhận yêu cầu giao hàng một phần");
                message = "Chủ shop đã chấp nhận yêu cầu giao hàng một phần của đơn hàng : " + partialRequest.getOrderDetailCode()
                        + " tiền COD giao hàng một phần " + AppUtil.formatDecimal(partialRequest.getPartialCod());
            } else {
                notifyRequest.setTitle("Từ chối yêu cầu giao hàng một phần");
                message = "Chủ shop đã từ chối yêu cầu giao hàng một phần của đơn hàng "
                        + partialRequest.getOrderDetailCode();
            }
            notifyRequest.setMessage(message);
            notifyRequest.setType("UPDATE_ORDER");
            String payLoad = gson.toJson(new UpdateOrderPayload(String.valueOf(shipOrderDetail.getOrderDetailCode()), shipOrderDetail.getShipId()));
            notifyRequest.setPayload(payLoad);
            response = CallServer.getInstance().post(AppConfig.getInstance().notify_ship_url, gson.toJson(notifyRequest));
        } catch (Exception e) {
            logger.info("======NOTIFY CANCEL EXCEPTION========" + partialRequest.getOrderDetailCode(), e);
        }
        if (response != null) {
            NotifyResponse notifyResponse = gson.fromJson(response, NotifyResponse.class);
            return notifyResponse;
        }
        return null;
    }

}
