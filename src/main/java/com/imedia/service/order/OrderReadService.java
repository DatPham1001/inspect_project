package com.imedia.service.order;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.config.application.AppConfig;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.dao.OrderDAO;
import com.imedia.oracle.entity.AppUser;
import com.imedia.oracle.entity.OrderPartialRequest;
import com.imedia.oracle.reportentity.*;
import com.imedia.oracle.reportrepository.*;
import com.imedia.oracle.repository.AppUserRepository;
import com.imedia.oracle.repository.DocumentLinkRepository;
import com.imedia.oracle.repository.OrderPartialRequestRepository;
import com.imedia.service.order.dto.*;
import com.imedia.service.order.enums.OrderStatusEnum;
import com.imedia.service.order.enums.OrderStatusNameEnum;
import com.imedia.service.order.enums.OrderStatusTabEnum;
import com.imedia.service.order.model.*;
import com.imedia.service.product.model.ProductReportOM;
import com.imedia.service.user.model.UserInfoAddressData;
import com.imedia.util.AppUtil;
import com.imedia.util.PreLoadStaticUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderReadService {
    static final Logger logger = LogManager.getLogger(OrderService.class);
    static final Gson gson = new Gson();
    private final SvcOrderDetailReportRepository orderDetailRepository;
    private final DetailProductReportRepository productRepository;
    private final DetailSellingFeeReportRepository detailSellingFeeReportRepository;
    private final OrderDAO orderDAO;
    private final MapperFacade mapperFacade;
    private final SvcOrderLogReportRepository orderLogReportRepository;
    private final OrderLogReportRepository shipOrderLogReportRepository;
    private final AppUserRepository appUserRepository;
    private final OrderDetailReportRepository orderDetailReportRepository;
    private final CarrierReportRepository carrierReportRepository;
    private final OrderPartialRequestRepository partialRequestRepository;
    static final Gson gsonWeb = new GsonBuilder()
            .setDateFormat("dd/MM/yyyy HH:mm:ss").create();
    private final DocumentLinkRepository documentLinkRepository;
    static final List<String> holaship1 = Arrays.asList("HLS1", "HOLA1");
    static final List<String> holaship2 = Arrays.asList("HLS", "HOLA");
    //    static final List<String> redeliveryAllow = Arrays.asList("GHN", "HNI","HLS1", "HOLA1");
//    static final List<String> refundAllow = Arrays.asList("HLS", "HOLA");
    private final V2PackageReportRepository packageReportRepository;

    @Autowired
    public OrderReadService(SvcOrderDetailReportRepository orderDetailRepository, DetailProductReportRepository productRepository, DetailSellingFeeReportRepository detailSellingFeeReportRepository, OrderDAO orderDAO, MapperFacade mapperFacade, SvcOrderLogReportRepository orderLogReportRepository, OrderLogReportRepository shipOrderLogReportRepository, AppUserRepository appUserRepository, OrderDetailReportRepository orderDetailReportRepository, CarrierReportRepository carrierReportRepository, OrderPartialRequestRepository partialRequestRepository, DocumentLinkRepository documentLinkRepository, V2PackageReportRepository packageReportRepository) {
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.detailSellingFeeReportRepository = detailSellingFeeReportRepository;
        this.orderDAO = orderDAO;
        this.mapperFacade = mapperFacade;
        this.orderLogReportRepository = orderLogReportRepository;
        this.shipOrderLogReportRepository = shipOrderLogReportRepository;
        this.appUserRepository = appUserRepository;
        this.orderDetailReportRepository = orderDetailReportRepository;
        this.carrierReportRepository = carrierReportRepository;
        this.partialRequestRepository = partialRequestRepository;
        this.documentLinkRepository = documentLinkRepository;
        this.packageReportRepository = packageReportRepository;
    }

    public BaseResponse filterOrder(FilterOrderRequest filterOrderRequest, String username) {
        //Validate inputs
        validateFilterOrderRequest(filterOrderRequest);
        List<FilterOrderDTO> filterOrderDTOS = orderDAO.filterOrder(filterOrderRequest, username);
        //Lấy Order không trùng lặp
        List<FilterOrderDTO> distinctOrder = filterOrderDTOS.stream()
                .filter(AppUtil.distinctByKey(FilterOrderDTO::getSvcOrderId))
                .collect(Collectors.toList());
        //Lấy detail không trùng lặp
        List<FilterOrderDTO> distinctOrderDetails = filterOrderDTOS.stream()
                .filter(AppUtil.distinctByKey(FilterOrderDTO::getSvcOrderDetailCode))
                .collect(Collectors.toList());
        //Build response
        List<FilterOrderResponse> filterOrderResponses = mapperFacade.mapAsList(distinctOrder, FilterOrderResponse.class);
        List<FilterOrderResponse> filterOrderResponsesResult = new ArrayList<>();
//        BigDecimal totalRealityCod = BigDecimal.ZERO;
        //tính tổng đơn con
        for (FilterOrderResponse filterOrderResponse : filterOrderResponses) {
            try {
//            if (!filterOrderResponse.getOrderStatus().equals(OrderStatusEnum.CANCEL_SHOP.code))
//                totalRealityCod = totalRealityCod.add(filterOrderResponse.getRealityCod());
                //Status model cho mobile
                filterOrderResponse.setStatusMobile(new OrderStatusData(filterOrderResponse.getOrderStatus()));
                //Shop address
                UserInfoAddressData addressData = PreLoadStaticUtil.addresses.get(String.valueOf(filterOrderResponse.getShopWardCode()));
                if (addressData != null) {
                    filterOrderResponse.setShopProvince(addressData.getProvinceName());
                    filterOrderResponse.setShopDistrict(addressData.getDistrictName());
                    filterOrderResponse.setShopWard(addressData.getWardName());
                }
                //Get all order fees (có trùng lặp)
                List<FilterOrderDTO> allOrderFees = filterOrderDTOS.stream()
                        .filter(f -> f.getSvcOrderId().equals(filterOrderResponse.getSvcOrderId()))
                        .collect(Collectors.toList());
                //Total order fee
                BigDecimal totalFee = BigDecimal.ZERO;
                for (FilterOrderDTO feeOrder : allOrderFees) {
                    if (feeOrder != null && feeOrder.getFeeValue() != null
                            && !feeOrder.getStatus().equals(Long.valueOf(OrderStatusEnum.CANCEL_SHOP.code)))
                        totalFee = totalFee.add(feeOrder.getFeeValue());
                }
                filterOrderResponse.setTotalFee(totalFee);
                //Order fees không trùng lặp
                //Check trạng thía không có Phí => không distict tránh exception
                List<FilterOrderDTO> distinctOrderFeeCodes = new ArrayList<>();
//            if (!filterOrderResponse.getOrderStatus().equals(OrderStatusEnum.WAIT_TO_CONFIRM.code)) {
                try {
                    distinctOrderFeeCodes = allOrderFees.stream()
                            .filter(AppUtil.distinctByKey(FilterOrderDTO::getFeeCode))
                            .collect(Collectors.toList());
                } catch (Exception e) {
                    logger.info("======GET ORDER DETAILS EXCEPTION======" + gson.toJson(filterOrderRequest), e);
                }
                //Cộng các giá trị trùng lặp vào để hiển thị giá trị không trùng lặp với value = tổng các value của trùng lặp
                List<FilterOrderResponseFee> fees = new ArrayList<>();
                for (FilterOrderDTO feeCode : distinctOrderFeeCodes) {
                    FilterOrderResponseFee filterOrderResponseFee = new FilterOrderResponseFee();
                    filterOrderResponseFee.setFeeCode(feeCode.getFeeCode());
                    filterOrderResponseFee.setFeeName(feeCode.getFeeName());
                    for (FilterOrderDTO feeOrder : allOrderFees) {
                        if (feeOrder.getFeeCode().equals(feeCode.getFeeCode())) {
                            BigDecimal feeValue = filterOrderResponseFee.getFeeValue().add(feeOrder.getFeeValue());
                            filterOrderResponseFee.setFeeValue(feeValue);
                        }
                    }
                    fees.add(filterOrderResponseFee);
                }
                filterOrderResponse.setOrderFees(fees);
                List<FilterOrderDTO> filterOrderDetails = filterOrderDTOS.stream()
                        .filter(o -> o.getSvcOrderId().equals(filterOrderResponse.getSvcOrderId()))
                        .collect(Collectors.toList());
                if (filterOrderDetails.size() > 0) {
                    List<ProductReportOM> detailProductReports =
                            productRepository.getProductsByOrderCode(String.valueOf(filterOrderDetails.get(0).getSvcOrderId()));

//                for (DetailProductReport detailProductReport : detailProductReports) {
//                    if (detailProductReport != null && detailProductReport.getCod() != null)
//                        totalCod = totalCod.add(detailProductReport.getCod().multiply(detailProductReport.getQuantity()));
//                }
                    //Order details from order code
                    List<FilterOrderDTO> orderDetails = distinctOrderDetails.stream()
                            .filter(d -> d.getSvcOrderId().equals(filterOrderResponse.getSvcOrderId()))
                            .collect(Collectors.toList());
                    List<FilterOrderDetailResponse> orderDetailResponses = new ArrayList<>();
                    //Total Cod (tính từ các product của order detail)
                    BigDecimal totalCod = BigDecimal.ZERO;
                    for (FilterOrderDTO orderDetail : orderDetails) {
                        FilterOrderDetailResponse orderDetailResponse = mapperFacade.map(orderDetail, FilterOrderDetailResponse.class);
                        //Status model cho mobile
                        orderDetailResponse.setStatusMobile(new OrderStatusData(orderDetailResponse.getStatus().intValue()));
                        //Shop address
                        UserInfoAddressData addressData1 = PreLoadStaticUtil.addresses.get(String.valueOf(orderDetailResponse.getShopWardCode()));
                        if (addressData != null) {
                            orderDetailResponse.setShopProvince(addressData1.getProvinceName());
                            orderDetailResponse.setShopDistrict(addressData1.getDistrictName());
                            orderDetailResponse.setShopWard(addressData1.getWardName());
                        }
                        //Set products
                        List<ProductReportOM> orderDetailProducts = detailProductReports.stream()
                                .filter(d -> d.getOrderDetailCode().equals(orderDetail.getSvcOrderDetailCode()))
                                .collect(Collectors.toList());
                        orderDetailResponse.setProducts(mapperFacade.mapAsList(orderDetailProducts, FilterOrderDetailProductResponse.class));
                        //Total detail cod
//                    BigDecimal totalDetailCod = BigDecimal.ZERO;
//                    for (DetailProductReport orderDetailProduct : orderDetailProducts) {
//                        totalDetailCod = totalDetailCod.add(orderDetailProduct.getCod().multiply(orderDetailProduct.getQuantity()));
//                    }
                        //Fee
                        List<FilterOrderDTO> feeOrderDetails = filterOrderDTOS.stream()
                                .filter(f -> f.getSvcOrderDetailCode().equals(orderDetail.getSvcOrderDetailCode()))
                                .collect(Collectors.toList());
                        orderDetailResponse.setDetailFees(mapperFacade.mapAsList(feeOrderDetails, FilterOrderResponseFee.class));
                        //Total detail fee
                        BigDecimal totalDetailFee = BigDecimal.ZERO;
                        for (FilterOrderDTO filterOrderDTO : feeOrderDetails) {
                            if (filterOrderDTO.getFeeValue() != null
                                    && !orderDetailResponse.getStatus().equals(Long.valueOf(OrderStatusEnum.CANCEL_SHOP.code)))
                                totalDetailFee = totalDetailFee.add(filterOrderDTO.getFeeValue());
                        }
                        orderDetailResponse.setTotalDetailCod(orderDetailResponse.getRealityCod());
                        orderDetailResponse.setTotalDetailFee(totalDetailFee);
                        //Nếu trạng thái chờ duyệt thì sẽ không có phí => build lại response
//                    if (filterOrderRequest.getOrderStatus().equals(OrderStatusEnum.WAIT_TO_CONFIRM.code)) {
//                        orderDetailResponse.setTotalDetailFee(null);
//                        orderDetailResponse.setDetailFees(null);
//                    }
                        if (!orderDetailResponse.getStatus().equals(Long.valueOf(OrderStatusEnum.CANCEL_SHOP.code)))
                            totalCod = totalCod.add(orderDetailResponse.getRealityCod());
                        orderDetailResponses.add(orderDetailResponse);
                    }
//                totalRealityCod = totalRealityCod.add(totalCod);
                    filterOrderResponse.setTotalCod(totalCod);
                    //Tính tổng Giao tqaanj tay và giao 1 phần
                    int totalIsDoorDelivery = 0;
                    int totalIsPartial = 0;
                    for (FilterOrderDetailResponse orderDetailRespons : orderDetailResponses) {
                        if (orderDetailRespons.getIsDoorDelivery() == 1)
                            totalIsDoorDelivery = totalIsDoorDelivery + 1;
                        if (orderDetailRespons.getIsPartDelivery() == 1)
                            totalIsPartial = totalIsPartial + 1;
                    }
                    filterOrderResponse.setTotalIsDoorDelivery(totalIsDoorDelivery);
                    filterOrderResponse.setTotalIsPartial(totalIsPartial);
                    filterOrderResponse.setOrderDetails(orderDetailResponses);
                    //Nếu trạng thái chờ duyệt thì sẽ không có phí => build lại response
//                if (filterOrderRequest.getOrderStatus().equals(OrderStatusEnum.WAIT_TO_CONFIRM.code)) {
//                    filterOrderResponse.setTotalFee(null);
//                    filterOrderResponse.setOrderFees(null);
//                }
                }
            } catch (Exception e) {
                logger.info("======GET ORDER DETAILS EXCEPTION======" + gson.toJson(filterOrderRequest), e);
            }
            filterOrderResponsesResult.add(filterOrderResponse);
        }
        //Build response
        HashMap<String, Object> response = new HashMap<>();
        CountTotalOM countModel = new CountTotalOM();

        if (filterOrderResponsesResult.size() > 0) {
            countModel = orderDAO.countFilterOrder(filterOrderRequest, username);
//            totalCod = orderDAO.countTotalCod(filterOrderRequest, username);
        }
        List<PackageOM> packageOMS = packageReportRepository.getUsedPackages(username);
        List<ShopAddressOM> shopAddressOMS = orderDetailRepository.getUsedShopAddress(username);
        response.put("total", countModel.getTotal());
        response.put("packages", packageOMS);
        response.put("shopAddresses", shopAddressOMS);
        response.put("totalOrder", countModel.getTotalOrder());
        response.put("totalCod", countModel.getTotalCod());
        response.put("totalFee", countModel.getTotalFee());
        response.put("page", filterOrderRequest.getPage() + 1);
        if (filterOrderRequest.getSize() <= distinctOrder.size())
            response.put("size", filterOrderRequest.getSize());
        else response.put("size", distinctOrder.size());
        response.put("data", filterOrderResponsesResult);
//        String res = gson.toJson(response);
        return new BaseResponse(200, response);
//        return new BaseResponse(200, null);9
    }

    public BaseResponse filterOrderDetails(FilterOrderRequest filterOrderRequest, String username) throws Exception {
        //Validate inputs
        validateFilterOrderRequest(filterOrderRequest);
        List<FilterOrderDTO> filterOrderDTOS = orderDAO.filterOrderDetail(filterOrderRequest, username);
        List<FilterOrderDTO> distinctOrderDetails = filterOrderDTOS.stream()
                .filter(AppUtil.distinctByKey(FilterOrderDTO::getSvcOrderDetailCode))
                .collect(Collectors.toList());
        List<FilterOrderDetailResponse> filterOrderDetailData = mapperFacade.mapAsList(distinctOrderDetails, FilterOrderDetailResponse.class);
        List<FilterOrderDetailResponse> responses = new ArrayList<>();
//        BigDecimal totalRealityCod = BigDecimal.ZERO;
        for (FilterOrderDetailResponse filterOrderDetailResponse : filterOrderDetailData) {
            try {
//            totalRealityCod = totalRealityCod.add(filterOrderDetailResponse.getRealityCod());
                //Status model cho mobile
                filterOrderDetailResponse.setStatusMobile(new OrderStatusData(filterOrderDetailResponse.getStatus().intValue()));
                //Shop address
                UserInfoAddressData addressData = PreLoadStaticUtil.addresses.get(String.valueOf(filterOrderDetailResponse.getShopWardCode()));
                if (addressData != null) {
                    filterOrderDetailResponse.setShopProvince(addressData.getProvinceName());
                    filterOrderDetailResponse.setShopDistrict(addressData.getDistrictName());
                    filterOrderDetailResponse.setShopWard(addressData.getWardName());
                }
                List<ProductReportOM> detailProductReports =
                        productRepository.getProductsByOrderCode(String.valueOf(filterOrderDetailResponse.getSvcOrderDetailCode()));
                //Total Cod
//            BigDecimal totalCod = BigDecimal.ZERO;
//            for (DetailProductReport detailProductReport : detailProductReports) {
//                totalCod = totalCod.add(detailProductReport.getCod().multiply(detailProductReport.getQuantity()));
//            }
                //Total Fee
                BigDecimal totalFee = BigDecimal.ZERO;
                List<FilterOrderDTO> orderDetailFees = filterOrderDTOS.stream()
                        .filter(f -> f.getSvcOrderDetailCode().equals(filterOrderDetailResponse.getSvcOrderDetailCode()))
                        .collect(Collectors.toList());
                for (FilterOrderDTO orderDetailFee : orderDetailFees) {
                    if (orderDetailFee.getFeeValue() != null)
                        totalFee = totalFee.add(orderDetailFee.getFeeValue());
                }
                //Fees
                List<FilterOrderResponseFee> detailFees = mapperFacade.mapAsList(orderDetailFees, FilterOrderResponseFee.class);
                //Products
                List<FilterOrderDetailProductResponse> detailProducts = mapperFacade.mapAsList(detailProductReports, FilterOrderDetailProductResponse.class);
                filterOrderDetailResponse.setProducts(detailProducts);
                filterOrderDetailResponse.setDetailFees(detailFees);
                filterOrderDetailResponse.setTotalDetailFee(totalFee);
                filterOrderDetailResponse.setTotalDetailCod(filterOrderDetailResponse.getRealityCod());
                SvcOrderLogReport orderLogReport = orderLogReportRepository.getLastLog(filterOrderDetailResponse.getSvcOrderDetailCode());
                if (orderLogReport != null)
                    filterOrderDetailResponse.setLastNote(orderLogReport.getNote());
                //Services
                StringBuilder services = new StringBuilder("");
                if (filterOrderDetailResponse.getCarrierId() != null) {
                    CarrierReport carrier = carrierReportRepository.findCarrierById(filterOrderDetailResponse.getCarrierId());
                    AppConfig appConfig = AppConfig.getInstance();
                    //Nếu là Hola 2 thì sẽ full service
                    if (carrier.getSpecialServices() != null) {
                        if (carrier.getSpecialServices().contains("4")
                                && appConfig.reDeliveryStatus.contains(String.valueOf(filterOrderDetailResponse.getStatus())))
                            services.append("RE_DELIVERY,");
                        if (carrier.getSpecialServices().contains("5")
                                && appConfig.refundRequestStatus.contains(String.valueOf(filterOrderDetailResponse.getStatus())))
                            services.append("REFUND_REQ");
                    }
                }
                filterOrderDetailResponse.setAction(services.toString());
                //Nếu trạng thái chờ duyệt thì sẽ không có phí => build lại response
//            if (filterOrderRequest.getOrderStatus().equals(OrderStatusEnum.WAIT_TO_CONFIRM.code)) {
//                filterOrderDetailResponse.setTotalDetailFee(null);
//                filterOrderDetailResponse.setDetailFees(null);
//            }
                responses.add(filterOrderDetailResponse);
            } catch (Exception e) {
                logger.info("======GET ORDER DETAILS EXCEPTION======" + gson.toJson(filterOrderRequest), e);
            }
        }
        //Total COD Filter Total FEE Filter
//        BigDecimal totalFilterCod = BigDecimal.ZERO;
//        BigDecimal totalFilterFee = BigDecimal.ZERO;
//        for (FilterOrderDetailResponse filterOrderResponse : responses) {
//            totalFilterCod = totalFilterCod.add(filterOrderResponse.getTotalDetailCod());
//            if (!filterOrderRequest.getOrderStatus().equals(OrderStatusEnum.WAIT_TO_CONFIRM.code))
//                totalFilterFee = totalFilterFee.add(filterOrderResponse.getTotalDetailFee());
//        }
        //Nếu trạng thái chờ duyệt thì sẽ không có phí => build lại response
//        if (filterOrderRequest.getOrderStatus().equals(OrderStatusEnum.WAIT_TO_CONFIRM.code))
//            totalFilterFee = null;
//        BigDecimal totalCod = orderDAO.countTotalCod(filterOrderRequest, username);
        //Build response
        HashMap<String, Object> response = new HashMap<>();
//        CountTotalOM countModel = orderDAO.countFilterOrderDetail(filterOrderRequest, username);
        CountTotalOM countModel = orderDAO.countFilterOrder(filterOrderRequest, username);
        List<ShopAddressOM> shopAddressOMS = orderDetailRepository.getUsedShopAddress(username);
        List<PackageOM> packageOMS = packageReportRepository.getUsedPackages(username);
        response.put("total", countModel.getTotalOrder());
        response.put("packages", packageOMS);
        response.put("shopAddresses", shopAddressOMS);
        response.put("totalCod", countModel.getTotalCod());
        response.put("totalOrder", countModel.getTotalOrder());
        response.put("totalFee", countModel.getTotalFee());
        response.put("page", filterOrderRequest.getPage() + 1);
        if (filterOrderRequest.getSize() <= responses.size())
            response.put("size", filterOrderRequest.getSize());
        else response.put("size", responses.size());
        response.put("data", responses);
        return new BaseResponse(200, response);
    }

    public BaseResponse getSumOrderStatus(FilterOrderRequest filterOrderRequest, String username) throws Exception {
        //Validate inputs
        validateFilterOrderRequest(filterOrderRequest);
        List<SumOrderStatusDTO> sumOrderStatus = orderDAO.sumOrderStatus(filterOrderRequest, username);
        List<SumOrderStatusDTO> waitToConfirm = orderDAO.sumOrderStatus2(filterOrderRequest, username);
        HashMap<Object, Object> response = new HashMap<>();
        int all = 0;
        for (Map.Entry<Integer, String> entry : AppConfig.getInstance().targetStatuses.entrySet()) {
            if (entry.getKey().equals(999)) {
                response.put(entry.getKey(), waitToConfirm.get(0).getTotal());
                continue;
            }
            List<String> groupStatuses = Arrays.asList(entry.getValue().split(","));
            int totalGroupedStatus = 0;
            List<SumOrderStatusDTO> targetStatuses = sumOrderStatus
                    .stream()
                    .filter(s -> groupStatuses.stream()
                            .anyMatch(g -> g.equals(String.valueOf(s.getStatus()))))
                    .collect(Collectors.toList());
            for (SumOrderStatusDTO targetStatus : targetStatuses) {
                totalGroupedStatus = totalGroupedStatus + targetStatus.getTotal();
            }
            response.put(entry.getKey(), totalGroupedStatus);

            all = all + totalGroupedStatus;
        }
        List<SumOrderStatusDTO> confirmed = sumOrderStatus.stream().filter(s -> s.getStatus().equals(OrderStatusEnum.CONFIRMED.code))
                .collect(Collectors.toList());
        for (SumOrderStatusDTO sumOrderStatusDTO : confirmed) {
            all = all + sumOrderStatusDTO.getTotal();
        }
        response.put(0, all);
        List<OrderTabMobileResponse> mobileResponses = new ArrayList<>();
        for (OrderStatusTabEnum val : OrderStatusTabEnum.values()) {
            Integer amount = (Integer) response.get(val.code);
            if (amount != null) {
                OrderTabMobileResponse mobileResponse = new OrderTabMobileResponse(val.code, amount);
                mobileResponses.add(mobileResponse);
            }
        }
        response.put("mobileResponse", mobileResponses);
        return new BaseResponse(200, response);
    }

    private void validateFilterOrderRequest(FilterOrderRequest filterOrderRequest) {
        if (filterOrderRequest.getSearchKey() == null)
            filterOrderRequest.setSearchKey("");
        else {
            String searchKey = filterOrderRequest.getSearchKey()
                    .replaceAll("\\(", "")
                    .replaceAll("\\)", "")
                    .replaceAll("'", "");
            filterOrderRequest.setSearchKey(searchKey);
        }
        if (filterOrderRequest.getPackCode() == null)
            filterOrderRequest.setPackCode("");
        if (filterOrderRequest.getFromDate() == null)
            filterOrderRequest.setFromDate("");
        if (filterOrderRequest.getToDate() == null)
            filterOrderRequest.setToDate("");
        if (filterOrderRequest.getExportExcel() == null)
            filterOrderRequest.setExportExcel(0);

//        //Gom list trạng thái
        if (filterOrderRequest.getOrderStatus() == null)
            filterOrderRequest.setOrderStatus(0);
        else {
            try {
                filterOrderRequest.setTargetStatuses(AppConfig.getInstance().targetStatuses.get(filterOrderRequest.getOrderStatus()));
                if (filterOrderRequest.getTargetStatuses() == null)
                    filterOrderRequest.setTargetStatuses("");
            } catch (Exception e) {
                filterOrderRequest.setTargetStatuses("");
            }
        }
        if (filterOrderRequest.getPage() == null || filterOrderRequest.getPage() <= 1)
            filterOrderRequest.setPage(0);
        else filterOrderRequest.setPage(filterOrderRequest.getPage() - 1);
        if (filterOrderRequest.getSize() == null)
            filterOrderRequest.setSize(25);
        if (filterOrderRequest.getOrderBy() == null)
            filterOrderRequest.setOrderBy(1);
    }

    public BaseResponse getOrderDetailById(BigDecimal orderDetailCode, String username) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        List<FilterOrderDTO> orderDetailFees = orderDAO.getOrderDetailFeeByCode(username, orderDetailCode);
        if (orderDetailFees.size() == 0)
            return new BaseResponse(614);
        //Lấy data của order detail
        GetOrderDetailResponse orderDetailResponse = mapperFacade.map(orderDetailFees.get(0), GetOrderDetailResponse.class);
        orderDetailResponse.setStatusMobile(new OrderStatusData(orderDetailResponse.getStatus()));
        //Shop address
        UserInfoAddressData addressData = PreLoadStaticUtil.addresses.get(String.valueOf(orderDetailResponse.getShopWardCode()));
        if (addressData != null) {
            orderDetailResponse.setShopProvince(addressData.getProvinceName());
            orderDetailResponse.setShopDistrict(addressData.getDistrictName());
            orderDetailResponse.setShopWard(addressData.getWardName());
        }
        //Lấy danh sách phí
        List<FilterOrderResponseFee> detailFees = mapperFacade.mapAsList(orderDetailFees, FilterOrderResponseFee.class);
        List<ProductReportOM> productReports = productRepository.getProductsByOrderCode(String.valueOf(orderDetailCode));
        //Lấy đổi COD và Giao 1 phần
//        if (!orderDetailResponse.getStatus().equals(Long.valueOf(OrderStatusEnum.WAIT_TO_CONFIRM.code))
//                && !orderDetailResponse.getStatus().equals(Long.valueOf(OrderStatusEnum.CONFIRMED.code))
//                && !orderDetailResponse.getStatus().equals(Long.valueOf(OrderStatusEnum.FINDING_SHIP.code))
//        ) {
        if (orderDetailResponse.getCarrierId() != null) {
            orderDetailResponse.setAction(getSepecialServices(orderDetailResponse));
        } else orderDetailResponse.setAction("PARTIAL,COD,WEIGHT,RECEIVER_INFO");
        AppUser shipper = appUserRepository.getShipperData(orderDetailCode);
        BigDecimal totalCod = BigDecimal.ZERO;
        BigDecimal totalFee = BigDecimal.ZERO;
//        for (DetailProductReport productReport : productReports) {
//            totalCod = totalCod.add(productReport.getCod().multiply(productReport.getQuantity()));
//        }
        //Fees
        List<FilterOrderResponseFee> feeResults = new ArrayList<>();
        if (detailFees != null && detailFees.size() > 0) {
            for (FilterOrderResponseFee detailFee : detailFees) {
                totalFee = totalFee.add(detailFee.getFeeValue());
            }
            //Cộng các gias trị lặp vào
            List<FilterOrderResponseFee> distinctFees = detailFees.stream()
                    .filter(AppUtil.distinctByKey(FilterOrderResponseFee::getFeeCode))
                    .collect(Collectors.toList());
            if (detailFees.size() != distinctFees.size()) {
                for (FilterOrderResponseFee distinctFee : distinctFees) {
                    FilterOrderResponseFee filterOrderResponseFee = new FilterOrderResponseFee();
                    filterOrderResponseFee.setFeeName(distinctFee.getFeeName());
                    filterOrderResponseFee.setFeeCode(distinctFee.getFeeCode());
                    for (FilterOrderResponseFee detailFee : detailFees) {
                        if (distinctFee.getFeeCode().equalsIgnoreCase(detailFee.getFeeCode())) {
                            BigDecimal feeValue = filterOrderResponseFee.getFeeValue().add(detailFee.getFeeValue());
                            filterOrderResponseFee.setFeeValue(feeValue);
                        }
                    }
                    feeResults.add(filterOrderResponseFee);
                }
            } else
                feeResults = distinctFees;
        }
        orderDetailResponse.setFees(feeResults);
        orderDetailResponse.setTotalCod(orderDetailResponse.getRealityCod());
        orderDetailResponse.setTotalFee(totalFee);
        //Partial request
        orderDetailResponse.setPartialRequest(buildPartialRequest(orderDetailResponse));
        //Shipper dât
        FilterOrderShipperData shipperData = mapperFacade.map(shipper, FilterOrderShipperData.class);
        orderDetailResponse.setShipper(shipperData);
        orderDetailResponse.setProducts(mapperFacade.mapAsList(productReports, FilterOrderDetailProductResponse.class));
        //Trackings
        List<SvcOrderLogReport> trackings = orderLogReportRepository.findAllBySvcOrderDetailCodeOrderByIdDesc(orderDetailCode);
        List<OrderTrackingResponse> trackingResponses = new ArrayList<>();
        for (SvcOrderLogReport tracking : trackings) {
            OrderTrackingResponse trackingResponse = mapperFacade.map(tracking, OrderTrackingResponse.class);
            //Build imgs
            if (tracking.getDocumentLinkId() != null) {
                List<String> trackingImgsResponse = new ArrayList<>();
                List<ImageOM> trackingImgs = orderDetailRepository.getImagesOfTracking(tracking.getDocumentLinkId());
                for (ImageOM trackingImg : trackingImgs) {
                    String url = appConfig.imageUrl + trackingImg.getPath() + "_" + trackingImg.getImgId();
                    trackingImgsResponse.add(url);
                }
                trackingResponse.setImages(trackingImgsResponse);
            }
            if (tracking.getToStatus().equals(orderDetailResponse.getStatusMobile().getCode())
                    && orderDetailResponse.getStatusMobile().getNote() == null) {
                orderDetailResponse.getStatusMobile().setNote(tracking.getNote());
            }
            trackingResponse.setStatusName(OrderStatusNameEnum.valueOf(trackingResponse.getToStatus()).message);
            trackingResponses.add(trackingResponse);
        }
        orderDetailResponse.setTrackings(trackingResponses);
        //Lấy ra ảnh ở trạng thái hiện tại
        orderDetailResponse.setImages(trackingResponses.get(0).getImages());
        //Get images
//        List<ImageOM> imageOMS = orderDetailRepository.getImages(orderDetailCode);
//        List<String> images = new ArrayList<>();
//        if (imageOMS.size() > 0)
//            for (ImageOM imageOM : imageOMS) {
//                if (imageOM.getImgId() != null) {
//                    String url = appConfig.imageUrl + imageOM.getPath() + "_" + imageOM.getImgId();
//                    images.add(url);
//                }
//            }
        return new BaseResponse(200, orderDetailResponse);
    }

    private String getSepecialServices(GetOrderDetailResponse orderDetailResponse) {
        StringBuilder services = new StringBuilder();
        try {
            AppConfig appConfig = AppConfig.getInstance();
            CarrierReport carrier = carrierReportRepository.findCarrierById(orderDetailResponse.getCarrierId());
            if (carrier.getSpecialServices() != null) {
                if (carrier.getSpecialServices().contains("1"))
                    services.append("PARTIAL,");
                if (carrier.getSpecialServices().contains("2")
                        && appConfig.changeCodStatus.contains(String.valueOf(orderDetailResponse.getStatus())))
                    services.append("COD,");
            }
            //Nếu là Hola thì sẽ full service
            if (holaship1.contains(carrier.getCode())) {
                services = new StringBuilder("PARTIAL,COD,WEIGHT,RECEIVER_INFO,");
                if (orderDetailResponse.getStatus() == OrderStatusEnum.DELIVERING.code)
                    services = new StringBuilder("");
            }
            if (holaship2.contains(carrier.getCode()))
                services = new StringBuilder("PARTIAL,COD,WEIGHT,RECEIVER_INFO,");
            if (orderDetailResponse.getStatus() == OrderStatusEnum.RETURN_WAIT.code
                    || orderDetailResponse.getStatus() == OrderStatusEnum.RETURN_STORED.code) {
                if (carrier.getSpecialServices() != null) {
                    if (carrier.getSpecialServices().contains("4")
                            && appConfig.reDeliveryStatus.contains(String.valueOf(orderDetailResponse.getStatus())))
                        services.append("RE_DELIVERY,");
                    if (carrier.getSpecialServices().contains("5")
                            && appConfig.refundRequestStatus.contains(String.valueOf(orderDetailResponse.getStatus())))
                        services.append("REFUND_REQ,");
                }
            }
            if (services.length() > 2)
                return services.substring(0, services.length() - 1);
            return services.toString();
        } catch (Exception e) {
            logger.info("======GET SPECIAL SERVICES EXCEPTION======" + orderDetailResponse.getSvcOrderDetailCode());
            return services.toString();
        }
    }

    private PartialRequestResponse buildPartialRequest(GetOrderDetailResponse orderDetail) {
        try {
            OrderPartialRequest partialRequest = partialRequestRepository.findByOrderDetailCodeAndIsConfirmed(orderDetail.getSvcOrderDetailCode(), 0);
            if (partialRequest == null)
                return null;
            PartialRequestResponse partial = mapperFacade.map(partialRequest, PartialRequestResponse.class);
            partial.setOrderDetailCode(orderDetail.getSvcOrderDetailCode());
//            partial.setId(partialRequest.getId());
            AppConfig appConfig = AppConfig.getInstance();
            List<Long> imgSuccessIds = Arrays.stream(partialRequest.getImgSuccessId().split(","))
                    .map(Long::valueOf).collect(Collectors.toList());
            List<Long> imgReturnIds = Arrays.stream(partialRequest.getImgReturnId().split(","))
                    .map(Long::valueOf).collect(Collectors.toList());
            ImageOM path = documentLinkRepository.getImgStoragePath(imgSuccessIds.get(0));
            List<String> imgSuccessUrls = new ArrayList<>();
            for (Long imgSuccessId : imgSuccessIds) {
                String url = appConfig.imageUrl + path.getPath() + "_" + imgSuccessId;
                imgSuccessUrls.add(url);
            }
            partial.setImgSuccess(imgSuccessUrls);
            List<String> imgReturnUrls = new ArrayList<>();
            for (Long imgReturnId : imgReturnIds) {
                String url = appConfig.imageUrl + path.getPath() + "_" + imgReturnId;
                imgReturnUrls.add(url);
            }
            partial.setImgReturn(imgReturnUrls);
//
//            partial.setPartialCod(partialRequest.getPartialCod());
//            partial.setExpectCod(partialRequest.getExpectCod());
            return partial;
        } catch (Exception e) {
            logger.info("==========PARTIAL REQUEST EXCEPTION READ ==========" + orderDetail.getSvcOrderDetailCode(), e);
        }
        return null;

    }

    public List<FilterOrderResponse> getOrderToConfirm(List<String> orderCodes, String username, String action) {
        String codes = orderCodes.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        List<ConfirmOrderDTO> confirmOrderDTOS = orderDAO.getOrderToConfirm(codes, username, action);
        //Lấy Order không trùng lặp
        List<ConfirmOrderDTO> distinctOrder = confirmOrderDTOS.stream()
                .filter(AppUtil.distinctByKey(ConfirmOrderDTO::getSvcOrderId))
                .collect(Collectors.toList());
        //Lấy detail không trùng lặp
        List<ConfirmOrderDTO> distinctOrderDetails = confirmOrderDTOS.stream()
                .filter(AppUtil.distinctByKey(ConfirmOrderDTO::getSvcOrderDetailCode))
                .collect(Collectors.toList());
        //Build base response order information
        List<FilterOrderResponse> filterOrderResponses = mapperFacade.mapAsList(distinctOrder, FilterOrderResponse.class);
        List<FilterOrderResponse> filterOrderResponsesResult = new ArrayList<>();
        for (FilterOrderResponse filterOrderResponse : filterOrderResponses) {
            //Shop address
            UserInfoAddressData addressData = PreLoadStaticUtil.addresses.get(String.valueOf(filterOrderResponse.getShopWardCode()));
            filterOrderResponse.setShopProvince(addressData.getProvinceName());
            filterOrderResponse.setShopDistrict(addressData.getDistrictName());
            filterOrderResponse.setShopWard(addressData.getWardName());
            //Danh sách order detail lấy từ distinct ứng với từng order lớn
            List<ConfirmOrderDTO> orderDetails = distinctOrderDetails.stream()
                    .filter(d -> d.getSvcOrderId().equals(filterOrderResponse.getSvcOrderId()))
                    .collect(Collectors.toList());
            List<FilterOrderDetailResponse> orderDetailResponses = new ArrayList<>();
            for (ConfirmOrderDTO orderDetail : orderDetails) {
                FilterOrderDetailResponse orderDetailResponse = mapperFacade.map(orderDetail, FilterOrderDetailResponse.class);
                //Shop address
                UserInfoAddressData shopAddressOrderDetail = PreLoadStaticUtil.addresses.get(String.valueOf(orderDetailResponse.getShopWardCode()));
                orderDetailResponse.setShopProvince(shopAddressOrderDetail.getProvinceName());
                orderDetailResponse.setShopDistrict(shopAddressOrderDetail.getDistrictName());
                orderDetailResponse.setShopWard(shopAddressOrderDetail.getWardName());
                //Lấy thông tin products theo detail
                List<ConfirmOrderDTO> productsOrderDetail = confirmOrderDTOS.stream()
                        .filter(o -> o.getSvcOrderDetailCode().equals(orderDetail.getSvcOrderDetailCode()))
                        .collect(Collectors.toList());
                List<FilterOrderDetailProductResponse> products = new ArrayList<>();
                for (ConfirmOrderDTO productDetail : productsOrderDetail) {
                    FilterOrderDetailProductResponse productResponse = new FilterOrderDetailProductResponse();
                    productResponse.setCod(productDetail.getProductCod());
                    productResponse.setId(productDetail.getId());
                    productResponse.setQuantity(BigDecimal.valueOf(productDetail.getProductQuantity()));
                    productResponse.setValue(productDetail.getProductValue());
                    productResponse.setName(productDetail.getProductName());
                    products.add(productResponse);
                }
                orderDetailResponse.setProducts(products);
                orderDetailResponses.add(orderDetailResponse);
            }
            filterOrderResponse.setOrderDetails(orderDetailResponses);
            filterOrderResponsesResult.add(filterOrderResponse);
        }
        return filterOrderResponsesResult;
    }

    public BaseResponse getBoughtUser(FilterConsigneeRequest consigneeRequest, String username) {
        List<ConsigneeInfoOM> consigneeInfoOMS = new ArrayList<>();
        if (consigneeRequest.getSearch().length() > 2) {
            consigneeInfoOMS = orderDetailReportRepository.getBoughtUser(consigneeRequest.getSearch(), username);
        }
        return new BaseResponse(200, consigneeInfoOMS);
    }

    public BaseResponse getConsultOrders(ConsultOrderRequest consultOrderRequest, String username) throws Exception {
        StringBuilder str = new StringBuilder();
        List<String> codes = Arrays.asList(consultOrderRequest.getOrderDetailCodes().split(","));
        if (codes.size() == 0)
            return new BaseResponse(200);
        for (String code : codes) {
            str.append("'").append(code).append("',");
        }
        String result = "";
        if (str.toString().length() > 1)
            result = str.substring(0, str.length() - 1);
        List<ConsultOrderDTO> filterOrderDTOS = orderDAO.getConsultOrders(result);
        List<ConsultOrderResponse> consultOrderResponses = new ArrayList<>();
        for (ConsultOrderDTO filterOrderDTO : filterOrderDTOS) {
            ConsultOrderResponse consultOrderResponse = mapperFacade.map(filterOrderDTO, ConsultOrderResponse.class);
            consultOrderResponse.setStatusMobile(new OrderStatusData(filterOrderDTO.getStatus()));
            List<DetailSellingFeeReport> feeReports = detailSellingFeeReportRepository.findAllByOrderDetailCode(filterOrderDTO.getSvcOrderDetailCode());
            BigDecimal totalFee = BigDecimal.ZERO;
            List<FilterOrderResponseFee> fees = new ArrayList<>();
            for (DetailSellingFeeReport feeReport : feeReports) {
                totalFee = totalFee.add(feeReport.getValue());
                FilterOrderResponseFee fee = new FilterOrderResponseFee();
                fee.setFeeCode(feeReport.getCode());
                fee.setFeeName(feeReport.getName());
                fee.setFeeValue(feeReport.getValue());
                fees.add(fee);
                //Sender
                UserInfoAddressData senderAddress = PreLoadStaticUtil.addresses.get(String.valueOf(consultOrderResponse.getShopWardCode()));
                if (senderAddress != null) {
                    consultOrderResponse.setShopProvince(senderAddress.getProvinceName());
                    consultOrderResponse.setShopDistrict(senderAddress.getDistrictName());
                    consultOrderResponse.setShopWard(senderAddress.getWardName());
                }
                List<ProductReportOM> productReports = productRepository.getProductsByOrderCode(String.valueOf(filterOrderDTO.getSvcOrderDetailCode()));
                List<FilterOrderDetailProductResponse> products = mapperFacade.mapAsList(productReports, FilterOrderDetailProductResponse.class);
                consultOrderResponse.setProducts(products);
                consultOrderResponse.setFees(fees);
                consultOrderResponse.setTotalFee(totalFee);
                List<SvcOrderLogReport> trackings = orderLogReportRepository.findAllBySvcOrderDetailCodeOrderByIdDesc(filterOrderDTO.getSvcOrderDetailCode());
                List<OrderTrackingResponse> trackingResponses = new ArrayList<>();
                for (SvcOrderLogReport tracking : trackings) {
                    OrderTrackingResponse trackingResponse = mapperFacade.map(tracking, OrderTrackingResponse.class);
                    //Build imgs
                    if (tracking.getDocumentLinkId() != null) {
                        List<String> trackingImgsResponse = new ArrayList<>();
                        List<ImageOM> trackingImgs = orderDetailRepository.getImagesOfTracking(tracking.getDocumentLinkId());
                        for (ImageOM trackingImg : trackingImgs) {
                            String url = AppConfig.getInstance().imageUrl + trackingImg.getPath() + "_" + trackingImg.getImgId();
                            trackingImgsResponse.add(url);
                        }
                        trackingResponse.setImages(trackingImgsResponse);
                    }
                    if (tracking.getToStatus().equals(consultOrderResponse.getStatusMobile().getCode())) {
                        consultOrderResponse.getStatusMobile().setNote(tracking.getNote());
                    }
                    trackingResponse.setStatusName(OrderStatusNameEnum.valueOf(trackingResponse.getToStatus()).message);
                    trackingResponses.add(trackingResponse);
                }
                consultOrderResponse.setTrackings(trackingResponses);
            }
            OrderDetailReport orderDetail = orderDetailReportRepository.findByOrderDetailCode(filterOrderDTO.getSvcOrderDetailCode());
            if (orderDetail != null) {
                OrderLogReport lastLog = shipOrderLogReportRepository.getLastNote(orderDetail.getId());
//            consultOrderResponse.setShipper(new FilterOrderShipperData(filterOrderDTO.getShipperName(), filterOrderDTO.getShipperPhone()));
                if (lastLog != null) {
                    AppUser shipper = appUserRepository.findAppUserById(lastLog.getShipId());
                    if (shipper != null)
                        consultOrderResponse.setShipper(new FilterOrderShipperData(shipper.getName(), shipper.getPhone()));
                }
            }
            //Ẩn thông tin nếu chưa đăng nhập
            if (username != null) {
                AppUser appUser = appUserRepository.findByPhone(username);
                if (appUser == null || appUser.getId() != filterOrderDTO.getShopId()) {
                    hideInfo(consultOrderResponse);
                }
            } else {
                hideInfo(consultOrderResponse);
            }
            consultOrderResponses.add(consultOrderResponse);
        }
        return new BaseResponse(200, consultOrderResponses);
    }

    private void hideInfo(ConsultOrderResponse consultOrderResponse) {
        consultOrderResponse.setShopName(hideText(consultOrderResponse.getShopName()));
        consultOrderResponse.setShopPhone(hideText(consultOrderResponse.getShopPhone()));
        consultOrderResponse.setShopAddress(hideText(consultOrderResponse.getShopAddress()));
//        consultOrderResponse.setShopProvince(hideText(consultOrderResponse.getShopProvince()));
//        consultOrderResponse.setShopDistrict(hideText(consultOrderResponse.getShopDistrict()));
//        consultOrderResponse.setShopWard(hideText(consultOrderResponse.getShopWard()));

        consultOrderResponse.setConsignee(hideText(consultOrderResponse.getConsignee()));
        consultOrderResponse.setPhone(hideText(consultOrderResponse.getPhone()));
        consultOrderResponse.setDeliveryAddress(hideText(consultOrderResponse.getDeliveryAddress()));
//        consultOrderResponse.setDeliveryProvince(hideText(consultOrderResponse.getDeliveryProvince()));
//        consultOrderResponse.setDeliveryDistrict(hideText(consultOrderResponse.getDeliveryDistrict()));
//        consultOrderResponse.setDeliveryWard(hideText(consultOrderResponse.getDeliveryWard()));
    }

    private String hideText(String clearText) {
        if (clearText != null) {
            int sub = 5;
            if (clearText.length() < sub)
                sub = 0;
            StringBuilder hidden = new StringBuilder();
            for (int i = 0; i < clearText.length() - sub; i++)
                hidden.append("*");
            return hidden + clearText.substring(clearText.length() - sub);
        }
        return null;
    }

}