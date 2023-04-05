package com.imedia.oracle.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the ORDER_DETAILS database table.
 */
@Entity
@Table(name = "ORDER_DETAILS")
@Getter
@Setter
public class OrderDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private BigDecimal amount;

	@Column(name = "CARRIER_CODE")
	private String carrierCode;

	@Column(name = "CARRIER_ID")
	private BigDecimal carrierId;

	@Column(name = "CARRIER_ID_ACCOUNT")
	private BigDecimal carrierIdAccount;

	@Column(name = "CARRIER_OLD_STATUS")
	private String carrierOldStatus;

	@Column(name = "CARRIER_ORDER_ID")
	private String carrierOrderId;

	@Column(name = "CARRIER_PRICE")
	private BigDecimal carrierPrice;

	@Column(name = "CARRIER_STATUS")
	private String carrierStatus;

	private BigDecimal cod;

	private String consignee;

	@Column(name = "CREATE_AT")
	private Timestamp createAt;

	@Column(name = "DECLARE_PRODUCT_FEE")
	private BigDecimal declareProductFee;

	@Column(name = "DOCUMENT_LINKS_ID")
	private BigDecimal documentLinksId;

	@Column(name = "DOOR_DELIVERY_FEE")
	private BigDecimal doorDeliveryFee;

	@Column(name = "EXPECT_DELIVER_DATE")
	private Timestamp expectDeliverDate;

	@Column(name = "EXPECT_PICK_DATE")
	private Timestamp expectPickDate;

	private Integer height;

	@Column(name = "HOLA_NET")
	private BigDecimal holaNet;

	@Column(name = "HOLD_SHIP")
	private BigDecimal holdShip;

	@Column(name = "HOLD_SHOP")
	private BigDecimal holdShop;

	@Column(name = "IS_DECLARE_PRODUCT")
	private BigDecimal isDeclareProduct;

	@Column(name = "IS_DELETE")
	private Integer isDelete;

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

	@Column(name = "ORDER_CHECK_POINT_ID")
	private BigDecimal orderCheckPointId;

	@Column(name = "ORDER_DETAIL_CODE")
	private BigDecimal orderDetailCode;

	@Column(name = "ORDER_ID")
	private Long orderId;

	@Column(name = "OTHER_FEE")
	private BigDecimal otherFee;

	private String phone;

	@Column(name = "PICK_TYPE")
	private Integer pickType;

	@Column(name = "PORTER_FEE")
	private BigDecimal porterFee;

	@Column(name = "PRODUCT_CATE_ID")
	private Integer productCateId;

	@Column(name = "PRODUCT_NAME")
	private String productName;

	@Column(name = "PRODUCT_VALUE")
	private BigDecimal productValue;

	private Integer quantity;

	@Column(name = "RECEIVED_AT")
	private Timestamp receivedAt;

	@Column(name = "REFUND_FEE")
	private BigDecimal refundFee;

	@Column(name = "REQUIRED_NOTE")
	private Integer requiredNote;

	@Column(name = "SERVICE_PACK_ID")
	private Integer servicePackId;

	@Column(name = "SERVICE_PACK_SETTING_ID")
	private Integer servicePackSettingId;

	@Column(name = "SHIP_BASE_FEE")
	private BigDecimal shipBaseFee;

	@Column(name = "SHIP_FEE")
	private BigDecimal shipFee;

	@Column(name = "SHIP_ID")
	private BigDecimal shipId;

	@Lob
	@Column(name = "SHIP_IMG_CONFIRM")
	private String shipImgConfirm;

	@Column(name = "SHIP_NET")
	private BigDecimal shipNet;

	@Column(name = "SHOP_ID")
	private BigDecimal shopId;

	@Column(name = "SHOP_ORDER_ID")
	private String shopOrderId;

	@Column(name = "SHORT_ID")
	private String shortId;

	private Integer status;

	@Column(name = "SURCHARGE_FEE")
	private BigDecimal surchargeFee;

	@Column(name = "TYPE")
	private Integer type;

	private Timestamp utimestamp;

	private Integer weight;

	private Integer width;

	public OrderDetail() {
	}


}