/*******************************************************************************
 * Copyright 2017 Bstek
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.bstek.ureport.console.designer;

import com.bstek.ureport.definition.*;
import com.bstek.ureport.definition.datasource.DatasourceDefinition;
import com.bstek.ureport.definition.searchform.SearchForm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 * @since 1月29日
 */
public class ReportDefinitionWrapper implements Serializable {
	private Paper paper;
	private HeaderFooterDefinition header;
	private HeaderFooterDefinition footer;
	private SearchForm searchForm;
	private String searchFormXml;
	private List<RowDefinition> rows;
	private List<ColumnDefinition> columns;
	private List<DatasourceDefinition> datasources;
	private Map<String,CellDefinition> cellsMap=new HashMap<String,CellDefinition>();
	public ReportDefinitionWrapper(ReportDefinition report) {
		this.paper=report.getPaper();
		this.header=report.getHeader();
		this.footer=report.getFooter();
		this.searchForm=report.getSearchForm();
		this.searchFormXml=report.getSearchFormXml();
		this.rows=report.getRows();
		this.columns=report.getColumns();
		this.datasources=report.getDatasources();
		for(CellDefinition cell:report.getCells()){
			cellsMap.put(cell.getRowNumber()+","+cell.getColumnNumber(), cell);
		}
	}

	public Paper getPaper() {
		return paper;
	}

	public void setPaper(Paper paper) {
		this.paper = paper;
	}

	public HeaderFooterDefinition getHeader() {
		return header;
	}

	public void setHeader(HeaderFooterDefinition header) {
		this.header = header;
	}

	public HeaderFooterDefinition getFooter() {
		return footer;
	}

	public void setFooter(HeaderFooterDefinition footer) {
		this.footer = footer;
	}

	public SearchForm getSearchForm() {
		return searchForm;
	}

	public void setSearchForm(SearchForm searchForm) {
		this.searchForm = searchForm;
	}

	public String getSearchFormXml() {
		return searchFormXml;
	}

	public void setSearchFormXml(String searchFormXml) {
		this.searchFormXml = searchFormXml;
	}

	public List<RowDefinition> getRows() {
		return rows;
	}

	public void setRows(List<RowDefinition> rows) {
		this.rows = rows;
	}

	public List<ColumnDefinition> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnDefinition> columns) {
		this.columns = columns;
	}

	public List<DatasourceDefinition> getDatasources() {
		return datasources;
	}

	public void setDatasources(List<DatasourceDefinition> datasources) {
		this.datasources = datasources;
	}

	public Map<String, CellDefinition> getCellsMap() {
		return cellsMap;
	}

	public void setCellsMap(Map<String, CellDefinition> cellsMap) {
		this.cellsMap = cellsMap;
	}
}
