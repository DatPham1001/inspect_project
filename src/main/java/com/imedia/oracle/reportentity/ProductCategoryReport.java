package com.imedia.oracle.reportentity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * The persistent class for the PRODUCT_CATEGORYS database table.
 */
@Entity
@Table(name = "PRODUCT_CATEGORYS")
@Getter
@Setter
public class ProductCategoryReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    private String name;

    private String status;


}