package com.raymond.db.utils;

import java.util.List;

/**
 * 接口分页对象
 *
 * @author :  raymond
 * @version :  V1.0
 * @date :  2019-12-09 11:44
 */
public class PageInfo<T> {

    private int pageSize = 15;

    private int total;

    private int pageIndex = 1;

    private int pageTotal;

    private List<T> records;

    public PageInfo() {
    }

    public PageInfo(List<T> records) {
        this.records = records;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}
