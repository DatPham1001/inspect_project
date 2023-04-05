package com.imedia.oracle.dao;

import com.imedia.service.order.dto.ConfirmOrderDTO;
import com.imedia.service.order.dto.ConsultOrderDTO;
import com.imedia.service.order.dto.CountTotalOM;
import com.imedia.service.order.dto.FilterOrderDTO;
import com.imedia.service.order.model.ConsultOrderRequest;
import com.imedia.service.order.model.FilterOrderRequest;
import com.imedia.service.order.model.PrintOrderRequest;
import com.imedia.service.order.model.SumOrderStatusDTO;
import com.imedia.service.postage.model.ForWardConfigOM;
import com.imedia.service.postage.model.PostageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderDAO {
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManagerReport;
    private final EntityManager entityManager;

    @Autowired
    public OrderDAO(EntityManagerFactory entityManagerFactory,
                    @Qualifier("secondaryEntityManagerFactory") EntityManager entityManagerReport,
                    @Qualifier("primaryEntityManagerFactory") EntityManager entityManager) {
        this.entityManagerFactory = entityManagerFactory;
        this.entityManagerReport = entityManagerReport;
        this.entityManager = entityManager;
    }

    public List<PostageData> getSpPostage() {
        String sql = "SELECT p.CODE,p.NAME,p.STATUS,ps.TYPE,p.TIME\n" +
                "FROM \n" +
                "(SELECT * FROM V2_PACKAGES \n" +
                "WHERE STATUS = 1) p \n" +
                "LEFT JOIN V2_PACKAGE_PRICE pp ON pp.PACKAGE_ID = p.ID\n" +
                "LEFT JOIN V2_PRICE_SETTING ps ON pp.PRICE_SETTING_ID = ps.ID\n" +
                "WHERE ps.TYPE = 1 " +
                " GROUP BY p.CODE,p.NAME,p.STATUS,ps.TYPE,p.TIME";
        Query query = entityManagerReport.createNativeQuery(sql, "SpPostageDataMapping");
        return query.getResultList();
    }

    public List<PostageData> getKmPostage() {
        String sql = "SELECT a1.*,pb.TYPE as BASE_TYPE,pb.MAX FROM \n" +
                "(SELECT p.CODE,p.NAME,p.STATUS,ps.TYPE,p.TIME,ps.ID as SETTING_ID\n" +
                "FROM\n" +
                "(SELECT * FROM V2_PACKAGES \n" +
                "WHERE STATUS = 1) p \n" +
                "LEFT JOIN V2_PACKAGE_PRICE pp ON pp.PACKAGE_ID = p.ID\n" +
                "LEFT JOIN V2_PRICE_SETTING ps ON pp.PRICE_SETTING_ID = ps.ID\n" +
                "WHERE ps.TYPE = 2\n" +
                "GROUP BY p.CODE,p.NAME,p.STATUS,ps.TYPE,p.TIME,ps.ID) a1 \n" +
                "LEFT JOIN V2_PRICE_BASE pb ON pb.PRICE_SETTING_ID = a1.SETTING_ID\n" +
                "WHERE pb.TYPE IN (3,4) ";
        Query query = entityManagerReport.createNativeQuery(sql, "KmPostageDataMapping");
        return query.getResultList();
    }

    public List<FilterOrderDTO> filterOrder(FilterOrderRequest filterOrderRequest, String username) {
        StringBuilder str = new StringBuilder("" +
                "SELECT d.ID,d.SVC_ORDER_DETAIL_CODE,d.SHOP_ID,d.SVC_ORDER_ID,d.ADDRESS_DELIVERY_ID,d.SERVICE_PACK_ID,d.SERVICE_PACK_SETTING_ID" +
                ",d.CONSIGNEE,d.PHONE,d.WEIGHT,d.LENGTH,d.WIDTH,d.HEIGHT,d.EXPECT_PICK_DATE,d.EXPECT_DELIVER_DATE,d.IS_PART_DELIVERY,\n" +
                "d.IS_REFUND,d.IS_PORTER,d.IS_DOOR_DELIVERY,d.IS_DECLARE_PRODUCT,d.REQUIRED_NOTE,d.NOTE,d.IS_FREE,\n" +
                "d.STATUS,d.UTIMESTAMP,d.CREATE_AT,d.OLD_STATUS,d.CARRIER_ORDER_ID,d.SHOP_ORDER_ID,d.SHOP_ADDRESS_ID,d.CARRIER_ID,\n" +
                "d.CARRIER_SERVICE_CODE,d.ID_ACCOUNT_CARRIER,d.PICK_TYPE,d.PAYMENT_TYPE,d.REALITY_COD,d.EXPECT_COD," +
                "f.NAME as FEE_NAME,f.CODE as FEE_CODE,f.VALUE as FEE_VALUE,o.TYPE,o.TOTAL_ADDRESS_DILIVERY,o.TOTAL_DISTANCE,o.TOTAL_ORDER_DETAIL,\n" +
                "o.EXPECT_SHIP_ID,o.PICKUP_TYPE,o.STATUS as ORDER_STATUS,p.NAME as PACK_NAME,p.CODE as PACK_CODE," +
                " sa.ADDRESS as SHOP_ADDRESS,sa.NAME as SHOP_NAME,sa.PHONE as SHOP_PHONE,sa.PROVINCE_CODE as SHOP_PROVINCE_CODE," +
                "sa.DISTRICT_ID as SHOP_DISTRICT_CODE,\n" +
                "sa.WARD_ID as SHOP_WARD_CODE,ad.ADDRESS as ADDRESS_DELIVERY," +
                " ad.PROVINCE_NAME as DELIVERY_PROVINCE,ad.DISTRICT_NAME as DELIVERY_DISTRICT,ad.WARD_NAME as DELIVERY_WARD," +
                "ad.PROVINCE_CODE,ad.DISTRICT_CODE,ad.WARD_CODE,au.PHONE as SHIPPER_PHONE,au.NAME as SHIPPER_NAME" +
                " FROM (SELECT * FROM SVC_ORDER_DETAILS  WHERE SHOP_ID = ( SELECT ID FROM APP_USERS WHERE PHONE = '" + username + "' ) " +
                " AND ( IS_DELETED IS NULL OR IS_DELETED = 0 ))d \n");
        str.append(" LEFT JOIN SVC_ORDERS o ON o.ORDER_CODE = d.SVC_ORDER_ID\n");
        str.append(" LEFT JOIN SHOP_ADDRESS sa  ON o.SHOP_ADDRESS_ID = sa.ID\n");
        str.append(" LEFT JOIN V2_PACKAGES p ON o.SERVICE_PACK_ID = p.ID\n");
        str.append(" LEFT JOIN DETAIL_SELLING_FEE f ON f.ORDER_DETAIL_CODE = d.SVC_ORDER_DETAIL_CODE\n");
        str.append(" LEFT JOIN ADDRESS_DELIVERY ad ON ad.ID = d.ADDRESS_DELIVERY_ID\n");
        str.append(" LEFT JOIN APP_USERS au ON d.ID_ACCOUNT_CARRIER = au.ID\n");
        str.append(" WHERE d.SVC_ORDER_ID IN (SELECT DISTINCT( o.ORDER_CODE ) FROM (SELECT * FROM SVC_ORDERS " +
                " WHERE SHOP_ID = ( SELECT ID FROM APP_USERS WHERE PHONE = '" + username + "' ) \n" +
                " AND ( IS_DELETED IS NULL OR IS_DELETED = 0 ) ) o" +
                " LEFT JOIN SVC_ORDER_DETAILS d ON d.SVC_ORDER_ID = o.ORDER_CODE\n");
        str.append(" WHERE ( LOWER(d.SVC_ORDER_DETAIL_CODE) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(d.PHONE) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(d.SHOP_ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(d.CARRIER_ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(d.CONSIGNEE) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%')))");
        if (filterOrderRequest.getPackCode() != null && !filterOrderRequest.getPackCode().isEmpty()) {
            str.append(" AND o.SERVICE_PACK_ID = (SELECT ID FROM V2_PACKAGES WHERE CODE = '")
                    .append(filterOrderRequest.getPackCode())
                    .append("')");
        }
        if (filterOrderRequest.getShopAddressId() != null) {
            str.append(" AND o.SHOP_ADDRESS_ID = ");
            str.append(filterOrderRequest.getShopAddressId());
        }
        if (filterOrderRequest.getOrderStatus() != null && filterOrderRequest.getOrderStatus() != 0
                && filterOrderRequest.getTargetStatuses() != null && !filterOrderRequest.getTargetStatuses().isEmpty()) {
            str.append(" AND d.STATUS IN ( ")
                    .append(filterOrderRequest.getTargetStatuses())
                    .append(" ) ");
        }

        if (filterOrderRequest.getOrderBy() == 1)
            str.append(" ORDER BY o.ID DESC,o.CREATE_AT DESC");
        else str.append(" ORDER BY o.ID DESC,o.UTIMESTAMP DESC");
        str.append(" OFFSET ");
        str.append(filterOrderRequest.getPage() * filterOrderRequest.getSize());
        str.append(" ROWS FETCH NEXT ");
        str.append(filterOrderRequest.getSize());
        str.append(" ROWS ONLY ) ");
        if (filterOrderRequest.getOrderBy() == 1) {
            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
                str.append(" AND d.CREATE_AT > TO_DATE('")
                        .append(filterOrderRequest.getFromDate())
                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
            }
            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
                str.append(" AND d.CREATE_AT < TO_DATE('")
                        .append(filterOrderRequest.getToDate())
                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
            }
            str.append(" ORDER BY o.ID DESC,o.CREATE_AT DESC");
        } else {
            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
                str.append(" AND d.UTIMESTAMP > TO_DATE('")
                        .append(filterOrderRequest.getFromDate())
                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
            }
            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
                str.append(" AND d.UTIMESTAMP < TO_DATE('")
                        .append(filterOrderRequest.getToDate())
                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
            }
            str.append(" ORDER BY o.ID DESC,o.UTIMESTAMP DESC");
        }
        Query query = entityManagerReport.createNativeQuery(str.toString(), "FilterOrderMapping");
//        query.setFirstResult(filterOrderRequest.getPage() * filterOrderRequest.getSize());
//        query.setMaxResults(filterOrderRequest.getSize());
        List<FilterOrderDTO> result = query.getResultList();

        return result;
    }

    public CountTotalOM countFilterOrder(FilterOrderRequest filterOrderRequest, String username) {
        StringBuilder str = new StringBuilder("SELECT\n" +
                " COUNT( DISTINCT SVC_ORDER_ID ) AS TOTAL,\n" +
                " SUM( VALUE ) AS TOTAL_FEE,\n" +
                " COUNT( DISTINCT SVC_ORDER_DETAIL_CODE ) AS TOTAL_ORDER_DETAIL," +
                " SUM( REALITY_COD ) TOTAL_COD " +
                " FROM SVC_ORDER_DETAILS d \n" +
                " LEFT JOIN (SELECT SUM(VALUE) as VALUE,ORDER_DETAIL_CODE FROM DETAIL_SELLING_FEE GROUP BY ORDER_DETAIL_CODE) f " +
                " ON f.ORDER_DETAIL_CODE = d.SVC_ORDER_DETAIL_CODE\n" +
                " LEFT JOIN V2_PACKAGES p ON p.Id = d.SERVICE_PACK_ID\n");
        if (filterOrderRequest.getTargetStatuses() != null && filterOrderRequest.getTargetStatuses().equals("999"))
            str.append("  INNER JOIN ORDER_PARTIAL_REQUEST op ON op.ORDER_DETAIL_CODE = d.SVC_ORDER_DETAIL_CODE AND op.IS_CONFIRMED = 0");
        str.append(" WHERE d.SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE ='")
                .append(username)
                .append("')")
                .append(" AND (d.IS_DELETED IS NULL OR d.IS_DELETED = 0)\n");
        if (filterOrderRequest.getOrderStatus() != null && filterOrderRequest.getOrderStatus() != 0
                && filterOrderRequest.getTargetStatuses() != null && !filterOrderRequest.getTargetStatuses().isEmpty()) {
//            if (filterOrderRequest.getOrderStatus().equals(OrderStatusEnum.WAIT_TO_CONFIRM.code)) {
//                str.append(" AND STATUS IN ( ")
//                        .append(filterOrderRequest.getOrderStatus())
//                        .append(OrderStatusEnum.FINDING_SHIP.code)
//                        .append(") ");
//            } else {
            if (!filterOrderRequest.getTargetStatuses().equals("999"))
                str.append(" AND d.STATUS IN ( ")
                        .append(filterOrderRequest.getTargetStatuses())
                        .append(") ");
//            }
        }
        str.append(" AND ( LOWER(d.SVC_ORDER_DETAIL_CODE) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(d.PHONE) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(d.SHOP_ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(d.CARRIER_ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(d.CONSIGNEE) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%')))");
        if (filterOrderRequest.getPackCode() != null && !filterOrderRequest.getPackCode().isEmpty()) {
            str.append(" AND p.CODE = '")
                    .append(filterOrderRequest.getPackCode())
                    .append("'");
        }
        if (filterOrderRequest.getShopAddressId() != null) {
            str.append(" AND d.SHOP_ADDRESS_ID = ");
            str.append(filterOrderRequest.getShopAddressId());
        }
        if (filterOrderRequest.getOrderBy() == 1) {
            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
                str.append(" AND d.CREATE_AT > TO_DATE('")
                        .append(filterOrderRequest.getFromDate())
                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
            }
            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
                str.append(" AND d.CREATE_AT < TO_DATE('")
                        .append(filterOrderRequest.getToDate())
                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
            }

            str.append(" ORDER BY d.ID DESC,d.CREATE_AT DESC");
        } else {
            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
                str.append(" AND d.UTIMESTAMP > TO_DATE('")
                        .append(filterOrderRequest.getFromDate())
                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
            }
            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
                str.append(" AND d.UTIMESTAMP < TO_DATE('")
                        .append(filterOrderRequest.getToDate())
                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
            }
            str.append(" ORDER BY d.ID DESC,d.UTIMESTAMP DESC");
        }
        Query query = entityManagerReport.createNativeQuery(str.toString(), "CountTotalMapping");
//        query.setFirstResult(filterOrderRequest.getPage() * filterOrderRequest.getSize());
//        query.setMaxResults(filterOrderRequest.getSize());
        CountTotalOM result = (CountTotalOM) query.getSingleResult();

        return result;
    }

//    public BigDecimal countTotalCod(FilterOrderRequest filterOrderRequest, String username) {
//        StringBuilder str = new StringBuilder("SELECT SUM(dp.COD * dp.QUANTITY) as TOTAL" +
//                " FROM SVC_ORDER_DETAILS d \n" +
//                " LEFT JOIN SVC_ORDERS o ON o.ORDER_CODE = d.SVC_ORDER_ID\n" +
//                " LEFT JOIN SHOP_ADDRESS sa  ON o.SHOP_ADDRESS_ID = sa.ID\n" +
//                " LEFT JOIN V2_PACKAGES p  ON o.SERVICE_PACK_ID = p.ID\n" +
//                " LEFT JOIN DETAIL_PRODUCT dp ON dp.ORDER_DETAIL_CODE = d.SVC_ORDER_DETAIL_CODE\n" +
//                " LEFT JOIN ADDRESS_DELIVERY ad ON ad.ID = d.ADDRESS_DELIVERY_ID\n" +
//                " WHERE d.SVC_ORDER_ID IN " +
//                " (SELECT ORDER_CODE FROM SVC_ORDERS\n" +
//                " WHERE SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE ='" + username + "')\n");
//        if (filterOrderRequest.getOrderStatus() != null && filterOrderRequest.getOrderStatus() != 0
//                && filterOrderRequest.getTargetStatuses() != null && !filterOrderRequest.getTargetStatuses().isEmpty()) {
////            if (filterOrderRequest.getOrderStatus().equals(OrderStatusEnum.WAIT_TO_CONFIRM.code)) {
////                str.append(" AND STATUS IN ( ")
////                        .append(filterOrderRequest.getOrderStatus())
////                        .append(OrderStatusEnum.FINDING_SHIP.code)
////                        .append(") ");
////            } else {
//            str.append(" AND STATUS IN ( ")
//                    .append(filterOrderRequest.getTargetStatuses())
//                    .append(") ");
////            }
//        }
//
//        if (filterOrderRequest.getOrderBy() == 1)
//            str.append(" ORDER BY CREATE_AT DESC");
//        else str.append(" ORDER BY UTIMESTAMP DESC");
//        str.append(" OFFSET 0 ROWS FETCH NEXT 10000000 ROWS ONLY)");
//        str.append(" AND ( LOWER(d.SVC_ORDER_DETAIL_CODE) like LOWER(CONCAT(CONCAT('%', '")
//                .append(filterOrderRequest.getSearchKey())
//                .append("'), '%'))");
//        str.append(" OR LOWER(d.PHONE) like LOWER(CONCAT(CONCAT('%', '")
//                .append(filterOrderRequest.getSearchKey())
//                .append("'), '%'))");
//        str.append(" OR LOWER(d.CARRIER_ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
//                .append(filterOrderRequest.getSearchKey())
//                .append("'), '%'))");
//        str.append(" OR LOWER(d.CONSIGNEE) like LOWER(CONCAT(CONCAT('%', '")
//                .append(filterOrderRequest.getSearchKey())
//                .append("'), '%')))");
//        if (filterOrderRequest.getOrderBy() == 1) {
//            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
//                str.append(" AND d.CREATE_AT > TO_DATE('")
//                        .append(filterOrderRequest.getFromDate())
//                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
//            }
//            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
//                str.append(" AND d.CREATE_AT < TO_DATE('")
//                        .append(filterOrderRequest.getToDate())
//                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
//            }
//        } else {
//            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
//                str.append(" AND d.UTIMESTAMP > TO_DATE('")
//                        .append(filterOrderRequest.getFromDate())
//                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
//            }
//            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
//                str.append(" AND d.UTIMESTAMP < TO_DATE('")
//                        .append(filterOrderRequest.getToDate())
//                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
//            }
//        }
//        if (filterOrderRequest.getOrderBy() == 1)
//            str.append(" ORDER BY o.CREATE_AT DESC");
//        else str.append(" ORDER BY o.UTIMESTAMP DESC");
//        Query query = entityManagerReport.createNativeQuery(str.toString());
//        return (BigDecimal) query.getSingleResult();
//    }

    public List<SumOrderStatusDTO> sumOrderStatus(FilterOrderRequest filterOrderRequest, String username) {
        StringBuilder str = new StringBuilder("SELECT d.STATUS,COUNT(d.STATUS) as COUNT\n" +
                "FROM SVC_ORDER_DETAILS d \n" +
                " LEFT JOIN ADDRESS_DELIVERY ad ON ad.ID = d.ADDRESS_DELIVERY_ID " +
                " LEFT JOIN V2_PACKAGES p ON p.ID = d.SERVICE_PACK_ID \n" +
                " WHERE SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE ='" + username + "')");
        str.append(" AND ( LOWER(d.SVC_ORDER_DETAIL_CODE) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(d.PHONE) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(d.CARRIER_ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(d.SHOP_ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(d.CONSIGNEE) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))) AND (d.IS_DELETED IS NULL OR d.IS_DELETED = 0)");
        if (filterOrderRequest.getPackCode() != null && !filterOrderRequest.getPackCode().isEmpty()) {
            str.append(" AND p.CODE = '")
                    .append(filterOrderRequest.getPackCode())
                    .append("'");
        }
        if (filterOrderRequest.getShopAddressId() != null) {
            str.append(" AND d.SHOP_ADDRESS_ID = ");
            str.append(filterOrderRequest.getShopAddressId());
        }
        if (filterOrderRequest.getOrderBy() == 1) {
            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
                str.append(" AND d.CREATE_AT > TO_DATE('")
                        .append(filterOrderRequest.getFromDate())
                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
            }
            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
                str.append(" AND d.CREATE_AT < TO_DATE('")
                        .append(filterOrderRequest.getToDate())
                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
            }
        } else {
            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
                str.append(" AND d.UTIMESTAMP > TO_DATE('")
                        .append(filterOrderRequest.getFromDate())
                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
            }
            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
                str.append(" AND d.UTIMESTAMP < TO_DATE('")
                        .append(filterOrderRequest.getToDate())
                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
            }
        }
        str.append(" GROUP BY d.STATUS");
        Query query = entityManagerReport.createNativeQuery(str.toString(), "SumOrderStatusMapping");
        List<SumOrderStatusDTO> result = query.getResultList();
        return result;
    }

    public List<SumOrderStatusDTO> sumOrderStatus2(FilterOrderRequest filterOrderRequest, String username) {
        StringBuilder str = new StringBuilder("SELECT 999 as STATUS,COUNT(d.SVC_ORDER_DETAIL_CODE) as COUNT\n" +
                "FROM SVC_ORDER_DETAILS d \n" +
                " LEFT JOIN ADDRESS_DELIVERY ad ON ad.ID = d.ADDRESS_DELIVERY_ID " +
                " LEFT JOIN V2_PACKAGES p ON p.ID = d.SERVICE_PACK_ID \n" +
                " LEFT JOIN V2_PACKAGES p ON p.ID = d.SERVICE_PACK_ID \n" +
                " INNER JOIN ORDER_PARTIAL_REQUEST op ON op.ORDER_DETAIL_CODE = d.SVC_ORDER_DETAIL_CODE " +
                " WHERE op.IS_CONFIRMED = 0" +
                " AND SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE ='" + username + "')");
        str.append(" AND ( LOWER(d.SVC_ORDER_DETAIL_CODE) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(d.PHONE) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(d.CARRIER_ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(d.SHOP_ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(d.CONSIGNEE) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))) AND (d.IS_DELETED IS NULL OR d.IS_DELETED = 0)");
        if (filterOrderRequest.getPackCode() != null && !filterOrderRequest.getPackCode().isEmpty()) {
            str.append(" AND p.CODE = '")
                    .append(filterOrderRequest.getPackCode())
                    .append("'");
        }
        if (filterOrderRequest.getShopAddressId() != null) {
            str.append(" AND d.SHOP_ADDRESS_ID = ");
            str.append(filterOrderRequest.getShopAddressId());
        }
        if (filterOrderRequest.getOrderBy() == 1) {
            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
                str.append(" AND d.CREATE_AT > TO_DATE('")
                        .append(filterOrderRequest.getFromDate())
                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
            }
            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
                str.append(" AND d.CREATE_AT < TO_DATE('")
                        .append(filterOrderRequest.getToDate())
                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
            }
        } else {
            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
                str.append(" AND d.UTIMESTAMP > TO_DATE('")
                        .append(filterOrderRequest.getFromDate())
                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
            }
            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
                str.append(" AND d.UTIMESTAMP < TO_DATE('")
                        .append(filterOrderRequest.getToDate())
                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
            }
        }
        Query query = entityManagerReport.createNativeQuery(str.toString(), "SumOrderStatusMapping");
        List<SumOrderStatusDTO> result = query.getResultList();
        return result;
    }

    public List<FilterOrderDTO> filterOrderDetail(FilterOrderRequest filterOrderRequest, String username) {
        StringBuilder str = new StringBuilder(
                "SELECT d.ID,d.SVC_ORDER_DETAIL_CODE,d.SHOP_ID,d.SVC_ORDER_ID,d.ADDRESS_DELIVERY_ID,d.SERVICE_PACK_ID,d.SERVICE_PACK_SETTING_ID," +
                        "d.CONSIGNEE,d.PHONE,d.WEIGHT,d.LENGTH,d.WIDTH,d.HEIGHT,d.EXPECT_PICK_DATE,d.EXPECT_DELIVER_DATE,d.IS_PART_DELIVERY,\n" +
                        "d.IS_REFUND,d.IS_PORTER,d.IS_DOOR_DELIVERY,d.IS_DECLARE_PRODUCT,d.REQUIRED_NOTE,d.NOTE,d.IS_FREE,\n" +
                        "d.STATUS,d.UTIMESTAMP,d.CREATE_AT,d.OLD_STATUS,d.CARRIER_ORDER_ID,d.SHOP_ORDER_ID,d.SHOP_ADDRESS_ID,d.CARRIER_ID,\n" +
                        "d.CARRIER_SERVICE_CODE,d.ID_ACCOUNT_CARRIER,d.PICK_TYPE,d.PAYMENT_TYPE,d.REALITY_COD,d.EXPECT_COD," +
                        "f.NAME as FEE_NAME,f.CODE as FEE_CODE,f.VALUE as FEE_VALUE,o.TYPE,o.TOTAL_ADDRESS_DILIVERY," +
                        "o.TOTAL_DISTANCE,o.TOTAL_ORDER_DETAIL,o.EXPECT_SHIP_ID,o.PICKUP_TYPE,o.STATUS as ORDER_STATUS," +
                        "p.NAME as PACK_NAME,p.CODE as PACK_CODE, sa.ADDRESS as SHOP_ADDRESS,sa.NAME as SHOP_NAME," +
                        "sa.PHONE as SHOP_PHONE,sa.PROVINCE_CODE as SHOP_PROVINCE_CODE,sa.DISTRICT_ID as SHOP_DISTRICT_CODE,\n" +
                        "sa.WARD_ID as SHOP_WARD_CODE," +
                        "ad.ADDRESS as ADDRESS_DELIVERY, ad.PROVINCE_NAME as DELIVERY_PROVINCE," +
                        "ad.DISTRICT_NAME as DELIVERY_DISTRICT,ad.WARD_NAME as DELIVERY_WARD," +
                        "ad.PROVINCE_CODE,ad.DISTRICT_CODE,ad.WARD_CODE,au.PHONE as SHIPPER_PHONE,au.NAME as SHIPPER_NAME \n" +
                        "FROM (SELECT d.*  FROM SVC_ORDER_DETAILS d LEFT JOIN V2_PACKAGES p ON d.SERVICE_PACK_ID = p.ID \n" +
                        " WHERE SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE ='" + username + "') AND (IS_DELETED IS NULL OR IS_DELETED = 0)");
        if (filterOrderRequest.getOrderStatus() != null && filterOrderRequest.getOrderStatus() != 0
                && filterOrderRequest.getTargetStatuses() != null && !filterOrderRequest.getTargetStatuses().isEmpty()) {
            if (!filterOrderRequest.getTargetStatuses().equals("999"))
                str.append(" AND d.STATUS IN ( ")
                        .append(filterOrderRequest.getTargetStatuses())
                        .append(") ");
        }
        if (!filterOrderRequest.getSearchKey().isEmpty()) {
            str.append(" AND ( LOWER(SVC_ORDER_DETAIL_CODE) like LOWER(CONCAT(CONCAT('%', '")
                    .append(filterOrderRequest.getSearchKey())
                    .append("'), '%'))");
            str.append(" OR LOWER(PHONE) like LOWER(CONCAT(CONCAT('%', '")
                    .append(filterOrderRequest.getSearchKey())
                    .append("'), '%'))");
            str.append(" OR LOWER(SHOP_ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
                    .append(filterOrderRequest.getSearchKey())
                    .append("'), '%'))");
            str.append(" OR LOWER(CARRIER_ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
                    .append(filterOrderRequest.getSearchKey())
                    .append("'), '%'))");
            str.append(" OR LOWER(CONSIGNEE) like LOWER(CONCAT(CONCAT('%', '")
                    .append(filterOrderRequest.getSearchKey())
                    .append("'), '%')))");
        }
        if (filterOrderRequest.getPackCode() != null && !filterOrderRequest.getPackCode().isEmpty()) {
            str.append(" AND d.SERVICE_PACK_ID = (SELECT ID FROM V2_PACKAGES WHERE CODE = '")
                    .append(filterOrderRequest.getPackCode())
                    .append("')");
        }
        if (filterOrderRequest.getShopAddressId() != null) {
            str.append(" AND d.SHOP_ADDRESS_ID = ");
            str.append(filterOrderRequest.getShopAddressId());
        }
        if (filterOrderRequest.getOrderBy() == 1) {
            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
                str.append(" AND d.CREATE_AT > TO_DATE('")
                        .append(filterOrderRequest.getFromDate())
                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
            }
            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
                str.append(" AND d.CREATE_AT < TO_DATE('")
                        .append(filterOrderRequest.getToDate())
                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
            }

            str.append(" ORDER BY d.ID DESC,d.CREATE_AT DESC");
        } else {
            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
                str.append(" AND d.UTIMESTAMP > TO_DATE('")
                        .append(filterOrderRequest.getFromDate())
                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
            }
            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
                str.append(" AND d.UTIMESTAMP < TO_DATE('")
                        .append(filterOrderRequest.getToDate())
                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
            }
            str.append(" ORDER BY d.ID DESC,d.UTIMESTAMP DESC");
        }
        if (filterOrderRequest.getExportExcel() != 1) {
            str.append(" OFFSET ");
            str.append(filterOrderRequest.getPage() * filterOrderRequest.getSize());
            str.append(" ROWS FETCH NEXT ");
            str.append(filterOrderRequest.getSize());
            str.append(" ROWS ONLY");
        }
        str.append(") d ");
        str.append(" LEFT JOIN SVC_ORDERS o ON o.ORDER_CODE = d.SVC_ORDER_ID\n" +
                " LEFT JOIN SHOP_ADDRESS sa  ON o.SHOP_ADDRESS_ID = sa.ID\n" +
                " LEFT JOIN V2_PACKAGES p  ON o.SERVICE_PACK_ID = p.ID\n" +
                " LEFT JOIN DETAIL_SELLING_FEE f ON f.ORDER_DETAIL_CODE = d.SVC_ORDER_DETAIL_CODE\n" +
                " LEFT JOIN ADDRESS_DELIVERY ad ON ad.ID = d.ADDRESS_DELIVERY_ID" +
                " LEFT JOIN APP_USERS au ON d.ID_ACCOUNT_CARRIER = au.ID");
        if (filterOrderRequest.getTargetStatuses() != null && filterOrderRequest.getTargetStatuses().equals("999"))
            str.append("  INNER JOIN ORDER_PARTIAL_REQUEST op ON op.ORDER_DETAIL_CODE = d.SVC_ORDER_DETAIL_CODE AND op.IS_CONFIRMED = 0");


//        if (filterOrderRequest.getOrderBy() == 1) {
//            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
//                str.append(" AND d.CREATE_AT > TO_DATE('")
//                        .append(filterOrderRequest.getFromDate())
//                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
//            }
//            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
//                str.append(" AND d.CREATE_AT < TO_DATE('")
//                        .append(filterOrderRequest.getToDate())
//                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
//            }
//            str.append(" ORDER BY o.CREATE_AT DESC");
//        } else {
//            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
//                str.append(" AND d.UTIMESTAMP > TO_DATE('")
//                        .append(filterOrderRequest.getFromDate())
//                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
//            }
//            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
//                str.append(" AND d.UTIMESTAMP < TO_DATE('")
//                        .append(filterOrderRequest.getToDate())
//                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
//            }
//            str.append(" ORDER BY o.UTIMESTAMP DESC");
//        }
        Query query = entityManagerReport.createNativeQuery(str.toString(), "FilterOrderMapping");
        List<FilterOrderDTO> result = query.getResultList();
        return result;

    }

    public CountTotalOM countFilterOrderDetail(FilterOrderRequest filterOrderRequest, String username) {
        StringBuilder str = new StringBuilder("SELECT COUNT( DISTINCT SVC_ORDER_ID ) as TOTAL,SUM(FEE_VALUE) as TOTAL_FEE" +
                ",COUNT( DISTINCT SVC_ORDER_DETAIL_CODE ) as TOTAL_ORDER_DETAIL" +
                " FROM(SELECT d.* ,f.NAME as FEE_NAME,f.CODE as FEE_CODE,f.VALUE as FEE_VALUE,o.TYPE,o.TOTAL_ADDRESS_DILIVERY," +
                "o.TOTAL_DISTANCE,o.TOTAL_ORDER_DETAIL,o.EXPECT_SHIP_ID,o.PICKUP_TYPE,o.STATUS as ORDER_STATUS," +
                "p.NAME as PACK_NAME,p.CODE as PACK_CODE, sa.ADDRESS as SHOP_ADDRESS,sa.NAME as SHOP_NAME," +
                "sa.PHONE as SHOP_PHONE,ad.ADDRESS as ADDRESS_DELIVERY, ad.PROVINCE_NAME as DELIVERY_PROVINCE," +
                "ad.DISTRICT_NAME as DELIVERY_DISTRICT,ad.WARD_NAME as DELIVERY_WARD," +
                "ad.PROVINCE_CODE,ad.DISTRICT_CODE,ad.WARD_CODE\n" +
                "FROM (SELECT * FROM SVC_ORDER_DETAILS \n" +
                " WHERE SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE ='" + username + "') AND (IS_DELETED IS NULL OR IS_DELETED = 0)");
        if (filterOrderRequest.getOrderStatus() != null && filterOrderRequest.getOrderStatus() != 0
                && filterOrderRequest.getTargetStatuses() != null && !filterOrderRequest.getTargetStatuses().isEmpty()) {
            str.append(" AND STATUS IN ( ")
                    .append(filterOrderRequest.getTargetStatuses())
                    .append(") ");
        }
        str.append(" AND ( LOWER(SVC_ORDER_DETAIL_CODE) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(PHONE) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(CARRIER_ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%'))");
        str.append(" OR LOWER(CONSIGNEE) like LOWER(CONCAT(CONCAT('%', '")
                .append(filterOrderRequest.getSearchKey())
                .append("'), '%')))");
        if (filterOrderRequest.getOrderBy() == 1) {
            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
                str.append(" AND CREATE_AT > TO_DATE('")
                        .append(filterOrderRequest.getFromDate())
                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
            }
            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
                str.append(" AND CREATE_AT < TO_DATE('")
                        .append(filterOrderRequest.getToDate())
                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
            }

        } else {
            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
                str.append(" AND UTIMESTAMP > TO_DATE('")
                        .append(filterOrderRequest.getFromDate())
                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
            }
            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
                str.append(" AND UTIMESTAMP < TO_DATE('")
                        .append(filterOrderRequest.getToDate())
                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
            }
        }
        str.append(") d");
        str.append(" LEFT JOIN SVC_ORDERS o ON o.ORDER_CODE = d.SVC_ORDER_ID\n" +
                " LEFT JOIN SHOP_ADDRESS sa  ON o.SHOP_ADDRESS_ID = sa.ID\n" +
                " LEFT JOIN V2_PACKAGES p  ON o.SERVICE_PACK_ID = p.ID\n" +
                " LEFT JOIN DETAIL_SELLING_FEE f ON f.ORDER_DETAIL_CODE = d.SVC_ORDER_DETAIL_CODE\n" +
                " LEFT JOIN ADDRESS_DELIVERY ad ON ad.ID = d.ADDRESS_DELIVERY_ID");
//        if (filterOrderRequest.getOrderBy() == 1) {
//            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
//                str.append(" AND d.CREATE_AT > TO_DATE('")
//                        .append(filterOrderRequest.getFromDate())
//                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
//            }
//            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
//                str.append(" AND d.CREATE_AT < TO_DATE('")
//                        .append(filterOrderRequest.getToDate())
//                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
//            }
//            str.append(" ORDER BY o.CREATE_AT DESC");
//        } else {
//            if (!filterOrderRequest.getFromDate().isEmpty() && filterOrderRequest.getFromDate() != null) {
//                str.append(" AND d.UTIMESTAMP > TO_DATE('")
//                        .append(filterOrderRequest.getFromDate())
//                        .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
//            }
//            if (!filterOrderRequest.getToDate().isEmpty() && filterOrderRequest.getToDate() != null) {
//                str.append(" AND d.UTIMESTAMP < TO_DATE('")
//                        .append(filterOrderRequest.getToDate())
//                        .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
//            }
//            str.append(" ORDER BY o.UTIMESTAMP DESC");
//        }
        str.append(")");
        Query query = entityManagerReport.createNativeQuery(str.toString(), "CountTotalMapping");
        CountTotalOM result = (CountTotalOM) query.getSingleResult();
        return result;
    }

    public List<FilterOrderDTO> getOrderDetailFeeByCode(String username, BigDecimal orderDetailCode) {
        String str = "" +
                "SELECT d.ID,d.SVC_ORDER_DETAIL_CODE,d.SHOP_ID,d.SVC_ORDER_ID,d.ADDRESS_DELIVERY_ID," +
                "d.SERVICE_PACK_ID,d.SERVICE_PACK_SETTING_ID," +
                "d.CONSIGNEE,d.PHONE,d.WEIGHT,d.LENGTH,d.WIDTH,d.HEIGHT,d.EXPECT_PICK_DATE,d.EXPECT_DELIVER_DATE,d.IS_PART_DELIVERY," +
                " d.IS_REFUND,d.IS_PORTER,d.IS_DOOR_DELIVERY,d.IS_DECLARE_PRODUCT,d.REQUIRED_NOTE,d.NOTE,d.IS_FREE," +
                " d.STATUS,d.UTIMESTAMP,d.CREATE_AT,d.OLD_STATUS,d.CARRIER_ORDER_ID,d.SHOP_ORDER_ID,d.SHOP_ADDRESS_ID,d.CARRIER_ID," +
                " d.CARRIER_SERVICE_CODE,d.ID_ACCOUNT_CARRIER,d.PICK_TYPE,d.PAYMENT_TYPE,d.REALITY_COD,d.EXPECT_COD," +
                "f.NAME as FEE_NAME,f.CODE as FEE_CODE,f.VALUE as FEE_VALUE,o.TYPE,o.TOTAL_ADDRESS_DILIVERY," +
                "o.TOTAL_DISTANCE,o.TOTAL_ORDER_DETAIL,o.EXPECT_SHIP_ID,o.PICKUP_TYPE,o.STATUS as ORDER_STATUS,p.NAME as PACK_NAME," +
                "p.CODE as PACK_CODE, sa.ADDRESS as SHOP_ADDRESS,sa.NAME as SHOP_NAME,sa.PHONE as SHOP_PHONE,sa.PROVINCE_CODE AS SHOP_PROVINCE_CODE," +
                "sa.DISTRICT_ID AS SHOP_DISTRICT_CODE,sa.WARD_ID AS SHOP_WARD_CODE,ad.ADDRESS as ADDRESS_DELIVERY," +
                " ad.PROVINCE_NAME as DELIVERY_PROVINCE,ad.DISTRICT_NAME as DELIVERY_DISTRICT,ad.WARD_NAME as DELIVERY_WARD," +
                "ad.PROVINCE_CODE,ad.DISTRICT_CODE,ad.WARD_CODE,au.PHONE as SHIPPER_PHONE,au.NAME as SHIPPER_NAME \n" +
                "FROM (SELECT * FROM SVC_ORDER_DETAILS \n" +
                "WHERE SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE ='" + username + "') AND (IS_DELETED IS NULL OR IS_DELETED = 0) \n" +
                "AND SVC_ORDER_DETAIL_CODE = " + orderDetailCode + ") d\n" +
                "LEFT JOIN SVC_ORDERS o ON o.ORDER_CODE = d.SVC_ORDER_ID\n" +
                "LEFT JOIN SHOP_ADDRESS sa  ON o.SHOP_ADDRESS_ID = sa.ID\n" +
                "LEFT JOIN V2_PACKAGES p  ON o.SERVICE_PACK_ID = p.ID\n" +
                "LEFT JOIN DETAIL_SELLING_FEE f ON f.ORDER_DETAIL_CODE = d.SVC_ORDER_DETAIL_CODE\n" +
                "LEFT JOIN ADDRESS_DELIVERY ad ON ad.ID = d.ADDRESS_DELIVERY_ID\n" +
                " LEFT JOIN APP_USERS au ON d.ID_ACCOUNT_CARRIER = au.ID";
        Query query = entityManagerReport.createNativeQuery(str, "FilterOrderMapping");
        List<FilterOrderDTO> result = query.getResultList();
        return result;
    }


    public List<ForWardConfigOM> getTargetConfig(String packCode, int priceSettingId, int isPartial,
                                                 int orderPayment, int paymentType) {
        StringBuilder str = new StringBuilder("SELECT fc.ID,fc.CARRIER_CODE as code,fc.CARRIER_PACK_CODE carrierPackCode," +
                "fc.PRICE_SETTING_ID as priceSettingId,c.GATEWAY_URL as url,\n" +
                "c.SPECIAL_SERVICES as specialServices,c.IS_HOLA as isHola\n" +
                " FROM \n" +
                " LEADTIME_FORWARD_CONFIGS fc LEFT JOIN CARRIERS c ON fc.CARRIER_ID = c.ID\n" +
                " WHERE fc.PRICE_SETTING_ID = " + priceSettingId +
                " AND fc.PACK_CODE = " + packCode +
                " AND fc.IS_DELETE = 1 ");
        //Giao 1 phan
        if (isPartial == 1)
            str.append(" AND  SPECIAL_SERVICES LIKE '%1%' ");
        //Thanh toan tien mat
        if (paymentType == 1)
            str.append(" AND SPECIAL_SERVICES LIKE '%3%' ");
        //Nguoi nhan tra phi
        if (orderPayment == 2)
            str.append(" AND SPECIAL_SERVICES LIKE '%2%' ");
        str.append("ORDER BY fc.RANK DESC");
        Query query = entityManager.createNativeQuery(str.toString(), "TargetConfigMapping");
        List<ForWardConfigOM> configIMS = query.getResultList();
        return configIMS;
    }

    public List<ForWardConfigOM> getTargetConfig(int priceSettingId) {
        StringBuilder str = new StringBuilder("SELECT fc.ID,fc.CARRIER_CODE as code,fc.CARRIER_PACK_CODE carrierPackCode," +
                "fc.PRICE_SETTING_ID as priceSettingId,c.GATEWAY_URL as url,\n" +
                "c.SPECIAL_SERVICES as specialServices,c.IS_HOLA as isHola\n" +
                " FROM \n" +
                " LEADTIME_FORWARD_CONFIGS fc LEFT JOIN CARRIERS c ON fc.CARRIER_ID = c.ID\n" +
                " WHERE fc.PRICE_SETTING_ID = " + priceSettingId +
                " AND fc.IS_DELETE = 1" +
                " ORDER BY fc.RANK DESC ");
        Query query = entityManager.createNativeQuery(str.toString(), "TargetConfigMapping");
        List<ForWardConfigOM> configIMS = query.getResultList();
        return configIMS;
    }

    public List<ConfirmOrderDTO> getOrderToConfirm(String orderCodes, String username, String action
    ) {
        StringBuilder str = new StringBuilder("" +
                "SELECT d.ID,d.SVC_ORDER_DETAIL_CODE,d.SHOP_ID,d.SVC_ORDER_ID,d.ADDRESS_DELIVERY_ID,d.SERVICE_PACK_ID,d.SERVICE_PACK_SETTING_ID," +
                "d.CONSIGNEE,d.PHONE,d.WEIGHT,d.LENGTH,d.WIDTH,d.HEIGHT,d.EXPECT_PICK_DATE,d.EXPECT_DELIVER_DATE,d.IS_PART_DELIVERY,\n" +
                "d.IS_REFUND,d.IS_PORTER,d.IS_DOOR_DELIVERY,d.IS_DECLARE_PRODUCT,d.REQUIRED_NOTE,d.NOTE,d.IS_FREE,\n" +
                "d.STATUS,d.UTIMESTAMP,d.CREATE_AT,d.OLD_STATUS,d.CARRIER_ORDER_ID,d.SHOP_ORDER_ID,d.SHOP_ADDRESS_ID,d.CARRIER_ID,\n" +
                "d.CARRIER_SERVICE_CODE,d.ID_ACCOUNT_CARRIER,d.PICK_TYPE,d.PAYMENT_TYPE,d.REALITY_COD,d.EXPECT_COD," +
                "o.TYPE,o.STATUS as ORDER_STATUS,\n" +
                "dp.NAME ,dp.value ,dp.cod ,dp.quantity ,\n" +
                "p.NAME as PACK_NAME,p.CODE as PACK_CODE, \n" +
                "sa.ADDRESS as SHOP_ADDRESS,sa.NAME as SHOP_NAME,sa.PHONE as SHOP_PHONE,sa.PROVINCE_CODE as SHOP_PROVINCE_CODE,sa.DISTRICT_ID as SHOP_DISTRICT_CODE,sa.WARD_ID as SHOP_WARD_CODE,\n" +
                "ad.ADDRESS as ADDRESS_DELIVERY, ad.PROVINCE_NAME as DELIVERY_PROVINCE,ad.DISTRICT_NAME as DELIVERY_DISTRICT,\n" +
                "ad.WARD_NAME as DELIVERY_WARD,ad.PROVINCE_CODE,ad.DISTRICT_CODE,ad.WARD_CODE\n" +
                "FROM SVC_ORDER_DETAILS d \n" +
                " LEFT JOIN SVC_ORDERS o ON o.ORDER_CODE = d.SVC_ORDER_ID\n" +
                " LEFT JOIN SHOP_ADDRESS sa  ON o.SHOP_ADDRESS_ID = sa.ID\n" +
                " LEFT JOIN V2_PACKAGES p  ON o.SERVICE_PACK_ID = p.ID\n" +
                " LEFT JOIN DETAIL_PRODUCT dp ON dp.ORDER_DETAIL_CODE = d.SVC_ORDER_DETAIL_CODE\n" +
                " LEFT JOIN ADDRESS_DELIVERY ad ON ad.ID = d.ADDRESS_DELIVERY_ID\n");
        str.append(" WHERE d.SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE = '")
                .append(username)
                .append("')");
        if (!action.equals("ALL")) {
            str.append(" AND d.SVC_ORDER_ID IN ( ");
            str.append(orderCodes);
            str.append(" ) ");
        }
        str.append(" AND d.STATUS = 900 AND (d.IS_DELETED IS NULL OR d.IS_DELETED = 0)");
        Query query = entityManager.createNativeQuery(str.toString(), "ConfirmOrderMapping");
        List<ConfirmOrderDTO> result = query.getResultList();
        return result;
    }

    public List<FilterOrderDTO> getOrderToPrint(PrintOrderRequest printOrderRequest, String username) {
        StringBuilder str = new StringBuilder(
                "SELECT d.SVC_ORDER_DETAIL_CODE,d.CONSIGNEE,d.PHONE,\n" +
                        "d.WEIGHT,d.LENGTH,d.HEIGHT,d.WIDTH,\n" +
                        "d.CARRIER_ORDER_ID,d.SHOP_ORDER_ID,\n" +
                        "d.IS_FREE,d.REQUIRED_NOTE,d.REALITY_COD,d.NOTE,d.STATUS,\n" +
                        "sa.NAME as SHOP_NAME,sa.PHONE as SHOP_PHONE,sa.ADDRESS as SHOP_ADDRESS," +
                        "sa.WARD_ID as SHOP_WARD_CODE,\n" +
                        "ad.ADDRESS as RECEIVER_ADDRESS,ad.WARD_CODE as RECEIVER_WARD_CODE,d.CARRIER_ID,d.CARRIER_SHORT_CODE" +
                        " FROM SVC_ORDER_DETAILS d \n" +
                        " LEFT JOIN SVC_ORDERS o ON o.ORDER_CODE = d.SVC_ORDER_ID\n" +
                        " LEFT JOIN SHOP_ADDRESS sa  ON o.SHOP_ADDRESS_ID = sa.ID\n" +
                        " LEFT JOIN V2_PACKAGES p  ON o.SERVICE_PACK_ID = p.ID\n" +
                        " LEFT JOIN ADDRESS_DELIVERY ad ON ad.ID = d.ADDRESS_DELIVERY_ID\n" +
                        " WHERE d.SHOP_ID = (SELECT ID FROM APP_USERS WHERE PHONE = '"
        );
        str.append(username).append("') AND (d.IS_DELETED IS NULL OR d.IS_DELETED = 0) ");
        //In đơn nhỏ
        if (printOrderRequest.getType() == 1) {
            str.append(" AND d.SVC_ORDER_DETAIL_CODE IN (")
                    .append(printOrderRequest.getOrderCodes())
                    .append(")");
        }
        //In đơn to
        if (printOrderRequest.getType() == 2) {
            str.append(" AND d.SVC_ORDER_ID IN (")
                    .append(printOrderRequest.getOrderCodes())
                    .append(")");
        }
        //In theo status group
        if (printOrderRequest.getType() == 3) {
            if (!printOrderRequest.getSearchKey().isEmpty()) {
                str.append(" AND ( LOWER(d.SVC_ORDER_DETAIL_CODE) like LOWER(CONCAT(CONCAT('%', '")
                        .append(printOrderRequest.getSearchKey())
                        .append("'), '%'))");
                str.append(" OR LOWER(d.PHONE) like LOWER(CONCAT(CONCAT('%', '")
                        .append(printOrderRequest.getSearchKey())
                        .append("'), '%'))");
                str.append(" OR LOWER(d.SHOP_ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
                        .append(printOrderRequest.getSearchKey())
                        .append("'), '%'))");
                str.append(" OR LOWER(d.CARRIER_ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
                        .append(printOrderRequest.getSearchKey())
                        .append("'), '%'))");
                str.append(" OR LOWER(d.CONSIGNEE) like LOWER(CONCAT(CONCAT('%', '")
                        .append(printOrderRequest.getSearchKey())
                        .append("'), '%')))");
            }
            if (printOrderRequest.getTargetStatuses() != null && !printOrderRequest.getTargetStatuses().isEmpty()) {
                str.append(" AND d.STATUS IN (")
                        .append(printOrderRequest.getTargetStatuses())
                        .append(")");
            }
            if (printOrderRequest.getPackCode() != null && !printOrderRequest.getPackCode().isEmpty()) {
                str.append(" AND d.SERVICE_PACK_ID = (SELECT ID FROM V2_PACKAGES WHERE CODE = '")
                        .append(printOrderRequest.getPackCode())
                        .append("')");
            }
            if (printOrderRequest.getShopAddressId() != null) {
                str.append(" AND d.SHOP_ADDRESS_ID = ");
                str.append(printOrderRequest.getShopAddressId());
            }
            if (printOrderRequest.getOrderBy() == 1) {
                if (!printOrderRequest.getFromDate().isEmpty() && printOrderRequest.getFromDate() != null) {
                    str.append(" AND d.CREATE_AT > TO_DATE('")
                            .append(printOrderRequest.getFromDate())
                            .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
                }
                if (!printOrderRequest.getToDate().isEmpty() && printOrderRequest.getToDate() != null) {
                    str.append(" AND d.CREATE_AT < TO_DATE('")
                            .append(printOrderRequest.getToDate())
                            .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
                }

                str.append(" ORDER BY d.ID DESC,d.CREATE_AT DESC");
            } else {
                if (!printOrderRequest.getFromDate().isEmpty() && printOrderRequest.getFromDate() != null) {
                    str.append(" AND d.UTIMESTAMP > TO_DATE('")
                            .append(printOrderRequest.getFromDate())
                            .append(" 00:00:00', 'DD/MM/YYYY HH24:MI:SS') ");
                }
                if (!printOrderRequest.getToDate().isEmpty() && printOrderRequest.getToDate() != null) {
                    str.append(" AND d.UTIMESTAMP < TO_DATE('")
                            .append(printOrderRequest.getToDate())
                            .append(" 23:59:59','DD/MM/YYYY HH24:MI:SS')");
                }
                str.append(" ORDER BY d.ID DESC,d.UTIMESTAMP DESC");
            }
        }
        Query query = entityManagerReport.createNativeQuery(str.toString(), "FilterOrderPrint");
        List<FilterOrderDTO> result = query.getResultList();
        return result;
    }

    public List<ConsultOrderDTO> getConsultOrders(String codes ) {
        StringBuilder str = new StringBuilder(
                "SELECT d.ID,d.SVC_ORDER_DETAIL_CODE,d.SHOP_ID,d.SVC_ORDER_ID,d.ADDRESS_DELIVERY_ID,d.SERVICE_PACK_ID,\n" +
                        "d.SERVICE_PACK_SETTING_ID,d.CONSIGNEE,d.PHONE,d.WEIGHT,d.LENGTH,d.WIDTH,d.HEIGHT,d.EXPECT_PICK_DATE,\n" +
                        "d.EXPECT_DELIVER_DATE,d.IS_PART_DELIVERY, d.IS_REFUND,d.IS_PORTER,d.IS_DOOR_DELIVERY,d.IS_DECLARE_PRODUCT,\n" +
                        "d.REQUIRED_NOTE,d.NOTE,d.IS_FREE, d.STATUS,d.UTIMESTAMP,d.CREATE_AT,d.OLD_STATUS,d.CARRIER_ORDER_ID,\n" +
                        "d.SHOP_ORDER_ID,d.SHOP_ADDRESS_ID,d.CARRIER_ID, d.CARRIER_SERVICE_CODE,d.ID_ACCOUNT_CARRIER,d.PICK_TYPE,\n" +
                        "d.PAYMENT_TYPE,d.REALITY_COD,d.EXPECT_COD,\n" +
                        "o.TYPE,o.TOTAL_ADDRESS_DILIVERY,o.TOTAL_DISTANCE,o.TOTAL_ORDER_DETAIL,o.EXPECT_SHIP_ID,o.PICKUP_TYPE,o.STATUS as ORDER_STATUS,\n" +
                        "p.NAME as PACK_NAME,p.CODE as PACK_CODE,\n" +
                        "sa.ADDRESS as SHOP_ADDRESS,sa.NAME as SHOP_NAME,sa.PHONE as SHOP_PHONE,sa.PROVINCE_CODE AS SHOP_PROVINCE_CODE,sa.DISTRICT_ID AS SHOP_DISTRICT_CODE,sa.WARD_ID AS SHOP_WARD_CODE,\n" +
                        "ad.ADDRESS as ADDRESS_DELIVERY, ad.PROVINCE_NAME as DELIVERY_PROVINCE,ad.DISTRICT_NAME as DELIVERY_DISTRICT,\n" +
                        "ad.WARD_NAME as DELIVERY_WARD,ad.PROVINCE_CODE,ad.DISTRICT_CODE,ad.WARD_CODE,\n" +
                        "au.PHONE as SHIPPER_PHONE,au.NAME as SHIPPER_NAME " +
                        "FROM (SELECT * FROM SVC_ORDER_DETAILS WHERE (IS_DELETED IS NULL OR IS_DELETED = 0)");
        str.append(" AND (TO_CHAR(SVC_ORDER_DETAIL_CODE) IN (").append(codes).append(")");
        str.append(" OR CARRIER_ORDER_ID IN (").append(codes).append(")");
        str.append(")) d ");
        str.append("LEFT JOIN SVC_ORDERS o ON o.ORDER_CODE = d.SVC_ORDER_ID\n" +
                "LEFT JOIN SHOP_ADDRESS sa  ON o.SHOP_ADDRESS_ID = sa.ID\n" +
                "LEFT JOIN V2_PACKAGES p  ON o.SERVICE_PACK_ID = p.ID\n" +
                "LEFT JOIN ADDRESS_DELIVERY ad ON ad.ID = d.ADDRESS_DELIVERY_ID\n" +
                " LEFT JOIN APP_USERS au ON d.ID_ACCOUNT_CARRIER = au.ID");
        Query query = entityManagerReport.createNativeQuery(str.toString(), "ConsultOrderMapping");
        List<ConsultOrderDTO> result = query.getResultList();
        return result;
    }

}
