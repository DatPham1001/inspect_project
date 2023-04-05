package com.imedia.oracle.dao;

import com.imedia.service.userwallet.model.FilterWalletLogDataResponse;
import com.imedia.service.userwallet.model.FilterWalletLogRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletLogsDAO {
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;

    @Autowired
    public WalletLogsDAO(EntityManagerFactory entityManagerFactory, @Qualifier("secondaryEntityManagerFactory") EntityManager entityManager) {
        this.entityManagerFactory = entityManagerFactory;
        this.entityManager = entityManager;
    }


    public List<FilterWalletLogDataResponse> filterWalletLogs(FilterWalletLogRequest filterWalletLogRequest) {
        StringBuilder stringBuilder =
                new StringBuilder(
                        "SELECT wl.ID,d.CARRIER_ORDER_ID,wl.REASON,wl.TYPE,wl.CHANGE_BASE,wl.COST,wl.STATUS,wl.CREATE_AT," +
                                "wl.UTIMESTAMP,wl.CODE,wl.TO_BASE,wl.ORDER_DETAIL_ID " +
                                "FROM WALLET_LOGS wl " +
                                "LEFT JOIN SVC_ORDER_DETAILS d ON d.SVC_ORDER_DETAIL_CODE = wl.ORDER_DETAIL_ID " +
                                "WHERE " +
                                "WALLET_ID = (SELECT ACCOUNT_EPURSE_ID FROM APP_USERS " +
                                "WHERE PHONE = '");
        stringBuilder.append(filterWalletLogRequest.getUsername());
        stringBuilder.append("' )");
        stringBuilder.append(" AND wl.IS_DELETED = 1 ");
        if (!filterWalletLogRequest.getOrderId().isEmpty()) {
            stringBuilder.append(" AND  (LOWER(wl.ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
                    .append(filterWalletLogRequest.getOrderId()).append("'), '%'))")
                    .append("OR  LOWER(wl.CODE) like LOWER(CONCAT(CONCAT('%', '")
                    .append(filterWalletLogRequest.getOrderId()).append("'), '%'))")
                    .append("OR  LOWER(d.SVC_ORDER_DETAIL_CODE) like LOWER(CONCAT(CONCAT('%', '")
                    .append(filterWalletLogRequest.getOrderId()).append("'), '%'))")
                    .append("OR  LOWER(d.CARRIER_ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
                    .append(filterWalletLogRequest.getOrderId()).append("'), '%')))");
        }
        if (!String.valueOf(filterWalletLogRequest.getStatus()).equals(String.valueOf(-1))) {
            if (filterWalletLogRequest.getStatus() == 1)
                stringBuilder.append(" AND wl.STATUS NOT IN (0,2,3)");
            else if (filterWalletLogRequest.getStatus() == 2)
                stringBuilder.append(" AND wl.STATUS NOT IN (0,1,4,5)");
            else stringBuilder.append(" AND wl.STATUS = ").append(filterWalletLogRequest.getStatus());
        }
        if (filterWalletLogRequest.getType() != null) {
            if (filterWalletLogRequest.getType() == 0)
                stringBuilder.append(" AND wl.TYPE IN (1,2,3) ");
            if (filterWalletLogRequest.getType() == 1)
                stringBuilder.append(" AND wl.TYPE IN (10,11,12) ");
        } else stringBuilder.append(" AND wl.TYPE IN (1,2,3,10,11,12) ");

//        if (!filterWalletLogRequest.getFromDate().isEmpty())
//            stringBuilder.append(" AND  wl.UTIMESTAMP > TO_DATE('")
//                    .append(filterWalletLogRequest.getFromDate()).append(" 00:00:00").append("', 'DD/MM/YYYY HH24:MI:SS')");
//        if (!filterWalletLogRequest.getToDate().isEmpty())
//            stringBuilder.append(" AND  wl.UTIMESTAMP < TO_DATE('")
//                    .append(filterWalletLogRequest.getToDate()).append(" 23:59:59").append("','DD/MM/YYYY HH24:MI:SS')");
//        stringBuilder.append(" ORDER BY wl.UTIMESTAMP DESC");
        if (!filterWalletLogRequest.getFromDate().isEmpty())
            stringBuilder.append(" AND  wl.CREATE_AT > TO_DATE('")
                    .append(filterWalletLogRequest.getFromDate()).append(" 00:00:00").append("', 'DD/MM/YYYY HH24:MI:SS')");
        if (!filterWalletLogRequest.getToDate().isEmpty())
            stringBuilder.append(" AND  wl.CREATE_AT < TO_DATE('")
                    .append(filterWalletLogRequest.getToDate()).append(" 23:59:59").append("','DD/MM/YYYY HH24:MI:SS')");
        stringBuilder.append(" ORDER BY wl.CREATE_AT DESC");

        if (filterWalletLogRequest.getExportExcel() == null || filterWalletLogRequest.getExportExcel() != 1) {
            stringBuilder.append(" OFFSET ");
            stringBuilder.append(filterWalletLogRequest.getPage() * filterWalletLogRequest.getSize());
            stringBuilder.append(" ROWS FETCH NEXT ");
            stringBuilder.append(filterWalletLogRequest.getSize());
            stringBuilder.append(" ROWS ONLY");
        }
        Query query = entityManager.createNativeQuery(stringBuilder.toString(), "FilterWalletLogMapping");
//        query.setFirstResult(filterWalletLogRequest.getPage() * filterWalletLogRequest.getSize());
//        query.setMaxResults(filterWalletLogRequest.getSize());
        System.out.println(stringBuilder);
        List<FilterWalletLogDataResponse> result = query.getResultList();
        return result;
    }

    public BigDecimal countFilterWalletLogs(FilterWalletLogRequest filterWalletLogRequest) {
        StringBuilder stringBuilder =
                new StringBuilder(
                        "SELECT COUNT(wl.ID) " +
                                "FROM WALLET_LOGS wl " +
                                "LEFT JOIN SVC_ORDER_DETAILS d ON d.SVC_ORDER_DETAIL_CODE = wl.ORDER_DETAIL_ID " +
                                "WHERE " +
                                "WALLET_ID = ( SELECT ACCOUNT_EPURSE_ID FROM APP_USERS " +
                                "WHERE PHONE = '");
        stringBuilder.append(filterWalletLogRequest.getUsername());
        stringBuilder.append("'  )");
        stringBuilder.append(" AND wl.IS_DELETED = 1 ");
        if (!filterWalletLogRequest.getOrderId().isEmpty()) {
            stringBuilder.append(" AND  (LOWER(wl.ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
                    .append(filterWalletLogRequest.getOrderId()).append("'), '%'))")
                    .append("OR  LOWER(wl.CODE) like LOWER(CONCAT(CONCAT('%', '")
                    .append(filterWalletLogRequest.getOrderId()).append("'), '%'))")
                    .append("OR  LOWER(d.CARRIER_ORDER_ID) like LOWER(CONCAT(CONCAT('%', '")
                    .append(filterWalletLogRequest.getOrderId()).append("'), '%')))");
        }
        if (!String.valueOf(filterWalletLogRequest.getStatus()).equals(String.valueOf(-1))) {
            if (filterWalletLogRequest.getStatus() == 1)
                stringBuilder.append(" AND wl.STATUS NOT IN (0,2,3)");
            else if (filterWalletLogRequest.getStatus() == 2)
                stringBuilder.append(" AND wl.STATUS NOT IN (0,1,4,5)");
            else stringBuilder.append(" AND wl.STATUS = ").append(filterWalletLogRequest.getStatus());
        }
        if (filterWalletLogRequest.getType() != null) {
            if (filterWalletLogRequest.getType() == 0)
                stringBuilder.append(" AND wl.TYPE IN (1,2,3) ");
            if (filterWalletLogRequest.getType() == 1)
                stringBuilder.append(" AND wl.TYPE IN (10,11,12) ");
        } else stringBuilder.append(" AND wl.TYPE IN (1,2,3,10,11,12) ");
        if (!filterWalletLogRequest.getFromDate().isEmpty())
            stringBuilder.append(" AND  wl.UTIMESTAMP > TO_DATE('")
                    .append(filterWalletLogRequest.getFromDate()).append(" 00:00:00").append("', 'DD/MM/YYYY HH24:MI:SS')");
        if (!filterWalletLogRequest.getToDate().isEmpty())
            stringBuilder.append(" AND  wl.UTIMESTAMP < TO_DATE('")
                    .append(filterWalletLogRequest.getToDate()).append(" 23:59:59").append("', 'DD/MM/YYYY HH24:MI:SS')");
        Query query = entityManager.createNativeQuery(stringBuilder.toString());
        BigDecimal result = (BigDecimal) query.getSingleResult();
        return result;
    }

}
