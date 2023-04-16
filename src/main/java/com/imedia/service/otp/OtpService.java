package com.imedia.service.otp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.config.application.AppConfig;
import com.imedia.oracle.dao.AddressDAO;
import com.imedia.oracle.dao.ShopProfileDAO;
import com.imedia.oracle.entity.*;
import com.imedia.oracle.reportrepository.AppContractReportRepository;
import com.imedia.oracle.repository.*;
import com.imedia.service.cache.model.CacheLoginInfo;
import com.imedia.service.device.UserDeviceService;
import com.imedia.service.user.UserService;
import com.imedia.service.user.model.GetOTPRequest;
import com.imedia.service.user.model.VerifyOTPRequest;
import com.imedia.service.user.model.VerifyOTPResponse;
import com.imedia.service.wallet.WalletService;
import com.imedia.service.wallet.model.WalletBaseResponse;
import com.imedia.service.wallet.model.WalletUserInfo;
import com.imedia.util.CallRedis;
import com.imedia.util.PreLoadStaticUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class OtpService {
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

    public OtpService(AppUserRepository appUserRepository, ShopProfileRepository shopProfileRepository, WalletService walletService, AppUserDeviceRepository appUserDeviceRepository, MapperFacade mapperFacade, AddressDAO addressDAO, ShopProfileDAO shopProfileDAO, ShipperProfileRepository shipperProfileRepository, AppContractReportRepository contractReportRepository, BankAccountRepository bankAccountRepository, AppContractRepository appContractRepository, BankRepository bankRepository, UserDeviceService userDeviceService) {
        this.appUserRepository = appUserRepository;
        this.shopProfileRepository = shopProfileRepository;
        this.walletService = walletService;
        this.appUserDeviceRepository = appUserDeviceRepository;
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

    public VerifyOTPResponse getOTP(GetOtp getOTPRequest) {
        try {
            VerifyOTPResponse response =  getOTPRequest.doGetOtp();
            return response;
        } catch (Exception e) {
            logger.info("====GET OTP EXCEPTION===== " + gson.toJson(getOTPRequest), e);
        }
        return new VerifyOTPResponse(500, errorCodes.get(500).getMessage(), null);
    }
}
