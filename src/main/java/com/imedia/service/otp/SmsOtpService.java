package com.imedia.service.otp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.config.application.AppConfig;
import com.imedia.oracle.dao.AddressDAO;
import com.imedia.oracle.dao.ShopProfileDAO;
import com.imedia.oracle.entity.AppUser;
import com.imedia.oracle.entity.ErrorCodesWebshop;
import com.imedia.oracle.entity.ShipperProfile;
import com.imedia.oracle.entity.ShopProfile;
import com.imedia.oracle.reportrepository.AppContractReportRepository;
import com.imedia.oracle.repository.*;
import com.imedia.service.device.UserDeviceService;
import com.imedia.service.user.UserService;
import com.imedia.service.user.model.GetOTPRequest;
import com.imedia.service.user.model.VerifyOTPResponse;
import com.imedia.service.wallet.WalletService;
import com.imedia.service.wallet.model.WalletBaseResponse;
import com.imedia.util.CallRedis;
import com.imedia.util.PreLoadStaticUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class SmsOtpService extends GetOtp {
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
    private GetOTPRequest getOTPRequest;
    private AppUser appUser;

    public SmsOtpService(AppUserRepository appUserRepository, ShopProfileRepository shopProfileRepository, WalletService walletService, AppUserDeviceRepository appUserDeviceRepository, MapperFacade mapperFacade, AddressDAO addressDAO, ShopProfileDAO shopProfileDAO, ShipperProfileRepository shipperProfileRepository, AppContractReportRepository contractReportRepository, BankAccountRepository bankAccountRepository, AppContractRepository appContractRepository, BankRepository bankRepository, UserDeviceService userDeviceService) {
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

    @Override
    public VerifyOTPResponse doGetOtp() throws Exception {
        userDeviceService.saveUserDevice(getOTPRequest.getDeviceId(), appUser);
        ShopProfile shopProfile = shopProfileRepository.findByAppUserId(appUser.getId());
        //Sync from ship profile
        if (shopProfile == null) {
            ShipperProfile shipperProfile = shipperProfileRepository.findByProfileId(appUser.getId());
            if (shipperProfile == null) {
                //Create shop profile
                shopProfileRepository.insertShopProfile(appUser.getId(), getOTPRequest.getUsername(), null);
                shopProfile = shopProfileRepository.findByAppUserId(appUser.getId());
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
        return new VerifyOTPResponse(500, errorCodes.get(500).getMessage(), null);
    }
}
