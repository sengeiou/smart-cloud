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
package com.bstek.ureport.console.pdf;

import com.bstek.ureport.build.ReportBuilder;
import com.bstek.ureport.console.BaseServletAction;
import com.bstek.ureport.console.cache.TempObjectCache;
import com.bstek.ureport.console.config.DataSourceConfig;
import com.bstek.ureport.console.exception.ReportDesignException;
import com.bstek.ureport.console.ureport.entity.ReportEntity;
import com.bstek.ureport.console.ureport.service.ReportService;
import com.bstek.ureport.console.ureport.util.UreportUtil;
import com.bstek.ureport.console.util.ActionResult;
import com.bstek.ureport.console.util.JdbcUtil;
import com.bstek.ureport.definition.Paper;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.export.ExportManager;
import com.bstek.ureport.export.ReportRender;
import com.bstek.ureport.export.pdf.PdfProducer;
import com.bstek.ureport.model.Report;
import smart.util.DataSourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 导出报表到PDF
 * @author
 * @since 3月20日
 */
@Slf4j
public class ExportPdfServletAction extends BaseServletAction {
    @Autowired
    private ReportService reportService;
    @Autowired
    private DataSourceConfig dataSourceConfig;
    @Autowired
    private DataSourceUtil dataSourceUtil;

    private ReportBuilder reportBuilder;
    private ExportManager exportManager;
    private ReportRender reportRender;
    private PdfProducer pdfProducer = new PdfProducer();

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = retriveMethod(req);
        if (method != null) {
            invokeMethod(method, req, resp);
        } else {
            buildPdf(req, resp, false);
        }
    }

    public void show(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        buildPdf(req, resp, true);
    }

    //临时预览时候的导出pdf
    public void buildPdf(HttpServletRequest req, HttpServletResponse resp, boolean forPrint) throws IOException {
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
            outputStream = resp.getOutputStream();
            Map<String, Object> parameters = buildParameters(req);
            if (reportDefinition == null) {
                throw new ReportDesignException("Report data has expired,can not do export pdf.");
            }
            if (forPrint) {
                Connection connection = null;
                try {
                    connection = JdbcUtil.getConnection(dataSourceConfig.getMultiTenancy(),dataSourceUtil);
                }catch (Exception e){

                }
                Report report = reportBuilder.buildReportToConnection(reportDefinition, parameters, connection);
                pdfProducer.produce(report, outputStream);
            } else {
                resp.setCharacterEncoding("UTF-8");
                resp.setHeader("content-Type", "application/x-download");
                resp.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fullName + ".pdf", "UTF-8"));
                Connection connection = null;
                try {
                    connection = JdbcUtil.getConnection(dataSourceConfig.getMultiTenancy(),dataSourceUtil);
                }catch (Exception e){

                }
                Report report = reportBuilder.buildReportToConnection(reportDefinition, parameters, connection);
                pdfProducer.produce(report, outputStream);
            }
        } catch (Exception ex) {
            writeObjectToJson(resp, ActionResult.fail("请检查参数"));
        } finally {
                outputStream.flush();
                outputStream.close();
        }
    }

    public void newPaging(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        id = decode(id);
        String token = req.getHeader("Authorization");
        token = decode(token);
        if (StringUtils.isBlank(id) || StringUtils.isBlank(token)) {
            writeObjectToJson(resp, ActionResult.fail("请检查参数"));
        }
        if ("preview".equals(id)) {
            ReportDefinition reportDefinition = (ReportDefinition) TempObjectCache.getObject(token);
            Map<String, Object> parameters = buildParameters(req);
            Connection connection = null;
            try {
                connection = JdbcUtil.getConnection(dataSourceConfig.getMultiTenancy(),dataSourceUtil);
            } catch (Exception throwables) {
                log.error("数据源错误："+throwables.getMessage());
            }
            Report report = reportBuilder.buildReportToConnection(reportDefinition, parameters, connection);
            String paper = req.getParameter("_paper");
            ObjectMapper mapper = new ObjectMapper();
            Paper newPaper = mapper.readValue(paper, Paper.class);
            report.rePaging(newPaper);
            writeObjectToJson(resp, report.getPaper());
        } else {
            ReportEntity entity = reportService.GetInfo(id);
            if (entity == null) {
                writeObjectToJson(resp, ActionResult.fail("数据不存在"));
            } else {
                Connection connection = null;
                try {
                    connection = JdbcUtil.getConnection(dataSourceConfig.getMultiTenancy(),dataSourceUtil);
                } catch (Exception throwables) {
                    log.error("数据源错误："+throwables.getMessage());
                }
                byte[] content = entity.getContent().getBytes();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
                ReportDefinition reportDefinition = UreportUtil.parseReport(inputStream, entity.getFullName());
                Map<String, Object> parameters = new HashMap<>();
                Report report = reportBuilder.buildReportToConnection(reportDefinition, parameters, connection);
                //获取
                String paper = req.getParameter("_paper");
                ObjectMapper mapper = new ObjectMapper();
                Paper newPaper = mapper.readValue(paper, Paper.class);
                report.rePaging(newPaper);
                writeObjectToJson(resp, report.getPaper());
            }
        }
    }

    public void setReportRender(ReportRender reportRender) {
        this.reportRender = reportRender;
    }

    public void setExportManager(ExportManager exportManager) {
        this.exportManager = exportManager;
    }

    public void setReportBuilder(ReportBuilder reportBuilder) {
        this.reportBuilder = reportBuilder;
    }

    @Override
    public String url() {
        return "/pdf";
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
