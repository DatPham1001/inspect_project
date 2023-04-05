package com.imedia.oracle.repository;

import com.imedia.oracle.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;


@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByPhone(String phone);

    AppUser findAppUserById(Long id);

    List<AppUser> findAllByFacebookIdOrPhone(String facebookId, String phone);

    List<AppUser> findByEmail(String email);

    @Query(value = "SELECT a.* FROM APP_USERS a JOIN ORDER_DETAILS d ON a.ID = d.SHIP_ID\n" +
            "WHERE d.ORDER_DETAIL_CODE = :code", nativeQuery = true)
    AppUser getShipperData(@Param("code") BigDecimal detailCode);


    @Transactional
    @Modifying
    @Query(value = "UPDATE APP_USERS SET ACCOUNT_EPURSE_ID = :id WHERE PHONE = :username", nativeQuery = true)
    void updateAccountEpurse(@Param("username") String username, @Param("id") Long id);

}
