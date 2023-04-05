package com.imedia.service.postage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.config.application.AppConfig;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.dao.OrderDAO;
import com.imedia.oracle.entity.*;
import com.imedia.oracle.reportrepository.CarrierReportRepository;
import com.imedia.oracle.repository.ShopAddressRepository;
import com.imedia.oracle.repository.SvcOrderDetailRepository;
import com.imedia.oracle.repository.V2PackageRepository;
import com.imedia.service.order.enums.OrderStatusEnum;
import com.imedia.service.order.enums.SellingFeeEnum;
import com.imedia.service.order.model.*;
import com.imedia.service.pickupaddress.model.LocationData;
import com.imedia.service.postage.model.*;
import com.imedia.service.user.model.UserInfoAddressData;
import com.imedia.util.AppUtil;
import com.imedia.util.CallRedis;
import com.imedia.util.CallServer;
import com.imedia.util.PreLoadStaticUtil;
import ma.glasnost.orika.MapperFacade;
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
public class PostageService {
    static final Logger logger = LogManager.getLogger(PostageService.class);
    static final Gson gson = new GsonBuilder().serializeNulls().create();
    static final Gson gson2 = new Gson();
    private static final HashMap<Integer, ErrorCodesWebshop> errorCodes = PreLoadStaticUtil.errorCodeWeb;
    private final OrderDAO orderDAO;
    private final MapperFacade mapperFacade;
    private final ShopAddressRepository shopAddressRepository;
    private final V2PackageRepository packageRepository;
    private final SvcOrderDetailRepository orderDetailRepository;
    private final CarrierReportRepository carrierReportRepository;

    @Autowired
    public PostageService(OrderDAO orderDAO, MapperFacade mapperFacade, ShopAddressRepository shopAddressRepository, V2PackageRepository packageRepository, SvcOrderDetailRepository orderDetailRepository, CarrierReportRepository carrierReportRepository) {
        this.orderDAO = orderDAO;
        this.mapperFacade = mapperFacade;
        this.shopAddressRepository = shopAddressRepository;
        this.packageRepository = packageRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.carrierReportRepository = carrierReportRepository;
    }


    public BaseResponse getPostage() {
        HashMap<String, Object> data = new HashMap<>();
        try {
            List<PostageData> spPostageData = orderDAO.getSpPostage();
            List<PostageData> kmPostage = orderDAO.getKmPostage();
            List<PostageData> kmPostageNumber = kmPostage.stream()
                    .filter(AppUtil.distinctByKey(PostageData::getCode)).collect(Collectors.toList());
            //Build response
            List<PostageDataResponse> kmResponses = new ArrayList<>();
            List<PostageDataResponse> spResponses = mapperFacade.mapAsList(spPostageData, PostageDataResponse.class);
            for (PostageData km : kmPostageNumber) {
                PostageDataResponse dataResponse = mapperFacade.map(km, PostageDataResponse.class);
                for (PostageData postageData : kmPostage) {
                    if (postageData.getCode().equals(dataResponse.getCode())) {
                        if (postageData.getBaseType() == 3)
                            dataResponse.setMaxDeliveryPoint(postageData.getMax());
                        if (postageData.getBaseType() == 4)
                            dataResponse.setMaxOrderPerDeliveryPoint(postageData.getMax());
                    }
                }
                kmResponses.add(dataResponse);
            }
            data.put("packageKm", kmResponses);
            data.put("packageSp", spResponses);
            CallRedis.setCache("PACKAGE", gson.toJson(data));
            return new BaseResponse(200, data);
        } catch (Exception e) {
            logger.info("======GET POSTAGE EXCEPTION======", e);
            return new BaseResponse(500);
        }
    }

    //for FE
    public BaseResponse calculateTotalFee(CreateOrder createOrder, AppUser appUser) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        CalculateFeeRequest calculateFeeRequest = buildCalculateFeeRequest(createOrder, appUser);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Token", appUser.getAccessToken());
        headers.put("Content-Type", "application/json");
        String response = null;
        V2Package v2Package = packageRepository.findByCode(createOrder.getPackCode());
        try {
            response = CallServer.getInstance().postWithHeaders(appConfig.calculateFeeUrl, headers, gson2.toJson(calculateFeeRequest));
        } catch (Exception e) {
            logger.info("=======CALCULATE FEE EXCEPTION======" + gson2.toJson(createOrder), e);
            return new BaseResponse(500);
        }
        if (response != null) {
            CalculateFeeOrderResponse calculateFeeOrderResponse = gson.fromJson(response, CalculateFeeOrderResponse.class);
            if (calculateFeeOrderResponse.getCode() == 0 && calculateFeeOrderResponse.getData().getDeliveryPoint() != null) {
                //Check gói cước đồng giá có hợp lệ với cấu hình Giao 1 phần hay Người nhận trả phí không
                if (v2Package.getPriceSettingType() == 1) {
                    int isFree = createOrder.getDeliveryPoint().get(0).getReceivers().get(0).getIsFree();
                    int isPartial = createOrder.getDeliveryPoint().get(0).getReceivers().get(0).getPartialDelivery();
                    int priceSettingId = calculateFeeOrderResponse.getData().getDeliveryPoint()
                            .get(0).getReceivers().get(0).getPriceSettingId();
                    int paymentType = createOrder.getPaymentType();
                    if ((isFree == 2 || isPartial == 1 || paymentType == 1)) {
                        List<ForWardConfigOM> configIMS = orderDAO.getTargetConfig(v2Package.getCode(), priceSettingId, isPartial, isFree, paymentType);
                        if (configIMS.size() == 0) {
                            return new BaseResponse(610, handleFeeReasonCode(isPartial, isFree, paymentType), handleFeeReasonCode(isPartial, isFree, paymentType));
                        }
                    }
                }
                CalculateFeeResponseAPI responseAPI = buildTotalFee(calculateFeeOrderResponse);
                BigDecimal totalCod = BigDecimal.ZERO;
                BigDecimal totalProductValue = BigDecimal.ZERO;
                //Get totalCOd
                for (CreateOrderDetail createOrderDetail : createOrder.getDeliveryPoint())
                    for (CreateOrderReceiver receiver : createOrderDetail.getReceivers())
                        for (OrderDetailProduct item : receiver.getItems()) {
                            totalCod = totalCod.add(item.getCod().multiply(item.getQuantity()));
                            totalProductValue = totalProductValue.add(item.getProductValue().multiply(item.getQuantity()));
                        }
                responseAPI.setTotalMoneyCollect(responseAPI.getTotalMoneyCollect().add(totalCod));
                responseAPI.setTotalCod(totalCod);
                responseAPI.setTotalProductValue(totalProductValue);
                return new BaseResponse(200, responseAPI);
            }
            return new BaseResponse(2000 + calculateFeeOrderResponse.getCode());

        }
        return new BaseResponse(500);
    }

    private CalculateFeeRequest buildCalculateFeeRequest(CreateOrder createOrder, AppUser appUser) {
        CalculateFeeRequest calculateFeeRequest = mapperFacade.map(createOrder, CalculateFeeRequest.class);
        //Delivery address
        for (CreateOrderDetail createOrderDetail : calculateFeeRequest.getDeliveryPoint()) {
            UserInfoAddressData addressData = PreLoadStaticUtil.addresses.get(String.valueOf(createOrderDetail.getWard()));
            String address = createOrderDetail.getAddress() + "," + addressData.getWardName()
                    + "," + addressData.getDistrictName() + "," + addressData.getProvinceName();
            createOrderDetail.setAddress(address);
        }
        ShopAddress shopAddress = shopAddressRepository.findShopAddressByIdAndShopId(Long.valueOf(createOrder.getPickupAddressId()), appUser.getId());
        calculateFeeRequest.setPickProvince(shopAddress.getProvinceCode());
        calculateFeeRequest.setPickName(shopAddress.getSenderName());
        calculateFeeRequest.setPickPhone(shopAddress.getPhone());
        calculateFeeRequest.setPickDistrict(String.valueOf(shopAddress.getDistrictId()));
        calculateFeeRequest.setPickWard(String.valueOf(shopAddress.getWardId()));
        //build address string for google
        UserInfoAddressData addressData = PreLoadStaticUtil.addresses.get(String.valueOf(shopAddress.getWardId()));
        String address = shopAddress.getAddress();
        if (addressData != null)
            address = address + "," + addressData.getWardName() + "," + addressData.getDistrictName() + "," + addressData.getProvinceName();
        calculateFeeRequest.setPickAddress(address);
        return calculateFeeRequest;
    }

    public CalculateFeeResponseAPI buildTotalFee(CalculateFeeOrderResponse calculateFeeOrderResponse) {
        CalculateFeeResponseAPI responseAPI = new CalculateFeeResponseAPI();
        CalculateFeeResponseDetailAPI detailAPI = new CalculateFeeResponseDetailAPI();

        for (CalculateFeeDeliveryPoint deliveryPoint : calculateFeeOrderResponse.getData().getDeliveryPoint()) {
            for (CalculateFeeReceivers receivers : deliveryPoint.getReceivers()) {
                BigDecimal totalFee = receivers.getTransportFee().add(receivers.getCodFee().add(receivers.getPickupFee()))
                        .add(receivers.getHandoverFee().add(receivers.getInsuranceFee().add(receivers.getPartialFee()
                                .add(receivers.getPorterFee()).add(receivers.getOtherFee()))));
                //Total
                if (receivers.getIsFree() == 2)
                    responseAPI.setTotalMoneyCollect(responseAPI.getTotalMoneyCollect().add(totalFee));
                responseAPI.setTotalFee(responseAPI.getTotalFee().add(totalFee));
                detailAPI.setTransportFee(detailAPI.getTransportFee().add(receivers.getTransportFee()));
                detailAPI.setPickupFee(detailAPI.getPickupFee().add(receivers.getPickupFee()));
                detailAPI.setPorterFee(detailAPI.getPorterFee().add(receivers.getPorterFee()));
                detailAPI.setPartialFee(detailAPI.getPartialFee().add(receivers.getPartialFee()));
                detailAPI.setHandoverFee(detailAPI.getHandoverFee().add(receivers.getHandoverFee()));
                detailAPI.setInsuranceFee(detailAPI.getInsuranceFee().add(receivers.getInsuranceFee()));
                detailAPI.setCodFee(detailAPI.getCodFee().add(receivers.getCodFee()));
                detailAPI.setOtherFee(detailAPI.getOtherFee().add(receivers.getOtherFee()));
            }
        }
        responseAPI.setFees(detailAPI);
        //Build mobile model
        List<FilterOrderResponseFee> fees = new ArrayList<>();
        if (detailAPI.getTransportFee().compareTo(BigDecimal.ZERO) > 0)
            fees.add(new FilterOrderResponseFee(SellingFeeEnum.TRANSPORT_FEE.message,
                    SellingFeeEnum.TRANSPORT_FEE.code, detailAPI.getTransportFee()));
        if (detailAPI.getInsuranceFee().compareTo(BigDecimal.ZERO) > 0)
            fees.add(new FilterOrderResponseFee(SellingFeeEnum.INSURANCE_FEE.message,
                    SellingFeeEnum.INSURANCE_FEE.code, detailAPI.getInsuranceFee()));
        if (detailAPI.getCodFee().compareTo(BigDecimal.ZERO) > 0)
            fees.add(new FilterOrderResponseFee(SellingFeeEnum.COD_FEE.message,
                    SellingFeeEnum.COD_FEE.code, detailAPI.getCodFee()));
        if (detailAPI.getPickupFee().compareTo(BigDecimal.ZERO) > 0)
            fees.add(new FilterOrderResponseFee(SellingFeeEnum.PICKUP_FEE.message,
                    SellingFeeEnum.PICKUP_FEE.code, detailAPI.getPickupFee()));
        if (detailAPI.getPartialFee().compareTo(BigDecimal.ZERO) > 0)
            fees.add(new FilterOrderResponseFee(SellingFeeEnum.PARTIAL_FEE.message,
                    SellingFeeEnum.PARTIAL_FEE.code, detailAPI.getPartialFee()));
        if (detailAPI.getPorterFee().compareTo(BigDecimal.ZERO) > 0)
            fees.add(new FilterOrderResponseFee(SellingFeeEnum.PORTER_FEE.message,
                    SellingFeeEnum.PORTER_FEE.code, detailAPI.getPorterFee()));
        if (detailAPI.getHandoverFee().compareTo(BigDecimal.ZERO) > 0)
            fees.add(new FilterOrderResponseFee(SellingFeeEnum.HANDOVER_FEE.message,
                    SellingFeeEnum.HANDOVER_FEE.code, detailAPI.getHandoverFee()));
        if (detailAPI.getOtherFee().compareTo(BigDecimal.ZERO) > 0)
            fees.add(new FilterOrderResponseFee(SellingFeeEnum.OTHER_FEE.message,
                    SellingFeeEnum.OTHER_FEE.code, detailAPI.getOtherFee()));
        responseAPI.setFeeDetails(fees);
        return responseAPI;
    }
//
//    public CalculateFeeResponseAPI buildTotalFee(CalculateFeeOrderResponse calculateFeeOrderResponse, BigDecimal totalCod) {
//        CalculateFeeResponseAPI responseAPI = new CalculateFeeResponseAPI();
//        CalculateFeeResponseDetailAPI detailAPI = new CalculateFeeResponseDetailAPI();
//        for (CalculateFeeDeliveryPoint deliveryPoint : calculateFeeOrderResponse.getData().getDeliveryPoint()) {
//            for (CalculateFeeReceivers receivers : deliveryPoint.getReceivers()) {
//                BigDecimal totalFee = receivers.getTransportFee().add(receivers.getCodFee().add(receivers.getPickupFee()))
//                        .add(receivers.getHandoverFee().add(receivers.getInsuranceFee().add(receivers.getPartialFee()
//                                .add(receivers.getPorterFee()).add(receivers.getOtherFee()))));
//                //Total
//                responseAPI.setTotalFee(responseAPI.getTotalFee().add(totalFee));
//                //Người nhận trả phí
//                if (receivers.getIsFree() == 2)
//                    responseAPI.setTotalFeeToCollect(responseAPI.getTotalFeeToCollect());
//                detailAPI.setTransportFee(detailAPI.getTransportFee().add(receivers.getTransportFee()));
//                detailAPI.setPickupFee(detailAPI.getPickupFee().add(receivers.getPickupFee()));
//                detailAPI.setPorterFee(detailAPI.getPorterFee().add(receivers.getPorterFee()));
//                detailAPI.setPartialFee(detailAPI.getPartialFee().add(receivers.getPartialFee()));
//                detailAPI.setHandoverFee(detailAPI.getHandoverFee().add(receivers.getHandoverFee()));
//                detailAPI.setInsuranceFee(detailAPI.getInsuranceFee().add(receivers.getInsuranceFee()));
//                detailAPI.setCodFee(detailAPI.getCodFee().add(receivers.getCodFee()));
//                detailAPI.setOtherFee(detailAPI.getOtherFee().add(receivers.getOtherFee()));
//            }
//        }
//        responseAPI.setFees(detailAPI);
//        return responseAPI;
//    }

    public CalculateFeeOrderResponse calculateFeeForFile(CreateOrder createOrder, ShopAddress shopAddress, AppUser appUser) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        CalculateFeeRequest calculateFeeRequest = mapperFacade.map(createOrder, CalculateFeeRequest.class);
        calculateFeeRequest.setPickAddress(shopAddress.getAddress());
        calculateFeeRequest.setPickName(shopAddress.getSenderName());
        calculateFeeRequest.setPickPhone(shopAddress.getPhone());
        calculateFeeRequest.setPickProvince(shopAddress.getProvinceCode());
        calculateFeeRequest.setPickDistrict(String.valueOf(shopAddress.getDistrictId()));
        calculateFeeRequest.setPickWard(String.valueOf(shopAddress.getWardId()));
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Token", appUser.getAccessToken());
        headers.put("Content-Type", "application/json");
        String response = null;
        try {
            response = CallServer.getInstance().postWithHeaders(appConfig.calculateFeeUrl, headers, gson2.toJson(calculateFeeRequest));
        } catch (Exception e) {
            logger.info("=======CALCULATE FEE EXCEPTION======" + gson2.toJson(calculateFeeRequest), e);
            return null;
        }
        if (response != null) {
            CalculateFeeOrderResponse calculateFeeOrderResponse = gson2.fromJson(response, CalculateFeeOrderResponse.class);
            return calculateFeeOrderResponse;
        }
        return null;
    }

    //TODO delete test before
    public BaseResponse calculateFee(CreateOrder createOrder, AppUser appUser) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        CalculateFeeRequest calculateFeeRequest = buildCalculateFeeRequest(createOrder, appUser);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Token", appUser.getAccessToken());
        headers.put("Content-Type", "application/json");
        String response = null;
        V2Package v2Package = packageRepository.findByCode(createOrder.getPackCode());
        try {
            response = CallServer.getInstance().postWithHeaders(appConfig.calculateFeeUrl, headers, gson2.toJson(calculateFeeRequest));
        } catch (Exception e) {
            logger.info("=======CALCULATE FEE EXCEPTION======" + gson2.toJson(createOrder), e);
            return new BaseResponse(500);
        }
        if (response != null) {
            CalculateFeeOrderResponse calculateFeeOrderResponse = gson2.fromJson(response, CalculateFeeOrderResponse.class);
            if (calculateFeeOrderResponse.getCode() == 0 && calculateFeeOrderResponse.getData().getDeliveryPoint() != null) {
//                CalculateFeeResponseAPI responseAPI = buildTotalFee(calculateFeeOrderResponse);
                return new BaseResponse(200, calculateFeeOrderResponse);
            } else if (calculateFeeOrderResponse.getCode() == 1) {
                return new BaseResponse(2001);
            }

        }
        return new BaseResponse(2105);
    }

    public BaseResponse calculateFeeOrderFile(List<CreateOrderFile> createOrderFiles, AppUser appUser) throws Exception {
        ShopAddress shopAddress = shopAddressRepository
                .findShopAddressByIdAndShopId(Long.valueOf(createOrderFiles.get(0).getPickupAddressID()), appUser.getId());
        List<V2Package> samePricePackages = packageRepository.findAllByStatusAndPriceSettingType(BigDecimal.ONE, 1);
        List<CreateOrderFile> createOrderFileResponses = new ArrayList<>();
        for (CreateOrderFile createOrderFile : createOrderFiles) {
            //validate

            int isFree = createOrderFile.getOrderPayment();
            int isPartial = createOrderFile.getPartialDelivery();
            int paymentType = 2;
            if (createOrderFile.getPaymentType() != null)
                paymentType = createOrderFile.getPaymentType();
            if (createOrderFile.getPackageCode().equals("ALL")) {
                List<CreateOrderFileFee> fees = new ArrayList<>();
                for (V2Package samePricePackage : samePricePackages) {
                    CreateOrder createOrder =
                            buildRequestCreateOrderFile(createOrderFile, samePricePackage.getCode());
                    //Get fees
                    CalculateFeeOrderResponse feeResponse = calculateFeeForFile(createOrder, shopAddress, appUser);
                    //build response
                    if (feeResponse != null && feeResponse.getCode() == 0 && feeResponse.getData() != null) {
                        CalculateFeeReceivers receivers = feeResponse.getData().getDeliveryPoint().get(0)
                                .getReceivers().get(0);
                        int priceSettingId = receivers.getPriceSettingId();
                        List<ForWardConfigOM> configOMS = orderDAO.getTargetConfig(createOrder.getPackCode(), priceSettingId, isPartial, isFree, paymentType);
                        if (configOMS.size() > 0) {
                            CreateOrderFileFee createOrderFileFee = new CreateOrderFileFee();
                            CalculateFeeResponseDetailAPI createOrderFileFeeDetail
                                    = mapperFacade.map(feeResponse.getData().getDeliveryPoint().get(0)
                                    .getReceivers().get(0), CalculateFeeResponseDetailAPI.class);
                            createOrderFileFee.setPackageCode(samePricePackage.getCode());
                            createOrderFileFee.setPackageName(samePricePackage.getName());
                            createOrderFileFee.setTotalFee(feeResponse.getData().getDeliveryPoint().get(0)
                                    .getReceivers().get(0).getTotalFee());
                            createOrderFileFee.setPriceSettingId(receivers.getPriceSettingId());
                            createOrderFileFee.setFeeDetail(createOrderFileFeeDetail);
                            fees.add(createOrderFileFee);
                        }
                    }
                }
//                if (fees.size() == 0)
//                    createOrderFile.setFeeReason(handleFeeReasonCode(isPartial, isFree, paymentType));
                createOrderFile.setFees(fees);
                if (fees.size() == 0)
                    createOrderFile.setFeeReason(handleFeeReasonCode(isPartial, isFree, paymentType));
            } else {
                V2Package v2Package = packageRepository.findByCode(createOrderFile.getPackageCode());
                CreateOrder createOrder =
                        buildRequestCreateOrderFile(createOrderFile, createOrderFile.getPackageCode());
                CalculateFeeOrderResponse feeResponse = calculateFeeForFile(createOrder, shopAddress, appUser);
                if (feeResponse != null && feeResponse.getCode() == 0 && feeResponse.getData() != null) {
                    CalculateFeeReceivers receivers = feeResponse.getData().getDeliveryPoint().get(0)
                            .getReceivers().get(0);
//                    int isFree = receivers.getIsFree();
//                    int isPartial = receivers.getPartialDelivery();
//                    int paymentType = createOrder.getPaymentType();
                    int priceSettingId = receivers.getPriceSettingId();
                    List<ForWardConfigOM> configOMS = orderDAO.getTargetConfig(createOrder.getPackCode(), priceSettingId, isPartial, isFree, paymentType);
                    if (configOMS.size() > 0) {
                        CreateOrderFileFee createOrderFileFee = new CreateOrderFileFee();
                        CalculateFeeResponseDetailAPI createOrderFileFeeDetail
                                = mapperFacade.map(feeResponse.getData().getDeliveryPoint().get(0)
                                .getReceivers().get(0), CalculateFeeResponseDetailAPI.class);
                        createOrderFileFee.setPackageCode(createOrderFile.getPackageCode());
                        createOrderFileFee.setPackageName(v2Package.getName());
                        createOrderFileFee.setPriceSettingId(receivers.getPriceSettingId());
                        createOrderFileFee.setFeeDetail(createOrderFileFeeDetail);
                        createOrderFileFee.setTotalFee(feeResponse.getData().getDeliveryPoint().get(0)
                                .getReceivers().get(0).getTotalFee());
                        createOrderFile.setFees(Collections.singletonList(createOrderFileFee));
                    } else createOrderFile.setFeeReason(handleFeeReasonCode(isPartial, isFree, paymentType));
                } else
                    createOrderFile.setFeeReason("Không có tuyến giao");
                if (v2Package != null)
                    createOrderFile.setPackName(v2Package.getName());
            }
            createOrderFileResponses.add(createOrderFile);
        }
        return new BaseResponse(200, createOrderFileResponses);
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
        if (isFree == 2 || isPartial == 1 || paymentType == 1)
            return note.substring(0, note.length() - 1);
        else
            return "Không có tuyến giao";
    }

    public CreateOrder buildRequestCreateOrderFile(CreateOrderFile createOrderFile, String packCode) {
        CreateOrder createOrder = new CreateOrder();
        try {
            createOrder.setPackCode(packCode);
            createOrder.setPickupAddressId(createOrderFile.getPickupAddressID());
            createOrder.setPickupType(createOrderFile.getPickupType());
            createOrder.setPaymentType(createOrderFile.getPaymentType());
            //Order detail
            CreateOrderDetail createOrderDetail = new CreateOrderDetail();
            //Receiver Address
            createOrderDetail.setAddress(createOrderFile.getReceiverAddress());
            createOrderDetail.setProvince(createOrderFile.getReceiverProvinceCode());
            createOrderDetail.setDistrict(createOrderFile.getReceiverDistrictCode());
            createOrderDetail.setWard(createOrderFile.getReceiverWardCode());
            //Receiver
            CreateOrderReceiver createOrderReceiver = new CreateOrderReceiver();
            createOrderReceiver.setName(createOrderFile.getReceiverName());
            createOrderReceiver.setPhone(createOrderFile.getReceiverPhone());
            //Stats
            createOrderReceiver.setLength(createOrderFile.getLength());
            createOrderReceiver.setWidth(createOrderFile.getWidth());
            createOrderReceiver.setHeight(createOrderFile.getHeight());
            createOrderReceiver.setWeight(createOrderFile.getWeight());
            //Other
            createOrderReceiver.setRequireNote(createOrderFile.getRequireNote());
            createOrderReceiver.setPartialDelivery(createOrderFile.getPartialDelivery());
            createOrderReceiver.setIsFree(createOrderFile.getOrderPayment());
            //orderReceiver.setConfirmType(createOrderFile.getRequireNote());
            createOrderReceiver.setIsRefund(createOrderFile.getIsReturn());
            //ExtraServices
            createOrderReceiver.setExtraServices(Collections.singletonList(
                    new ExtraService(createOrderFile.getIsDoorDeliver(), createOrderFile.getIsPorter())));
            //Order products
            List<OrderDetailProduct> orderDetailProducts =
                    mapperFacade.mapAsList(createOrderFile.getProducts(), OrderDetailProduct.class);
            createOrderReceiver.setItems(orderDetailProducts);
            //Set to request
            createOrderDetail.setReceivers(Collections.singletonList(createOrderReceiver));
            createOrder.setDeliveryPoint(Collections.singletonList(createOrderDetail));
            return createOrder;
        } catch (Exception e) {
            logger.info("=======buildRequestCreateOrderFile=======", e);
            return null;
        }
    }

    public BaseResponse filterPackagesForOrderFile(List<CreateOrderFile> createOrderFiles) throws Exception {
        List<V2Package> samePricePackages = packageRepository.findAllByStatusAndPriceSettingType(BigDecimal.ONE, 1);
        List<CreateOrderFile> createOrderFileResponses = new ArrayList<>();
        for (CreateOrderFile createOrderFile : createOrderFiles) {
            if (createOrderFile.getPackageCode().equals("ALL")) {
                List<CreateOrderFileFee> fees = new ArrayList<>();
                for (V2Package samePricePackage : samePricePackages) {
                    CreateOrderFileFee createOrderFileFee = new CreateOrderFileFee();
                    createOrderFileFee.setPackageCode(samePricePackage.getCode());
                    createOrderFileFee.setPackageName(samePricePackage.getName());
                    fees.add(createOrderFileFee);
                }
                createOrderFile.setFees(fees);
            } else {
//                V2Package v2Package = packageRepository.findByCode(createOrderFile.getPackageCode());
//                if (v2Package == null) {
//                    createOrderFile.setFees(new ArrayList<>());
//                } else {
//                    CreateOrderFileFee createOrderFileFee = new CreateOrderFileFee();
//                    createOrderFileFee.setPackageCode(v2Package.getCode());
//                    createOrderFileFee.setPackageName(v2Package.getName());
//                    createOrderFile.setFees(Collections.singletonList(createOrderFileFee));
//                }
                CreateOrderFileFee createOrderFileFee = new CreateOrderFileFee();
                createOrderFileFee.setPackageCode(createOrderFile.getPackageCode());
                createOrderFileFee.setPackageName("Giả lập");
                createOrderFile.setFees(Collections.singletonList(createOrderFileFee));
                if (createOrderFile.getPackageCode().equals("CHU")) {
                    createOrderFile.setFees(new ArrayList<>());
                }
            }
            createOrderFileResponses.add(createOrderFile);
        }
        return new BaseResponse(200, createOrderFileResponses);
    }

    public CalculateFeeOrderResponse calculateFeeCreateOrderFile(CreateOrderFile createOrderFile, AppUser appUser) {
        ShopAddress shopAddress = shopAddressRepository
                .findShopAddressByIdAndShopId(Long.valueOf(createOrderFile.getPickupAddressID()), appUser.getId());
        CreateOrder createOrder =
                buildRequestCreateOrderFile(createOrderFile, createOrderFile.getPackageCode());
        try {
            return calculateFeeForFile(createOrder, shopAddress, appUser);
        } catch (Exception e) {
            logger.info("=======CALCULATE FEE CREATE ORDER FILE EXCEPTION======" + gson.toJson(createOrderFile), e);
            return null;
        }
    }

    //Chỉ có đơn tạo bằng file 1 người nhận 1 điểm giao mới có trạng thái chờ duyệt
    public CalculateFeeOrderResponse calculateFeeForConfirmOrder(FilterOrderResponse confirmOrderRequest, AppUser appUser) throws Exception {
        CalculateFeeRequest calculateFeeRequest = new CalculateFeeRequest();
        calculateFeeRequest.setPackCode(confirmOrderRequest.getPackCode());
        calculateFeeRequest.setShopOrderCode("");
        calculateFeeRequest.setPickName(confirmOrderRequest.getShopName());
        calculateFeeRequest.setPickPhone(confirmOrderRequest.getShopPhone());
        calculateFeeRequest.setPickAddress(confirmOrderRequest.getShopAddress());
        //Shop address
        calculateFeeRequest.setPickProvince(confirmOrderRequest.getShopProvinceCode());
        calculateFeeRequest.setPickDistrict(String.valueOf(confirmOrderRequest.getShopDistrictCode()));
        calculateFeeRequest.setPickWard(String.valueOf(confirmOrderRequest.getShopWardCode()));
        calculateFeeRequest.setPickupType(confirmOrderRequest.getPickupType());
        //Delivery point
        FilterOrderDetailResponse orderDetailResponse = confirmOrderRequest.getOrderDetails().get(0);
        SvcOrderDetail svcOrderDetail = orderDetailRepository.getValidBySvcOrderDetailCode(orderDetailResponse.getSvcOrderDetailCode());
        CreateOrderDetail createOrderDetail = new CreateOrderDetail();
        createOrderDetail.setAddress(confirmOrderRequest.getOrderDetails().get(0).getDeliveryAddress());
        createOrderDetail.setProvince(confirmOrderRequest.getOrderDetails().get(0).getDeliveryProvinceCode());
        createOrderDetail.setDistrict(confirmOrderRequest.getOrderDetails().get(0).getDeliveryDistrictCode());
        createOrderDetail.setWard(confirmOrderRequest.getOrderDetails().get(0).getDeliveryWardCode());
        //Receiver
        CreateOrderReceiver receiver = new CreateOrderReceiver();
        receiver.setOrderDetailCode(confirmOrderRequest.getOrderDetails().get(0).getSvcOrderDetailCode());
        receiver.setName(orderDetailResponse.getConsignee());
        receiver.setPhone(orderDetailResponse.getPhone());
        //Stats
        receiver.setLength(svcOrderDetail.getLength());
        receiver.setWidth(svcOrderDetail.getWidth());
        receiver.setWeight(svcOrderDetail.getWeight());
        receiver.setHeight(svcOrderDetail.getHeight());

        receiver.setPartialDelivery(svcOrderDetail.getIsPartDelivery());
        receiver.setIsFree(svcOrderDetail.getIsFree());
        receiver.setIsRefund(svcOrderDetail.getIsRefund());
        receiver.setConfirmType(svcOrderDetail.getRequiredNote());
        receiver.setRequireNote(svcOrderDetail.getRequiredNote());
        receiver.setExtraServices(Collections.singletonList(new ExtraService(svcOrderDetail.getIsDoorDelivery(),
                svcOrderDetail.getIsPorter())));
        //products
        List<OrderDetailProduct> detailProducts = new ArrayList<>();
        for (FilterOrderDetailProductResponse product : orderDetailResponse.getProducts()) {
            OrderDetailProduct orderDetailProduct = new OrderDetailProduct();
            orderDetailProduct.setProductName(product.getName());
            orderDetailProduct.setProductValue(product.getValue());
            orderDetailProduct.setQuantity(product.getQuantity());
            orderDetailProduct.setCod(product.getCod());
            detailProducts.add(orderDetailProduct);

        }
        receiver.setItems(detailProducts);
        //Build request
        createOrderDetail.setReceivers(Collections.singletonList(receiver));
        calculateFeeRequest.setDeliveryPoint(Collections.singletonList(createOrderDetail));
        //Request
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Token", appUser.getAccessToken());
        headers.put("Content-Type", "application/json");
        String response = null;
        try {
            response = CallServer.getInstance().postWithHeaders(AppConfig.getInstance().calculateFeeUrl, headers, gson2.toJson(calculateFeeRequest));
        } catch (Exception e) {
            logger.info("=======CALCULATE FEE CONFIRM ORDER EXCEPTION======" + gson2.toJson(calculateFeeRequest), e);
        }
        if (response != null) {
            CalculateFeeOrderResponse calculateFeeOrderResponse = gson2.fromJson(response, CalculateFeeOrderResponse.class);
            return calculateFeeOrderResponse;
        }
        return null;
    }

    public CalculateFeeOrderResponse calculateFeeForReFindShipper(FilterOrderResponse filterOrderResponse, AppUser appUser) {
        CalculateFeeRequest calculateFeeRequest = new CalculateFeeRequest();
        calculateFeeRequest.setPackCode(filterOrderResponse.getPackCode());
        calculateFeeRequest.setShopOrderCode("");
        calculateFeeRequest.setPickName(filterOrderResponse.getShopName());
        calculateFeeRequest.setPickPhone(filterOrderResponse.getShopPhone());
        UserInfoAddressData addressData = PreLoadStaticUtil.addresses.get(String.valueOf(filterOrderResponse.getShopWardCode()));
        String address = filterOrderResponse.getShopAddress();
        if (addressData != null)
            address = address + "," + addressData.getWardName() + "," + addressData.getDistrictName() + "," + addressData.getProvinceName();
        calculateFeeRequest.setPickAddress(address);
        //Shop address
        calculateFeeRequest.setPickProvince(filterOrderResponse.getShopProvinceCode());
        calculateFeeRequest.setPickDistrict(String.valueOf(filterOrderResponse.getShopDistrictCode()));
        calculateFeeRequest.setPickWard(String.valueOf(filterOrderResponse.getShopWardCode()));
        calculateFeeRequest.setPickupType(filterOrderResponse.getPickupType());
        //Delivery point
        //Lọc các order detail để gom thành các điểm giao
        List<Long> distinctDeliveryIds = filterOrderResponse.getOrderDetails()
                .stream().filter(AppUtil.distinctByKey(FilterOrderDetailResponse::getAddressDeliveryId))
                .map(FilterOrderDetailResponse::getAddressDeliveryId)
                .collect(Collectors.toList());
        //Tìm tất cả các detail thuộc 1 điểm giao distinct trên
        List<CreateOrderDetail> createOrderDetails = new ArrayList<>();
        for (Long distinctDeliveryId : distinctDeliveryIds) {
            List<FilterOrderDetailResponse> orderDetailResponses = filterOrderResponse.getOrderDetails()
                    .stream().filter(o -> o.getAddressDeliveryId().equals(distinctDeliveryId))
                    .collect(Collectors.toList());
            //Build deliveryPoint
            CreateOrderDetail createOrderDetail = new CreateOrderDetail();
            FilterOrderDetailResponse addressInfo = orderDetailResponses.get(0);
            String deliveryAddress = addressInfo.getDeliveryAddress();
            UserInfoAddressData deliveryAddressData = PreLoadStaticUtil.addresses.get(String.valueOf(filterOrderResponse.getShopWardCode()));
            if (deliveryAddress != null)
                deliveryAddress = deliveryAddress + "," + deliveryAddressData.getWardName() + ","
                        + deliveryAddressData.getDistrictName() + "," + deliveryAddressData.getProvinceName();
            createOrderDetail.setAddress(deliveryAddress);
            createOrderDetail.setWard(addressInfo.getDeliveryWardCode());
            createOrderDetail.setProvince(addressInfo.getDeliveryProvinceCode());
            createOrderDetail.setDistrict(addressInfo.getDeliveryDistrictCode());
            //Build receivers
            List<CreateOrderReceiver> receivers = new ArrayList<>();
            for (FilterOrderDetailResponse filterOrderDetail : orderDetailResponses) {
//                SvcOrderDetail svcOrderDetail = orderDetailRepository.findBySvcOrderDetailCode(filterOrderDetail.getSvcOrderDetailCode());
                CreateOrderReceiver createOrderReceiver = new CreateOrderReceiver();
                createOrderReceiver.setOrderDetailCode(filterOrderDetail.getSvcOrderDetailCode());
                createOrderReceiver.setName(filterOrderDetail.getConsignee());
                createOrderReceiver.setPhone(filterOrderDetail.getPhone());
                createOrderReceiver.setLength(filterOrderDetail.getLength());
                createOrderReceiver.setWidth(filterOrderDetail.getWidth());
                createOrderReceiver.setHeight(filterOrderDetail.getHeight());
                createOrderReceiver.setWeight(filterOrderDetail.getWeight());
                //Other
                createOrderReceiver.setPartialDelivery(filterOrderDetail.getIsPartDelivery().intValue());
                createOrderReceiver.setIsFree(filterOrderDetail.getIsFree().intValue());
                createOrderReceiver.setConfirmType(1);
                createOrderReceiver.setIsRefund(filterOrderDetail.getIsRefund().intValue());
                //Extra services
                createOrderReceiver.setExtraServices(Collections.singletonList(new ExtraService(filterOrderDetail.getIsDoorDelivery().intValue(),
                        filterOrderDetail.getIsPorter().intValue())));
                //Items
                List<OrderDetailProduct> orderDetailProducts = new ArrayList<>();
                for (FilterOrderDetailProductResponse product : filterOrderDetail.getProducts()) {
                    OrderDetailProduct orderDetailProduct = new OrderDetailProduct();
                    orderDetailProduct.setProductName(product.getName());
                    orderDetailProduct.setCategory(1);
                    orderDetailProduct.setProductValue(product.getValue());
                    orderDetailProduct.setQuantity(product.getQuantity());
                    orderDetailProduct.setCod(product.getCod());
                    orderDetailProducts.add(orderDetailProduct);
                }
                createOrderReceiver.setItems(orderDetailProducts);
                receivers.add(createOrderReceiver);
            }
            //Set receivers
            createOrderDetail.setReceivers(receivers);
            createOrderDetails.add(createOrderDetail);
        }
        //Set delivery point
        calculateFeeRequest.setDeliveryPoint(createOrderDetails);
        //Request
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Token", appUser.getAccessToken());
        headers.put("Content-Type", "application/json");
        String response = null;
        try {
            response = CallServer.getInstance().postWithHeaders(AppConfig.getInstance().calculateFeeUrl, headers, gson.toJson(calculateFeeRequest));
        } catch (Exception e) {
            logger.info("=======CALCULATE FEE FIND SHIPPER EXCEPTION======" + gson.toJson(calculateFeeRequest), e);
        }
        if (response != null) {
            CalculateFeeOrderResponse calculateFeeOrderResponse = gson.fromJson(response, CalculateFeeOrderResponse.class);
            return calculateFeeOrderResponse;
        }
        return null;
    }

    public CalculateSpecificFeeData calculateSpecificFee(BigDecimal orderDetailCode, String packCode, String feeType,
                                                         int count, BigDecimal transportFee, String changeInfoType) {
        CalSpecificFeeRequest request = new CalSpecificFeeRequest();
        request.setFeeType(feeType);
        request.setPackCode(packCode);
        request.setOrderDetailCode(String.valueOf(orderDetailCode));
        request.setCount(count);
        request.setChangedInfoType(changeInfoType);
        request.setTransportFee(transportFee);
        String response = null;
        try {
            response = CallServer.getInstance().post(AppConfig.getInstance().calculateSpecificFeeUrl, gson.toJson(request));
            if (response != null) {
                CalculateSpecificFeeResponse calculateSpecificFeeResponse = gson.fromJson(response, CalculateSpecificFeeResponse.class);
                if (calculateSpecificFeeResponse.getCode() == 0 && calculateSpecificFeeResponse.getData() != null) {
                    CalculateSpecificFeeData data = calculateSpecificFeeResponse.getData();
                    return data;
                }
            }
        } catch (Exception e) {
            logger.info("========CALCULATE SPECIFIC FEE EXCEPTION=======" + gson.toJson(request), e);
        }

        return null;
    }

    public BaseResponse publicCalculateFee(PublicCalculateFeeRequest publicCalculateFeeRequest) {
        List<V2Package> v2Packages = packageRepository.getValidPackages();
        V2Package v2Package = v2Packages.get(0);
        CalculateFeeRequest calculateFeeRequest = new CalculateFeeRequest();
        calculateFeeRequest.setPackCode(v2Package.getCode());
        List<LocationData> senderWards = PreLoadStaticUtil.wardCache.get(publicCalculateFeeRequest.getSenderDistrictCode());
        List<LocationData> receiverWards = PreLoadStaticUtil.wardCache.get(publicCalculateFeeRequest.getSenderDistrictCode());
        PublicCalculateFeeResponse publicCalculateFeeResponse = new PublicCalculateFeeResponse();
        if (senderWards.size() > 0 && receiverWards.size() > 0) {
            calculateFeeRequest.setPickProvince(publicCalculateFeeRequest.getSenderProvinceCode());
            calculateFeeRequest.setPickDistrict(publicCalculateFeeRequest.getSenderDistrictCode());
            calculateFeeRequest.setPickWard(senderWards.get(0).getCode());
            calculateFeeRequest.setPickupType(1);
            CreateOrderDetail createOrderDetail = new CreateOrderDetail();
            createOrderDetail.setWard(receiverWards.get(0).getCode());
            createOrderDetail.setDistrict(publicCalculateFeeRequest.getReceiverDistrictCode());
            createOrderDetail.setProvince(publicCalculateFeeRequest.getReceiverProvinceCode());
            CreateOrderReceiver createOrderReceiver = new CreateOrderReceiver();
            createOrderReceiver.setItems(Collections.singletonList(new OrderDetailProduct()));
            createOrderReceiver.setExtraServices(Collections.singletonList(new ExtraService()));
            createOrderReceiver.setWeight(publicCalculateFeeRequest.getWeight());
            createOrderDetail.setReceivers(Collections.singletonList(createOrderReceiver));
            calculateFeeRequest.setDeliveryPoint(Collections.singletonList(createOrderDetail));
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            String response = null;
            try {
                response = CallServer.getInstance().postWithHeaders(AppConfig.getInstance().calculateFeeUrl,
                        headers, gson2.toJson(calculateFeeRequest));
            } catch (Exception e) {
                logger.info("=======CALCULATE FEE EXCEPTION======" + gson2.toJson(calculateFeeRequest), e);
                return new BaseResponse(500);
            }
            if (response != null) {
                CalculateFeeOrderResponse calculateFeeOrderResponse = gson.fromJson(response, CalculateFeeOrderResponse.class);
                publicCalculateFeeResponse.setTotalFee(calculateFeeOrderResponse
                        .getData().getDeliveryPoint().get(0)
                        .getReceivers().get(0)
                        .getTotalFee());
            }
        }
        return new BaseResponse(200, publicCalculateFeeResponse);
    }
}
