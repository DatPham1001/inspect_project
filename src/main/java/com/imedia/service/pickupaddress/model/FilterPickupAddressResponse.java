package com.imedia.service.pickupaddress.model;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class FilterPickupAddressResponse {
    private Integer total;
    private Integer page;
    private Integer size;
    private List<ShopAddressDTO> shopAddress;


    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<ShopAddressDTO> getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(List<ShopAddressDTO> shopAddress) {
        this.shopAddress = shopAddress;
    }
}
