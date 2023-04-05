package com.imedia.oracle.reportentity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the ROUTES database table.
 */
@Entity
@Getter
@Setter
@Table(name = "ROUTES")
public class RoutesReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(unique = true, nullable = false)
    private long id;

    @Column(length = 50)
    private String code;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(length = 255)
    private String name;

    @Column(nullable = false)
    private Integer status;

    @Column(name = "STORE_ID", nullable = false)
    private Integer storeId;

    @Column(name = "TYPE", nullable = false)
    private Integer type;

    public RoutesReport() {
    }
}