package com.imedia.controller;

import com.google.gson.Gson;
import com.imedia.config.authentication.JwtTokenUtil;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.entity.ErrorCodesWebshop;
import com.imedia.service.authenticate.model.SignInResponse;
import com.imedia.service.user.UserService;
import com.imedia.service.user.model.*;
import com.imedia.service.userwallet.UserWalletService;
import com.imedia.service.userwallet.model.*;
import com.imedia.util.PreLoadStaticUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class UserController {
    static final Logger logger = LogManager.getLogger(UserController.class);
    static final Gson gson = new Gson();
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserWalletService userWalletService;
    private static final HashMap<Integer, ErrorCodesWebshop> errorCodes = PreLoadStaticUtil.errorCodeWeb;


    @Autowired
    public UserController(UserService userService, JwtTokenUtil jwtTokenUtil, UserWalletService userWalletService) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userWalletService = userWalletService;
    }

    @PostMapping("/public/register")
    public ResponseEntity<?> register(@RequestBody SignUpRequest signUpRequest) {
        logger.info("======REGISTER REQUEST===== " + gson.toJson(signUpRequest));
        SignUpResponse signUpResponse = userService.register(signUpRequest);
        signUpResponse.setRequestId(signUpRequest.getRequestId());
        logger.info("======REGISTER RESPONSE===== " + gson.toJson(signUpResponse));
        return ResponseEntity.ok(signUpResponse);
    }

    @PostMapping("/public/verifyOTP")
    public ResponseEntity<?> verifyOTP(@RequestBody VerifyOTPRequest verifyOTPRequest) {
        logger.info("======VERIFY REQUEST===== " + gson.toJson(verifyOTPRequest));
        VerifyOTPResponse verifyOTPResponse = userService.verifyOTP(verifyOTPRequest);
        verifyOTPResponse.setRequestId(verifyOTPRequest.getRequestId());
        logger.info("======VERIFY RESPONSE===== " + gson.toJson(verifyOTPResponse));
        return ResponseEntity.ok(verifyOTPResponse);
    }

    @PostMapping("/public/getOTP")
    public ResponseEntity<?> getOTP(@RequestBody GetOTPRequest getOTPRequest) {
        logger.info("======GETOTP REQUEST===== " + gson.toJson(getOTPRequest));
        VerifyOTPResponse verifyOTPResponse = userService.getOTP(getOTPRequest);
        verifyOTPResponse.setRequestId(getOTPRequest.getRequestId());
        logger.info("======GETOTP RESPONSE===== " + gson.toJson(verifyOTPResponse));
        return ResponseEntity.ok(verifyOTPResponse);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token, @Param("username") String username) {
//        logger.info("======GET USER INFO REQUEST===== " + username);
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (!tokenUsername.equals(username))
            return ResponseEntity.badRequest().body(new SignInResponse("Tuy vấn sai username ứng với token", 402));
        else {
            try {
                BaseResponse userInfoResponse = userService.getUserInfo(username);
                return ResponseEntity.ok().body(userInfoResponse);
            } catch (Exception e) {
                logger.info("====GET ADDRESS INFO=====", e);
                return ResponseEntity.badRequest().body(new BaseResponse(500));
            }
        }
    }

    @PostMapping("/user")
    public ResponseEntity<?> updateUserInfo(@RequestHeader("Authorization") String token, @RequestBody UpdateUserInfoRequest updateUserInfoRequest) {
//        logger.info("======UPDATE USER INFO REQUEST===== " + request);
        if (updateUserInfoRequest != null) {
            String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
            if (!tokenUsername.equals(updateUserInfoRequest.getUsername()))
                return ResponseEntity.badRequest().body(new SignInResponse(errorCodes.get(402).getMessage(), 402));
            else {
                try {
                    BaseResponse userInfoResponse = userService.updateUserInfo(updateUserInfoRequest,
                            updateUserInfoRequest.getUsername());
                    userInfoResponse.setRequestId(updateUserInfoRequest.getRequestId());
//                    logger.info("====UPDATE USER INFO RESPONSE=====" + gson.toJson(userInfoResponse));
                    return ResponseEntity.ok().body(userInfoResponse);
                } catch (Exception e) {
                    logger.info("====UPDATE USER INFO EXCEPTION=====", e);
                    return ResponseEntity.badRequest().body(new BaseResponse(500));
                }
            }
        } else return ResponseEntity.badRequest().body(new BaseResponse(400));
    }

    @PostMapping("/user/updatePhone")
    public ResponseEntity<?> updateUserInfoPhone(@RequestHeader("Authorization") String token, @RequestBody UpdatePhoneRequest updatePhoneRequest) {
        if (updatePhoneRequest != null) {
            String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
            if (!tokenUsername.equals(updatePhoneRequest.getUsername()))
                return ResponseEntity.badRequest().body(new SignInResponse(errorCodes.get(402).getMessage(), 402));
            else {
                try {
                    BaseResponse userInfoResponse = userService.updatePhone(updatePhoneRequest);
                    userInfoResponse.setRequestId(updatePhoneRequest.getRequestId());
                    return ResponseEntity.ok().body(userInfoResponse);
                } catch (Exception e) {
                    logger.info("====UPDATE USER PHONE REQUEST EXCEPTION=====", e);
                    return ResponseEntity.badRequest().body(new BaseResponse(500));
                }
            }
        } else return ResponseEntity.badRequest().body(new BaseResponse(400));
    }

    @PostMapping("/public/user/resetPass")
    public ResponseEntity<?> resetPass(@RequestBody UserResetPassRequest userResetPassRequest) {
        if (userResetPassRequest != null) {
            try {
                logger.info("====RESET PASS REQUEST=====" + gson.toJson(userResetPassRequest));
                BaseResponse userInfoResponse = userService.resetPassword(userResetPassRequest);
                userInfoResponse.setRequestId(userResetPassRequest.getRequestId());
                return ResponseEntity.ok().body(userInfoResponse);
            } catch (Exception e) {
                logger.info("====RESET PASS REQUEST EXCEPTION=====", e);
                return ResponseEntity.badRequest().body(new BaseResponse(500));
            }
        }
        return null;
    }

    @PostMapping("/user/checkPermission")
    public ResponseEntity<?> checkPermission(@RequestHeader("Authorization") String token, @RequestBody CheckPermissionRequest permissionRequest) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        try {
            BaseResponse userInfoResponse = userService.checkPermission(permissionRequest, tokenUsername);
            return ResponseEntity.ok().body(userInfoResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BaseResponse(500));
        }
    }

    @PostMapping("/user/updateAvatar")
    public ResponseEntity<?> updateAvatar(@RequestHeader("Authorization") String token, @RequestBody UpdateAvatarRequest updateAvatarRequest) {
//        logger.info("======UPDATE AVATAR REQUEST===== " + request);
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (updateAvatarRequest != null) {
            if (!tokenUsername.equals(updateAvatarRequest.getUsername()))
                return ResponseEntity.badRequest().body(new SignInResponse("Tuy vấn sai username ứng với token", 402));
            else {
                try {
                    BaseResponse userInfoResponse = userService.updateAvatar(updateAvatarRequest);
                    userInfoResponse.setRequestId(updateAvatarRequest.getRequestId());
//                    logger.info("======UPDATE USER AVATAR RESPONSE===== " + gson.toJson(userInfoResponse));
                    return ResponseEntity.ok().body(userInfoResponse);
                } catch (Exception e) {
                    logger.info("====UPDATE USER PHONE REQUEST EXCEPTION=====", e);
                    return ResponseEntity.badRequest().body(new BaseResponse(500));
                }
            }
        }
        return ResponseEntity.badRequest().body(new BaseResponse(400));
    }

    @PostMapping("/user/registerVA")
    public ResponseEntity<?> createVA(@RequestHeader("Authorization") String token, @RequestBody UserRegisterVARequest registerVARequest) {
        logger.info("=======REGISTER VA REQUEST====" + gson.toJson(registerVARequest));
        if (registerVARequest != null) {
            String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
            String sessionKey = jwtTokenUtil.getSessionFromToken(token);
            registerVARequest.setSessionKey(sessionKey);
            if (!tokenUsername.equals(registerVARequest.getUsername()))
                return ResponseEntity.badRequest().body(new SignInResponse(errorCodes.get(402).getMessage(), 402));
            else {
                try {
                    BaseResponse baseResponse = userWalletService.createVA(registerVARequest);
                    logger.info("=======REGISTER VA RESPONSE====" + gson.toJson(baseResponse));
                    return ResponseEntity.ok().body(baseResponse);
                } catch (Exception e) {
                    logger.info("=======REGISTER VA EXCEPTION====" + registerVARequest.getUsername(), e);
                    return ResponseEntity.badRequest().body(new BaseResponse(500));
                }
            }
        }
        return ResponseEntity.badRequest().body(new BaseResponse(400));
    }

    @GetMapping("/user/bankVA")
    public ResponseEntity<?> getVA(@RequestHeader("Authorization") String token) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername == null)
            return ResponseEntity.badRequest().body(new SignInResponse(errorCodes.get(402).getMessage(), 402));
        else {
            return ResponseEntity.ok().body(userWalletService.getVA(tokenUsername));
        }
    }


    @PostMapping("/user/addWithdrawBankAccount")
    public ResponseEntity<?> addWithdrawBankAccount(@RequestHeader("Authorization") String token,
                                                    @RequestBody AddBankWithdrawAccountRequest withdrawAccountRequest) {
        logger.info("======ADD WITHDRAW BANK REQUEST===== " + gson.toJson(withdrawAccountRequest));
        if (withdrawAccountRequest != null) {
            String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
            if (!tokenUsername.equals(withdrawAccountRequest.getUsername())) {
                SignInResponse signInResponse = new SignInResponse(errorCodes.get(402).getMessage(), 402);
                logger.info("======ADD WITHDRAW BANK RESPONSE===== " + gson.toJson(signInResponse));
                return ResponseEntity.badRequest().body(signInResponse);
            } else {
                try {
                    String accUsername = StringUtils.stripAccents(withdrawAccountRequest.getAccUsername().replaceAll("đ", ""));
                    withdrawAccountRequest.setAccUsername(accUsername);
                    BaseResponse baseResponse = userWalletService.addBankAccount(withdrawAccountRequest);
                    logger.info("======ADD WITHDRAW BANK RESPONSE===== " + gson.toJson(baseResponse));
                    return ResponseEntity.ok().body(baseResponse);
                } catch (Exception e) {
                    logger.info("=======ADD WITHDRAW BANK EXCEPTION====" + withdrawAccountRequest.getUsername(), e);
                    return ResponseEntity.badRequest().body(new BaseResponse(500));
                }
            }
        }
        return ResponseEntity.badRequest().body(new BaseResponse(400));
    }

    @PostMapping("/user/deleteBankAccount")
    public ResponseEntity<?> deleteBankAccount(@RequestHeader("Authorization") String token,
                                               @RequestParam("id") BigDecimal id) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername == null)
            return ResponseEntity.badRequest().body(new BaseResponse(402));
        logger.info("======DELETE BANK ACCOUNT REQUEST===== " + id + "||username" + tokenUsername);
        return ResponseEntity.ok().body(userWalletService.deleteBankAccount(tokenUsername, id));
    }

    @GetMapping("/user/bank")
    public ResponseEntity<?> getListBankAccount(@RequestHeader("Authorization") String token,
                                                @RequestParam("withdrawType") Integer withdrawType) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
//        logger.info("======GET LIST BANK=====" + tokenUsername);
        BaseResponse baseResponse = userWalletService.getListBankAccount(tokenUsername, withdrawType);
//        logger.info("======GET LIST BANK RESPONSE=====" + gson.toJson(baseResponse));
        return ResponseEntity.ok().body(baseResponse);
    }

    @PostMapping("/public/user/depositVA")
    public NotifyBalanceResponse depositVA(@RequestBody String request) {
        logger.info("======DEPOSIT VA REQUEST=====" + request);
        NotifyBalanceRequest balanceRequest = gson.fromJson(request, NotifyBalanceRequest.class);
        try {
            userWalletService.depositVA(balanceRequest);
        } catch (Exception e) {
            logger.info("======DEPOSIT VA EXCEPTION=====" + request, e);
        }
        return new NotifyBalanceResponse("200", "Thành công", null);
    }

    @PostMapping("/user/walletLogs")
    public ResponseEntity<?> filterWalletLogs(@RequestHeader("Authorization") String token,
                                              @RequestBody FilterWalletLogRequest walletLogRequest) {
//        logger.info("======FILTER WALLET LOGS REQUEST===== " + request);
        if (walletLogRequest != null) {
            try {
                BaseResponse baseResponse = userWalletService.filterWalletLogs(walletLogRequest);
//                logger.info("=======FILTER WALLET LOGS RESPONSE========" + gson.toJson(baseResponse));
                return ResponseEntity.ok().body(baseResponse);
            } catch (Exception e) {
                logger.info("======FILTER WALLET LOGS EXCEPTION===== " + gson.toJson(walletLogRequest), e);

            }
        }
        return ResponseEntity.badRequest().body(new BaseResponse(500));
    }

    @PostMapping("/user/withdraw")
    public ResponseEntity<?> withdrawBank(@RequestHeader("Authorization") String token,
                                          @RequestBody UserWithdrawRequest userWithdrawRequest) {
        logger.info("======WITHDRAW TO BANK REQUEST===== " + gson.toJson(userWithdrawRequest));
        try {
            String sessionKey = jwtTokenUtil.getSessionFromToken(token);
            userWithdrawRequest.setSessionKey(sessionKey);
            BaseResponse baseResponse = userWalletService.withdrawBank(userWithdrawRequest);
            logger.info("=======WITHDRAW TO BANK RESPONSE========" + gson.toJson(baseResponse));
            return ResponseEntity.ok().body(baseResponse);
        } catch (Exception e) {
            logger.info("======WITHDRAW TO BANK EXCEPTION===== " + gson.toJson(userWithdrawRequest), e);
            return ResponseEntity.badRequest().body(new BaseResponse(500));
        }
    }

    @GetMapping("/user/balances")
    public ResponseEntity<?> getBalances(@RequestHeader("Authorization") String token) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            UserBalanceResponse userBalanceResponse = userWalletService.getBalances(tokenUsername);
            if (userBalanceResponse == null)
                return ResponseEntity.ok().body(new BaseResponse(500));
            else return ResponseEntity.ok().body(new BaseResponse(200, userBalanceResponse));
        }
        return ResponseEntity.badRequest().body(new BaseResponse(402));
    }

    @PostMapping("/user/changePass")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String token,
                                            @RequestBody ChangePasswordRequest changePasswordRequest) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        String sessionKey = jwtTokenUtil.getSessionFromToken(token);
        if (tokenUsername != null) {
            changePasswordRequest.setUsername(tokenUsername);
            changePasswordRequest.setSessionKey(sessionKey);
            BaseResponse baseResponse = userService.changePass(changePasswordRequest);
            logger.info("=========CHANGE PASS RESPONSE=========" + tokenUsername + "||" + gson.toJson(baseResponse));
            return ResponseEntity.ok().body(baseResponse);
        }
        return ResponseEntity.badRequest().body(new BaseResponse(402));
    }

    @PostMapping("/user/contract")
    public ResponseEntity<?> createContract(@RequestHeader("Authorization") String token,
                                            @RequestBody CreateContractRequest createContractRequest) {
        String tokenUsername = jwtTokenUtil.getUsernameFromToken(token);
        if (tokenUsername != null) {
            logger.info("========CREATE CONTRACT REQUEST=========" + tokenUsername + "||" + gson.toJson(createContractRequest));
            BaseResponse baseResponse = userService.createContract(tokenUsername, createContractRequest);
            logger.info("========CREATE CONTRACT RESPONSE=========" + tokenUsername + "||" + gson.toJson(baseResponse));
            return ResponseEntity.ok().body(baseResponse);
        }
        return ResponseEntity.badRequest().body(new BaseResponse(402));
    }
}
