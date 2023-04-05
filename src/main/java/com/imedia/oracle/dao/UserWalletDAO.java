package com.imedia.oracle.dao;

import com.imedia.service.userwallet.model.BankAccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

@Service
public class UserWalletDAO {
    private final EntityManagerFactory entityManagerFactory;
    private final EntityManager entityManager;

    @Autowired
    public UserWalletDAO(EntityManagerFactory entityManagerFactory, @Qualifier("secondaryEntityManagerFactory") EntityManager entityManager) {
        this.entityManagerFactory = entityManagerFactory;
        this.entityManager = entityManager;
    }

    public List<BankAccountDTO> getBankAccounts(String username, Integer withdrawType) throws Exception {
        String sql = "SELECT b.id,b.BANK_ACCOUNT,b.BANK_ACCOUNT_NAME,b.PHONE,b.ROLE_TYPE,\n" +
                "b.STATUS,b.TYPE,b.WITHDRAW_TYPE,b.BANK_CODE,b1.NAME as bankName,b1.CODE as bankShortName \n" +
                "FROM BANK_ACCOUNTS b \n" +
                "LEFT JOIN BANKS b1 on b.BANK_CODE = b1.BANK_CODE " +
                "WHERE APP_USER_ID = (SELECT ID FROM APP_USERS WHERE PHONE = '" + username + "') " +
                "AND b.ROLE_TYPE = 0 AND b.STATUS = 1 AND b.IS_DELETED = 1 AND b.WITHDRAW_TYPE =  " + withdrawType;
        Query query = entityManager.createNativeQuery(sql, "BankAccountsMapping");
        List<BankAccountDTO> bankAccounts = query.getResultList();
        return bankAccounts;
    }
}
