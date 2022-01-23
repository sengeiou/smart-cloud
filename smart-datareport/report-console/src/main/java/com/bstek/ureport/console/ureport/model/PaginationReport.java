package com.bstek.ureport.console.ureport.model;

import com.bstek.ureport.console.util.Pagination;
import lombok.Data;

@Data
public class PaginationReport extends Pagination {
    private String categoryId;
}
