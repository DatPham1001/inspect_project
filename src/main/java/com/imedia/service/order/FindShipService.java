package com.imedia.service.order;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.config.application.AppConfig;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.entity.*;
import com.imedia.oracle.repository.*;
import com.imedia.service.order.enums.OrderStatusEnum;
import com.imedia.service.order.model.*;
import com.imedia.service.wallet.WalletService;
import com.imedia.util.CallServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class FindShipService {
    static final Logger logger = LogManager.getLogger(FindShipService.class);
    static final Gson gson = new Gson();
    static final Gson gson2 = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private final AppUserRepository appUserRepository;
    private final WalletService walletService;
    private final SvcOrderRepository orderRepository;
    private final SvcOrderDetailRepository orderDetailRepository;
    private final WalletLogRepository walletLogRepository;
    private final CarrierRepository carrierRepository;
    private final OrderLogService orderLogService;
    private final OrderDetailRepository shipOrderDetailRepository;
    private final DetailSellingFeeRepository sellingFeeRepository;
    private final CallBackService callBackService;

    @Autowired
    public FindShipService(AppUserRepository appUserRepository, WalletService walletService, SvcOrderRepository orderRepository, SvcOrderDetailRepository orderDetailRepository, WalletLogRepository walletLogRepository, CarrierRepository carrierRepository, OrderLogService orderLogService, OrderDetailRepository shipOrderDetailRepository, DetailSellingFeeRepository sellingFeeRepository, CallBackService callBackService) {
        this.appUserRepository = appUserRepository;
        this.walletService = walletService;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.walletLogRepository = walletLogRepository;
        this.carrierRepository = carrierRepository;
        this.orderLogService = orderLogService;
        this.shipOrderDetailRepository = shipOrderDetailRepository;
        this.sellingFeeRepository = sellingFeeRepository;
        this.callBackService = callBackService;
    }

    public BaseResponse handleFindShip(SvcOrder order, List<SvcOrderDetail> orderDetails, AppUser appUser) {
        Carrier carrier = carrierRepository.getHolaCarrier();
        PushOrderData pushOrderData = new PushOrderData(order, orderDetails);
        String response = "";
        try {
            response = CallServer.getInstance()
                    .postUnlimitTimeout(AppConfig.getInstance().findShipUrl, gson2.toJson(pushOrderData));
        } catch (Exception e) {
            logger.info("========PUSH KM ORDER EXCEPTION=======" + order.getOrderCode(), e);
            //Revert holding
            return new BaseResponse(611);
        }
        if (response == null)
            return new BaseResponse(500);
        PushKmOrderBaseResponse pushKmOrderResponse = gson.fromJson(response, PushKmOrderBaseResponse.class);
        //status response 0 thành công 1 thất bại do lỗi 2 là đang tìm ship
        //Sẽ có luồng khác chờ queue tìm ship để cập nahatj trạng thái
        //Thành công và tìm được ship luôn
        if (pushKmOrderResponse.getCode() == 0) {
            PushKmOrderResponse data = pushKmOrderResponse.getData();
            OrderStatusEnum orderStatusEnum = OrderStatusEnum.WAIT_TO_PICK;
            if (order.getPickupType() == 1)
                orderStatusEnum = OrderStatusEnum.WAIT_TO_BRING;
            //insert log
            orderLogService.insertOrderLog(orderDetails, orderStatusEnum.message, orderStatusEnum.code);
            //Cập nhật trạng thái đơn
            order.setStatus(orderStatusEnum.code);
            order.setExpectShipId(Long.valueOf(data.getOrder().getShipId()));
            for (SvcOrderDetail orderDetail : orderDetails) {
                orderDetail.setOldStatus(orderDetail.getStatus());
                orderDetail.setStatus(orderStatusEnum.code);
                orderDetail.setCarrierServiceCode(carrier.getCode());
                orderDetail.setIdAccountCarrier(data.getOrder().getShipId());
                orderDetail.setCarrierId(carrier.getId());
                for (PushKmOrderDetailDataResponse pushKmOrderDetailDataResponse : data.getOrderDetails()) {
                    if (orderDetail.getSvcOrderDetailCode().equals(pushKmOrderDetailDataResponse.getOrderDetailCode()))
                        orderDetail.setCarrierOrderId(pushKmOrderDetailDataResponse.getOrderDetailShortId());
                }
                if (orderDetail.getChannel() != null &&
                        (orderDetail.getChannel().equals("AFF")
                                || orderDetail.getChannel().equals("API"))) {
                    callBackService.callbackAffiliate(orderDetail, orderDetail.getStatus(), orderStatusEnum.message);
                }
                orderDetailRepository.save(orderDetail);
            }
            orderRepository.save(order);
            return new BaseResponse(600, order);
        } else
            //Đang tìm ship
            if (pushKmOrderResponse.getCode() == 2) {
                //insert log
                orderLogService.insertOrderLog(orderDetails, OrderStatusEnum.FINDING_SHIP.message, OrderStatusEnum.FINDING_SHIP.code);
                PushKmOrderResponse data = pushKmOrderResponse.getData();
                order.setStatus(OrderStatusEnum.FINDING_SHIP.code);
                for (SvcOrderDetail orderDetail : orderDetails) {
                    orderDetail.setOldStatus(orderDetail.getStatus());
                    orderDetail.setCarrierId(carrier.getId());
                    orderDetail.setCarrierServiceCode(carrier.getCode());
                    orderDetail.setStatus(OrderStatusEnum.FINDING_SHIP.code);
                    for (PushKmOrderDetailDataResponse pushKmOrderDetailDataResponse : data.getOrderDetails()) {
                        if (orderDetail.getSvcOrderDetailCode().equals(pushKmOrderDetailDataResponse.getOrderDetailCode()))
                            orderDetail.setCarrierOrderId(pushKmOrderDetailDataResponse.getOrderDetailShortId());
                    }
                    if (orderDetail.getChannel() != null &&
                            (orderDetail.getChannel().equals("AFF")
                                    || orderDetail.getChannel().equals("API"))) {
                        callbackAffiliate(orderDetail, orderDetail.getStatus(), OrderStatusEnum.FINDING_SHIP.message);
                    }
                }
                orderRepository.save(order);
                orderDetailRepository.saveAll(orderDetails);
                return new BaseResponse(612, order);
            }
            //Lỗi phía app ship không tạo được đơn cho ship
            else {
                //insert log
                orderLogService.insertOrderLog(orderDetails, OrderStatusEnum.NOTE_NOTFOUND_SHIPPER.message, OrderStatusEnum.NOTE_NOTFOUND_SHIPPER.code);
                order.setStatus(OrderStatusEnum.WAIT_TO_CONFIRM.code);
                if (pushKmOrderResponse.getData() != null)
                    order.setExpectShipId(Long.valueOf(pushKmOrderResponse.getData().getOrder().getShipId()));
                for (SvcOrderDetail orderDetail : orderDetails) {
                    orderDetail.setOldStatus(orderDetail.getStatus());
                    orderDetail.setCarrierServiceCode(carrier.getCode());
                    orderDetail.setStatus(OrderStatusEnum.WAIT_TO_CONFIRM.code);
                    orderDetailRepository.save(orderDetail);
                    if (orderDetail.getChannel() != null &&
                            (orderDetail.getChannel().equals("AFF")
                                    || orderDetail.getChannel().equals("API"))) {
                        callbackAffiliate(orderDetail, OrderStatusEnum.PUSH_FAILED.code, OrderStatusEnum.PUSH_FAILED.message);
                    }
                }
                orderRepository.save(order);

                //Revert holding
                for (SvcOrderDetail orderDetail : orderDetails) {
                    //gỡ tạm giữ
                    List<WalletLog> walletLog = walletLogRepository
                            .findByOrderDetailIdAndTypeAndIsDeleted(orderDetail.getSvcOrderDetailCode(), BigDecimal.valueOf(20), 1);
                    try {
                        for (WalletLog log : walletLog) {
                            walletService.revertHoldingBalance(log, appUser);
                        }
                    } catch (Exception e) {
                        logger.info("======REVERT HOLDING BALANCE EXPCETION======" + orderDetail.getSvcOrderDetailCode(), e);
                    }
                }
                return new BaseResponse(611);
            }
    }

    public synchronized void handleFindShipperOrderCallback(String callback) {
        PushKmOrderBaseResponse response = gson.fromJson(callback, PushKmOrderBaseResponse.class);
        //Thành công tìm được ship
        if (response.getCode() == 0) {
            Carrier carrier = carrierRepository.getHolaCarrier();
            PushKmOrderResponse data = response.getData();
            SvcOrder order = orderRepository.findByOrderCode(data.getOrder().getOrderCode());
            List<SvcOrderDetail> orderDetails = orderDetailRepository.getValidBySvcOrderId(data.getOrder().getOrderCode());
            if (order != null && orderDetails.size() > 0 && carrier != null) {
                order.setStatus(OrderStatusEnum.WAIT_TO_PICK.code);
                for (SvcOrderDetail orderDetail : orderDetails) {
                    orderDetail.setOldStatus(orderDetail.getStatus());
                    orderDetail.setStatus(OrderStatusEnum.WAIT_TO_PICK.code);
                    orderDetail.setIdAccountCarrier(data.getOrder().getShipId());
                    orderDetail.setCarrierServiceCode(carrier.getCode());
                    orderDetail.setCarrierId(carrier.getId());
                    orderLogService.insertOrderLog(orderDetail, OrderStatusEnum.WAIT_TO_PICK.message, OrderStatusEnum.WAIT_TO_PICK.code);
                    orderDetailRepository.save(orderDetail);
                    if (orderDetail.getChannel() != null &&
                            (orderDetail.getChannel().equals("AFF")
                                    || orderDetail.getChannel().equals("API"))) {
                        callbackAffiliate(orderDetail, orderDetail.getStatus(), OrderStatusEnum.NOTE_NOTFOUND_SHIPPER.message);
                    }
                    logger.info("========FIND SHIP CALLBACK SVE TO DB======= ORDER " + gson.toJson(order)
                            + "|| ORDER DETAILS " + gson.toJson(orderDetail));
                }
                orderRepository.save(order);
            }
        }
        //Ship từ chối hết
        if (response.getCode() == 2) {
            PushKmOrderResponse data = response.getData();
            SvcOrder order = orderRepository.findByOrderCode(data.getOrder().getOrderCode());
            List<SvcOrderDetail> orderDetails = orderDetailRepository.getValidBySvcOrderId(data.getOrder().getOrderCode());
            AppUser appUser = appUserRepository.findAppUserById(order.getShopId().longValue());
            //insert log
            orderLogService.insertOrderLog(orderDetails, OrderStatusEnum.NOTE_NOTFOUND_SHIPPER.message, OrderStatusEnum.NOTE_NOTFOUND_SHIPPER.code);
            order.setStatus(OrderStatusEnum.WAIT_TO_CONFIRM.code);
            for (SvcOrderDetail orderDetail : orderDetails) {
                orderDetail.setOldStatus(orderDetail.getStatus());
                orderDetail.setStatus(OrderStatusEnum.WAIT_TO_CONFIRM.code);
                orderDetailRepository.save(orderDetail);
                if (orderDetail.getChannel() != null &&
                        (orderDetail.getChannel().equals("AFF")
                                || orderDetail.getChannel().equals("API"))) {
                    callbackAffiliate(orderDetail, orderDetail.getStatus(), OrderStatusEnum.NOTE_NOTFOUND_SHIPPER.message);
                }
            }
            orderRepository.save(order);

            //Revert holding
            for (SvcOrderDetail orderDetail : orderDetails) {
                //gỡ tạm giữ
                List<WalletLog> walletLog = walletLogRepository
                        .findByOrderDetailIdAndTypeAndIsDeleted(orderDetail.getSvcOrderDetailCode(), BigDecimal.valueOf(20), 1);
                try {
                    for (WalletLog log : walletLog) {
                        walletService.revertHoldingBalance(log, appUser);
                    }
                } catch (Exception e) {
                    logger.info("======REVERT HOLDING BALANCE EXPCETION======" + orderDetail.getSvcOrderDetailCode(), e);
                }
            }
        }
    }

    private void callbackAffiliate(SvcOrderDetail orderDetail, int status, String note) {
        //Callback
        try {
            if (!orderDetail.getChannel().equals("AFF") && !orderDetail.getChannel().equals("API")) {
                logger.info("======NOT AFFILIATE ORDER TO CALLBACK======" + orderDetail.getSvcOrderDetailCode());
                return;
            }
            if (orderDetail.getShopReferId() == null)
                return;
            AppUser referUser = appUserRepository.findAppUserById(orderDetail.getShopReferId());

            //Build callback
            AffiliateCallbackData affiliateCallbackData = new AffiliateCallbackData();
            affiliateCallbackData.setShopId(orderDetail.getShopId());
            affiliateCallbackData.setOrderShortcode(String.valueOf(orderDetail.getSvcOrderDetailCode()));
            if (orderDetail.getCarrierOrderId() != null)
                affiliateCallbackData.setCarrierOrderCode(orderDetail.getCarrierOrderId());
            affiliateCallbackData.setShopOrderCode(orderDetail.getShopOrderId());
            affiliateCallbackData.setCarrierId(orderDetail.getCarrierId());
            affiliateCallbackData.setCarrierCode(orderDetail.getCarrierServiceCode());
            affiliateCallbackData.setStatus(status);
//            affiliateCallbackData.setStatusText(OrderStatusNameEnum.valueOf(status).message);
            affiliateCallbackData.setNote(note);
            affiliateCallbackData.setActionTime(new Timestamp(new Date().getTime()));
            affiliateCallbackData.setWeight(orderDetail.getWeight());
            BigDecimal totalFee = sellingFeeRepository.sumTotalFee(orderDetail.getSvcOrderDetailCode());
            affiliateCallbackData.setFee(totalFee);
            affiliateCallbackData.setCod(orderDetail.getRealityCod());
            affiliateCallbackData.setCallbackUrl(referUser.getCallbackUrl());
            if (referUser.getCallbackUrl() != null)
                CallServer.getInstance().post(referUser.getCallbackUrl(), gson.toJson(affiliateCallbackData));
        } catch (Exception e) {
            logger.info("======CALBACK TO AFF EXCEPTION=====" + orderDetail.getSvcOrderDetailCode(), e);
        }
    }
}
