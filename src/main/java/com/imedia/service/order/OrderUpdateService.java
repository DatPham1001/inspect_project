package com.imedia.service.order;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.config.application.AppConfig;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.dao.OrderDAO;
import com.imedia.oracle.dao.ShopProfileDAO;
import com.imedia.oracle.entity.*;
import com.imedia.oracle.reportrepository.RouteAreaReportRepository;
import com.imedia.oracle.repository.*;
import com.imedia.service.gateway.GatewayService;
import com.imedia.service.gateway.model.Hls1UpdateResponse;
import com.imedia.service.notify.NotifyService;
import com.imedia.service.order.enums.OrderStatusEnum;
import com.imedia.service.order.enums.SellingFeeEnum;
import com.imedia.service.order.model.*;
import com.imedia.service.pickupaddress.model.AddressDataOM;
import com.imedia.service.postage.FeeService;
import com.imedia.service.postage.PostageService;
import com.imedia.service.postage.model.*;
import com.imedia.service.product.ProductService;
import com.imedia.service.user.model.UserInfoAddressData;
import com.imedia.service.userwallet.UserWalletService;
import com.imedia.service.userwallet.model.UserBalanceResponse;
import com.imedia.service.wallet.WalletService;
import com.imedia.util.AppUtil;
import com.imedia.util.CallRedis;
import com.imedia.util.PreLoadStaticUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderUpdateService {
    static final Logger logger = LogManager.getLogger(OrderService.class);
    static final Gson gson = new Gson();
    static final Gson gson2 = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private final AppUserRepository appUserRepository;
    private final ShopProfileDAO shopProfileDAO;
    private final PostageService postageService;
    private final UserWalletService userWalletService;
    private final V2PackageRepository packageRepository;
    private final SvcOrderRepository orderRepository;
    private final SvcOrderDetailRepository orderDetailRepository;
    private final DetailProductRepository productRepository;
    private final DetailSellingFeeRepository detailSellingFeeRepository;
    private final OrderDAO orderDAO;
    private final SvcOrderLogRepository orderLogRepository;
    private final CarrierRepository carrierRepository;
    private final ForwardConfigRepository forwardConfigRepository;
    private final OrderService orderService;
    private final GatewayService gatewayService;
    private final OrderDetailRepository shipOrderDetailRepository;
    private final NotifyService notifyService;
    private final HistoryChangeReceiverRepository changeReceiverRepository;
    private final FeeService feeService;
    private final ProductService productService;
    private final FindShipService findShipService;
    private final AddressDeliveryService addressDeliveryService;
    private final OrderLogService orderLogService;
    private final WardRepository wardRepository;
    private final OrderCheckPointRepository checkPointRepository;
    private final OrderActionService orderActionService;
    private final WalletService walletService;
    private final ShopAddressRepository shopAddressRepository;
    private final RouteAreaReportRepository routeAreaReportRepository;
    private final AddressDeliveryRepository addressDeliveryRepository;
    private final OrderPartialRequestRepository partialRequestRepository;

    @Autowired
    public OrderUpdateService(AppUserRepository appUserRepository, ShopProfileDAO shopProfileDAO, PostageService postageService, UserWalletService userWalletService, V2PackageRepository packageRepository, SvcOrderRepository orderRepository, SvcOrderDetailRepository orderDetailRepository, DetailProductRepository productRepository, DetailSellingFeeRepository detailSellingFeeRepository, OrderDAO orderDAO, SvcOrderLogRepository orderLogRepository, CarrierRepository carrierRepository, ForwardConfigRepository forwardConfigRepository, OrderService orderService, GatewayService gatewayService, OrderDetailRepository shipOrderDetailRepository, NotifyService notifyService, HistoryChangeReceiverRepository changeReceiverRepository, FeeService feeService, ProductService productService, FindShipService findShipService, AddressDeliveryService addressDeliveryService, OrderLogService orderLogService, WardRepository wardRepository, OrderCheckPointRepository checkPointRepository, OrderActionService orderActionService, WalletService walletService, ShopAddressRepository shopAddressRepository, RouteAreaReportRepository routeAreaReportRepository, AddressDeliveryRepository addressDeliveryRepository, OrderPartialRequestRepository partialRequestRepository) {
        this.appUserRepository = appUserRepository;
        this.shopProfileDAO = shopProfileDAO;
        this.postageService = postageService;
        this.userWalletService = userWalletService;
        this.packageRepository = packageRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.detailSellingFeeRepository = detailSellingFeeRepository;
        this.orderDAO = orderDAO;
        this.orderLogRepository = orderLogRepository;
        this.carrierRepository = carrierRepository;
        this.forwardConfigRepository = forwardConfigRepository;
        this.orderService = orderService;
        this.gatewayService = gatewayService;
        this.shipOrderDetailRepository = shipOrderDetailRepository;
        this.notifyService = notifyService;
        this.changeReceiverRepository = changeReceiverRepository;
        this.feeService = feeService;
        this.productService = productService;
        this.findShipService = findShipService;
        this.addressDeliveryService = addressDeliveryService;
        this.orderLogService = orderLogService;
        this.wardRepository = wardRepository;
        this.checkPointRepository = checkPointRepository;
        this.orderActionService = orderActionService;
        this.walletService = walletService;
        this.shopAddressRepository = shopAddressRepository;
        this.routeAreaReportRepository = routeAreaReportRepository;
        this.addressDeliveryRepository = addressDeliveryRepository;
        this.partialRequestRepository = partialRequestRepository;
    }

    // Chỉ sửa đơn con => quan hệ sẽ là 1 đơn to 1 điểm giao 1 người nhận (cập n hật COD sẽ ở api khác)
    public BaseResponse handleUpdateOrderInfo(CreateOrder createOrder, String username, BigDecimal orderDetailCode) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        SvcOrderDetail oldOrderDetail = orderDetailRepository.getOrderDetailByCodeWithPhone(orderDetailCode, username);
        AppUser appUser = appUserRepository.findByPhone(username);
        if (oldOrderDetail == null)
            return new BaseResponse(614);
        boolean isUpdated = false;
        CreateOrderDetail updateDeliveryPoint = createOrder.getDeliveryPoint().get(0);
        CreateOrderReceiver updateCreateOrderReceiver = updateDeliveryPoint.getReceivers().get(0);
        //Kiểm tra địa chỉ có thay đổi không?
        UserInfoAddressData addressData = PreLoadStaticUtil.addresses.get(updateDeliveryPoint.getWard());
        AddressDeliveryRequest addressDeliveryRequest = new AddressDeliveryRequest(updateDeliveryPoint.getAddress(), addressData.getProvinceName(),
                addressData.getProvinceCode(), addressData.getDistrictName(), addressData.getDistrictCode(), addressData.getWardName(), addressData.getWardCode());
        Carrier carrier = null;
        if (oldOrderDetail.getCarrierId() != null)
            carrier = carrierRepository.findCarrierById(oldOrderDetail.getCarrierId());
        AddressDelivery addressDeliveryResponse = (AddressDelivery) addressDeliveryService.createAddressDelivery(addressDeliveryRequest).getData();
        //Nếu là phase 1 là đi luồng riêng
        if (carrier != null
                && (carrier.getCode().equalsIgnoreCase("HLS1")
                || carrier.getCode().equalsIgnoreCase("HOLA1"))) {
            //Tinh gia
            BaseResponse calculateFee = postageService.calculateFee(createOrder, appUser);
            if (calculateFee.getStatus() != 200)
                return calculateFee;
            CalculateFeeOrderResponse calculateFeeData = (CalculateFeeOrderResponse) calculateFee.getData();
            CalculateFeeReceivers receivers = calculateFeeData.getData().getDeliveryPoint().get(0).getReceivers().get(0);
            Hls1UpdateResponse updateResponse = gatewayService.updateOrderHls1(carrier, createOrder, oldOrderDetail, appUser, false);
            if (updateResponse.getChangeStatus() == 0) {
                CreateOrderDetail deliveryPoint = createOrder.getDeliveryPoint().get(0);
                CreateOrderReceiver receiver = deliveryPoint.getReceivers().get(0);
                if (receiver.getWeight() != null) oldOrderDetail.setWeight(receiver.getWeight());
                if (receiver.getWidth() != null) oldOrderDetail.setWidth(receiver.getWidth());
                if (receiver.getLength() != null) oldOrderDetail.setLength(receiver.getLength());
                if (receiver.getHeight() != null) oldOrderDetail.setHeight(receiver.getHeight());
                if (receiver.getName() != null && !receiver.getName().isEmpty())
                    oldOrderDetail.setConsignee(receiver.getName());
                if (receiver.getPhone() != null && !receiver.getPhone().isEmpty())
                    oldOrderDetail.setPhone(receiver.getPhone());
                //Dia chi ng nhan
                oldOrderDetail.setAddressDeliveryId(addressDeliveryResponse.getId());
                orderDetailRepository.save(oldOrderDetail);
                feeService.updateSellingFee(oldOrderDetail.getSvcOrderDetailCode(), receivers);
                //insert tracking
                orderLogService.insertOrderLog(oldOrderDetail, OrderStatusEnum.NOTE_UPDATE_ORDER_INFO.message, oldOrderDetail.getStatus());
            }
            return new BaseResponse(707, oldOrderDetail);
        }
        //Cập nhật thông tin người nhận và sđt
        if (!oldOrderDetail.getConsignee().equalsIgnoreCase(updateCreateOrderReceiver.getName())
                || !oldOrderDetail.getPhone().equalsIgnoreCase(updateCreateOrderReceiver.getPhone())) {
            if (carrier != null) {
                //Nếu đã có NVC thì check xem có phải HLS k
                if (carrier.getCode().equalsIgnoreCase("HLS") || carrier.getCode().equalsIgnoreCase("HOLA")) {
                    String oldConsignee = oldOrderDetail.getConsignee();
                    String oldPhone = oldOrderDetail.getPhone();
                    logger.info("========UPDATE RECEIVER INFO======== " + orderDetailCode);
                    //Kiểm tra số lần cập nhật
                    int counter = changeReceiverRepository.countUpdatedChange(FeeConstant.ChangedInfoTypeFee.PHONE.name(), orderDetailCode) + 1;
                    FeeSettingInfoOM feeSettingInfo = packageRepository.getConfigFeeSetting(oldOrderDetail.getServicePackId(),
                            SellingFeeEnum.UPDATE_FEE.type, FeeConstant.ChangedInfoTypeFee.PHONE.code);
                    if (feeSettingInfo != null && feeSettingInfo.getMax() < counter)
                        return new BaseResponse(713, String.valueOf(feeSettingInfo.getMax()), 2);
                    //Insert history
                    feeService.insertChangeReceiverInfoHistory(oldOrderDetail, updateCreateOrderReceiver,
                            0L, FeeConstant.ChangedInfoTypeFee.PHONE.name());
                    //Tính thêm phí cập nhật
                    feeService.insertUpdateFee(oldOrderDetail.getSvcOrderDetailCode(), createOrder.getPackCode(), FeeConstant.ChangedInfoTypeFee.PHONE.name(), counter);
                    oldOrderDetail.setConsignee(updateCreateOrderReceiver.getName());
                    oldOrderDetail.setPhone(updateCreateOrderReceiver.getPhone());
                    SvcOrderDetail result = orderDetailRepository.save(oldOrderDetail);
                    logger.info("========ORDER DETAIL UPDATE  SAVE DB======== " + orderDetailCode + "||" + gson.toJson(result));
                    //Cập nhật đơn cho shipper
                    shipOrderDetailRepository.updateShipOrderDetail(updateCreateOrderReceiver.getName(),
                            updateCreateOrderReceiver.getPhone(), oldOrderDetail.getSvcOrderDetailCode());
                    //insert tracking
                    orderLogService.insertOrderLog(oldOrderDetail,
                            OrderStatusEnum.NOTE_UPDATE_ORDER_RECEIVER.message
                                    + " từ " + oldConsignee + " - " + oldPhone
                                    + " thành " + oldOrderDetail.getConsignee() + " - " + oldOrderDetail.getPhone(),
                            oldOrderDetail.getStatus());
                }
            }
            //Nếu chưa có NVC thì auto cập nhật
            else {
                oldOrderDetail.setConsignee(updateCreateOrderReceiver.getName());
                oldOrderDetail.setPhone(updateCreateOrderReceiver.getPhone());
                SvcOrderDetail result = orderDetailRepository.save(oldOrderDetail);
                logger.info("========ORDER DETAIL UPDATE  SAVE DB======== " + orderDetailCode + "||" + gson.toJson(result));
            }
        }
        //Cập nhật địa chỉ mới
        if (addressDeliveryResponse.getId() != oldOrderDetail.getAddressDeliveryId()) {
            logger.info("==========================UPDATE ADDRESS DELIVERY====================" + orderDetailCode);
            //Nếu là trạng thái được phép đổi địa chỉ và là NVC Hola
//            if (!appConfig.changeInfoStatus.contains(String.valueOf(oldOrderDetail.getStatus())))
//                return new BaseResponse(703);
//            if (carrier != null) {
//                if (!carrier.getCode().equalsIgnoreCase("HLS") && !carrier.getCode().equalsIgnoreCase("HOLA"))
//                    return new BaseResponse(711);
//            }
            //Kiểm tra rule cập nhật đúng kho
            BaseResponse checkAddressValid = checkAddressValid(addressDeliveryResponse.getWardCode(), oldOrderDetail);
            if (checkAddressValid != null)
                return checkAddressValid;
            BaseResponse calculateFee = postageService.calculateFee(createOrder, appUserRepository.findByPhone(username));
            if (calculateFee.getStatus() != 200)
                return calculateFee;
            else {
                CalculateFeeOrderResponse calculateFeeData = (CalculateFeeOrderResponse) calculateFee.getData();
                feeService.updateSellingFee(orderDetailCode, calculateFeeData.getData().getDeliveryPoint().get(0).getReceivers().get(0));
                //Kiểm tra số lần cập nhật
                int counter = changeReceiverRepository.countUpdatedChange(FeeConstant.ChangedInfoTypeFee.ADDRESS.name(), orderDetailCode) + 1;
                FeeSettingInfoOM feeSettingInfo = packageRepository.getConfigFeeSetting(oldOrderDetail.getServicePackId(),
                        SellingFeeEnum.UPDATE_FEE.type, FeeConstant.ChangedInfoTypeFee.ADDRESS.code);
                if (feeSettingInfo != null && feeSettingInfo.getMax() < counter)
                    return new BaseResponse(715, String.valueOf(feeSettingInfo.getMax()), 2);
                //Insert history
                feeService.insertChangeReceiverInfoHistory(oldOrderDetail, updateCreateOrderReceiver,
                        addressDeliveryResponse.getId(), FeeConstant.ChangedInfoTypeFee.ADDRESS.name());
                oldOrderDetail.setAddressDeliveryId(addressDeliveryResponse.getId());
                //Tính thêm phí cập nhật
                feeService.insertUpdateFee(oldOrderDetail.getSvcOrderDetailCode(), createOrder.getPackCode(), FeeConstant.ChangedInfoTypeFee.ADDRESS.name(), counter);
                //Cap naht checkpoint ship
                handleUpdateShipCheckPoint(orderDetailCode, addressDeliveryResponse);
                //insert tracking
                orderLogService.insertOrderLog(oldOrderDetail, OrderStatusEnum.NOTE_UPDATE_ORDER_RECEIVER_ADD.message, oldOrderDetail.getStatus());

                isUpdated = true;
            }
        }
        int oldWeight = -oldOrderDetail.getWeight();
        //Cập nhật cân nặng
        if (!updateCreateOrderReceiver.getWeight().equals(oldOrderDetail.getWeight())
                || !updateCreateOrderReceiver.getLength().equals(oldOrderDetail.getLength())
                || !updateCreateOrderReceiver.getWidth().equals(oldOrderDetail.getWidth())
                || !updateCreateOrderReceiver.getHeight().equals(oldOrderDetail.getHeight())
        ) {
            if (!appConfig.changePackageStatus.contains(String.valueOf(oldOrderDetail.getStatus())))
                return new BaseResponse(704);
            if (carrier != null && !carrier.getSpecialServices().contains("2"))
                return new BaseResponse(712);
            logger.info("==========================UPDATE WEIGHT ====================" + orderDetailCode
                    + " OLD WEIGHT " + oldOrderDetail.getWeight() + " NEW WEIGHT " + updateCreateOrderReceiver.getWeight());
            BaseResponse calculateFee = postageService.calculateFee(createOrder, appUserRepository.findByPhone(username));
            if (calculateFee.getStatus() != 200)
                return calculateFee;
            else {
                CalculateFeeOrderResponse calculateFeeData = (CalculateFeeOrderResponse) calculateFee.getData();
                CalculateFeeReceivers receivers = calculateFeeData.getData().getDeliveryPoint().get(0).getReceivers().get(0);
                //Nếu có NVC
                if (carrier != null) {
                    //Kiểm tra số lần cập nhật
                    int counter = changeReceiverRepository.countUpdatedChange(FeeConstant.ChangedInfoTypeFee.DIMENSION.name(), orderDetailCode) + 1;
                    FeeSettingInfoOM feeSettingInfo = packageRepository.getConfigFeeSetting(oldOrderDetail.getServicePackId(),
                            SellingFeeEnum.UPDATE_FEE.type, FeeConstant.ChangedInfoTypeFee.DIMENSION.code);
                    if (feeSettingInfo != null && feeSettingInfo.getMax() < counter)
                        return new BaseResponse(716, String.valueOf(feeSettingInfo.getMax()), 2);
                    //Nếu là HOLA
                    if (carrier.getCode().equalsIgnoreCase("HLS") || carrier.getCode().equalsIgnoreCase("HOLA")) {
                        shipOrderDetailRepository.updateShipOrderDetail(updateCreateOrderReceiver.getWeight(), updateCreateOrderReceiver.getWidth(),
                                updateCreateOrderReceiver.getHeight(), updateCreateOrderReceiver.getLength(), oldOrderDetail.getSvcOrderDetailCode());
                        feeService.updateSellingFee(orderDetailCode, receivers);
                        //Insert history
                        feeService.insertChangeReceiverInfoHistory(oldOrderDetail, updateCreateOrderReceiver,
                                0L, FeeConstant.ChangedInfoTypeFee.DIMENSION.toString());
                        //Tính thêm phí cập nhật
                        feeService.insertUpdateFee(oldOrderDetail.getSvcOrderDetailCode(), createOrder.getPackCode(),
                                FeeConstant.ChangedInfoTypeFee.DIMENSION.name(), counter);
                        updateWeight(updateCreateOrderReceiver, oldOrderDetail);
                        isUpdated = true;
                    } else {
                        //Người nhận trả phí => gọi đổi COD
                        if (oldOrderDetail.getIsFree() == 2) {
                            //Tính thêm phí cập nhật
                            DetailSellingFee updateFee = feeService.insertUpdateFee(oldOrderDetail.getSvcOrderDetailCode(), createOrder.getPackCode(),
                                    FeeConstant.ChangedInfoTypeFee.DIMENSION.name(), counter);
                            BigDecimal totalNewFee = receivers.getTotalFee();
                            if (updateFee != null) totalNewFee = totalNewFee.add(updateFee.getValue());
                            BigDecimal oldCod = productRepository.getTotalCod(orderDetailCode);
                            boolean codChanged = gatewayService.changeCod(oldOrderDetail, oldCod.add(totalNewFee), carrier);
                            if (codChanged) {
                                feeService.updateSellingFee(orderDetailCode, receivers);
                                //Insert history
                                feeService.insertChangeReceiverInfoHistory(oldOrderDetail, updateCreateOrderReceiver,
                                        0L, FeeConstant.ChangedInfoTypeFee.DIMENSION.toString());
                                updateWeight(updateCreateOrderReceiver, oldOrderDetail);
                                isUpdated = true;
                            } else {
                                detailSellingFeeRepository.revertUpdateFee(orderDetailCode);
                                return new BaseResponse(708);
                            }
                        } else {
                            //Insert history
                            feeService.insertChangeReceiverInfoHistory(oldOrderDetail, updateCreateOrderReceiver,
                                    0L, FeeConstant.ChangedInfoTypeFee.DIMENSION.toString());
                            //Tính thêm phí cập nhật
                            feeService.insertUpdateFee(oldOrderDetail.getSvcOrderDetailCode(), createOrder.getPackCode(),
                                    FeeConstant.ChangedInfoTypeFee.DIMENSION.name(), counter);
                            feeService.updateSellingFee(orderDetailCode, receivers);
                            updateWeight(updateCreateOrderReceiver, oldOrderDetail);
                            isUpdated = true;
                        }
                    }
                }
                //Nếu chưa có NVc thì cho cập nhật
                else {
                    feeService.updateSellingFee(orderDetailCode, receivers);
                    updateWeight(updateCreateOrderReceiver, oldOrderDetail);
                    isUpdated = true;
                }
            }
        }
        if (isUpdated) {
            SvcOrderDetail result = orderDetailRepository.save(oldOrderDetail);
            logger.info("========ORDER DETAIL UPDATE  SAVE DB======== " + orderDetailCode + "||" + gson.toJson(result));

        }
        return new BaseResponse(700, oldOrderDetail);

    }

    private BaseResponse checkAddressValid(String toWardCode, SvcOrderDetail svcOrderDetail) {
        AddressDelivery oldAddressDelivery = addressDeliveryRepository.findAddressDeliveryById(svcOrderDetail.getAddressDeliveryId());

        List<SvcOrderLog> deliveredLog = orderLogRepository.getValidUpdateOrder(svcOrderDetail.getSvcOrderDetailCode(), OrderStatusEnum.DELIVERING.code);
        //Đơn đang giao
        if (svcOrderDetail.getStatus().equals(OrderStatusEnum.DELIVERING.code)
                || svcOrderDetail.getStatus().equals(OrderStatusEnum.DELIVERING_LAST.code)) {
            List<BigDecimal> routeFr = routeAreaReportRepository.getRouteIdToCheck(oldAddressDelivery.getWardCode());
            List<BigDecimal> routeTo = routeAreaReportRepository.getRouteIdToCheck(toWardCode);
            if (routeTo.size() == 0 || routeFr.size() == 0)
                return new BaseResponse(2105);
            if (!routeTo.get(0).equals(routeFr.get(0)))
                return new BaseResponse(500, "Đơn đang giao không cho phép thay đổi địa chỉ giao khác Tuyến", 3);
        } else if (deliveredLog.size() > 0) {
            List<BigDecimal> storeFr = routeAreaReportRepository.getStoreIdToCheck(oldAddressDelivery.getWardCode());
            List<BigDecimal> storeTo = routeAreaReportRepository.getStoreIdToCheck(toWardCode);
            if (storeTo.size() == 0 || storeFr.size() == 0)
                return new BaseResponse(2105);
            if (!storeTo.get(0).equals(storeFr.get(0)))
                return new BaseResponse(500, "Đơn đã giao,không cho phép thay đổi địa chỉ giao khác Kho", 3);
        } else {
            List<SvcOrderLog> transferLog = orderLogRepository.getValidUpdateOrder(svcOrderDetail.getSvcOrderDetailCode(),
                    OrderStatusEnum.TRANSPORTING.code);
            //Đã luân chuyển => chỉ chỉnh sửa trong kho quản lý
            if (transferLog.size() > 0) {
                List<BigDecimal> storeFr = routeAreaReportRepository.getStoreIdToCheck(oldAddressDelivery.getWardCode());
                List<BigDecimal> storeTo = routeAreaReportRepository.getStoreIdToCheck(toWardCode);
                if (storeTo.size() == 0 || storeFr.size() == 0)
                    return new BaseResponse(2105);
                if (!storeTo.get(0).equals(storeFr.get(0)))
                    return new BaseResponse(500, "Đơn đã luân chuyển,không cho phép thay đổi địa chỉ giao khác Kho", 3);
            }
        }
        return null;
    }

    private void handleUpdateShipCheckPoint(BigDecimal orderCode, AddressDelivery addressDeliveryResponse) {
        AddressDataOM addressDataOM = wardRepository.getAddressDataId(addressDeliveryResponse.getWardCode());
        OrderCheckPoint checkPoint = checkPointRepository.getCheckPointyOrderCode(orderCode);
        if (checkPoint != null) {
            checkPoint.setAddress(addressDeliveryResponse.getAddress());
            checkPoint.setLatitude(addressDeliveryResponse.getLatitude());
            checkPoint.setLongitude(addressDeliveryResponse.getLongitude());
            checkPoint.setWardId(addressDataOM.getWardId());
            checkPoint.setDistrictId(addressDataOM.getDistrictId());
            checkPoint.setProvinceId(addressDataOM.getProvinceId());
            checkPointRepository.save(checkPoint);
        }
    }

    private void updateWeight(CreateOrderReceiver updateCreateOrderReceiver, SvcOrderDetail orderDetail) {
        if (!updateCreateOrderReceiver.getWeight().equals(orderDetail.getWeight())) {
            orderLogService.insertOrderLog(orderDetail, OrderStatusEnum.NOTE_UPDATE_ORDER_WEIGHT.message
                            + "từ " + AppUtil.formatDecimal(BigDecimal.valueOf(orderDetail.getWeight())).replace(",", ".")
                            + "gr thành " + AppUtil.formatDecimal(BigDecimal.valueOf(updateCreateOrderReceiver.getWeight()))
                            .replace(",", ".") + "gr",
                    orderDetail.getStatus());
        } else {
            orderLogService.insertOrderLog(orderDetail, OrderStatusEnum.NOTE_UPDATE_ORDER_WEIGHT2.message
                            + "từ " + orderDetail.getLength() + "-" + orderDetail.getWidth() + "-" + orderDetail.getHeight()
                            + " thành " + updateCreateOrderReceiver.getLength() + "-"
                            + updateCreateOrderReceiver.getWidth() + "-"
                            + updateCreateOrderReceiver.getHeight(),
                    orderDetail.getStatus());
        }
        orderDetail.setWeight(updateCreateOrderReceiver.getWeight());
        orderDetail.setWidth(updateCreateOrderReceiver.getWidth());
        orderDetail.setHeight(updateCreateOrderReceiver.getHeight());
        orderDetail.setLength(updateCreateOrderReceiver.getLength());
        orderDetail.setWeight(updateCreateOrderReceiver.getWeight());
    }


    public BaseResponse handleChangeCOD(CreateOrder createOrder, String username, BigDecimal orderDetailCode) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        SvcOrderDetail oldOrderDetail = orderDetailRepository.getValidBySvcOrderDetailCode(orderDetailCode);
        if (oldOrderDetail == null)
            return new BaseResponse(614);
        OrderPartialRequest partialRequest = partialRequestRepository.findByOrderDetailCodeAndIsConfirmed(oldOrderDetail.getSvcOrderDetailCode(), 0);
        if (partialRequest != null)
            return new BaseResponse(719);
        CreateOrderDetail updateDeliveryPoint = createOrder.getDeliveryPoint().get(0);
        CreateOrderReceiver updateCreateOrderReceiver = updateDeliveryPoint.getReceivers().get(0);
        //check status can change cod
        if (!appConfig.changeCodStatus.contains(String.valueOf(oldOrderDetail.getStatus())))
            return new BaseResponse(702);
        List<DetailProduct> products = productRepository.getProductsByOrderCode(String.valueOf(orderDetailCode));
        productService.updateProducts(products, updateCreateOrderReceiver, oldOrderDetail, false);
        BigDecimal oldCod = oldOrderDetail.getRealityCod();
        BigDecimal newCod = BigDecimal.ZERO;
        for (OrderDetailProduct item : updateCreateOrderReceiver.getItems()) {
            newCod = newCod.add(item.getCod().multiply(item.getQuantity()));
        }
        boolean allowChangeCod = false;
        if (!newCod.equals(oldCod)) {
            Carrier carrier = carrierRepository.findCarrierById(oldOrderDetail.getCarrierId());
            //Hola phase 1 luong rieng
            if (carrier != null
                    && (carrier.getCode().equalsIgnoreCase("HLS1")
                    || carrier.getCode().equalsIgnoreCase("HOLA1"))) {
                logger.info("==========================UPDATE COD HOLA 1 ====================" + orderDetailCode
                        + " OLD COD " + oldCod + " NEW COD " + newCod);
                AppUser appUser = appUserRepository.findAppUserById(oldOrderDetail.getShopId());
                //Tinh gia
                BaseResponse calculateFee = postageService.calculateFee(createOrder, appUser);
                if (calculateFee.getStatus() != 200)
                    return calculateFee;
                CalculateFeeOrderResponse calculateFeeData = (CalculateFeeOrderResponse) calculateFee.getData();
                CalculateFeeReceivers receivers = calculateFeeData.getData().getDeliveryPoint().get(0).getReceivers().get(0);
                //Holaphase 1
                Hls1UpdateResponse updateResponse = gatewayService.updateOrderHls1(carrier, createOrder, oldOrderDetail, appUser, true);
                if (updateResponse.getChangeStatus() == 0) {
                    productService.updateProducts(products, updateCreateOrderReceiver, oldOrderDetail, true);
                    oldOrderDetail.setRealityCod(newCod);
                    orderDetailRepository.save(oldOrderDetail);
                    feeService.updateSellingFee(orderDetailCode, receivers);
                    //insert tracking
                    orderLogService.insertOrderLog(oldOrderDetail, OrderStatusEnum.NOTE_UPDATE_ORDER_COD.message, oldOrderDetail.getStatus());
                    return new BaseResponse(707, oldOrderDetail);
                } else return new BaseResponse(708);
            }
            //Kiểm tra min max COD đôi
            FeeSettingInfoOM rangeCod = packageRepository.getRangeCodChange(oldOrderDetail.getServicePackId());
//            if (newCod.compareTo(BigDecimal.valueOf(rangeCod.getMin())) < 0)
//                return new BaseResponse(717, decimalFormat.format(rangeCod.getMin()), "");
            if (rangeCod != null && newCod.compareTo(BigDecimal.valueOf(rangeCod.getMax())) > 0)
                return new BaseResponse(718, AppUtil.formatDecimal(BigDecimal.valueOf(rangeCod.getMax())), 2);
            logger.info("==========================UPDATE COD ====================" + orderDetailCode
                    + " OLD COD " + oldCod + " NEW COD " + newCod);
            BaseResponse calculateFee = postageService.calculateFee(createOrder, appUserRepository.findByPhone(username));
            if (calculateFee.getStatus() != 200)
                return new BaseResponse(500);
            else {
                if (oldOrderDetail.getCarrierId() != null) {
                    if (carrier != null && carrier.getSpecialServices() != null && carrier.getSpecialServices().contains("2")) {
                        //Kiểm tra số lần cập nhật
                        int counter = changeReceiverRepository.countUpdatedChange(FeeConstant.ChangedInfoTypeFee.COD.name(), orderDetailCode) + 1;
                        FeeSettingInfoOM feeSettingInfo = packageRepository.getConfigFeeSetting(oldOrderDetail.getServicePackId(),
                                SellingFeeEnum.UPDATE_FEE.type, FeeConstant.ChangedInfoTypeFee.COD.code);
                        if (feeSettingInfo != null && feeSettingInfo.getMax() < counter)
                            return new BaseResponse(714, String.valueOf(feeSettingInfo.getMax()), 2);
                        CalculateFeeOrderResponse calculateFeeData = (CalculateFeeOrderResponse) calculateFee.getData();
                        CalculateFeeReceivers receivers = calculateFeeData.getData().getDeliveryPoint().get(0).getReceivers().get(0);
                        boolean codChanged = false;
                        //Đơn của Hola
                        if (carrier.getCode().equalsIgnoreCase("HLS") || carrier.getCode().equalsIgnoreCase("HOLA")) {
                            //Nếu đơn là đang giao thì sẽ phải chờ ship xác nhận
                            if (oldOrderDetail.getStatus().equals(OrderStatusEnum.DELIVERING.code)
                                    || oldOrderDetail.getStatus().equals(OrderStatusEnum.DELIVERING_LAST.code)) {
                                notifyService.notifyChangeCodAppShip(oldOrderDetail, oldCod, newCod);
                                handleRequestChangeCod(updateCreateOrderReceiver, products);
                                CacheChangeCodRequest request = new CacheChangeCodRequest(oldCod, newCod, createOrder.getPackCode(), counter, receivers);
                                CallRedis.setCacheExpiry("COD:" + orderDetailCode, gson.toJson(request));
                                logger.info("=====CACHE COD======" + gson.toJson(request));
                                return new BaseResponse(705);
                            }
                            //Nếu không thì cập nhật COD và bắn notify
                            else {
                                //Insert history
                                feeService.insertChangeCodHistory(oldOrderDetail, oldCod, newCod);
                                //Tính thêm phí cập nhật
                                feeService.insertUpdateFee(oldOrderDetail.getSvcOrderDetailCode(), createOrder.getPackCode(),
                                        FeeConstant.ChangedInfoTypeFee.COD.name(), counter);
//                                notifyService.notifyChangeCodAppShip(oldOrderDetail, oldCod, newCod);
                                feeService.updateSellingFee(orderDetailCode, receivers);
                                oldOrderDetail.setRealityCod(newCod);
                                shipOrderDetailRepository.updateCod(oldOrderDetail.getRealityCod(), oldOrderDetail.getSvcOrderDetailCode());
                                allowChangeCod = true;
                            }
                        } else {
                            //Đơn của NVC khác
                            //Tính thêm phí cập nhật
                            DetailSellingFee updateFee = feeService.insertUpdateFee(oldOrderDetail.getSvcOrderDetailCode(), createOrder.getPackCode(),
                                    FeeConstant.ChangedInfoTypeFee.COD.name(), counter);
                            BigDecimal totalNewFee = receivers.getTotalFee();
                            if (updateFee != null)
                                totalNewFee = totalNewFee.add(updateFee.getValue());
                            //Người nhận trả phí => gọi đổi COD + tổng phí
                            if (oldOrderDetail.getIsFree() == 2) {
                                codChanged = gatewayService.changeCod(oldOrderDetail, newCod.add(totalNewFee), carrier);
                            } else {
                                //Gọi sang đối tác đổi COD
                                codChanged = gatewayService.changeCod(oldOrderDetail, newCod, carrier);
                            }
                            if (codChanged) {
                                //Insert history
                                feeService.insertChangeCodHistory(oldOrderDetail, oldCod, newCod);
                                feeService.updateSellingFee(orderDetailCode, receivers);
                                oldOrderDetail.setRealityCod(newCod);
                                allowChangeCod = true;
                            } else {
                                detailSellingFeeRepository.revertUpdateFee(orderDetailCode);
                                return new BaseResponse(709);
                            }
                        }

                    } else return new BaseResponse(710);
                }
                //Đơn chưa có NVC => đổi cod bình thường
                else {
                    CalculateFeeOrderResponse calculateFeeData = (CalculateFeeOrderResponse) calculateFee.getData();
                    feeService.updateSellingFee(orderDetailCode, calculateFeeData.getData().getDeliveryPoint().get(0).getReceivers().get(0));
                    allowChangeCod = true;
                }
            }
        }
        productService.updateProducts(products, updateCreateOrderReceiver, oldOrderDetail, allowChangeCod);
        if (allowChangeCod) {
            orderDetailRepository.save(oldOrderDetail);
            //insert tracking
            orderLogService.insertOrderLog(oldOrderDetail,
                    OrderStatusEnum.NOTE_UPDATE_ORDER_COD.message
                            + " từ " + AppUtil.formatDecimal(oldCod)
                            + "đ thành " + AppUtil.formatDecimal(newCod) + "đ",
                    oldOrderDetail.getStatus());

//            shipOrderDetailRepository.updateCod(oldOrderDetail.getRealityCod(), oldOrderDetail.getSvcOrderDetailCode());
        }
        return new BaseResponse(707, oldOrderDetail);

    }


    private void handleRequestChangeCod(CreateOrderReceiver createOrderReceiver, List<DetailProduct> oldProducts) {
        for (DetailProduct oldProduct : oldProducts) {
            for (OrderDetailProduct item : createOrderReceiver.getItems()) {
                if (!item.getProductName().isEmpty())
                    oldProduct.setName(item.getProductName());
                if (oldProduct.getId() == item.getId()) {
                    oldProduct.setConfirmed(0);
                    oldProduct.setChangedCod(item.getCod());
                    productRepository.save(oldProduct);
                }
            }
        }

    }

    public void handleConfirmCodCallback(ChangeCodCallback callback) throws Exception {
        String cacheChangeCod = CallRedis.getCache("COD:" + callback.getData().getDetailCode());
        if (cacheChangeCod == null) {
            logger.info("=====CONFIRM COD FAILED====== CACHE NULL" + callback.getData().getDetailCode());
            return;
        }
        CacheChangeCodRequest cacheChangeCodRequest = gson.fromJson(cacheChangeCod, CacheChangeCodRequest.class);
        logger.info("=====CACHE COD CALLBACK======" + gson.toJson(cacheChangeCodRequest));
        SvcOrderDetail oldOrderDetail = orderDetailRepository.getValidBySvcOrderDetailCode(callback.getData().getDetailCode());
        //Accept
        if (callback.getCode() == 3) {
            //Insert history
            feeService.insertChangeCodHistory(oldOrderDetail, cacheChangeCodRequest.getOldCod(), cacheChangeCodRequest.getNewCod());
            //Tính thêm phí cập nhật
            feeService.insertUpdateFee(oldOrderDetail.getSvcOrderDetailCode(), cacheChangeCodRequest.getPackCode(),
                    FeeConstant.ChangedInfoTypeFee.COD.name(), cacheChangeCodRequest.getCounter());
            feeService.updateSellingFee(oldOrderDetail.getSvcOrderDetailCode(), cacheChangeCodRequest.getReceivers());
            orderLogService.insertOrderLog(oldOrderDetail,
                    OrderStatusEnum.NOTE_COD_CONFIRM.message
                            + "từ " + AppUtil.formatDecimal(cacheChangeCodRequest.getOldCod())
                            + "đ thành " + AppUtil.formatDecimal(cacheChangeCodRequest.getNewCod()) + "đ",
                    oldOrderDetail.getStatus());
            oldOrderDetail.setRealityCod(cacheChangeCodRequest.getNewCod());
            orderDetailRepository.save(oldOrderDetail);
            shipOrderDetailRepository.updateCod(oldOrderDetail.getRealityCod(), oldOrderDetail.getSvcOrderDetailCode());
        }
        //Reject
        else {
            orderLogService.insertOrderLog(oldOrderDetail, OrderStatusEnum.NOTE_COD_REJECT.message, oldOrderDetail.getStatus());
        }
    }

    public BaseResponse getWTCOrderToUpdate(BigDecimal orderCode, String username) {
        AppUser appUser = appUserRepository.findByPhone(username);
        SvcOrder order = orderRepository.findByOrderCodeAndShopId(orderCode, BigDecimal.valueOf(appUser.getId()));
        if (order == null)
            return new BaseResponse(614);
        List<Integer> statuses = Arrays.asList(900, 1001);
        List<SvcOrderDetail> orderDetails = orderDetailRepository.getValidAllBySvcOrderIdAndStatusIn(orderCode, statuses);
        if (orderDetails.size() == 0)
            return new BaseResponse(614);
        V2Package v2Package = packageRepository.findV2PackageById(order.getServicePackId());
        CreateOrder createOrder = orderActionService.buildCreateOrderFromOrder(v2Package, order, orderDetails);
        return new BaseResponse(200, createOrder);
    }

    public BaseResponse handleUpdateWaitToConfirmOrder(CreateOrder createOrder, String username,
                                                       BigDecimal orderCode) throws Exception {
        AppUser appUser = appUserRepository.findByPhone(username);
        SvcOrder checkPointOrder = orderRepository.findByOrderCodeAndShopIdAndStatus(orderCode, BigDecimal.valueOf(appUser.getId()), 900);
        if (checkPointOrder == null)
            return new BaseResponse(614);
//        CreateOrderDetail updateDeliveryPoint = createOrder.getDeliveryPoint().get(0);
//        CreateOrderReceiver updateCreateOrderReceiver = updateDeliveryPoint.getReceivers().get(0);
        BaseResponse calculateFee = postageService.calculateFee(createOrder, appUser);
        if (calculateFee.getStatus() != 200)
            return calculateFee;
        CalculateFeeOrderResponse calculateFeeData = (CalculateFeeOrderResponse) calculateFee.getData();
        V2Package v2Package = packageRepository.findByCode(createOrder.getPackCode());
        if (v2Package == null)
            return new BaseResponse(706);
        int isFree = createOrder.getDeliveryPoint().get(0).getReceivers().get(0).getIsFree();
        int isPartial = createOrder.getDeliveryPoint().get(0).getReceivers().get(0).getPartialDelivery();
        int priceSettingId = calculateFeeData.getData().getDeliveryPoint()
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
        if (createOrder.getPaymentType() == 2) {
            CalculateFeeResponseAPI totalFee = postageService.buildTotalFee(calculateFeeData);
            UserBalanceResponse userInfo = userWalletService.getBalances(appUser.getPhone());
            if (totalFee.getTotalFee().compareTo(userInfo.getAvailableHold()) > 0)
                return new BaseResponse(616);
        }
        //Tạo check point
        List<SvcOrderDetail> checkPointOrderDetails = orderDetailRepository.getValidBySvcOrderId(checkPointOrder.getOrderCode());
        String channel = checkPointOrderDetails.get(0).getChannel();
        List<DetailSellingFee> checkPointSellingFees = new ArrayList<>();
        List<DetailProduct> checkPointDetailProducts = new ArrayList<>();
        for (SvcOrderDetail checkPointOrderDetail : checkPointOrderDetails) {
            List<DetailSellingFee> sellingFees = detailSellingFeeRepository.findAllByOrderDetailCode(checkPointOrderDetail.getSvcOrderDetailCode());
            checkPointSellingFees.addAll(sellingFees);
            List<DetailProduct> detailProducts = productRepository.getProductsByOrderCode(String.valueOf(checkPointOrderDetail.getSvcOrderDetailCode()));
            checkPointDetailProducts.addAll(detailProducts);
            //Xoa het don cu
            detailSellingFeeRepository.deleteSellingFeeByDetailCode(checkPointOrderDetail.getSvcOrderDetailCode());
            productRepository.deleteProductByDetailCode(checkPointOrderDetail.getSvcOrderDetailCode());
            orderDetailRepository.delete(checkPointOrderDetail);
        }
        orderRepository.delete(checkPointOrder);
        //Tao don moi
        SvcOrder updatedOrder = createOrder(createOrder, orderCode, BigDecimal.valueOf(appUser.getId()), v2Package, priceSettingId, OrderStatusEnum.CONFIRMED.code);
        List<SvcOrderDetail> updatedOrderDetails = createOrderDetail(createOrder, updatedOrder, v2Package, appUser, calculateFeeData, priceSettingId, channel);
        //Save
        SvcOrder svcOrder = orderRepository.save(updatedOrder);
        updatedOrderDetails.forEach(d -> {
            d.setSvcOrderId(svcOrder.getOrderCode());
        });
        List<SvcOrderDetail> svcOrderDetails = orderDetailRepository.saveAll(updatedOrderDetails);
        logger.info("======SVC ORDER SAVE TO DB=======" + gson.toJson(svcOrder));
        logger.info("======SVC ORDER DETAIL SAVE TO DB=======" + gson.toJson(svcOrderDetails));
        orderLogService.insertOrderLog(svcOrderDetails, OrderStatusEnum.NOTE_CREATED.message, OrderStatusEnum.CONFIRMED.code);
        handleHoldBalance(svcOrderDetails, appUser);
        BaseResponse baseResponse = new BaseResponse(500, createOrder);
        //Đẩy đơn sang NVC
        if (svcOrder.getType() == 1) {
            //Nếu cấu hình đặc biệt có hola => chỉ đẩy cho Hola và end luôn
            List<ForWardConfigOM> holaSpecialConfig = configIMS.stream()
                    .filter(c -> c.getCode().equalsIgnoreCase("HLS") || c.getCode().equalsIgnoreCase("HOLA"))
                    .collect(Collectors.toList());
            if (holaSpecialConfig.size() > 0) {
                baseResponse = findShipService.handleFindShip(svcOrder, svcOrderDetails, appUser);
                if (baseResponse.getStatus() == 612 || baseResponse.getStatus() == 600)
                    return new BaseResponse(707, svcOrder);
                else return baseResponse;
            }
            //Tìm cấu hình không đặc biệt
            if (configIMS.size() == 0) {
                configIMS = orderDAO.getTargetConfig(priceSettingId);
                List<ForWardConfigOM> holaConfig = configIMS.stream()
                        .filter(c -> c.getCode().equalsIgnoreCase("HLS") || c.getCode().equalsIgnoreCase("HOLA"))
                        .collect(Collectors.toList());
                if (holaConfig.size() > 0) {
                    baseResponse = findShipService.handleFindShip(svcOrder, svcOrderDetails, appUser);
                    if (baseResponse.getStatus() == 612 || baseResponse.getStatus() == 600)
                        return new BaseResponse(707, svcOrder);
                    else return baseResponse;
                }
            }
            //Còn lại sẽ là đơn NVc khác và là đơn 1 1
            HashMap<String, Object> pushRequest = new HashMap<>();
            pushRequest.put("configs", gson.toJson(configIMS));
            pushRequest.put("orderDetail", gson.toJson(svcOrderDetails.get(0)));
            boolean pushed = CallRedis.pushQueue("PUSH_SP_ORDER", pushRequest.toString(), null);
            if (pushed)
                return new BaseResponse(707, svcOrder);

        }
        //Tìm shipper nếu là đơn km
        if (svcOrder.getType() == 2) {
//                        List<PushKmOrderDetailRequest> detailRequests = buildPushDetailRequest(orderDetails);
            try {
                baseResponse = findShipService.handleFindShip(svcOrder, svcOrderDetails, appUser);
                if (baseResponse.getStatus() == 612 || baseResponse.getStatus() == 600)
                    return new BaseResponse(707, svcOrder);
            } catch (Exception e) {
                logger.info("=====FIND SHIP EXCEPTION======" + gson.toJson(createOrder), e);
            }
        }
        return baseResponse;
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

    private SvcOrder createOrder(CreateOrder createOrder, BigDecimal orderCode,
                                 BigDecimal appUserId, V2Package v2Package, int priceSettingId, int status) {
        SvcOrder order = new SvcOrder();
        order.setOrderCode(orderCode);
        order.setShopId(appUserId);
        //TODO SERVICE PACK ID => TYPE
        order.setServicePackId(v2Package.getId());
        order.setType(v2Package.getPriceSettingType());
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
        else order.setShopOrderId(String.valueOf(orderCode));
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
                    if (!createOrderReceiver.getExpectDate().isEmpty() && !createOrderReceiver.getExpectTime().isEmpty()) {
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

//    public BaseResponse handleUpdateWaitToConfirmOrder(CreateOrder createOrder, String username,
//                                                       BigDecimal orderCode) throws Exception {
//        AppUser appUser = appUserRepository.findByPhone(username);
//        SvcOrder oldOrder = orderRepository.findByOrderCodeAndShopId(orderCode, BigDecimal.valueOf(appUser.getId()));
//        if (oldOrder == null)
//            return new BaseResponse(614);
////        CreateOrderDetail updateDeliveryPoint = createOrder.getDeliveryPoint().get(0);
////        CreateOrderReceiver updateCreateOrderReceiver = updateDeliveryPoint.getReceivers().get(0);
//        BaseResponse calculateFee = postageService.calculateFee(createOrder, appUser);
//        if (calculateFee.getStatus() != 200)
//            return calculateFee;
//        CalculateFeeOrderResponse calculateFeeData = (CalculateFeeOrderResponse) calculateFee.getData();
//        V2Package v2Package = packageRepository.findByCode(createOrder.getPackCode());
//        if (v2Package == null)
//            return new BaseResponse(706);
//        int isFree = createOrder.getDeliveryPoint().get(0).getReceivers().get(0).getIsFree();
//        int isPartial = createOrder.getDeliveryPoint().get(0).getReceivers().get(0).getPartialDelivery();
//        int priceSettingId = calculateFeeData.getData().getDeliveryPoint()
//                .get(0).getReceivers().get(0).getPriceSettingId();
//        int paymentType = createOrder.getPaymentType();
//        List<ForWardConfigOM> configIMS = new ArrayList<>();
//        //CHECK SO DU danh cho thanh toan vi hola
//        if (createOrder.getPaymentType() == 2) {
//            CalculateFeeResponseAPI totalFee = postageService.buildTotalFee(calculateFeeData);
//            UserBalanceResponse userInfo = userWalletService.getBalances(appUser.getPhone());
//            if (totalFee.getTotalFee().compareTo(userInfo.getAvailableHold()) > 0)
//                return new BaseResponse(616);
//        }
//        //Cập nhật đơn
//        List<SvcOrderDetail> updatedOrderDetails = new ArrayList<>();
//        SvcOrder updatedOrder = updateOrder(createOrder, oldOrder, BigDecimal.valueOf(appUser.getId()), v2Package, priceSettingId);
//        for (int i = 0; i < createOrder.getDeliveryPoint().size(); i++) {
//            CreateOrderDetail deliveryPoint = createOrder.getDeliveryPoint().get(i);
//            CalculateFeeDeliveryPoint calculateFeeDeliveryPoint = calculateFeeData.getData().getDeliveryPoint().get(i);
//            for (int j = 0; j < deliveryPoint.getReceivers().size(); j++) {
//                CreateOrderReceiver receiver = deliveryPoint.getReceivers().get(j);
//                CalculateFeeReceivers calculateFeeReceivers = calculateFeeDeliveryPoint.getReceivers().get(j);
//                SvcOrderDetail oldOrderDetail = orderDetailRepository
//                        .findBySvcOrderDetailCodeAndShopIdAndStatus(receiver.getOrderDetailCode(), appUser.getId(), OrderStatusEnum.WAIT_TO_CONFIRM.code);
//                if (oldOrderDetail == null)
//                    continue;
//                //Tìm đơn hàng đã được tính phí mới
//                updateOrderDetail(createOrder, deliveryPoint, receiver, oldOrder, oldOrderDetail, v2Package, calculateFeeReceivers);
//                //Update product
//                List<DetailProduct> oldProducts = productRepository.getProductsByOrderCode(String.valueOf(oldOrderDetail.getSvcOrderDetailCode()));
//                productService.updateProducts(oldProducts, receiver, oldOrderDetail, true);
//                //insert fee
//                feeService.createSellingFee(oldOrderDetail.getSvcOrderDetailCode(), calculateFeeReceivers);
//                //Save to db
//                orderDetailRepository.save(oldOrderDetail);
//                updatedOrderDetails.add(oldOrderDetail);
//                logger.info("=======ORDER DETAIL UPDATED TO DB========" + gson.toJson(oldOrderDetail));
//            }
//        }
//        //Kiểm tra đơn đặc biệt để thông báo cho khách hàng
//        if (v2Package.getPriceSettingType() == 1
//                && (isFree == 2 || isPartial == 1)) {
//            configIMS = orderDAO.getTargetConfig(v2Package.getCode(), priceSettingId, isPartial, isFree, paymentType);
//            if (configIMS.size() == 0) {
//                List<String> message = new ArrayList<>();
//                StringBuilder note = new StringBuilder(OrderStatusEnum.PRE_CONFIRM_FAIL.message);
//                if (isFree == 2) {
//                    message.add(new BaseResponse(610).getMessage());
//                    note.append(OrderStatusEnum.NOTE_ORDER_PAYMENT.message);
//                    note.append(",");
//                }
//                if (isPartial == 1) {
//                    note.append(OrderStatusEnum.NOTE_PARTIAL_TYPE.message);
//                    note.append(",");
//                    message.add(new BaseResponse(615).getMessage());
//                }
//                if (paymentType == 1) {
//                    note.append(OrderStatusEnum.NOTE_PAYMENT_TYPE.message);
//                    note.append(",");
//                    message.add(new BaseResponse(617).getMessage());
//                }
//                return new BaseResponse(700, note.substring(0, note.length() - 1), 1);
//            }
//        }
//        //Nếu đơn đặc biệt và có Hola thì chỉ đẩy cho hola và end
//        List<ForWardConfigOM> holaSpecialConfig = configIMS.stream()
//                .filter(c -> c.getCode().equalsIgnoreCase("HLS") || c.getCode().equalsIgnoreCase("HOLA"))
//                .collect(Collectors.toList());
//        if (holaSpecialConfig.size() > 0) {
//            BaseResponse baseResponse = findShipService.handleFindShip(updatedOrder, updatedOrderDetails, appUser);
//            if (baseResponse.getStatus() == 612)
//                return new BaseResponse(707);
//        }
//        //Tìm cấu hình không đặc biệt
//        if (configIMS.size() == 0) {
//            configIMS = orderDAO.getTargetConfig(priceSettingId);
//            List<ForWardConfigOM> holaConfig = configIMS.stream()
//                    .filter(c -> c.getCode().equalsIgnoreCase("HLS") || c.getCode().equalsIgnoreCase("HOLA"))
//                    .collect(Collectors.toList());
//            if (holaConfig.size() > 0) {
//                BaseResponse baseResponse = findShipService.handleFindShip(updatedOrder, updatedOrderDetails, appUser);
//                if (baseResponse.getStatus() == 612)
//                    return new BaseResponse(707);
//            }
//        }
//        //Còn lại sẽ là đơn NVC khác => đơn 1 1
//        HashMap<String, Object> pushRequest = new HashMap<>();
//        pushRequest.put("configs", gson.toJson(configIMS));
//        pushRequest.put("orderDetail", gson.toJson(updatedOrderDetails.get(0)));
//        CallRedis.pushQueue("PUSH_SP_ORDER", pushRequest.toString(), null);
//        return new BaseResponse(707, pushRequest);
//    }

    private SvcOrder updateOrder(CreateOrder createOrder, SvcOrder oldOrder,
                                 BigDecimal appUserId, V2Package v2Package, int priceSettingId) {
        oldOrder.setOrderCode(oldOrder.getOrderCode());
        oldOrder.setShopId(appUserId);
        //TODO SERVICE PACK ID => TYPE
        oldOrder.setServicePackId(v2Package.getId());
        oldOrder.setType(v2Package.getPriceSettingType());
        //No setting service pack(wrong for order)
        oldOrder.setServicePackSettingId(BigDecimal.valueOf(priceSettingId));
        oldOrder.setShopAddressId(createOrder.getPickupAddressId());
        //1 la NVC toi lay hang 0 la mang hang ra buu cuc
        oldOrder.setStatus(OrderStatusEnum.CONFIRMED.code);
        oldOrder.setPaymentType(createOrder.getPaymentType());
        oldOrder.setShopOrderId(String.valueOf(oldOrder.getOrderCode()));
        oldOrder.setPickupType(createOrder.getPickupType());
        //TODO check these values
        oldOrder.setTotalAddressDilivery((long) createOrder.getDeliveryPoint().size());
        oldOrder.setTotalDistance(0L);
        //Total details
        AtomicInteger totalDetail = new AtomicInteger();
        createOrder.getDeliveryPoint().forEach(o -> {
            o.getReceivers().forEach(r -> {
                totalDetail.addAndGet(1);
            });
        });
        oldOrder.setTotalOrderDetail(totalDetail.intValue());
        oldOrder.setExpectShipId(0L);
        orderRepository.save(oldOrder);
        return oldOrder;
    }


    private SvcOrderDetail updateOrderDetail(CreateOrder createOrder, CreateOrderDetail updateDeliveryPoint,
                                             CreateOrderReceiver updateCreateOrderReceiver,
                                             SvcOrder oldOrder, SvcOrderDetail oldOrderDetail,
                                             V2Package v2Package, CalculateFeeReceivers calculateFeeReceivers) {
        //Tao diem nhan hang
        AddressDelivery addressDelivery = null;
        try {
            UserInfoAddressData addressData = PreLoadStaticUtil.addresses.get(updateDeliveryPoint.getWard());
            AddressDeliveryRequest addressDeliveryRequest = new AddressDeliveryRequest(updateDeliveryPoint.getAddress(), addressData.getProvinceName(),
                    addressData.getProvinceCode(), addressData.getDistrictName(), addressData.getDistrictCode(), addressData.getWardName(), addressData.getWardCode());
            BaseResponse deliveryResponse = addressDeliveryService.createAddressDelivery(addressDeliveryRequest);
            if (deliveryResponse != null && deliveryResponse.getStatus() == 200) {
                addressDelivery = (AddressDelivery) deliveryResponse.getData();
            }
        } catch (Exception e) {
            logger.info("=======CREATE ORDER ADDRESS DELIVERY EXCEPTION======" + gson.toJson(updateCreateOrderReceiver), e);
            return oldOrderDetail;
        }
        if (addressDelivery != null) {
            //Code
            oldOrderDetail.setSvcOrderDetailCode(oldOrderDetail.getSvcOrderDetailCode());
            oldOrderDetail.setShopId(oldOrderDetail.getShopId());

            oldOrderDetail.setSvcOrderId(oldOrderDetail.getSvcOrderId());
            oldOrderDetail.setAddressDeliveryId(addressDelivery.getId());
            oldOrderDetail.setServicePackId(v2Package.getId().intValue());
            oldOrderDetail.setServicePackSettingId(calculateFeeReceivers.getPriceSettingId());
            //receiver
            oldOrderDetail.setConsignee(updateCreateOrderReceiver.getName());
            oldOrderDetail.setPhone(updateCreateOrderReceiver.getPhone());
            //Thong so
            oldOrderDetail.setWeight(updateCreateOrderReceiver.getWeight());
            oldOrderDetail.setLength(updateCreateOrderReceiver.getLength());
            oldOrderDetail.setWidth(updateCreateOrderReceiver.getWidth());
            oldOrderDetail.setHeight(updateCreateOrderReceiver.getHeight());
            //Others
            oldOrderDetail.setIsPartDelivery(updateCreateOrderReceiver.getPartialDelivery());
            oldOrderDetail.setIsRefund(updateCreateOrderReceiver.getIsRefund());
            oldOrderDetail.setIsPorter(updateCreateOrderReceiver.getExtraServices().get(0).getIsPorter());
            oldOrderDetail.setIsDoorDelivery(updateCreateOrderReceiver.getExtraServices().get(0).getIsDoorDeliver());
            oldOrderDetail.setPickType(oldOrder.getPickupType());
            oldOrderDetail.setPaymentType(oldOrder.getPaymentType());
            //TODO check declare product
            oldOrderDetail.setRequiredNote(updateCreateOrderReceiver.getRequireNote());
            oldOrderDetail.setNote(updateCreateOrderReceiver.getNote());
            oldOrderDetail.setIsFree(updateCreateOrderReceiver.getIsFree());
            //Status
            oldOrderDetail.setStatus(oldOrder.getStatus());
            oldOrderDetail.setOldStatus(oldOrder.getStatus());
            //Shop order id
            if (createOrder.getShopOrderCode() != null)
                oldOrderDetail.setShopOrderId(createOrder.getShopOrderCode());
            else oldOrderDetail.setShopOrderId(String.valueOf(oldOrderDetail.getSvcOrderDetailCode()));
            oldOrderDetail.setShopAddressId(oldOrder.getShopAddressId());
            //Date
            //TODO no expect pick date in front end
            oldOrderDetail.setExpectPickDate(null);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String expectDate = updateCreateOrderReceiver.getExpectDate().replaceAll(" ", "")
                    + " "
                    + updateCreateOrderReceiver.getExpectTime().replaceAll(" ", "");
            try {
                oldOrderDetail.setExpectDeliverDate(new Timestamp(dateFormat.parse(expectDate).getTime()));
            } catch (ParseException e) {
                logger.info("========CREATE ORDER DETAIL PARSE DATE EXCEPTION======" + oldOrderDetail.getSvcOrderDetailCode()
                        + "||" + e.getMessage());
                oldOrderDetail.setExpectDeliverDate(null);
            }
            //Gói Km
            if (v2Package.getPriceSettingType() == 2) {
                List<LeadtimeForwardConfig> forwardConfigs = forwardConfigRepository
                        .findAllByPriceSettingIdAndIsDeleteAndPackIdOrderByRankAsc(calculateFeeReceivers.getPriceSettingId(), 1,
                                v2Package.getId().intValue());
                if (forwardConfigs != null && forwardConfigs.size() > 0) {
                    oldOrderDetail.setCarrierId(forwardConfigs.get(0).getCarrierId().longValue());
                    oldOrderDetail.setCarrierServiceCode(forwardConfigs.get(0).getCarrierPackCode());
                }
            }
        }
        return oldOrderDetail;
    }


}
