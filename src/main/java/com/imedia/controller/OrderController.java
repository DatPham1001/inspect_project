package com.imedia.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.config.authentication.JwtTokenUtil;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.entity.AppUser;
import com.imedia.oracle.entity.ErrorCodesWebshop;
import com.imedia.oracle.reportentity.ProductCategoryReport;
import com.imedia.oracle.reportrepository.ProductCategoryReportRepo;
import com.imedia.oracle.repository.AppUserRepository;
import com.imedia.service.bank.BankService;
import com.imedia.service.order.*;
import com.imedia.service.order.model.*;
import com.imedia.service.postage.PostageService;
import com.imedia.util.PreLoadStaticUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {
    static final Logger logger = LogManager.getLogger(OrderController.class);
    static final Gson gson = new GsonBuilder().serializeNulls().create();
    private final JwtTokenUtil jwtTokenUtil;
    private final PostageService postageService;
    private final OrderService orderService;
    private static final HashMap<Integer, ErrorCodesWebshop> errorCodes = PreLoadStaticUtil.errorCodeWeb;
    private final AppUserRepository appUserRepository;
    private final OrderFileService orderFileService;
    private final OrderReadService orderReadService;
    private final OrderUpdateService orderUpdateService;
    private final OrderConfirmService orderConfirmService;
    private final AddressDeliveryService addressDeliveryService;
    private final ProductCategoryReportRepo productCategoryReportRepo;
    private final BankService bankService;

    @Autowired
    public OrderController(JwtTokenUtil jwtTokenUtil, PostageService postageService, OrderService orderService, AppUserRepository appUserRepository, OrderFileService orderFileService, OrderReadService orderReadService, OrderUpdateService orderUpdateService, OrderConfirmService orderConfirmService, AddressDeliveryService addressDeliveryService, ProductCategoryReportRepo productCategoryReportRepo, BankService bankService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.postageService = postageService;
        this.orderService = orderService;
        this.appUserRepository = appUserRepository;
        this.orderFileService = orderFileService;
        this.orderReadService = orderReadService;
        this.orderUpdateService = orderUpdateService;
        this.orderConfirmService = orderConfirmService;
        this.addressDeliveryService = addressDeliveryService;
        this.productCategoryReportRepo = productCategoryReportRepo;
        this.bankService = bankService;
    }

    @GetMapping("/public/postage")
    public ResponseEntity<?> getPostage() {
        return ResponseEntity.ok().body(postageService.getPostage());
    }

    @PostMapping("/order/addressDelivery")
    public ResponseEntity<?> createAddressDelivery(@RequestBody AddressDeliveryRequest addressDeliveryRequest) {
        BaseResponse response = null;
        try {
            response = addressDeliveryService.createAddressDelivery(addressDeliveryRequest);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new BaseResponse(500));
        }
    }

    @PostMapping("/order/calculateFee")
    public ResponseEntity<?> calculateFee(@RequestHeader("Authorization") String token, @RequestBody CreateOrder createOrder) {
//    public ResponseEntity<?> calculateFee(@RequestHeader("Authorization") String token, @RequestBody String request) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            try {
//                logger.info("======CALCULATE FEE REQUEST=======" + request);
//                CreateOrder createOrder = gson.fromJson(request, CreateOrder.class);
                logger.info("======CALCULATE FEE REQUEST=======" + gson.toJson(createOrder));
                AppUser appUser = appUserRepository.findByPhone(tokenUsername);
                BaseResponse response = postageService.calculateTotalFee(createOrder, appUser);
                logger.info("======CALCULATE FEE RESPONSE======" + gson.toJson(response));
                return ResponseEntity.ok().body(response);
            } catch (Exception e) {
                logger.info("======CALCULATE FEE EXCEPTION======", e);
                return ResponseEntity.ok().body(new BaseResponse(500));
            }
        } else return ResponseEntity.ok().body(new BaseResponse(401));
    }

    @PostMapping("/order")
    public ResponseEntity<?> createOrder(@RequestHeader("Authorization") String token,
                                         @RequestHeader("Channel") String channel,
                                         @RequestBody CreateOrder createOrder) {
//        try{
//             = gson.fromJson(request,CreateOrder.class);
//        }
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            try {
                logger.info("======CREATE ORDER REQUEST======" + gson.toJson(createOrder) + "||" + tokenUsername);
                BaseResponse response = orderService.handleCreateOrder(createOrder, tokenUsername, channel);
                logger.info("======CREATE ORDER RESPONSE======" + createOrder.getRequestId() + "||" + gson.toJson(response));
                return ResponseEntity.ok().body(response);
            } catch (Exception e) {
                logger.info("======CREATE ORDER EXCEPTION======", e);
                return ResponseEntity.ok().body(new BaseResponse(500));
            }
        } else return ResponseEntity.ok().body(new BaseResponse(401));
    }

    @PostMapping("/orderFile")
    public ResponseEntity<?> createFileOrder(@RequestHeader("Authorization") String token,
                                             @RequestBody List<CreateOrderFile> createOrderFiles) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            try {
                AppUser appUser = appUserRepository.findByPhone(tokenUsername);
                BaseResponse response = orderFileService.createFileOrder(createOrderFiles, tokenUsername);
                return ResponseEntity.ok().body(response);
            } catch (Exception e) {
                logger.info("======CREATE FILE ORDER EXCEPTION======", e);
                return ResponseEntity.badRequest().body(new BaseResponse(500));
            }
        } else return ResponseEntity.status(401).body(new BaseResponse(401));
    }

    @PostMapping("/orderFile/calculateFee")
    public ResponseEntity<?> calculateFeeFilOrder(@RequestHeader("Authorization") String token,
                                                  @RequestBody List<CreateOrderFile> createOrderFiles) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            try {
                logger.info("======CALCULATE FEE FILE REQUEST=======" + gson.toJson(createOrderFiles));
                AppUser appUser = appUserRepository.findByPhone(tokenUsername);
                BaseResponse response = postageService.calculateFeeOrderFile(createOrderFiles, appUser);
                logger.info("======CREATE ORDER RESPONSE======" + gson.toJson(response));
                return ResponseEntity.ok().body(response);
            } catch (Exception e) {
                logger.info("======CREATE ORDER EXCEPTION======" + gson.toJson(createOrderFiles), e);
                return ResponseEntity.ok().body(new BaseResponse(500));
            }
        } else return ResponseEntity.ok().body(new BaseResponse(401));
    }

//    @PostMapping("/orderFile/filterPackages")
//    public ResponseEntity<?> filterPackages(@RequestHeader("Authorization") String token,
//                                            @RequestBody List<CreateOrderFile> createOrderFiles) {
//        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
//        if (tokenUsername != null) {
//            try {
//                return ResponseEntity.ok().body(postageService.filterPackagesForOrderFile(createOrderFiles));
//            } catch (Exception e) {
//                logger.info("======FILTER PACKAGE ORDER FILE EXCEPTION======", e);
//                return ResponseEntity.ok().body(new BaseResponse(500));
//            }
//        } else return ResponseEntity.ok().body(new BaseResponse(401));
//    }

    @PostMapping("/getOrders")
    public ResponseEntity<?> filterOrders(@RequestHeader("Authorization") String token, @RequestBody
            FilterOrderRequest filterOrderRequest) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            try {
                return ResponseEntity.ok().body(orderReadService.filterOrder(filterOrderRequest, tokenUsername));
            } catch (Exception e) {
                logger.info("=====GET ORDERS EXCEPTION======" + gson.toJson(filterOrderRequest), e);
                return ResponseEntity.ok().body(new BaseResponse(500));
            }
        } else return ResponseEntity.ok().body(new BaseResponse(401));
    }

    @PostMapping("/getOrderDetails")
    public ResponseEntity<?> filterOrderDetails(@RequestHeader("Authorization") String token, @RequestBody
            FilterOrderRequest filterOrderRequest) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            try {
                return ResponseEntity.ok().body(orderReadService.filterOrderDetails(filterOrderRequest, tokenUsername));
            } catch (Exception e) {
                logger.info("======GET ORDER DETAILS EXCEPTION======" + gson.toJson(filterOrderRequest), e);
                return ResponseEntity.ok().body(new BaseResponse(500));
            }
        } else return ResponseEntity.ok().body(new BaseResponse(401));
    }

    @PostMapping("/getSumOrderStatus")
    public ResponseEntity<?> getSumOrderStatus(@RequestHeader("Authorization") String token, @RequestBody
            FilterOrderRequest filterOrderRequest) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            try {
                return ResponseEntity.ok().body(orderReadService.getSumOrderStatus(filterOrderRequest, tokenUsername));
            } catch (Exception e) {
                logger.info("======GET SUM ORDER STATUS EXCEPTION======" + gson.toJson(filterOrderRequest), e);
                return ResponseEntity.ok().body(new BaseResponse(500));
            }
        } else return ResponseEntity.ok().body(new BaseResponse(401));
    }

    @GetMapping("/getOrderDetail")
    public ResponseEntity<?> getOrderDetailById(@RequestHeader("Authorization") String token,
                                                @RequestParam("orderId") BigDecimal id) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            try {
                return ResponseEntity.ok().body(orderReadService.getOrderDetailById(id, tokenUsername));
            } catch (Exception e) {
                logger.info("======GET ORDER DETAIL BY ID EXCEPTION======" + id, e);
                return ResponseEntity.ok().body(new BaseResponse(500));
            }
        } else return ResponseEntity.ok().body(new BaseResponse(401));
    }

    @PostMapping("/orderDraft")
    public ResponseEntity<?> draftOrder(@RequestHeader("Authorization") String token, @RequestBody OrderDraftRequest request) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            try {
                return ResponseEntity.ok().body(orderService.draftOrder(request, tokenUsername));
            } catch (Exception e) {
                logger.info("======DRAFT ORDER EXCEPTION======", e);
                return ResponseEntity.ok().body(new BaseResponse(500));
            }
        } else return ResponseEntity.ok().body(new BaseResponse(401));
    }

    @PostMapping("/confirmOrder")
    public ResponseEntity<?> confirmOrder(@RequestHeader("Authorization") String token,
                                          @RequestBody ConfirmOrderRequest confirmOrderRequest) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            try {
                logger.info("======CONFIRM ORDER REQUEST=======" + gson.toJson(confirmOrderRequest));
                if (confirmOrderRequest.getOrderCodes() == null)
                    confirmOrderRequest.setOrderCodes("");
                if (confirmOrderRequest.getAction() == null)
                    confirmOrderRequest.setAction("");
                BaseResponse baseResponse = orderConfirmService.handleConfirmOrder(confirmOrderRequest, tokenUsername);
                logger.info("======CONFIRM ORDER RESPONSE=======" + gson.toJson(baseResponse));
                return ResponseEntity.ok().body(baseResponse);
            } catch (Exception e) {
                logger.info("======CONFIRM ORDER EXCEPTION======" + gson.toJson(confirmOrderRequest), e);
                return ResponseEntity.ok().body(new BaseResponse(500));
            }
        } else return ResponseEntity.ok().body(new BaseResponse(401));
    }

    @PostMapping("/updateOrder")
    public ResponseEntity<?> updateOrder(@RequestHeader("Authorization") String token,
                                         @RequestBody CreateOrder createOrder,
                                         @RequestParam("orderDetailCode") BigDecimal orderDetailCode) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            try {
                logger.info("==========UPDATE ORDER REQUEST==========" + orderDetailCode + "||" + gson.toJson(createOrder));
                BaseResponse response = orderUpdateService.handleUpdateOrderInfo(createOrder, tokenUsername, orderDetailCode);
                logger.info("==========UPDATE ORDER RESPONSE==========" + gson.toJson(response));
                return ResponseEntity.ok().body(response);
            } catch (Exception e) {
                logger.info("======UPDATE ORDER INFO EXCEPTION======" + orderDetailCode, e);
                return ResponseEntity.ok().body(new BaseResponse(500));
            }
        } else return ResponseEntity.ok().body(new BaseResponse(401));
    }

    @PostMapping("/changeCod")
    public ResponseEntity<?> updateOrderChangeCod(@RequestHeader("Authorization") String token,
                                                  @RequestBody CreateOrder createOrder,
                                                  @RequestParam("orderDetailCode") BigDecimal orderDetailCode) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            try {
                logger.info("==========UPDATE COD REQUEST==========" + orderDetailCode + "||" + gson.toJson(createOrder));
                BaseResponse response = orderUpdateService.handleChangeCOD(createOrder, tokenUsername, orderDetailCode);
                logger.info("==========UPDATE COD RESPONSE==========" + gson.toJson(response));
                return ResponseEntity.ok().body(response);
            } catch (Exception e) {
                logger.info("======UPDATE COD INFO EXCEPTION======" + orderDetailCode, e);
                return ResponseEntity.ok().body(new BaseResponse(500));
            }
        } else return ResponseEntity.ok().body(new BaseResponse(401));
    }

    @PostMapping("/updateWTCOrder")
    public ResponseEntity<?> updateWTCOrder(@RequestHeader("Authorization") String token,
                                            @RequestBody CreateOrder createOrder,
                                            @RequestParam("orderCode") BigDecimal orderCode) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            try {
                logger.info("==========UPDATE WTC ORDER REQUEST==========" + orderCode + "||" + gson.toJson(createOrder));
                BaseResponse response = orderUpdateService.handleUpdateWaitToConfirmOrder(createOrder, tokenUsername, orderCode);
                logger.info("==========UPDATE WTC ORDER RESPONSE==========" + gson.toJson(response));
                return ResponseEntity.ok().body(response);
            } catch (Exception e) {
                logger.info("======UPDATE ORDER INFO EXCEPTION======" + orderCode, e);
                return ResponseEntity.ok().body(new BaseResponse(500));
            }
        } else return ResponseEntity.ok().body(new BaseResponse(401));
    }

    @GetMapping("/getWTCOrder")
    public ResponseEntity<?> getWTCOrder(@RequestHeader("Authorization") String token, @RequestParam("orderDetailCode") BigDecimal orderDetailCode) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            try {
                logger.info("==========GET WTC ORDER==========" + orderDetailCode + "||" + tokenUsername);
                BaseResponse response = orderUpdateService.getWTCOrderToUpdate(orderDetailCode, tokenUsername);
                logger.info("==========GET WTC ORDER RESPONSE==========" + gson.toJson(response));
                return ResponseEntity.ok().body(response);
            } catch (Exception e) {
                logger.info("======GET WTC ORDER EXCEPTION======" + orderDetailCode, e);
                return ResponseEntity.ok().body(new BaseResponse(500));
            }
        } else return ResponseEntity.ok().body(new BaseResponse(401));
    }

    @GetMapping("/public/addressCode")
    public ResponseEntity<?> getAddressCode(@RequestParam("code") String code, @RequestParam("filterString") String filterString) {
        try {
            BaseResponse response = addressDeliveryService.getAddressCode(code, filterString);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.info("======GET ADDRESS CODE EXCEPTION======" + code, e);
            return ResponseEntity.ok().body(new BaseResponse(500));
        }
    }

    @GetMapping("/public/categories")
    public ResponseEntity<?> getCategories() {
        try {
            List<ProductCategoryReport> categoryReports = productCategoryReportRepo.findAll();
            return ResponseEntity.ok().body(new BaseResponse(200, categoryReports));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new BaseResponse(500));
        }
    }

    @PostMapping("/getConsignees")
    public ResponseEntity<?> getConsignees(@RequestHeader("Authorization") String token,
                                           @RequestBody FilterConsigneeRequest consigneeRequest) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername == null) return ResponseEntity.ok().body(new BaseResponse(401));
        try {
            logger.info("====GET CONSIGNEES REQUEST======" + gson.toJson(consigneeRequest));
            BaseResponse response = orderReadService.getBoughtUser(consigneeRequest, tokenUsername);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.info("======GET CONSIGNEES EXCEPTION======" + gson.toJson(consigneeRequest), e);
            return ResponseEntity.ok().body(new BaseResponse(500));
        }
    }

    @GetMapping("/public/banks")
    public ResponseEntity<?> getBankList(@RequestParam String searchString) {
        try {
            BaseResponse response = bankService.getBankList(searchString);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new BaseResponse(500));
        }
    }

    @PostMapping("/public/consultOrders")
    public ResponseEntity<?> getConsultOrders(@RequestHeader(value = "Authorization", required = false) String token,
                                              @RequestBody ConsultOrderRequest consultOrderRequest) {
        try {
            String tokenUsername = null;
            if (token != null)
                tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
            BaseResponse response = orderReadService.getConsultOrders(consultOrderRequest,tokenUsername);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.info("======CONSULT ORDERS EXCEPTION======" + gson.toJson(consultOrderRequest), e);
            return ResponseEntity.ok().body(new BaseResponse(500));
        }
    }
}

