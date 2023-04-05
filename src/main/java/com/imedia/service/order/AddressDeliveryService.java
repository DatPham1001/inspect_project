package com.imedia.service.order;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imedia.config.application.AppConfig;
import com.imedia.model.BaseResponse;
import com.imedia.oracle.entity.AddressDelivery;
import com.imedia.oracle.repository.AddressDeliveryRepository;
import com.imedia.service.order.model.AddressDeliveryRequest;
import com.imedia.service.pickupaddress.PickupAddressService;
import com.imedia.service.pickupaddress.model.GeometryData;
import com.imedia.service.pickupaddress.model.LocationData;
import com.imedia.util.PreLoadStaticUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class AddressDeliveryService {
    static final Logger logger = LogManager.getLogger(AddressDeliveryService.class);
    static final Gson gson = new Gson();
    static final Gson gson2 = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private final AddressDeliveryRepository addressDeliveryRepository;
    private final PickupAddressService pickupAddressService;

    @Autowired
    public AddressDeliveryService(AddressDeliveryRepository addressDeliveryRepository, PickupAddressService pickupAddressService) {
        this.addressDeliveryRepository = addressDeliveryRepository;
        this.pickupAddressService = pickupAddressService;
    }

    public BaseResponse createAddressDelivery(AddressDeliveryRequest addressDeliveryRequest) throws Exception {
        AppConfig appConfig = AppConfig.getInstance();
        //Get long last
        String fullAddress = addressDeliveryRequest.getAddress() + "," + addressDeliveryRequest.getWardName() + "," + addressDeliveryRequest.getDistrictName()
                + "," + addressDeliveryRequest.getProvinceName();
        String allias = StringUtils.stripAccents(fullAddress.replaceAll(" ", "")
                .replaceAll(",", "").toLowerCase().replaceAll("đ", "d"));
        //Check exist
        AddressDelivery oldAddress = addressDeliveryRepository.stringFilterAddress(allias);
        if (oldAddress != null) {
            if (oldAddress.getLongitude() == null
                    && oldAddress.getLatitude() == null) {
                GeometryData geometryData = pickupAddressService.getGeometry(fullAddress);
                if (geometryData != null) {
                    oldAddress.setLongitude(geometryData.getLng());
                    oldAddress.setLatitude(geometryData.getLat());
                    addressDeliveryRepository.save(oldAddress);
                }
            }
            return new BaseResponse(200, oldAddress);
        }
        //Save long last
        AddressDelivery addressDelivery = new AddressDelivery();
        addressDelivery.setProvinceCode(addressDeliveryRequest.getProvinceCode());
        addressDelivery.setDistrictCode(addressDeliveryRequest.getDistrictCode());
        addressDelivery.setWardCode(addressDeliveryRequest.getWardCode());
        addressDelivery.setAddress(addressDeliveryRequest.getAddress());
        addressDelivery.setProvinceName(addressDeliveryRequest.getProvinceName());
        addressDelivery.setDistrictName(addressDeliveryRequest.getDistrictName());
        addressDelivery.setWardName(addressDeliveryRequest.getWardName());
        addressDelivery.setStringFilter(allias);
        GeometryData geometryData = pickupAddressService.getGeometry(fullAddress);
        if (geometryData != null) {
            addressDelivery.setLongitude(geometryData.getLng());
            addressDelivery.setLatitude(geometryData.getLat());
        }
        AddressDelivery result = addressDeliveryRepository.save(addressDelivery);
        return new BaseResponse(200, result);
    }

    public BaseResponse getAddressCode(String code, String filterString) {
        if (code == null || code.isEmpty()) {
            List<LocationData> provinces = new ArrayList<>(PreLoadStaticUtil.provinceCache.values());
            provinces = filterAddressName(filterString, provinces);
            return new BaseResponse(200, provinces);
        }
        //Province code
        if (code.matches("[A-Z]*")) {
            LocationData province = PreLoadStaticUtil.provinceCache.get(code);
            List<LocationData> districts = PreLoadStaticUtil.districtCache.get(province.getCode());
            districts = filterAddressName(filterString, districts);
            return new BaseResponse(200,districts);
        }
        //district code
        if (code.matches("[0-9]*")) {
            List<LocationData> districts = PreLoadStaticUtil.wardCache.get(code);
            districts = filterAddressName(filterString, districts);
            return new BaseResponse(200,districts);
        }
        return null;
    }

    private List<LocationData> filterAddressName(String filterString, List<LocationData> addresses) {
        List<LocationData> filterAddress = new ArrayList<>();
        for (LocationData address: addresses) {
            if (removeAccent(address.getName())
                    .contains(removeAccent(filterString))) {
                filterAddress.add(address);
            }
        }

        return filterAddress;
    }

    private String removeAccent(String str) {
        String removedAccentString = StringUtils.stripAccents(str)
                .toLowerCase()
                .replaceAll("đ", "d")
                .replaceAll(" ", "");
        return removedAccentString;
    }
}
