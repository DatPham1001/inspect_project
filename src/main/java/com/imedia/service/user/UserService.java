package com.imedia.service.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.config.application.AppConfig;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.dao.AddressDAO;
import com.imedia.oracle.dao.ShopProfileDAO;
import com.imedia.oracle.entity.*;
import com.imedia.oracle.reportentity.AppContractReport;
import com.imedia.oracle.reportrepository.AppContractReportRepository;
import com.imedia.oracle.repository.*;
import com.imedia.service.cache.model.CacheLoginInfo;
import com.imedia.service.device.UserDeviceService;
import com.imedia.service.pickupaddress.model.ShopAddressDTO;
import com.imedia.service.user.model.*;
import com.imedia.service.wallet.WalletService;
import com.imedia.service.wallet.model.WalletBaseResponse;
import com.imedia.service.wallet.model.WalletUserBalanceData;
import com.imedia.service.wallet.model.WalletUserInfo;
import com.imedia.service.wallet.model.WalletUserInfoResponse;
import com.imedia.util.CallRedis;
import com.imedia.util.PreLoadStaticUtil;
import com.imedia.util.TripleDES;
import ma.glasnost.orika.MapperFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    static final Logger logger = LogManager.getLogger(UserService.class);
    static final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    private final AppUserRepository appUserRepository;
    private final ShopProfileRepository shopProfileRepository;
    private final WalletService walletService;
    private final AppUserDeviceRepository appUserDeviceRepository;
    private static final HashMap<Integer, ErrorCodesWebshop> errorCodes = PreLoadStaticUtil.errorCodeWeb;
    private final MapperFacade mapperFacade;
    private final AddressDAO addressDAO;
    private final ShopProfileDAO shopProfileDAO;
    private final ShipperProfileRepository shipperProfileRepository;
    static int rememberMeCache = 604800;
    private final AppContractReportRepository contractReportRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AppContractRepository appContractRepository;
    private final BankRepository bankRepository;
    private final UserDeviceService userDeviceService;

    @Autowired
    public UserService(AppUserRepository appUserRepository, WalletService walletService,
                       AppUserDeviceRepository appUserDeviceRepository, ShopProfileRepository shopProfileRepository, MapperFacade mapperFacade, AddressDAO addressDAO, ShopProfileDAO shopProfileDAO, ShipperProfileRepository shipperProfileRepository, AppContractReportRepository contractReportRepository, BankAccountRepository bankAccountRepository, AppContractRepository appContractRepository, BankRepository bankRepository, UserDeviceService userDeviceService) {
        this.appUserRepository = appUserRepository;
        this.walletService = walletService;
        this.appUserDeviceRepository = appUserDeviceRepository;
        this.shopProfileRepository = shopProfileRepository;
        this.mapperFacade = mapperFacade;
        this.addressDAO = addressDAO;
        this.shopProfileDAO = shopProfileDAO;
        this.shipperProfileRepository = shipperProfileRepository;
        this.contractReportRepository = contractReportRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.appContractRepository = appContractRepository;
        this.bankRepository = bankRepository;
        this.userDeviceService = userDeviceService;
    }

    public SignUpResponse register(SignUpRequest signUpRequest) {
        //Validate user
//        AppUser oldUser = appUserRepository.findByPhone(signUpRequest.getUsername());
        AppUser oldUser = appUserRepository.findByPhone(signUpRequest.getUsername());
        if (oldUser != null && !oldUser.getPhone().isEmpty()) {
            return new SignUpResponse(406, errorCodes.get(406).getMessage());
        } else {
            //TODO call wallet
            try {
                WalletBaseResponse baseResponse = walletService.register(signUpRequest);
                if (baseResponse != null) {
                    SignUpResponse signUpResponse = new SignUpResponse(baseResponse.getStatus(), baseResponse.getResponse_msg());
                    if (baseResponse.getStatus() == 200) {
                        signUpResponse.setMessage("Đăng ký thành công");
                        WalletUserInfoResponse data = gson.fromJson(
                                TripleDES.decrypt(AppConfig.getInstance().epurse_key, baseResponse.getData()), WalletUserInfoResponse.class);
                        //TODO save device
                        if (signUpRequest.getDeviceId() != null && !signUpRequest.getDeviceId().isEmpty()) {
                            AppUser appUser = saveAppUser(signUpRequest, data.getAccount_epurse_id());
                            if (appUser == null)
                                return new SignUpResponse(500, PreLoadStaticUtil.errorCodeWeb.get(500).getMessage());
                            userDeviceService.saveUserDevice(signUpRequest.getDeviceId(), appUser);
                            walletService.createUserWallet(appUser.getId(), data.getAccount_epurse_id());
                        }
                        return new SignUpResponse(200, PreLoadStaticUtil.errorCodeWeb.get(200).getMessage());
                    }
                    if (baseResponse.getStatus() == 132)
                        return new SignUpResponse(140,
                                PreLoadStaticUtil.errorCodeWeb.get(140).getMessage());
                    else return new SignUpResponse(baseResponse.getStatus(),
                            PreLoadStaticUtil.errorCodeWeb.get(baseResponse.getStatus()).getMessage());
                } else return new SignUpResponse(408, PreLoadStaticUtil.errorCodeWeb.get(408).getMessage());
            } catch (Exception e) {
                logger.info("====SIGNUP EXCEPTION===== " + gson.toJson(signUpRequest), e);
            }
        }
        return new SignUpResponse(500, PreLoadStaticUtil.errorCodeWeb.get(500).getMessage());
    }

    public VerifyOTPResponse verifyOTP(VerifyOTPRequest verifyOTPRequest) {
        try {
            String phone = shopProfileDAO.getPhoneOTPFromUsername(verifyOTPRequest.getUsername());
            if (phone == null) phone = verifyOTPRequest.getUsername();
            WalletBaseResponse baseResponse = walletService.verifyOTP(verifyOTPRequest, phone);
            if (baseResponse != null) {
                VerifyOTPResponse verifyOTPResponse = new VerifyOTPResponse(baseResponse.getStatus(), PreLoadStaticUtil.errorCodeWeb.get(baseResponse.getStatus()).getMessage(), phone);
                if (baseResponse.getStatus() == 200) {
                    verifyOTPResponse.setMessage("Verify OTP thành công");
                    //TODO update device ID verify
                    AppUser appUser = appUserRepository.findByPhone(verifyOTPRequest.getUsername());
                    if (appUser == null)
                        return new VerifyOTPResponse(101, PreLoadStaticUtil.errorCodeWeb.get(101).getMessage(), null);
                    List<AppUserDevice> appUserDevice = appUserDeviceRepository
                            .getExistByTokenAndAppUser(appUser.getId(), verifyOTPRequest.getDeviceId());
                    if (appUserDevice != null && appUserDevice.size() > 0) {
//                        appUserDevice.get(0).setEnabled(BigDecimal.ONE);
                        appUserDevice.get(0).setShop(BigDecimal.ONE);
                        appUserDeviceRepository.save(appUserDevice.get(0));
                        //Update cache
                        String cacheData = CallRedis.getCache("LOGIN:" + verifyOTPRequest.getUsername());
                        if (cacheData != null) {
                            CacheLoginInfo cacheLoginInfo = gson.fromJson(cacheData, CacheLoginInfo.class);
                            cacheLoginInfo.getDeviceIds().add(appUserDevice.get(0).getDeviceToken());
                            CallRedis.setCache("LOGIN:" + verifyOTPRequest.getUsername(), gson.toJson(cacheLoginInfo));
                        }
                    } else new VerifyOTPResponse(504, PreLoadStaticUtil.errorCodeWeb.get(504).getMessage(), null);
                }
                return verifyOTPResponse;
            }
        } catch (Exception e) {
            logger.info("====VERIFY EXCEPTION===== " + gson.toJson(verifyOTPRequest), e);
        }
        return new VerifyOTPResponse(500, PreLoadStaticUtil.errorCodeWeb.get(500).getMessage(), null);
    }

    public VerifyOTPResponse getOTP(GetOTPRequest getOTPRequest) {
        try {
            AppUser appUser = appUserRepository.findByPhone(getOTPRequest.getUsername());
            if (appUser != null) {
                userDeviceService.saveUserDevice(getOTPRequest.getDeviceId(), appUser);
                ShopProfile shopProfile = shopProfileRepository.findByAppUserId(appUser.getId());
                //Sync from ship profile
                if (shopProfile == null) {
                    ShipperProfile shipperProfile = shipperProfileRepository.findByProfileId(appUser.getId());
                    if (shipperProfile == null) {
                        //Create shop profile
                        shopProfileRepository.insertShopProfile(appUser.getId(), getOTPRequest.getUsername(), null);
                        shopProfile = shopProfileRepository.findByAppUserId(appUser.getId());
                    } else {
                        ShopProfile synced = syncShipProfile(appUser, shipperProfile);
                        if (synced == null)
                            return new VerifyOTPResponse(500, errorCodes.get(500).getMessage(), null);
                        else {
                            shopProfile = synced;
                        }
                    }
                }
                getOTPRequest.setPhone(shopProfile.getPhoneOtp());
                //Get cache OTP
                String cache = CallRedis.getCache("OTP:" + getOTPRequest.getUsername());
                if (cache == null) {
                    WalletBaseResponse baseResponse = walletService.getOTP(getOTPRequest);
                    if (baseResponse != null) {
                        VerifyOTPResponse verifyOTPResponse = new VerifyOTPResponse(baseResponse.getStatus(),
                                errorCodes.get(baseResponse.getStatus()).getMessage(), shopProfile.getPhoneOtp());
                        if (baseResponse.getStatus() == 200) {
                            verifyOTPResponse.setMessage("Get OTP thành công,đã gửi về điện thoại");
                            CallRedis.setCacheExpiry("OTP:" + getOTPRequest.getUsername(), "1", 1);
                        }
                        return verifyOTPResponse;
                    }
                } else {
                    int number = Integer.parseInt(cache);
                    if (number < AppConfig.getInstance().maxOtpPerDay) {
                        WalletBaseResponse baseResponse = walletService.getOTP(getOTPRequest);
                        if (baseResponse != null) {
                            VerifyOTPResponse verifyOTPResponse = new VerifyOTPResponse(baseResponse.getStatus(),
                                    errorCodes.get(baseResponse.getStatus()).getMessage(), shopProfile.getPhoneOtp());
                            if (baseResponse.getStatus() == 200) {
                                number = number + 1;
                                verifyOTPResponse.setMessage("Get OTP thành công,đã gửi về điện thoại");
                                CallRedis.setCacheExpiry("OTP:" + getOTPRequest.getUsername(), String.valueOf(number), 1);
                            }
                            return verifyOTPResponse;
                        }
                    } else
                        return new VerifyOTPResponse(211, errorCodes.get(211).getMessage(), shopProfile.getPhoneOtp());
                }

            }
            //Case sync user from wallet
            else {
                WalletUserInfo userInfo = walletService.getUserInfo(getOTPRequest.getUsername());
                if (userInfo != null) {
                    AppUser appUserSave = new AppUser();
                    appUserSave.setPhone(getOTPRequest.getUsername().trim());
                    appUserSave.setIdentify("PASSWORD");
                    appUserSave.setAccessToken(UUID.randomUUID().toString());
                    appUserSave.setAccountCode(String.valueOf(System.nanoTime()));
                    appUserSave.setIdentifyRoleType(0);
                    appUserSave.setAccountEpurseId(BigDecimal.valueOf(userInfo.getAccount_epurse_id()));
                    AppUser result = null;
                    try {
                        result = appUserRepository.save(appUserSave);
                        //Save shop profile
                        shopProfileRepository.insertShopProfile(result.getId(), getOTPRequest.getUsername(), null);
                        walletService.createUserWallet(result.getId(), userInfo.getAccount_epurse_id());
                    } catch (Exception e) {
                        logger.info("======SYNC SAVE APPUSER EXCEPTION======" + getOTPRequest.getUsername(), e);
                        appUserRepository.delete(appUserSave);
                        return new VerifyOTPResponse(500, errorCodes.get(500).getMessage(), null);
                    }
                    ShopProfile shopProfile = shopProfileRepository.findByAppUserId(result.getId());
                    if (shopProfile == null)
                        return new VerifyOTPResponse(101, errorCodes.get(101).getMessage(), null);
                    getOTPRequest.setPhone(shopProfile.getPhoneOtp());
                    //Get cache OTP
                    String cache = CallRedis.getCache("OTP:" + getOTPRequest.getUsername());
                    if (cache == null) {
                        WalletBaseResponse baseResponse = walletService.getOTP(getOTPRequest);
                        if (baseResponse != null) {
                            VerifyOTPResponse verifyOTPResponse = new VerifyOTPResponse(baseResponse.getStatus(),
                                    errorCodes.get(baseResponse.getStatus()).getMessage(), shopProfile.getPhoneOtp());
                            if (baseResponse.getStatus() == 200) {
                                CallRedis.setCacheExpiry("OTP:" + getOTPRequest.getUsername(), "1", 1);
                            }
                            return verifyOTPResponse;
                        }
                    } else {
                        int number = Integer.parseInt(cache);
                        if (number < AppConfig.getInstance().maxOtpPerDay) {
                            WalletBaseResponse baseResponse = walletService.getOTP(getOTPRequest);
                            if (baseResponse != null) {
                                VerifyOTPResponse verifyOTPResponse = new VerifyOTPResponse(baseResponse.getStatus(),
                                        errorCodes.get(baseResponse.getStatus()).getMessage(), shopProfile.getPhoneOtp());
                                if (baseResponse.getStatus() == 200) {
                                    number = number + 1;
                                    verifyOTPResponse.setMessage("Get OTP thành công,đã gửi về điện thoại");
                                    CallRedis.setCacheExpiry("OTP:" + getOTPRequest.getUsername(), String.valueOf(number), 1);
                                }
                                return verifyOTPResponse;
                            }
                        } else
                            return new VerifyOTPResponse(211, errorCodes.get(211).getMessage(), shopProfile.getPhoneOtp());
                    }
                }
                return new VerifyOTPResponse(101, errorCodes.get(101).getMessage(), null);
            }
        } catch (Exception e) {
            logger.info("====GET OTP EXCEPTION===== " + gson.toJson(getOTPRequest), e);
        }
        return new VerifyOTPResponse(500, errorCodes.get(500).getMessage(), null);
    }

//    public void saveUserDevice(String deviceToken, AppUser appUser) {
//        List<AppUserDevice> appUserDevices = appUserDeviceRepository.getExistByTokenAndAppUser(appUser.getId(), deviceToken);
//        if (appUserDevices != null && appUserDevices.size() == 0) {
//            AppUserDevice appUserDevice = new AppUserDevice();
//            appUserDevice.setAppUserId(BigDecimal.valueOf(appUser.getId()));
//            appUserDevice.setDeviceToken(deviceToken.trim());
//            appUserDevice.setShop(BigDecimal.ZERO);
//            appUserDevice.setEnabled(BigDecimal.ZERO);
//            appUserDevice.setConfirm("OTP");
//            appUserDeviceRepository.insertDevice(appUserDevice.getAppUserId(), appUserDevice.getConfirm(),
//                    appUserDevice.getDeviceToken());
//        }
//    }

    private AppUser saveAppUser(SignUpRequest signUpRequest, Long accountEpurseId) {
        AppUser appUser = new AppUser();
        appUser.setPhone(signUpRequest.getUsername().trim());
        appUser.setEmail(signUpRequest.getEmail().trim());
        appUser.setIdentify("PASSWORD");
        appUser.setAccessToken(UUID.randomUUID().toString());
        appUser.setAccountCode(String.valueOf(System.nanoTime()));
        appUser.setIdentifyRoleType(0);
        appUser.setShop(2);
        appUser.setAllowWithdraw(1);
        appUser.setShip(0);
//        appUser.setWalletShopId(BigDecimal.valueOf(accountEpurseId));
        appUser.setAccountEpurseId(BigDecimal.valueOf(accountEpurseId));
        try {
            AppUser result = appUserRepository.save(appUser);
            //Save shop profile
            shopProfileRepository.insertShopProfile(result.getId(), signUpRequest.getUsername(), signUpRequest.getEmail());
            return result;
        } catch (Exception e) {
            logger.info("======REGISTER SAVE APPUSER EXCEPTION======" + signUpRequest.getUsername(), e);
            appUserRepository.delete(appUser);
        }
        return null;
    }

    public BaseResponse getUserInfo(String username) throws Exception {
        AppUser appUser = appUserRepository.findByPhone(username);
        ShopProfile shopProfile = shopProfileRepository.findByAppUserId(appUser.getId());
//        logger.info("=======DEBUG USER INFO==========" + gson.toJson(appUser) + "|| PROFILE " + gson.toJson(shopProfile));
        if (shopProfile == null) {
            return new BaseResponse(101);
        } else {
//            UserInfoResponse userInfoResponse = mapperFacade.map(appUser, UserInfoResponse.class);
            UserInfoResponse userInfoResponse = new UserInfoResponse();
            userInfoResponse.setName(appUser.getName());
            userInfoResponse.setEmail(appUser.getEmail());
            userInfoResponse.setAvatar(appUser.getAvatar());
            userInfoResponse.setUsername(appUser.getPhone());
            userInfoResponse.setIdentityCard(appUser.getIdentityCard());
            userInfoResponse.setSex(appUser.getSex());
            //Token
            userInfoResponse.setAccessToken(appUser.getAccessToken());
            //Point
            userInfoResponse.setPoint(appUser.getPoint());
            userInfoResponse.setProfileLevel(appUser.getProfileLevel());
            userInfoResponse.setCompany(appUser.getCompany());
            userInfoResponse.setRating(appUser.getRating());
            //Ship shop
            userInfoResponse.setShip(appUser.getShip());
            userInfoResponse.setShop(appUser.getShop());
            //Types
            userInfoResponse.setTypes(appUser.getTypes());
            userInfoResponse.setUserGroupId(appUser.getUserGroupId());
            userInfoResponse.setUtimestamp(appUser.getUtimestamp());
            //Address
            userInfoResponse.setAddress(addressDAO.getAddressData(appUser.getId()));
            //Info from shop profile
            userInfoResponse.setPlaceIdCard(shopProfile.getPlaceIdCard());
            userInfoResponse.setPhoneOTP(shopProfile.getPhoneOtp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            AppContractReport contractReport = contractReportRepository.findByAppUserId(appUser.getId());
            if (contractReport != null) {
                UserContractResponse contractResponse = mapperFacade.map(contractReport, UserContractResponse.class);
                BankAccount bankAccount = bankAccountRepository.findByIdAndPhone(BigDecimal.valueOf(contractReport.getBankAccountId()), appUser.getPhone());
                Bank bank = bankRepository.findByBankCode(bankAccount.getBankCode());
                BankAccountResponse bankAccountResponse = mapperFacade.map(bankAccount, BankAccountResponse.class);
                bankAccountResponse.setImageBank(
                        AppConfig.getInstance().imageUrl.replace("/document", "")
                                + bank.getImageBank());
                bankAccountResponse.setBankName(bank.getName());
                bankAccountResponse.setBankCode(bank.getBankCode());
                contractResponse.setBankAccount(bankAccountResponse);
                userInfoResponse.setContract(contractResponse);
            }
            if (appUser.getBirthday() != null)
                userInfoResponse.setBirthday(simpleDateFormat.format(appUser.getBirthday()));
            if (shopProfile.getDateIdCard() != null)
                userInfoResponse.setDateIdCard(simpleDateFormat.format(shopProfile.getDateIdCard()));
            WalletUserInfo data = walletService.getUserInfo(username);
            if (data != null) {
                HashMap<String, Object> balance = new HashMap<>();
                for (WalletUserBalanceData balanceData : data.getBalance()) {
                    balance.put("W" + balanceData.getBal_code(), balanceData);
                }
                if (userInfoResponse.getEmail() != null
                        && !userInfoResponse.getEmail().isEmpty()
                        && userInfoResponse.getAvatar() != null
                        && !userInfoResponse.getAvatar().isEmpty()
                        && userInfoResponse.getAddress() != null
                        && userInfoResponse.getName()!=null
                        && !userInfoResponse.getName().isEmpty()
                )
                    userInfoResponse.setIsUpdated(1);
                userInfoResponse.setWalletInfo(balance);
                //CACHE
                handleCacheUserInfo(userInfoResponse, username);
                return new BaseResponse(200, userInfoResponse);
            }
            return new BaseResponse(500);
        }
    }

    public BaseResponse checkPermission(CheckPermissionRequest permissionRequest, String username) {
        AppUser appUser = appUserRepository.findByPhone(username);
        //Rut tien
        if (permissionRequest.getType() == 1) {
            if (appUser.getAllowWithdraw() == null || appUser.getAllowWithdraw() != 0)
                return new BaseResponse(200);
            else
                return new BaseResponse(403, "Tài khoản không có quyền truy cập tính năng này", 3);
        }
        return new BaseResponse(200);
    }

    private void handleCacheUserInfo(UserInfoResponse userInfoResponse, String username) {
        //TODO UPDATE CACHE REDIS
        String oldCache = CallRedis.getCache(username);
        if (oldCache == null) {
            CallRedis.setCacheExpiry(username, rememberMeCache, gson.toJson(userInfoResponse));
        } else {
            UserInfoResponse cache = gson.fromJson(oldCache, UserInfoResponse.class);
            List<ShopAddressDTO> pickupAddress = cache.getPickupAddress();
            cache = mapperFacade.map(userInfoResponse, UserInfoResponse.class);
            cache.setWalletInfo(userInfoResponse.getWalletInfo());
            //Shop address
            cache.setPickupAddress(pickupAddress);
            CallRedis.updateKey(username, gson.toJson(cache));
        }

    }

    public BaseResponse updateUserInfo(UpdateUserInfoRequest updateUserInfoRequest, String username) {
        AppUser oldUser = appUserRepository.findByPhone(username);
        if (oldUser == null) {
            return new BaseResponse(101);
        } else {
            ShopProfile shopProfile = shopProfileRepository.findByAppUserId(oldUser.getId());
            if (shopProfile == null) {
                return new BaseResponse(101);
            }
            try {
                //Name
                oldUser.setName(updateUserInfoRequest.getName());
                //Email
                List<AppUser> email = appUserRepository.findByEmail(updateUserInfoRequest.getEmail());
                if (email.size() == 1 && email.get(0).getPhone().equals(oldUser.getPhone()))
                    oldUser.setEmail(updateUserInfoRequest.getEmail());
                else if (email.size() == 0)
                    oldUser.setEmail(updateUserInfoRequest.getEmail());
                else return new BaseResponse(141);
                shopProfile.setEmailBill(updateUserInfoRequest.getEmail());
                //BOD
                if (updateUserInfoRequest.getBirthday() != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date birthDay = dateFormat.parse(updateUserInfoRequest.getBirthday());
                    oldUser.setBirthday(birthDay);
                } else oldUser.setBirthday(null);
                //Avatar
                if (!updateUserInfoRequest.getAvatar().isEmpty() && updateUserInfoRequest.getAvatar() != null)
                    oldUser.setAvatar(updateUserInfoRequest.getAvatar());
                //ID card
                oldUser.setIdentityCard(updateUserInfoRequest.getIdentityCard());
                shopProfile.setPlaceIdCard(updateUserInfoRequest.getPlaceIdCard());
                if (updateUserInfoRequest.getDateIdCard() != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    shopProfile.setDateIdCard(dateFormat.parse(updateUserInfoRequest.getDateIdCard()));
                } else shopProfile.setDateIdCard(null);
                //Gender
                if (updateUserInfoRequest.getSex() != null)
                    oldUser.setSex(updateUserInfoRequest.getSex());
                //Company
                if (updateUserInfoRequest.getCompany() != null)
                    oldUser.setCompany(updateUserInfoRequest.getCompany());
                //Address
                shopProfile.setAddress(updateUserInfoRequest.getAddress().getAddress());
                shopProfile.setProvinceCode(updateUserInfoRequest.getAddress().getProvinceCode());
                shopProfile.setDistrictCode(updateUserInfoRequest.getAddress().getDistrictCode());
                shopProfile.setWardCode(updateUserInfoRequest.getAddress().getWardCode());
                //PhoneOTP
                shopProfile.setPhoneOtp(updateUserInfoRequest.getPhoneOTP());
                //Update info to wallet
                try {
                    WalletBaseResponse walletBaseResponse = walletService.updateUserInfo(updateUserInfoRequest, username);
                    if (walletBaseResponse.getStatus() == 200) {
                        shopProfileRepository.save(shopProfile);
//                        Address result = addressRepository.save(address);
//                        oldUser.setAddressId(BigDecimal.valueOf(result.getId()));
                        appUserRepository.save(oldUser);
                        return getUserInfo(username);
                    } else {
                        return new BaseResponse(walletBaseResponse.getStatus());
                    }
                } catch (Exception e) {
                    logger.info("====UPDATE USER EXCEPTION====== " + username, e);
                    return new BaseResponse(500);
                }
            } catch (Exception e) {
                logger.info("====UPDATE USER EXCEPTION====== " + username, e);
                return new BaseResponse(400);
            }
        }
    }

    public BaseResponse updatePhone(UpdatePhoneRequest updatePhoneRequest) {
        AppUser oldUser = appUserRepository.findByPhone(updatePhoneRequest.getUsername());
        if (oldUser == null) {
            return new BaseResponse(101, errorCodes.get(101));
        } else {
            ShopProfile shopProfile = shopProfileRepository.findByAppUserId(oldUser.getId());
            if (shopProfile == null) {
                return new BaseResponse(101, errorCodes.get(101));
            }
            try {
                WalletBaseResponse walletBaseResponse = walletService.updatePhone(updatePhoneRequest);
                if (walletBaseResponse.getStatus() == 200) {
                    shopProfileRepository.updatePhoneOtp(updatePhoneRequest.getPhoneOTP(), oldUser.getId());
                }
                return new BaseResponse(walletBaseResponse.getStatus());
            } catch (Exception e) {
                logger.info("====UPDATE USER EXCEPTION====== " + updatePhoneRequest.getUsername(), e);
                return new BaseResponse(400);
            }
        }
    }

    public BaseResponse resetPassword(UserResetPassRequest userResetPassRequest) {
        try {
//            AppUser appUser = appUserRepository.findByPhone(userResetPassRequest.getUsername());
//            if (appUser == null)
//                return new BaseResponse(101);
            WalletBaseResponse walletBaseResponse = walletService.resetPass(userResetPassRequest);
//            if (walletBaseResponse.getStatus() == 200) {
//                shopProfile.setPhoneOtp(updatePhoneRequest.getNewPhone());
//                shopProfileRepository.save(shopProfile);
//            }
            return new BaseResponse(walletBaseResponse.getStatus());
        } catch (Exception e) {
            logger.info("====UPDATE USER EXCEPTION====== " + userResetPassRequest.getUsername(), e);
            return new BaseResponse(500);
        }

    }

    public BaseResponse changePass(ChangePasswordRequest changePasswordRequest) {
        try {
            WalletBaseResponse walletBaseResponse = walletService.changePass(changePasswordRequest);
            if (walletBaseResponse.getStatus() == 200) {
                return new BaseResponse(410);
            } else return new BaseResponse(walletBaseResponse.getStatus());
        } catch (Exception e) {
            logger.info("=========CHANGE PASSS EXCEPTION======" + changePasswordRequest.getUsername(), e);
        }
        return new BaseResponse(500);
    }

    public BaseResponse updateAvatar(UpdateAvatarRequest updateAvatarRequest) {
        AppUser oldUser = appUserRepository.findByPhone(updateAvatarRequest.getUsername());
        if (oldUser == null) {
            return new BaseResponse(101);
        } else {
            try {
                if (!updateAvatarRequest.getAvatarUrl().isEmpty() && updateAvatarRequest.getAvatarUrl() != null) {
                    oldUser.setAvatar(updateAvatarRequest.getAvatarUrl());
                    appUserRepository.save(oldUser);
                    String data = CallRedis.getCache(updateAvatarRequest.getUsername());
                    UserInfoResponse userInfoResponse = gson.fromJson(data, UserInfoResponse.class);
                    userInfoResponse.setAvatar(updateAvatarRequest.getAvatarUrl());
                    CallRedis.updateKey(updateAvatarRequest.getUsername(), gson.toJson(userInfoResponse));
                }
                return new BaseResponse(200);
            } catch (Exception e) {
                return new BaseResponse(500);
            }
        }
    }

    public BaseResponse createContract(String username, CreateContractRequest createContractRequest) {
        BaseResponse validate = validateContract(createContractRequest);
        if (validate != null)
            return validate;
        AppUser appUser = appUserRepository.findByPhone(username);
        AppContract old = appContractRepository.findByAppUserId(appUser.getId());
        if (old != null)
            return new BaseResponse(666, "Tài khoản đã có hợp đồng", 3);
        BankAccount bankAccount = bankAccountRepository
                .findByIdAndPhoneAndIsDeleted(BigDecimal.valueOf(createContractRequest.getBankAccountId()), username, 1);
        if (bankAccount == null)
            return new BaseResponse(128);
        AppContract appContract = mapperFacade.map(createContractRequest, AppContract.class);
        appContract.setAppUserId(appUser.getId());
        appContract.setBankAccountId(bankAccount.getId());
        appContractRepository.save(appContract);
        return new BaseResponse(200, appContract);
    }

    private ShopProfile syncShipProfile(AppUser appUser, ShipperProfile shipperProfile) {
        try {
            logger.info("=======SYNC SHIP PROFILE TO SHOP PROFILE=========" + appUser.getPhone());
            ShopProfile shopProfile = new ShopProfile();
            shopProfile.setAppUserId(appUser.getId());
            if (shipperProfile.getContactPhone() != null)
                shopProfile.setPhoneOtp(shipperProfile.getContactPhone());
            else
                shopProfile.setPhoneOtp(appUser.getPhone());
            shopProfile.setEmailBill(appUser.getEmail());
            shopProfile.setNumberIdCard(shipperProfile.getNumberIdCard());
            shopProfile.setDateIdCard(shipperProfile.getDateIdCard());
            shopProfile.setPlaceIdCard(shipperProfile.getPlaceIdCard());
            shopProfile.setStatus(1);
            shopProfile.setAddress(shipperProfile.getContactAddress());
            ShopProfile result = shopProfileRepository.save(shopProfile);
            appUser.setShop(2);
            appUserRepository.save(appUser);
            return result;
        } catch (Exception e) {
            logger.info("=======SYNC SHIP PROFILE EXCEPTION=========" + appUser.getPhone(), e);
            return null;
        }


    }

    private BaseResponse validateContract(CreateContractRequest createContractRequest) {
        if (createContractRequest.getPhone() == null || createContractRequest.getPhone().isEmpty() || !createContractRequest.getPhone().matches("[0-9]+"))
            return new BaseResponse(666, "Số điện thoại không được để trống", 3);
        if (createContractRequest.getOwner() == null || createContractRequest.getOwner().isEmpty())
            return new BaseResponse(666, "Chủ sở hữu không được để trống", 3);
        if (createContractRequest.getPosition() == null || createContractRequest.getPosition().isEmpty())
            return new BaseResponse(666, "Vị trí không được để trống", 3);
        if (createContractRequest.getAddress() == null || createContractRequest.getAddress().isEmpty())
            return new BaseResponse(666, "Địa chỉ không được để trống", 3);
        if (createContractRequest.getContractFileUrl() == null || createContractRequest.getContractFileUrl().isEmpty())
            return new BaseResponse(666, "File hợp đồng không được để trống", 3);
        if (createContractRequest.getSignatureUrl() == null || createContractRequest.getSignatureUrl().isEmpty())
            return new BaseResponse(666, "Ảnh chữ ký không được để trống", 3);
        if (createContractRequest.getBankAccountId() == null)
            return new BaseResponse(666, "Vui lòng chọn tài khoản ngân hàng", 3);
        //1 Ca nhan 2 Doanh nghiep
        if (createContractRequest.getType() == 1) {
            if (createContractRequest.getDob() == null || createContractRequest.getDob().isEmpty())
                return new BaseResponse(666, "Ngày sinh không được để trống", 3);
            if (createContractRequest.getIdCard() == null || createContractRequest.getIdCard().isEmpty())
                return new BaseResponse(666, "Số chứng minh thư không được để trống", 3);
            if (createContractRequest.getIdCardDate() == null || createContractRequest.getIdCardDate().isEmpty())
                return new BaseResponse(666, "Ngày cấp không được để trống", 3);
            if (createContractRequest.getIdCardPlace() == null || createContractRequest.getIdCardPlace().isEmpty())
                return new BaseResponse(666, "Nơi cấp không được để trống", 3);
            if (createContractRequest.getCardBackUrl() == null || createContractRequest.getCardBackUrl().isEmpty())
                return new BaseResponse(666, "Ảnh chứng minh thư mặt sau không được để trống", 3);
            if (createContractRequest.getCardFrontUrl() == null || createContractRequest.getCardFrontUrl().isEmpty())
                return new BaseResponse(666, "Ảnh chứng minh thư mặt trước không được để trống", 3);
        } else {
            if (createContractRequest.getCardBackUrl() == null || createContractRequest.getCardBackUrl().isEmpty())
                return new BaseResponse(666, "Ảnh  không được để trống", 3);
            if (createContractRequest.getCardFrontUrl() == null || createContractRequest.getCardFrontUrl().isEmpty())
                return new BaseResponse(666, "Ảnh chứng minh thư mặt trước không được để trống", 3);
        }
        return null;
    }

    public static void main(String[] args) throws ParseException {
        Integer a = null;
        System.out.println(gson.toJson(a));
    }
}
