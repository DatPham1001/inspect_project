package com.imedia.oracle.reportrepository;

import com.imedia.oracle.reportentity.SvcOrderDetailReport;
import com.imedia.service.order.dto.ImageOM;
import com.imedia.service.order.dto.ShopAddressOM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SvcOrderDetailReportRepository extends JpaRepository<SvcOrderDetailReport, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM SVC_ORDER_DETAILS WHERE SVC_ORDER_DETAIL_CODE = :code", nativeQuery = true)
    void deleteDetailByCode(@Param("code") BigDecimal code);

    SvcOrderDetailReport findBySvcOrderDetailCodeAndShopId(BigDecimal code, Long appUserId);

    SvcOrderDetailReport findBySvcOrderDetailCode(BigDecimal code);

    @Query(value = "SELECT dl.ID,dl.PATH_TOPIC as path,ds.ID as imgId,ds.FILE_NAME as fileName FROM ORDER_DETAILS d " +
            " LEFT JOIN DOCUMENT_LINKS dl ON dl.ID = d.DOCUMENT_LINKS_ID" +
            " LEFT JOIN DOCUMENT_STORAGES ds ON ds.DOCUMENT_LINK_ID = dl.ID" +
            " WHERE d.ORDER_DETAIL_CODE = :code " +
            " ORDER BY ds.UTIMESTAMP ", nativeQuery = true)
    List<ImageOM> getImages(@Param("code") BigDecimal code);


    @Query(value = "SELECT dl.ID,dl.PATH_TOPIC as path,ds.ID as imgId,ds.FILE_NAME as fileName \n" +
            "FROM DOCUMENT_LINKS dl\n" +
            "LEFT JOIN DOCUMENT_STORAGES ds ON ds.DOCUMENT_LINK_ID = dl.ID\n" +
            "WHERE dl.ID = :linkId", nativeQuery = true)
    List<ImageOM> getImagesOfTracking(@Param("linkId") Long linkId);
//    List<ImageOM> getImagesOfTracking(@Param("linkId") Long linkId);

    @Query(value = "SELECT ID,PHONE,NAME FROM SHOP_ADDRESS sa \n" +
            "WHERE ID IN (SELECT DISTINCT(SHOP_ADDRESS_ID) \n" +
            "FROM SVC_ORDER_DETAILS \n" +
            "WHERE SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE = :phone))", nativeQuery = true)
    List<ShopAddressOM> getUsedShopAddress(@Param("phone") String phone);
}
