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
package com.bstek.ureport.console.excel;

import com.bstek.ureport.build.ReportBuilder;
import com.bstek.ureport.console.BaseServletAction;
import com.bstek.ureport.console.cache.TempObjectCache;
import com.bstek.ureport.console.config.DataSourceConfig;
import com.bstek.ureport.console.ureport.service.ReportService;
import com.bstek.ureport.console.util.JdbcUtil;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.export.ExportManager;
import com.bstek.ureport.export.excel.high.ExcelProducer;
import com.bstek.ureport.model.Report;
import smart.util.DataSourceUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 导出报表到Excel
 * @author
 * @since 4月17日
 */
public class ExportExcelServletAction extends BaseServletAction {
    @Autowired
    private ReportService reportService;
    @Autowired
    private DataSourceConfig dataSourceConfig;
    private ReportBuilder reportBuilder;
    private ExportManager exportManager;
    private ExcelProducer excelProducer = new ExcelProducer();
    private DataSourceUtil dataSourceUtil;

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = retriveMethod(req);
        if (method != null) {
            invokeMethod(method, req, resp);
        } else {
            buildExcel(req, resp, false, false);
        }
    }

    public void paging(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        buildExcel(req, resp, true, false);
    }

    public void sheet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        buildExcel(req, resp, false, true);
    }

    public void buildExcel(HttpServletRequest req, HttpServletResponse resp, boolean withPage, boolean withSheet) throws IOException {
        String token = req.getParameter("token");
        token = decode(token);
        String id = req.getParameter("id");
        id = decode(id);
        if (StringUtils.isBlank(token) || StringUtils.isBlank(id)) {
            throw new ReportComputeException("Report file can not be null.");
        }
        OutputStream outputStream = null;
        try {
            String fullName = null;
            ReportDefinition reportDefinition = (ReportDefinition) TempObjectCache.getObject(id);
            if (reportDefinition == null) {
                reportDefinition = (ReportDefinition) TempObjectCache.getObject(token);
                fullName = String.valueOf(UUID.randomUUID());
            } else {
                fullName = reportService.GetInfo(id).getFullName();
            }
            resp.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fullName + ".xlsx", "UTF-8"));
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("content-Type", "application/x-download");
            outputStream = resp.getOutputStream();
            Map<String, Object> parameters = new HashMap<>();
            Connection connection = JdbcUtil.getConnection(dataSourceConfig.getMultiTenancy(),dataSourceUtil);
            Report report = reportBuilder.buildReportToConnection(reportDefinition, parameters, connection);
            SXSSFWorkbook workbook = null;
            if (withPage) {
                excelProducer.produceWithPaging(report, outputStream);
            } else if (withSheet) {
                excelProducer.produceWithSheet(report, outputStream);
            } else {
                excelProducer.produce(report, outputStream);
            }
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }

    public void setReportBuilder(ReportBuilder reportBuilder) {
        this.reportBuilder = reportBuilder;
    }

    public void setExportManager(ExportManager exportManager) {
        this.exportManager = exportManager;
    }

    @Override
    public String url() {
        return "/excel";
    }

    protected void writeObjectToJson(HttpServletResponse resp, Object obj) throws IOException {
        resp.setContentType("text/json");
        resp.setCharacterEncoding("UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        OutputStream out = resp.getOutputStream();
        try {
            mapper.writeValue(out, obj);
        } finally {
            out.flush();
            out.close();
        }
    }
}
