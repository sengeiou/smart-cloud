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
import smart.model.device.DevicePaginationExportModel;
import smart.model.device.DevicePagination;
import smart.permission.entity.UserEntity;
import smart.permission.service.UserService;
import smart.model.device.DeviceCrForm;
import smart.model.device.DeviceInfoVO;
import smart.model.device.DeviceListVO;
import smart.model.device.DeviceUpForm;
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
import smart.entity.DeviceEntity;
import smart.service.DeviceService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


/**
 * 设备管理
 *
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-11-28 12:22:22
 */
@Slf4j
@RestController
@Api(tags = "设备管理", description = "device")
@RequestMapping("/Device")
public class DeviceController {
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
    private DeviceService deviceService;


    /**
     * 列表
     *
     * @param devicePagination
     * @return
     */
    @GetMapping
    public ActionResult list(DevicePagination devicePagination) throws IOException {
        List<DeviceEntity> list = deviceService.getList(devicePagination);
        //处理id字段转名称，若无需转或者为空可删除

        for (DeviceEntity entity : list) {
            entity.setNetworktype(dynDicUtil.getDicName(entity.getNetworktype()));
            entity.setOnlinestatus(dynDicUtil.getDicName(entity.getOnlinestatus()));
            entity.setAlarmstatus(dynDicUtil.getDicName(entity.getAlarmstatus()));
            entity.setDevicestatus(dynDicUtil.getDicName(entity.getDevicestatus()));
            entity.setCreatoruserid(generaterSwapUtil.userSelectValue(entity.getCreatoruserid()));
            entity.setLastmodifyuserid(generaterSwapUtil.userSelectValue(entity.getLastmodifyuserid()));
        }
        List<DeviceListVO> listVO = JsonUtil.getJsonToList(list, DeviceListVO.class);
        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = JsonUtil.getJsonToBean(devicePagination, PaginationVO.class);
        vo.setPagination(page);
        return ActionResult.success(vo);
    }


    /**
     * 创建
     *
     * @param deviceCrForm
     * @return
     */
    @PostMapping
    @Transactional
    public ActionResult create(@RequestBody @Valid DeviceCrForm deviceCrForm) throws DataException {
        UserInfo userInfo = userProvider.get();
        deviceCrForm.setCreatoruserid(userInfo.getUserId());
        deviceCrForm.setCreatortime(DateUtil.getNow());
        DeviceEntity entity = JsonUtil.getJsonToBean(deviceCrForm, DeviceEntity.class);
        entity.setId(RandomUtil.uuId());
        deviceService.create(entity);
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
    public ActionResult Export(DevicePaginationExportModel devicePaginationExportModel) throws IOException {
        DevicePagination devicePagination = JsonUtil.getJsonToBean(devicePaginationExportModel, DevicePagination.class);
        List<DeviceEntity> list = deviceService.getTypeList(devicePagination, devicePaginationExportModel.getDataType());
        //处理id字段转名称，若无需转或者为空可删除

        for (DeviceEntity entity : list) {
            entity.setNetworktype(dynDicUtil.getDicName(entity.getNetworktype()));
            entity.setOnlinestatus(dynDicUtil.getDicName(entity.getOnlinestatus()));
            entity.setAlarmstatus(dynDicUtil.getDicName(entity.getAlarmstatus()));
            entity.setDevicestatus(dynDicUtil.getDicName(entity.getDevicestatus()));
            entity.setCreatoruserid(generaterSwapUtil.userSelectValue(entity.getCreatoruserid()));
            entity.setLastmodifyuserid(generaterSwapUtil.userSelectValue(entity.getLastmodifyuserid()));
        }
        List<DeviceListVO> listVO = JsonUtil.getJsonToList(list, DeviceListVO.class);
        //转换为map输出
        List<Map<String, Object>> mapList = JsonUtil.getJsonToListMap(JsonUtil.getObjectToStringDateFormat(listVO, "yyyy-MM-dd HH:mm:ss"));
        String[] keys = !StringUtil.isEmpty(devicePaginationExportModel.getSelectKey()) ? devicePaginationExportModel.getSelectKey().split(",") : new String[0];
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
                        case "name":
                            entitys.add(new ExcelExportEntity("设备名称", "name"));
                            break;
                        case "code":
                            entitys.add(new ExcelExportEntity("系统编号", "code"));
                            break;
                        case "sn":
                            entitys.add(new ExcelExportEntity("设备SN", "sn"));
                            break;
                        case "heartbeatcycle":
                            entitys.add(new ExcelExportEntity("心跳周期", "heartbeatcycle"));
                            break;
                        case "networktype":
                            entitys.add(new ExcelExportEntity("上网方式", "networktype"));
                            break;
                        case "onlinestatus":
                            entitys.add(new ExcelExportEntity("在线状态", "onlinestatus"));
                            break;
                        case "alarmstatus":
                            entitys.add(new ExcelExportEntity("告警状态", "alarmstatus"));
                            break;
                        case "devicestatus":
                            entitys.add(new ExcelExportEntity("设备状态", "devicestatus"));
                            break;
                        case "enabledmark":
                            entitys.add(new ExcelExportEntity("有效标志", "enabledmark"));
                            break;
                        case "devicepropertyjson":
                            entitys.add(new ExcelExportEntity("设备属性", "devicepropertyjson"));
                            break;
                        case "lasttelemetrydatajson":
                            entitys.add(new ExcelExportEntity("最后一次遥测", "lasttelemetrydatajson"));
                            break;
                        case "lastheartbeatdatajson":
                            entitys.add(new ExcelExportEntity("最后一次心跳", "lastheartbeatdatajson"));
                            break;
                        case "creatoruserid":
                            entitys.add(new ExcelExportEntity("创建用户", "creatoruserid"));
                            break;
                        case "creatortime":
                            entitys.add(new ExcelExportEntity("创建时间", "creatortime"));
                            break;
                        case "lastmodifyuserid":
                            entitys.add(new ExcelExportEntity("修改用户", "lastmodifyuserid"));
                            break;
                        case "lastmodifytime":
                            entitys.add(new ExcelExportEntity("修改时间", "lastmodifytime"));
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
    public ActionResult<DeviceInfoVO> info(@PathVariable("id") String id) {
        DeviceEntity entity = deviceService.getInfo(id);
        DeviceInfoVO vo = JsonUtil.getJsonToBean(entity, DeviceInfoVO.class);
        UserEntity userEntity = userService.getInfo(entity.getCreatoruserid());
        if (userEntity != null) {
            vo.setCreatoruserid(userEntity.getRealName() + "/" + userEntity.getAccount());
        }
        if (vo.getCreatortime() != null) {
            vo.setCreatortime(DateUtil.dateFormatHHmmssAddEight(Long.valueOf(vo.getCreatortime())));
        }
        if (userEntity != null) {
            vo.setLastmodifyuserid(userEntity.getRealName() + "/" + userEntity.getAccount());
        }
        if (vo.getLastmodifytime() != null) {
            vo.setLastmodifytime(DateUtil.dateFormatHHmmssAddEight(Long.valueOf(vo.getLastmodifytime())));
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
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid DeviceUpForm deviceUpForm) throws DataException {
        DeviceEntity entity = deviceService.getInfo(id);
        if (entity != null) {
            deviceService.delete(entity);
            UserInfo userInfo = userProvider.get();
            deviceUpForm.setCreatoruserid(entity.getCreatoruserid());
            deviceUpForm.setCreatortime(DateUtil.dateFormat(entity.getCreatortime()));
            deviceUpForm.setLastmodifyuserid(userInfo.getUserId());
            deviceUpForm.setLastmodifytime(DateUtil.dateFormat(new Date()));
            entity = JsonUtil.getJsonToBean(deviceUpForm, DeviceEntity.class);
            entity.setId(id);
            deviceService.create(entity);
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
        DeviceEntity entity = deviceService.getInfo(id);
        if (entity != null) {
            deviceService.delete(entity);
        }
        return ActionResult.success("删除成功");
    }

}
