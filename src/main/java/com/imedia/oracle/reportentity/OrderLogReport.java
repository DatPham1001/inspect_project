package com.imedia.oracle.reportentity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the ORDER_LOGS database table.
 */
@Entity
@Table(name = "ORDER_LOGS")
@Getter
@Setter
public class OrderLogReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "ORDER_LOGS_SEQ1", sequenceName = "ORDER_LOGS_SEQ1", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORDER_LOGS_SEQ1")
    private long id;

    @CreationTimestamp
    @Column(name = "CREATE_AT")
    private Timestamp createAt;

    @Column(name = "CREATE_BY")
    private Long createBy;

    @Column(name = "CREATE_BY_TYPE")
    private Integer createByType;

    @Column(name = "DOCUMENT_LINKS_ID")
    private Long documentLinksId;

    @Column(name = "FROM_STATUS")
    private Integer fromStatus;

    private String note;

    @Column(name = "ORDER_DETAIL_ID")
    private Long orderDetailId;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "ROLLBACK_FLAG")
    private Integer rollbackFlag;

    @Column(name = "SUCCESS_FLAG")
    private Integer successFlag;

    @Column(name = "TO_STATUS")
    private Integer toStatus;
    @Column(name = "SHIP_ID")
    private Long shipId;

    public OrderLogReport() {
    }


}