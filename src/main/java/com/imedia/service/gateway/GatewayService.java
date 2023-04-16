package com.imedia.service.gateway;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.oracle.dao.OrderDAO;
import com.imedia.oracle.entity.AppUser;
import com.imedia.oracle.entity.Carrier;
import com.imedia.oracle.entity.SvcOrderDetail;
import com.imedia.service.gateway.model.*;
import com.imedia.service.order.FindShipService;
import com.imedia.service.order.model.CreateOrder;
import com.imedia.service.order.model.CreateOrderDetail;
import com.imedia.service.order.model.CreateOrderReceiver;
import com.imedia.service.order.model.OrderDetailProduct;
import com.imedia.service.postage.PostageService;
import com.imedia.util.CallServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;

@Service
public class GatewayService {
    static final Gson gson = new Gson();
    static final Logger logger = LogManager.getLogger(GatewayService.class);
    static final Gson gson2 = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private final PostageService postageService;
    private final FindShipService findShipService;
    private final OrderDAO orderDAO;

    @Autowired
    public GatewayService(PostageService postageService, FindShipService findShipService, OrderDAO orderDAO) {
        this.postageService = postageService;
        this.findShipService = findShipService;
        this.orderDAO = orderDAO;
    }

    public boolean changeCod(SvcOrderDetail orderDetail, BigDecimal newCod, Carrier carrier) {
        try {
            //TODO Cập nhật COD vào đơn hagnf của appship
//        if(carrier.getCode().equals("HLS")){
//
//        }
            if (carrier.getSpecialServices().contains("2") && carrier.getCodUrl() != null
                    && !carrier.getCodUrl().isEmpty()) {
                ChangeCodRequest data = new ChangeCodRequest();
                data.setOrder_id(String.valueOf(orderDetail.getSvcOrderDetailCode()));
                data.setOrder_code(orderDetail.getCarrierOrderId());
                data.setConfirm_type(9);
                data.setCod_amount(newCod);
                data.setId_account_partner(orderDetail.getIdAccountCarrier());
                HashMap<String, Object> request = new HashMap<>();
                request.put("data", gson.toJson(data));
                request.put("type", 2);
                String response = "";
                try {
                    String param = URLEncoder.encode(gson.toJson(request), "UTF-8");
                    logger.info("=======POST URL=======" + carrier.getCodUrl());
                    logger.info("=======POST DATA======= CHANGE COD" + gson.toJson(request));
                    response = CallServer.getInstance().postWithParam(carrier.getCodUrl().replaceAll("\\?",
                            "") + "?data=" + param);
                } catch (Exception e) {
                    logger.info("=========CHANGE COD REQUEST EXCEPTION=======" + request.toString(), e);

                }
                if (response != null && !response.isEmpty()) {
                    ChangeCodResponse changeCodResponse = gson.fromJson(response, ChangeCodResponse.class);
                    if (changeCodResponse.getStatus() == 1)
                        return true;
                }
            } else
                logger.info("========CHANGE COD FAILED=========" + orderDetail.getSvcOrderDetailCode() + "|| Carrier " + gson.toJson(carrier));

        } catch (Exception e) {
            logger.info("========CHANGE COD EXCEPTION========" + orderDetail.getSvcOrderDetailCode(), e);
        }
        return false;
    }

//    public BaseResponse handleFindShip(SvcOrder order, List<SvcOrderDetail> orderDetails, AppUser appUser) {
//        PushOrderData pushOrderData = new PushOrderData(order, orderDetails);
//        String response = "";
//        try {
//            response = CallServer.getInstance()
//                    .postUnlimitTimeout(AppConfig.getInstance().findShipUrl, gson2.toJson(pushOrderData));
//        } catch (Exception e) {
//            logger.info("========PUSH KM ORDER EXCEPTION=======" + order.getOrderCode(), e);
//            //Revert holding
//            return new BaseResponse(611);
//        }
//        PushKmOrderBaseResponse pushKmOrderResponse = gson.fromJson(response, PushKmOrderBaseResponse.class);
//        //status response 0 thành công 1 thất bại do lỗi 2 là đang tìm ship
//        //Sẽ có luồng khác chờ queue tìm ship để cập nahatj trạng thái
//        //Thành công và tìm được ship luôn
//        if (pushKmOrderResponse.getCode() == 0) {
//            PushKmOrderResponse data = pushKmOrderResponse.getData();
//            //Cập nhật trạng thái đơn
//            if (order.getPickupType() == 2)
//                order.setStatus(OrderStatusEnum.WAIT_TO_PICK.code);
//            else
//                order.setStatus(OrderStatusEnum.WAIT_TO_BRING.code);
//            order.setExpectShipId(Long.valueOf(data.getOrder().getShipId()));
//            orderDetails.forEach(o -> {
//                o.setOldStatus(o.getStatus());
//                if (order.getPickupType() == 2)
//                    o.setStatus(OrderStatusEnum.WAIT_TO_PICK.code);
//                else o.setStatus(OrderStatusEnum.WAIT_TO_BRING.code);
//                o.setIdAccountCarrier(data.getOrder().getShipId());
//                for (PushKmOrderDetailDataResponse pushKmOrderDetailDataResponse : data.getOrderDetails()) {
//                    if (o.getSvcOrderDetailCode().equals(pushKmOrderDetailDataResponse.getOrderDetailCode()))
//                        o.setCarrierOrderId(pushKmOrderDetailDataResponse.getOrderDetailShortId());
//                }
//            });
//            orderRepository.save(order);
//            orderDetailRepository.saveAll(orderDetails);
//            return new BaseResponse(600, order);
//        } else
//            //Đang tìm ship
//            if (pushKmOrderResponse.getCode() == 2) {
//                PushKmOrderResponse data = pushKmOrderResponse.getData();
//                order.setStatus(OrderStatusEnum.FINDING_SHIP.code);
//                orderDetails.forEach(o -> {
//                    o.setOldStatus(o.getStatus());
//                    o.setStatus(OrderStatusEnum.FINDING_SHIP.code);
//                    for (PushKmOrderDetailDataResponse pushKmOrderDetailDataResponse : data.getOrderDetails()) {
//                        if (o.getSvcOrderDetailCode().equals(pushKmOrderDetailDataResponse.getOrderDetailCode()))
//                            o.setCarrierOrderId(pushKmOrderDetailDataResponse.getOrderDetailShortId());
//                    }
//                });
//                orderRepository.save(order);
//                orderDetailRepository.saveAll(orderDetails);
//                return new BaseResponse(612, order);
//            }
//            //Lỗi phía app ship không tạo được đơn cho ship
//            else {
//                order.setStatus(OrderStatusEnum.WAIT_TO_CONFIRM.code);
//                if (pushKmOrderResponse.getData() != null)
//                    order.setExpectShipId(Long.valueOf(pushKmOrderResponse.getData().getOrder().getShipId()));
//                orderDetails.forEach(o -> {
//                    o.setOldStatus(o.getStatus());
//                    o.setStatus(OrderStatusEnum.WAIT_TO_CONFIRM.code);
//                });
//                orderRepository.save(order);
//                orderDetailRepository.saveAll(orderDetails);
//                //Revert holding
//                for (SvcOrderDetail orderDetail : orderDetails) {
//                    //gỡ tạm giữ
//                    WalletLog walletLog = walletLogRepository
//                            .findByOrderDetailIdAndTypeAndIsDeleted(orderDetail.getSvcOrderDetailCode(), BigDecimal.valueOf(20), 1).get(0);
//                    try {
//                        walletService.revertHoldingBalance(walletLog, appUser);
//                    } catch (Exception e) {
//                        logger.info("======REVERT HOLDING BALANCE EXPCETION======" + orderDetail.getSvcOrderDetailCode(), e);
//                    }
//                }
//                return new BaseResponse(611);
//            }
//    }

    public boolean cancelOrder(SvcOrderDetail orderDetail, Carrier carrier) {
        try {
            CancelOrderGWRequest cancelOrderGWRequest = new CancelOrderGWRequest();
            cancelOrderGWRequest.setOrder_id(String.valueOf(orderDetail.getSvcOrderDetailCode()));
            cancelOrderGWRequest.setPartner_order_id(orderDetail.getCarrierOrderId());
            cancelOrderGWRequest.setTransporter_id(orderDetail.getIdAccountCarrier());
            cancelOrderGWRequest.setTransporter_code(carrier.getCode());
            if (carrier.getCode().equals("HOLA1") || carrier.getCode().equals("HLS1"))
                cancelOrderGWRequest.setTransporter_code(carrier.getCode().replace("1", ""));
            cancelOrderGWRequest.setId_account_partner(orderDetail.getIdAccountCarrier());
            HashMap<String, Object> request = new HashMap<>();
            request.put("data", gson.toJson(cancelOrderGWRequest));
            request.put("type", 2);
            String response = null;
            try {
                String param = URLEncoder.encode(gson.toJson(request), "UTF-8");
                logger.info("=======POST URL=======" + carrier.getGatewayUrl());
                logger.info("=======POST DATA======= CANCEL" + gson.toJson(request));
                response = CallServer.getInstance().postWithParam(carrier.getGatewayUrl().replaceAll("\\?",
                        "") + "?data=" + param);
            } catch (Exception e) {
                logger.info("=========CANCEL ORDER REQUEST EXCEPTION=======" + request.toString(), e);
            }
            if (response != null) {
                ChangeCodResponse changeCodResponse = gson.fromJson(response, ChangeCodResponse.class);
                if (changeCodResponse.getStatus() == 1)
                    return true;
            }
        } catch (Exception e) {
            logger.info("========CANCEL ORDER EXCEPTION========" + orderDetail.getSvcOrderDetailCode(), e);
        }
        return false;
    }

    public boolean redelivery(SvcOrderDetail orderDetail, Carrier carrier) {
        try {
            RedeliveryGWRequest redeliveryGWRequest = new RedeliveryGWRequest();
            redeliveryGWRequest.setOrder_id(String.valueOf(orderDetail.getSvcOrderDetailCode()));
            redeliveryGWRequest.setPartner_order_id(orderDetail.getCarrierOrderId());
            redeliveryGWRequest.setTransporter_id(orderDetail.getIdAccountCarrier());
            redeliveryGWRequest.setTransporter_code(carrier.getCode());
            redeliveryGWRequest.setId_account_partner(orderDetail.getIdAccountCarrier());
            HashMap<String, Object> request = new HashMap<>();
            request.put("data", gson.toJson(redeliveryGWRequest));
            request.put("type", 2);
            String response = null;
            try {
                String param = URLEncoder.encode(gson.toJson(request), "UTF-8");
                logger.info("=======POST URL=======" + carrier.getGatewayUrl());
                logger.info("=======POST DATA======= REDELIVERY" + gson.toJson(request));
                response = CallServer.getInstance().postWithParam(carrier.getGatewayUrl().replaceAll("\\?",
                        "") + "?data=" + param);
            } catch (Exception e) {
                logger.info("=========REDELIVERY ORDER REQUEST EXCEPTION=======" + request.toString(), e);
            }
            if (response != null) {
                ChangeCodResponse changeCodResponse = gson.fromJson(response, ChangeCodResponse.class);
                if (changeCodResponse.getStatus() == 1)
                    return true;
            }
        } catch (Exception e) {
            logger.info("========REDELIVERY ORDER EXCEPTION========" + orderDetail.getSvcOrderDetailCode(), e);
        }
        return false;
    }

    public boolean refund(SvcOrderDetail orderDetail, Carrier carrier) {
        try {
            RefundGWRequest refundGWRequest = new RefundGWRequest();
            refundGWRequest.setOrder_id(String.valueOf(orderDetail.getSvcOrderDetailCode()));
            refundGWRequest.setPartner_order_id(orderDetail.getCarrierOrderId());
            refundGWRequest.setTransporter_id(orderDetail.getIdAccountCarrier());
            refundGWRequest.setTransporter_code(carrier.getCode());
            refundGWRequest.setId_account_partner(orderDetail.getIdAccountCarrier());
            HashMap<String, Object> request = new HashMap<>();
            request.put("data", gson.toJson(refundGWRequest));
            request.put("type", 2);
            String response = null;
            try {
                String param = URLEncoder.encode(gson.toJson(request), "UTF-8");
                logger.info("=======POST URL=======" + carrier.getGatewayUrl());
                logger.info("=======POST DATA======= REFUND" + gson.toJson(request));
                response = CallServer.getInstance().postWithParam(carrier.getGatewayUrl().replaceAll("\\?",
                        "") + "?data=" + param);
            } catch (Exception e) {
                logger.info("=========REFUND ORDER REQUEST EXCEPTION=======" + request.toString(), e);
            }
            if (response != null) {
                ChangeCodResponse changeCodResponse = gson.fromJson(response, ChangeCodResponse.class);
                if (changeCodResponse.getStatus() == 1)
                    return true;
            }
        } catch (Exception e) {
            logger.info("========REFUND ORDER EXCEPTION========" + orderDetail.getSvcOrderDetailCode(), e);
        }
        return false;
    }

    //    public BaseResponse updateHola1Order(CreateOrder createOrder, SvcOrderDetail oldOrderDetail, Carrier carrier, AppUser appUser) throws Exception {
//        CreateOrderDetail updateDeliveryPoint = createOrder.getDeliveryPoint().get(0);
//        CreateOrderReceiver updateCreateOrderReceiver = updateDeliveryPoint.getReceivers().get(0);
//        BigDecimal newCod = BigDecimal.ZERO;
//        for (OrderDetailProduct item : updateCreateOrderReceiver.getItems()) {
//            newCod = newCod.add(item.getCod());
//        }
//        Hola1UpdateRequest hola1UpdateRequest = new Hola1UpdateRequest();
//        hola1UpdateRequest.setReceiverDistrictCode(updateDeliveryPoint.getDistrict());
//        hola1UpdateRequest.setReceiverProvinceCode(updateDeliveryPoint.getProvince());
//        hola1UpdateRequest.setReceiverWardCode(updateDeliveryPoint.getWard());
//        if (updateCreateOrderReceiver.getWeight() == null)
//            hola1UpdateRequest.setWeight(oldOrderDetail.getWeight());
//        if (updateCreateOrderReceiver.getWidth() == null)
//            hola1UpdateRequest.setWidth(oldOrderDetail.getWidth());
//        if (updateCreateOrderReceiver.getHeight() == null)
//            hola1UpdateRequest.setHeight(oldOrderDetail.getHeight());
//        if (updateCreateOrderReceiver.getLength() == null)
//            hola1UpdateRequest.setLength(oldOrderDetail.getLength());
//        hola1UpdateRequest.setCod(newCod);
//        Hls1UpdateResponse updateResponse = gatewayService.updateOrderHls1(carrier, hola1UpdateRequest);
//        if (updateResponse == null)
//            return new BaseResponse(500);
//        if (updateResponse.getChangeStatus() == 0) {
//            //Update svc order
//            if (updateCreateOrderReceiver.getName() != null && !updateCreateOrderReceiver.getName().isEmpty())
//                oldOrderDetail.setConsignee(updateCreateOrderReceiver.getName());
//            if (updateCreateOrderReceiver.getPhone() != null && !updateCreateOrderReceiver.getPhone().isEmpty())
//                oldOrderDetail.setPhone(updateCreateOrderReceiver.getPhone());
//            //Thong so
//            oldOrderDetail.setWeight(updateOrderRequest.getWeight());
//            oldOrderDetail.setWidth(updateOrderRequest.getWidth());
//            oldOrderDetail.setLength(updateOrderRequest.getLength());
//            oldOrderDetail.setHeight(updateOrderRequest.getHeight());
//            //Diem giao
//            AddressDelivery addressDelivery = (AddressDelivery) addressDeliveryService
//                    .createAddressDelivery(updateOrderRequest.getReceiverWardCode(), updateOrderRequest.getReceiverAddress()).getData();
//            if (addressDelivery != null && !oldOrderDetail.getAddressDeliveryId().equals(addressDelivery.getId()))
//                oldOrderDetail.setAddressDeliveryId(addressDelivery.getId());
//            //Cod
//            oldOrderDetail.setRealityCod(updateOrderRequest.getCod());
//            //Tinh gia
//            try {
//                CalculateFeeOrderResponse calculateFeeOrderResponse = (CalculateFeeOrderResponse) postageService.calculateFee(createOrder, appUser).getData();
//                feeService.updateSellingFee(oldOrderDetail.getSvcOrderDetailCode(), calculateFeeOrderResponse.getData().getDeliveryPoint().get(0).getReceivers().get(0));
//            } catch (Exception e) {
//                logger.info("======UPDATE FEE EXCEPTION======" + oldOrderDetail.getSvcOrderDetailCode(), e);
//            }
//            AffiliateCreateOrderResponse response = buildCreateResponse(oldOrderDetail);
//            return new BaseResponseAffiliate(0, "success", response);
//        }
//        return new BaseResponseAffiliate(1);
//    }
    public Hls1UpdateResponse updateOrderHls1(Carrier carrier, CreateOrder createOrder, SvcOrderDetail orderDetail, AppUser appUser, boolean isChangeCod) throws Exception {
        CreateOrderDetail deliveryPoint = createOrder.getDeliveryPoint().get(0);
        CreateOrderReceiver receiver = deliveryPoint.getReceivers().get(0);
        Hola1UpdateRequest updateOrderRequest = new Hola1UpdateRequest();
//        if (!isChangeCod) {
        updateOrderRequest.setCode(String.valueOf(orderDetail.getSvcOrderDetailCode()));
        updateOrderRequest.setWeight((receiver.getWeight() == null) ? orderDetail.getWeight() : receiver.getWeight());
        updateOrderRequest.setWidth((receiver.getWidth() == null) ? orderDetail.getWidth() : receiver.getWidth());
        updateOrderRequest.setHeight((receiver.getHeight() == null) ? orderDetail.getHeight() : receiver.getHeight());
        updateOrderRequest.setLength((receiver.getLength() == null) ? orderDetail.getLength() : receiver.getLength());
        //Receiver
        updateOrderRequest.setReceiver(receiver.getName());
        updateOrderRequest.setReceiverPhone(receiver.getPhone());
        updateOrderRequest.setReceiverAddress(deliveryPoint.getAddress());
        updateOrderRequest.setReceiverWardCode(deliveryPoint.getWard());
        updateOrderRequest.setReceiverDistrictCode(deliveryPoint.getDistrict());
        updateOrderRequest.setReceiverProvinceCode(deliveryPoint.getProvince());
//        } else {
//            updateOrderRequest.setCode(String.valueOf(orderDetail.getSvcOrderDetailCode()));
//            updateOrderRequest.setWeight(orderDetail.getWeight());
//            updateOrderRequest.setWeight(orderDetail.getWidth());
//            updateOrderRequest.setWeight(orderDetail.getHeight());
//            updateOrderRequest.setWeight(orderDetail.getLength());
//            //Receiver
//            updateOrderRequest.setReceiver(orderDetail.getConsignee());
//            updateOrderRequest.setReceiverPhone(orderDetail.getPhone());
//            updateOrderRequest.setReceiverAddress(deliveryPoint.getAddress());
//            updateOrderRequest.setReceiverWardCode(deliveryPoint.getWard());
//            updateOrderRequest.setReceiverDistrictCode(deliveryPoint.getDistrict());
//            updateOrderRequest.setReceiverProvinceCode(deliveryPoint.getProvince());
//        }
        if (isChangeCod) {
            BigDecimal totalCod = BigDecimal.ZERO;
            for (OrderDetailProduct item : receiver.getItems()) {
                totalCod = totalCod.add(item.getCod().multiply(item.getQuantity()));
            }
            updateOrderRequest.setCod(totalCod);
        } else updateOrderRequest.setCod(orderDetail.getRealityCod());
        String response = null;
        try {
            logger.info("=======POST URL=======" + carrier.getGatewayUrl().replaceAll("\\?", "") + "/UPDATE_ORDER_INFO");
            logger.info("=======POST DATA=======" + gson.toJson(updateOrderRequest));
            response = CallServer.getInstance()
                    .post(carrier.getGatewayUrl().replaceAll("\\?", "") + "/UPDATE_ORDER_INFO",
                            gson.toJson(updateOrderRequest));
        } catch (Exception e) {
            logger.info("=========UPDATE AFFILIATE ORDER EXCEPTION=======" + gson.toJson(updateOrderRequest), e);
            return null;
        }
        if (response == null)
            return null;
        Hls1UpdateResponse updateResponse = gson.fromJson(response, Hls1UpdateResponse.class);
        return updateResponse;
    }
}
