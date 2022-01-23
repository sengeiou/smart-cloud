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
package com.bstek.ureport.console.html;

import com.bstek.ureport.build.Context;
import com.bstek.ureport.build.ReportBuilder;
import com.bstek.ureport.build.paging.Page;
import com.bstek.ureport.cache.CacheUtils;
import com.bstek.ureport.chart.ChartData;
import com.bstek.ureport.console.MobileUtils;
import com.bstek.ureport.console.RenderPageServletAction;
import com.bstek.ureport.console.cache.TempObjectCache;
import com.bstek.ureport.console.config.DataSourceConfig;
import com.bstek.ureport.console.exception.ReportDesignException;
import com.bstek.ureport.console.ureport.entity.ReportEntity;
import com.bstek.ureport.console.ureport.model.ReportPreviewVO;
import com.bstek.ureport.console.ureport.service.ReportService;
import com.bstek.ureport.console.ureport.util.UreportPreviewUtil;
import com.bstek.ureport.console.ureport.util.UreportUtil;
import com.bstek.ureport.console.util.ActionResult;
import com.bstek.ureport.console.util.JdbcUtil;
import com.bstek.ureport.definition.Paper;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.definition.searchform.FormPosition;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.export.*;
import com.bstek.ureport.export.html.HtmlProducer;
import com.bstek.ureport.export.html.HtmlReport;
import com.bstek.ureport.export.html.SearchFormData;
import com.bstek.ureport.model.Report;
import smart.util.DataSourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.*;

/**
 * @author
 * @since 2月15日
 */
@Slf4j
public class HtmlPreviewServletAction extends RenderPageServletAction {
    @Autowired
    private DataSourceConfig dataSourceConfig;
    @Autowired
    private DataSourceUtil dataSourceUtil;

    @Autowired
    private ReportService reportService;
    private ExportManager exportManager;
    private ReportBuilder reportBuilder;
    private ReportRender reportRender;
    private HtmlProducer htmlProducer = new HtmlProducer();

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = retriveMethod(req);
        if (method != null) {
            invokeMethod(method, req, resp);
        } else {
            Map<String, Object> objectMap = new HashMap<String, Object>();
            VelocityContext context = new VelocityContext();
            HtmlReport htmlReport = null;
            String errorMsg = null;
            try {
                htmlReport = loadReport(req);
            } catch (Exception ex) {
                if (!(ex instanceof ReportDesignException)) {
                    ex.printStackTrace();
                }
                errorMsg = buildExceptionMessage(ex);
            }
            String title = buildTitle(req);
            context.put("title", title);
            if (htmlReport == null) {
                context.put("content", "<div style='color:red'><strong>报表计算出错，错误信息如下：</strong><br><div style=\"margin:10px\">" + errorMsg + "</div></div>");
                context.put("error", true);
                context.put("searchFormJs", "");
                context.put("downSearchFormHtml", "");
                context.put("upSearchFormHtml", "");
            } else {
                SearchFormData formData = htmlReport.getSearchFormData();
                if (formData != null) {
                    context.put("searchFormJs", formData.getJs());
                    if (formData.getFormPosition().equals(FormPosition.up)) {
                        context.put("upSearchFormHtml", formData.getHtml());
                        context.put("downSearchFormHtml", "");
                    } else {
                        context.put("downSearchFormHtml", formData.getHtml());
                        context.put("upSearchFormHtml", "");
                    }
                } else {
                    context.put("searchFormJs", "");
                    context.put("downSearchFormHtml", "");
                    context.put("upSearchFormHtml", "");
                }
                objectMap.put("content", htmlReport.getContent());
                objectMap.put("style", htmlReport.getStyle());
                context.put("content", htmlReport.getContent());
                context.put("style", htmlReport.getStyle());
                context.put("reportAlign", htmlReport.getReportAlign());
                context.put("totalPage", htmlReport.getTotalPage());
                context.put("totalPageWithCol", htmlReport.getTotalPageWithCol());
                context.put("pageIndex", htmlReport.getPageIndex());
                context.put("chartDatas", convertJson(htmlReport.getChartDatas()));
                context.put("error", false);
                context.put("file", req.getParameter("_u"));
                context.put("intervalRefreshValue", htmlReport.getHtmlIntervalRefreshValue());
                String customParameters = buildCustomParameters(req);
                context.put("customParameters", customParameters);
                context.put("_t", "");
                Tools tools = null;
                if (MobileUtils.isMobile(req)) {
                    tools = new Tools(false);
                    tools.setShow(false);
                } else {
                    String toolsInfo = req.getParameter("_t");
                    if (StringUtils.isNotBlank(toolsInfo)) {
                        tools = new Tools(false);
                        if (toolsInfo.equals("0")) {
                            tools.setShow(false);
                        } else {
                            String[] infos = toolsInfo.split(",");
                            for (String name : infos) {
                                tools.doInit(name);
                            }
                        }
                        context.put("_t", toolsInfo);
                        context.put("hasTools", true);
                    } else {
                        tools = new Tools(true);
                    }
                }
                context.put("tools", tools);
            }
            context.put("contextPath", req.getContextPath());
            writeObjectToJson(resp, objectMap);
        }
    }

    private String buildTitle(HttpServletRequest req) {
        String title = req.getParameter("_title");
        if (StringUtils.isBlank(title)) {
            title = req.getParameter("_u");
            title = decode(title);
            int point = title.lastIndexOf(".ureport.xml");
            if (point > -1) {
                title = title.substring(0, point);
            }
            if (title.equals("p")) {
                title = "设计中报表";
            }
        } else {
            title = decode(title);
        }
        return title + "-ureport";
    }

    private String convertJson(Collection<ChartData> data) {
        if (data == null || data.size() == 0) {
            return "";
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(data);
            return json;
        } catch (Exception e) {
            throw new ReportComputeException(e);
        }
    }

    public void loadData(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HtmlReport htmlReport = loadReport(req);
        writeObjectToJson(resp, htmlReport);
    }

    //展示内容
    public void loadPrintPages(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        id = decode(id);
        String token = req.getParameter("token");
        token = decode(token);
        if (StringUtils.isBlank(id) || StringUtils.isBlank(token)) {
            writeObjectToJson(resp, ActionResult.fail("请检查参数"));
        }
        if ("preview".equals(id)) {
            ReportDefinition reportDefinition = (ReportDefinition) TempObjectCache.getObject(token);
            Connection connection = null;
            try {
                connection = JdbcUtil.getConnection(dataSourceConfig.getMultiTenancy(),dataSourceUtil);
            } catch (Exception throwables) {
                log.error("数据源错误："+throwables.getMessage());
            }
            Map<String, Object> parameters = new HashMap<>();
            Report report = reportBuilder.buildReports(reportDefinition, parameters, connection);

            Map<String, ChartData> chartMap = report.getContext().getChartDataMap();
            if (chartMap.size() > 0) {
                CacheUtils.storeChartDataMap(chartMap);
            }
            FullPageData pageData = PageBuilder.buildFullPageData(report);
            StringBuilder sb = new StringBuilder();
            List<List<Page>> list = pageData.getPageList();
            Context context = report.getContext();
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    List<Page> columnPages = list.get(i);
                    if (i == 0) {
                        String html = htmlProducer.produce(context, columnPages, pageData.getColumnMargin(), false);
                        sb.append(html);
                    } else {
                        String html = htmlProducer.produce(context, columnPages, pageData.getColumnMargin(), false);
                        sb.append(html);
                    }
                }
            } else {
                List<Page> pages = report.getPages();
                for (int i = 0; i < pages.size(); i++) {
                    Page page = pages.get(i);
                    if (i == 0) {
                        String html = htmlProducer.produce(context, page, false);
                        sb.append(html);
                    } else {
                        String html = htmlProducer.produce(context, page, true);
                        sb.append(html);
                    }
                }
            }
            Object obj = sb;
            writeObjectToJson(resp, ActionResult.success(obj));
        } else {
            ReportEntity entity = reportService.GetInfo(id);
            if (entity == null) {
                writeObjectToJson(resp, ActionResult.fail("数据不存在"));
            } else {
                ReportPreviewVO vo = null;
                UreportPreviewUtil previewUtil = new UreportPreviewUtil();
                Connection connection = null;
                try {
                    connection = JdbcUtil.getConnection(dataSourceConfig.getMultiTenancy(),dataSourceUtil);
                } catch (Exception throwables) {
                    log.error("数据源错误："+throwables.getMessage());
                }
                vo = previewUtil.priview(entity, false, 1, connection);
                Object obj = vo.getContent();
                writeObjectToJson(resp, ActionResult.success(obj));
            }
        }
    }

    public void loadPagePaper(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        id = decode(id);
        String token = req.getParameter("token");
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
            Paper paper = report.getPaper();
            writeObjectToJson(resp, paper);
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
                writeObjectToJson(resp, report.getPaper());
            }
        }
    }

    private HtmlReport loadReport(HttpServletRequest req) {
        Map<String, Object> parameters = buildParameters(req);
        HtmlReport htmlReport = null;
        String file = req.getParameter("_u");
        file = decode(file);
        String pageIndex = req.getParameter("_i");
        if (StringUtils.isBlank(file)) {
            throw new ReportComputeException("Report file can not be null.");
        }
        if (file.equals(PREVIEW_KEY)) {
            ReportDefinition reportDefinition = (ReportDefinition) TempObjectCache.getObject(PREVIEW_KEY);
            if (reportDefinition == null) {
                throw new ReportDesignException("Report data has expired,can not do preview.");
            }
            Report report = reportBuilder.buildReport(reportDefinition, parameters);
            Map<String, ChartData> chartMap = report.getContext().getChartDataMap();
            if (chartMap.size() > 0) {
                CacheUtils.storeChartDataMap(chartMap);
            }
            htmlReport = new HtmlReport();
            String html = null;
            if (StringUtils.isNotBlank(pageIndex) && !pageIndex.equals("0")) {
                Context context = report.getContext();
                int index = Integer.valueOf(pageIndex);
                SinglePageData pageData = PageBuilder.buildSinglePageData(index, report);
                List<Page> pages = pageData.getPages();
                if (pages.size() == 1) {
                    Page page = pages.get(0);
                    html = htmlProducer.produce(context, page, false);
                } else {
                    html = htmlProducer.produce(context, pages, pageData.getColumnMargin(), false);
                }
                htmlReport.setTotalPage(pageData.getTotalPages());
                htmlReport.setPageIndex(index);
            } else {
                html = htmlProducer.produce(report);
            }
            if (report.getPaper().isColumnEnabled()) {
                htmlReport.setColumn(report.getPaper().getColumnCount());
            }
            htmlReport.setChartDatas(report.getContext().getChartDataMap().values());
            htmlReport.setContent(html);
            htmlReport.setTotalPage(report.getPages().size());
            htmlReport.setStyle(reportDefinition.getStyle());
            htmlReport.setSearchFormData(reportDefinition.buildSearchFormData(report.getContext().getDatasetMap(), parameters));
            htmlReport.setReportAlign(report.getPaper().getHtmlReportAlign().name());
            htmlReport.setHtmlIntervalRefreshValue(report.getPaper().getHtmlIntervalRefreshValue());
        } else {
            if (StringUtils.isNotBlank(pageIndex) && !pageIndex.equals("0")) {
                int index = Integer.valueOf(pageIndex);
                htmlReport = exportManager.exportHtml(file, req.getContextPath(), parameters, index);
            } else {
                htmlReport = exportManager.exportHtml(file, req.getContextPath(), parameters);
            }
        }
        return htmlReport;
    }


    private String buildCustomParameters(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        Enumeration<?> enumeration = req.getParameterNames();
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            if (obj == null) {
                continue;
            }
            String name = obj.toString();
            String value = req.getParameter(name);
            if (name == null || value == null || (name.startsWith("_") && !name.equals("_n"))) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(name);
            sb.append("=");
            sb.append(value);
        }
        return sb.toString();
    }

    private String buildExceptionMessage(Throwable throwable) {
        Throwable root = buildRootException(throwable);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        root.printStackTrace(pw);
        String trace = sw.getBuffer().toString();
        trace = trace.replaceAll("\n", "<br>");
        pw.close();
        return trace;
    }

    public void setExportManager(ExportManager exportManager) {
        this.exportManager = exportManager;
    }

    public void setReportBuilder(ReportBuilder reportBuilder) {
        this.reportBuilder = reportBuilder;
    }

    public void setReportRender(ReportRender reportRender) {
        this.reportRender = reportRender;
    }

    //数据源连接
//    private Connection buildConnection() {
//        HttpServletResponse resp = null;
//        Connection conn = null;
//        DataConfigEntity dataConfigEntity = dataConfigService.GetInfo();
//        Map<String, Object> map = JSONUtil.StringToMap(JSONUtil.StringToMap(dataConfigEntity.getDatasource()).get("dataSource").toString());
//        String username = map.get("username").toString();
//        String password = map.get("password").toString();
//        String driver = map.get("driver").toString();
//        String url = map.get("url").toString();
//        try {
//            Class.forName(driver);
//            conn = DriverManager.getConnection(url, username, password);
//        } catch (Exception e) {
//            try {
//                writeObjectToJson(resp, ActionResult.fail("请先配置正确的数据源"));
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//        return conn;
//    }

    @Override
    public String url() {
        return "/preview";
    }
}
