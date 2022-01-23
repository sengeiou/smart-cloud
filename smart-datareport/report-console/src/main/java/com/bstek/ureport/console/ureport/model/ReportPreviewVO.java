package com.bstek.ureport.console.ureport.model;

import com.bstek.ureport.chart.ChartData;
import com.bstek.ureport.export.html.SearchFormData;
import lombok.Data;

import java.util.Collection;

@Data
public class ReportPreviewVO {
    private String content;
    private String style;
    private int totalPage;
    private int pageIndex;
    private int column;
    private String reportAlign;
    private Collection<ChartData> chartDatas;
    private int htmlIntervalRefreshValue;
    private SearchFormData searchFormData;
}
