package com.imedia.oracle.repository;

import com.imedia.oracle.entity.ShopAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ShopAddressRepository extends JpaRepository<ShopAddress, Long> {

    ShopAddress findShopAddressByIdAndShopId(Long id, Long shopId);
    ShopAddress findShopAddressById(Long id);

    List<ShopAddress> findAllByShopIdAndIsDefaultAndIdIsNotLike(Long shopId, Integer isDefault, Long id);

    List<ShopAddress> findAllByShopIdAndStatus(Long shopId, Integer status);

    List<ShopAddress> findAllByShopId(Long shopId);

    @Query(value = "SELECT * FROM SHOP_ADDRESS where SHOP_ID = :shopid \n" +
            "AND (LOWER(NAME) like LOWER(CONCAT(CONCAT('%', :keyword ), '%')) \n" +
            "OR LOWER(PHONE)like LOWER(CONCAT(CONCAT('%', :keyword), '%'))) \n" +
            "AND STATUS  = :status \n" +
            "OFFSET :page ROWS FETCH NEXT :size ROWS ONLY"
            , nativeQuery = true)
    List<ShopAddress> filterShopAddress(@Param("keyword") String keyword, @Param("status") Integer status,
                                        @Param("shopid") BigDecimal shopid, @Param("page") Integer page,
                                        @Param("size") Integer size);

    @Query(value = "SELECT COUNT(ID) FROM SHOP_ADDRESS where SHOP_ID = :shopid \n" +
            "AND (LOWER(NAME) like LOWER(CONCAT(CONCAT('%', :keyword ), '%')) \n" +
            "OR LOWER(PHONE)like LOWER(CONCAT(CONCAT('%', :keyword), '%'))) \n" +
            "AND STATUS  = :status \n"
            , nativeQuery = true)
    Integer countFilter(@Param("keyword") String keyword, @Param("status") Integer status,
                        @Param("shopid") Long shopid);

    @Transactional
    @Modifying
    @Query(value = "UPDATE SHOP_ADDRESS SET STATUS = 0 WHERE ID = :id", nativeQuery = true)
    void deactivateShopAddress(@Param("id") Long id);

    ShopAddress findTopByShopIdOrderByCreateDateDesc(Long shopId);

    @Query(value = " SELECT sa.* FROM SHOP_ADDRESS sa " +
            " LEFT JOIN APP_USERS au ON au.ID = sa.SHOP_ID" +
            " WHERE sa.ID = :id " +
            " AND au.PHONE = :phone ", nativeQuery = true)
    ShopAddress getShopAddress(@Param("id") int id,
                               @Param("phone") String phone);
}
