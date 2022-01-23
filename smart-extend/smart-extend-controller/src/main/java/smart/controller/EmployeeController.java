package smart.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.base.UserInfo;
import smart.base.vo.DownloadVO;
import smart.base.vo.PaginationVO;
import smart.emnus.FileTypeEnum;
import smart.entity.EmployeeEntity;
import smart.exception.DataException;
import smart.file.FileApi;
import smart.model.EmployeeModel;
import smart.model.employee.*;
import smart.service.EmployeeService;
import smart.util.*;
import smart.util.type.StringNumber;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 职员信息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 *
 */
@Slf4j
@Api(tags = "职员信息", value = "Employee")
@RestController
@RequestMapping("/Employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private FileApi fileApi;

    /**
     * 列表(忽略验证Token)
     *
     * @param paginationEmployee
     * @return
     */
    @ApiOperation("获取职员列表")
    @GetMapping
    public ActionResult getList(PaginationEmployee paginationEmployee) {
        List<EmployeeEntity> data = employeeService.getList(paginationEmployee);
        List<EmployeeListVO> list = JsonUtil.getJsonToList(data, EmployeeListVO.class);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationEmployee, PaginationVO.class);
        return ActionResult.page(list, paginationVO);
    }

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取职员信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        EmployeeEntity entity = employeeService.getInfo(id);
        EmployeeInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, EmployeeInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建
     *
     * @param employeeCrForm 实体对象
     * @return
     */
    @ApiOperation("app添加职员信息")
    @PostMapping
    public ActionResult create(@RequestBody @Valid EmployeeCrForm employeeCrForm) {
        EmployeeEntity entity = JsonUtil.getJsonToBean(employeeCrForm, EmployeeEntity.class);
        employeeService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 更新
     *
     * @param id             主键值
     * @param employeeUpForm 实体对象
     * @return
     */
    @ApiOperation("app修改职员信息")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid EmployeeUpForm employeeUpForm) {
        EmployeeEntity entity = JsonUtil.getJsonToBean(employeeUpForm, EmployeeEntity.class);
        employeeService.update(id, entity);
        return ActionResult.success("更新成功");
    }

    /**
     * 删除
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除职员信息")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        EmployeeEntity entity = employeeService.getInfo(id);
        if (entity != null) {
            employeeService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /**
     * 模板下载
     *
     * @return
     */
    @ApiOperation("模板下载")
    @GetMapping("/TemplateDownload")
    public ActionResult templateDownload() {
        UserInfo userInfo = userProvider.get();
        DownloadVO vo = DownloadVO.builder().build();
        try {
            vo.setName("职员信息.xlsx");
            vo.setUrl(UploaderUtil.uploaderFile("/api/file/DownloadModel?encryption=", userInfo.getId() + "#" + "职员信息" +
                    ".xlsx" + "#" + "Temporary"));
        } catch (Exception e) {
            log.error("信息导出Excel错误:{}", e.getMessage());
        }
        return ActionResult.success(vo);
    }

    /**
     * 导出Excel
     *
     * @return
     */
    @ApiOperation("导出Excel")
    @GetMapping("/ExportExcel")
    public ActionResult exportExcel() {
        UserInfo userInfo = userProvider.get();

        List<EmployeeEntity> entityList = employeeService.getList();
        List<EmployeeExportVO> list = JsonUtil.listToJsonfield(JsonUtil.getJsonToList(JsonUtilEx.getObjectToStringDateFormat(entityList, "yyyy-MM-dd"), EmployeeExportVO.class));
        List<ExcelExportEntity> entitys = new ArrayList<>();
        entitys.add(new ExcelExportEntity("工号", "enCode"));
        entitys.add(new ExcelExportEntity("姓名", "fullName"));
        entitys.add(new ExcelExportEntity("性别", "gender"));
        entitys.add(new ExcelExportEntity("部门", "departmentName"));
        entitys.add(new ExcelExportEntity("岗位", "positionName", 25));
        entitys.add(new ExcelExportEntity("用工性质", "workingNature"));
        entitys.add(new ExcelExportEntity("身份证号", "idNumber", 25));
        entitys.add(new ExcelExportEntity("联系电话", "telephone", 20));
        entitys.add(new ExcelExportEntity("出生年月", "birthday", 20));
        entitys.add(new ExcelExportEntity("参加工作", "attendWorkTime", 20));
        entitys.add(new ExcelExportEntity("最高学历", "education"));
        entitys.add(new ExcelExportEntity("所学专业", "major"));
        entitys.add(new ExcelExportEntity("毕业院校", "graduationAcademy"));
        entitys.add(new ExcelExportEntity("毕业时间", "graduationTime", 20));
        ExportParams exportParams = new ExportParams(null, "职员信息");
        exportParams.setType(ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, entitys, list);
        String name = "职员信息" + DateUtil.dateNow("yyyyMMdd") + "_" + RandomUtil.uuId() + ".xlsx";
        String fileName = fileApi.getPath(FileTypeEnum.TEMPORARY) + name;
        DownloadVO vo = DownloadVO.builder().build();
        try {
            FileOutputStream output = new FileOutputStream(fileName);
            workbook.write(output);
            vo.setName(name);
            vo.setUrl(UploaderUtil.uploaderFile(userInfo.getId() + "#" + name + "#" + "Temporary"));
        } catch (Exception e) {
            log.error("信息导出Excel错误:{}", e.getMessage());
        }
        return ActionResult.success(vo);
    }

    /**
     * 导出Word
     *
     * @return
     */
    @ApiOperation("导出Word")
    @GetMapping("/ExportWord")
    public ActionResult exportWord() {
        UserInfo userInfo = userProvider.get();
        List<EmployeeEntity> list = employeeService.getList();
        //模板文件地址
        String inputUrl = fileApi.getPath(FileTypeEnum.TEMPLATEFILE) + "employee_export_template.docx";
        //新生产的模板文件
        String name = "职员信息" + DateUtil.dateNow("yyyyMMdd") + "_" + RandomUtil.uuId() + ".docx";
        String outputUrl = fileApi.getPath(FileTypeEnum.TEMPORARY) + name;
        List<String[]> testList = new ArrayList<>();
        Map<String, String> testMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            String[] employee = new String[13];
            EmployeeEntity entity = list.get(i);
            employee[0] = entity.getFullName();
            employee[1] = entity.getGender();
            employee[2] = entity.getDepartmentName();
            employee[3] = entity.getPositionName();
            employee[4] = entity.getWorkingNature();
            employee[5] = entity.getIdNumber();
            employee[6] = entity.getTelephone();
            employee[7] = entity.getBirthday() != null ? DateUtil.daFormat(entity.getBirthday()) : "";
            employee[8] = entity.getAttendWorkTime() != null ? DateUtil.daFormat(entity.getAttendWorkTime()) : "";
            employee[9] = entity.getEducation();
            employee[10] = entity.getMajor();
            employee[11] = entity.getGraduationAcademy();
            employee[12] = entity.getGraduationTime() != null ? DateUtil.daFormat(entity.getGraduationTime()) : "";
            testList.add(employee);
        }
        WordUtil.changWord(inputUrl, outputUrl, testMap, testList);
        if (FileUtil.fileIsFile(outputUrl)) {
            DownloadVO vo = DownloadVO.builder().name(name).url(UploaderUtil.uploaderFile(userInfo.getId() + "#" + name + "#" + "Temporary")).build();
            return ActionResult.success(vo);
        }
        return ActionResult.success("文件导出失败");
    }

    /**
     * 导出pdf
     *
     * @return
     */
    @ApiOperation("导出pdf")
    @GetMapping("/ExportPdf")
    public ActionResult exportPdf() {
        UserInfo userInfo = userProvider.get();
        String name = "职员信息" + DateUtil.dateNow("yyyyMMdd") + "_" + RandomUtil.uuId() + ".pdf";
        String outputUrl = fileApi.getPath(FileTypeEnum.TEMPORARY) + name;
        employeeService.exportPdf(employeeService.getList(), outputUrl);
        if (FileUtil.fileIsFile(outputUrl)) {
            DownloadVO vo = DownloadVO.builder().name(name).url(UploaderUtil.uploaderFile(userInfo.getId() + "#" + name + "#" + "Temporary")).build();
            return ActionResult.success(vo);
        }
        return ActionResult.success("文件导出失败");
    }

    /**
     * 导出Excel
     *
     * @return
     */
    @ApiOperation("导出Excel(备用)")
    @GetMapping("/Excel")
    public void excel() {
        Map<String, Object> map = new HashMap<>();
        List<EmployeeEntity> list = employeeService.getList();
        TemplateExportParams param = new TemplateExportParams(fileApi.getPath(FileTypeEnum.TEMPLATEFILE) + "employee_import_template.xlsx", true);
        map.put("Employee", JSON.parse(JSONObject.toJSONStringWithDateFormat(list, "yyyy-MM-dd")));
        Workbook workbook = ExcelExportUtil.exportExcel(param, map);
        DownUtil.dowloadExcel(workbook, "职员信息.xlsx");
    }


    /**
     * 上传文件(excel)
     *
     * @return
     */
    @ApiOperation("上传文件")
    @PostMapping("/Uploader")
    public ActionResult uploader() {
        List<MultipartFile> list = UpUtil.getFileAll();
        MultipartFile file = list.get(0);
        if (file.getOriginalFilename().contains(".xlsx")) {
            String filePath = fileApi.getPath(FileTypeEnum.TEMPORARY);
            String fileName = RandomUtil.uuId() + "." + UpUtil.getFileType(file);
            //上传文件
            FileUtil.upFile(file, filePath, fileName);
            DownloadVO vo = DownloadVO.builder().build();
            vo.setName(fileName);
            return ActionResult.success(vo);
        } else {
            return ActionResult.fail("选择文件不符合导入");
        }

    }

    /**
     * 导入预览
     *
     * @return
     */
    @ApiOperation("导入预览")
    @GetMapping("/ImportPreview")
    public ActionResult importPreview(String fileName) {
        String filePath = fileApi.getPath(FileTypeEnum.TEMPORARY);
        File temporary = new File(filePath + fileName);
        //得到数据
        List<EmployeeModel> personList = ExcelUtil.importExcel(temporary, 0, 1, EmployeeModel.class);
        //预览数据
        Map<String, Object> map = employeeService.importPreview(personList);
        return ActionResult.success(map);
    }

    /**
     * 导入数据
     *
     * @return
     */
    @ApiOperation("导入数据")
    @PostMapping("/ImportData")
    public ActionResult importData(@RequestBody EmployeeModel data) {
        List<EmployeeModel> dataList=JsonUtil.getJsonToList(data.getList(),EmployeeModel.class);
        //导入数据
        EmployeeImportVO result = employeeService.importData(dataList);
        return ActionResult.success(result);
    }

    /**
     * 导出Excel(可选字段)
     *
     * @return
     */
    @ApiOperation("导出Excel（可选字段）")
    @GetMapping("/ExportExcelData")
    public ActionResult exportExcelData(String dataType, String selectKey, PaginationEmployee paginationEmployee) {
        UserInfo userInfo = userProvider.get();
        List<EmployeeEntity> entityList = new ArrayList<>();
        if (StringNumber.ZERO.equals(dataType)) {
            entityList = employeeService.getList(paginationEmployee);
        } else if (StringNumber.ONE.equals(dataType)) {
            entityList = employeeService.getList();
        }
        List<EmployeeExportVO> list = JsonUtil.listToJsonfield(JsonUtil.getJsonToList(JsonUtilEx.getObjectToStringDateFormat(entityList, "yyyy-MM-dd"), EmployeeExportVO.class));
        List<ExcelExportEntity> entitys = new ArrayList<>();
        String[] splitData = selectKey.split(",");
        if (splitData != null && splitData.length > 0) {
            for (int i = 0; i < splitData.length; i++) {
                if ("enCode".equals(splitData[i])) {
                    entitys.add(new ExcelExportEntity("工号", "enCode"));
                }
                if ("fullName".equals(splitData[i])) {
                    entitys.add(new ExcelExportEntity("姓名", "fullName"));
                }
                if ("gender".equals(splitData[i])) {
                    entitys.add(new ExcelExportEntity("性别", "gender"));
                }
                if ("departmentName".equals(splitData[i])) {
                    entitys.add(new ExcelExportEntity("部门", "departmentName"));
                }
                if ("positionName".equals(splitData[i])) {
                    entitys.add(new ExcelExportEntity("岗位", "positionName", 25));
                }
                if ("workingNature".equals(splitData[i])) {
                    entitys.add(new ExcelExportEntity("用工性质", "workingNature"));
                }
                if ("idNumber".equals(splitData[i])) {
                    entitys.add(new ExcelExportEntity("身份证号", "idNumber", 25));
                }
                if ("telephone".equals(splitData[i])) {
                    entitys.add(new ExcelExportEntity("联系电话", "telephone", 20));
                }
                if ("birthday".equals(splitData[i])) {
                    entitys.add(new ExcelExportEntity("出生年月", "birthday", 20));
                }
                if ("attendWorkTime".equals(splitData[i])) {
                    entitys.add(new ExcelExportEntity("参加工作", "attendWorkTime", 20));
                }
                if ("education".equals(splitData[i])) {
                    entitys.add(new ExcelExportEntity("最高学历", "education"));
                }
                if ("major".equals(splitData[i])) {
                    entitys.add(new ExcelExportEntity("所学专业", "major"));
                }
                if ("graduationAcademy".equals(splitData[i])) {
                    entitys.add(new ExcelExportEntity("毕业院校", "graduationAcademy"));
                }
                if ("graduationTime".equals(splitData[i])) {
                    entitys.add(new ExcelExportEntity("毕业时间", "graduationTime", 20));
                }
                if ("creatorTime".equals(splitData[i])) {
                    entitys.add(new ExcelExportEntity("创建时间", "creatorTime"));
                }
            }
        }
        ExportParams exportParams = new ExportParams(null, "职员信息");
        exportParams.setType(ExcelType.XSSF);

        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, entitys, list);
        String name = "职员信息" + DateUtil.dateNow("yyyyMMdd") + "_" + RandomUtil.uuId() + ".xlsx";
        String fileName = fileApi.getPath(FileTypeEnum.TEMPORARY) + name;
        DownloadVO vo = DownloadVO.builder().build();
        try {
            FileOutputStream output = new FileOutputStream(fileName);
            workbook.write(output);
            vo.setName(name);
            vo.setUrl(UploaderUtil.uploaderFile(userInfo.getId() + "#" + name + "#" + "Temporary"));
        } catch (Exception e) {
            log.error("信息导出Excel错误:{}", e.getMessage());
        }
        return ActionResult.success(vo);
    }

}
