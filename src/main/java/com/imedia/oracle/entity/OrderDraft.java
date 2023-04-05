package com.imedia.oracle.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "ORDER_DRAFT")
@Getter
@Setter
public class OrderDraft {
    @Id
    @SequenceGenerator(name = "ORDER_DRAFT_SEQ", sequenceName = "ORDER_DRAFT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORDER_DRAFT_SEQ")
    private long id;

    @Column(name = "ORDER_ID")
    private BigDecimal orderId;

    @Column(name = "SHOP_ID")
    private BigDecimal shopId;
    @CreationTimestamp
    @Column(name = "CREATE_DATE")
    private Timestamp createdDate;

    private String message;


}
