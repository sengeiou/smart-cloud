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
import smart.model.parking.ParkingPaginationExportModel;
import smart.model.parking.ParkingPagination;
import smart.permission.entity.UserEntity;
import smart.permission.service.UserService;
import smart.base.entity.ProvinceEntity;
import smart.base.service.ProvinceService;
import smart.model.parking.ParkingCrForm;
import smart.model.parking.ParkingInfoVO;
import smart.model.parking.ParkingListVO;
import smart.model.parking.ParkingUpForm;
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
import smart.entity.ParkingEntity;
import smart.service.ParkingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


/**
 * 车场管理
 *
 * @版本： V3.1.0
 * @版权： SmartCloud项目开发组
 * @作者： SmartCloud
 * @日期： 2021-11-15 14:30:27
 */
@Slf4j
@RestController
@Api(tags = "车场管理", description = "system")
@RequestMapping("/Parking")
public class ParkingController {
    @Autowired
    private DynDicUtil dynDicUtil;
    @Autowired
    private GeneraterSwapUtil generaterSwapUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private ProvinceService provinceService;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private UserProvider userProvider;

    @Autowired
    private ParkingService parkingService;


    /**
     * 列表
     *
     * @param parkingPagination
     * @return
     */
    @GetMapping
    public ActionResult list(ParkingPagination parkingPagination) throws IOException {
        List<ParkingEntity> list = parkingService.getList(parkingPagination);
        //处理id字段转名称，若无需转或者为空可删除

        for (ParkingEntity entity : list) {
            entity.setPaid(dynDicUtil.getDynName("b5da17b6ef2542ce9f5ada70a3722d0f", "F_Name", "F_Id", entity.getPaid()));
            entity.setType(dynDicUtil.getDicName(entity.getType()));
            entity.setContactuserid(generaterSwapUtil.userSelectValues(entity.getContactuserid()));
            List<String> provList1 = generaterSwapUtil.provinceData(entity.getLeveladdress());
            if (provList1 != null && provList1.size() > 0) {
                StringBuilder restStr = new StringBuilder();
                List<ProvinceEntity> provinceEntities = provinceService.listByIds(provList1);
                for (ProvinceEntity proventity1 : provinceEntities) {
                    restStr.append(proventity1.getFullName() + "/");
                }
                if (restStr.length() != 0) {
                    restStr.deleteCharAt(restStr.length() - 1);
                }
                entity.setLeveladdress(String.valueOf(restStr));
            }
            entity.setCreatoruserid(generaterSwapUtil.userSelectValue(entity.getCreatoruserid()));
            entity.setLastmodifyuserid(generaterSwapUtil.userSelectValue(entity.getLastmodifyuserid()));
        }
        List<String> arrayFieldList = new ArrayList<>();
        arrayFieldList.add("contactuserid");
        List<Map<String, Object>> mapListVO = JsonUtil.getJsonToListMap(JsonUtil.getObjectToString(list));
        List<Map<String, Object>> newMapListVO = new ArrayList<>();
        for (Map<String, Object> dataMap : mapListVO) {
            for (String field : arrayFieldList) {
                if (dataMap.get(field) != null) {
                    dataMap.put(field, dataMap.get(field).toString().split(","));
                }
            }
            newMapListVO.add(dataMap);
        }
        List<ParkingListVO> listVO = JsonUtil.getJsonToList(JsonUtil.getObjectToString(newMapListVO), ParkingListVO.class);
        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = JsonUtil.getJsonToBean(parkingPagination, PaginationVO.class);
        vo.setPagination(page);
        return ActionResult.success(vo);
    }


    /**
     * 创建
     *
     * @param parkingCrForm
     * @return
     */
    @PostMapping
    @Transactional
    public ActionResult create(@RequestBody @Valid ParkingCrForm parkingCrForm) throws DataException {
        UserInfo userInfo = userProvider.get();
        parkingCrForm.setCreatortime(DateUtil.getNow());
        parkingCrForm.setCreatoruserid(userInfo.getUserId());
        ParkingEntity entity = JsonUtil.getJsonToBean(parkingCrForm, ParkingEntity.class);
        entity.setId(RandomUtil.uuId());
        parkingService.create(entity);
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
    public ActionResult Export(ParkingPaginationExportModel parkingPaginationExportModel) throws IOException {
        ParkingPagination parkingPagination = JsonUtil.getJsonToBean(parkingPaginationExportModel, ParkingPagination.class);
        List<ParkingEntity> list = parkingService.getTypeList(parkingPagination, parkingPaginationExportModel.getDataType());
        //处理id字段转名称，若无需转或者为空可删除

        for (ParkingEntity entity : list) {
            entity.setId(dynDicUtil.getDynName("b5da17b6ef2542ce9f5ada70a3722d0f", "F_Name", "F_Id", entity.getId()));
            entity.setType(dynDicUtil.getDicName(entity.getType()));
            entity.setContactuserid(generaterSwapUtil.userSelectValues(entity.getContactuserid()));
            List<String> provList1 = generaterSwapUtil.provinceData(entity.getLeveladdress());
            if (provList1 != null && provList1.size() > 0) {
                StringBuilder restStr = new StringBuilder();
                List<ProvinceEntity> provinceEntities = provinceService.listByIds(provList1);
                for (ProvinceEntity proventity1 : provinceEntities) {
                    restStr.append(proventity1.getFullName() + "/");
                }
                if (restStr.length() != 0) {
                    restStr.deleteCharAt(restStr.length() - 1);
                }
                entity.setLeveladdress(String.valueOf(restStr));
            }
            entity.setCreatoruserid(generaterSwapUtil.userSelectValue(entity.getCreatoruserid()));
            entity.setLastmodifyuserid(generaterSwapUtil.userSelectValue(entity.getLastmodifyuserid()));
        }
        List<String> arrayFieldList = new ArrayList<>();
        arrayFieldList.add("contactuserid");
        List<Map<String, Object>> mapListVO = JsonUtil.getJsonToListMap(JsonUtil.getObjectToString(list));
        List<Map<String, Object>> newMapListVO = new ArrayList<>();
        for (Map<String, Object> dataMap : mapListVO) {
            for (String field : arrayFieldList) {
                if (dataMap.get(field) != null) {
                    dataMap.put(field, dataMap.get(field).toString().split(","));
                }
            }
            newMapListVO.add(dataMap);
        }
        List<ParkingListVO> listVO = JsonUtil.getJsonToList(JsonUtil.getObjectToString(newMapListVO), ParkingListVO.class);
        //转换为map输出
        List<Map<String, Object>> mapList = JsonUtil.getJsonToListMap(JsonUtil.getObjectToStringDateFormat(listVO, "yyyy-MM-dd HH:mm:ss"));
        String[] keys = !StringUtil.isEmpty(parkingPaginationExportModel.getSelectKey()) ? parkingPaginationExportModel.getSelectKey().split(",") : new String[0];
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
                        case "pid":
                            entitys.add(new ExcelExportEntity("所属片区", "pid"));
                            break;
                        case "name":
                            entitys.add(new ExcelExportEntity("名称", "name"));
                            break;
                        case "type":
                            entitys.add(new ExcelExportEntity("车场类型", "type"));
                            break;
                        case "contactuserid":
                            entitys.add(new ExcelExportEntity("车场管理员", "contactuserid"));
                            break;
                        case "leveladdress":
                            entitys.add(new ExcelExportEntity("省份", "leveladdress"));
                            break;
                        case "address":
                            entitys.add(new ExcelExportEntity("详细地址", "address"));
                            break;
                        case "spacetotal":
                            entitys.add(new ExcelExportEntity("泊位总数量", "spacetotal"));
                            break;
                        case "reservedspacetotal":
                            entitys.add(new ExcelExportEntity("可预约车位数", "reservedspacetotal"));
                            break;
                        case "longrentalspacetotal":
                            entitys.add(new ExcelExportEntity("长租泊位数量", "longrentalspacetotal"));
                            break;
                        case "chargingpiletotal":
                            entitys.add(new ExcelExportEntity("充电桩数量", "chargingpiletotal"));
                            break;
                        case "isselfsupport":
                            entitys.add(new ExcelExportEntity("是否自营停车场", "isselfsupport"));
                            break;
                        case "issupportadvancepayment":
                            entitys.add(new ExcelExportEntity("是否支持预支付", "issupportadvancepayment"));
                            break;
                        case "enabledmark":
                            entitys.add(new ExcelExportEntity("停车场启用状态", "enabledmark"));
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
    public ActionResult<ParkingInfoVO> info(@PathVariable("id") String id) {
        ParkingEntity entity = parkingService.getInfo(id);
        ParkingInfoVO vo = JsonUtil.getJsonToBean(entity, ParkingInfoVO.class);
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
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid ParkingUpForm parkingUpForm) throws DataException {
        ParkingEntity entity = parkingService.getInfo(id);
        if (entity != null) {
            parkingService.delete(entity);
            UserInfo userInfo = userProvider.get();
            parkingUpForm.setCreatortime(DateUtil.dateFormat(entity.getCreatortime()));
            parkingUpForm.setCreatoruserid(entity.getCreatoruserid());
            parkingUpForm.setLastmodifytime(DateUtil.dateFormat(new Date()));
            parkingUpForm.setLastmodifyuserid(userInfo.getUserId());
            entity = JsonUtil.getJsonToBean(parkingUpForm, ParkingEntity.class);
            entity.setId(id);
            parkingService.create(entity);
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
        ParkingEntity entity = parkingService.getInfo(id);
        if (entity != null) {
            parkingService.delete(entity);
        }
        return ActionResult.success("删除成功");
    }

}
