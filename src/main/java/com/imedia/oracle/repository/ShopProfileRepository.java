package com.imedia.oracle.repository;

import com.imedia.oracle.entity.ShopProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ShopProfileRepository extends JpaRepository<ShopProfile, Long> {
    ShopProfile findByAppUserId(Long appUserID);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO SHOP_PROFILES (app_user_id, PHONE_OTP, EMAIL_BILL, STATUS, CREATE_AT, UTIMESTAMP) \n" +
            "values (:appUserId,:phoneOtp,:email,1,sysdate,sysdate)", nativeQuery = true)
    void insertShopProfile(@Param("appUserId") Long appUserId,
                           @Param("phoneOtp") String phoneOtp,
                           @Param("email") String email);

    @Modifying
    @Transactional
    @Query(value = "UPDATE SHOP_PROFILES SET PHONE_OTP = :phone WHERE APP_USER_ID = :id", nativeQuery = true)
    void updatePhoneOtp(@Param("phone") String phone,
                        @Param("id") Long appUserId);


}
