package com.bstek.ureport.console.ureport.model;

import com.bstek.ureport.definition.*;
import com.bstek.ureport.definition.datasource.DatasourceDefinition;
import com.bstek.ureport.definition.searchform.SearchForm;
import lombok.Data;

import java.util.List;

@Data
public class ReportInitVo {
    private Paper paper;
    private CellDefinition rootCell;
    private HeaderFooterDefinition header;
    private HeaderFooterDefinition footer;
    private SearchForm searchForm;
    private List<CellDefinition> cellsMap;
    private List<RowDefinition> rows;
    private List<ColumnDefinition> columns;
    private List<DatasourceDefinition> datasources;
    private String searchFormXml;
}
