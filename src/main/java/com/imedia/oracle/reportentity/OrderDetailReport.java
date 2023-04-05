package com.imedia.oracle.reportentity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the ORDER_DETAILS database table.
 */
@Entity
@Table(name = "ORDER_DETAILS")
@NamedQuery(name = "OrderDetail.findAll", query = "SELECT o FROM OrderDetailReport o")
public class OrderDetailReport implements Serializable {
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

    private BigDecimal height;

    @Column(name = "HOLA_NET")
    private BigDecimal holaNet;

    @Column(name = "HOLD_SHIP")
    private BigDecimal holdShip;

    @Column(name = "HOLD_SHOP")
    private BigDecimal holdShop;

    @Column(name = "IS_DECLARE_PRODUCT")
    private BigDecimal isDeclareProduct;

    @Column(name = "IS_DELETE")
    private BigDecimal isDelete;

    @Column(name = "IS_DOOR_DELIVERY")
    private BigDecimal isDoorDelivery;

    @Column(name = "IS_FREE")
    private BigDecimal isFree;

    @Column(name = "IS_PART_DELIVERY")
    private BigDecimal isPartDelivery;

    @Column(name = "IS_PORTER")
    private BigDecimal isPorter;

    @Column(name = "IS_REFUND")
    private BigDecimal isRefund;

    @Column(name = "LENGTH")
    private BigDecimal length;

    private String note;

    @Column(name = "OLD_STATUS")
    private BigDecimal oldStatus;

    @Column(name = "ORDER_CHECK_POINT_ID")
    private BigDecimal orderCheckPointId;

    @Column(name = "ORDER_DETAIL_CODE")
    private BigDecimal orderDetailCode;

    @Column(name = "ORDER_ID")
    private BigDecimal orderId;

    @Column(name = "OTHER_FEE")
    private BigDecimal otherFee;

    private String phone;

    @Column(name = "PICK_TYPE")
    private BigDecimal pickType;

    @Column(name = "PORTER_FEE")
    private BigDecimal porterFee;

    @Column(name = "PRODUCT_CATE_ID")
    private BigDecimal productCateId;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "PRODUCT_VALUE")
    private BigDecimal productValue;

    private BigDecimal quantity;

    @Column(name = "RECEIVED_AT")
    private Timestamp receivedAt;

    @Column(name = "REFUND_FEE")
    private BigDecimal refundFee;

    @Column(name = "REQUIRED_NOTE")
    private BigDecimal requiredNote;

    @Column(name = "SERVICE_PACK_ID")
    private BigDecimal servicePackId;

    @Column(name = "SERVICE_PACK_SETTING_ID")
    private BigDecimal servicePackSettingId;

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

    private BigDecimal status;

    @Column(name = "SURCHARGE_FEE")
    private BigDecimal surchargeFee;

    @Column(name = "TYPE")
    private BigDecimal type;

    private Timestamp utimestamp;

    private BigDecimal weight;

    private BigDecimal width;

    public OrderDetailReport() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCarrierCode() {
        return this.carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public BigDecimal getCarrierId() {
        return this.carrierId;
    }

    public void setCarrierId(BigDecimal carrierId) {
        this.carrierId = carrierId;
    }

    public BigDecimal getCarrierIdAccount() {
        return this.carrierIdAccount;
    }

    public void setCarrierIdAccount(BigDecimal carrierIdAccount) {
        this.carrierIdAccount = carrierIdAccount;
    }

    public String getCarrierOldStatus() {
        return this.carrierOldStatus;
    }

    public void setCarrierOldStatus(String carrierOldStatus) {
        this.carrierOldStatus = carrierOldStatus;
    }

    public String getCarrierOrderId() {
        return this.carrierOrderId;
    }

    public void setCarrierOrderId(String carrierOrderId) {
        this.carrierOrderId = carrierOrderId;
    }

    public BigDecimal getCarrierPrice() {
        return this.carrierPrice;
    }

    public void setCarrierPrice(BigDecimal carrierPrice) {
        this.carrierPrice = carrierPrice;
    }

    public String getCarrierStatus() {
        return this.carrierStatus;
    }

    public void setCarrierStatus(String carrierStatus) {
        this.carrierStatus = carrierStatus;
    }

    public BigDecimal getCod() {
        return this.cod;
    }

    public void setCod(BigDecimal cod) {
        this.cod = cod;
    }

    public String getConsignee() {
        return this.consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public Timestamp getCreateAt() {
        return this.createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    public BigDecimal getDeclareProductFee() {
        return this.declareProductFee;
    }

    public void setDeclareProductFee(BigDecimal declareProductFee) {
        this.declareProductFee = declareProductFee;
    }

    public BigDecimal getDocumentLinksId() {
        return this.documentLinksId;
    }

    public void setDocumentLinksId(BigDecimal documentLinksId) {
        this.documentLinksId = documentLinksId;
    }

    public BigDecimal getDoorDeliveryFee() {
        return this.doorDeliveryFee;
    }

    public void setDoorDeliveryFee(BigDecimal doorDeliveryFee) {
        this.doorDeliveryFee = doorDeliveryFee;
    }

    public Timestamp getExpectDeliverDate() {
        return this.expectDeliverDate;
    }

    public void setExpectDeliverDate(Timestamp expectDeliverDate) {
        this.expectDeliverDate = expectDeliverDate;
    }

    public Timestamp getExpectPickDate() {
        return this.expectPickDate;
    }

    public void setExpectPickDate(Timestamp expectPickDate) {
        this.expectPickDate = expectPickDate;
    }

    public BigDecimal getHeight() {
        return this.height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getHolaNet() {
        return this.holaNet;
    }

    public void setHolaNet(BigDecimal holaNet) {
        this.holaNet = holaNet;
    }

    public BigDecimal getHoldShip() {
        return this.holdShip;
    }

    public void setHoldShip(BigDecimal holdShip) {
        this.holdShip = holdShip;
    }

    public BigDecimal getHoldShop() {
        return this.holdShop;
    }

    public void setHoldShop(BigDecimal holdShop) {
        this.holdShop = holdShop;
    }

    public BigDecimal getIsDeclareProduct() {
        return this.isDeclareProduct;
    }

    public void setIsDeclareProduct(BigDecimal isDeclareProduct) {
        this.isDeclareProduct = isDeclareProduct;
    }

    public BigDecimal getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(BigDecimal isDelete) {
        this.isDelete = isDelete;
    }

    public BigDecimal getIsDoorDelivery() {
        return this.isDoorDelivery;
    }

    public void setIsDoorDelivery(BigDecimal isDoorDelivery) {
        this.isDoorDelivery = isDoorDelivery;
    }

    public BigDecimal getIsFree() {
        return this.isFree;
    }

    public void setIsFree(BigDecimal isFree) {
        this.isFree = isFree;
    }

    public BigDecimal getIsPartDelivery() {
        return this.isPartDelivery;
    }

    public void setIsPartDelivery(BigDecimal isPartDelivery) {
        this.isPartDelivery = isPartDelivery;
    }

    public BigDecimal getIsPorter() {
        return this.isPorter;
    }

    public void setIsPorter(BigDecimal isPorter) {
        this.isPorter = isPorter;
    }

    public BigDecimal getIsRefund() {
        return this.isRefund;
    }

    public void setIsRefund(BigDecimal isRefund) {
        this.isRefund = isRefund;
    }

    public BigDecimal getLength() {
        return this.length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getOldStatus() {
        return this.oldStatus;
    }

    public void setOldStatus(BigDecimal oldStatus) {
        this.oldStatus = oldStatus;
    }

    public BigDecimal getOrderCheckPointId() {
        return this.orderCheckPointId;
    }

    public void setOrderCheckPointId(BigDecimal orderCheckPointId) {
        this.orderCheckPointId = orderCheckPointId;
    }

    public BigDecimal getOrderDetailCode() {
        return this.orderDetailCode;
    }

    public void setOrderDetailCode(BigDecimal orderDetailCode) {
        this.orderDetailCode = orderDetailCode;
    }

    public BigDecimal getOrderId() {
        return this.orderId;
    }

    public void setOrderId(BigDecimal orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getOtherFee() {
        return this.otherFee;
    }

    public void setOtherFee(BigDecimal otherFee) {
        this.otherFee = otherFee;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getPickType() {
        return this.pickType;
    }

    public void setPickType(BigDecimal pickType) {
        this.pickType = pickType;
    }

    public BigDecimal getPorterFee() {
        return this.porterFee;
    }

    public void setPorterFee(BigDecimal porterFee) {
        this.porterFee = porterFee;
    }

    public BigDecimal getProductCateId() {
        return this.productCateId;
    }

    public void setProductCateId(BigDecimal productCateId) {
        this.productCateId = productCateId;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getProductValue() {
        return this.productValue;
    }

    public void setProductValue(BigDecimal productValue) {
        this.productValue = productValue;
    }

    public BigDecimal getQuantity() {
        return this.quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Timestamp getReceivedAt() {
        return this.receivedAt;
    }

    public void setReceivedAt(Timestamp receivedAt) {
        this.receivedAt = receivedAt;
    }

    public BigDecimal getRefundFee() {
        return this.refundFee;
    }

    public void setRefundFee(BigDecimal refundFee) {
        this.refundFee = refundFee;
    }

    public BigDecimal getRequiredNote() {
        return this.requiredNote;
    }

    public void setRequiredNote(BigDecimal requiredNote) {
        this.requiredNote = requiredNote;
    }

    public BigDecimal getServicePackId() {
        return this.servicePackId;
    }

    public void setServicePackId(BigDecimal servicePackId) {
        this.servicePackId = servicePackId;
    }

    public BigDecimal getServicePackSettingId() {
        return this.servicePackSettingId;
    }

    public void setServicePackSettingId(BigDecimal servicePackSettingId) {
        this.servicePackSettingId = servicePackSettingId;
    }

    public BigDecimal getShipBaseFee() {
        return this.shipBaseFee;
    }

    public void setShipBaseFee(BigDecimal shipBaseFee) {
        this.shipBaseFee = shipBaseFee;
    }

    public BigDecimal getShipFee() {
        return this.shipFee;
    }

    public void setShipFee(BigDecimal shipFee) {
        this.shipFee = shipFee;
    }

    public BigDecimal getShipId() {
        return this.shipId;
    }

    public void setShipId(BigDecimal shipId) {
        this.shipId = shipId;
    }

    public String getShipImgConfirm() {
        return this.shipImgConfirm;
    }

    public void setShipImgConfirm(String shipImgConfirm) {
        this.shipImgConfirm = shipImgConfirm;
    }

    public BigDecimal getShipNet() {
        return this.shipNet;
    }

    public void setShipNet(BigDecimal shipNet) {
        this.shipNet = shipNet;
    }

    public BigDecimal getShopId() {
        return this.shopId;
    }

    public void setShopId(BigDecimal shopId) {
        this.shopId = shopId;
    }

    public String getShopOrderId() {
        return this.shopOrderId;
    }

    public void setShopOrderId(String shopOrderId) {
        this.shopOrderId = shopOrderId;
    }

    public String getShortId() {
        return this.shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public BigDecimal getStatus() {
        return this.status;
    }

    public void setStatus(BigDecimal status) {
        this.status = status;
    }

    public BigDecimal getSurchargeFee() {
        return this.surchargeFee;
    }

    public void setSurchargeFee(BigDecimal surchargeFee) {
        this.surchargeFee = surchargeFee;
    }

    public BigDecimal getType() {
        return this.type;
    }

    public void setType(BigDecimal type) {
        this.type = type;
    }

    public Timestamp getUtimestamp() {
        return this.utimestamp;
    }

    public void setUtimestamp(Timestamp utimestamp) {
        this.utimestamp = utimestamp;
    }

    public BigDecimal getWeight() {
        return this.weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getWidth() {
        return this.width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

}