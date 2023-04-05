package com.imedia.service.order;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.dao.OrderDAO;
import com.imedia.oracle.dao.ShopProfileDAO;
import com.imedia.oracle.entity.*;
import com.imedia.oracle.repository.*;
import com.imedia.service.order.enums.OrderStatusEnum;
import com.imedia.service.order.enums.SellingFeeEnum;
import com.imedia.service.order.model.AddressDeliveryRequest;
import com.imedia.service.order.model.CacheOrderCode;
import com.imedia.service.order.model.CreateOrderFile;
import com.imedia.service.order.model.CreateOrderFileFee;
import com.imedia.service.postage.PostageService;
import com.imedia.service.postage.model.ForWardConfigOM;
import com.imedia.service.product.ProductService;
import com.imedia.service.user.model.UserInfoAddressData;
import com.imedia.service.userwallet.UserWalletService;
import com.imedia.service.userwallet.model.UserBalanceResponse;
import com.imedia.service.wallet.WalletService;
import com.imedia.service.wallet.model.WalletBaseResponse;
import com.imedia.util.CallRedis;
import com.imedia.util.GenerateTransactionId;
import com.imedia.util.PreLoadStaticUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderFileService {
    static final Logger logger = LogManager.getLogger(OrderFileService.class);
    static final Gson gson = new GsonBuilder().serializeNulls().create();
    private final AppUserRepository appUserRepository;
    private final ShopAddressRepository shopAddressRepository;
    private final ShopProfileDAO shopProfileDAO;
    private final PostageService postageService;
    private final WalletService walletService;
    private final UserWalletService userWalletService;
    private final V2PackageRepository packageRepository;
    private final SvcOrderRepository orderRepository;
    private final SvcOrderDetailRepository orderDetailRepository;
    private final DetailSellingFeeRepository detailSellingFeeRepository;
    private final OrderDAO orderDAO;
    static final Gson gson2 = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private final FindShipService findShipService;
    private final AddressDeliveryService addressDeliveryService;
    private final ProductService productService;
    private final OrderLogService orderLogService;

    @Autowired
    public OrderFileService(AppUserRepository appUserRepository, ShopAddressRepository shopAddressRepository, ShopProfileDAO shopProfileDAO, PostageService postageService, WalletService walletService, UserWalletService userWalletService, V2PackageRepository packageRepository, SvcOrderRepository orderRepository, SvcOrderDetailRepository orderDetailRepository, DetailSellingFeeRepository detailSellingFeeRepository, OrderDAO orderDAO, FindShipService findShipService, AddressDeliveryService addressDeliveryService, ProductService productService, OrderLogService orderLogService) {
        this.appUserRepository = appUserRepository;
        this.shopAddressRepository = shopAddressRepository;
        this.shopProfileDAO = shopProfileDAO;
        this.postageService = postageService;
        this.walletService = walletService;
        this.userWalletService = userWalletService;
        this.packageRepository = packageRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.detailSellingFeeRepository = detailSellingFeeRepository;
        this.orderDAO = orderDAO;
        this.findShipService = findShipService;
        this.addressDeliveryService = addressDeliveryService;
        this.productService = productService;
        this.orderLogService = orderLogService;
    }


    public List<CreateOrderFile> validateInputs(List<CreateOrderFile> createOrderFiles, String username) throws Exception {
//        List<CreateOrderFile> distinct = createOrderFiles.stream()
//                .filter(AppUtil.distinctByKey(CreateOrderFile::getPackageCode))
//                .collect(Collectors.toList());
//        List<String> packageCodes = new ArrayList<>();
//        distinct.forEach(d -> packageCodes.add(d.getPackageCode()));
//        List<V2Package> validPackageList = packageRepository.findAllByStatusAndPriceSettingTypeAndCodeIn(BigDecimal.ONE, 1, packageCodes);
//        HashMap<String, V2Package> validPackages = new HashMap<>();
//        validPackageList.forEach(p -> {
//            validPackages.put(p.getCode(), p);
//        });
//        List<CreateOrderFile> responseData = new ArrayList<>();
//        for (CreateOrderFile createOrderFile : createOrderFiles) {
//            CreateOrderFileFee createOrderFileFee = new CreateOrderFileFee();
//            V2Package v2Package = validPackages.get(createOrderFile.getPackageCode());
//            if (v2Package != null) {
//                createOrderFileFee.setPackageName(v2Package.getName());
//                createOrderFileFee.setPackageCode(v2Package.getCode());
//                createOrderFile.setFees(Collections.singletonList(createOrderFileFee));
//            } else {
//                createOrderFile.setFees(new ArrayList<>());
//            }
//            responseData.add(createOrderFile);
//        }
        //Calculate fee
        List<CreateOrderFile> createOrderFileResponses = (List<CreateOrderFile>) postageService
                .calculateFeeOrderFile(createOrderFiles, appUserRepository.findByPhone(username)).getData();
        return createOrderFileResponses;
    }

    public BaseResponse createFileOrder(List<CreateOrderFile> createOrderFiles, String username) throws Exception {
        int totalPendingSuccess = 0;
        int totalPendingError = 0;
        logger.info("======CREATE FILE ORDER REQUEST======" + gson.toJson(createOrderFiles));
//        BigDecimal shopProfileId = shopProfileDAO.getShopProfileIDFromAppPhone(username);
        ShopAddress shopAddress = shopAddressRepository
                .getShopAddress(createOrderFiles.get(0).getPickupAddressID(), username);
        if (shopAddress == null)
            return new BaseResponse(607);
        //Validate valid packCode
        List<CreateOrderFile> validate = validateInputs(createOrderFiles, username);
        UserBalanceResponse balances = userWalletService.getBalances(username);
        BigDecimal remainWalletMoney = balances.getAvailableHold();
        BigDecimal remainMoneyToConfirm = BigDecimal.ZERO;
        for (CreateOrderFile createOrderFile : validate) {
            try {
                if (createOrderFile.getFees() != null && createOrderFile.getFees().size() > 0) {
                    BigDecimal totalFee = createOrderFile.getFees().get(0).getTotalFee();
                    if (createOrderFile.getPaymentType() == 2) {
                        if (remainWalletMoney.compareTo(totalFee) >= 0) {
                            remainWalletMoney = remainWalletMoney.subtract(totalFee);
                            totalPendingSuccess += 1;
                        } else {
                            remainMoneyToConfirm = remainMoneyToConfirm.add(totalFee);
                            totalPendingError += 1;
                        }
                    } else {
                        totalPendingSuccess += 1;
                    }
                    createOrderFile.setUserPhone(username);
                    boolean pushed = CallRedis.pushQueue("PUSH_FILE_ORDER", gson.toJson(createOrderFile), 4);
                }
            } catch (Exception e) {
                logger.info("======CREATE ORDER FILE EXCEPTION======" + gson.toJson(createOrderFile), e);
                continue;
            }
        }
        HashMap<String, Object> response = new HashMap<>();
        response.put("totalRequest", String.valueOf(createOrderFiles.size()));
        response.put("totalPendingSuccess", totalPendingSuccess);
        response.put("remainMoneyToConfirm", BigDecimal.ZERO);
        if (remainMoneyToConfirm.subtract(remainWalletMoney).compareTo(BigDecimal.ZERO) > 0)
            response.put("remainMoneyToConfirm", remainMoneyToConfirm.subtract(remainWalletMoney));
        response.put("totalPendingError", totalPendingError);
        List<CreateOrderFile> errorOrder = validate.stream().filter(v -> v.getFees() == null || v.getFees().size() == 0).collect(Collectors.toList());
        response.put("totalError", errorOrder.size());
        List<Integer> errorIndex = new ArrayList<>();
        if (errorOrder.size() > 0) {
            errorOrder.forEach(e -> {
                errorIndex.add(e.getIndex());
            });
        }
        response.put("errorOrderIndex", errorIndex);
        return new BaseResponse(600, response);
    }

    public void handleCreateFileOrder(String data) throws Exception {
        CreateOrderFile createOrderFile = gson.fromJson(data, CreateOrderFile.class);
//        ShopAddress shopAddress = shopAddressRepository.findShopAddressById(Long.valueOf(createOrderFile.getPickupAddr9essID()));
        AppUser appUser = appUserRepository.findByPhone(createOrderFile.getUserPhone());
        BigDecimal shopProfileId = shopProfileDAO.getShopProfileIDFromAppPhone(createOrderFile.getUserPhone());
        //Validate packages for deliver points
        V2Package v2Package = packageRepository.findByCode(createOrderFile.getPackageCode());
        //TODO thanh toán tiền mặt có tạm giữ k??
        //Check so du
        UserBalanceResponse userInfo = userWalletService.getBalances(createOrderFile.getUserPhone());
//        CalculateFeeOrderResponse calculateFeeOrderResponse = postageService.calculateFeeCreateOrderFile(createOrderFile, appUser);
//        if (userInfo != null && calculateFeeOrderResponse != null && v2Package != null && calculateFeeOrderResponse.getCode() == 0) {
        if (userInfo != null && v2Package != null && appUser != null) {
            BigDecimal totalFee = createOrderFile.getFees().get(0).getTotalFee();
            int isFree = createOrderFile.getOrderPayment();
            int isPartial = createOrderFile.getPartialDelivery();
            int paymentType = 2;
            if (createOrderFile.getPaymentType() != null)
                paymentType = createOrderFile.getPaymentType();
            if (userInfo.getAvailableHold().compareTo(totalFee) >= 0) {
                //goi dong gia và là giao 1 phần hoặc người nhận trả phí = > đặc biệt => luồng riêng
                if (v2Package.getPriceSettingType() == 1
                        && (isFree == 2 || isPartial == 1 || paymentType == 1)) {
                    //Kiểm tra dịch vụ của gói cước có phù hợp không
                    List<ForWardConfigOM> configIMS = orderDAO.getTargetConfig(v2Package.getCode(),
                            createOrderFile.getFees().get(0).getPriceSettingId(), isPartial, isFree, paymentType);
                    //Nếu có thì duyệt đơn không có thì để chờ duyệt với lý do gói cước chỉ trả trước
                    if (configIMS.size() > 0) {
                        //Đạt đủ điều kiện check => tạo đơn và tạm giữ đẩy đơn bình thường
                        SvcOrder order = createOrder(createOrderFile, BigDecimal.valueOf(appUser.getId()), v2Package, OrderStatusEnum.CONFIRMED.code, shopProfileId);
                        SvcOrderDetail orderDetail = createOrderDetail(createOrderFile, order, v2Package, appUser);
                        if (orderDetail != null) {
                            SvcOrder orderResult = orderRepository.save(order);
                            SvcOrderDetail orderDetailResult = orderDetailRepository.save(orderDetail);
                            logger.info("=====CREATE ORDER FILE SAVE DB=======" + gson.toJson(orderResult) + "|| ORDER DETAIL " + gson.toJson(orderDetailResult));
                            //Tam giu
                            try {
                                WalletBaseResponse walletBaseResponse = walletService.addHoldingBalance(orderDetail.getSvcOrderDetailCode(), appUser, totalFee);
                                if (walletBaseResponse.getStatus() == 200) {
                                    orderLogService.insertOrderLog(orderDetailResult, OrderStatusEnum.NOTE_CREATED.message, OrderStatusEnum.CONFIRMED.code);
                                    //Nếu cấu hình đặc biệt có hola => chỉ đẩy cho Hola và end luôn
                                    List<ForWardConfigOM> holaSpecialConfig = configIMS.stream()
                                            .filter(c -> c.getCode().equalsIgnoreCase("HLS") || c.getCode().equalsIgnoreCase("HOLA"))
                                            .collect(Collectors.toList());
                                    if (holaSpecialConfig.size() > 0) {
                                        findShipService.handleFindShip(order, Collections.singletonList(orderDetail), appUser);
                                        return;
                                    }
                                    //Tìm cấu hình không đặc biệt
                                    if (configIMS.size() == 0) {
                                        configIMS = orderDAO.getTargetConfig(createOrderFile.getFees().get(0).getPriceSettingId());
                                        List<ForWardConfigOM> holaConfig = configIMS.stream()
                                                .filter(c -> c.getCode().equalsIgnoreCase("HLS") || c.getCode().equalsIgnoreCase("HOLA"))
                                                .collect(Collectors.toList());
                                        if (holaConfig.size() > 0) {
                                            findShipService.handleFindShip(order, Collections.singletonList(orderDetail), appUser);
                                            return;
                                        }
                                    }
                                    //Còn lại sẽ là đơn NVC khác và là đơn 1 1
                                    HashMap<String, Object> pushRequest = new HashMap<>();
                                    pushRequest.put("configs", gson.toJson(configIMS));
                                    pushRequest.put("orderDetail", gson.toJson(orderDetail));
                                    CallRedis.pushQueue("PUSH_SP_ORDER", pushRequest.toString(), 4);
                                } else {
                                    orderResult.setStatus(OrderStatusEnum.WAIT_TO_CONFIRM.code);
                                    orderDetailResult.setStatus(OrderStatusEnum.WAIT_TO_CONFIRM.code);
                                    orderLogService.insertOrderLog(orderDetailResult, OrderStatusEnum.NOTE_HOLD_FAILED.message, OrderStatusEnum.WAIT_TO_CONFIRM.code);
                                    orderDetailRepository.save(orderDetailResult);
                                }
                            } catch (Exception e) {
                                logger.info("======CREATE ORDER FILE HOLDING EXCEPTION======" + orderDetail.getSvcOrderDetailCode(), e);
                            }
                        }
                    }
                    //Không thỏa mãn => tạo đơn với trạng thái chờ duyệt => lý do
                    else {
                        SvcOrder order = createOrder(createOrderFile, BigDecimal.valueOf(appUser.getId()), v2Package, OrderStatusEnum.WAIT_TO_CONFIRM.code, shopProfileId);
                        SvcOrderDetail orderDetail = createOrderDetail(createOrderFile, order, v2Package, appUser);
                        if (orderDetail != null) {
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
                            orderLogService.insertOrderLog(Collections.singletonList(orderDetail), note.substring(0, note.length() - 1), OrderStatusEnum.WAIT_TO_CONFIRM.code);
                            SvcOrder orderResult = orderRepository.save(order);
                            SvcOrderDetail orderDetailResult = orderDetailRepository.save(orderDetail);
                            logger.info("=====CREATE ORDER FILE SAVE DB=======" + gson.toJson(orderResult)
                                    + "|| ORDER DETAIL " + gson.toJson(orderDetailResult));
                        }
                    }
                }
                //Không phải đơn đặc biệt => tạo và tạm giữ bình thường
                else {
                    //Lấy ra cấu hình
                    List<ForWardConfigOM> configIMS = orderDAO.getTargetConfig(v2Package.getCode(),
                            createOrderFile.getFees().get(0).getPriceSettingId(), 0, 1, 2);
                    SvcOrder order = createOrder(createOrderFile, BigDecimal.valueOf(appUser.getId()), v2Package, OrderStatusEnum.CONFIRMED.code, shopProfileId);
                    SvcOrderDetail orderDetail = createOrderDetail(createOrderFile, order, v2Package, appUser);
                    if (orderDetail != null) {
                        SvcOrder orderResult = orderRepository.save(order);
                        SvcOrderDetail orderDetailResult = orderDetailRepository.save(orderDetail);
                        logger.info("=====CREATE ORDER FILE SAVE DB=======" + gson.toJson(orderResult) + "|| ORDER DETAIL " + gson.toJson(orderDetailResult));
                        //Tam giu
                        try {
                            WalletBaseResponse walletBaseResponse = walletService.addHoldingBalance(orderDetail.getSvcOrderDetailCode(), appUser, totalFee);
                            if (walletBaseResponse.getStatus() == 200) {
                                orderLogService.insertOrderLog(orderDetailResult, OrderStatusEnum.NOTE_CREATED.message, OrderStatusEnum.CONFIRMED.code);
                                //Nếu cấu hình có hola => chỉ đẩy cho Hola và end luôn
                                List<ForWardConfigOM> holaSpecialConfig = configIMS.stream()
                                        .filter(c -> c.getCode().equalsIgnoreCase("HLS") || c.getCode().equalsIgnoreCase("HOLA"))
                                        .collect(Collectors.toList());
                                if (holaSpecialConfig.size() > 0) {
                                    findShipService.handleFindShip(order, Collections.singletonList(orderDetail), appUser);
                                    return;
                                }
                                //Tìm cấu hình không đặc biệt
                                if (configIMS.size() == 0) {
                                    configIMS = orderDAO.getTargetConfig(createOrderFile.getFees().get(0).getPriceSettingId());
                                    List<ForWardConfigOM> holaConfig = configIMS.stream()
                                            .filter(c -> c.getCode().equalsIgnoreCase("HLS") || c.getCode().equalsIgnoreCase("HOLA"))
                                            .collect(Collectors.toList());
                                    if (holaConfig.size() > 0) {
                                        findShipService.handleFindShip(order, Collections.singletonList(orderDetail), appUser);
                                        return;
                                    }
                                }
                                //Còn lại sẽ là đơn NVc khác và là đơn 1 1
                                HashMap<String, Object> pushRequest = new HashMap<>();
                                pushRequest.put("configs", null);
                                pushRequest.put("orderDetail", gson.toJson(orderDetail));
                                CallRedis.pushQueue("PUSH_SP_ORDER", pushRequest.toString(), null);
                            } else {
                                orderResult.setStatus(OrderStatusEnum.WAIT_TO_CONFIRM.code);
                                orderDetailResult.setStatus(OrderStatusEnum.WAIT_TO_CONFIRM.code);
                                orderLogService.insertOrderLog(orderDetailResult, OrderStatusEnum.NOTE_HOLD_FAILED.message, OrderStatusEnum.WAIT_TO_CONFIRM.code);
                                orderDetailRepository.save(orderDetailResult);
                            }
                        } catch (Exception e) {
                            logger.info("======CREATE ORDER FILE HOLDING EXCEPTION======" + orderDetail.getSvcOrderDetailCode(), e);
                        }
                    }
                }
            }
            //Không đủ tạm giữ => tạo với lý do không đủ số dư => chờ duyệt
            else {
                SvcOrder order = createOrder(createOrderFile, BigDecimal.valueOf(appUser.getId()), v2Package, OrderStatusEnum.WAIT_TO_CONFIRM.code, shopProfileId);
                SvcOrderDetail orderDetail = createOrderDetail(createOrderFile, order, v2Package, appUser);
                if (orderDetail != null) {
                    //Insert log
                    orderLogService.insertOrderLog(orderDetail, OrderStatusEnum.NOTE_HOLD_FAILED.message, OrderStatusEnum.WAIT_TO_CONFIRM.code);
                    SvcOrder orderResult = orderRepository.save(order);
                    SvcOrderDetail orderDetailResult = orderDetailRepository.save(orderDetail);
                    logger.info("=====CREATE ORDER FILE SAVE DB=======" + gson.toJson(orderResult)
                            + "|| ORDER DETAIL " + gson.toJson(orderDetailResult));
                }
            }
        } else {
            //PUSH BACK TO QUEUE
            logger.info("========BALANCE AND FEE NULL ====== PUSHING BACK TO QUEUE =======");
            CallRedis.pushQueue("PUSH_FILE_ORDER", data, 4);
        }
    }

    private SvcOrder createOrder(CreateOrderFile createOrderFile, BigDecimal appUserId, V2Package v2Package,
                                 int status, BigDecimal shopProfileId) {
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
        order.setShopIdReference(shopProfileId);
        //TODO SERVICE PACK ID => TYPE
        order.setServicePackId(v2Package.getId());
        order.setType(v2Package.getPriceSettingType());
        //No setting service pack(wrong for order)
        order.setServicePackSettingId(BigDecimal.ZERO);
        order.setShopAddressId(createOrderFile.getPickupAddressID());
        order.setStatus(status);
//        if (createOrder.getPickupType() == 0)
//            order.setStatus(OrderStatusEnum.WAIT_TO_BRING.code);
        order.setShopOrderId(String.valueOf(orderId));
        order.setPickupType(createOrderFile.getPickupType());
        //TODO check these values
        order.setTotalAddressDilivery(1L);
        order.setTotalDistance(0L);
        //Total details
        order.setTotalOrderDetail(1);
        order.setExpectShipId(0L);
        order.setPaymentType(createOrderFile.getPaymentType());
//        SvcOrder result = orderRepository.save(order);
//        logger.info("======SVC ORDER SAVED TO DB========" + gson.toJson(result));
        return order;
    }

    private SvcOrderDetail createOrderDetail(CreateOrderFile createOrderFile, SvcOrder order,
                                             V2Package v2Package, AppUser appUser) {
//        CalculateFeeOrderData feeOrderData = calculateFeeOrderResponse.getData();
        //Tao diem nhan hang
        AddressDelivery addressDelivery = null;
        try {
            UserInfoAddressData addressData = PreLoadStaticUtil.addresses.get(createOrderFile.getReceiverWardCode());
            AddressDeliveryRequest addressDeliveryRequest = new AddressDeliveryRequest();
            addressDeliveryRequest.setProvinceCode(addressData.getProvinceCode());
            addressDeliveryRequest.setProvinceName(addressData.getProvinceName());
            addressDeliveryRequest.setDistrictCode(addressData.getDistrictCode());
            addressDeliveryRequest.setDistrictName(addressData.getDistrictName());
            addressDeliveryRequest.setWardCode(addressData.getWardCode());
            addressDeliveryRequest.setWardName(addressData.getWardName());
            addressDeliveryRequest.setAddress(createOrderFile.getReceiverAddress());
            BaseResponse deliveryResponse = addressDeliveryService.createAddressDelivery(addressDeliveryRequest);
            if (deliveryResponse != null && deliveryResponse.getStatus() == 200) {
                addressDelivery = (AddressDelivery) deliveryResponse.getData();
            }
        } catch (Exception e) {
            logger.info("=======CREATE ORDER ADDRESS DELIVERY EXCEPTION======" + gson.toJson(createOrderFile), e);
            //TODO handle retry push back to queue
        }
        if (addressDelivery == null){
//            CallRedis.pushQueue("PUSH_FILE_ORDER", gson.toJson(createOrderFile), 4);
            logger.info("=====CREATE ORDER FILE ADDRESS FAILED=====" + gson.toJson(createOrderFile));

        } else {
            SvcOrderDetail orderDetail = new SvcOrderDetail();
            orderDetail.setSvcOrderDetailCode(order.getOrderCode().multiply(BigDecimal.valueOf(10)).add(BigDecimal.ONE));
            orderDetail.setChannel("WEB");
            orderDetail.setShopId(order.getShopId().longValue());
            orderDetail.setSvcOrderId(order.getOrderCode());
            orderDetail.setAddressDeliveryId(addressDelivery.getId());
            orderDetail.setServicePackId(v2Package.getId().intValue());
            orderDetail.setServicePackSettingId(createOrderFile.getFees().get(0).getPriceSettingId());
            //receiver
            orderDetail.setConsignee(createOrderFile.getReceiverName());
            orderDetail.setPhone(createOrderFile.getReceiverPhone());
            //Thong so
            orderDetail.setWeight(createOrderFile.getWeight());
            orderDetail.setLength(createOrderFile.getLength());
            orderDetail.setWidth(createOrderFile.getWidth());
            orderDetail.setHeight(createOrderFile.getHeight());
            //Others
            orderDetail.setIsPartDelivery(createOrderFile.getPartialDelivery());
            orderDetail.setIsRefund(createOrderFile.getIsReturn());
            orderDetail.setIsPorter(createOrderFile.getIsPorter());
            orderDetail.setIsDoorDelivery(createOrderFile.getIsDoorDeliver());
            orderDetail.setPickType(order.getPickupType());
            orderDetail.setPaymentType(order.getPaymentType());
            //Shop order id
            orderDetail.setShopOrderId(createOrderFile.getShopOrderId());
            //TODO check declare product
            orderDetail.setRequiredNote(createOrderFile.getRequireNote());
            orderDetail.setNote(createOrderFile.getNote());
            orderDetail.setIsFree(createOrderFile.getOrderPayment());
            //Status
            orderDetail.setStatus(order.getStatus());
            orderDetail.setOldStatus(order.getStatus());
            orderDetail.setShopAddressId(order.getShopAddressId());
            try {
                createSellingFee(orderDetail, createOrderFile.getFees().get(0));
                List<DetailProduct> products = productService.createDetailProduct(createOrderFile, orderDetail);
                BigDecimal realityCod = BigDecimal.ZERO;
                for (DetailProduct product : products) {
                    realityCod = realityCod.add(product.getCod());
                }
                orderDetail.setRealityCod(realityCod);
                orderDetail.setExpectCod(realityCod);
                return orderDetail;
            } catch (Exception e) {
                logger.info("=======CREATE SELLING FEE EXCEPTION=======" + appUser.getPhone() + "||" + gson.toJson(createOrderFile), e);
            }
        }
        return null;
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
    private List<DetailSellingFee> createSellingFee(SvcOrderDetail orderDetail, CreateOrderFileFee
            createOrderFileFee) throws Exception {
        detailSellingFeeRepository.deleteSellingFeeByDetailCode(orderDetail.getSvcOrderDetailCode());
        List<DetailSellingFee> detailSellingFees = new ArrayList<>();
        //Transport
        if (createOrderFileFee.getFeeDetail().getTransportFee().compareTo(BigDecimal.ZERO) > 0) {
            DetailSellingFee transportFee = new DetailSellingFee(
                    SellingFeeEnum.TRANSPORT_FEE.code,
                    SellingFeeEnum.TRANSPORT_FEE.message,
                    orderDetail.getSvcOrderDetailCode(),
                    createOrderFileFee.getFeeDetail().getTransportFee());
            detailSellingFees.add(transportFee);
        }

        //Pickup
        if (createOrderFileFee.getFeeDetail().getPickupFee().compareTo(BigDecimal.ZERO) > 0) {
            DetailSellingFee pickupFee = new DetailSellingFee(SellingFeeEnum.PICKUP_FEE.code,
                    SellingFeeEnum.PICKUP_FEE.message,
                    orderDetail.getSvcOrderDetailCode(),
                    createOrderFileFee.getFeeDetail().getPickupFee());
            detailSellingFees.add(pickupFee);
        }
        //Porter
        if (createOrderFileFee.getFeeDetail().getPorterFee().compareTo(BigDecimal.ZERO) > 0) {
            DetailSellingFee porterFee = new DetailSellingFee(SellingFeeEnum.PORTER_FEE.code,
                    SellingFeeEnum.PORTER_FEE.message,
                    orderDetail.getSvcOrderDetailCode(),
                    createOrderFileFee.getFeeDetail().getPorterFee());
            detailSellingFees.add(porterFee);
        }
        ///Partial
        if (createOrderFileFee.getFeeDetail().getPartialFee().compareTo(BigDecimal.ZERO) > 0) {
            DetailSellingFee partialFee = new DetailSellingFee(SellingFeeEnum.PARTIAL_FEE.code,
                    SellingFeeEnum.PARTIAL_FEE.message,
                    orderDetail.getSvcOrderDetailCode(),
                    createOrderFileFee.getFeeDetail().getPartialFee());
            detailSellingFees.add(partialFee);
        }
        //Handover
        if (createOrderFileFee.getFeeDetail().getHandoverFee().compareTo(BigDecimal.ZERO) > 0) {
            DetailSellingFee handoverFee = new DetailSellingFee(SellingFeeEnum.HANDOVER_FEE.code,
                    SellingFeeEnum.HANDOVER_FEE.message,
                    orderDetail.getSvcOrderDetailCode(),
                    createOrderFileFee.getFeeDetail().getHandoverFee());
            detailSellingFees.add(handoverFee);
        }
        //Insurance
        if (createOrderFileFee.getFeeDetail().getInsuranceFee().compareTo(BigDecimal.ZERO) > 0) {
            DetailSellingFee insuranceFee = new DetailSellingFee(SellingFeeEnum.INSURANCE_FEE.code,
                    SellingFeeEnum.INSURANCE_FEE.message,
                    orderDetail.getSvcOrderDetailCode(),
                    createOrderFileFee.getFeeDetail().getInsuranceFee());
            detailSellingFees.add(insuranceFee);
        }
        //COD fee
        if (createOrderFileFee.getFeeDetail().getCodFee().compareTo(BigDecimal.ZERO) > 0) {
            DetailSellingFee codFee = new DetailSellingFee(SellingFeeEnum.COD_FEE.code,
                    SellingFeeEnum.COD_FEE.message,
                    orderDetail.getSvcOrderDetailCode(),
                    createOrderFileFee.getFeeDetail().getCodFee());
            detailSellingFees.add(codFee);
        }
        //OtherFee
        if (createOrderFileFee.getFeeDetail().getOtherFee().compareTo(BigDecimal.ZERO) > 0) {
            DetailSellingFee otherFee = new DetailSellingFee(SellingFeeEnum.OTHER_FEE.code,
                    SellingFeeEnum.OTHER_FEE.message,
                    orderDetail.getSvcOrderDetailCode(),
                    createOrderFileFee.getFeeDetail().getOtherFee());
            detailSellingFees.add(otherFee);
        }
        logger.info("======DETAIL FEE SAVE TO DB=======" + gson.toJson(detailSellingFees));
        detailSellingFeeRepository.saveAll(detailSellingFees);
        return detailSellingFees;
    }

}
