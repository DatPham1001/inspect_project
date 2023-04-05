package com.imedia.config.application;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.*;

public class AppConfig {
    public String epurse_key;
    public String epurse_service_code;
    public String epurse_url;
    public String epurse_admin_url;
    public String imageUrl;
    public String notify_ship_url;
    public String firmbank_url;
    public String firmbank_partnercode;
    public String firmbank_thuho_privateKey;
    public String firmbank_thuho_publicKey;
    public String addressUrl;

    public String mainStackCode;
    public String rewardPointStackCode;
    public Boolean autoWithdraw;
    public Boolean otpStatus;
    public BigDecimal bankFee;
    public BigDecimal maxWithdraw;
    public BigDecimal minWithdraw;
    public Integer bankFeeType;
    public Integer maxOtpPerDay;
    public Integer checkPending;
    public Integer rememberMeCache;

    public String geoUrl;
    public String calculateFeeUrl;
    public String calculateSpecificFeeUrl;
    public String findShipUrl;
    public List<String> changeCodStatus = new ArrayList<>();
    public List<String> changeInfoStatus = new ArrayList<>();
    public List<String> changePackageStatus = new ArrayList<>();
    public List<String> reDeliveryStatus = new ArrayList<>();
    public List<String> refundRequestStatus = new ArrayList<>();
    public List<String> cancelStatus = new ArrayList<>();
    public HashMap<Integer, String> targetStatuses = new HashMap<>();
    //    public List<String> targetStatuses2 = new ArrayList<>();
    public String partialRequestWait;
    private static AppConfig instance;


    public static void setInstance(AppConfig instance) {
        AppConfig.instance = instance;
    }

    public static AppConfig getInstance() throws Exception {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }

    public AppConfig() throws Exception {
        Properties properties = new Properties();
        String fileName = RootConfig.getInstance().getFile("app_config.cfg");
        FileInputStream fileStream = new FileInputStream(fileName);
        properties.load(fileStream);
        fileStream.close();
        this.epurse_key = properties.getProperty("epurse_key");
        this.epurse_service_code = properties.getProperty("epurse_service_code");
        this.epurse_url = properties.getProperty("epurse_url");
        this.imageUrl = properties.getProperty("imageUrl");
        this.notify_ship_url = properties.getProperty("notify_ship_url");
        this.epurse_admin_url = properties.getProperty("epurse_admin_url");
        this.firmbank_url = properties.getProperty("firmbank_url");
        this.firmbank_partnercode = properties.getProperty("firmbank_partnercode");
        this.firmbank_thuho_privateKey = properties.getProperty("firmbank_thuho_privateKey");
        this.firmbank_thuho_publicKey = properties.getProperty("firmbank_thuho_publicKey");
        this.mainStackCode = properties.getProperty("mainStackCode");
        this.rewardPointStackCode = properties.getProperty("rewardPointStackCode");
        this.autoWithdraw = Boolean.valueOf(properties.getProperty("autoWithdraw"));
        this.otpStatus = Boolean.valueOf(properties.getProperty("otpStatus"));
        this.bankFee = BigDecimal.valueOf(Long.parseLong(properties.getProperty("bankFee")));
        this.maxWithdraw = BigDecimal.valueOf(Long.parseLong(properties.getProperty("maxWithdraw")));
        this.minWithdraw = BigDecimal.valueOf(Long.parseLong(properties.getProperty("minWithdraw")));
        this.bankFeeType = Integer.valueOf(properties.getProperty("bankFeeType"));
        this.maxOtpPerDay = Integer.valueOf(properties.getProperty("maxOtpPerDay"));
        this.geoUrl = properties.getProperty("geoUrl");
        this.addressUrl = properties.getProperty("addressUrl");
        this.calculateFeeUrl = properties.getProperty("calculateFeeUrl") + "fee";
        this.calculateSpecificFeeUrl = properties.getProperty("calculateFeeUrl") + "cal-fee";
        this.findShipUrl = properties.getProperty("calculateFeeUrl") + "find-shipper";
        this.checkPending = Integer.valueOf(properties.getProperty("checkPending"));
        this.rememberMeCache = Integer.valueOf(properties.getProperty("rememberMeCache"));
        this.partialRequestWait = properties.getProperty("partialRequestWait");
        targetStatuses.put(900, properties.getProperty("900"));
        targetStatuses.put(100, properties.getProperty("100"));
        targetStatuses.put(102, properties.getProperty("102"));
        targetStatuses.put(200, properties.getProperty("200"));
        targetStatuses.put(504, properties.getProperty("504"));
        targetStatuses.put(500, properties.getProperty("500"));
        targetStatuses.put(511, properties.getProperty("511"));
        targetStatuses.put(505, properties.getProperty("505"));
        targetStatuses.put(501, properties.getProperty("501"));
        targetStatuses.put(502, properties.getProperty("502"));
        targetStatuses.put(901, properties.getProperty("901"));
        targetStatuses.put(2006, properties.getProperty("2006"));
        targetStatuses.put(905, properties.getProperty("905"));
        targetStatuses.put(924, properties.getProperty("924"));
        targetStatuses.put(923, properties.getProperty("923"));
        targetStatuses.put(999, properties.getProperty("999"));

        this.changeCodStatus = Arrays.asList(properties.getProperty("changeCodStatus").split(","));
        this.changeInfoStatus = Arrays.asList(properties.getProperty("changeInfoStatus").split(","));
        this.changePackageStatus = Arrays.asList(properties.getProperty("changePackageStatus").split(","));
        this.reDeliveryStatus = Arrays.asList(properties.getProperty("reDeliveryStatus").split(","));
        this.refundRequestStatus = Arrays.asList(properties.getProperty("refundRequestStatus").split(","));
        this.cancelStatus = Arrays.asList(properties.getProperty("cancelStatus").split(","));


        /*
         * end Key file
         */
    }
}
