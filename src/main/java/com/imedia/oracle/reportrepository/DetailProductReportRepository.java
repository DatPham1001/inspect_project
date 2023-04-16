package com.imedia.oracle.reportrepository;

import com.imedia.oracle.reportentity.DetailProductReport;
import com.imedia.service.product.model.ProductReportOM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DetailProductReportRepository extends JpaRepository<DetailProductReport, Long> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM DETAIL_PRODUCT WHERE ORDER_DETAIL_CODE = :code", nativeQuery = true)
    void deleteProductByDetailCode(@Param("code") BigDecimal code);

    @Query(value = " SELECT p.Id,p.COD,p.NAME,p.PRODUCT_CATE_ID as productCateId,p.ORDER_DETAIL_CODE as orderDetailCode," +
            "p.QUANTITY,p.VALUE,c.NAME as CATEGORYNAME FROM DETAIL_PRODUCT p \n" +
            " LEFT JOIN PRODUCT_CATEGORYS c ON c.ID = p.PRODUCT_CATE_ID\n" +
            " WHERE ORDER_DETAIL_CODE\n" +
            " LIKE CONCAT(CONCAT('%', :code), '%')", nativeQuery = true)
    List<ProductReportOM> getProductsByOrderCode(@Param("code") String code);
//    @Query(value = " SELECT * FROM DETAIL_PRODUCT WHERE ORDER_DETAIL_CODE LIKE CONCAT(CONCAT('%', :code), '%')", nativeQuery = true)
//    List<DetailProductReport> findAllByOrderDetailCode(BigDecimal detailCode);
}
