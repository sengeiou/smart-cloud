package com.bstek.ureport.console.ureport.controller;

import com.bstek.ureport.build.ReportBuilder;
import com.bstek.ureport.console.BaseServletAction;
import com.bstek.ureport.console.cache.TempObjectCache;
import com.bstek.ureport.console.config.DataSourceConfig;
import com.bstek.ureport.console.designer.ReportDefinitionWrapper;
import com.bstek.ureport.console.ureport.entity.ReportEntity;
import com.bstek.ureport.console.ureport.model.*;
import com.bstek.ureport.console.ureport.service.ReportService;
import com.bstek.ureport.console.ureport.util.*;
import com.bstek.ureport.console.util.*;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.export.ReportRender;
import com.bstek.ureport.export.html.HtmlReport;
import com.bstek.ureport.model.Report;
import smart.util.jwt.JwtUtil;
import smart.permission.UsersApi;
import smart.permission.model.user.UserInfoVO;
import smart.util.DataSourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
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
import java.util.List;
import java.util.Map;

/**
 * 核心控制层，大部分功能
 */
@Slf4j
public class DataReportController extends BaseServletAction {
    @Autowired
    private DataSourceConfig dataSourceConfig;
    @Autowired
    private ReportService reportService;
    @Autowired
    private DataSourceUtil dataSourceUtil;
    @Autowired
    private ReportRender reportRender;
    @Autowired
    private UsersApi usersApi;
    @Autowired
    private UpdateData updateData;

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = retriveMethod(req);
        String method = req.getMethod();
        if (method.equals("POST")) {
            if (url == null) {
                savaData(req, resp);
            } else {
                writeObjectToJson(resp, ActionResult.fail("找不到接口"));
            }
        }
        if (method.equals("PUT")) {
//            int strStartIndex = url.indexOf("/");
//            if (strStartIndex < 0) {
//                writeObjectToJson(resp, ActionResult.fail("数据不存在"));
//            }
//            String id = url.substring(strStartIndex + 1);
            updateData(req, resp, url);
        }
        if (method.equals("DELETE")) {
//            int strStartIndex = url.indexOf("/");
//            if (strStartIndex < 0) {
//                writeObjectToJson(resp, ActionResult.fail("数据不存在"));
//            } else {
//                String id = url.substring(strStartIndex + 1);
                deleteData(req, resp, url);
//            }
        }
        if (method.equals("GET")) {
            if (url == null) {
                getList(req, resp);
            }else if (url.equals("init")) {
                init(req, resp);
            }else if (url.equals("Selector")) {
                Selector(req, resp);
            }else if (url.equals("preview")) {
                previewData(req, resp);
            }else if (url.contains("/Actions/Export/")) {
                //截取Id
                String id = url.split("/")[0];
                //截取type
                String type = url.split("/")[3];
                exportData(req, resp, id, type);
            }
            //打开报表
            //if (!url.contains("/Actions/Export/") && !url.equals("DataReport/init") && !url.equals("DataReport/Selector") && !url.contains("/preview"))
            else {
                getInfo(req, resp, url);
            }
        }
    }

    //初始化
    public void init(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ReportDefinition reportDef = reportRender.parseReport("classpath:template/template.ureport.xml");
        writeObjectToJson(resp, ActionResult.success(new ReportDefinitionWrapper(reportDef)));
    }

    //列表
    public void getList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ReportEntity> data = reportService.GetList();
        List<ReportListVO> list = JSONUtil.getJsonToList(data, ReportListVO.class);
        for (ReportListVO vo : list) {
            if (vo.getCreatorUser()!=null||!vo.getCreatorUser().equals("")){
                UserInfoVO infoVO = usersApi.getInfo(vo.getCreatorUser()).getData();
                if (infoVO!=null) {
                    vo.setCreatorUser(infoVO.getAccount() + "/" + infoVO.getRealName());
                }else {
                    vo.setCreatorUser("");
                }
            }
            if (vo.getCreatorUser()!=null||!vo.getCreatorUser().equals("")){
                UserInfoVO infoVO = usersApi.getInfo(vo.getLastModifyUser()).getData();
                if (infoVO!=null) {
                    vo.setLastModifyUser(infoVO.getAccount() + "/" + infoVO.getRealName());
                }else {
                    vo.setLastModifyUser("");
                }
            }
        }
        ListVO vo = new ListVO();
        vo.setList(list);
        writeObjectToJson(resp, ActionResult.success(vo));
    }

    //下拉
    public void Selector(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ReportEntity> data = reportService.GetList();
        List<ReportSelectorVO> list = JSONUtil.getJsonToList(data, ReportSelectorVO.class);
        ListVO vo = new ListVO();
        vo.setList(list);
        writeObjectToJson(resp, ActionResult.success(vo));
    }

    //预览报表
    public void previewData(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String page = req.getParameter("page");
        String token = JwtUtil.getRealToken(req.getHeader("Authorization"));
        if ("preview".equals(id)) {
            try {
                //未保存文件在编辑器预览
                ReportDefinition reportDefinition = (ReportDefinition) TempObjectCache.getObject(token);
                Map<String, Object> parameters = new HashMap<>();
                ReportBuilder reportBuilder = new ReportBuilder();
                Connection connection = null;
                connection = JdbcUtil.getConnection(dataSourceConfig.getMultiTenancy(),dataSourceUtil);
                Report report = null;
                report = reportBuilder.buildReports(reportDefinition, parameters, connection);
                UreportPreviewUtil previewUtil = new UreportPreviewUtil();
                HtmlReport htmlReport = null;
                //分页操作
                if ("".equals(page) || null == page || "0".equals(page)) {
                    htmlReport = previewUtil.loadReport(report, false, 0);
                } else {
                    htmlReport = previewUtil.loadReport(report, true, Integer.valueOf(page));
                }
                htmlReport.setStyle(reportDefinition.getStyle());
                htmlReport.setSearchFormData(reportDefinition.buildSearchFormData(report.getContext().getDatasetMap(), parameters));
                writeObjectToJson(resp, ActionResult.success(htmlReport));
            } catch (Exception e) {
//                e.printStackTrace();
                log.error(e.getMessage());
                writeObjectToJson(resp, ActionResult.fail("缓存已超时"));
            }
        } else {
            //通过id预览
            ReportEntity entity = reportService.GetInfo(id);
            UreportPreviewUtil previewUtil = new UreportPreviewUtil();
            Connection connection = null;
            connection = JdbcUtil.getConnection(dataSourceConfig.getMultiTenancy(),dataSourceUtil);
            ReportPreviewVO vo = previewUtil.priview(entity, false, 1, connection);
            writeObjectToJson(resp, ActionResult.success(vo));
        }
    }

    //通过id打开到报表编辑器
    public void getInfo(HttpServletRequest req, HttpServletResponse resp, String id) throws ServletException, IOException {
        ReportEntity entity = reportService.GetInfo(id);
        ReportDefinition reportDefinition = null;
        if (entity == null) {
            writeObjectToJson(resp, ActionResult.fail("数据不存在"));
        }
        byte[] content = entity.getContent().getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
        reportDefinition = UreportUtil.parseReport(inputStream, entity.getFullName());
        ReportDefinitionWrapper wrapper = new ReportDefinitionWrapper(reportDefinition);
        ReportInfoModel model = JSONUtil.getJsonToBean(entity, ReportInfoModel.class);
        writeObjectToJson(resp, ActionResult.successTOBase(wrapper, model));
    }

    //保存报表
    public void savaData(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String payload = RequestUtil.getPayload(req);
        String token = JwtUtil.getRealToken(req.getHeader("Authorization"));
        Map<String, Object> map = JSONUtil.StringToMap(payload);
        if (map == null) {
            writeObjectToJson(resp, ActionResult.fail("不能添加空数据"));
        } else {
            ReportCrForm reportCrForm = JSONUtil.getJsonToBean(map, ReportCrForm.class);
            reportCrForm.setContent(UreportUtil.decodeContent(reportCrForm.getContent()));
            ReportEntity entity = JSONUtil.getJsonToBean(reportCrForm, ReportEntity.class);
            if (reportService.IsExistByFullName(entity.getFullName(), entity.getId())) {
                writeObjectToJson(resp, ActionResult.fail("名称不能重复"));
            } else {
                String userId = updateData.getUserId(token);
                entity.setCreatorUser(userId);
                reportService.Create(entity);
                Object id = entity.getId();
                writeObjectToJson(resp, ActionResult.success(id));
            }
        }
    }

    //修改
    public void updateData(HttpServletRequest req, HttpServletResponse resp, String id) throws ServletException, IOException {
        String token = JwtUtil.getRealToken(req.getHeader("Authorization"));
        String payload = RequestUtil.getPayload(req);
        Map<String, Object> map = JSONUtil.StringToMap(payload);
        if (id == null || id.equals("")) {
            writeObjectToJson(resp, ActionResult.fail("数据不存在，修改失败"));
        } else {
            ReportUpForm reportUpForm = JSONUtil.getJsonToBean(map, ReportUpForm.class);
            reportUpForm.setContent(UreportUtil.decodeContent(reportUpForm.getContent()));
            ReportEntity entity = JSONUtil.getJsonToBean(reportUpForm, ReportEntity.class);
            entity.setContent(UreportUtil.decodeContent(entity.getContent()));
            if (reportService.IsExistByFullName(entity.getFullName(), id)) {
                writeObjectToJson(resp, ActionResult.fail("名称不能重复"));
            } else {
                String userId = updateData.getUserId(token);
                entity.setLastModifyUser(userId);
                boolean flags = reportService.Update(id, entity);
                if (flags) {
                    writeObjectToJson(resp, ActionResult.success("修改成功"));
                } else {
                    writeObjectToJson(resp, ActionResult.fail("数据不存在，修改失败"));
                }
            }
        }
    }

    //删除
    public void deleteData(HttpServletRequest req, HttpServletResponse resp, String id) throws ServletException, IOException {
        if (id == null || id.equals("")) {
            writeObjectToJson(resp, ActionResult.fail("数据不存在，修改失败"));
        } else {
            ReportEntity entity = reportService.GetInfo(id);
            boolean flags = reportService.Delete(entity);
            if (flags) {
                writeObjectToJson(resp, ActionResult.success("删除成功"));
            } else {
                writeObjectToJson(resp, ActionResult.fail("数据不存在，修改失败"));
            }
        }
    }

    //通过id导出报表
    public void exportData(HttpServletRequest req, HttpServletResponse resp, String id, String type) throws ServletException, IOException {
        ReportEntity entity = reportService.GetInfo(id);
        if (entity == null) {
            writeObjectToJson(resp, ActionResult.fail("导出数据不能为空"));
        }
        String fileName = entity.getFullName();
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("content-Type", "application/x-download");
        Connection connection = null;
        try {
            connection = JdbcUtil.getConnection(dataSourceConfig.getMultiTenancy(),dataSourceUtil);
        } catch (Exception throwables) {
            throwables.printStackTrace();
            log.error("数据源错误：" + throwables.getMessage());
        }
        if (type.toLowerCase().equals("pdf")) {
            resp.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".pdf", "UTF-8"));
            OutputStream outputStream = resp.getOutputStream();
            UreportPdfUtil pdfUtil = new UreportPdfUtil();
            pdfUtil.buildPdfToConnection(entity, outputStream, connection);
            outputStream.flush();
            outputStream.close();
        } else if (type.toLowerCase().equals("word")) {
            resp.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".docx", "UTF-8"));
            UreportWordUtil wordUtil = new UreportWordUtil();
            XWPFDocument xwpfDocument = wordUtil.buildWord(entity, connection);
            xwpfDocument.write(resp.getOutputStream());
            xwpfDocument.close();
        } else if (type.toLowerCase().equals("excel")) {
            resp.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
            UreportExcelUtil excelUtil = new UreportExcelUtil();
            Workbook workbook = excelUtil.buildExcel(entity, false, false, connection);
            workbook.write(resp.getOutputStream());
            workbook.close();
        }
    }


    @Override
    public String url() {
        return "/DataReport";
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
