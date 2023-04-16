package com.imedia.controller;

import com.google.gson.Gson;
import com.imedia.config.authentication.JwtTokenUtil;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.entity.ErrorCodesWebshop;
import com.imedia.service.pickupaddress.PickupAddressService;
import com.imedia.service.pickupaddress.model.CreatePickupAddressRequest;
import com.imedia.util.PreLoadStaticUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/pickupAddress")
public class PickupAddressController {
    static final Logger logger = LogManager.getLogger(UserController.class);
    static final Gson gson = new Gson();
    private final JwtTokenUtil jwtTokenUtil;
    private final PickupAddressService pickupAddressService;
    private static final HashMap<Integer, ErrorCodesWebshop> errorCodes = PreLoadStaticUtil.errorCodeWeb;

    @Autowired
    public PickupAddressController(JwtTokenUtil jwtTokenUtil, PickupAddressService pickupAddressService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.pickupAddressService = pickupAddressService;
    }

    @PostMapping()
    public ResponseEntity<?> createPickupAddress(@RequestBody CreatePickupAddressRequest pickupAddressRequest,
                                                 @RequestHeader("Authorization") String token) {
        logger.info("======CREATE PICKUP ADDRESS===== " + gson.toJson(pickupAddressRequest));
        if (pickupAddressRequest != null) {
            String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
            try {
                BaseResponse baseResponse = pickupAddressService.createShopAddress(pickupAddressRequest,tokenUsername);
                baseResponse.setRequestId(pickupAddressRequest.getRequestId());
//                    logger.info("======CREATE PICKUP ADDRESS RESPONSE===== " + gson.toJson(baseResponse));
                return ResponseEntity.ok().body(baseResponse);
            } catch (Exception e) {
                logger.info("======CREATE PICKUP ADDRESS EXCEPTION======" + gson.toJson(pickupAddressRequest), e);
            }
        } else return ResponseEntity.badRequest().body(new BaseResponse(400));
        return ResponseEntity.ok().body(new BaseResponse(500));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updatePickupAddress(@RequestBody CreatePickupAddressRequest pickupAddressRequest,
                                                 @RequestHeader("Authorization") String token) {
//        logger.info("======UPDATE PICKUP ADDRESS===== " + request);
        if (pickupAddressRequest != null) {
            String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
            try {
                BaseResponse baseResponse = pickupAddressService.updatePickupAddress(pickupAddressRequest,
                        pickupAddressRequest.getShopAddressId(), tokenUsername);
//                    logger.info("======UPDATE PICKUP ADDRESS RESPONSE===== " + gson.toJson(baseResponse));
                return ResponseEntity.ok().body(baseResponse);
            } catch (Exception e) {
                logger.info("======UPDATE USER INFO REQUEST EXCEPTION======" + gson.toJson(pickupAddressRequest), e);
            }
        }
        return ResponseEntity.ok().body(new BaseResponse(500));
    }

    @GetMapping()
    public ResponseEntity<?> filterShopAddress(@RequestParam Optional<String> key,
                                               @RequestParam Integer status,
                                               @RequestParam Optional<Integer> page,
                                               @RequestParam Optional<Integer> size, @RequestHeader("Authorization") String token) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
//            logger.info("========FILTER SHOP ADDRESS REQUEST=======" + key + "||" + status + "||" + page + "||" + size);
            return ResponseEntity.ok().body(pickupAddressService.filterPickupAddress(tokenUsername, key.orElse(""),
                    status, page.orElse(1), size.orElse(10)));
        }
        return ResponseEntity.badRequest().body(new BaseResponse(400));
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivatePickupAddress(@RequestHeader("Authorization") String token, @RequestParam Long shopAddressId) {
        return ResponseEntity.ok().body(pickupAddressService.deactivateShopAddressId(shopAddressId));
    }

//    @PostMapping("/public/aiAddress")
//    public ResponseEntity<?> aiAddress(@RequestBody String body) throws Exception {
//        AppConfig appConfig = AppConfig.getInstance();
//        String response = CallServer.getInstance().post(appConfig.addressUrl,body);
//        return ResponseEntity.ok().body(response);
//    }

}

