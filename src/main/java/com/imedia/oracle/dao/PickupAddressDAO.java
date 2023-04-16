package com.imedia.oracle.dao;

import com.imedia.service.pickupaddress.model.ShopAddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Service
public class PickupAddressDAO {
    private final EntityManagerFactory entityManagerFactory;
    @PersistenceContext(unitName = "primaryEntityManagerFactory")
    private final EntityManager entityManager;

    @Autowired
    public PickupAddressDAO(EntityManagerFactory entityManagerFactory, @Qualifier("primaryEntityManagerFactory") EntityManager entityManager) {
        this.entityManagerFactory = entityManagerFactory;
        this.entityManager = entityManager;
    }

    public List<ShopAddressDTO> filterPickupAddress(String keyword, Integer status, Long shopid,
                                                    Integer page, Integer size) {
        StringBuilder stringBuilder = new StringBuilder(
                "SELECT sa.ID,sa.SHOP_ID,sa.NAME,sa.PHONE,sa.ADDRESS,sa.SENDER_NAME,sa.PROVINCE_CODE,sa.DISTRICT_ID,sa.WARD_ID,p.NAME as provinceName,d.NAME as districtName,w.NAME as wardName,sa.STATUS,sa.IS_DEFAULT \n" +
                        "FROM SHOP_ADDRESS sa \n" +
                        "LEFT JOIN PROVINCES p on sa.PROVINCE_CODE = p.CODE \n" +
                        "LEFT JOIN DISTRICTS d on sa.DISTRICT_ID = d.CODE \n" +
                        "LEFT JOIN WARDS w on sa.WARD_ID = w.CODE ");
        stringBuilder.append(" where sa.SHOP_ID = ").append(shopid);
        stringBuilder.append(" AND (LOWER(sa.NAME) like LOWER(CONCAT(CONCAT('%', '").append(keyword).append("'), '%'))");
        stringBuilder.append(" OR LOWER(sa.PHONE)like LOWER(CONCAT(CONCAT('%','").append(keyword).append("' ), '%')))");
        if (status != null)
            stringBuilder.append(" AND sa.STATUS  = ").append(status);
        stringBuilder.append(" ORDER BY sa.IS_DEFAULT DESC,sa.UTIMESTAMP DESC");
        stringBuilder.append(" OFFSET ");
        stringBuilder.append(page * size);
        stringBuilder.append(" ROWS FETCH NEXT ");
        stringBuilder.append(size);
        stringBuilder.append(" ROWS ONLY");
        Query query = entityManager.createNativeQuery(stringBuilder.toString(), "PickupAddressMapping");
        return query.getResultList();
    }

    public List<ShopAddressDTO> getAllShopAddress(Long shopId) {
        String sql = "SELECT sa.ID,sa.SHOP_ID,sa.NAME,sa.PHONE,sa.ADDRESS,sa.SENDER_NAME,sa.PROVINCE_CODE,sa.DISTRICT_ID,sa.WARD_ID,p.NAME as provinceName,d.NAME as districtName,w.NAME as wardName,sa.STATUS,sa.IS_DEFAULT\n" +
                "FROM SHOP_ADDRESS sa\n" +
                "LEFT JOIN PROVINCES p on sa.PROVINCE_CODE = p.CODE\n" +
                "LEFT JOIN DISTRICTS d on sa.DISTRICT_ID = d.CODE\n" +
                "LEFT JOIN WARDS w on sa.WARD_ID = w.CODE\n" +
                "where sa.SHOP_ID = " + shopId +
                " ORDER BY sa.IS_DEFAULT DESC,sa.UTIMESTAMP DESC";
        Query query = entityManager.createNativeQuery(sql, "PickupAddressMapping");
        return query.getResultList();
    }
}
