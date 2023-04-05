package com.imedia.oracle.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.math.BigDecimal;

@Service
public class ShopProfileDAO {
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;

    @Autowired
    public ShopProfileDAO(EntityManagerFactory entityManagerFactory, @Qualifier("primaryEntityManagerFactory") EntityManager entityManager) {
        this.entityManagerFactory = entityManagerFactory;
        this.entityManager = entityManager;
    }

    public BigDecimal getShopProfileIDFromAppPhone(String username) throws Exception {
        String sql = "SELECT sp.ID FROM SHOP_PROFILES sp \n" +
                "LEFT JOIN APP_USERS au on sp.APP_USER_ID = au.ID\n" +
                "Where au.PHONE = '" + username + "'";
        Query query = entityManager.createNativeQuery(sql);
        BigDecimal id = (BigDecimal) query.getSingleResult();
        return id;
    }

    public String getPhoneOTPFromUsername(String username) throws Exception {
        String sql = "SELECT sp.PHONE_OTP FROM SHOP_PROFILES sp \n" +
                "LEFT JOIN APP_USERS au on sp.APP_USER_ID = au.ID\n" +
                "Where au.PHONE = '" + username + "'";
        Query query = entityManager.createNativeQuery(sql);
        Object result = query.getSingleResult();
        if (result != null)
            return String.valueOf(result);
        return null;
    }
}
