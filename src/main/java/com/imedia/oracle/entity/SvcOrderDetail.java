package com.imedia.oracle.entity;

import com.imedia.service.order.dto.ConfirmOrderDTO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the SVC_ORDER_DETAILS database table.
 */
@Entity
@Table(name = "SVC_ORDER_DETAILS")
@Getter
@Setter
@SqlResultSetMapping(
        name = "ConfirmOrderMapping",
        classes = {
                @ConstructorResult(
                        targetClass = ConfirmOrderDTO.class,
                        columns = {
                                @ColumnResult(name = "ID", type = Long.class),
                                @ColumnResult(name = "SVC_ORDER_DETAIL_CODE", type = BigDecimal.class),
                                @ColumnResult(name = "SHOP_ID", type = Long.class),
                                @ColumnResult(name = "SVC_ORDER_ID", type = BigDecimal.class),
                                @ColumnResult(name = "ADDRESS_DELIVERY_ID", type = Long.class),
                                @ColumnResult(name = "SERVICE_PACK_ID", type = Long.class),
                                @ColumnResult(name = "SERVICE_PACK_SETTING_ID", type = Long.class),
                                @ColumnResult(name = "CONSIGNEE", type = String.class),
                                @ColumnResult(name = "PHONE", type = String.class),
                                @ColumnResult(name = "WEIGHT", type = Long.class),
                                @ColumnResult(name = "LENGTH", type = Long.class),
                                @ColumnResult(name = "WIDTH", type = Long.class),
                                @ColumnResult(name = "HEIGHT", type = Long.class),
                                @ColumnResult(name = "EXPECT_PICK_DATE", type = Date.class),
                                @ColumnResult(name = "EXPECT_DELIVER_DATE", type = Date.class),
                                @ColumnResult(name = "IS_PART_DELIVERY", type = Long.class),
                                @ColumnResult(name = "IS_REFUND", type = Long.class),
                                @ColumnResult(name = "IS_PORTER", type = Long.class),
                                @ColumnResult(name = "IS_DOOR_DELIVERY", type = Long.class),
                                @ColumnResult(name = "IS_DECLARE_PRODUCT", type = Long.class),
                                @ColumnResult(name = "REQUIRED_NOTE", type = Long.class),
                                @ColumnResult(name = "NOTE", type = String.class),
                                @ColumnResult(name = "IS_FREE", type = Long.class),
                                @ColumnResult(name = "STATUS", type = Long.class),
                                @ColumnResult(name = "UTIMESTAMP", type = Date.class),
                                @ColumnResult(name = "CREATE_AT", type = Date.class),
                                @ColumnResult(name = "OLD_STATUS", type = Long.class),
                                @ColumnResult(name = "CARRIER_ORDER_ID", type = String.class),
                                @ColumnResult(name = "SHOP_ORDER_ID", type = String.class),
                                @ColumnResult(name = "SHOP_ADDRESS_ID", type = Long.class),
                                @ColumnResult(name = "CARRIER_ID", type = Long.class),
                                @ColumnResult(name = "CARRIER_SERVICE_CODE", type = String.class),
                                @ColumnResult(name = "ID_ACCOUNT_CARRIER", type = Long.class),
                                @ColumnResult(name = "PICK_TYPE", type = Integer.class),
                                @ColumnResult(name = "PAYMENT_TYPE", type = Integer.class),
                                @ColumnResult(name = "REALITY_COD", type = BigDecimal.class),
                                @ColumnResult(name = "EXPECT_COD", type = BigDecimal.class),
                                //Order
                                @ColumnResult(name = "TYPE", type = Long.class),
                                @ColumnResult(name = "ORDER_STATUS", type = Long.class),
                                //Product
                                @ColumnResult(name = "NAME", type = String.class),
                                @ColumnResult(name = "VALUE", type = BigDecimal.class),
                                @ColumnResult(name = "COD", type = BigDecimal.class),
                                @ColumnResult(name = "QUANTITY", type = Integer.class),
                                //Pack
                                @ColumnResult(name = "PACK_NAME", type = String.class),
                                @ColumnResult(name = "PACK_CODE", type = String.class),
                                //Shop info
                                @ColumnResult(name = "SHOP_ADDRESS", type = String.class),
                                @ColumnResult(name = "SHOP_NAME", type = String.class),
                                @ColumnResult(name = "SHOP_PHONE", type = String.class),
                                @ColumnResult(name = "SHOP_PROVINCE_CODE", type = String.class),
                                @ColumnResult(name = "SHOP_DISTRICT_CODE", type = BigDecimal.class),
                                @ColumnResult(name = "SHOP_WARD_CODE", type = BigDecimal.class),
                                //Delivery
                                @ColumnResult(name = "ADDRESS_DELIVERY", type = String.class),
                                @ColumnResult(name = "DELIVERY_PROVINCE", type = String.class),
                                @ColumnResult(name = "DELIVERY_DISTRICT", type = String.class),
                                @ColumnResult(name = "DELIVERY_WARD", type = String.class),
                                @ColumnResult(name = "PROVINCE_CODE", type = String.class),
                                @ColumnResult(name = "DISTRICT_CODE", type = BigDecimal.class),
                                @ColumnResult(name = "WARD_CODE", type = BigDecimal.class),
                        }
                )
        }
)
public class SvcOrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "SVC_ORDER_DETAILS_SEQ", sequenceName = "SVC_ORDER_DETAILS_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SVC_ORDER_DETAILS_SEQ")
    private long id;

    @Column(name = "ADDRESS_DELIVERY_ID")
    private Long addressDeliveryId;

    @Column(name = "CARRIER_ID")
    private Long carrierId;

    @Column(name = "CARRIER_ORDER_ID")
    private String carrierOrderId;

    @Column(name = "CARRIER_SERVICE_CODE")
    private String carrierServiceCode;

    private String consignee;

    @CreationTimestamp
    @Column(name = "CREATE_AT", updatable = false)
    private Timestamp createAt;

    @Column(name = "EXPECT_DELIVER_DATE")
    private Timestamp expectDeliverDate;

    @Column(name = "EXPECT_PICK_DATE")
    private Timestamp expectPickDate;

    private Integer height;

    @Column(name = "ID_ACCOUNT_CARRIER")
    private Integer idAccountCarrier;

    @Column(name = "IS_DECLARE_PRODUCT")
    private Integer isDeclareProduct;

    @Column(name = "IS_DOOR_DELIVERY")
    private Integer isDoorDelivery;

    @Column(name = "IS_FREE")
    private Integer isFree;

    @Column(name = "IS_PART_DELIVERY")
    private Integer isPartDelivery;

    @Column(name = "IS_PORTER")
    private Integer isPorter;

    @Column(name = "IS_REFUND")
    private Integer isRefund;

    @Column(name = "LENGTH")
    private Integer length;

    private String note;

    @Column(name = "OLD_STATUS")
    private Integer oldStatus;

    private String phone;

    @Column(name = "PICK_TYPE")
    private Integer pickType;

    @Column(name = "REQUIRED_NOTE")
    private Integer requiredNote;

    @Column(name = "SERVICE_PACK_ID")
    private Integer servicePackId;

    @Column(name = "SERVICE_PACK_SETTING_ID")
    private Integer servicePackSettingId;

    @Column(name = "SHOP_ADDRESS_ID")
    private Integer shopAddressId;

    @Column(name = "SHOP_ID")
    private Long shopId;

    @Column(name = "SHOP_ORDER_ID")
    private String shopOrderId;

    private Integer status;

    @Column(name = "SVC_ORDER_DETAIL_CODE")
    private BigDecimal svcOrderDetailCode;

    @Column(name = "SVC_ORDER_ID")
    private BigDecimal svcOrderId;

    @UpdateTimestamp
    @Column(name = "UTIMESTAMP", updatable = false)
    private Timestamp utimestamp;

    private Integer weight;

    private Integer width;

    @Column(name = "PAYMENT_TYPE")
    private Integer paymentType;

    @Column(name = "REALITY_COD")
    private BigDecimal realityCod;

    @Column(name = "EXPECT_COD")
    private BigDecimal expectCod;

    @Column(name = "SHOP_REFER_ID", updatable = false)
    private Long shopReferId;

    @Column(name = "CHANNEL")
    private String channel;

    @Column(name = "CARRIER_SHORT_CODE")
    private String carrierShortCode;

    @Column(name = "IS_DELETED")
    private Integer isDeleted;

    public SvcOrderDetail() {
    }


}