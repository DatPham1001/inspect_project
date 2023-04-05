package com.imedia.service.order;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.config.application.AppConfig;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.dao.OrderDAO;
import com.imedia.oracle.entity.*;
import com.imedia.oracle.repository.*;
import com.imedia.service.order.enums.OrderStatusEnum;
import com.imedia.service.order.model.*;
import com.imedia.service.postage.FeeService;
import com.imedia.service.postage.PostageService;
import com.imedia.service.postage.model.CalculateFeeDeliveryPoint;
import com.imedia.service.postage.model.CalculateFeeOrderResponse;
import com.imedia.service.postage.model.CalculateFeeReceivers;
import com.imedia.service.postage.model.ForWardConfigOM;
import com.imedia.service.userwallet.UserWalletService;
import com.imedia.service.userwallet.model.UserBalanceResponse;
import com.imedia.service.wallet.WalletService;
import com.imedia.service.wallet.model.WalletBaseResponse;
import com.imedia.util.CallRedis;
import com.imedia.util.CallServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderConfirmService {
    static final Logger logger = LogManager.getLogger(OrderConfirmService.class);
    static final Gson gson = new Gson();
    static final Gson gson2 = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private final AppUserRepository appUserRepository;
    private final PostageService postageService;
    private final WalletService walletService;
    private final UserWalletService userWalletService;
    private final SvcOrderRepository orderRepository;
    private final SvcOrderDetailRepository orderDetailRepository;
    private final DetailSellingFeeRepository detailSellingFeeRepository;
    private final OrderDAO orderDAO;
    private final WalletLogRepository walletLogRepository;
    private final CarrierRepository carrierRepository;
    private final OrderReadService orderReadService;
    private final FeeService feeService;
    private final OrderLogService orderLogService;

    @Autowired
    public OrderConfirmService(AppUserRepository appUserRepository, PostageService postageService, WalletService walletService, UserWalletService userWalletService, SvcOrderRepository orderRepository, SvcOrderDetailRepository orderDetailRepository, DetailSellingFeeRepository detailSellingFeeRepository, OrderDAO orderDAO, WalletLogRepository walletLogRepository, CarrierRepository carrierRepository, OrderReadService orderReadService, FeeService feeService, OrderLogService orderLogService) {
        this.appUserRepository = appUserRepository;
        this.postageService = postageService;
        this.walletService = walletService;
        this.userWalletService = userWalletService;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.detailSellingFeeRepository = detailSellingFeeRepository;
        this.orderDAO = orderDAO;
        this.walletLogRepository = walletLogRepository;
        this.carrierRepository = carrierRepository;
        this.orderReadService = orderReadService;
        this.feeService = feeService;
        this.orderLogService = orderLogService;
    }


    public BaseResponse handleConfirmOrder(ConfirmOrderRequest confirmOrderRequest, String username) throws Exception {
        List<String> orderCodes = Arrays.asList(confirmOrderRequest.getOrderCodes().split(","));
        AppUser appUser = appUserRepository.findByPhone(username);
        List<FilterOrderResponse> confirmOrderRequests = orderReadService.getOrderToConfirm(orderCodes, username, confirmOrderRequest.getAction());
        if (confirmOrderRequests.size() == 0)
            return new BaseResponse(614);
//        Duyệt đơn lẻ sẽ trả về mã lỗi chi tiết
        if (!confirmOrderRequest.getAction().equals("ALL") && orderCodes.size() == 1) {
            return confirmOrder(confirmOrderRequests, appUser);
        } else {
            return confirmAllOrder(orderCodes, confirmOrderRequests, appUser);
        }
    }

    private BaseResponse confirmOrder(List<FilterOrderResponse> confirmOrderRequests, AppUser appUser) throws Exception {
//        BigDecimal totalFeeToHold = BigDecimal.ZERO;
//        BigDecimal remainBalance = BigDecimal.ZERO;
        if (confirmOrderRequests.size() == 0)
            return new BaseResponse(614);
        FilterOrderResponse confirmOrder = confirmOrderRequests.get(0);
        for (FilterOrderDetailResponse orderDetail : confirmOrder.getOrderDetails()) {
            List<WalletLog> logs = walletLogRepository
                    .findByOrderDetailIdAndTypeAndIsDeleted(orderDetail.getSvcOrderDetailCode(), BigDecimal.valueOf(20), 1);
            try {
                for (WalletLog log : logs) {
                    walletService.revertHoldingBalance(log, appUser);
                }
            } catch (Exception e) {
                logger.info("======REVERT HOLDING BALANCE EXPCETION======" + orderDetail.getSvcOrderDetailCode(), e);
            }
        }
        //Dơn km
        if (confirmOrder.getType() == 2) {
            SvcOrder svcOrder = orderRepository.findByOrderCodeAndShopId(confirmOrder.getSvcOrderId(),
                    BigDecimal.valueOf(appUser.getId()));
            if (svcOrder == null || confirmOrder.getType() == 1) {
                return new BaseResponse(614);
            }
            List<SvcOrderDetail> orderDetails = orderDetailRepository.getValidBySvcOrderId(svcOrder.getOrderCode());
            //Tính lại giá
            CalculateFeeOrderResponse calculateFeeOrderResponse = postageService
                    .calculateFeeForReFindShipper(confirmOrder, appUser);
            BigDecimal totalFee = BigDecimal.ZERO;
            if (calculateFeeOrderResponse.getCode() != 0) {
                BaseResponse baseResponse = new BaseResponse(2000 + calculateFeeOrderResponse.getCode());
                baseResponse.setData(Collections.singletonList(baseResponse.getMessage()));
                return baseResponse;
            }
            if (calculateFeeOrderResponse.getCode() == 0)
                for (CalculateFeeDeliveryPoint deliveryPoint : calculateFeeOrderResponse.getData().getDeliveryPoint()) {
                    for (CalculateFeeReceivers receiver : deliveryPoint.getReceivers()) {
                        totalFee = totalFee.add(receiver.getTotalFee());
                    }
                }
            UserBalanceResponse userInfo = userWalletService.getBalances(appUser.getPhone());
            if (userInfo == null)
                return new BaseResponse(500);
            else if (userInfo.getAvailableHold().compareTo(totalFee) < 0) {
                ConfirmOrderResponse confirmOrderResponse = new ConfirmOrderResponse(1, 0, 1,
                        userInfo.getAvailableHold(), totalFee.subtract(userInfo.getAvailableHold()), "");
                //insert log
                orderLogService.insertOrderLog(orderDetails, OrderStatusEnum.NOTE_PUSH_GW_FAILED.message, OrderStatusEnum.WAIT_TO_CONFIRM.code);
                return new BaseResponse(616, confirmOrderResponse);
            }
            //Tạm giữ
            for (CalculateFeeDeliveryPoint deliveryPoint : calculateFeeOrderResponse.getData().getDeliveryPoint()) {
//                            for (CalculateFeeReceivers receiver : deliveryPoint.getReceivers()) {
                for (int i = 0; i < deliveryPoint.getReceivers().size(); i++) {
                    CalculateFeeReceivers receiver = deliveryPoint.getReceivers().get(i);
                    BigDecimal orderDetailCode = orderDetails.get(i).getSvcOrderDetailCode();
                    try {
                        walletService.addHoldingBalance(receiver.getOrderDetailCode(), appUser, receiver.getTotalFee());
                    } catch (Exception e) {
                        logger.info("=========ADD HOLDING BALANCE EXCEPTION=======" + receiver.getOrderDetailCode(), e);
                    }
//                    detailSellingFeeRepository.deleteSellingFeeByDetailCode(orderDetailCode);
                    feeService.createSellingFee(orderDetailCode, calculateFeeOrderResponse.getData().getDeliveryPoint().get(0).getReceivers().get(0));
                }
            }
            //Tìm lại ship
            BaseResponse response = handleFindShip(svcOrder, orderDetails, appUser);
            if (response.getStatus() == 600)
                return new BaseResponse(609, svcOrder);
            return response;
        }
        //Đơn đồng giá sẽ chỉ có 1 delivery point => 1 detail
        else {
            List<ForWardConfigOM> configIMS = new ArrayList<>();
            int isFree = confirmOrder.getOrderDetails().get(0).getIsFree().intValue();
            int isPartial = confirmOrder.getOrderDetails().get(0).getIsPartDelivery().intValue();
            int priceSettingId = confirmOrder.getOrderDetails().get(0).getServicePackSettingId();
            int paymentType = confirmOrder.getPaymentType();
            //Đơn đặc biệt phải tìm config trước
            if (isFree == 2 || isPartial == 1 || paymentType == 1) {
                configIMS = orderDAO.getTargetConfig(confirmOrder.getPackCode(),
                        priceSettingId, isPartial, isFree, paymentType);
                if (configIMS.size() == 0) {
                    List<String> message = new ArrayList<>();
                    StringBuilder note = new StringBuilder(OrderStatusEnum.PRE_CONFIRM_FAIL.message);
                    if (isFree == 2) {
                        message.add(new BaseResponse(610).getMessage());
                        note.append(OrderStatusEnum.NOTE_ORDER_PAYMENT.message);
                        note.append(",");
                    }
                    if (isPartial == 1) {
                        note.append(OrderStatusEnum.NOTE_PARTIAL_TYPE.message);
                        note.append(",");
                        message.add(new BaseResponse(615).getMessage());
                    }
                    if (paymentType == 1) {
                        note.append(OrderStatusEnum.NOTE_PAYMENT_TYPE.message);
                        note.append(",");
                        message.add(new BaseResponse(617).getMessage());
                    }
                    orderLogService.insertOrderLog(confirmOrder, confirmOrder.getOrderDetails(),
                            OrderStatusEnum.WAIT_TO_CONFIRM.code, note.substring(0, note.length() - 1));
                    return new BaseResponse(618, message);
                }
            }
            CalculateFeeOrderResponse calculateFeeOrderResponse = null;
            try {
                calculateFeeOrderResponse = postageService.calculateFeeForConfirmOrder(confirmOrder, appUser);
            } catch (Exception e) {
                logger.info("=======CALCULATE FEE CONFIRM ORDER EXCEPTION========" + confirmOrder.getOrderDetails().get(0).getSvcOrderDetailCode(), e);
            }
            BigDecimal orderDetailCode = confirmOrder.getOrderDetails().get(0).getSvcOrderDetailCode();
            SvcOrderDetail svcOrderDetail =
                    orderDetailRepository.getValidSvcOrderDetailCodeAndShopIdAndStatus(orderDetailCode, appUser.getId(), OrderStatusEnum.WAIT_TO_CONFIRM.code);
            if (calculateFeeOrderResponse.getCode() != 0) {
                BaseResponse baseResponse = new BaseResponse(2000 + calculateFeeOrderResponse.getCode());
                baseResponse.setData(Collections.singletonList(baseResponse.getMessage()));
                return baseResponse;
            }
            BigDecimal totalFee = calculateFeeOrderResponse.getData().getDeliveryPoint().get(0).getReceivers().get(0).getTotalFee();
            UserBalanceResponse userInfo = userWalletService.getBalances(appUser.getPhone());
            if (userInfo == null)
                return new BaseResponse(500);
            else if (userInfo.getAvailableHold().compareTo(totalFee) < 0) {
                ConfirmOrderResponse confirmOrderResponse = new ConfirmOrderResponse(1, 0,
                        1, userInfo.getAvailableHold(), totalFee.subtract(userInfo.getAvailableHold()), "");
                //insert log
                orderLogService.insertOrderLog(Collections.singletonList(svcOrderDetail), OrderStatusEnum.NOTE_PUSH_GW_FAILED.message, OrderStatusEnum.WAIT_TO_CONFIRM.code);
                return new BaseResponse(616, confirmOrderResponse);

            }
            //Tam giu
            WalletBaseResponse baseResponse = walletService.addHoldingBalance(orderDetailCode, appUser, totalFee);
            //Tam giu that bai
            if (baseResponse.getStatus() != 200)
                return new BaseResponse(500);
            try {
//                detailSellingFeeRepository.deleteSellingFeeByDetailCode(orderDetailCode);
                feeService.createSellingFee(orderDetailCode, calculateFeeOrderResponse.getData().getDeliveryPoint().get(0).getReceivers().get(0));
                svcOrderDetail.setStatus(OrderStatusEnum.CONFIRMED.code);
                SvcOrderDetail result = orderDetailRepository.save(svcOrderDetail);
                orderRepository.updateStatus(OrderStatusEnum.CONFIRMED.code, confirmOrder.getSvcOrderId());
                //Nếu đơn có Hola thì chỉ đẩy cho hola và end
                List<ForWardConfigOM> holaSpecialConfig = configIMS.stream()
                        .filter(c -> c.getCode().equalsIgnoreCase("HLS") || c.getCode().equalsIgnoreCase("HOLA"))
                        .collect(Collectors.toList());
                SvcOrder order = orderRepository.findByOrderCode(svcOrderDetail.getSvcOrderId());
                if (holaSpecialConfig.size() > 0) {
                    BaseResponse response = handleFindShip(order, Collections.singletonList(svcOrderDetail), appUser);
                    if (response.getStatus() == 600)
                        return new BaseResponse(609, order);
                    else return response;
                }
                //Tìm cấu hình không đặc biệt
                if (configIMS.size() == 0) {
                    configIMS = orderDAO.getTargetConfig(priceSettingId);
                    List<ForWardConfigOM> holaConfig = configIMS.stream()
                            .filter(c -> c.getCode().equalsIgnoreCase("HLS")
                                    || c.getCode().equalsIgnoreCase("HOLA"))
                            .collect(Collectors.toList());
                    if (holaConfig.size() > 0) {
                        BaseResponse response = handleFindShip(order, Collections.singletonList(svcOrderDetail), appUser);
                        if (response.getStatus() == 600)
                            return new BaseResponse(609, order);
                        return response;
                    }
                }
                HashMap<String, Object> pushRequest = new HashMap<>();
                pushRequest.put("configs", gson.toJson(configIMS));
                pushRequest.put("orderDetail", gson.toJson(svcOrderDetail));
                boolean pushed = CallRedis.pushQueue("PUSH_SP_ORDER", pushRequest.toString(), null);
                if (!pushed) return new BaseResponse(500);
                else return new BaseResponse(609, order);
            } catch (Exception e) {
                logger.info("=======CREATE SELLING FEE CONFIRM ORDER EXCEPTION========" + orderDetailCode, e);
                //Rollback
//                    detailSellingFeeRepository.deleteSellingFeeByDetailCode(orderDetailCode);
                svcOrderDetail.setStatus(OrderStatusEnum.WAIT_TO_CONFIRM.code);
                orderDetailRepository.save(svcOrderDetail);
                //Revert holding
                //gỡ tạm giữ
                List<WalletLog> walletLog = walletLogRepository
                        .findByOrderDetailIdAndTypeAndIsDeleted(svcOrderDetail.getSvcOrderDetailCode(), BigDecimal.valueOf(20), 1);
                for (WalletLog log : walletLog) {
                    walletService.revertHoldingBalance(log, appUser);
                }
                return new BaseResponse(500);
            }
        }

    }

    private BaseResponse confirmAllOrder(List<String> orderCodes, List<FilterOrderResponse> confirmOrderRequests, AppUser appUser) throws Exception {
        int totalFailed = 0;
        int totalSuccess = 0;
        BigDecimal totalFeeToHold = BigDecimal.ZERO;
        BigDecimal remainBalance = BigDecimal.ZERO;
        if (confirmOrderRequests.size() == 0)
            totalFailed = orderCodes.size();
        for (FilterOrderResponse confirmOrder : confirmOrderRequests) {
            //Revert all order
            for (FilterOrderDetailResponse orderDetail : confirmOrder.getOrderDetails()) {
                List<WalletLog> logs = walletLogRepository
                        .findByOrderDetailIdAndTypeAndIsDeleted(orderDetail.getSvcOrderDetailCode(), BigDecimal.valueOf(20), 1);
                try {
                    for (WalletLog log : logs) {
                        walletService.revertHoldingBalance(log, appUser);
                    }
                } catch (Exception e) {
                    logger.info("======REVERT HOLDING BALANCE EXPCETION======" + orderDetail.getSvcOrderDetailCode(), e);
                }
            }
            //Đơn km
            if (confirmOrder.getType() == 2) {
                SvcOrder svcOrder = orderRepository.findByOrderCodeAndShopId(confirmOrder.getSvcOrderId(),
                        BigDecimal.valueOf(appUser.getId()));
                if (svcOrder == null || confirmOrder.getType() == 1) {
                    totalFailed = totalFailed + 1;
                    continue;
                }
                List<SvcOrderDetail> orderDetails = orderDetailRepository.getValidBySvcOrderId(svcOrder.getOrderCode());
                //Tính lại giá
                CalculateFeeOrderResponse calculateFeeOrderResponse = postageService
                        .calculateFeeForReFindShipper(confirmOrder, appUser);
                if (calculateFeeOrderResponse.getCode() != 0) {
                    totalFailed = totalFailed + 1;
                    continue;
                }
                BigDecimal totalFee = BigDecimal.ZERO;
                for (CalculateFeeDeliveryPoint deliveryPoint : calculateFeeOrderResponse.getData().getDeliveryPoint()) {
                    for (CalculateFeeReceivers receiver : deliveryPoint.getReceivers()) {
                        totalFee = totalFee.add(receiver.getTotalFee());
                    }
                }
                UserBalanceResponse userInfo = userWalletService.getBalances(appUser.getPhone());
                if (userInfo != null)
                    if (userInfo.getAvailableHold().compareTo(totalFee) >= 0) {
                        //Tạm giữ
                        for (CalculateFeeDeliveryPoint deliveryPoint : calculateFeeOrderResponse.getData().getDeliveryPoint()) {
                            for (int i = 0; i < deliveryPoint.getReceivers().size(); i++) {
                                CalculateFeeReceivers receiver = deliveryPoint.getReceivers().get(i);
                                BigDecimal orderDetailCode = orderDetails.get(i).getSvcOrderDetailCode();
                                try {
                                    walletService.addHoldingBalance(receiver.getOrderDetailCode(), appUser, receiver.getTotalFee());
                                } catch (Exception e) {
                                    logger.info("=========ADD HOLDING BALANCE EXCEPTION=======" + receiver.getOrderDetailCode(), e);
                                }
//                                detailSellingFeeRepository.deleteSellingFeeByDetailCode(orderDetailCode);
                                feeService.createSellingFee(orderDetailCode, calculateFeeOrderResponse.getData().getDeliveryPoint().get(0).getReceivers().get(0));
                            }
                        }
                        //Tìm lại ship
                        BaseResponse response = handleFindShip(svcOrder, orderDetails, appUser);
                        if (response.getStatus() == 600 || response.getStatus() == 612)
                            totalSuccess = totalSuccess + 1;
                        else totalFailed += 1;
                    } else {
                        totalFeeToHold = totalFeeToHold.add(totalFee);
                        totalFailed = totalFailed + 1;
                        remainBalance = userInfo.getAvailableHold();
                    }
                else totalFailed = totalFailed + 1;
            }
            //Đơn đồng giá sẽ chỉ có 1 delivery point => 1 detail
            else {
                List<ForWardConfigOM> configIMS = new ArrayList<>();
                int isFree = confirmOrder.getOrderDetails().get(0).getIsFree().intValue();
                int isPartial = confirmOrder.getOrderDetails().get(0).getIsPartDelivery().intValue();
                int priceSettingId = confirmOrder.getOrderDetails().get(0).getServicePackSettingId();
                int paymentType = confirmOrder.getPaymentType();
                //Đơn đặc biệt phải tìm config trước
                if (isFree == 2 || isPartial == 1 || paymentType == 1) {
                    configIMS = orderDAO.getTargetConfig(confirmOrder.getPackCode(), priceSettingId, isPartial, isFree, paymentType);
                    if (configIMS.size() == 0) {
                        //Insert log
                        StringBuilder note = new StringBuilder(OrderStatusEnum.PRE_CONFIRM_FAIL.message);
                        if (isFree == 2) {
                            note.append(OrderStatusEnum.NOTE_ORDER_PAYMENT.message);
                            note.append(",");
                        }
                        if (isPartial == 1) {
                            note.append(OrderStatusEnum.NOTE_PARTIAL_TYPE.message);
                            note.append(",");
                        }
                        if (paymentType == 1) {
                            note.append(OrderStatusEnum.NOTE_PAYMENT_TYPE.message);
                            note.append(",");
                        }
                        orderLogService.insertOrderLog(confirmOrder, confirmOrder.getOrderDetails(), OrderStatusEnum.WAIT_TO_CONFIRM.code, note.substring(0, note.length() - 1));
                        totalFailed = totalFailed + 1;
                        continue;
                    }
                }
                CalculateFeeOrderResponse calculateFeeOrderResponse = null;
                try {
                    calculateFeeOrderResponse = postageService.calculateFeeForConfirmOrder(confirmOrder, appUser);
                } catch (Exception e) {
                    logger.info("=======CALCULATE FEE CONFIRM ORDER EXCEPTION========" + confirmOrder.getOrderDetails().get(0).getSvcOrderDetailCode(), e);
                }
                BigDecimal orderDetailCode = confirmOrder.getOrderDetails().get(0).getSvcOrderDetailCode();
                SvcOrderDetail svcOrderDetail =
                        orderDetailRepository.getValidSvcOrderDetailCodeAndShopIdAndStatus(orderDetailCode, appUser.getId(), OrderStatusEnum.WAIT_TO_CONFIRM.code);
                if (calculateFeeOrderResponse != null && calculateFeeOrderResponse.getCode() == 0
                        && calculateFeeOrderResponse.getData() != null && svcOrderDetail != null) {
                    BigDecimal totalFee = calculateFeeOrderResponse.getData().getDeliveryPoint().get(0).getReceivers().get(0).getTotalFee();
                    UserBalanceResponse userInfo = userWalletService.getBalances(appUser.getPhone());
                    if (userInfo == null)
                        totalFailed = totalFailed + 1;
                    else if (userInfo.getAvailableHold().compareTo(totalFee) >= 0) {
                        //Tam giu
                        WalletBaseResponse baseResponse = walletService.addHoldingBalance(orderDetailCode, appUser, totalFee);
                        //Tam giu that bai
                        if (baseResponse == null || baseResponse.getStatus() != 200) {
                            totalFailed = totalFailed + 1;
                            continue;
                        }
                        try {
//                            detailSellingFeeRepository.deleteSellingFeeByDetailCode(orderDetailCode);
                            feeService.createSellingFee(orderDetailCode, calculateFeeOrderResponse.getData().getDeliveryPoint().get(0).getReceivers().get(0));
                            svcOrderDetail.setStatus(OrderStatusEnum.CONFIRMED.code);
                            SvcOrderDetail result = orderDetailRepository.save(svcOrderDetail);
                            orderRepository.updateStatus(OrderStatusEnum.CONFIRMED.code, confirmOrder.getSvcOrderId());
                            //Nếu đơn có Hola thì chỉ đẩy cho hola và end
                            List<ForWardConfigOM> holaSpecialConfig = configIMS.stream()
                                    .filter(c -> c.getCode().equalsIgnoreCase("HLS") || c.getCode().equalsIgnoreCase("HOLA"))
                                    .collect(Collectors.toList());
                            SvcOrder order = orderRepository.findByOrderCode(svcOrderDetail.getSvcOrderId());
                            if (holaSpecialConfig.size() > 0) {
                                BaseResponse response = handleFindShip(order, Collections.singletonList(svcOrderDetail), appUser);
                                if (response.getStatus() == 600 || response.getStatus() == 612)
                                    totalSuccess = totalSuccess + 1;
                                else totalFailed += 1;
                                continue;
                            }
                            //Tìm cấu hình không đặc biệt
                            if (configIMS.size() == 0) {
                                configIMS = orderDAO.getTargetConfig(priceSettingId);
                                List<ForWardConfigOM> holaConfig = configIMS.stream()
                                        .filter(c -> c.getCode().equalsIgnoreCase("HLS") || c.getCode().equalsIgnoreCase("HOLA"))
                                        .collect(Collectors.toList());
                                if (holaConfig.size() > 0) {
                                    BaseResponse response = handleFindShip(order, Collections.singletonList(svcOrderDetail), appUser);
                                    if (response.getStatus() == 600 || response.getStatus() == 612)
                                        totalSuccess = totalSuccess + 1;
                                    else totalFailed += 1;
                                    continue;
                                }
                            }
                            //Còn lại sẽ là đơn NVc khác và là đơn 1 1
                            HashMap<String, Object> pushRequest = new HashMap<>();
                            pushRequest.put("configs", gson.toJson(configIMS));
                            pushRequest.put("orderDetail", gson.toJson(svcOrderDetail));
                            boolean pushed = CallRedis.pushQueue("PUSH_SP_ORDER", pushRequest.toString(), null);
                            if (!pushed) totalFailed = totalFailed + 1;
                            else totalSuccess = totalSuccess + 1;
                        } catch (Exception e) {
                            logger.info("=======CREATE SELLING FEE CONFIRM ORDER EXCEPTION========" + orderDetailCode, e);
                            //Rollback
//                                detailSellingFeeRepository.deleteSellingFeeByDetailCode(orderDetailCode);
                            svcOrderDetail.setStatus(OrderStatusEnum.WAIT_TO_CONFIRM.code);
                            orderDetailRepository.save(svcOrderDetail);
                            //gỡ tạm giữ
                            List<WalletLog> walletLog = walletLogRepository
                                    .findByOrderDetailIdAndTypeAndIsDeleted(svcOrderDetail.getSvcOrderDetailCode(), BigDecimal.valueOf(20), 1);
                            for (WalletLog log : walletLog) {
                                walletService.revertHoldingBalance(log, appUser);
                            }
                            totalFailed = totalFailed + 1;
                            continue;
                        }
                    } else {
                        totalFailed = totalFailed + 1;
                        totalFeeToHold = totalFeeToHold.add(totalFee);
                        remainBalance = userInfo.getAvailableHold();
                    }
                } else totalFailed = totalFailed + 1;
            }
        }
        ConfirmOrderResponse confirmOrderResponse = new ConfirmOrderResponse();
        confirmOrderResponse.setTotalRequest(orderCodes.size());
        confirmOrderResponse.setTotalFailed(totalFailed);
        confirmOrderResponse.setTotalSuccess(totalSuccess);
        confirmOrderResponse.setRemainBalance(remainBalance);
        if (totalFeeToHold.subtract(remainBalance).compareTo(BigDecimal.ZERO) > 0)
            confirmOrderResponse.setMinimumToConfirm(totalFeeToHold.subtract(remainBalance));
        else confirmOrderResponse.setMinimumToConfirm(BigDecimal.ZERO);
        return new BaseResponse(609, confirmOrderResponse);
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
                orderDetail.setIdAccountCarrier(data.getOrder().getShipId());
                orderDetail.setCarrierId(carrier.getId());
                for (PushKmOrderDetailDataResponse pushKmOrderDetailDataResponse : data.getOrderDetails()) {
                    if (orderDetail.getSvcOrderDetailCode().equals(pushKmOrderDetailDataResponse.getOrderDetailCode()))
                        orderDetail.setCarrierOrderId(pushKmOrderDetailDataResponse.getOrderDetailShortId());
                }
            }
            orderRepository.save(order);
            orderDetailRepository.saveAll(orderDetails);
            return new BaseResponse(600, order);
        } else
            //Đang tìm ship
            if (pushKmOrderResponse.getCode() == 2) {
                //insert log
                orderLogService.insertOrderLog(orderDetails, OrderStatusEnum.FINDING_SHIP.message, OrderStatusEnum.FINDING_SHIP.code);
                PushKmOrderResponse data = pushKmOrderResponse.getData();
                order.setStatus(OrderStatusEnum.FINDING_SHIP.code);
//                //insert log
//                orderLogService.insertOrderLog(orderDetails, OrderStatusEnum.WAIT_TO_PICK.message, OrderStatusEnum.WAIT_TO_PICK.code);
                orderDetails.forEach(o -> {
                    o.setOldStatus(o.getStatus());
                    o.setCarrierId(carrier.getId());
                    o.setStatus(OrderStatusEnum.FINDING_SHIP.code);
                    for (PushKmOrderDetailDataResponse pushKmOrderDetailDataResponse : data.getOrderDetails()) {
                        if (o.getSvcOrderDetailCode().equals(pushKmOrderDetailDataResponse.getOrderDetailCode()))
                            o.setCarrierOrderId(pushKmOrderDetailDataResponse.getOrderDetailShortId());
                    }
                });
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
                orderDetails.forEach(o -> {
                    o.setOldStatus(o.getStatus());
                    o.setStatus(OrderStatusEnum.WAIT_TO_CONFIRM.code);
                });
                orderRepository.save(order);
                orderDetailRepository.saveAll(orderDetails);
                //Revert holding
                for (SvcOrderDetail orderDetail : orderDetails) {
                    //gỡ tạm giữ
                    List<WalletLog> walletLog = walletLogRepository
                            .findByOrderDetailIdAndTypeAndIsDeleted(orderDetail.getSvcOrderDetailCode(), BigDecimal.valueOf(20), 1);
                    for (WalletLog log : walletLog) {
                        try {
                            walletService.revertHoldingBalance(log, appUser);
                        } catch (Exception e) {
                            logger.info("======REVERT HOLDING BALANCE EXPCETION======" + orderDetail.getSvcOrderDetailCode(), e);
                        }
                    }

                }
                return new BaseResponse(611);
            }
    }

}
