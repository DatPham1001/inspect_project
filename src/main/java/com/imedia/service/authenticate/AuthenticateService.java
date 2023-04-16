package com.imedia.service.authenticate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.config.application.AppConfig;
import com.imedia.config.authentication.JwtTokenUtil;
import com.imedia.config.jwt.CustomUserDetails;
import com.imedia.oracle.dao.ShopProfileDAO;
import com.imedia.oracle.entity.*;
import com.imedia.oracle.repository.*;
import com.imedia.service.authenticate.model.SignInRequest;
import com.imedia.service.authenticate.model.SignInResponse;
import com.imedia.service.authenticate.model.SignInWalletResponse;
import com.imedia.service.authenticate.model.SocialInfo;
import com.imedia.service.cache.model.CacheLoginInfo;
import com.imedia.service.cache.model.CacheSocialInfo;
import com.imedia.service.pickupaddress.PickupAddressService;
import com.imedia.service.user.UserService;
import com.imedia.service.user.model.GetOTPRequest;
import com.imedia.service.user.model.VerifyOTPResponse;
import com.imedia.service.wallet.WalletService;
import com.imedia.service.wallet.model.WalletBaseResponse;
import com.imedia.service.wallet.model.WalletUserInfoResponse;
import com.imedia.util.AppUtil;
import com.imedia.util.CallRedis;
import com.imedia.util.PreLoadStaticUtil;
import com.imedia.util.TripleDES;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthenticateService implements UserDetailsService {
    static final Logger logger = LogManager.getLogger(AuthenticateService.class);
    static final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    private final WalletService walletService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AppUserRepository appUserRepository;
    private final AppAuthRepository appAuthRepository;
    private final AppUserDeviceRepository appUserDeviceRepository;
    private final UserService userService;
    private final ShopProfileRepository shopProfileRepository;
    private static final HashMap<Integer, ErrorCodesWebshop> errorCodes = PreLoadStaticUtil.errorCodeWeb;
    private final PickupAddressService pickupAddressService;
    private final ShopProfileDAO shopProfileDAO;
    private final ShipperProfileRepository shipperProfileRepository;
    @Autowired
    public AuthenticateService(WalletService walletService, JwtTokenUtil jwtTokenUtil, AppUserRepository appUserRepository, AppAuthRepository appAuthRepository, AppUserDeviceRepository appUserDeviceRepository, UserService userService, ShopProfileRepository shopProfileRepository, PickupAddressService pickupAddressService, ShopProfileDAO shopProfileDAO, ShipperProfileRepository shipperProfileRepository) {
        this.walletService = walletService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.appUserRepository = appUserRepository;
        this.appAuthRepository = appAuthRepository;
        this.appUserDeviceRepository = appUserDeviceRepository;
        this.userService = userService;
        this.shopProfileRepository = shopProfileRepository;
        this.pickupAddressService = pickupAddressService;
        this.shopProfileDAO = shopProfileDAO;
        this.shipperProfileRepository = shipperProfileRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByPhone(username);
        if (appUser != null) {
            return new User(appUser.getPhone(), "",
                    new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    public SignInResponse login(SignInRequest signInRequest) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        try {
            WalletBaseResponse baseResponse = walletService.login(signInRequest);
            if (baseResponse != null) {
                SignInResponse signInResponse = new SignInResponse(errorCodes.get(baseResponse.getStatus()).getMessage(),
                        baseResponse.getStatus());
                //Request ok
                if (baseResponse.getStatus() == 200) {
                    //Decrypt
                    String decryptedData = "";
                    try {
                        decryptedData = TripleDES.decrypt(appConfig.epurse_key, baseResponse.getData());
                    } catch (Exception e) {
                        logger.info("Decrypt exception : " + gson.toJson(baseResponse), e);
                    }
                    SignInWalletResponse signInWalletResponse = gson.fromJson(decryptedData, SignInWalletResponse.class);
                    AppUser oldAppUser = appUserRepository.findByPhone(signInRequest.getUsername());
                    if (oldAppUser == null) {
                        AppUser appUser = saveAppUser(signInRequest, signInWalletResponse.getAccount_epurse_id());
                        if (appUser == null)
                            return new SignInResponse(PreLoadStaticUtil.errorCodeWeb.get(500).getMessage(), 500);
                        walletService.createUserWallet(appUser.getId(), signInWalletResponse.getAccount_epurse_id());
                    } else {
                        if (oldAppUser.getShip() == null) oldAppUser.setShip(0);
                        if (oldAppUser.getShop() == null) oldAppUser.setShop(2);
                        ShipperProfile shipperProfile = shipperProfileRepository.findByProfileId(oldAppUser.getId());
                        ShopProfile shopProfile = shopProfileRepository.findByAppUserId(oldAppUser.getId());
                        //Sync shipper profile
                        if (shopProfile == null && shipperProfile != null)
                            syncShipProfile(oldAppUser, shipperProfile);
                    }
                    //Get cache first
                    boolean isNewDevice = true;
                    isNewDevice = handleLoginCache(signInRequest, isNewDevice);
                    if (isNewDevice && appConfig.otpStatus) {
                        signInResponse.setStatus(501);
                        signInResponse.setMessage(PreLoadStaticUtil.errorCodeWeb.get(501).getMessage());
                        //Send get otp
                        GetOTPRequest getOTPRequest = new GetOTPRequest(signInRequest.getUsername(), signInRequest.getUsername(), signInRequest.getDeviceId());
                        VerifyOTPResponse verifyOTPResponse = userService.getOTP(getOTPRequest);
                        if (verifyOTPResponse.getStatus() == 211)
                            return new SignInResponse(PreLoadStaticUtil.errorCodeWeb.get(211).getMessage(), 211);
                        return signInResponse;
                    }
                    CustomUserDetails userDetails = new CustomUserDetails(signInRequest.getUsername(), signInWalletResponse.getSession_key(), signInRequest.getRememberMe());
                    String token = jwtTokenUtil.generateToken(userDetails);
                    signInResponse.setUsername(signInRequest.getUsername());
                    signInResponse.setToken(token);
                    signInResponse.setSessionKey(signInWalletResponse.getSession_key());
                    signInResponse.setMessage("Đăng nhập thành công!");
                    //Balance
//                    WalletUserInfo data = walletService.getUserInfo(signInRequest.getUsername());
//                    if (data != null) {
//                        HashMap<String, Object> balance = new HashMap<>();
//                        balance.put("username", data.getUsername());
//                        balance.put("email", data.getEmail());
//                        for (WalletUserBalanceData balanceData : data.getBalance()) {
//                            balance.put("W" + balanceData.getBal_code(), balanceData);
//                        }
//                        signInResponse.setUserInfo(balance);
//                    }
                    handleCacheRemember(signInRequest, signInResponse);
                    saveAppAuth(signInWalletResponse, token, signInRequest.getDeviceId());
                    userService.getUserInfo(signInRequest.getUsername());
                    AppUser appUser = appUserRepository.findByPhone(signInRequest.getUsername());
                    pickupAddressService.handleCachePickupAddress(signInRequest.getUsername(), appUser.getId());
                }
                return signInResponse;
            }
            return new SignInResponse(PreLoadStaticUtil.errorCodeWeb.get(409).getMessage(), 409);
        } catch (Exception e) {
            logger.info("====LOGIN EXCEPTION===== " + gson.toJson(signInRequest), e);
        }
        return new SignInResponse(PreLoadStaticUtil.errorCodeWeb.get(500).getMessage(), 500);
    }

    public SignInResponse loginSocial(SignInRequest signInRequest) throws Exception {
        //Check phone exist
        List<AppUser> oldAppUser = appUserRepository.findAllByFacebookIdOrPhone(signInRequest.getSocialId(), signInRequest.getUsername());
        //create first user
        if (oldAppUser == null || oldAppUser.size() == 0) {
            AppConfig appConfig = AppConfig.getInstance();
            WalletBaseResponse baseResponse = walletService.login(signInRequest);
            if (baseResponse != null) {
                SignInResponse signInResponse = new SignInResponse(baseResponse.getResponse_msg(),baseResponse.getStatus());
                //Request ok
                if (baseResponse.getStatus() == 200) {
                    //Send OTP
                    WalletBaseResponse walletBaseResponse = walletService.getOTP(new
                            GetOTPRequest(signInRequest.getUsername(), signInRequest.getUsername(), signInRequest.getDeviceId()));
                    if (walletBaseResponse.getStatus() == 200) {
                        signInResponse.setStatus(201);
                        signInResponse.setMessage("Đăng ký thành công,đã gửi OTP về số điện thoại");
                        //Decrypt
                        WalletUserInfoResponse data = gson.fromJson(
                                TripleDES.decrypt(AppConfig.getInstance().epurse_key, baseResponse.getData()), WalletUserInfoResponse.class);
                        AppUser appUser = saveAppUser(signInRequest, data.getAccount_epurse_id());
                        if (appUser == null)
                            return new SignInResponse(PreLoadStaticUtil.errorCodeWeb.get(500).getMessage(), 500);
//                        userService.saveUserDevice(signInRequest.getDeviceId(), appUser);
                        walletService.createUserWallet(appUser.getId(), data.getAccount_epurse_id());
                        handleCacheRemember(signInRequest, signInResponse);
                        handleCacheSocialId(appUser);
                        //Balance
//                        WalletUserInfo data2 = walletService.getUserInfo(signInRequest.getUsername());
//                        if (data2 != null) {
//                            HashMap<String, Object> balance = new HashMap<>();
//                            balance.put("username", data2.getUsername());
//                            balance.put("email", data2.getEmail());
//                            for (WalletUserBalanceData balanceData : data2.getBalance()) {
//                                balance.put("W" + balanceData.getBal_code(), balanceData);
//                            }
//                            signInResponse.setUserInfo(balance);
//                        }
                        userService.getUserInfo(signInRequest.getUsername());
                        pickupAddressService.handleCachePickupAddress(appUser.getPhone(), appUser.getId());
                    } else {
                        signInResponse.setStatus(walletBaseResponse.getStatus());
                        signInResponse.setMessage(errorCodes.get(walletBaseResponse.getStatus()).getMessage());
                    }
                }
                return signInResponse;
            } else
                return new SignInResponse(PreLoadStaticUtil.errorCodeWeb.get(409).getMessage(), 409);

        } else {
            //Check phone and facebook id are pared
            if (oldAppUser.size() > 1) {
                return new SignInResponse(errorCodes.get(406).getMessage(), 406);
            } else {
                if (oldAppUser.get(0).getFacebookId() != null && signInRequest.getSocialId() != null
                        && signInRequest.getUsername().trim().equals(oldAppUser.get(0).getPhone().trim())
                        && signInRequest.getSocialId().trim().equals(oldAppUser.get(0).getFacebookId().trim())) {
                    SignInResponse signInResponse = handleSocialWalletLogin(signInRequest);
                    handleCacheSocialId(oldAppUser.get(0));
                    userService.getUserInfo(signInRequest.getUsername());
                    pickupAddressService.handleCachePickupAddress(oldAppUser.get(0).getPhone(), oldAppUser.get(0).getId());
                    return signInResponse;
                } else {
                    return new SignInResponse(errorCodes.get(406).getMessage(), 406);
                }
            }
        }
    }

    private SignInResponse handleSocialWalletLogin(SignInRequest signInRequest) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        try {
            WalletBaseResponse baseResponse = walletService.login(signInRequest);
            if (baseResponse != null) {
                SignInResponse signInResponse = new SignInResponse(PreLoadStaticUtil.errorCodeWeb.get(baseResponse.getStatus()).getMessage(),baseResponse.getStatus());
                //Request ok
                if (baseResponse.getStatus() == 200) {
                    //Decrypt
                    String decryptedData = "";
                    try {
                        decryptedData = TripleDES.decrypt(appConfig.epurse_key, baseResponse.getData());
                    } catch (Exception e) {
                        logger.info("Decrypt exception : " + gson.toJson(baseResponse), e);
                    }
                    SignInWalletResponse signInWalletResponse = gson.fromJson(decryptedData, SignInWalletResponse.class);
                    //TODO check first login
                    //Get cache first
                    boolean isNewDevice = true;
                    isNewDevice = handleLoginCache(signInRequest, isNewDevice);
                    if (isNewDevice && appConfig.otpStatus) {
                        signInResponse.setStatus(501);
                        signInResponse.setMessage(PreLoadStaticUtil.errorCodeWeb.get(501).getMessage());
                        //Send get otp
                        GetOTPRequest getOTPRequest = new GetOTPRequest(signInRequest.getUsername(), signInRequest.getUsername(), signInRequest.getDeviceId());
                        VerifyOTPResponse verifyOTPResponse = userService.getOTP(getOTPRequest);
                        if (verifyOTPResponse.getStatus() == 211)
                            return new SignInResponse(PreLoadStaticUtil.errorCodeWeb.get(211).getMessage(), 211);
                        return signInResponse;
                    }
                    CustomUserDetails userDetails = new CustomUserDetails(signInRequest.getUsername(), signInWalletResponse.getSession_key(), signInRequest.getRememberMe());
                    String token = jwtTokenUtil.generateToken(userDetails);
                    signInResponse.setUsername(signInRequest.getUsername());
                    signInResponse.setToken(token);
                    signInResponse.setSessionKey(signInWalletResponse.getSession_key());
                    signInResponse.setMessage("Đăng nhập thành công!");
                    handleCacheRemember(signInRequest, signInResponse);
                    //TODO save app auth to db
                    saveAppAuth(signInWalletResponse, token, signInRequest.getDeviceId());

                }
                return signInResponse;
            }
        } catch (Exception e) {
            logger.info("====LOGIN EXCEPTION===== " + gson.toJson(signInRequest), e);
        }
        return new SignInResponse(PreLoadStaticUtil.errorCodeWeb.get(500).getMessage(), 500);
    }

    private void handleCacheRemember(SignInRequest signInRequest, SignInResponse signInResponse) throws Exception {
        //Set key to redis
        if (signInRequest.getKey() != null) {
            if (signInRequest.getRememberMe() == null)
                signInRequest.setRememberMe(1);
            if (signInRequest.getRememberMe() == 0)
                CallRedis.setCacheExpiry(signInRequest.getKey(), gson.toJson(signInResponse));
            else
                CallRedis.setCacheExpiry(signInRequest.getKey(), AppConfig.getInstance().rememberMeCache, gson.toJson(signInResponse));
        }
    }

    private void handleCacheSocialId(AppUser appUser) {
        String cacheSocial = CallRedis.getCache("SOCIAL_ID");
        CacheSocialInfo cacheSocialInfo;
        if (cacheSocial != null) {
            cacheSocialInfo = gson.fromJson(cacheSocial, CacheSocialInfo.class);
            SocialInfo socialInfo = new SocialInfo(appUser.getFacebookId(), appUser.getPhone());
            cacheSocialInfo.getSocialInfos().add(socialInfo);
            List<SocialInfo> socialInfos = cacheSocialInfo.getSocialInfos().stream()
                    .filter(AppUtil.distinctByKey(SocialInfo::getUsername)).collect(Collectors.toList());
            cacheSocialInfo.setSocialInfos(socialInfos);
        } else {
            cacheSocialInfo = new CacheSocialInfo(Collections.singletonList(new SocialInfo(appUser.getFacebookId(), appUser.getPhone())));
        }
        CallRedis.setCache("SOCIAL_ID", gson.toJson(cacheSocialInfo));
    }

//    private void saveUserDevice(String deviceToken, AppUser appUser) {
//        List<AppUserDevice> appUserDevices = appUserDeviceRepository.getActiveByTokenAndAppUser(appUser.getId(), deviceToken.trim());
//        if(appUserDevices.size() ==0){
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

    private AppUser saveAppUser(SignInRequest signInRequest, Long accountEpurseId) throws Exception {
//        AppConfig appConfig = AppConfig.getInstance();
        AppUser appUser = new AppUser();
        appUser.setPhone(signInRequest.getUsername().trim());
        if (signInRequest.getSocialId() != null)
            appUser.setFacebookId(signInRequest.getSocialId());
        appUser.setEmail(signInRequest.getEmail().trim());
//        if (signInRequest.getAccessToken() != null && !signInRequest.getAccessToken().isEmpty())
//            appUser.setAccessToken(signInRequest.getAccessToken());
//        else
            appUser.setAccessToken(UUID.randomUUID().toString());
        appUser.setIdentify("PASSWORD");
        appUser.setAccountCode(String.valueOf(System.nanoTime()));
        appUser.setIdentifyRoleType(0);
        appUser.setShop(2);
        appUser.setShip(0);
        appUser.setAccountEpurseId(BigDecimal.valueOf(accountEpurseId));
        try {
            AppUser result = appUserRepository.save(appUser);
            //Save shop profile
            shopProfileRepository.insertShopProfile(result.getId(), signInRequest.getUsername(), signInRequest.getEmail());
            return result;
        } catch (Exception e) {
            logger.info("======REGISTER SAVE APPUSER EXCEPTION======" + signInRequest.getUsername(), e);
            appUserRepository.delete(appUser);
        }
        return null;
    }

    private AppAuth saveAppAuth(SignInWalletResponse signInWalletResponse, String jwt, String deviceToken) {
        AppAuth appAuth = new AppAuth();
        appAuth.setId(System.nanoTime());
        appAuth.setService("WEB SHOP");
        appAuth.setSessionKey(signInWalletResponse.getSession_key());
        appAuth.setJwt("not use");
        appAuth.setStatus(BigDecimal.valueOf(1));
        //TODO device token
        appAuth.setDeviceToken("not use");
        appAuth.setUserName(signInWalletResponse.getUsername());
        AppAuth result = appAuthRepository.save(appAuth);
        return result;
    }

    private CacheLoginInfo getCacheLoginInfo(SignInRequest signInRequest) {
        String cacheData = CallRedis.getCache("LOGIN:" + signInRequest.getUsername());
        if (cacheData != null) {
            return gson.fromJson(cacheData, CacheLoginInfo.class);
        }
        return null;
    }

    private boolean handleLoginCache(SignInRequest signInRequest, boolean isNewDevice) {
        CacheLoginInfo cacheLoginInfo = getCacheLoginInfo(signInRequest);
        AppUser appUser = appUserRepository.findByPhone(signInRequest.getUsername());
        if (cacheLoginInfo != null) {
            //Old device
            if (cacheLoginInfo.getDeviceIds().contains(signInRequest.getDeviceId())) {
                appUserDeviceRepository.enableDevice(appUser.getId(), signInRequest.getDeviceId());
                isNewDevice = false;
            }
        } else {
            List<AppUserDevice> appUserDevice = appUserDeviceRepository.getActiveByTokenAndAppUser(appUser.getId(), signInRequest.getDeviceId());
            if (appUserDevice != null && appUserDevice.size() > 0) {
                isNewDevice = false;
                appUserDeviceRepository.enableDevice(appUser.getId(), signInRequest.getDeviceId());
                //Create new cache
                CacheLoginInfo newCacheLoginInfo = new CacheLoginInfo(signInRequest.getUsername(),
                        appUserDevice.stream().map(AppUserDevice::getDeviceToken).collect(Collectors.toList()));
                CallRedis.setCache("LOGIN:" + signInRequest.getUsername(), gson.toJson(newCacheLoginInfo));
            }
        }
        return isNewDevice;
    }

    private ShopProfile syncShipProfile(AppUser appUser, ShipperProfile shipperProfile) {
        try {
            logger.info("=======SYNC SHIP PROFILE TO SHOP PROFILE=========" + appUser.getPhone());
            ShopProfile shopProfile = new ShopProfile();
            shopProfile.setAppUserId(appUser.getId());
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

    public static void main(String[] args) {
        String cacheSocial = CallRedis.getCache("SOCIAL_ID");
        CacheSocialInfo cacheSocialInfo = gson.fromJson(cacheSocial, CacheSocialInfo.class);
        List<SocialInfo> socialInfo = cacheSocialInfo.getSocialInfos().stream()
                .filter(AppUtil.distinctByKey(SocialInfo::getUsername)).collect(Collectors.toList());
        cacheSocialInfo.setSocialInfos(socialInfo);
        CallRedis.setCache("SOCIAL_ID", gson.toJson(cacheSocialInfo));
    }
}