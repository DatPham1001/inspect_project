package com.imedia.service.device;

import com.imedia.oracle.entity.AppUser;
import com.imedia.oracle.entity.AppUserDevice;
import com.imedia.oracle.repository.AppUserDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserDeviceService {
    private final AppUserDeviceRepository appUserDeviceRepository;

    @Autowired
    public UserDeviceService(AppUserDeviceRepository appUserDeviceRepository) {
        this.appUserDeviceRepository = appUserDeviceRepository;
    }

    public void saveUserDevice(String deviceToken, AppUser appUser) {
        List<AppUserDevice> appUserDevices = appUserDeviceRepository.getExistByTokenAndAppUser(appUser.getId(), deviceToken);
        if (appUserDevices != null && appUserDevices.size() == 0) {
            AppUserDevice appUserDevice = new AppUserDevice();
            appUserDevice.setAppUserId(BigDecimal.valueOf(appUser.getId()));
            appUserDevice.setDeviceToken(deviceToken.trim());
            appUserDevice.setShop(BigDecimal.ZERO);
            appUserDevice.setEnabled(BigDecimal.ZERO);
            appUserDevice.setConfirm("OTP");
            appUserDeviceRepository.insertDevice(appUserDevice.getAppUserId(), appUserDevice.getConfirm(),
                    appUserDevice.getDeviceToken());
        }
    }
}
