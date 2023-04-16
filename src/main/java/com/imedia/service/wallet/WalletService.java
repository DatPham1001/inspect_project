package com.imedia.service.wallet;

import com.google.gson.Gson;
import com.imedia.config.application.AppConfig;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.entity.AppUser;
import com.imedia.oracle.entity.Bank;
import com.imedia.oracle.entity.BankAccount;
import com.imedia.oracle.entity.WalletLog;
import com.imedia.oracle.repository.AppUserRepository;
import com.imedia.oracle.repository.BankAccountRepository;
import com.imedia.oracle.repository.WalletLogRepository;
import com.imedia.oracle.repository.WalletRepository;
import com.imedia.service.authenticate.model.SignInRequest;
import com.imedia.service.authenticate.model.SignInSocialWalletRequest;
import com.imedia.service.authenticate.model.SignInWalletRequest;
import com.imedia.service.user.model.*;
import com.imedia.service.userwallet.model.*;
import com.imedia.service.wallet.enums.WalletLogEnum;
import com.imedia.service.wallet.enums.WalletPrCode;
import com.imedia.service.wallet.enums.WalletSubTypeCode;
import com.imedia.service.wallet.model.*;
import com.imedia.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WalletService {
    static final Logger logger = LogManager.getLogger(WalletService.class);
    static final Gson gson = new Gson();
    final int CLIENT_TYPE = 3;
    private final WalletRepository walletRepository;
    private final WalletLogRepository walletLogRepository;
    private final BankAccountRepository bankAccountRepository;
    static final List<Integer> withdrawFailCode = Arrays.asList(103, 104, 105, 108, 116, 115, 117, 138, 143, 120, 135);
    static final List<Integer> walletFailedCode = Arrays.asList(103, 104, 105, 106, 108, 116, 115, 117, 138, 143, 120, 135);
    private final AppUserRepository appUserRepository;

    //    static final String prefixAddDebt;
//    static final String prefixSubtractDebt;
    @Autowired
    public WalletService(WalletRepository walletRepository, WalletLogRepository walletLogRepository, BankAccountRepository bankAccountRepository, AppUserRepository appUserRepository) {
        this.walletRepository = walletRepository;
        this.walletLogRepository = walletLogRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.appUserRepository = appUserRepository;
    }

    public WalletBaseResponse login(SignInRequest signInRequest) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        //Login username password
        if (signInRequest.getLoginFrom() == 0) {
            SignInWalletRequest signInWalletRequest = new SignInWalletRequest();
            signInWalletRequest.setUsername(signInRequest.getUsername());
            signInWalletRequest.setLogin_from(signInRequest.getLoginFrom());
            signInWalletRequest.setPassword(signInRequest.getPassword());


//        if(signInRequest.getLoginFrom() == 1)
            String request = gson.toJson(signInWalletRequest);
            String encryptData = "";
            try {
                encryptData = TripleDES.encrypt(appConfig.epurse_key, request);
            } catch (Exception e) {
                logger.info("=====ENCRYPT EXCEPTION===== : " + request, e);
                e.printStackTrace();
            }
            if (!encryptData.isEmpty()) {
                WalletBaseRequest baseRequest = new WalletBaseRequest(WalletPrCode.PR_LOGIN.code,
                        encryptData, "", "", appConfig.epurse_service_code);
                if (signInRequest.getRememberMe() == 1)
                    baseRequest.setSessionExpectLifeTime(appConfig.rememberMeCache);
                else
                    baseRequest.setSessionExpectLifeTime(DateUtil.getRemainSecondsTilNextDay(3));
                String response = CallServer.getInstance().post(appConfig.epurse_url, gson.toJson(baseRequest));
                if (response != null && !response.isEmpty()) {
                    WalletBaseResponse baseResponse = gson.fromJson(response, WalletBaseResponse.class);
                    return baseResponse;
                }
            }
        }
        //Login social
        else {
            SignInSocialWalletRequest signInSocialWalletRequest = new SignInSocialWalletRequest();
            signInSocialWalletRequest.setUsername(signInRequest.getUsername());
            signInSocialWalletRequest.setPhone(signInRequest.getUsername());
            signInSocialWalletRequest.setOther_system_auth_user_id(signInRequest.getAccessToken());
            signInSocialWalletRequest.setLogin_from(signInRequest.getLoginFrom());
            signInSocialWalletRequest.setEmail(signInRequest.getEmail());
            String request = gson.toJson(signInSocialWalletRequest);
            String encryptData = "";
            try {
                encryptData = TripleDES.encrypt(appConfig.epurse_key, request);
            } catch (Exception e) {
                logger.info("=====ENCRYPT EXCEPTION===== : " + request, e);
                e.printStackTrace();
            }
            if (!encryptData.isEmpty()) {
                WalletBaseRequest baseRequest = new WalletBaseRequest(WalletPrCode.PR_LOGIN_SOCIAL.code,
                        encryptData, "", "", appConfig.epurse_service_code);
                String response = CallServer.getInstance().post(appConfig.epurse_url, gson.toJson(baseRequest));
                if (response != null && !response.isEmpty()) {
                    WalletBaseResponse baseResponse = gson.fromJson(response, WalletBaseResponse.class);
                    return baseResponse;
                }
            }
        }
        return null;
    }

    public WalletBaseResponse getOTP(GetOTPRequest getOTPRequest) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        GetOTPWalletRequest getOTPWalletRequest = new GetOTPWalletRequest();
        getOTPWalletRequest.setUsername(getOTPRequest.getUsername());
        getOTPWalletRequest.setPhone(getOTPRequest.getPhone());
        getOTPWalletRequest.setClient_identity_str(getOTPRequest.getDeviceId());
        String request = gson.toJson(getOTPWalletRequest);
        String encryptData = "";
        try {
            encryptData = TripleDES.encrypt(appConfig.epurse_key, request);
        } catch (Exception e) {
            logger.info("=====ENCRYPT EXCEPTION===== : " + request, e);
            e.printStackTrace();
        }
        if (!encryptData.isEmpty()) {
            WalletBaseRequest baseRequest = new WalletBaseRequest(WalletPrCode.PR_GET_OTP.code,
                    encryptData, "", "", appConfig.epurse_service_code);
            String response = CallServer.getInstance().post(appConfig.epurse_url, gson.toJson(baseRequest));
            if (response != null && !response.isEmpty()) {
                WalletBaseResponse baseResponse = gson.fromJson(response, WalletBaseResponse.class);
                return baseResponse;
            }
        }
        return new WalletBaseResponse(500);
    }

    public WalletBaseResponse register(SignUpRequest signUpRequest) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        SignUpWalletRequest signUpWalletRequest = new SignUpWalletRequest();
        signUpWalletRequest.setAcc_email(signUpRequest.getEmail());
        signUpWalletRequest.setCell_phone(signUpRequest.getUsername());
        signUpWalletRequest.setAcc_pwd(signUpRequest.getPassword());
        signUpWalletRequest.setAcc_identify(signUpRequest.getUsername());
        String request = gson.toJson(signUpWalletRequest);
        String encryptData = "";
        //Request
        try {
            encryptData = TripleDES.encrypt(appConfig.epurse_key, request);
        } catch (Exception e) {
            logger.info("=====ENCRYPT EXCEPTION===== : " + request, e);
            e.printStackTrace();
        }
        if (!encryptData.isEmpty()) {
            WalletBaseRequest baseRequest = new WalletBaseRequest(WalletPrCode.PR_REGISTER.code,
                    encryptData, "", "", appConfig.epurse_service_code);
            String response = CallServer.getInstance().post(appConfig.epurse_url, gson.toJson(baseRequest));
            if (response != null && !response.isEmpty()) {
                return gson.fromJson(response, WalletBaseResponse.class);
            }
        }
        return new WalletBaseResponse(500);
    }

    public void createUserWallet(Long appUserId, Long accountEpurseId) {
        walletRepository.insertWallet(accountEpurseId, appUserId);
    }

    public WalletBaseResponse verifyOTP(VerifyOTPRequest verifyOTPRequest, String phone) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        VerifyOTPWalletRequest verifyOTPWalletRequest = new VerifyOTPWalletRequest(verifyOTPRequest.getUsername(), verifyOTPRequest.getOtpCode(), phone);
        String encryptData = "";
        //Request
        try {
            encryptData = TripleDES.encrypt(appConfig.epurse_key, gson.toJson(verifyOTPWalletRequest));
        } catch (Exception e) {
            logger.info("=====ENCRYPT EXCEPTION===== : " + verifyOTPRequest, e);
        }
        if (!encryptData.isEmpty()) {
            WalletBaseRequest baseRequest = new WalletBaseRequest(WalletPrCode.PR_VERIFY_OTP.code,
                    encryptData, "", "", appConfig.epurse_service_code);
            String response = CallServer.getInstance().post(appConfig.epurse_url, gson.toJson(baseRequest));
            if (response != null && !response.isEmpty()) {
                return gson.fromJson(response, WalletBaseResponse.class);
            }
        }
        return new WalletBaseResponse(500);
    }

    public WalletBaseResponse updateUserInfo(UpdateUserInfoRequest updateUserInfoRequest, String username) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        UpdateUserInfoWalletRequest updateUserInfoWalletRequest = new UpdateUserInfoWalletRequest();
        updateUserInfoWalletRequest.setUsername(username);
        updateUserInfoWalletRequest.setDisplay_name(updateUserInfoRequest.getName());
//        updateUserInfoWalletRequest.setSession_key(sessionKey);
        updateUserInfoWalletRequest.setId_full_name(updateUserInfoRequest.getName());
        if(updateUserInfoRequest.getIdentityCard()!= null && !updateUserInfoRequest.getIdentityCard().isEmpty()){
            updateUserInfoWalletRequest.setId_number(updateUserInfoRequest.getIdentityCard());
        }else updateUserInfoWalletRequest.setId_number("");
        if (updateUserInfoRequest.getBirthday() != null && !updateUserInfoRequest.getBirthday().isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date birthDay = dateFormat.parse(updateUserInfoRequest.getBirthday());
            dateFormat = new SimpleDateFormat("yyyyMMdd");
            updateUserInfoWalletRequest.setBirthday(dateFormat.format(birthDay));
        } else updateUserInfoWalletRequest.setBirthday("");
        String encryptData = "";

        //Request
        try {
            encryptData = TripleDES.encrypt(appConfig.epurse_key, gson.toJson(updateUserInfoWalletRequest));
        } catch (Exception e) {
            logger.info("=====ENCRYPT EXCEPTION===== : " + updateUserInfoWalletRequest, e);
        }
        if (!encryptData.isEmpty()) {
            WalletBaseRequest baseRequest = new WalletBaseRequest(WalletPrCode.PR_UPDATE_USER_INFO.code,
                    encryptData, "", "", appConfig.epurse_service_code);
            String response = CallServer.getInstance().post(appConfig.epurse_url, gson.toJson(baseRequest));
            if (response != null && !response.isEmpty()) {
                return gson.fromJson(response, WalletBaseResponse.class);
            }
        }
        return new WalletBaseResponse(500);
    }

    public WalletUserInfo getUserInfo(String username) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        WalletUserInfoRequest walletUserInfoRequest = new WalletUserInfoRequest();
        walletUserInfoRequest.setUsername(username);
        String encryptData = "";
        //Request
        try {
            encryptData = TripleDES.encrypt(appConfig.epurse_key, gson.toJson(walletUserInfoRequest));
        } catch (Exception e) {
            logger.info("=====ENCRYPT EXCEPTION===== : " + walletUserInfoRequest, e);
        }
        if (!encryptData.isEmpty()) {
            WalletBaseRequest baseRequest = new WalletBaseRequest(WalletPrCode.PR_GETUSER_INF.code,
                    encryptData, "", "", appConfig.epurse_service_code);
            String response = CallServer.getInstance().post(appConfig.epurse_url, gson.toJson(baseRequest));
            if (response != null && !response.isEmpty()) {
                //Decrypt
                WalletBaseResponse walletBaseResponse = gson.fromJson(response, WalletBaseResponse.class);
                if (walletBaseResponse.getStatus() == 200) {
                    String data = TripleDES.decrypt(appConfig.epurse_key, walletBaseResponse.getData());
                    WalletUserInfo walletUserInfo = gson.fromJson(data, WalletUserInfo.class);
                    AppUser appUser = appUserRepository.findByPhone(username);
                    if (appUser != null && (appUser.getAccountEpurseId() == null
                            || !appUser.getAccountEpurseId().equals(BigDecimal.valueOf(walletUserInfo.getAccount_epurse_id()))))
                        appUserRepository.updateAccountEpurse(username, walletUserInfo.getAccount_epurse_id());
                    return walletUserInfo;
                }
            }
        }
        return null;
    }

    public WalletBaseResponse updatePhone(UpdatePhoneRequest updatePhoneRequest) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        HashMap<String, String> updatePhoneModel = new HashMap<>();
        updatePhoneModel.put("username", updatePhoneRequest.getUsername());
        updatePhoneModel.put("otp_code", updatePhoneRequest.getPhoneOTP());
        updatePhoneModel.put("new_phone", updatePhoneRequest.getNewPhone());
        String encryptData = "";
        //Request
        try {
            encryptData = TripleDES.encrypt(appConfig.epurse_key, gson.toJson(updatePhoneModel));
        } catch (Exception e) {
            logger.info("=====ENCRYPT EXCEPTION===== : " + updatePhoneModel, e);
        }
        if (!encryptData.isEmpty()) {
            WalletBaseRequest baseRequest = new WalletBaseRequest(WalletPrCode.PR_ACTIVEPHONE.code,
                    encryptData, "", "", appConfig.epurse_service_code);
            String response = CallServer.getInstance().post(appConfig.epurse_url, gson.toJson(baseRequest));
            if (response != null && !response.isEmpty()) {
                //Decrypt
//                WalletBaseResponse walletBaseResponse = gson.fromJson(response, WalletBaseResponse.class);
                return gson.fromJson(response, WalletBaseResponse.class);
//                if (walletBaseResponse.getStatus() == 200) {
//                    String data = TripleDES.decrypt(appConfig.epurse_key, walletBaseResponse.getData());
//                    return gson.fromJson(data, UpdatePhoneRequest.class);
//                }
            }
        }
        return new WalletBaseResponse(500);
    }

    public WalletBaseResponse resetPass(UserResetPassRequest userResetPassRequest) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        WalletResetPassRequest walletResetPassRequest = new WalletResetPassRequest();
        walletResetPassRequest.setUsername(userResetPassRequest.getUsername());
        walletResetPassRequest.setNewpassword(userResetPassRequest.getNewPassword());
        walletResetPassRequest.setOtp_code(userResetPassRequest.getOtpCode());
        String encryptData = "";
        //Request
        try {
            encryptData = TripleDES.encrypt(appConfig.epurse_key, gson.toJson(walletResetPassRequest));
        } catch (Exception e) {
            logger.info("=====ENCRYPT EXCEPTION===== : " + walletResetPassRequest, e);
        }
        if (!encryptData.isEmpty()) {
            WalletBaseRequest baseRequest = new WalletBaseRequest(WalletPrCode.PR_RESET_PASS.code,
                    encryptData, "", "", appConfig.epurse_service_code);
            String response = CallServer.getInstance().post(appConfig.epurse_url, gson.toJson(baseRequest));
            if (response != null && !response.isEmpty()) {
                //Decrypt
//                WalletBaseResponse walletBaseResponse = gson.fromJson(response, WalletBaseResponse.class);
                return gson.fromJson(response, WalletBaseResponse.class);
//                if (walletBaseResponse.getStatus() == 200) {
//                    String data = TripleDES.decrypt(appConfig.epurse_key, walletBaseResponse.getData());
//                    return gson.fromJson(data, UpdatePhoneRequest.class);
//                }
            }
        }
        return null;
    }

    public WalletBaseResponse changePass(ChangePasswordRequest changePasswordRequest) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        WalletResetPassRequest walletResetPassRequest = new WalletResetPassRequest();
        walletResetPassRequest.setUsername(changePasswordRequest.getUsername());
        walletResetPassRequest.setPhone(changePasswordRequest.getUsername());
        walletResetPassRequest.setNewpassword(changePasswordRequest.getNewPassword());
        walletResetPassRequest.setPassword(changePasswordRequest.getOldPassword());
        walletResetPassRequest.setSession_key(changePasswordRequest.getSessionKey());
        String encryptData = "";
        //Request
        try {
            encryptData = TripleDES.encrypt(appConfig.epurse_key, gson.toJson(walletResetPassRequest));
        } catch (Exception e) {
            logger.info("=====ENCRYPT EXCEPTION===== : " + walletResetPassRequest, e);
        }
        if (!encryptData.isEmpty()) {
            WalletBaseRequest baseRequest = new WalletBaseRequest(WalletPrCode.PR_CHANGEPASS.code,
                    encryptData, "", "", appConfig.epurse_service_code);
            String response = CallServer.getInstance().post(appConfig.epurse_url, gson.toJson(baseRequest));
            if (response != null && !response.isEmpty()) {
                //Decrypt
//                WalletBaseResponse walletBaseResponse = gson.fromJson(response, WalletBaseResponse.class);
                return gson.fromJson(response, WalletBaseResponse.class);
//                if (walletBaseResponse.getStatus() == 200) {
//                    String data = TripleDES.decrypt(appConfig.epurse_key, walletBaseResponse.getData());
//                    return gson.fromJson(data, UpdatePhoneRequest.class);
//                }
            }
        }
        return null;
    }

    public WalletBaseResponse createVA(UserRegisterVARequest userRegisterVARequest) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        WalletRegisterVA walletRegisterVA = new WalletRegisterVA();
        walletRegisterVA.setUsername(userRegisterVARequest.getUsername());
        walletRegisterVA.setCustomer_name("CONG TY CP CONG NGHE VA DICH VU IMEDIA");
        walletRegisterVA.setSession_key(userRegisterVARequest.getSessionKey());
        walletRegisterVA.setRequest_id(GenerateTransactionId.GetInstance().GeneratePartnerTransactionId("RVA_"));
        String encryptData = "";
        //Request
        try {
            encryptData = TripleDES.encrypt(appConfig.epurse_key, gson.toJson(walletRegisterVA));
        } catch (Exception e) {
            logger.info("=====ENCRYPT EXCEPTION===== : " + walletRegisterVA, e);
        }
        if (!encryptData.isEmpty()) {
            WalletBaseRequest baseRequest = new WalletBaseRequest(WalletPrCode.PR_REGISTER_VA_ACCOUNT.code,
                    encryptData, "", "", appConfig.epurse_service_code);
            String response = CallServer.getInstance().post(appConfig.epurse_url, gson.toJson(baseRequest));
            if (response != null && !response.isEmpty()) {
                //Decrypt
                return gson.fromJson(response, WalletBaseResponse.class);
            }
        }
        return null;
    }

    public synchronized WalletBaseResponse depositVA(NotifyBalanceRequest notifyBalanceRequest, AppUser appUser) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        //Pre save logs first
        WalletLog walletLog = new WalletLog();
        //Wallet log info
        walletLog.setCode(GenerateTransactionId.GetInstance().GeneratePartnerTransactionId("DVA"));
        walletLog.setType(BigDecimal.valueOf(3));
        walletLog.setCreateBy(BigDecimal.valueOf(appUser.getId()));
        walletLog.setReason(WalletLogContentEnum.DEPOSIT_VA.message + notifyBalanceRequest.getAccountNumber());
        walletLog.setMemo(notifyBalanceRequest.getMemo() + "||" + notifyBalanceRequest.getRequestId());
        walletLog.setWalletId(appUser.getAccountEpurseId());
        walletLog.setIsDeleted(1);
        walletLog.setFromBase(BigDecimal.ZERO);
        walletLog.setToBase(BigDecimal.ZERO);
        walletLog.setFromHold(BigDecimal.ZERO);
        walletLog.setToHold(BigDecimal.ZERO);
        walletLog.setChangeBase(BigDecimal.valueOf(notifyBalanceRequest.getChargeAmount()));
        walletLog.setChangeHold(BigDecimal.ZERO);
        walletLog.setChangeRoot(BigDecimal.ZERO);
        //Balance change
        //TODO CHECK FEE
        walletLog.setCost(BigDecimal.valueOf(0));
        walletLog.setBalance(BigDecimal.valueOf(notifyBalanceRequest.getChargeAmount()).subtract(BigDecimal.valueOf(0)));
        //TODO no promotion
        walletLog.setToBorrow(BigDecimal.ZERO);
        walletLog.setFromBorrow(BigDecimal.ZERO);
        walletLog.setChangeBorrow(BigDecimal.ZERO);
        walletLog.setFromPromotion(BigDecimal.ZERO);
        walletLog.setToPromotion(BigDecimal.ZERO);
        walletLog.setChangePromotion(BigDecimal.ZERO);
        //Status
        walletLog.setStatus(BigDecimal.valueOf(2));
        //stack
        walletLog.setBalStackCode(appConfig.mainStackCode);
        //pre save
        try {
            walletLogRepository.save(walletLog);
            logger.info("=====WALLET LOG PRE SAVE=======" + gson.toJson(walletLog));
        } catch (Exception e) {
            logger.info("=======PRE SAVE WALLET LOGS VA EXCEPTION=======" + gson.toJson(notifyBalanceRequest), e);
            return null;
        }
//        String transId = GenerateTransactionId.GetInstance().GeneratePartnerTransactionId("DVA");
        //Build data
        WalletBalanceRequest walletBalanceRequest = new WalletBalanceRequest();
        BigDecimal accountEpureId = appUser.getAccountEpurseId();
        if (accountEpureId == null) {
            WalletUserInfo walletUserInfo = getUserInfo(appUser.getPhone());
            appUser.setAccountEpurseId(BigDecimal.valueOf(walletUserInfo.getAccount_epurse_id()));
            appUserRepository.save(appUser);
        }
        walletBalanceRequest.setAccId(appUser.getAccountEpurseId().longValue());
        walletBalanceRequest.setMoney_active(BigDecimal.ZERO);
        walletBalanceRequest.setBalChangeStatus(BigDecimal.ZERO);
        walletBalanceRequest.setBalChangeType(BigDecimal.valueOf(0));
        walletBalanceRequest.setBalChangeAmount(BigDecimal.valueOf(notifyBalanceRequest.getChargeAmount()).subtract(walletLog.getCost()));
        walletBalanceRequest.setCreatedBy(appUser.getPhone());
        walletBalanceRequest.setClient_request_id(walletLog.getCode());
        walletBalanceRequest.setClientReqId(walletLog.getCode());
        walletBalanceRequest.setClient_sub_request_id(notifyBalanceRequest.getRequestId());
        walletBalanceRequest.setBal_stack_code(appConfig.mainStackCode);
        walletBalanceRequest.setBalance_sub_type(WalletSubTypeCode.SUB_TYPE_ADD_EPURSE_VA.code);
        String encryptData = "";
        //Request
        try {
            encryptData = TripleDES.encrypt(appConfig.epurse_key, gson.toJson(walletBalanceRequest));
        } catch (Exception e) {
            logger.info("=====ENCRYPT EXCEPTION===== : " + gson.toJson(walletBalanceRequest), e);
            walletLog.setErrorDetail("EXCEPTION :" + e.getMessage());
            walletLogRepository.save(walletLog);
            logger.info("=====WALLET LOG UPDATE SAVE=======" + gson.toJson(walletLog));
            return null;
        }
        if (!encryptData.isEmpty()) {
            WalletBaseRequest baseRequest = new WalletBaseRequest(WalletPrCode.PR_CREDIT_SERVER_SERVER.code,
                    encryptData, "", "", appConfig.epurse_service_code);
            String response = CallServer.getInstance().post(appConfig.epurse_url, gson.toJson(baseRequest));
            if (response != null) {
                WalletBaseResponse walletBaseResponse = gson.fromJson(response, WalletBaseResponse.class);
                walletLog.setErrorCode(String.valueOf(walletBaseResponse.getStatus()));
                walletLog.setErrorDetail(walletBaseResponse.getResponse_msg());
                if (walletBaseResponse.getStatus() == 200) {
                    walletLog.setStatus(BigDecimal.ZERO);
                    walletLog.setFromBase(BigDecimal.valueOf(walletBaseResponse.getBalBefore()));
                    walletLog.setToBase(BigDecimal.valueOf(walletBaseResponse.getBalAfter()));
                    updateBalanceCache(appUser.getPhone(), BigDecimal.valueOf(walletBaseResponse.getBalAfter()));
                }
                walletLogRepository.save(walletLog);
                logger.info("=====WALLET LOG UPDATE SAVE=======" + gson.toJson(walletLog));
                //TODO update cache
                return walletBaseResponse;
            }
        }
        return null;
    }

    public BaseResponse withdrawBank(UserWithdrawRequest userWithdrawRequest,
                                     BankAccount bankAccount, String requestId, Bank bank, BigDecimal amount, AppUser appUser) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        //TODO CMS sẽ phải đưa giao dịch về trạng thái 5 là từ chối chuyển tiền
        //Build log
//        Wallet wallet = walletRepository.findByUserId(BigDecimal.valueOf(bankAccount.getAppUserId()));
        //Pre save logs first
        WalletLog walletLog = new WalletLog();
        //Wallet log info
        walletLog.setCode(requestId);
        walletLog.setType(BigDecimal.valueOf(11));
        walletLog.setCreateBy(BigDecimal.valueOf(bankAccount.getAppUserId()));
        walletLog.setReason(WalletLogContentEnum.WITHDRAW_BANK.message + userWithdrawRequest.getBankAccount());
        walletLog.setWalletId(appUser.getAccountEpurseId());
        preBuildWalletLog(walletLog);
        //Balance change
        walletLog.setChangeBase(userWithdrawRequest.getAmount());
        walletLog.setCost(appConfig.bankFee);
        walletLog.setBalance(BigDecimal.ZERO.subtract(amount));
        //Status
        walletLog.setStatus(BigDecimal.valueOf(2));
        //stack
        walletLog.setBalStackCode(appConfig.mainStackCode);
        walletLog.setBankAccount(userWithdrawRequest.getBankAccount());
        //Build request
        WalletWithdrawBankRequest withdrawBankRequest = new WalletWithdrawBankRequest();
        withdrawBankRequest.setClient_request_id(requestId);
        withdrawBankRequest.setSession_key(userWithdrawRequest.getSessionKey());
        withdrawBankRequest.setBal_stack_code(appConfig.mainStackCode);
        withdrawBankRequest.setUsername(userWithdrawRequest.getUsername());
        withdrawBankRequest.setOtp_code(userWithdrawRequest.getOtpCode());
        //Amount
        //Phi ngoai
        if (appConfig.bankFeeType == 2) {
            withdrawBankRequest.setBalChangeAmount(userWithdrawRequest.getAmount());
            walletLog.setMemo("Phí ngoài");
        }
        //Phi trong
        if (appConfig.bankFeeType == 1) {
            withdrawBankRequest.setBalChangeAmount(amount);
            walletLog.setMemo("Phí trong");
        }
        withdrawBankRequest.setTransaction_fee(appConfig.bankFee);
        withdrawBankRequest.setCreatedBy(userWithdrawRequest.getUsername());
        withdrawBankRequest.setBankCode(bankAccount.getBankCode());
        withdrawBankRequest.setAccBankName(bankAccount.getBankAccountName());
        withdrawBankRequest.setAccBankCode(bankAccount.getBankAccount());
        withdrawBankRequest.setBankAccType(0L);
        withdrawBankRequest.setBalance_sub_type(WalletSubTypeCode.SUB_TYPE_SUBTRACT_EPURSE_WITHDRAW.code);
        withdrawBankRequest.setAuto_withdrawal(appConfig.autoWithdraw);
        //Build response
        UserWithdrawResponse userWithdrawResponse = new UserWithdrawResponse();
        userWithdrawResponse.setAmount(userWithdrawRequest.getAmount());
        userWithdrawResponse.setBankAccount(userWithdrawRequest.getBankAccount());
        userWithdrawResponse.setBankAccountName(bankAccount.getBankAccountName());
        userWithdrawResponse.setBankName(bank.getName());
        //TODO CHECK FEE
        userWithdrawResponse.setCost(appConfig.bankFee);
        userWithdrawResponse.setTransId(requestId);
        userWithdrawResponse.setStatus(2);
        userWithdrawResponse.setMessage("Yêu cầu đang được xử lý");
        //Presave
        try {
            walletLogRepository.save(walletLog);
        } catch (Exception e) {
            logger.info("=====PRE SAVE EXCEPTION===== : " + requestId, e);
            return new BaseResponse(500);
        }
        String encryptData = "";
        //Request
        try {
            encryptData = TripleDES.encrypt(appConfig.epurse_key, gson.toJson(withdrawBankRequest));
        } catch (Exception e) {
            logger.info("=====ENCRYPT EXCEPTION===== : " + gson.toJson(withdrawBankRequest), e);
            walletLog.setErrorDetail("EXCEPTION :" + e.getMessage());
            walletLog.setIsDeleted(0);
            walletLogRepository.save(walletLog);
            return new BaseResponse(500);
        }
        if (!encryptData.isEmpty()) {
            WalletBaseRequest baseRequest = new WalletBaseRequest(WalletPrCode.PR_WITHDRAW_BANK.code,
                    encryptData, "", "", appConfig.epurse_service_code);
            logger.info("=====POST WITHDRAW REQUEST ID===== : " + requestId + "|| username" + userWithdrawRequest.getUsername());
            String response = CallServer.getInstance().post(appConfig.epurse_url, gson.toJson(baseRequest));
            logger.info("=====POST RESPONSE WITHDRAW REQUEST ID===== : " + requestId + "|| username" + userWithdrawRequest.getUsername() + "||" + response);
            if (response != null) {
                WalletBaseResponse walletBaseResponse = gson.fromJson(response, WalletBaseResponse.class);
                walletLog.setErrorCode(String.valueOf(walletBaseResponse.getStatus()));
                walletLog.setErrorDetail(walletBaseResponse.getResponse_msg());
                if (walletBaseResponse.getStatus() == 200) {
                    walletLog.setStatus(BigDecimal.ZERO);
                    walletLog.setFromBase(BigDecimal.valueOf(walletBaseResponse.getBalBefore()));
                    walletLog.setToBase(BigDecimal.valueOf(walletBaseResponse.getBalAfter()));
                    userWithdrawResponse.setStatus(0);
                    walletLogRepository.save(walletLog);
                    return new BaseResponse(200, userWithdrawResponse);
                }
                if (walletBaseResponse.getStatus() == 99) {
                    //Decrypt
                    String data = TripleDES.decrypt(appConfig.epurse_key, walletBaseResponse.getData());
//                    WalletUserInfo walletUserInfo = gson.fromJson(data, WalletUserInfo.class);
                    if (!appConfig.autoWithdraw)
                        walletLog.setStatus(BigDecimal.valueOf(3));
                    walletLog.setFromBase(BigDecimal.valueOf(walletBaseResponse.getBalBefore()));
                    walletLog.setToBase(BigDecimal.valueOf(walletBaseResponse.getBalAfter()));
                    //Update response
                    if (!appConfig.autoWithdraw)
                        userWithdrawResponse.setStatus(2);
                    walletLogRepository.save(walletLog);
                }
                if (walletBaseResponse.getStatus() == 146) {
                    walletLog.setFromBase(BigDecimal.valueOf(walletBaseResponse.getBalBefore()));
                    walletLog.setToBase(BigDecimal.valueOf(walletBaseResponse.getBalAfter()));
                }
                if (walletBaseResponse.getStatus() == 146 && !appConfig.autoWithdraw)
                    walletLog.setStatus(BigDecimal.valueOf(3));
                //Failed
                if (withdrawFailCode.contains(walletBaseResponse.getStatus())) {
                    //update balance to log
                    WalletUserInfo walletUserInfo = getUserInfo(withdrawBankRequest.getUsername());
                    walletUserInfo.getBalance().forEach(b -> {
                        if (b.getBal_code().equals(appConfig.mainStackCode)) {
                            walletLog.setToBase(b.getRemain_balance());
                            walletLog.setFromBase(b.getRemain_balance());
                        }
                    });
                    userWithdrawResponse.setStatus(1);
                    walletLog.setStatus(BigDecimal.ONE);
                    if (walletBaseResponse.getStatus() == 135) {
                        walletLog.setIsDeleted(0);
                        walletLogRepository.save(walletLog);
                        return new BaseResponse(135);
                    }
                }
                walletLogRepository.save(walletLog);
                if (!walletLog.getToBase().equals(BigDecimal.ZERO))
                    updateBalanceCache(userWithdrawRequest.getUsername(), walletLog.getToBase());
            }
            return new BaseResponse(99, userWithdrawResponse);
        }
        return new BaseResponse(500);
    }

    public void updateBalanceCache(String username, BigDecimal newBalance) throws Exception {
        String data = CallRedis.getCache(username);
        if (data != null) {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject walletInfo = jsonObject.getJSONObject("walletInfo");
            JSONObject mainWallet = walletInfo.getJSONObject("W" + AppConfig.getInstance().mainStackCode);
            mainWallet.put("remain_balance", newBalance);
            CallRedis.updateKey(username, jsonObject.toString());
        }

    }

    private void preBuildWalletLog(WalletLog walletLog) {
        walletLog.setFromBase(BigDecimal.ZERO);
        walletLog.setIsDeleted(1);
        walletLog.setToBase(BigDecimal.ZERO);
        walletLog.setFromHold(BigDecimal.ZERO);
        walletLog.setToHold(BigDecimal.ZERO);
        walletLog.setChangeHold(BigDecimal.ZERO);
        walletLog.setChangeRoot(BigDecimal.ZERO);
        walletLog.setToBorrow(BigDecimal.ZERO);
        walletLog.setFromBorrow(BigDecimal.ZERO);
        walletLog.setChangeBorrow(BigDecimal.ZERO);
        walletLog.setFromPromotion(BigDecimal.ZERO);
        walletLog.setToPromotion(BigDecimal.ZERO);
        walletLog.setChangePromotion(BigDecimal.ZERO);
    }

    public void checkPendingTransaction(WalletLog walletLog) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        HashMap<String, String> data = new HashMap<>();
        data.put("client_request_id", walletLog.getCode());
        String encryptData = "";
        try {
            encryptData = TripleDES.encrypt(appConfig.epurse_key, gson.toJson(data));
        } catch (Exception e) {
            logger.info("=====ENCRYPT EXCEPTION===== : " + gson.toJson(data), e);
            walletLog.setErrorDetail("EXCEPTION :" + e.getMessage());
            walletLogRepository.save(walletLog);
        }
        if (!encryptData.isEmpty()) {
            logger.info("=====CHECK PENDING===== : " + walletLog.getCode());
            WalletBaseRequest baseRequest = new WalletBaseRequest(WalletPrCode.PR_CHECKTRANS.code,
                    encryptData, "", "", appConfig.epurse_service_code);
            String response = CallServer.getInstance().post(appConfig.epurse_admin_url, gson.toJson(baseRequest));
            if (response != null) {
                WalletBaseResponse walletBaseResponse = gson.fromJson(response, WalletBaseResponse.class);
                walletLog.setErrorCode(String.valueOf(walletBaseResponse.getStatus()));
                walletLog.setErrorDetail(String.valueOf(walletBaseResponse.getResponse_msg()));
                //Success
                if (walletBaseResponse.getStatus() == 200) {
                    if (walletBaseResponse.getLstQueryLog() != null && walletBaseResponse.getLstQueryLog().size() > 0) {
                        CheckPendingInfo info = walletBaseResponse.getLstQueryLog().get(0);
                        //TODO UNLOCK LOG
                        walletLog.setErrorCode(String.valueOf(info.getFirmStatus()));
                        walletLog.setFromBase(info.getBalBef());
                        walletLog.setToBase(info.getBalAf());
                        //Success
                        if (info.getFirmStatus() == 200) {
                            walletLog.setStatus(BigDecimal.valueOf(0));
                            WalletLog log = walletLogRepository.save(walletLog);
                            logger.info("=======WALLET LOG SAVE TO DB=======" + gson.toJson(log));
                        }
                        //Failed
                        if (info.getFirmStatus() == 149 || info.getFirmStatus() == 120) {
                            if (!appConfig.autoWithdraw)
                                walletLog.setStatus(BigDecimal.valueOf(4));
                            else walletLog.setStatus(BigDecimal.ONE);
                            WalletLog log = walletLogRepository.save(walletLog);
                            logger.info("=======WALLET LOG SAVE TO DB=======" + gson.toJson(log));
                        }
                        //Pending
                        if (info.getFirmStatus() == 99) {
                            if (!appConfig.autoWithdraw)
                                walletLog.setStatus(BigDecimal.valueOf(3));
                            WalletLog log = walletLogRepository.save(walletLog);
                            logger.info("=======WALLET LOG SAVE TO DB=======" + gson.toJson(log));
                        }
                    }
                }
                //No transaction found
                if (walletBaseResponse.getStatus() == 147) {
                    walletLog.setStatus(BigDecimal.ONE);
                    WalletLog log = walletLogRepository.save(walletLog);
                    logger.info("=======WALLET LOG SAVE TO DB=======" + gson.toJson(log));
                }
            }
        }
    }

    public WalletBaseResponse addHoldingBalance(BigDecimal orderDetailCode, AppUser appUser, BigDecimal amount) throws Exception {
        logger.info("=========ADD HOLDING BALANCE========" + orderDetailCode);
        AppConfig appConfig = AppConfig.getInstance();
        String transId = WalletLogEnum.HOLD_ADD.prefix + orderDetailCode +
                GenerateTransactionId.GetInstance().generateRandomId(4);
        WalletBaseRequest request = buildBalanceRequest(WalletPrCode.PR_ADD_HOLDING_BALANCE.code, WalletSubTypeCode.SUB_TYPE_ADD_HOLDING_ORDER.code,
                amount, orderDetailCode, appUser, transId);
        WalletLog walletLog = new WalletLog();
        preBuildWalletLog(walletLog);
        //Id
        walletLog.setCode(transId);
        walletLog.setType(BigDecimal.valueOf(20));
        walletLog.setCreateBy(BigDecimal.valueOf(appUser.getId()));
        walletLog.setReason("Cộng tạm giữ cho đơn hàng : " + orderDetailCode);
        walletLog.setOrderDetailId(orderDetailCode);
        //Order id = orderDetail - last character
        String orderId = String.valueOf(orderDetailCode).substring(0, 11);
        walletLog.setOrderId(BigDecimal.valueOf(Long.parseLong(orderId)));
        walletLog.setWalletId(appUser.getAccountEpurseId());
        walletLog.setStatus(BigDecimal.valueOf(2));
        walletLog.setBalStackCode(appConfig.mainStackCode);
        walletLog.setMemo("Cộng tạm giữ cho đơn hàng : " + orderDetailCode);
        //Amount
        walletLog.setChangeHold(amount);
        walletLog.setChangeBase(BigDecimal.ZERO);
        String response = null;
        try {
            response = CallServer.getInstance().post(appConfig.epurse_url, gson.toJson(request));
        } catch (Exception e) {
            logger.info("=======ADD HOLDING BALANCE EXCEPTION======= RETRY 1" + orderDetailCode, e);
//            Thread.sleep(2000);
//            //TODO do something to re try
//            response = CallServer.getInstance().post(appConfig.epurse_url, gson.toJson(request));
        }
        if (response != null) {
            WalletBaseResponse baseResponse = gson.fromJson(response, WalletBaseResponse.class);
            walletLog.setErrorCode(String.valueOf(baseResponse.getStatus()));
            walletLog.setErrorDetail(baseResponse.getResponse_msg());
            if (baseResponse.getStatus() == 200) {
                walletLog.setStatus(BigDecimal.ZERO);
                walletLog.setFromHold(BigDecimal.valueOf(baseResponse.getHoldingBalBefore()));
                walletLog.setToHold(BigDecimal.valueOf(baseResponse.getHoldingBalAfter()).add(amount));
            }
            WalletLog result = walletLogRepository.save(walletLog);
            logger.info("=======ADD HOLDING BALANCE  WALLET LOG SAVED TO DB========" + gson.toJson(result));
            return gson.fromJson(response, WalletBaseResponse.class);
        }
        return null;
    }

    private WalletBaseRequest buildBalanceRequest(int prCode, String subType, BigDecimal amount,
                                                  BigDecimal orderIdDetail, AppUser appUser,
                                                  String transId) throws Exception {
        try {
            AppConfig appConfig = AppConfig.getInstance();
            WalletBalanceRequest balanceRequest = new WalletBalanceRequest();
            balanceRequest.setBal_stack_code(appConfig.mainStackCode);
            balanceRequest.setAccId(appUser.getAccountEpurseId().longValue());
            balanceRequest.setBalChangeAmount(amount);
            balanceRequest.setClient_request_id(transId);
            balanceRequest.setClientReqId(transId);
            balanceRequest.setClient_sub_request_id(String.valueOf(orderIdDetail));
            balanceRequest.setList_query_client_id(Collections.singletonList(transId));
            balanceRequest.setCreatedBy(appUser.getPhone());
            balanceRequest.setBalance_sub_type(subType);
            String data = gson.toJson(balanceRequest);
//            logger.info("=======Order detail====: " + orderIdDetail + " | WalletBalanceRequest: " + data + "| PR CODE " + prCode);
            //Encrypt data
            String encryptData = null;
            encryptData = TripleDES.encrypt(appConfig.epurse_key, data);
            return new WalletBaseRequest(prCode, encryptData, "", "", appConfig.epurse_service_code);
        } catch (Exception e) {
            logger.info("BUILD REQUEST EXCEPTION : " + prCode + "||" + orderIdDetail, e);
        }
        return null;
    }

    public void revertHoldingBalance(WalletLog holdLog, AppUser appUser) {
        logger.info("=======REVERT HOLDING BALANCE========" + holdLog.getOrderDetailId() + "|| Amount " + holdLog.getChangeHold());
        try {
            AppConfig appConfig = AppConfig.getInstance();
            String transId = WalletLogEnum.HOLD_RVT.prefix + holdLog.getOrderDetailId() +
                    GenerateTransactionId.GetInstance().generateRandomId(4);
            WalletBalanceRequest balanceRequest = new WalletBalanceRequest();
            balanceRequest.setBal_stack_code(appConfig.mainStackCode);
            //Id to revert
            balanceRequest.setList_query_client_id(Collections.singletonList(holdLog.getCode()));
            balanceRequest.setCreatedBy(appUser.getPhone());
            balanceRequest.setClient_request_id(holdLog.getCode());
            balanceRequest.setClientReqId(holdLog.getCode());
            balanceRequest.setAccId(appUser.getAccountEpurseId().longValue());
            String encryptData = TripleDES.encrypt(appConfig.epurse_key, gson.toJson(balanceRequest));
            WalletBaseRequest request = new WalletBaseRequest(3900, encryptData, "", "", appConfig.epurse_service_code);
            WalletLog walletLog = new WalletLog();
            preBuildWalletLog(walletLog);
            //Id
            walletLog.setCode(transId);
            walletLog.setType(BigDecimal.valueOf(WalletLogEnum.HOLD_RVT.code));
            walletLog.setCreateBy(BigDecimal.valueOf(appUser.getId()));
            walletLog.setReason(WalletLogEnum.HOLD_RVT.message + holdLog.getOrderDetailId());
            walletLog.setOrderDetailId(holdLog.getOrderDetailId());
            //Order id = orderDetail - last character
            String orderId = String.valueOf(holdLog.getOrderDetailId()).substring(0, 11);
            walletLog.setOrderId(BigDecimal.valueOf(Long.parseLong(orderId)));
            walletLog.setWalletId(appUser.getAccountEpurseId());
            walletLog.setStatus(BigDecimal.valueOf(2));
            walletLog.setBalStackCode(appConfig.mainStackCode);
            walletLog.setMemo(WalletLogEnum.HOLD_RVT.message + holdLog.getOrderDetailId());
            //Amount
            walletLog.setChangeHold(BigDecimal.ZERO.subtract(holdLog.getChangeHold()));
            walletLog.setChangeBase(BigDecimal.ZERO);
            String response = null;
            try {
                response = CallServer.getInstance().post(appConfig.epurse_admin_url, gson.toJson(request));
            } catch (Exception e) {
                logger.info("========REVERT HOLDING BALANCE EXCEPTION========" + walletLog.getCode(), e);
            }
            if (response != null) {
                WalletBaseResponse baseResponse = gson.fromJson(response, WalletBaseResponse.class);
                walletLog.setErrorCode(String.valueOf(baseResponse.getStatus()));
                walletLog.setErrorDetail(baseResponse.getResponse_msg());
                // 200 thành công 137 là không đủ số dư tạm giữ để revert
                if (baseResponse.getStatus() == 200 || baseResponse.getStatus() == 147) {
                    walletLog.setMemo("Revert tạm giữ thành công");
                    walletLog.setStatus(BigDecimal.ZERO);
                    holdLog.setIsDeleted(0);
                    if (baseResponse.getStatus() == 147) {
                        walletLog.setToHold(BigDecimal.valueOf(baseResponse.getHoldingBalAfter()));
                        walletLog.setFromHold(BigDecimal.valueOf(baseResponse.getHoldingBalBefore()));
                    }
                } else if (baseResponse.getStatus() == 137) {
                    walletLog.setMemo("Không đủ số dư tạm giữ để revert");
                    walletLog.setStatus(BigDecimal.ZERO);
                    holdLog.setIsDeleted(0);
//                    walletLog.setToHold(BigDecimal.valueOf(baseResponse.getHoldingBalAfter()));
//                    walletLog.setFromHold(BigDecimal.valueOf(baseResponse.getHoldingBalBefore()));
                } else walletLog.setMemo("Revert tạm giữ thất bại");
            } else {
                walletLog.setStatus(BigDecimal.ONE);
                walletLog.setErrorCode(String.valueOf(-1));
                walletLog.setErrorDetail("Response null");
            }
            WalletLog revertLog = walletLogRepository.save(walletLog);
            logger.info("========WALLET REVERT LOG SAVE TO DB=======" + gson.toJson(revertLog));
        } catch (Exception e) {
            logger.info("=======REVERT HOLDING BALANCE EXCEPTION========" + gson.toJson(holdLog), e);
            holdLog.setErrorCode(String.valueOf(-1));
            holdLog.setErrorDetail("Response null");
        }
        WalletLog result = walletLogRepository.save(holdLog);
        logger.info("========WALLET HOLD LOG SAVE TO DB=======" + gson.toJson(result));
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
        System.out.println(simpleDateFormat.parse("210715433181848"));
    }
}