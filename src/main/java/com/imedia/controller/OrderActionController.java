package com.imedia.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.config.authentication.JwtTokenUtil;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.entity.ErrorCodesWebshop;
import com.imedia.service.order.OrderActionService;
import com.imedia.service.order.model.CancelOrderRequest;
import com.imedia.service.order.model.ConfirmPartialRequest;
import com.imedia.service.order.model.DeleteOrderRequest;
import com.imedia.service.order.model.PrintOrderRequest;
import com.imedia.util.PreLoadStaticUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class OrderActionController {
    static final Logger logger = LogManager.getLogger(OrderController.class);
    static final Gson gson = new GsonBuilder().serializeNulls().create();
    private final JwtTokenUtil jwtTokenUtil;
    private static final HashMap<Integer, ErrorCodesWebshop> errorCodes = PreLoadStaticUtil.errorCodeWeb;
    private final OrderActionService orderActionService;

    @Autowired
    public OrderActionController(JwtTokenUtil jwtTokenUtil, OrderActionService orderActionService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.orderActionService = orderActionService;
    }

    @PostMapping("/reDelivery")
    public ResponseEntity<?> redeliveryOrder(@RequestHeader("Authorization") String token,
                                             @RequestParam("orderDetailCode") BigDecimal orderDetailCode) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            try {
                logger.info("==========REDELIVERY REQUEST==========" + gson.toJson(orderDetailCode));
                BaseResponse response = orderActionService.reDeliveryOrder(orderDetailCode);
                logger.info("==========REDELIVERY RESPONSE==========" + gson.toJson(response));
                return ResponseEntity.ok().body(response);
            } catch (Exception e) {
                logger.info("======REDELIVERY EXCEPTION======" + orderDetailCode, e);
                return ResponseEntity.ok().body(new BaseResponse(500));
            }
        } else return ResponseEntity.ok().body(new BaseResponse(401));
    }

    @PostMapping("/cancelOrder")
    public ResponseEntity<?> cancelOrder(@RequestHeader("Authorization") String token,
                                         @RequestBody CancelOrderRequest cancelOrderRequest) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            try {
                logger.info("==========CANCEL REQUEST==========" + gson.toJson(cancelOrderRequest));
                BaseResponse response = orderActionService.cancelOrder(cancelOrderRequest, tokenUsername);
                logger.info("==========CANCEL RESPONSE==========" + cancelOrderRequest.getOrderCode() + "||" + gson.toJson(response));
                return ResponseEntity.ok().body(response);
            } catch (Exception e) {
                logger.info("======CANCEL EXCEPTION======" + gson.toJson(cancelOrderRequest), e);
                return ResponseEntity.ok().body(new BaseResponse(500));
            }
        } else return ResponseEntity.ok().body(new BaseResponse(401));
    }

    @PostMapping("/refundOrder")
    public ResponseEntity<?> refundOrder(@RequestHeader("Authorization") String token,
                                         @RequestParam("orderDetailCode") BigDecimal orderCode) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            try {
                logger.info("==========REFUND ORDER REQUEST==========" + orderCode);
                BaseResponse response = orderActionService.refundOrder(orderCode, tokenUsername);
                logger.info("==========REFUND ORDER RESPONSE==========" + gson.toJson(response));
                return ResponseEntity.ok().body(response);
            } catch (Exception e) {
                logger.info("======REFUND ORDER EXCEPTION======" + orderCode, e);
                return ResponseEntity.ok().body(new BaseResponse(500));
            }
        } else return ResponseEntity.ok().body(new BaseResponse(401));
    }

    @PostMapping("/confirmPartialRequest")
    public ResponseEntity<?> confirmPartialRequest(@RequestHeader("Authorization") String token,
                                                   @RequestBody ConfirmPartialRequest confirmPartialRequest) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername == null) return ResponseEntity.ok().body(new BaseResponse(401));
        try {
            logger.info("==========CONFIRM PARTIAL REQUEST==========" + confirmPartialRequest.getOrderDetailCode());
            BaseResponse response = orderActionService.orderConfirmPartialRequest(confirmPartialRequest, tokenUsername);
            logger.info("==========CONFIRM PARTIAL RESPONSE==========" + gson.toJson(response));
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.info("======CONFIRM PARTIAL EXCEPTION======" + confirmPartialRequest.getOrderDetailCode(), e);
            return ResponseEntity.ok().body(new BaseResponse(500));
        }
    }

    @PostMapping("/printOrders")
    public ResponseEntity<?> getOrderToPrint(@RequestHeader("Authorization") String token,
                                             @RequestBody PrintOrderRequest printOrderRequest) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername == null) return ResponseEntity.ok().body(new BaseResponse(401));
        try {
            logger.info("====PRINT ORDER REQUEST======" + gson.toJson(printOrderRequest));
            BaseResponse response = orderActionService.getOrderDataToPrint(printOrderRequest, tokenUsername);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.info("======PRINT EXCEPTION======" + gson.toJson(printOrderRequest), e);
            return ResponseEntity.ok().body(new BaseResponse(500));
        }
    }

    @PostMapping("/deleteOrder")
    public ResponseEntity<?> deleteOrder(@RequestHeader("Authorization") String token,
                                         @RequestBody DeleteOrderRequest deleteOrderRequest) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername == null) return ResponseEntity.ok().body(new BaseResponse(401));
        try {
            logger.info("====DELETE ORDER REQUEST======" + gson.toJson(deleteOrderRequest));
            if (deleteOrderRequest.getOrderCodes() == null && deleteOrderRequest.getGroupStatus() == null)
                return ResponseEntity.ok().body(new BaseResponse(614));
            BaseResponse response = orderActionService.deleteOrder(deleteOrderRequest, tokenUsername);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            logger.info("======DELETE ORDER EXCEPTION======" + gson.toJson(deleteOrderRequest), e);
            return ResponseEntity.ok().body(new BaseResponse(500));
        }
    }


}
