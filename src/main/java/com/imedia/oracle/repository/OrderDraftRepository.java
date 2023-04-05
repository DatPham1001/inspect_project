package com.imedia.oracle.repository;

import com.imedia.oracle.entity.OrderDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface OrderDraftRepository extends JpaRepository<OrderDraft, Long> {
    OrderDraft findByOrderId(BigDecimal orderId);

}
