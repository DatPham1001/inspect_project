package com.imedia.service.pickupaddress;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.config.application.AppConfig;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.dao.PickupAddressDAO;
import com.imedia.oracle.dao.ShopProfileDAO;
import com.imedia.oracle.entity.AppUser;
import com.imedia.oracle.entity.ErrorCodesWebshop;
import com.imedia.oracle.entity.ShopAddress;
import com.imedia.oracle.repository.AppUserRepository;
import com.imedia.oracle.repository.ShopAddressRepository;
import com.imedia.oracle.repository.ShopProfileRepository;
import com.imedia.service.pickupaddress.model.CreatePickupAddressRequest;
import com.imedia.service.pickupaddress.model.FilterPickupAddressResponse;
import com.imedia.service.pickupaddress.model.GeometryData;
import com.imedia.service.pickupaddress.model.ShopAddressDTO;
import com.imedia.service.user.model.UserInfoAddressData;
import com.imedia.service.user.model.UserInfoResponse;
import com.imedia.util.CallRedis;
import com.imedia.util.CallServer;
import com.imedia.util.PreLoadStaticUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@Service
public class PickupAddressService {
    static final Logger logger = LogManager.getLogger(PickupAddressService.class);
    static final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    private static final HashMap<Integer, ErrorCodesWebshop> errorCodes = PreLoadStaticUtil.errorCodeWeb;
    private final ShopProfileRepository shopProfileRepository;
    private final AppUserRepository appUserRepository;
    private final ShopAddressRepository shopAddressRepository;
    private final ShopProfileDAO shopProfileDAO;
    private final MapperFacade mapperFacade;
    private final PickupAddressDAO pickupAddressDAO;

    @Autowired
    public PickupAddressService(ShopProfileRepository shopProfileRepository, AppUserRepository appUserRepository, ShopAddressRepository shopAddressRepository, ShopProfileDAO shopProfileDAO, MapperFacade mapperFacade, PickupAddressDAO pickupAddressDAO) {
        this.shopProfileRepository = shopProfileRepository;
        this.appUserRepository = appUserRepository;
        this.shopAddressRepository = shopAddressRepository;
        this.shopProfileDAO = shopProfileDAO;
        this.mapperFacade = mapperFacade;
        this.pickupAddressDAO = pickupAddressDAO;
    }

    public BaseResponse createShopAddress(CreatePickupAddressRequest pickupAddressRequest,String username) throws Exception {
        AppUser appUser = appUserRepository.findByPhone(username);
        UserInfoAddressData addressData = PreLoadStaticUtil.addresses.get(pickupAddressRequest.getWardCode());
        if (addressData == null)
            return new BaseResponse(505);
//        ShopProfile shopProfile = shopProfileRepository.findByAppUserId(appUser.getId());
        String fullAddress = pickupAddressRequest.getAddressDetail() + "," + addressData.getWardName() + "," + addressData.getDistrictName()
                + "," + addressData.getProvinceName();
        ShopAddress shopAddress = new ShopAddress();
        //TODO fix this ID
        shopAddress.setShopId(appUser.getId());
        shopAddress.setSenderName(pickupAddressRequest.getSenderName());
        shopAddress.setName(pickupAddressRequest.getShopName());
        shopAddress.setPhone(pickupAddressRequest.getShopPhone());
        shopAddress.setStatus(1);
        shopAddress.setAddressId(0);
        //Shop address
        shopAddress.setAddress(pickupAddressRequest.getAddressDetail());
        String addressAlias = StringUtils.stripAccents(fullAddress)
                .toLowerCase()
                .replaceAll("đ", "d")
                .replaceAll(" ", "")
                .replaceAll(",", "");
        shopAddress.setAddressAlias(addressAlias);
        shopAddress.setProvinceCode(pickupAddressRequest.getProvinceCode());
        shopAddress.setDistrictId(Integer.valueOf(pickupAddressRequest.getDistrictCode()));
        shopAddress.setWardId(Integer.valueOf(pickupAddressRequest.getWardCode()));
        //Get long lat
        GeometryData geometryData = getGeometry(fullAddress);
        if (geometryData != null) {
            shopAddress.setLongitude(geometryData.getLng());
            shopAddress.setLatitude(geometryData.getLat());
        }
        //Check default address
        List<ShopAddress> allShopAddress = shopAddressRepository.findAllByShopId(appUser.getId());
        if (pickupAddressRequest.getIsDefault() == 1) {
            //Exist default address and request is default = 1 =>  update all existed address default to 0
            if (allShopAddress.size() > 0) {
                for (ShopAddress defaultAddress : allShopAddress) {
                    if (defaultAddress.getIsDefault() == null || defaultAddress.getIsDefault() != 0) {
                        defaultAddress.setIsDefault(0);
                        shopAddressRepository.save(defaultAddress);
                    }
                }
            }
            shopAddress.setIsDefault(1);
        } else {
            //First address is default
            if (allShopAddress == null || allShopAddress.size() == 0)
                shopAddress.setIsDefault(1);
            else shopAddress.setIsDefault(0);
        }
        shopAddressRepository.save(shopAddress);
        //TODO update cache
        handleCachePickupAddress(pickupAddressRequest.getUsername(), appUser.getId());
        ShopAddressDTO shopAddressDTO = mapperFacade.map(shopAddress, ShopAddressDTO.class);
        shopAddressDTO.setProvinceName(addressData.getProvinceName());
        shopAddressDTO.setDistrictName(addressData.getDistrictName());
        shopAddressDTO.setWardName(addressData.getWardName());
        return new BaseResponse(200, shopAddressDTO);
    }

    public BaseResponse updatePickupAddress(CreatePickupAddressRequest createPickupAddressRequest, Long shopAddressId, String username) throws Exception {
        AppUser appUser = appUserRepository.findByPhone(username);
        ShopAddress shopAddress = shopAddressRepository.findShopAddressByIdAndShopId(shopAddressId, appUser.getId());
        if (shopAddress == null)
            return new BaseResponse(506);
        UserInfoAddressData addressData = PreLoadStaticUtil.addresses.get(createPickupAddressRequest.getWardCode());
        if (addressData == null)
            return new BaseResponse(505);
        String fullAddress = createPickupAddressRequest.getAddressDetail() + "," + addressData.getWardName() + "," + addressData.getDistrictName()
                + "," + addressData.getProvinceName();
        Integer lastDefault = shopAddress.getIsDefault();
        shopAddress.setName(createPickupAddressRequest.getShopName());
        shopAddress.setPhone(createPickupAddressRequest.getShopPhone());
        shopAddress.setStatus(createPickupAddressRequest.getStatus());
        shopAddress.setAddressId(0);
        shopAddress.setSenderName(createPickupAddressRequest.getSenderName());
        shopAddress.setAddress(createPickupAddressRequest.getAddressDetail());
        String addressAlias = StringUtils.stripAccents(fullAddress)
                .toLowerCase()
                .replaceAll("đ", "d")
                .replaceAll(" ", "")
                .replaceAll(",", "");
        shopAddress.setAddressAlias(addressAlias);
        shopAddress.setIsDefault(createPickupAddressRequest.getIsDefault());
        shopAddress.setProvinceCode(createPickupAddressRequest.getProvinceCode());
        shopAddress.setDistrictId(Integer.valueOf(createPickupAddressRequest.getDistrictCode()));
        shopAddress.setWardId(Integer.valueOf(createPickupAddressRequest.getWardCode()));
        //Get long lat
        GeometryData geometryData = getGeometry(fullAddress);
        if (geometryData != null) {
            shopAddress.setLongitude(geometryData.getLng());
            shopAddress.setLatitude(geometryData.getLat());
        }
        //Check default
        if (createPickupAddressRequest.getIsDefault() == 1) {
            List<ShopAddress> defaultAddress =
                    shopAddressRepository.findAllByShopIdAndIsDefaultAndIdIsNotLike(shopAddress.getShopId(), 1, shopAddressId);
            if (defaultAddress.size() > 0) {
                for (ShopAddress defaultAddress1 : defaultAddress) {
                    defaultAddress1.setIsDefault(0);
                    shopAddressRepository.save(defaultAddress1);
                }
            }
        }
        //Cập nhật kho chính thành kho phụ thì tìm kho mới nhất thành kho chính
        if (createPickupAddressRequest.getIsDefault() == 0 && lastDefault == 1) {
            //Get last shopAddress
            ShopAddress lastShopAddress = shopAddressRepository.findTopByShopIdOrderByCreateDateDesc(shopAddress.getShopId());
            lastShopAddress.setIsDefault(1);
            shopAddressRepository.save(lastShopAddress);
        }
        ShopAddress result = shopAddressRepository.save(shopAddress);
        ShopAddressDTO shopAddressDTO = mapperFacade.map(result, ShopAddressDTO.class);
        shopAddressDTO.setProvinceName(addressData.getProvinceName());
        shopAddressDTO.setDistrictName(addressData.getDistrictName());
        shopAddressDTO.setWardName(addressData.getWardName());
        return new BaseResponse(200, shopAddressDTO);
    }

    public BaseResponse filterPickupAddress(String username, String key, Integer status, Integer page, Integer size) {
        try {
            AppUser appUser = appUserRepository.findByPhone(username);
            if (page == null || page <= 1) page = 0;
            else page = page - 1;
            if (size == null || size <= 0) size = 10;
            List<ShopAddressDTO> shopAddresses = pickupAddressDAO.filterPickupAddress(key, status, appUser.getId(), page, size);
            //Get address
            Integer total = shopAddressRepository.countFilter(key, status, appUser.getId());
            handleCachePickupAddress(username, appUser.getId());
            return new BaseResponse(200, new FilterPickupAddressResponse(total, page + 1, size, shopAddresses));
        } catch (Exception e) {
            logger.info("======FILTER SHOP ADDRESSES EXCEPTION======" + username, e);
            BaseResponse baseResponse = new BaseResponse(500);
            return baseResponse;
        }
    }

    public void handleCachePickupAddress(String username, Long appUserId) {
        //TODO update cache
        UserInfoResponse userInfoResponse = gson.fromJson(CallRedis.getCache(username), UserInfoResponse.class);
        List<ShopAddressDTO> shopAddresses = pickupAddressDAO.getAllShopAddress(appUserId);
        userInfoResponse.setPickupAddress(shopAddresses);
        CallRedis.updateKey(username, gson.toJson(userInfoResponse));
    }

    public BaseResponse deactivateShopAddressId(Long shopAddressId) {
        try {
            shopAddressRepository.deactivateShopAddress(shopAddressId);
            return new BaseResponse(200);
        } catch (Exception e) {
            return new BaseResponse(500);
        }
    }

    public GeometryData getGeometry(String fullAddress) {
        try {
            String url = AppConfig.getInstance().geoUrl + "?address=" + fullAddress.replaceAll(" ", "%20");
            String response = CallServer.getInstance().get(url);
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("code") == 0) {
                        if (jsonObject.has("data")) {
                            JSONObject data = jsonObject.getJSONArray("data").getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location");
                            BigDecimal lng = data.getBigDecimal("lng");
                            BigDecimal lat = data.getBigDecimal("lat");
                            return new GeometryData(lng.doubleValue(), lat.doubleValue());
                        }
                    }
                } catch (Exception e) {
                    logger.info("=======MAPBOX API EXCEPTION======", e);
                }
            }
        } catch (Exception e) {
            logger.info("=======MAPBOX API CONFIG EXCEPTION======", e);
        }
        return null;
    }

    private boolean validateAddress(CreatePickupAddressRequest pickupAddressRequest) {
        try {
            UserInfoAddressData addressData = PreLoadStaticUtil.addresses.get(pickupAddressRequest.getWardCode());
            if (addressData == null)
                return false;

            String addressInfo = CallRedis.getCache("ADDRESS_INFO");
            JSONObject address = new JSONObject(addressInfo);
            if (address.has(pickupAddressRequest.getProvinceCode())) {
                JSONObject province = address.getJSONObject(pickupAddressRequest.getProvinceCode());
                if (province.has(pickupAddressRequest.getDistrictCode())) {
                    JSONObject district = province.getJSONObject(pickupAddressRequest.getDistrictCode());
                    if (district.has(pickupAddressRequest.getWardCode())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.info("=======VALIDATE ADDRESS EXCEPTION======= " + gson.toJson(pickupAddressRequest), e);
        }
        return false;
    }

}
