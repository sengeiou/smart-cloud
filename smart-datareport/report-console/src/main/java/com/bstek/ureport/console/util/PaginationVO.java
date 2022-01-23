package com.bstek.ureport.console.util;

import lombok.Data;

@Data
public class PaginationVO {
    private long currentPage;
    private long pageSize;
    private int total;
}
