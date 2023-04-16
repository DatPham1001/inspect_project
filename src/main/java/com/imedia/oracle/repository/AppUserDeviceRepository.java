package com.imedia.oracle.repository;

import com.imedia.oracle.entity.AppUserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AppUserDeviceRepository extends JpaRepository<AppUserDevice, Long> {

//    AppUserDevice findByDeviceTokenAndAppUserIdAndEnabled(String deviceToken, BigDecimal appUserId, BigDecimal enabled);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO APP_USER_DEVICES (app_user_id, confirm, device_token, enabled, ship, shop) \n" +
            "values (:appUserId,:confirm,:deviceToken,0,0,0)", nativeQuery = true)
    void insertDevice(@Param("appUserId") BigDecimal appUserId,
                      @Param("confirm") String confirm,
                      @Param("deviceToken") String deviceToken);

    @Query(value = "SELECT * FROM APP_USER_DEVICES WHERE DEVICE_TOKEN = :deviceToken AND APP_USER_ID =  :appUserId" +
            " AND SHOP = 1", nativeQuery = true)
    List<AppUserDevice> getActiveByTokenAndAppUser(@Param("appUserId") Long appUserId,
                                                   @Param("deviceToken") String deviceToken);
    @Query(value = "SELECT * FROM APP_USER_DEVICES WHERE DEVICE_TOKEN = :deviceToken AND APP_USER_ID =  :appUserId", nativeQuery = true)
    List<AppUserDevice> getExistByTokenAndAppUser(@Param("appUserId") Long appUserId,
                                                   @Param("deviceToken") String deviceToken);
    @Modifying
    @Transactional
    @Query(value = "UPDATE APP_USER_DEVICES SET ENABLED = 1\n" +
            "WHERE DEVICE_TOKEN = :deviceToken AND APP_USER_ID =:appUserId ", nativeQuery = true)
    void enableDeviceRepo(@Param("appUserId") Long appUserId, @Param("deviceToken") String deviceToken);

    @Modifying
    @Transactional
    @Query(value = "UPDATE APP_USER_DEVICES SET ENABLED = 0 \n" +
            "WHERE DEVICE_TOKEN = :deviceToken ", nativeQuery = true)
    void disableOtherDeviceRepo(@Param("deviceToken") String deviceToken);

    default void enableDevice(Long appUserId, String deviceToken) {
        disableOtherDeviceRepo(deviceToken);
        enableDeviceRepo(appUserId, deviceToken);
    }
}
