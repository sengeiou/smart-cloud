package smart.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.base.UserInfo;
import smart.base.util.DynDicUtil;
import smart.base.util.GeneraterSwapUtil;
import smart.base.vo.DownloadVO;
import smart.base.vo.PageListVO;
import smart.base.vo.PaginationVO;
import smart.config.ConfigValueUtil;
import smart.entity.ActivityEntity;
import smart.exception.DataException;
import smart.model.activity.*;
import smart.permission.entity.UserEntity;
import smart.permission.service.UserService;
import smart.service.ActivityService;
import smart.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 活动管理
 *
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-12-30 17:41:03
 */
@Slf4j
@RestController
@Api(tags = "活动管理", description = "parking")
@RequestMapping("/Activity")
public class ActivityController {
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
    private ActivityService activityService;


    /**
     * 列表
     *
     * @param activityPagination
     * @return
     */
    @GetMapping
    public ActionResult list(ActivityPagination activityPagination) throws IOException {
        List<ActivityEntity> list = activityService.getList(activityPagination);
        //处理id字段转名称，若无需转或者为空可删除

        for (ActivityEntity entity : list) {
            entity.setActivitytype(dynDicUtil.getDicName(entity.getActivitytype()));
            entity.setCreatoruserid(generaterSwapUtil.userSelectValue(entity.getCreatoruserid()));
            entity.setLastmodifyuserid(generaterSwapUtil.userSelectValue(entity.getLastmodifyuserid()));
        }
        List<ActivityListVO> listVO = JsonUtil.getJsonToList(list, ActivityListVO.class);
        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = JsonUtil.getJsonToBean(activityPagination, PaginationVO.class);
        vo.setPagination(page);
        return ActionResult.success(vo);
    }


    /**
     * 创建
     *
     * @param activityCrForm
     * @return
     */
    @PostMapping
    @Transactional
    public ActionResult create(@RequestBody @Valid ActivityCrForm activityCrForm) throws DataException {
        UserInfo userInfo = userProvider.get();
        activityCrForm.setCreatoruserid(userInfo.getUserId());
        activityCrForm.setCreatortime(DateUtil.getNow());
        ActivityEntity entity = JsonUtil.getJsonToBean(activityCrForm, ActivityEntity.class);
        entity.setId(RandomUtil.uuId());
        activityService.create(entity);
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
    public ActionResult Export(ActivityPaginationExportModel activityPaginationExportModel) throws IOException {
        ActivityPagination activityPagination = JsonUtil.getJsonToBean(activityPaginationExportModel, ActivityPagination.class);
        List<ActivityEntity> list = activityService.getTypeList(activityPagination, activityPaginationExportModel.getDataType());
        //处理id字段转名称，若无需转或者为空可删除

        for (ActivityEntity entity : list) {
            entity.setActivitytype(dynDicUtil.getDicName(entity.getActivitytype()));
            entity.setCreatoruserid(generaterSwapUtil.userSelectValue(entity.getCreatoruserid()));
            entity.setLastmodifyuserid(generaterSwapUtil.userSelectValue(entity.getLastmodifyuserid()));
        }
        List<ActivityListVO> listVO = JsonUtil.getJsonToList(list, ActivityListVO.class);
        //转换为map输出
        List<Map<String, Object>> mapList = JsonUtil.getJsonToListMap(JsonUtil.getObjectToStringDateFormat(listVO, "yyyy-MM-dd HH:mm:ss"));
        String[] keys = !StringUtil.isEmpty(activityPaginationExportModel.getSelectKey()) ? activityPaginationExportModel.getSelectKey().split(",") : new String[0];
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
                            entitys.add(new ExcelExportEntity("活动名称", "name"));
                            break;
                        case "thumbnail":
                            entitys.add(new ExcelExportEntity("缩略图url", "thumbnail"));
                            break;
                        case "activitytype":
                            entitys.add(new ExcelExportEntity("活动类型", "activitytype"));
                            break;
                        case "mutex":
                            entitys.add(new ExcelExportEntity("是否与其他活动互斥", "mutex"));
                            break;
                        case "participate":
                            entitys.add(new ExcelExportEntity("可重复参与次数", "participate"));
                            break;
                        case "starttime":
                            entitys.add(new ExcelExportEntity("活动开始时间", "starttime"));
                            break;
                        case "endtime":
                            entitys.add(new ExcelExportEntity("活动结束时间", "endtime"));
                            break;
                        case "status":
                            entitys.add(new ExcelExportEntity("活动状态", "status"));
                            break;
                        case "goal":
                            entitys.add(new ExcelExportEntity("活动目标", "goal"));
                            break;
                        case "originator":
                            entitys.add(new ExcelExportEntity("活动发起人", "originator"));
                            break;
                        case "enabledmark":
                            entitys.add(new ExcelExportEntity("有效标志", "enabledmark"));
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
    public ActionResult<ActivityInfoVO> info(@PathVariable("id") String id) {
        ActivityEntity entity = activityService.getInfo(id);
        ActivityInfoVO vo = JsonUtil.getJsonToBean(entity, ActivityInfoVO.class);
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
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid ActivityUpForm activityUpForm) throws DataException {
        ActivityEntity entity = activityService.getInfo(id);
        if (entity != null) {
            activityService.delete(entity);
            UserInfo userInfo = userProvider.get();
            activityUpForm.setCreatoruserid(entity.getCreatoruserid());
            activityUpForm.setCreatortime(DateUtil.dateFormat(entity.getCreatortime()));
            activityUpForm.setLastmodifyuserid(userInfo.getUserId());
            activityUpForm.setLastmodifytime(DateUtil.dateFormat(new Date()));
            entity = JsonUtil.getJsonToBean(activityUpForm, ActivityEntity.class);
            entity.setId(id);
            activityService.create(entity);
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
        ActivityEntity entity = activityService.getInfo(id);
        if (entity != null) {
            activityService.delete(entity);
        }
        return ActionResult.success("删除成功");
    }

}
