package smart.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import smart.base.service.BillRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.base.vo.PageListVO;
import smart.base.vo.PaginationVO;
import smart.base.UserInfo;
import smart.base.vo.DownloadVO;
import smart.config.ConfigValueUtil;
import smart.exception.DataException;
import org.springframework.transaction.annotation.Transactional;
import smart.base.entity.ProvinceEntity;
import smart.model.customercar.CustomerCarPaginationExportModel;
import smart.model.customercar.CustomerCarPagination;
import smart.permission.entity.UserEntity;
import smart.permission.service.UserService;
import smart.model.customercar.CustomerCarCrForm;
import smart.model.customercar.CustomerCarInfoVO;
import smart.model.customercar.CustomerCarListVO;
import smart.model.customercar.CustomerCarUpForm;
import smart.entity.*;
import smart.base.service.ProvinceService;
import smart.util.*;
import smart.base.util.*;
import smart.base.vo.ListVO;
import smart.util.context.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smart.entity.CustomerCarEntity;
import smart.service.CustomerCarService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


/**
 * 车辆信息
 *
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-11-17 11:44:41
 */
@Slf4j
@RestController
@Api(tags = "车辆信息", description = "customer")
@RequestMapping("/CustomerCar")
public class CustomerCarController {
    @Autowired
    private DynDicUtil dynDicUtil;
    @Autowired
    private GeneraterSwapUtil generaterSwapUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private UserProvider userProvider;

    @Autowired
    private CustomerCarService customerCarService;


    /**
     * 列表
     *
     * @param customerCarPagination
     * @return
     */
    @GetMapping
    public ActionResult list(CustomerCarPagination customerCarPagination) throws IOException {
        List<CustomerCarEntity> list = customerCarService.getList(customerCarPagination);
        //处理id字段转名称，若无需转或者为空可删除
        for (CustomerCarEntity entity : list) {
            entity.setCuid(dynDicUtil.getDynName("a5db1469499f40ae8a302630bac9ab5e", "Mobile", "CUId", entity.getCuid()));
            entity.setPlatetype(dynDicUtil.getDicName(entity.getPlatetype()));
            entity.setCartype(dynDicUtil.getDicName(entity.getCartype()));
            entity.setLimiteduse(dynDicUtil.getDicName(entity.getLimiteduse()));
            entity.setCreatoruserid(generaterSwapUtil.userSelectValue(entity.getCreatoruserid()));
            entity.setLastmodifyuserid(generaterSwapUtil.userSelectValue(entity.getLastmodifyuserid()));
        }
        List<CustomerCarListVO> listVO = JsonUtil.getJsonToList(list, CustomerCarListVO.class);
        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = JsonUtil.getJsonToBean(customerCarPagination, PaginationVO.class);
        vo.setPagination(page);
        return ActionResult.success(vo);
    }


    /**
     * 创建
     *
     * @param customerCarCrForm
     * @return
     */
    @PostMapping
    @Transactional
    public ActionResult create(@RequestBody @Valid CustomerCarCrForm customerCarCrForm) throws DataException {
        UserInfo userInfo = userProvider.get();
        customerCarCrForm.setCreatortime(DateUtil.getNow());
        customerCarCrForm.setCreatoruserid(userInfo.getUserId());
        customerCarCrForm.setLastmodifytime(DateUtil.getNow());
        CustomerCarEntity entity = JsonUtil.getJsonToBean(customerCarCrForm, CustomerCarEntity.class);
        entity.setId(RandomUtil.uuId());
        customerCarService.create(entity);
        return ActionResult.success("新建成功");
    }


    /**
     * 模板下载
     *
     * @return
     */
    @ApiOperation("模板下载")
    @GetMapping("/templateDownload")
    public ActionResult<DownloadVO> TemplateDownload() {
        UserInfo userInfo = userProvider.get();
        DownloadVO vo = DownloadVO.builder().build();
        try {
            vo.setName("职员信息.xlsx");
            vo.setUrl(UploaderUtil.uploaderFile("/api/Common/DownloadModel?encryption=", userInfo.getId() + "#" + "职员信息.xlsx" + "#" + "Temporary"));
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
    @GetMapping("/Actions/Export")
    public ActionResult Export(CustomerCarPaginationExportModel customerCarPaginationExportModel) throws IOException {
        CustomerCarPagination customerCarPagination = JsonUtil.getJsonToBean(customerCarPaginationExportModel, CustomerCarPagination.class);
        List<CustomerCarEntity> list = customerCarService.getTypeList(customerCarPagination, customerCarPaginationExportModel.getDataType());
        //处理id字段转名称，若无需转或者为空可删除

        for (CustomerCarEntity entity : list) {
            entity.setCuid(dynDicUtil.getDynName("a5db1469499f40ae8a302630bac9ab5e", "Mobile", "CUId", entity.getCuid()));
            entity.setPlatetype(dynDicUtil.getDicName(entity.getPlatetype()));
            entity.setCartype(dynDicUtil.getDicName(entity.getCartype()));
            entity.setLimiteduse(dynDicUtil.getDicName(entity.getLimiteduse()));
            entity.setCreatoruserid(generaterSwapUtil.userSelectValue(entity.getCreatoruserid()));
            entity.setLastmodifyuserid(generaterSwapUtil.userSelectValue(entity.getLastmodifyuserid()));
        }
        List<CustomerCarListVO> listVO = JsonUtil.getJsonToList(list, CustomerCarListVO.class);
        //转换为map输出
        List<Map<String, Object>> mapList = JsonUtil.getJsonToListMap(JsonUtil.getObjectToStringDateFormat(listVO, "yyyy-MM-dd HH:mm:ss"));
        String[] keys = !StringUtil.isEmpty(customerCarPaginationExportModel.getSelectKey()) ? customerCarPaginationExportModel.getSelectKey().split(",") : new String[0];
        UserInfo userInfo = userProvider.get();
        DownloadVO vo = creatModelExcel(configValueUtil.getTemporaryFilePath(), mapList, keys, userInfo);
        return ActionResult.success(vo);
    }

    //导出表格
    public static DownloadVO creatModelExcel(String path, List<Map<String, Object>> list, String[] keys, UserInfo userInfo) {
        DownloadVO vo = DownloadVO.builder().build();
        try {
            List<ExcelExportEntity> entitys = new ArrayList<>();
            if (keys.length > 0) {
                for (String key : keys) {
                    switch (key) {
                        case "cuid":
                            entitys.add(new ExcelExportEntity("所属车主用户", "cuid"));
                            break;
                        case "platenumber":
                            entitys.add(new ExcelExportEntity("车牌号", "platenumber"));
                            break;
                        case "platetype":
                            entitys.add(new ExcelExportEntity("号牌类型", "platetype"));
                            break;
                        case "cartype":
                            entitys.add(new ExcelExportEntity("车辆类型", "cartype"));
                            break;
                        case "isdefaultplate":
                            entitys.add(new ExcelExportEntity("是否默认车牌", "isdefaultplate"));
                            break;
                        case "vin":
                            entitys.add(new ExcelExportEntity("车辆识别代号VIN", "vin"));
                            break;
                        case "enabledmark":
                            entitys.add(new ExcelExportEntity("有效标志", "enabledmark"));
                            break;
                        case "limiteduse":
                            entitys.add(new ExcelExportEntity("使用限制", "limiteduse"));
                            break;
                        case "creatortime":
                            entitys.add(new ExcelExportEntity("创建时间", "creatortime"));
                            break;
                        case "creatoruserid":
                            entitys.add(new ExcelExportEntity("创建用户", "creatoruserid"));
                            break;
                        case "lastmodifytime":
                            entitys.add(new ExcelExportEntity("修改时间", "lastmodifytime"));
                            break;
                        case "lastmodifyuserid":
                            entitys.add(new ExcelExportEntity("修改用户", "lastmodifyuserid"));
                            break;
                        default:
                            break;
                    }
                }
            }
            ExportParams exportParams = new ExportParams(null, "表单信息");
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, entitys, list);
            String fileName = "表单信息" + DateUtil.dateNow("yyyyMMddHHmmss") + ".xls";
            vo.setName(fileName);
            vo.setUrl(UploaderUtil.uploaderFile(userInfo.getId() + "#" + fileName + "#" + "Temporary"));
            path = path + fileName;
            FileOutputStream fos = new FileOutputStream(path);
            workbook.write(fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vo;
    }


    /**
     * 信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ActionResult<CustomerCarInfoVO> info(@PathVariable("id") String id) {
        CustomerCarEntity entity = customerCarService.getInfo(id);
        CustomerCarInfoVO vo = JsonUtil.getJsonToBean(entity, CustomerCarInfoVO.class);
        if (vo.getCreatortime() != null) {
            vo.setCreatortime(DateUtil.dateFormatHHmmssAddEight(Long.valueOf(vo.getCreatortime())));
        }
        UserEntity userEntity = userService.getInfo(entity.getCreatoruserid());
        if (userEntity != null) {
            vo.setCreatoruserid(userEntity.getRealName() + "/" + userEntity.getAccount());
        }
        if (vo.getLastmodifytime() != null) {
            vo.setLastmodifytime(DateUtil.dateFormatHHmmssAddEight(Long.valueOf(vo.getLastmodifytime())));
        }
        if (userEntity != null) {
            vo.setLastmodifyuserid(userEntity.getRealName() + "/" + userEntity.getAccount());
        }
        return ActionResult.success(vo);
    }


    /**
     * 更新
     *
     * @param id
     * @return
     */
    @PutMapping("/{id}")
    @Transactional
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid CustomerCarUpForm customerCarUpForm) throws DataException {
        CustomerCarEntity entity = customerCarService.getInfo(id);
        if (entity != null) {
            customerCarService.delete(entity);
            UserInfo userInfo = userProvider.get();
            customerCarUpForm.setCreatortime(DateUtil.dateFormat(entity.getCreatortime()));
            customerCarUpForm.setCreatoruserid(entity.getCreatoruserid());
            customerCarUpForm.setLastmodifytime(DateUtil.dateFormat(entity.getLastmodifytime()));
            customerCarUpForm.setLastmodifyuserid(userInfo.getUserId());
            entity = JsonUtil.getJsonToBean(customerCarUpForm, CustomerCarEntity.class);
            entity.setId(id);
            customerCarService.create(entity);
            return ActionResult.success("更新成功");
        } else {
            return ActionResult.fail("更新失败，数据不存在");
        }
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ActionResult delete(@PathVariable("id") String id) {
        CustomerCarEntity entity = customerCarService.getInfo(id);
        if (entity != null) {
            customerCarService.delete(entity);
        }
        return ActionResult.success("删除成功");
    }

}
