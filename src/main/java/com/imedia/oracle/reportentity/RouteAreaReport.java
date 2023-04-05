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
 * The persistent class for the ROUTE_AREAS database table.
 */
@Entity
@Getter
@Setter
@Table(name = "ROUTE_AREAS")
public class RouteAreaReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    @Column(name = "AREA_CODE")
    private String areaCode;

    @Column(name = "AREA_ID")
    private Long areaId;

    @Column(name = "AREA_TYPE")
    private Integer areaType;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "IS_DELETED")
    private Integer isDeleted;

    @Column(name = "ROUTE_CODE")
    private String routeCode;

    @Column(name = "ROUTE_ID")
    private Long routeId;

    private Timestamp utimestamp;


}