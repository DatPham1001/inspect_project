package com.imedia.oracle.reportentity;

import com.imedia.service.order.dto.ConsultOrderDTO;
import com.imedia.service.order.dto.CountTotalOM;
import com.imedia.service.order.dto.FilterOrderDTO;
import com.imedia.service.order.model.SumOrderStatusDTO;
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
        name = "FilterOrderMapping",
        classes = {
                @ConstructorResult(
                        targetClass = FilterOrderDTO.class,
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
                                //Fee
                                @ColumnResult(name = "FEE_NAME", type = String.class),
                                @ColumnResult(name = "FEE_CODE", type = String.class),
                                @ColumnResult(name = "FEE_VALUE", type = BigDecimal.class),
                                @ColumnResult(name = "TYPE", type = Long.class),
                                //Order
                                @ColumnResult(name = "TOTAL_ADDRESS_DILIVERY", type = Long.class),
                                @ColumnResult(name = "TOTAL_DISTANCE", type = Long.class),
                                @ColumnResult(name = "TOTAL_ORDER_DETAIL", type = Long.class),
                                @ColumnResult(name = "EXPECT_SHIP_ID", type = Long.class),
                                @ColumnResult(name = "PICKUP_TYPE", type = Long.class),
                                @ColumnResult(name = "ORDER_STATUS", type = Long.class),
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
                                //Shipper
                                @ColumnResult(name = "SHIPPER_PHONE", type = String.class),
                                @ColumnResult(name = "SHIPPER_NAME", type = String.class),

                        }
                )
        }
)
@SqlResultSetMapping(
        name = "SumOrderStatusMapping",
        classes = {
                @ConstructorResult(
                        targetClass = SumOrderStatusDTO.class,
                        columns = {
                                @ColumnResult(name = "STATUS", type = Integer.class),
                                @ColumnResult(name = "COUNT", type = Integer.class),
                        }
                )
        }
)
@SqlResultSetMapping(
        name = "CountTotalMapping",
        classes = {
                @ConstructorResult(
                        targetClass = CountTotalOM.class,
                        columns = {
                                @ColumnResult(name = "TOTAL", type = BigDecimal.class),
                                @ColumnResult(name = "TOTAL_FEE", type = BigDecimal.class),
                                @ColumnResult(name = "TOTAL_ORDER_DETAIL", type = BigDecimal.class),
                                @ColumnResult(name = "TOTAL_COD", type = BigDecimal.class),
                        }
                )
        }
)
@SqlResultSetMapping(
        name = "FilterOrderPrint",
        classes = {
                @ConstructorResult(
                        targetClass = FilterOrderDTO.class,
                        columns = {
                                @ColumnResult(name = "SVC_ORDER_DETAIL_CODE", type = BigDecimal.class),
                                @ColumnResult(name = "CONSIGNEE", type = String.class),
                                @ColumnResult(name = "PHONE", type = String.class),
                                @ColumnResult(name = "WEIGHT", type = Long.class),
                                @ColumnResult(name = "LENGTH", type = Long.class),
                                @ColumnResult(name = "HEIGHT", type = Long.class),
                                @ColumnResult(name = "WIDTH", type = Long.class),
                                @ColumnResult(name = "CARRIER_ORDER_ID", type = String.class),
                                @ColumnResult(name = "SHOP_ORDER_ID", type = String.class),
                                @ColumnResult(name = "IS_FREE", type = Long.class),
                                @ColumnResult(name = "REQUIRED_NOTE", type = Long.class),
                                @ColumnResult(name = "REALITY_COD", type = BigDecimal.class),
                                @ColumnResult(name = "NOTE", type = String.class),
                                @ColumnResult(name = "STATUS", type = Long.class),
                                //Shop info
                                @ColumnResult(name = "SHOP_NAME", type = String.class),
                                @ColumnResult(name = "SHOP_PHONE", type = String.class),
                                @ColumnResult(name = "SHOP_ADDRESS", type = String.class),
                                @ColumnResult(name = "SHOP_WARD_CODE", type = BigDecimal.class),
                                //Delivery
                                @ColumnResult(name = "RECEIVER_ADDRESS", type = String.class),
                                @ColumnResult(name = "RECEIVER_WARD_CODE", type = String.class),
                                @ColumnResult(name = "CARRIER_ID", type = Long.class),
                                @ColumnResult(name = "CARRIER_SHORT_CODE", type = String.class),
                        }
                )
        }
)
@SqlResultSetMapping(
        name = "ConsultOrderMapping",
        classes = {
                @ConstructorResult(
                        targetClass = ConsultOrderDTO.class,
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
                                @ColumnResult(name = "STATUS", type = Integer.class),
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
                                @ColumnResult(name = "TYPE", type = Long.class),
                                //Order
                                @ColumnResult(name = "TOTAL_ADDRESS_DILIVERY", type = Long.class),
                                @ColumnResult(name = "TOTAL_DISTANCE", type = Long.class),
                                @ColumnResult(name = "TOTAL_ORDER_DETAIL", type = Long.class),
                                @ColumnResult(name = "EXPECT_SHIP_ID", type = Long.class),
                                @ColumnResult(name = "PICKUP_TYPE", type = Long.class),
                                @ColumnResult(name = "ORDER_STATUS", type = Long.class),
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
                                //Shipper
                                @ColumnResult(name = "SHIPPER_PHONE", type = String.class),
                                @ColumnResult(name = "SHIPPER_NAME", type = String.class),
                        }
                )
        }
)
public class SvcOrderDetailReport implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name = "SVC_ORDER_DETAILS_SEQ", sequenceName = "SVC_ORDER_DETAILS_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SVC_ORDER_DETAILS_SEQ")
    private long id;

    @Column(name = "ADDRESS_DELIVERY_ID")
    private Long addressDeliveryId;

    @Column(name = "CARRIER_ID")
    private Integer carrierId;

    @Column(name = "CARRIER_ORDER_ID")
    private String carrierOrderId;

    @Column(name = "CARRIER_SERVICE_CODE")
    private String carrierServiceCode;

    private String consignee;

    @CreationTimestamp
    @Column(name = "CREATE_AT")
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
    private Timestamp utimestamp;

    private Integer weight;

    private Integer width;
    @Column(name = "ID_DELETED")
    private Integer isDeleted;
    public SvcOrderDetailReport() {
    }


}