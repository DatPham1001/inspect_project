package com.imedia.service.userwallet.model;

import java.util.List;

public class FilterWalletLogResponse {
    private Integer total;
    private Integer page;
    private Integer size;
    private List<FilterWalletLogDataResponse> logs;

    public FilterWalletLogResponse(Integer total, Integer page, Integer size, List<FilterWalletLogDataResponse> logs) {
        this.total = total;
        this.page = page;
        this.size = size;
        this.logs = logs;
    }

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

    public List<FilterWalletLogDataResponse> getLogs() {
        return logs;
    }

    public void setLogs(List<FilterWalletLogDataResponse> logs) {
        this.logs = logs;
    }
}
