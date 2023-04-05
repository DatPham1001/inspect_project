package com.imedia.util;

import com.google.gson.Gson;
import com.imedia.model.ErrorCode;
import com.imedia.oracle.dao.AddressDAO;
import com.imedia.oracle.entity.*;
import com.imedia.oracle.repository.*;
import com.imedia.service.pickupaddress.model.LocationData;
import com.imedia.service.user.model.UserInfoAddressData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PreLoadStaticUtil implements CommandLineRunner {
    private final ErrorCodeWebshopRepository errorCodeWebshopRepository;
    public static HashMap<Integer, ErrorCodesWebshop> errorCodeWeb = new HashMap<>();
    public static HashMap<String, UserInfoAddressData> addresses = new HashMap<>();
    public static HashMap<String, LocationData> provinceCache = new HashMap<>();
    public static HashMap<String, List<LocationData>> districtCache = new HashMap<>();
    public static HashMap<String, List<LocationData>> wardCache = new HashMap<>();
    //    public static List<Status> statuses = new ArrayList<>();
    private final WalletRepository walletRepository;
    private final AppUserRepository appUserRepository;
    private final WalletLogRepository walletLogRepository;
    private final StatusRepository statusRepository;
    private final WardRepository wardRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    static final Gson gson = new Gson();
    private final BankRepository bankRepository;
    private final AddressDAO addressDAO;
    static final Logger logger = LogManager.getLogger(PreLoadStaticUtil.class);

    @Autowired
    public PreLoadStaticUtil(ErrorCodeWebshopRepository errorCodeWebshopRepository, WalletRepository walletRepository, AppUserRepository appUserRepository, WalletLogRepository walletLogRepository, StatusRepository statusRepository, WardRepository wardRepository, ProvinceRepository provinceRepository, DistrictRepository districtRepository, BankRepository bankRepository, AddressDAO addressDAO) {
        this.errorCodeWebshopRepository = errorCodeWebshopRepository;
        this.walletRepository = walletRepository;
        this.appUserRepository = appUserRepository;
        this.walletLogRepository = walletLogRepository;
        this.statusRepository = statusRepository;
        this.wardRepository = wardRepository;
        this.provinceRepository = provinceRepository;
        this.districtRepository = districtRepository;
        this.bankRepository = bankRepository;
        this.addressDAO = addressDAO;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            loadErrorCodes();
            loadAddress();
            cacheStatus();
            cacheAddress();
            cacheBank();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Khởi động lỗi", e);
            System.exit(0);
        }
    }

    private void syncWallet() {
        List<Wallet> wallets = walletRepository.findAll();
        for (Wallet wallet : wallets) {
            AppUser appUser = appUserRepository.findAppUserById(wallet.getUserId().longValue());
            if (appUser != null) {
                appUser.setAccountEpurseId(wallet.getAccountEpurseId());
                appUserRepository.save(appUser);
            }
        }
        List<WalletLog> walletLogs = walletLogRepository.getW();
        for (WalletLog walletLog : walletLogs) {
            String bankAccount = walletLog.getReason().replace("Cộng tiền vào tài khoản VA", "")
                    .replace("Rút tiền vào tài khoản ngân hàng ", "").replaceAll(" ", "");
            walletLog.setBankAccount(bankAccount);
            walletLogRepository.save(walletLog);
        }
    }


    private void cacheStatus() {
        List<Status> statuses = statusRepository.findAll();
        Map<String, Object> cacheStatus = new HashMap<>();
        for (Status status : statuses) {
            cacheStatus.put(String.valueOf(status.getCode()), status);
        }
        CallRedis.setCache("ORDER_STATUS", gson.toJson(cacheStatus));
    }

    private void loadAddress() {
        List<UserInfoAddressData> addressData = addressDAO.getCacheAddress();
        for (UserInfoAddressData userInfoAddressData : addressData) {
            addresses.put(userInfoAddressData.getWardCode(), userInfoAddressData);
        }
    }

    private void cacheAddress() {
        //Create province
        List<Province> provinceCodes = provinceRepository.findAll();
        Map<String, Object> province = new HashMap<>();
        for (Province provinceCode : provinceCodes) {
            Map<String, Object> provinceData = new HashMap<>();
            provinceData.put("name", provinceCode.getName());
            provinceData.put("code", provinceCode.getCode());
            //Cache
            provinceCache.put(provinceCode.getCode(), new LocationData(provinceCode.getCode(), provinceCode.getName()));
            //Get district
            List<District> districts = districtRepository.getDistrict(provinceCode.getCode().trim());
            List<LocationData> districtCaches = new ArrayList<>();
            for (District districtCode : districts) {
                Map<String, Object> district = new HashMap<>();
                Map<String, Object> districtData = new HashMap<>();
                district.put("code", districtCode.getCode());
                district.put("name", districtCode.getName());
                //Cache
                districtCaches.add(new LocationData(districtCode.getCode(), districtCode.getName()));
                //Get wards
                List<Ward> wards = wardRepository.findAllByDistrictCode(districtCode.getCode().trim());
                List<LocationData> wardCaches = new ArrayList<>();
                for (Ward wardCode : wards) {
                    Map<String, Object> wardData = new HashMap<>();
                    wardData.put("name", wardCode.getName());
                    wardData.put("code", wardCode.getCode());
                    district.put(wardCode.getCode(), wardData);
                    //cache
                    wardCaches.add(new LocationData(wardCode.getCode(), wardCode.getName()));
                }
                wardCache.put(districtCode.getCode(), wardCaches);
                provinceData.put(districtCode.getCode(), district);
            }
            districtCache.put(provinceCode.getCode(), districtCaches);
            province.put(provinceCode.getCode(), provinceData);
            String res = gson.toJson(province);
//            System.out.println(res);
        }
        CallRedis.setCache("ADDRESS_INFO", gson.toJson(province));
    }

    private void cacheBank() {
        List<Bank> banks = bankRepository.findAll();
        HashMap<String, Object> bankCache = new HashMap<>();
        for (Bank bank : banks) {
            HashMap<String, String> bankCacheData = new HashMap<>();
            bankCacheData.put("bankCode", bank.getBankCode());
            bankCacheData.put("bankName", bank.getName());
            bankCacheData.put("shortName", bank.getCode());
            bankCache.put(bank.getBankCode(), bankCacheData);
        }
        CallRedis.setCache("BANK_INFO", gson.toJson(bankCache));
    }

    private void loadErrorCodes() throws Exception {
//        StringBuilder str = new StringBuilder();
//        List<ErrorCodesWebshop> errorCodesWebshops = errorCodeWebshopRepository.findAllByType(2);
        for (ErrorCode value : ErrorCode.values()) {
            errorCodeWeb.put(value.code, new ErrorCodesWebshop(value.code,value.message));
        }
//        for (ErrorCodesWebshop errorCodesWebshop : errorCodesWebshops) {
//            errorCodeWeb.put(errorCodesWebshop.getErrorCode(), errorCodesWebshop);
//            str.append("E").append(errorCodesWebshop.getErrorCode()).append("(")
//                    .append(errorCodesWebshop.getErrorCode())
//                    .append(",")
//                    .append("\"").append(errorCodesWebshop.getMessage()).append("\"),\n");
//        }
//        System.out.println(str.toString());
    }


}
