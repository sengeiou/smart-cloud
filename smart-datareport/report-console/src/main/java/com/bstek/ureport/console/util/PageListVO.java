package com.bstek.ureport.console.util;

import lombok.Data;

import java.util.List;

@Data
public class PageListVO<T> {
    private List<T> list;
    PaginationVO pagination;

}
