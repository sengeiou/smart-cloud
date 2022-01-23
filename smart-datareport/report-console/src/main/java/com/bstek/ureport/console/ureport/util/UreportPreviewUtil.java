package com.bstek.ureport.console.ureport.util;

import com.bstek.ureport.build.Context;
import com.bstek.ureport.build.ReportBuilder;
import com.bstek.ureport.build.paging.Page;
import com.bstek.ureport.console.cache.TempObjectCache;
import com.bstek.ureport.console.ureport.entity.ReportEntity;
import com.bstek.ureport.console.ureport.model.ReportPreviewVO;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.export.PageBuilder;
import com.bstek.ureport.export.SinglePageData;
import com.bstek.ureport.export.html.HtmlProducer;
import com.bstek.ureport.export.html.HtmlReport;
import com.bstek.ureport.model.Report;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UreportPreviewUtil {

    private ReportBuilder reportBuilder = new ReportBuilder();
    private HtmlProducer htmlProducer = new HtmlProducer();

    public ReportPreviewVO priviews(Map<String, Object> map, boolean page, int index, Connection connection) {
        ReportPreviewVO vo = new ReportPreviewVO();
        if (map != null) {
            byte[] content = map.get("F_Content").toString().getBytes();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
            ReportDefinition reportDefinition = UreportUtil.parseReport(inputStream, map.get("F_FullName").toString());
            Map<String, Object> parameters = new HashMap<>();
            Report report = reportBuilder.buildReports(reportDefinition, parameters,connection);
            HtmlReport htmlReport = loadReport(report, page, index);
            htmlReport.setStyle(reportDefinition.getStyle());
            htmlReport.setSearchFormData(reportDefinition.buildSearchFormData(report.getContext().getDatasetMap(), parameters));
            vo.setContent(htmlReport.getContent());
            vo.setPageIndex(htmlReport.getPageIndex());
            vo.setStyle(htmlReport.getStyle());
            vo.setTotalPage(htmlReport.getTotalPage());
            TempObjectCache.putObject("p",reportDefinition);
        }
        return vo;
    }

    public ReportPreviewVO priview(ReportEntity entity, boolean page, int index, Connection connection) {
        ReportPreviewVO vo = new ReportPreviewVO();
        if (entity != null) {
            byte[] content = entity.getContent().getBytes();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
            ReportDefinition reportDefinition = UreportUtil.parseReport(inputStream, entity.getFullName());
            TempObjectCache.putObject(entity.getId(),reportDefinition);
            Map<String, Object> parameters = new HashMap<>();
            Report report = reportBuilder.buildReports(reportDefinition, parameters,connection);
            HtmlReport htmlReport = loadReport(report, page, index);
            htmlReport.setStyle(reportDefinition.getStyle());
            htmlReport.setSearchFormData(reportDefinition.buildSearchFormData(report.getContext().getDatasetMap(), parameters));
            vo.setContent(htmlReport.getContent());
            vo.setPageIndex(htmlReport.getPageIndex());
            vo.setStyle(htmlReport.getStyle());
            vo.setTotalPage(htmlReport.getTotalPage());
        }
        return vo;
    }

    public HtmlReport loadReport(Report report, boolean page, int index) {
        HtmlReport htmlReport = new HtmlReport();
        String html = null;
        if (page) {
            SinglePageData pageData = PageBuilder.buildSinglePageData(index, report);
            html = this.produce(report, pageData);
            htmlReport.setContent(html);
            htmlReport.setTotalPage(pageData.getTotalPages());
            htmlReport.setPageIndex(index);
        } else {
            html = htmlProducer.produce(report);
            htmlReport.setContent(html);
        }
        if (report.getPaper().isColumnEnabled()) {
            htmlReport.setColumn(report.getPaper().getColumnCount());
        }
        htmlReport.setChartDatas(report.getContext().getChartDataMap().values());
        htmlReport.setTotalPage(report.getPages().size());
        htmlReport.setReportAlign(report.getPaper().getHtmlReportAlign().name());
        htmlReport.setHtmlIntervalRefreshValue(report.getPaper().getHtmlIntervalRefreshValue());
        return htmlReport;
    }

    //分页
    private String produce(Report report, SinglePageData pageData) {
        Context context = report.getContext();
        List<Page> pages = pageData.getPages();
        String html = null;
        if (pages.size() == 1) {
            Page page = pages.get(0);
            html = htmlProducer.produce(context, page, false);
        } else {
            html = htmlProducer.produce(context, pages, pageData.getColumnMargin(), false);
        }
        return html;
    }

}
