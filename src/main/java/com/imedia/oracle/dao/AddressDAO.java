package com.imedia.oracle.dao;

import com.imedia.service.user.model.UserInfoAddressData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Service
public class AddressDAO {
    //    @Qualifier("primaryEntityManagerFactory")
//    @PersistenceContext(unitName = "primaryDataSource")
    private final EntityManager entityManager;

    @Autowired
    public AddressDAO(@Qualifier("primaryEntityManagerFactory") EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public UserInfoAddressData getAddressData(Long appUserId) throws Exception {
        String sql = "SELECT p.NAME as provinceName,p.CODE as provinceCode,\n" +
                "d.NAME as districtName,d.CODE as districtCode,w.NAME as wardName,w.CODE as wardCode,s.ADDRESS\n" +
                "FROM \"SHOP_PROFILES\" s\n" +
                "left join PROVINCES p on s.PROVINCE_CODE = p.CODE\n" +
                "left join DISTRICTS d on s.DISTRICT_CODE = d.CODE\n" +
                "left join WARDS w on s.WARD_CODE = w.CODE\n" +
                "where s.APP_USER_ID = " + appUserId;
        Query query = entityManager.createNativeQuery(sql, "UserInfoAddressDataMapping");
        List<UserInfoAddressData> userInfoAddressData = query.getResultList();
        return userInfoAddressData.get(0);
    }

    public List<UserInfoAddressData> getCacheAddress() {
        String sql = "SELECT p.NAME as PROVINCE_NAME,p.CODE as PROVINCE_CODE,d.NAME AS DISTRICT_NAME,\n" +
                " d.CODE as DISTRICT_CODE,w.NAME as WARD_NAME,w.CODE as WARD_CODE FROM WARDS w\n" +
                " LEFT JOIN DISTRICTS d ON d.CODE = w.DISTRICT_CODE\n" +
                " LEFT JOIN PROVINCES p ON p.CODe = w.PROVINCE_CODE";
        Query query = entityManager.createNativeQuery(sql, "CacheAddressMapping");
        List<UserInfoAddressData> userInfoAddressData = query.getResultList();
        return userInfoAddressData;
    }

}
