package com.imedia.service.order;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.dao.OrderDAO;
import com.imedia.oracle.dao.ShopProfileDAO;
import com.imedia.oracle.entity.*;
import com.imedia.oracle.repository.*;
import com.imedia.service.order.dto.RemainOrderDetailOM;
import com.imedia.service.order.enums.OrderStatusEnum;
import com.imedia.service.order.model.*;
import com.imedia.service.postage.FeeService;
import com.imedia.service.postage.PostageService;
import com.imedia.service.postage.model.*;
import com.imedia.service.product.ProductService;
import com.imedia.service.user.model.UserInfoAddressData;
import com.imedia.service.userwallet.UserWalletService;
import com.imedia.service.userwallet.model.UserBalanceResponse;
import com.imedia.service.wallet.WalletService;
import com.imedia.util.CallRedis;
import com.imedia.util.GenerateTransactionId;
import com.imedia.util.PreLoadStaticUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderService {
    static final Logger logger = LogManager.getLogger(OrderService.class);
    static final Gson gson = new Gson();
    static final Gson gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private final AddressDeliveryRepository addressDeliveryRepository;
    private final AppUserRepository appUserRepository;
    private final ShopProfileDAO shopProfileDAO;
    private final MapperFacade mapperFacade;
    private final PostageService postageService;
    private final WalletService walletService;
    private final UserWalletService userWalletService;
    private final V2PackageRepository packageRepository;
    private final SvcOrderRepository orderRepository;
    private final SvcOrderDetailRepository orderDetailRepository;
    private final DetailProductRepository productRepository;
    private final DetailSellingFeeRepository detailSellingFeeRepository;
    private final ShopAddressRepository shopAddressRepository;
    private final OrderDAO orderDAO;
    private final SvcOrderLogRepository orderLogRepository;
    private final OrderDraftRepository orderDraftRepository;
    private final WalletLogRepository walletLogRepository;
    private final CarrierRepository carrierRepository;
    private final ForwardConfigRepository forwardConfigRepository;
    private final OrderReadService orderReadService;
    private final FeeService feeService;
    private final ProductService productService;
    private final OrderLogService orderLogService;
    private final FindShipService findShipService;
    private final AddressDeliveryService addressDeliveryService;

    @Autowired
    public OrderService(AddressDeliveryRepository addressDeliveryRepository, AppUserRepository appUserRepository, ShopProfileDAO shopProfileDAO, MapperFacade mapperFacade, PostageService postageService, WalletService walletService, UserWalletService userWalletService, V2PackageRepository packageRepository, SvcOrderRepository orderRepository, SvcOrderDetailRepository orderDetailRepository, DetailProductRepository productRepository, DetailSellingFeeRepository detailSellingFeeRepository, ShopAddressRepository shopAddressRepository, OrderDAO orderDAO, SvcOrderLogRepository orderLogRepository, OrderDraftRepository orderDraftRepository, WalletLogRepository walletLogRepository, CarrierRepository carrierRepository, ForwardConfigRepository forwardConfigRepository, OrderReadService orderReadService, FeeService feeService, ProductService productService, OrderLogService orderLogService, FindShipService findShipService, AddressDeliveryService addressDeliveryService) {
        this.addressDeliveryRepository = addressDeliveryRepository;
        this.appUserRepository = appUserRepository;
        this.shopProfileDAO = shopProfileDAO;
        this.mapperFacade = mapperFacade;
        this.postageService = postageService;
        this.walletService = walletService;
        this.userWalletService = userWalletService;
        this.packageRepository = packageRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.detailSellingFeeRepository = detailSellingFeeRepository;
        this.shopAddressRepository = shopAddressRepository;
        this.orderDAO = orderDAO;
        this.orderLogRepository = orderLogRepository;
        this.orderDraftRepository = orderDraftRepository;
        this.walletLogRepository = walletLogRepository;
        this.carrierRepository = carrierRepository;
        this.forwardConfigRepository = forwardConfigRepository;
        this.orderReadService = orderReadService;
        this.feeService = feeService;
        this.productService = productService;
        this.orderLogService = orderLogService;
        this.findShipService = findShipService;
        this.addressDeliveryService = addressDeliveryService;
    }


    public BaseResponse handleCreateOrder(CreateOrder createOrder, String username, String channel) throws Exception {
        AppUser appUser = appUserRepository.findByPhone(username);
        //Validate packages for deliver points
        V2Package v2Package = packageRepository.findByCode(createOrder.getPackCode());
        ShopAddress shopAddress = shopAddressRepository.getShopAddress(createOrder.getPickupAddressId(), username);
//        ShopProfile shopProfile = shopProfileRepository.findByAppUserId(appUser.getId());
        if (validateCreateOrder(v2Package, createOrder, shopAddress) != null)
            return validateCreateOrder(v2Package, createOrder, shopAddress);
        //Validate inputs
        BaseResponse baseResponse = new BaseResponse(500, createOrder);
        try {
            //TODO RESOLVE MESSAGE
            baseResponse = postageService.calculateFee(createOrder, appUser);
        } catch (Exception e) {
            logger.info("======CALCULATE FEE EXCEPTION======", e);
        }
        if (baseResponse == null)
            return new BaseResponse(500, createOrder);
        if (baseResponse.getStatus() != 200) {
//            baseResponse.setData(createOrder);
            return baseResponse;
        }
        if (baseResponse.getStatus() == 200) {
            //TODO CHECK
            CalculateFeeOrderResponse calculateFeeOrderResponse = mapperFacade.map(baseResponse.getData(), CalculateFeeOrderResponse.class);
            //Kiểm tra dịch vụ của gói cước có phù hợp không
            //goi dong gia và là giao 1 phần hoặc người nhận trả phí
            int isFree = createOrder.getDeliveryPoint().get(0).getReceivers().get(0).getIsFree();
            int isPartial = createOrder.getDeliveryPoint().get(0).getReceivers().get(0).getPartialDelivery();
            int priceSettingId = calculateFeeOrderResponse.getData().getDeliveryPoint()
                    .get(0).getReceivers().get(0).getPriceSettingId();
            int paymentType = createOrder.getPaymentType();
            List<ForWardConfigOM> configIMS = new ArrayList<>();
            if (v2Package.getPriceSettingType() == 1
                    && (isFree == 2 || isPartial == 1 || paymentType == 1)) {
                configIMS = orderDAO.getTargetConfig(v2Package.getCode(), priceSettingId, isPartial, isFree, paymentType);
                if (configIMS.size() == 0) {
                    String message = handleFeeReasonCode(isPartial, isFree, paymentType);
                    return new BaseResponse(610, message, 1);
                }
            }
            //CHECK SO DU danh cho thanh toan vi hola
            boolean isEnoughMoney = true;
            BigDecimal minimumMoneyToConfirm = BigDecimal.ZERO;
            if (createOrder.getPaymentType() == 2) {
                CalculateFeeResponseAPI totalFee = postageService.buildTotalFee(calculateFeeOrderResponse);
                UserBalanceResponse userInfo = userWalletService.getBalances(username);
                if (userInfo.getAvailableHold().compareTo(totalFee.getTotalFee()) < 0) {
                    minimumMoneyToConfirm = totalFee.getTotalFee().subtract(userInfo.getAvailableHold());
                    isEnoughMoney = false;
                }
            }
            BigDecimal shopId = shopProfileDAO.getShopProfileIDFromAppPhone(username);
            //Tao order
            SvcOrder order = null;
            try {
                if (isEnoughMoney)
                    order = createOrder(createOrder, username, BigDecimal.valueOf(appUser.getId()), v2Package,
                            shopId, priceSettingId, OrderStatusEnum.CONFIRMED.code);
                else order = createOrder(createOrder, username, BigDecimal.valueOf(appUser.getId()),
                        v2Package, shopId, priceSettingId, OrderStatusEnum.WAIT_TO_CONFIRM.code);
            } catch (Exception e) {
                logger.info("======CREATE ORDER EXCEPTION=====" + gson.toJson(createOrder), e);
                return new BaseResponse(602);
            }
            //Tao order detail
            List<SvcOrderDetail> orderDetails = createOrderDetail(createOrder, order, v2Package, appUser,
                    calculateFeeOrderResponse, priceSettingId, channel);
            //Check list entity created?
            //Count receivers
            int totalReceivers = 0;
            for (CreateOrderDetail createOrderDetail : createOrder.getDeliveryPoint()) {
                totalReceivers = totalReceivers + createOrderDetail.getReceivers().size();
            }
            if (orderDetails.size() == totalReceivers) {
                //Save to db
                try {
                    SvcOrder svcOrder = orderRepository.save(order);
                    orderDetails.forEach(d -> {
                        d.setSvcOrderId(svcOrder.getOrderCode());
                    });
                    List<SvcOrderDetail> svcOrderDetails = orderDetailRepository.saveAll(orderDetails);
                    logger.info("======SVC ORDER SAVE TO DB=======" + gson.toJson(svcOrder));
                    logger.info("======SVC ORDER DETAIL SAVE TO DB=======" + gson.toJson(svcOrderDetails));
                    //Insert log
                    if (isEnoughMoney)
                        orderLogService.insertOrderLog(svcOrderDetails, OrderStatusEnum.NOTE_CREATED.message, OrderStatusEnum.CONFIRMED.code);
                    else
                        orderLogService.insertOrderLog(svcOrderDetails, OrderStatusEnum.NOTE_HOLD_FAILED.message, OrderStatusEnum.WAIT_TO_CONFIRM.code);
                    //Tam giu
                    if (isEnoughMoney)
                        handleHoldBalance(svcOrderDetails, appUser);
                    //Đẩy đơn sang NVC
                    if (isEnoughMoney && svcOrder.getType() == 1) {
                        //Nếu cấu hình đặc biệt có hola => chỉ đẩy cho Hola và end luôn
                        List<ForWardConfigOM> holaSpecialConfig = configIMS.stream()
                                .filter(c -> c.getCode().equalsIgnoreCase("HLS") || c.getCode().equalsIgnoreCase("HOLA"))
                                .collect(Collectors.toList());
                        if (holaSpecialConfig.size() > 0) {
                            baseResponse = findShipService.handleFindShip(order, orderDetails, appUser);
                            if (baseResponse.getStatus() == 612)
                                return new BaseResponse(600, svcOrder);
                            else return baseResponse;
                        }
                        //Tìm cấu hình không đặc biệt
                        if (configIMS.size() == 0) {
                            configIMS = orderDAO.getTargetConfig(priceSettingId);
                            List<ForWardConfigOM> holaConfig = configIMS.stream()
                                    .filter(c -> c.getCode().equalsIgnoreCase("HLS") || c.getCode().equalsIgnoreCase("HOLA"))
                                    .collect(Collectors.toList());
                            if (holaConfig.size() > 0) {
                                baseResponse = findShipService.handleFindShip(order, orderDetails, appUser);
                                if (baseResponse.getStatus() == 612)
                                    return new BaseResponse(600, svcOrder);
                                else return baseResponse;
                            }
                        }
                        //Còn lại sẽ là đơn NVc khác và là đơn 1 1
                        HashMap<String, Object> pushRequest = new HashMap<>();
                        pushRequest.put("configs", gson.toJson(configIMS));
                        pushRequest.put("orderDetail", gson.toJson(svcOrderDetails.get(0)));
                        boolean pushed = CallRedis.pushQueue("PUSH_SP_ORDER", pushRequest.toString(), null);
                        if (!pushed) {
                            handleRollbackCreateOrder(order, orderDetails, appUser, 1);
                            return new BaseResponse(613);
                        }
                    }
                    //Tìm shipper nếu là đơn km
                    if (isEnoughMoney && svcOrder.getType() == 2) {
//                        List<PushKmOrderDetailRequest> detailRequests = buildPushDetailRequest(orderDetails);
                        try {
                            baseResponse = findShipService.handleFindShip(order, orderDetails, appUser);
                            if (baseResponse.getStatus() == 612)
                                return new BaseResponse(600, svcOrder);
                        } catch (Exception e) {
                            logger.info("=====FIND SHIP EXCEPTION======" + gson.toJson(createOrder), e);
                        }
                    }
                    if (!isEnoughMoney) {
                        HashMap<String, Object> minimumMoney = new HashMap<>();
                        minimumMoney.put("minimumMoneyToConfirm", minimumMoneyToConfirm);
                        return new BaseResponse(601, minimumMoney);
                    } else return new BaseResponse(600, svcOrder);
                } catch (Exception e) {
                    logger.info("======SAVE ORDER EXCEPTION======= ROLLING BACK : ORDER : " + gson.toJson(order)
                            + " ORDER DETAILS : " + gson.toJson(orderDetails), e);
                    handleRollbackCreateOrder(order, orderDetails, appUser, 1);
                    return new BaseResponse(602);
                }
            } else
                return new BaseResponse(603);
        }
        return baseResponse;
    }


    private SvcOrder createOrder(CreateOrder createOrder, String username,
                                 BigDecimal appUserId, V2Package v2Package,
                                 BigDecimal shopProfileId, int priceSettingId, int status) {
        SvcOrder order = new SvcOrder();
        BigDecimal orderId = GenerateTransactionId.generateUniqueOrderCode();
        //Check exist
        boolean checkExisted = checkOrderCode(orderId);
        while (!checkExisted) {
            orderId = GenerateTransactionId.generateUniqueOrderCode();
            checkExisted = checkOrderCode(orderId);
        }
        order.setOrderCode(orderId);
        order.setShopId(appUserId);
        //TODO SERVICE PACK ID => TYPE
        order.setServicePackId(v2Package.getId());
        order.setType(v2Package.getPriceSettingType());
        order.setShopIdReference(shopProfileId);
        //No setting service pack(wrong for order)
        order.setServicePackSettingId(BigDecimal.valueOf(priceSettingId));
        order.setShopAddressId(createOrder.getPickupAddressId());
        //1 la NVC toi lay hang 0 la mang hang ra buu cuc
        order.setStatus(status);
        order.setPaymentType(createOrder.getPaymentType());
//        if (createOrder.getPickupType() == 0)
//            order.setStatus(OrderStatusEnum.WAIT_TO_BRING.code);
        if (createOrder.getShopOrderCode() != null && !createOrder.getShopOrderCode().isEmpty())
            order.setShopOrderId(order.getShopOrderId());
        else order.setShopOrderId(String.valueOf(orderId));
        order.setPickupType(createOrder.getPickupType());
        //TODO check these values
        order.setTotalAddressDilivery((long) createOrder.getDeliveryPoint().size());
        order.setTotalDistance(0L);
        //Total details
        AtomicInteger totalDetail = new AtomicInteger();
        createOrder.getDeliveryPoint().forEach(o -> {
            o.getReceivers().forEach(r -> {
                totalDetail.addAndGet(1);
            });
        });
        order.setTotalOrderDetail(totalDetail.intValue());
        order.setExpectShipId(0L);

//        SvcOrder result = orderRepository.save(order);
//        logger.info("======SVC ORDER SAVED TO DB========" + gson.toJson(result));
        return order;
    }

    private List<SvcOrderDetail> createOrderDetail(CreateOrder createOrder, SvcOrder order,
                                                   V2Package v2Package, AppUser appUser,
                                                   CalculateFeeOrderResponse calculateFeeOrderResponse,
                                                   int priceSettingId, String channel) {
        CalculateFeeOrderData feeOrderData = calculateFeeOrderResponse.getData();
        List<SvcOrderDetail> orderDetails = new ArrayList<>();
//        for (OrderDetail detail : createOrder.getDeliveryPoint()) {
        BigDecimal index = BigDecimal.ONE;
        for (int i = 0; i < createOrder.getDeliveryPoint().size(); i++) {
            CreateOrderDetail detail = createOrder.getDeliveryPoint().get(i);
            CalculateFeeDeliveryPoint calculateFeeDeliveryPoint = feeOrderData.getDeliveryPoint().get(i);
            //Tao diem nhan hang
            AddressDelivery addressDelivery = null;
            try {
                UserInfoAddressData addressData = PreLoadStaticUtil.addresses.get(detail.getWard());
                AddressDeliveryRequest addressDeliveryRequest = new AddressDeliveryRequest();
                addressDeliveryRequest.setProvinceCode(detail.getProvince());
                addressDeliveryRequest.setProvinceName(addressData.getProvinceName());
                addressDeliveryRequest.setDistrictCode(detail.getDistrict());
                addressDeliveryRequest.setDistrictName(addressData.getDistrictName());
                addressDeliveryRequest.setWardCode(detail.getWard());
                addressDeliveryRequest.setWardName(addressData.getWardName());
                addressDeliveryRequest.setAddress(detail.getAddress());
                BaseResponse deliveryResponse = addressDeliveryService.createAddressDelivery(addressDeliveryRequest);
                if (deliveryResponse != null && deliveryResponse.getStatus() == 200) {
                    addressDelivery = (AddressDelivery) deliveryResponse.getData();
                }
            } catch (Exception e) {
                logger.info("=======CREATE ORDER ADDRESS DELIVERY EXCEPTION======" + gson.toJson(detail), e);
                continue;
            }
            if (addressDelivery != null) {
//                for (OrderReceiver orderReceiver : detail.getReceivers()) {
                for (int j = 0; j < detail.getReceivers().size(); j++) {
                    CreateOrderReceiver createOrderReceiver = detail.getReceivers().get(j);
                    CalculateFeeReceivers calculateFeeReceivers = calculateFeeDeliveryPoint.getReceivers().get(j);
                    SvcOrderDetail orderDetail = new SvcOrderDetail();
                    //Channel
                    orderDetail.setChannel(channel);
                    //Code
                    orderDetail.setSvcOrderDetailCode(order.getOrderCode().multiply(BigDecimal.valueOf(10)).add(index));
                    orderDetail.setShopId(order.getShopId().longValue());
                    orderDetail.setSvcOrderId(order.getOrderCode());
                    orderDetail.setAddressDeliveryId(addressDelivery.getId());
                    orderDetail.setServicePackId(v2Package.getId().intValue());
//                    if (calculateFeeReceivers.getPriceSettingId() != null)
                    orderDetail.setServicePackSettingId(calculateFeeReceivers.getPriceSettingId());
//                    else orderDetail.setServicePackSettingId(calculateFeeReceivers.getPackageId());
                    //receiver
                    orderDetail.setConsignee(createOrderReceiver.getName());
                    orderDetail.setPhone(createOrderReceiver.getPhone());
                    //Thong so
                    orderDetail.setWeight(createOrderReceiver.getWeight());
                    orderDetail.setLength(createOrderReceiver.getLength());
                    orderDetail.setWidth(createOrderReceiver.getWidth());
                    orderDetail.setHeight(createOrderReceiver.getHeight());
                    //Others
                    orderDetail.setIsPartDelivery(createOrderReceiver.getPartialDelivery());
                    orderDetail.setIsRefund(createOrderReceiver.getIsRefund());
                    orderDetail.setIsPorter(createOrderReceiver.getExtraServices().get(0).getIsPorter());
                    orderDetail.setIsDoorDelivery(createOrderReceiver.getExtraServices().get(0).getIsDoorDeliver());
                    orderDetail.setPickType(order.getPickupType());
                    orderDetail.setPaymentType(order.getPaymentType());
                    //TODO check declare product
                    orderDetail.setRequiredNote(createOrderReceiver.getRequireNote());
                    orderDetail.setNote(createOrderReceiver.getNote());
                    orderDetail.setIsFree(createOrderReceiver.getIsFree());
                    //Status
                    orderDetail.setStatus(order.getStatus());
                    orderDetail.setOldStatus(order.getStatus());
                    //Shop order id
                    if (createOrder.getShopOrderCode() != null)
                        orderDetail.setShopOrderId(createOrder.getShopOrderCode());
                    orderDetail.setShopAddressId(order.getShopAddressId());
                    //Date
                    //TODO no expect pick date in front end
                    orderDetail.setExpectPickDate(null);
                    if (!createOrderReceiver.getExpectDate().isEmpty()) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        String expectDate = createOrderReceiver.getExpectDate().replaceAll(" ", "")
                                + " "
                                + createOrderReceiver.getExpectTime().replaceAll(" ", "");
                        try {
                            orderDetail.setExpectDeliverDate(new Timestamp(dateFormat.parse(expectDate).getTime()));
                        } catch (ParseException e) {
                            logger.info("========CREATE ORDER DETAIL PARSE DATE EXCEPTION======" + orderDetail.getSvcOrderDetailCode()
                                    + "||" + e.getMessage());
                            orderDetail.setExpectDeliverDate(null);
                        }
                    }
                    //Gói Km
                    if (v2Package.getPriceSettingType() == 2) {
                        List<LeadtimeForwardConfig> forwardConfigs = forwardConfigRepository
                                .findAllByPriceSettingIdAndIsDeleteAndPackIdOrderByRankAsc(priceSettingId, 1,
                                        v2Package.getId().intValue());
                        if (forwardConfigs != null && forwardConfigs.size() > 0) {
                            orderDetail.setCarrierId(forwardConfigs.get(0).getCarrierId().longValue());
                            orderDetail.setCarrierServiceCode(forwardConfigs.get(0).getCarrierPackCode());
                        }
                    }
                    //Save
                    //Insert selling fee first
                    try {
                        feeService.createSellingFee(orderDetail.getSvcOrderDetailCode(), calculateFeeReceivers);
                        List<DetailProduct> products = productService.createDetailProduct(createOrderReceiver, orderDetail);
                        BigDecimal realityCod = BigDecimal.ZERO;
                        for (DetailProduct product : products) {
                            realityCod = realityCod.add(product.getCod().multiply(product.getQuantity()));
                        }
                        orderDetail.setRealityCod(realityCod);
                        orderDetail.setExpectCod(realityCod);
                    } catch (Exception e) {
                        logger.info("=======CREATE SELLING FEE EXCEPTION=======" + appUser.getPhone(), e);
                        break;
                    }
                    orderDetails.add(orderDetail);
                    index = index.add(BigDecimal.ONE);
                }
            }
        }
        return orderDetails;
    }

    private void handleHoldBalance(List<SvcOrderDetail> svcOrderDetails, AppUser appUser) {
        for (SvcOrderDetail orderDetail : svcOrderDetails) {
            List<DetailSellingFee> sellingFees = detailSellingFeeRepository.findAllByOrderDetailCode(orderDetail.getSvcOrderDetailCode());
            BigDecimal totalHold = BigDecimal.ZERO;
            for (DetailSellingFee sellingFee : sellingFees) {
                totalHold = totalHold.add(sellingFee.getValue());
            }
            try {
                walletService.addHoldingBalance(orderDetail.getSvcOrderDetailCode(), appUser, totalHold);
            } catch (Exception e) {
                logger.info("======ADD HOLDING BALANCE EXCEPTION=======" + orderDetail.getSvcOrderDetailCode(), e);
            }
        }
    }

    private void handleRollbackCreateOrder(SvcOrder order, List<SvcOrderDetail> orderDetails, AppUser appUser, int type) {
        //Rollback delete all
        if (type == 2) {
            for (SvcOrderDetail orderDetail : orderDetails) {
//                detailSellingFeeRepository.deleteSellingFeeByDetailCode(orderDetail.getSvcOrderDetailCode());
                productRepository.deleteProductByDetailCode(orderDetail.getSvcOrderDetailCode());
                orderDetailRepository.deleteDetailByCode(orderDetail.getSvcOrderDetailCode());
            }
            orderRepository.deleteOrderByCode(order.getOrderCode());
        } else {
            for (SvcOrderDetail orderDetail : orderDetails) {
//                detailSellingFeeRepository.deleteSellingFeeByDetailCode(orderDetail.getSvcOrderDetailCode());
                orderDetail.setStatus(OrderStatusEnum.WAIT_TO_CONFIRM.code);
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
            orderDetailRepository.saveAll(orderDetails);
            order.setStatus(OrderStatusEnum.WAIT_TO_CONFIRM.code);
            orderRepository.save(order);
        }
    }

    private List<PushKmOrderDetailRequest> buildPushDetailRequest(List<SvcOrderDetail> orderDetails) {
        List<PushKmOrderDetailRequest> orderDetailRequests = new ArrayList<>();
        for (SvcOrderDetail orderDetail : orderDetails) {
            PushKmOrderDetailRequest orderDetailRequest = mapperFacade.map(orderDetail, PushKmOrderDetailRequest.class);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:s");
            String expectDelivery = dateFormat.format(orderDetail.getExpectDeliverDate());
            String expectPick = dateFormat.format(orderDetail.getExpectPickDate());
            orderDetailRequest.setExpectDeliverDate(expectDelivery);
            orderDetailRequest.setExpectPickDate(expectPick);
            orderDetailRequests.add(orderDetailRequest);
        }
        return orderDetailRequests;
    }


    private BaseResponse validateCreateOrder(V2Package v2Package, CreateOrder createOrder,
                                             ShopAddress shopAddress) {
        if (shopAddress == null)
            return new BaseResponse(607);
        if (createOrder.getDeliveryPoint() == null || createOrder.getDeliveryPoint().size() == 0)
            return new BaseResponse(606);
        //Same price 1 point 1 receiver only
        if (v2Package.getPriceSettingType() == 1) {
            if (createOrder.getDeliveryPoint().size() == 1 && createOrder.getDeliveryPoint().get(0).getReceivers().size() == 1)
                return null;
            else return new BaseResponse(605);
        }
        if (createOrder.getPaymentType() == null)
            return new BaseResponse(666);

        for (CreateOrderDetail createOrderDetail : createOrder.getDeliveryPoint()) {
            for (CreateOrderReceiver receiver : createOrderDetail.getReceivers()) {
                if (receiver.getItems() == null || receiver.getItems().size() == 0)
                    return new BaseResponse(666);
                for (OrderDetailProduct item : receiver.getItems()) {
                    if (item.getProductName() == null || item.getProductName().isEmpty())
                        return new BaseResponse(666);
                    if (item.getCod() == null)
                        return new BaseResponse(666);
                    if (item.getQuantity() == null)
                        return new BaseResponse(666);
                }
            }
        }
        return null;
    }


    public BaseResponse draftOrder(OrderDraftRequest request, String username) {
        OrderDraft orderDraft = orderDraftRepository.findByOrderId(request.getOrderId());
        if (orderDraft != null) {
            orderDraft.setMessage(gson.toJson(request.getOrderDetail()));
        } else {
            orderDraft = new OrderDraft();
            AppUser appUser = appUserRepository.findByPhone(username);
            orderDraft.setMessage(gson.toJson(request.getOrderDetail()));
            orderDraft.setOrderId(request.getOrderId());
            orderDraft.setShopId(BigDecimal.valueOf(appUser.getId()));
        }
        orderDraftRepository.save(orderDraft);
        return new BaseResponse(200, orderDraft);
    }

    public void updateOrderStatus(SvcOrderDetail orderDetail, List<Integer> statuses) throws Exception {
        RemainOrderDetailOM remainOrderDetail = orderDetailRepository.getRemainOrderDetail(orderDetail.getSvcOrderId(), statuses);
        //Last orderDetail changed => update big order to status of last
        if (remainOrderDetail == null) {
            logger.info("=======LAST ORDER DETAIL CHANGED========" + orderDetail.getSvcOrderDetailCode()
                    + "|| STATUS " + orderDetail.getStatus());
            orderRepository.updateStatus(orderDetail.getStatus(), orderDetail.getSvcOrderId());
            logger.info("=======UPDATED BIG ORDER TO STATUS========");
        }
    }

    private String handleFeeReasonCode(int isPartial, int isFree, int paymentType) {
        StringBuilder note = new StringBuilder(OrderStatusEnum.PRE_FEE_FAIL.message);
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
        return note.substring(0, note.length() - 1);
    }

    private boolean checkOrderCode(BigDecimal orderCode) {
        String cache = CallRedis.getCache("ORDER_CODE");
        if (cache == null) {
            CacheOrderCode cacheOrderCode = new CacheOrderCode();
            cacheOrderCode.setOrderCodes(Collections.singletonList(orderCode));
            CallRedis.setCacheExpiry("ORDER_CODE", gson.toJson(cacheOrderCode), 1);
            return true;
        } else {
            CacheOrderCode cacheOrderCode = gson.fromJson(cache, CacheOrderCode.class);
            if (!cacheOrderCode.getOrderCodes().contains(orderCode)) {
                cacheOrderCode.getOrderCodes().add(orderCode);
                CallRedis.setCacheExpiry("ORDER_CODE", gson.toJson(cacheOrderCode), 1);
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
//        int a = new Random().nextInt(9);
//        System.out.println(a);
//        int b = 2;
//        while (b != 1) {
//            a = new Random().nextInt(9);
//            b = a;
//            System.out.println(b);
//        }/


    }

}
