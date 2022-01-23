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
import smart.model.parkingspace.ParkingSpacePaginationExportModel;
import smart.model.parkingspace.ParkingSpacePagination;
import smart.permission.entity.UserEntity;
import smart.permission.service.UserService;
import smart.model.parkingspace.ParkingSpaceCrForm;
import smart.model.parkingspace.ParkingSpaceInfoVO;
import smart.model.parkingspace.ParkingSpaceListVO;
import smart.model.parkingspace.ParkingSpaceUpForm;
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
import smart.entity.ParkingSpaceEntity;
import smart.service.ParkingSpaceService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


/**
 * 泊位管理
 *
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-11-12 13:48:03
 */
@Slf4j
@RestController
@Api(tags = "泊位管理", description = "system")
@RequestMapping("/ParkingSpace")
public class ParkingSpaceController {
    @Autowired
    private GeneraterSwapUtil generaterSwapUtil;
    @Autowired
    private DynDicUtil dynDicUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private UserProvider userProvider;

    @Autowired
    private ParkingSpaceService parkingSpaceService;


    /**
     * 列表
     *
     * @param parkingSpacePagination
     * @return
     */
    @GetMapping
    public ActionResult list(ParkingSpacePagination parkingSpacePagination) throws IOException {
        List<ParkingSpaceEntity> list = parkingSpaceService.getList(parkingSpacePagination);
        //处理id字段转名称，若无需转或者为空可删除

        for (ParkingSpaceEntity entity : list) {
            entity.setPid(dynDicUtil.getDynName("40332ea23d72460d85519c8b37d7ff9d", "F_Name", "F_Id", entity.getPid()));
            entity.setCreatoruserid(generaterSwapUtil.userSelectValue(entity.getCreatoruserid()));
            entity.setLastmodifyuserid(generaterSwapUtil.userSelectValue(entity.getLastmodifyuserid()));
        }
        List<ParkingSpaceListVO> listVO = JsonUtil.getJsonToList(list, ParkingSpaceListVO.class);
        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = JsonUtil.getJsonToBean(parkingSpacePagination, PaginationVO.class);
        vo.setPagination(page);
        return ActionResult.success(vo);
    }


    /**
     * 创建
     *
     * @param parkingSpaceCrForm
     * @return
     */
    @PostMapping
    @Transactional
    public ActionResult create(@RequestBody @Valid ParkingSpaceCrForm parkingSpaceCrForm) throws DataException {
        UserInfo userInfo = userProvider.get();
        parkingSpaceCrForm.setCreatortime(DateUtil.getNow());
        parkingSpaceCrForm.setCreatoruserid(userInfo.getUserId());
        ParkingSpaceEntity entity = JsonUtil.getJsonToBean(parkingSpaceCrForm, ParkingSpaceEntity.class);
        entity.setId(RandomUtil.uuId());
        parkingSpaceService.create(entity);
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
    public ActionResult Export(ParkingSpacePaginationExportModel parkingSpacePaginationExportModel) throws IOException {
        ParkingSpacePagination parkingSpacePagination = JsonUtil.getJsonToBean(parkingSpacePaginationExportModel, ParkingSpacePagination.class);
        List<ParkingSpaceEntity> list = parkingSpaceService.getTypeList(parkingSpacePagination, parkingSpacePaginationExportModel.getDataType());
        //处理id字段转名称，若无需转或者为空可删除

        for (ParkingSpaceEntity entity : list) {
            entity.setPid(dynDicUtil.getDynName("40332ea23d72460d85519c8b37d7ff9d", "F_Name", "F_Id", entity.getPid()));
            entity.setCreatoruserid(generaterSwapUtil.userSelectValue(entity.getCreatoruserid()));
            entity.setLastmodifyuserid(generaterSwapUtil.userSelectValue(entity.getLastmodifyuserid()));
        }
        List<ParkingSpaceListVO> listVO = JsonUtil.getJsonToList(list, ParkingSpaceListVO.class);
        //转换为map输出
        List<Map<String, Object>> mapList = JsonUtil.getJsonToListMap(JsonUtil.getObjectToStringDateFormat(listVO, "yyyy-MM-dd HH:mm:ss"));
        String[] keys = !StringUtil.isEmpty(parkingSpacePaginationExportModel.getSelectKey()) ? parkingSpacePaginationExportModel.getSelectKey().split(",") : new String[0];
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
                            entitys.add(new ExcelExportEntity("停车场", "pid"));
                            break;
                        case "device":
                            entitys.add(new ExcelExportEntity("设备序列号", "device"));
                            break;
                        case "name":
                            entitys.add(new ExcelExportEntity("泊位名称", "name"));
                            break;
                        case "type":
                            entitys.add(new ExcelExportEntity("泊位类型", "type"));
                            break;
                        case "ischarging":
                            entitys.add(new ExcelExportEntity("是否充电桩车位 0:否 1：是", "ischarging"));
                            break;
                        case "lon":
                            entitys.add(new ExcelExportEntity("经度", "lon"));
                            break;
                        case "lat":
                            entitys.add(new ExcelExportEntity("纬度", "lat"));
                            break;
                        case "enabledmark":
                            entitys.add(new ExcelExportEntity("有效标志", "enabledmark"));
                            break;
                        case "creatortime":
                            entitys.add(new ExcelExportEntity("创建时间", "creatortime"));
                            break;
                        case "lastmodifytime":
                            entitys.add(new ExcelExportEntity("修改时间", "lastmodifytime"));
                            break;
                        case "creatoruserid":
                            entitys.add(new ExcelExportEntity("创建用户", "creatoruserid"));
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
    public ActionResult<ParkingSpaceInfoVO> info(@PathVariable("id") String id) {
        ParkingSpaceEntity entity = parkingSpaceService.getInfo(id);
        ParkingSpaceInfoVO vo = JsonUtil.getJsonToBean(entity, ParkingSpaceInfoVO.class);
        if (vo.getCreatortime() != null) {
            vo.setCreatortime(DateUtil.dateFormatHHmmssAddEight(Long.valueOf(vo.getCreatortime())));
        }
        if (vo.getLastmodifytime() != null) {
            vo.setLastmodifytime(DateUtil.dateFormatHHmmssAddEight(Long.valueOf(vo.getLastmodifytime())));
        }
        UserEntity userEntity = userService.getInfo(entity.getCreatoruserid());
        if (userEntity != null) {
            vo.setCreatoruserid(userEntity.getRealName() + "/" + userEntity.getAccount());
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
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid ParkingSpaceUpForm parkingSpaceUpForm) throws DataException {
        ParkingSpaceEntity entity = parkingSpaceService.getInfo(id);
        if (entity != null) {
            parkingSpaceService.delete(entity);
            UserInfo userInfo = userProvider.get();
            parkingSpaceUpForm.setCreatortime(DateUtil.dateFormat(entity.getCreatortime()));
            parkingSpaceUpForm.setLastmodifytime(DateUtil.dateFormat(new Date()));
            parkingSpaceUpForm.setCreatoruserid(entity.getCreatoruserid());
            parkingSpaceUpForm.setLastmodifyuserid(userInfo.getUserId());
            entity = JsonUtil.getJsonToBean(parkingSpaceUpForm, ParkingSpaceEntity.class);
            entity.setId(id);
            parkingSpaceService.create(entity);
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
        ParkingSpaceEntity entity = parkingSpaceService.getInfo(id);
        if (entity != null) {
            parkingSpaceService.delete(entity);
        }
        return ActionResult.success("删除成功");
    }

}
