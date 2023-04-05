package com.imedia.service.order;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.config.application.AppConfig;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.dao.OrderDAO;
import com.imedia.oracle.dao.PickupAddressDAO;
import com.imedia.oracle.entity.*;
import com.imedia.oracle.repository.*;
import com.imedia.service.gateway.GatewayService;
import com.imedia.service.gateway.model.DataObject;
import com.imedia.service.gateway.model.ResponseObject;
import com.imedia.service.notify.NotifyService;
import com.imedia.service.order.dto.FilterOrderDTO;
import com.imedia.service.order.enums.OrderStatusEnum;
import com.imedia.service.order.enums.SellingFeeEnum;
import com.imedia.service.order.model.*;
import com.imedia.service.pickupaddress.model.ShopAddressDTO;
import com.imedia.service.postage.FeeService;
import com.imedia.service.postage.PostageService;
import com.imedia.service.postage.model.CalculateFeeDeliveryPoint;
import com.imedia.service.postage.model.CalculateFeeOrderResponse;
import com.imedia.service.postage.model.CalculateFeeReceivers;
import com.imedia.service.product.model.ProductReportOM;
import com.imedia.service.user.model.UserInfoAddressData;
import com.imedia.service.wallet.WalletService;
import com.imedia.util.AppUtil;
import com.imedia.util.CallRedis;
import com.imedia.util.PreLoadStaticUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderActionService {
    static final Logger logger = LogManager.getLogger(OrderActionService.class);
    static final Gson gson = new Gson();
    static final Gson gson2 = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private final AppUserRepository appUserRepository;
    private final WalletService walletService;
    private final SvcOrderDetailRepository orderDetailRepository;
    private final WalletLogRepository walletLogRepository;
    private final CarrierRepository carrierRepository;
    private final FeeService feeService;
    private final OrderLogService orderLogService;
    private final OrderDetailRepository shipOrderDetailRepository;
    private final NotifyService notifyService;
    private final GatewayService gatewayService;
    private final SvcOrderRepository orderRepository;
    private final OrderService orderService;
    private final OrderReadService orderReadService;
    private final V2PackageRepository packageRepository;
    private final AddressDeliveryRepository addressDeliveryRepository;
    private final DetailProductRepository productRepository;
    private final PostageService postageService;
    private final OrderPartialRequestRepository partialRequestRepository;
    private final OrderDAO orderDAO;
    private final MapperFacade mapperFacade;
    private final DetailSellingFeeRepository sellingFeeRepository;
    static final List<Integer> specialRefund = Arrays.asList(200, 2002, 2003, 20051, 2011);
    private final PickupAddressDAO pickupAddressDAO;
    private final ShopAddressRepository shopAddressRepository;

    //static final List<String>
    @Autowired
    public OrderActionService(AppUserRepository appUserRepository, WalletService walletService, SvcOrderDetailRepository orderDetailRepository, WalletLogRepository walletLogRepository, CarrierRepository carrierRepository, FeeService feeService, OrderLogService orderLogService, OrderDetailRepository shipOrderDetailRepository, NotifyService notifyService, GatewayService gatewayService, SvcOrderRepository orderRepository, OrderService orderService, OrderReadService orderReadService, V2PackageRepository packageRepository, AddressDeliveryRepository addressDeliveryRepository, DetailProductRepository productRepository, PostageService postageService, OrderPartialRequestRepository partialRequestRepository, OrderDAO orderDAO, MapperFacade mapperFacade, DetailSellingFeeRepository sellingFeeRepository, PickupAddressDAO pickupAddressDAO, ShopAddressRepository shopAddressRepository) {
        this.appUserRepository = appUserRepository;
        this.walletService = walletService;
        this.orderDetailRepository = orderDetailRepository;
        this.walletLogRepository = walletLogRepository;
        this.carrierRepository = carrierRepository;
        this.feeService = feeService;
        this.orderLogService = orderLogService;
        this.shipOrderDetailRepository = shipOrderDetailRepository;
        this.notifyService = notifyService;
        this.gatewayService = gatewayService;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.orderReadService = orderReadService;
        this.packageRepository = packageRepository;
        this.addressDeliveryRepository = addressDeliveryRepository;
        this.productRepository = productRepository;
        this.postageService = postageService;
        this.partialRequestRepository = partialRequestRepository;
        this.orderDAO = orderDAO;
        this.mapperFacade = mapperFacade;
        this.sellingFeeRepository = sellingFeeRepository;
        this.pickupAddressDAO = pickupAddressDAO;
        this.shopAddressRepository = shopAddressRepository;
    }


    public BaseResponse reDeliveryOrder(BigDecimal orderDetailCode) throws Exception {
//        List<String> orderDetailCodes = Arrays.asList(request.getOrderCodes().split(","));
//        for (String orderDetailCodeS : orderDetailCodes) {
//            try {
//                BigDecimal orderDetailCode = BigDecimal.valueOf(Long.parseLong(orderDetailCodeS));
        SvcOrderDetail orderDetail = orderDetailRepository.getValidBySvcOrderDetailCode(orderDetailCode);
        if (orderDetail == null)
            return new BaseResponse(614);
        Carrier carrier = carrierRepository.findCarrierById(orderDetail.getCarrierId());
        if (carrier.getSpecialServices() != null && !carrier.getSpecialServices().contains("4"))
            return new BaseResponse(751);
        if (!AppConfig.getInstance().reDeliveryStatus.contains(String.valueOf(orderDetail.getStatus())))
            return new BaseResponse(751);
        if (carrier.getCode().equals("HLS") || carrier.getCode().equals("HOLA")) {
            orderLogService.insertShipOrderLog(orderDetail, OrderStatusEnum.NOTE_REQ_REDELIVERY.message,
                    orderDetail.getStatus(), OrderStatusEnum.RE_DELIVERING.code);
            shipOrderDetailRepository.updateOrderDetailStatus(OrderStatusEnum.RE_DELIVERING.code, orderDetailCode);
            notifyService.notifyRedeliveryAppShip(orderDetail);
            orderLogService.insertOrderLog(orderDetail, OrderStatusEnum.NOTE_REQ_REDELIVERY.message, OrderStatusEnum.RE_DELIVERING.code);
            orderDetail.setOldStatus(orderDetail.getStatus());
            orderDetail.setStatus(OrderStatusEnum.RE_DELIVERING.code);
            orderDetailRepository.save(orderDetail);
            feeService.insertReDeliverySpecificFee(orderDetail);
            if (orderDetail.getOldStatus().equals(OrderStatusEnum.RETURN_STORED.code)) {
                orderLogService.insertShipOrderLog(orderDetail, OrderStatusEnum.NOTE_AUTO_STORED.message,
                        orderDetail.getStatus(), OrderStatusEnum.STORED_REDELIVERY.code);
                shipOrderDetailRepository.updateOrderDetailStatus(OrderStatusEnum.STORED_REDELIVERY.code,
                        orderDetailCode);
                notifyService.notifyRedeliveryAppShip(orderDetail);
                orderLogService.insertOrderLog(orderDetail, OrderStatusEnum.NOTE_AUTO_STORED.message,
                        OrderStatusEnum.STORED_REDELIVERY.code);
                orderDetail.setOldStatus(orderDetail.getStatus());
                orderDetail.setStatus(OrderStatusEnum.STORED_REDELIVERY.code);
                orderDetailRepository.save(orderDetail);
            }
            return new BaseResponse(750);
        } else {
            boolean request = gatewayService.redelivery(orderDetail, carrier);
            if (request) {
                orderLogService.insertOrderLog(orderDetail, OrderStatusEnum.RE_DELIVERING.message, OrderStatusEnum.RE_DELIVERING.code);
                orderDetail.setOldStatus(orderDetail.getStatus());
                orderDetail.setStatus(OrderStatusEnum.RE_DELIVERING.code);
                orderDetailRepository.save(orderDetail);
                feeService.insertReDeliverySpecificFee(orderDetail);
                return new BaseResponse(750);
            }
        }
        return new BaseResponse(751);
    }

    public BaseResponse cancelOrder(CancelOrderRequest cancelOrderRequest, String username) throws Exception {
        List<SvcOrderDetail> orderDetails = new ArrayList<>();
        //Hủy đơn to
        if (cancelOrderRequest.getType() == 1) {
            List<BigDecimal> orderCodes = new ArrayList<>();
            Arrays.asList(cancelOrderRequest.getOrderCode().split(",")).forEach(c -> orderCodes.add(BigDecimal.valueOf(Long.parseLong(c))));
            orderDetails = orderDetailRepository.getAllOrderDetailByOrderCodesWithPhone(orderCodes, username);
            if (orderDetails.size() == 0)
                return new BaseResponse(614);
            //Check nếu là hủy batch thì sẽ handle hàm khác
            if (orderCodes.size() > 1) {
                return cancelBatchOrder(orderDetails, username);
            }
        }
        //Hủy đơn nhỏ
        if (cancelOrderRequest.getType() == 2) {
            List<BigDecimal> orderCodes = new ArrayList<>();
            Arrays.asList(cancelOrderRequest.getOrderCode().split(",")).forEach(c -> orderCodes.add(BigDecimal.valueOf(Long.parseLong(c))));
            orderDetails = orderDetailRepository.getAllOrderDetailByCodeWithPhone(orderCodes, username);
            if (orderDetails.size() == 0)
                return new BaseResponse(614);
            //Check nếu là hủy batch thì sẽ handle hàm khác
            if (orderCodes.size() > 1) {
                return cancelBatchOrder(orderDetails, username);
            }
        }
        //Hủy toàn bộ đơn theo trạng thái
        if (cancelOrderRequest.getType() == 3) {
            List<Integer> statuses = Arrays.stream(AppConfig.getInstance().targetStatuses.get(cancelOrderRequest.getGroupStatus())
                    .split(",")).map(Integer::parseInt).collect(Collectors.toList());
            orderDetails = orderDetailRepository.getAllOrderDetailByPhoneAndGroupStatus(username, statuses);
            if (orderDetails.size() == 0)
                return new BaseResponse(614);
            return cancelBatchOrder(orderDetails, username);
        }
        return cancelSingleOrder(orderDetails, username);

    }

    private BaseResponse cancelSingleOrder(List<SvcOrderDetail> orderDetails, String username) throws Exception {
        if (orderDetails.size() == 0)
            return new BaseResponse(614);
        //Check status
        for (SvcOrderDetail orderDetail : orderDetails) {
            if (!AppConfig.getInstance().cancelStatus.contains(String.valueOf(orderDetail.getStatus())))
                return new BaseResponse(761);
        }
        AppUser appUser = appUserRepository.findByPhone(username);
        for (SvcOrderDetail orderDetail : orderDetails) {
            try {
                Carrier carrier = carrierRepository.findCarrierById(orderDetail.getCarrierId());
                logger.info("===========CANCELING ORDER DETAIL===========" + orderDetail.getSvcOrderDetailCode());
                //Nếu là Hola => có thể là đơn nhiều đơn nhỏ
                if (carrier.getCode().equalsIgnoreCase("HLS") || carrier.getCode().equalsIgnoreCase("HOLA")) {
                    notifyService.notifyCancelAppShip(orderDetail);
                    shipOrderDetailRepository.updateOrderDetailStatus(OrderStatusEnum.CANCEL_SHOP.code, orderDetail.getSvcOrderDetailCode());
                    orderLogService.insertShipOrderLog(orderDetail, OrderStatusEnum.CANCEL_SHOP.message,
                            orderDetail.getStatus(), OrderStatusEnum.CANCEL_SHOP.code);
                    orderLogService.insertOrderLog(orderDetail, OrderStatusEnum.CANCEL_SHOP.message, OrderStatusEnum.CANCEL_SHOP.code);
                    orderDetail.setStatus(OrderStatusEnum.CANCEL_SHOP.code);
                    orderDetailRepository.save(orderDetail);
                    handHoldBalance(orderDetail, appUser);
//                    List<Integer> statuses = AppConfig.getInstance().cancelStatus.stream().map(Integer::parseInt).collect(Collectors.toList());
//                    orderService.updateOrderStatus(orderDetail, statuses);
                    //Tính lại giá các đươn còn lại
                    calculateFeeAndUpdateForOtherOrder(orderDetail);
                    if (orderDetails.size() == 1) {
                        return new BaseResponse(760);
                    }
                }
                //NVC khac => chắc chắn là đơn 1 => return lỗi luôn
                else {
                    boolean isCanceled = gatewayService.cancelOrder(orderDetail, carrier);
                    if (isCanceled) {
                        orderLogService.insertOrderLog(orderDetail, OrderStatusEnum.CANCEL_SHOP.message, OrderStatusEnum.CANCEL_SHOP.code);
                        orderDetail.setStatus(OrderStatusEnum.CANCEL_SHOP.code);
                        orderDetailRepository.save(orderDetail);
                        handHoldBalance(orderDetail, appUser);
                        orderRepository.updateStatus(OrderStatusEnum.CANCEL_SHOP.code, orderDetail.getSvcOrderId());
                        return new BaseResponse(760);
                    } else return new BaseResponse(762);
                }
            } catch (Exception e) {
                logger.info("======CANCEL EXCEPTION======" + orderDetail.getSvcOrderDetailCode(), e);
                return new BaseResponse(500);
            }
        }
        return new BaseResponse(760);
    }

    private BaseResponse cancelBatchOrder(List<SvcOrderDetail> orderDetails, String username) throws Exception {
        AppUser appUser = appUserRepository.findByPhone(username);
        BatchOrderActionResponse response = new BatchOrderActionResponse();
        response.setTotalRequest(orderDetails.size());
        for (SvcOrderDetail orderDetail : orderDetails) {
            if (!AppConfig.getInstance().cancelStatus.contains(String.valueOf(orderDetail.getStatus()))) {
                response.setTotalFailed(response.getTotalFailed() + 1);
                continue;
            }
            try {
                Carrier carrier = carrierRepository.findCarrierById(orderDetail.getCarrierId());
                logger.info("===========CANCELING ORDER DETAIL===========" + orderDetail.getSvcOrderDetailCode());
                //Nếu là Hola => có thể là đơn nhiều đơn nhỏ
                if (carrier.getCode().equalsIgnoreCase("HLS") || carrier.getCode().equalsIgnoreCase("HOLA")) {
                    notifyService.notifyCancelAppShip(orderDetail);
                    shipOrderDetailRepository.updateOrderDetailStatus(OrderStatusEnum.CANCEL_SHOP.code, orderDetail.getSvcOrderDetailCode());
                    orderLogService.insertShipOrderLog(orderDetail, OrderStatusEnum.CANCEL_SHOP.message,
                            orderDetail.getStatus(), OrderStatusEnum.CANCEL_SHOP.code);
                    orderLogService.insertOrderLog(orderDetail, OrderStatusEnum.CANCEL_SHOP.message, OrderStatusEnum.CANCEL_SHOP.code);
                    orderDetail.setStatus(OrderStatusEnum.CANCEL_SHOP.code);
                    orderDetailRepository.save(orderDetail);
                    handHoldBalance(orderDetail, appUser);
//                    List<Integer> statuses = AppConfig.getInstance().cancelStatus.stream().map(Integer::parseInt).collect(Collectors.toList());
//                    orderService.updateOrderStatus(orderDetail, statuses);
                    //Tính lại giá các đươn còn lại
                    calculateFeeAndUpdateForOtherOrder(orderDetail);
                    response.setTotalSuccess(response.getTotalSuccess() + 1);
                }
                //NVC khac => chắc chắn là đơn 1 1
                else {
                    boolean isCanceled = gatewayService.cancelOrder(orderDetail, carrier);
                    if (isCanceled) {
                        handHoldBalance(orderDetail, appUser);
                        orderLogService.insertOrderLog(orderDetail, OrderStatusEnum.CANCEL_SHOP.message, OrderStatusEnum.CANCEL_SHOP.code);
                        orderDetail.setStatus(OrderStatusEnum.CANCEL_SHOP.code);
                        orderDetailRepository.save(orderDetail);
                        orderRepository.updateStatus(OrderStatusEnum.CANCEL_SHOP.code, orderDetail.getSvcOrderId());
                        response.setTotalSuccess(response.getTotalSuccess() + 1);
                    } else response.setTotalFailed(response.getTotalFailed() + 1);
                }
            } catch (Exception e) {
                logger.info("======CANCEL EXCEPTION======" + orderDetail.getSvcOrderDetailCode(), e);
                response.setTotalFailed(response.getTotalFailed() + 1);
            }
        }
        if (response.getTotalRequest() == response.getTotalFailed()) {
            return new BaseResponse(761, response);
        } else return new BaseResponse(760, response);
    }

    public BaseResponse refundOrder(BigDecimal orderDetailCode, String username) throws Exception {
        SvcOrderDetail orderDetail = orderDetailRepository.getOrderDetailByCodeWithPhone(orderDetailCode, username);
        if (orderDetail == null)
            return new BaseResponse(614);
        Carrier carrier = carrierRepository.findCarrierById(orderDetail.getCarrierId());
        if (carrier.getSpecialServices() != null && !carrier.getSpecialServices().contains("5"))
            return new BaseResponse(771);
        if (!AppConfig.getInstance().refundRequestStatus.contains(String.valueOf(orderDetail.getStatus())))
            return new BaseResponse(771);
        try {
            if (carrier.getCode().equals("HLS") || carrier.getCode().equals("HOLA")) {
                shipOrderDetailRepository.updateOrderDetailStatus(OrderStatusEnum.RETURN_REQ.code, orderDetail.getSvcOrderDetailCode());
                orderLogService.insertShipOrderLog(orderDetail, OrderStatusEnum.NOTE_RETURN_REQ.message,
                        orderDetail.getStatus(), OrderStatusEnum.RETURN_REQ.code);
                orderLogService.insertOrderLog(orderDetail, OrderStatusEnum.NOTE_RETURN_REQ.message, OrderStatusEnum.RETURN_REQ.code);
                orderDetail.setOldStatus(orderDetail.getStatus());
                orderDetail.setStatus(OrderStatusEnum.RETURN_REQ.code);
                orderDetailRepository.save(orderDetail);
                if (specialRefund.contains(orderDetail.getOldStatus())) {
                    shipOrderDetailRepository.updateOrderDetailStatus(OrderStatusEnum.RETURNING_STORED.code, orderDetail.getSvcOrderDetailCode());
                    orderLogService.insertShipOrderLog(orderDetail, OrderStatusEnum.NOTE_STORED_RETURN_REQ.message,
                            orderDetail.getStatus(), OrderStatusEnum.RETURNING_STORED.code);
                    orderLogService.insertOrderLog(orderDetail,
                            OrderStatusEnum.NOTE_STORED_RETURN_REQ.message,
                            OrderStatusEnum.RETURNING_STORED.code);
                    orderDetail.setOldStatus(orderDetail.getStatus());
                    orderDetail.setStatus(OrderStatusEnum.RETURNING_STORED.code);
                    orderDetailRepository.save(orderDetail);
                }
                return new BaseResponse(770);
            } else {
                boolean request = gatewayService.refund(orderDetail, carrier);
                if (request) {
                    orderLogService.insertOrderLog(orderDetail, OrderStatusEnum.RETURN_REQ.message, OrderStatusEnum.RETURN_REQ.code);
                    orderDetail.setStatus(OrderStatusEnum.RETURN_REQ.code);
                    orderDetailRepository.save(orderDetail);
                    feeService.insertReDeliverySpecificFee(orderDetail);
                    return new BaseResponse(750);
                }
            }
            return new BaseResponse(772);
        } catch (Exception e) {
            logger.info("=========REFUND REQUEST ORDER EXCEPTION=========" + orderDetailCode, e);
            return new BaseResponse(772);
        }
    }

    public BaseResponse getOrderDataToPrint(PrintOrderRequest printOrderRequest, String username) throws Exception {
        //Validate
        validatePrintOrderRequest(printOrderRequest);
        if (printOrderRequest.getType() != 1 && printOrderRequest.getType() != 2 && printOrderRequest.getType() != 3)
            return new BaseResponse(666);
        else {
            if ((printOrderRequest.getType() == 1 || printOrderRequest.getType() == 2)
                    && printOrderRequest.getOrderCodes().isEmpty())
                return new BaseResponse(666);
        }
        List<FilterOrderDTO> orderDetails = orderDAO.getOrderToPrint(printOrderRequest, username);
        List<PrintOrderResponse> printOrderResponses = new ArrayList<>();
        //Build model in don
        for (FilterOrderDTO filterOrderDTO : orderDetails) {
            Carrier carrier = carrierRepository.findCarrierById(filterOrderDTO.getCarrierId());
            if (carrier == null)
                continue;
            try {
                PrintOrderResponse printOrderResponse = mapperFacade.map(filterOrderDTO, PrintOrderResponse.class);
                printOrderResponse.setOrderCode(filterOrderDTO.getSvcOrderDetailCode());
                printOrderResponse.setCarrierOrderCode(filterOrderDTO.getCarrierOrderId());
                printOrderResponse.setBarCodePrint(filterOrderDTO.getCarrierOrderId());
                if (carrier.getCode().equals("HLS") || carrier.getCode().equals("HOLA"))
                    printOrderResponse.setBarCodePrint(String.valueOf(filterOrderDTO.getSvcOrderDetailCode()));
                if (carrier.getCode().equals("HLS1") || carrier.getCode().equals("HOLA1"))
                    printOrderResponse.setBarCodePrint(String.valueOf(filterOrderDTO.getSvcOrderDetailCode()));
                //Shop address
                UserInfoAddressData shopAddressData = PreLoadStaticUtil.addresses.get(String.valueOf(filterOrderDTO.getShopWardCode()));
                printOrderResponse.setShopProvince(shopAddressData.getProvinceName());
                printOrderResponse.setShopDistrict(shopAddressData.getDistrictName());
                printOrderResponse.setShopWard(shopAddressData.getWardName());
                printOrderResponse.setShopProvinceCode(shopAddressData.getProvinceCode());
                printOrderResponse.setShopDistrictCode(shopAddressData.getDistrictCode());
                printOrderResponse.setShopWardCode(shopAddressData.getWardCode());
                //Receiver
                UserInfoAddressData deliveryAddressData = PreLoadStaticUtil.addresses.get(filterOrderDTO.getDeliveryWard());
                printOrderResponse.setDeliveryName(filterOrderDTO.getConsignee());
                printOrderResponse.setDeliveryPhone(filterOrderDTO.getPhone());
                printOrderResponse.setDeliveryWardCode(deliveryAddressData.getWardCode());
                printOrderResponse.setDeliveryDistrictCode(deliveryAddressData.getDistrictCode());
                printOrderResponse.setDeliveryProvinceCode(deliveryAddressData.getProvinceCode());
                printOrderResponse.setDeliveryWard(deliveryAddressData.getWardName());
                printOrderResponse.setDeliveryDistrict(deliveryAddressData.getDistrictName());
                printOrderResponse.setDeliveryProvince(deliveryAddressData.getProvinceName());
                printOrderResponse.setRequireNote(filterOrderDTO.getRequiredNote());
                List<ProductReportOM> products = productRepository.getProductsCateByOrderCode(String.valueOf(printOrderResponse.getOrderCode()));
                List<PrintOrderProduct> productResponses = mapperFacade.mapAsList(products, PrintOrderProduct.class);
                printOrderResponse.setProducts(productResponses);
                printOrderResponse.setMoneyToCollect(filterOrderDTO.getRealityCod());
                BigDecimal totalFee = sellingFeeRepository.sumTotalFee(filterOrderDTO.getSvcOrderDetailCode());
                printOrderResponse.setCod(filterOrderDTO.getRealityCod());
                if (filterOrderDTO.getIsFree() == 2) {
                    printOrderResponse.setMoneyToCollect(filterOrderDTO.getRealityCod().add(totalFee));
                    printOrderResponse.setTotalFee(totalFee);
                } else printOrderResponse.setTotalFee(BigDecimal.ZERO);
                printOrderResponses.add(printOrderResponse);
            } catch (Exception e) {
                logger.info("=====PRINT EXCEPTION====" + filterOrderDTO.getSvcOrderDetailCode(), e);
            }
        }
        return new BaseResponse(200, printOrderResponses);
    }

    public BaseResponse orderConfirmPartialRequest(ConfirmPartialRequest confirmPartialRequest, String username) {
        OrderPartialRequest partialRequest = partialRequestRepository.getConfirmPartialRequest(confirmPartialRequest.getPartialRequestId(), username);
        if (partialRequest == null)
            return new BaseResponse(781);
        logger.info("=========CONFIRM PARTIAL REQUEST=========" + gson.toJson(partialRequest) + "|| CONFIRM " + confirmPartialRequest.getConfirmed());
        AppUser appUser = appUserRepository.findByPhone(username);
        SvcOrderDetail orderDetail = orderDetailRepository.getValidBySvcOrderDetailCode(partialRequest.getOrderDetailCode());
        partialRequest.setConfirmBy(appUser.getId());
        // 1 là thành công 2 là reject
        if (confirmPartialRequest.getConfirmed() == 1) {
            partialRequest.setIsConfirmed(1);
            partialRequestRepository.save(partialRequest);
            //insert delivered log to ship
            String note = partialRequest.getNote();
            if (partialRequest.getNote() == null || partialRequest.getNote().trim().isEmpty())
                note = OrderStatusEnum.NOTE_PARTIAL_DELIVERED.message;
            OrderDetail shipOrderDetail = shipOrderDetailRepository.findByOrderDetailCode(orderDetail.getSvcOrderDetailCode());
            orderLogService.insertShipOrderLog(orderDetail, shipOrderDetail, partialRequest.getNote(), OrderStatusEnum.DELIVERED.code);
            shipOrderDetailRepository.updateOrderDetailStatus(OrderStatusEnum.DELIVERED.code, orderDetail.getSvcOrderDetailCode());
            shipOrderDetail.setStatus(OrderStatusEnum.DELIVERED.code);
            shipOrderDetail.setCod(partialRequest.getPartialCod());
            shipOrderDetailRepository.save(shipOrderDetail);
            //callback
            callbackToMaincore(partialRequest, note, orderDetail, OrderStatusEnum.DELIVERED.code);
            if (orderDetail.getIsRefund() == 1) {
                //insert 907 returning log to ship
                //Auto note other
                orderLogService.insertShipOrderLog(orderDetail, shipOrderDetail, partialRequest.getNote(), OrderStatusEnum.RETURN_REQ.code);
                shipOrderDetail.setStatus(OrderStatusEnum.RETURN_REQ.code);
                shipOrderDetailRepository.save(shipOrderDetail);
                logger.info("=====SHIP ORDER DETAIL SAVE======" + gson.toJson(shipOrderDetail));
                //callback
                callbackToMaincore(partialRequest, OrderStatusEnum.NOTE_PARTIAL_RETURNING.message, orderDetail, OrderStatusEnum.RETURN_REQ.code);
                //Notify to app ship9
                notifyService.notifyPartialRequest(partialRequest);
                return new BaseResponse(780);
            }
            //Không chuyển hoàn mà tiêu hủy vì khách k tích chuyển hoàn
            else {
                //insert 907 returning log to ship
                //Auto note other
                orderLogService.insertShipOrderLog(orderDetail, shipOrderDetail, partialRequest.getNote(), OrderStatusEnum.DESTROYED.code);
                shipOrderDetailRepository.updateOrderDetailStatus(OrderStatusEnum.DESTROYED.code, orderDetail.getSvcOrderDetailCode());
                //callback
                callbackToMaincore(partialRequest, OrderStatusEnum.NOTE_PARTIAL_DESTROYED.message, orderDetail, OrderStatusEnum.DESTROYED.code);
                //Notify to app ship
                notifyService.notifyPartialRequest(partialRequest);
                return new BaseResponse(780);
            }
        }
        //Reject
        else {
            partialRequest.setIsConfirmed(2);
            //insert svc log
            orderLogService.insertOrderLog(orderDetail, OrderStatusEnum.NOTE_PARTIAL_REJECT.message, orderDetail.getStatus());
            //Notify to app ship
            notifyService.notifyPartialRequest(partialRequest);
            partialRequestRepository.save(partialRequest);
            sellingFeeRepository.deleteSellingFeeByDetailCode(orderDetail.getSvcOrderDetailCode(), SellingFeeEnum.PARTIAL_FEE.code);
            return new BaseResponse(782);
        }
    }

    private void calculateFeeAndUpdateForOtherOrder(SvcOrderDetail canceledOrderDetail) {
        try {
            SvcOrder order = orderRepository.findByOrderCode(canceledOrderDetail.getSvcOrderId());
            List<SvcOrderDetail> remainOrderDetails = orderDetailRepository.getOtherDetailToCalculate(canceledOrderDetail.getSvcOrderDetailCode(),
                    canceledOrderDetail.getSvcOrderId());
            if (remainOrderDetails.size() == 0)
                return;
            AppUser appUser = appUserRepository.findAppUserById(order.getShopId().longValue());
            V2Package v2Package = packageRepository.findV2PackageById(BigDecimal.valueOf(canceledOrderDetail.getServicePackId()));
            CreateOrder createOrder = buildCreateOrderFromOrder(v2Package, order, remainOrderDetails);
            BaseResponse calculateFee = postageService.calculateFee(createOrder, appUser);
            if (calculateFee.getStatus() == 200) {
                CalculateFeeOrderResponse calculateFeeData = (CalculateFeeOrderResponse) calculateFee.getData();
                for (CalculateFeeDeliveryPoint deliveryPoint : calculateFeeData.getData().getDeliveryPoint()) {
                    for (CalculateFeeReceivers receiver : deliveryPoint.getReceivers()) {
                        feeService.updateSellingFee(receiver.getOrderDetailCode(), receiver);
                    }
                }
            }
        } catch (Exception e) {
            logger.info("=========CALCULATE OTHER FEE EXCEPTION=======" + canceledOrderDetail.getSvcOrderDetailCode());
        }
    }

    public CreateOrder buildCreateOrderFromOrder(V2Package v2Package, SvcOrder order, List<SvcOrderDetail> orderDetails) {
        //Tạo thoogn tin ban đầu
        CreateOrder createOrder = new CreateOrder();
        createOrder.setPackCode(v2Package.getCode());
        createOrder.setPackName(v2Package.getName());
        createOrder.setPaymentType(order.getPaymentType());
        createOrder.setPickupAddressId(order.getShopAddressId());
        createOrder.setPickupType(order.getPickupType());
        createOrder.setPackType(order.getType());
        //Shop info
        ShopAddress shopAddress = shopAddressRepository.findShopAddressById(Long.valueOf(order.getShopAddressId()));
        if (shopAddress != null) {
            ShopAddressDTO shopAddressDTO = mapperFacade.map(shopAddress, ShopAddressDTO.class);
            UserInfoAddressData shopAddressInfo = PreLoadStaticUtil.addresses.get(String.valueOf(shopAddressDTO.getWardId()));
            if (shopAddressInfo != null) {
                shopAddressDTO.setWardName(shopAddressInfo.getWardName());
                shopAddressDTO.setDistrictName(shopAddressInfo.getDistrictName());
                shopAddressDTO.setProvinceName(shopAddressInfo.getProvinceName());
            }
            createOrder.setShopAddress(shopAddressDTO);
        }
        //Gom các đơn chung điểm giao
        List<Long> distinctAddressIds = orderDetails
                .stream()
                .filter(AppUtil.distinctByKey(SvcOrderDetail::getAddressDeliveryId))
                .map(SvcOrderDetail::getAddressDeliveryId)
                .collect(Collectors.toList());
        List<CreateOrderDetail> deliveryPoints = new ArrayList<>();
        for (Long distinctAddressId : distinctAddressIds) {
            AddressDelivery addressDelivery = addressDeliveryRepository.findAddressDeliveryById(distinctAddressId);
            List<SvcOrderDetail> groupedDeliveryPoints = orderDetails
                    .stream().filter(o -> o.getAddressDeliveryId().equals(distinctAddressId))
                    .collect(Collectors.toList());
            CreateOrderDetail deliveryPoint = new CreateOrderDetail();
            UserInfoAddressData addressData = PreLoadStaticUtil.addresses.get(addressDelivery.getWardCode());
            deliveryPoint.setAddress(addressDelivery.getAddress());
            if (addressData != null) {
                deliveryPoint.setWard(addressData.getWardCode());
                deliveryPoint.setDistrict(addressData.getDistrictCode());
                deliveryPoint.setProvince(addressData.getProvinceCode());
                deliveryPoint.setWardName(addressData.getWardName());
                deliveryPoint.setDistrictName(addressData.getDistrictName());
                deliveryPoint.setProvinceName(addressData.getProvinceName());
            }
            //Build receivers
            List<CreateOrderReceiver> receivers = new ArrayList<>();
            for (SvcOrderDetail groupedDeliveryPoint : groupedDeliveryPoints) {
                CreateOrderReceiver receiver = new CreateOrderReceiver();
                receiver.setOrderDetailCode(groupedDeliveryPoint.getSvcOrderDetailCode());
                //Receiver
                receiver.setName(groupedDeliveryPoint.getConsignee());
                receiver.setPhone(groupedDeliveryPoint.getPhone());
                //Statics
                receiver.setLength(groupedDeliveryPoint.getLength());
                receiver.setWeight(groupedDeliveryPoint.getWeight());
                receiver.setWidth(groupedDeliveryPoint.getWidth());
                receiver.setHeight(groupedDeliveryPoint.getHeight());
                //Others
                receiver.setPartialDelivery(groupedDeliveryPoint.getIsPartDelivery());
                receiver.setIsFree(groupedDeliveryPoint.getIsFree());
                receiver.setRequireNote(groupedDeliveryPoint.getRequiredNote());
                receiver.setConfirmType(0);
                receiver.setIsRefund(groupedDeliveryPoint.getIsRefund());
                //Extra Services
                receiver.setExtraServices(Collections.singletonList(new ExtraService(groupedDeliveryPoint.getIsDoorDelivery(), groupedDeliveryPoint.getIsPorter())));
                //Items
                List<ProductReportOM> products = productRepository.getProductsCateByOrderCode(String.valueOf(groupedDeliveryPoint.getSvcOrderDetailCode()));
                List<OrderDetailProduct> items = new ArrayList<>();
                for (ProductReportOM product : products) {
                    OrderDetailProduct item = new OrderDetailProduct();
                    item.setProductName(product.getName());
                    item.setId(product.getId());
                    item.setCategory(product.getProductCateId());
                    item.setProductCateId(product.getProductCateId());
                    item.setProductValue(product.getValue());
                    item.setQuantity(BigDecimal.valueOf(product.getQuantity()));
                    item.setCod(product.getCod());
                    items.add(item);
                }
                receiver.setItems(items);
                receivers.add(receiver);
            }
            deliveryPoint.setReceivers(receivers);
            deliveryPoints.add(deliveryPoint);
        }
        createOrder.setDeliveryPoint(deliveryPoints);
        return createOrder;
    }

    public void callbackToMaincore(OrderPartialRequest partialRequest, String note, SvcOrderDetail orderDetail, int status) {
        DataObject dataObject = new DataObject();
        dataObject.setCodAmount(partialRequest.getPartialCod());
        dataObject.setWeight(orderDetail.getWeight());
        dataObject.setOrderId(String.valueOf(orderDetail.getSvcOrderDetailCode()));
        dataObject.setDescription(note);
        dataObject.setStatus(String.valueOf(status));
        ResponseObject responseObject = new ResponseObject("HLS", dataObject);
        CallRedis.pushMaincoreQueue("CALLBACK_DATA2", gson.toJson(responseObject));
    }

    public BaseResponse deleteOrder(DeleteOrderRequest deleteOrderRequest, String username) throws Exception {
        List<BigDecimal> codes = new ArrayList<>();
        if (deleteOrderRequest.getOrderCodes() != null && !deleteOrderRequest.getOrderCodes().isEmpty()) {
            Arrays.asList(deleteOrderRequest.getOrderCodes().split(",")).forEach(c -> codes.add(BigDecimal.valueOf(Long.parseLong(c))));
            if (codes.size() == 0)
                return new BaseResponse(614);
        }
        List<Integer> allowDeletes = Arrays.asList(900, 999, 903, 107, 902, 924);
//        List<Integer> allowDeletes = Arrays.asList(900, 999);
        List<SvcOrderDetail> orderDetails = new ArrayList<>();
        if (deleteOrderRequest.getGroupStatus() != null) {
            if (!allowDeletes.contains(deleteOrderRequest.getGroupStatus()))
                return new BaseResponse(614);
            orderDetails = orderDetailRepository.getAllOrderDetailByPhoneAndGroupStatus(username, Collections.singletonList(deleteOrderRequest.getGroupStatus()));
        } else
            orderDetails = orderDetailRepository.getAllOrderDetailByCodesPhoneAndGroupStatus(username, codes, allowDeletes);
        if (orderDetails.size() == 0)
            return new BaseResponse(614);
        logger.info("=====DELETE ORDER=====" + gson.toJson(orderDetails.stream().map(SvcOrderDetail::getSvcOrderDetailCode).collect(Collectors.toList())));
        for (SvcOrderDetail orderDetail : orderDetails) {
            if (allowDeletes.contains(orderDetail.getStatus())) {
                List<WalletLog> logs = walletLogRepository.findByOrderDetailIdAndTypeAndIsDeleted(orderDetail.getSvcOrderDetailCode(), BigDecimal.valueOf(20), 1);
                if (logs.size() > 0) {
                    AppUser appUser = appUserRepository.findByPhone(username);
                    for (WalletLog log : logs) {
                        walletService.revertHoldingBalance(log, appUser);
                    }
                }
                orderDetail.setIsDeleted(1);
                orderDetailRepository.save(orderDetail);
                SvcOrder order = orderRepository.findByOrderCode(orderDetail.getSvcOrderId());
                order.setIsDeleted(1);
                orderRepository.save(order);
                logger.info("=====DELETED ORDER=====" + orderDetail.getSvcOrderDetailCode());
            }
        }
        return new BaseResponse(790);
    }

    private void validatePrintOrderRequest(PrintOrderRequest printOrderRequest) throws Exception {
        if (printOrderRequest.getSearchKey() == null)
            printOrderRequest.setSearchKey("");
        else {
            String searchKey = printOrderRequest.getSearchKey()
                    .replaceAll("\\(", "")
                    .replaceAll("\\)", "")
                    .replaceAll("'", "");
            printOrderRequest.setSearchKey(searchKey);
        }
        if (printOrderRequest.getPackCode() == null)
            printOrderRequest.setPackCode("");
        if (printOrderRequest.getFromDate() == null)
            printOrderRequest.setFromDate("");
        if (printOrderRequest.getToDate() == null)
            printOrderRequest.setToDate("");
        if (printOrderRequest.getType() == 3)
            printOrderRequest.setTargetStatuses(AppConfig.getInstance().targetStatuses.get(printOrderRequest.getOrderStatus()));
        if (printOrderRequest.getOrderBy() == null)
            printOrderRequest.setOrderBy(1);
    }

    private void handHoldBalance(SvcOrderDetail orderDetail, AppUser appUser) {
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

}
