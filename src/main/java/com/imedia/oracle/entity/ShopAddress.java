package com.imedia.oracle.entity;

import com.imedia.service.pickupaddress.model.ShopAddressDTO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * The persistent class for the SHOP_ADDRESS database table.
 */
@Entity
@Table(name = "SHOP_ADDRESS")
@SqlResultSetMapping(
        name = "PickupAddressMapping",
        classes = {
                @ConstructorResult(
                        targetClass = ShopAddressDTO.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "shop_id", type = Long.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "phone", type = String.class),
                                @ColumnResult(name = "sender_name", type = String.class),
                                @ColumnResult(name = "address", type = String.class),
                                @ColumnResult(name = "province_code", type = String.class),
                                @ColumnResult(name = "district_id", type = Integer.class),
                                @ColumnResult(name = "ward_id", type = Integer.class),
                                @ColumnResult(name = "provinceName", type = String.class),
                                @ColumnResult(name = "districtName", type = String.class),
                                @ColumnResult(name = "wardName", type = String.class),
                                @ColumnResult(name = "status", type = Integer.class),
                                @ColumnResult(name = "is_default", type = Integer.class),
                        }
                )
        }
)
@Getter
@Setter
public class ShopAddress implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "SHOP_ADDRESS_SEQ", sequenceName = "SHOP_ADDRESS_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SHOP_ADDRESS_SEQ")
    @Column(name = "ID")
    private long id;

    private String address;

    @Column(name = "ADDRESS_ALIAS")
    private String addressAlias;


    @Column(name = "ADDRESS_ID")
    private Integer addressId;

    @Column(name = "DISTRICT_ID")
    private Integer districtId;

    private Double latitude;

    private Double longitude;

    private String name;
    @Column(name = "SENDER_NAME")
    private String senderName;

    private String phone;

    @Column(name = "PROVINCE_ID")
    private Integer provinceId;

    @Column(name = "SHOP_ID")
    private Long shopId;

    private Integer status;

    @Column(name = "WARD_ID")
    private Integer wardId;
    @Column(name = "PROVINCE_CODE")
    private String provinceCode;
    @Column(name = "IS_DEFAULT")
    private Integer isDefault;
    @CreationTimestamp
    @Column(name = "CREATE_DATE", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @UpdateTimestamp
    @Column(name = "UTIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date utimestamp;

    public ShopAddress() {
    }

}