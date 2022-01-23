package smart.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.base.UserInfo;
import smart.base.util.GeneraterSwapUtil;
import smart.base.vo.DownloadVO;
import smart.base.vo.PageListVO;
import smart.base.vo.PaginationVO;
import smart.config.ConfigValueUtil;
import smart.entity.ChargesEntity;
import smart.exception.DataException;
import smart.model.charges.*;
import smart.permission.entity.UserEntity;
import smart.permission.service.UserService;
import smart.service.ChargesService;
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
 * 收费标准
 *
 * @版本： V3.1.0
@author SmartCloud项目开发组
 * @日期： 2021-12-10 15:18:55
 */
@Slf4j
@RestController
@Api(tags = "收费标准", description = "charges")
@RequestMapping("/Charges")
public class ChargesController {
    //    @Autowired
//    private GeneraterSwapUtil generaterSwapUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private UserProvider userProvider;

    @Autowired
    private ChargesService chargesService;


    /**
     * 列表
     *
     * @param chargesPagination
     * @return
     */
    @GetMapping
    public ActionResult list(ChargesPagination chargesPagination) throws IOException {
        List<ChargesEntity> list = chargesService.getList(chargesPagination);
        //处理id字段转名称，若无需转或者为空可删除

        for (ChargesEntity entity : list) {
            entity.setCreatoruserid(GeneraterSwapUtil.userSelectValue(entity.getCreatoruserid()));
            entity.setLastmodifyuserid(GeneraterSwapUtil.userSelectValue(entity.getLastmodifyuserid()));
        }
        List<ChargesListVO> listVO = JsonUtil.getJsonToList(list, ChargesListVO.class);
        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = JsonUtil.getJsonToBean(chargesPagination, PaginationVO.class);
        vo.setPagination(page);
        return ActionResult.success(vo);
    }


    /**
     * 创建
     *
     * @param chargesCrForm
     * @return
     */
    @PostMapping
    @Transactional
    public ActionResult create(@RequestBody @Valid ChargesCrForm chargesCrForm) {
        UserInfo userInfo = userProvider.get();
        chargesCrForm.setCreatortime(DateUtil.getNow());
        chargesCrForm.setCreatoruserid(userInfo.getUserId());
        ChargesEntity entity = JsonUtil.getJsonToBean(chargesCrForm, ChargesEntity.class);
        entity.setId(RandomUtil.uuId());
        chargesService.create(entity);
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
    public ActionResult Export(ChargesPaginationExportModel chargesPaginationExportModel) throws IOException {
        ChargesPagination chargesPagination = JsonUtil.getJsonToBean(chargesPaginationExportModel, ChargesPagination.class);
        List<ChargesEntity> list = chargesService.getTypeList(chargesPagination, chargesPaginationExportModel.getDataType());
        //处理id字段转名称，若无需转或者为空可删除

        for (ChargesEntity entity : list) {
            entity.setCreatoruserid(GeneraterSwapUtil.userSelectValue(entity.getCreatoruserid()));
            entity.setLastmodifyuserid(GeneraterSwapUtil.userSelectValue(entity.getLastmodifyuserid()));
        }
        List<ChargesListVO> listVO = JsonUtil.getJsonToList(list, ChargesListVO.class);
        //转换为map输出
        List<Map<String, Object>> mapList = JsonUtil.getJsonToListMap(JsonUtil.getObjectToStringDateFormat(listVO, "yyyy-MM-dd HH:mm:ss"));
        String[] keys = !StringUtil.isEmpty(chargesPaginationExportModel.getSelectKey()) ? chargesPaginationExportModel.getSelectKey().split(",") : new String[0];
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
                        case "standard_name":
                            entitys.add(new ExcelExportEntity("收费标准名称", "standard_name"));
                            break;
                        case "standard_type":
                            entitys.add(new ExcelExportEntity("标准类型", "standard_type"));
                            break;
                        case "start_time":
                            entitys.add(new ExcelExportEntity("有效开始时间", "start_time"));
                            break;
                        case "end_time":
                            entitys.add(new ExcelExportEntity("有效结束时间", "end_time"));
                            break;
                        case "money_ceiling":
                            entitys.add(new ExcelExportEntity("单笔收费上限（分）", "money_ceiling"));
                            break;
                        case "money_floor":
                            entitys.add(new ExcelExportEntity("单笔收费下限（分）", "money_floor"));
                            break;
                        case "day_money_ceiling":
                            entitys.add(new ExcelExportEntity("24小时收费上限(分）", "day_money_ceiling"));
                            break;
                        case "free_time":
                            entitys.add(new ExcelExportEntity("免费停车时长(分钟)", "free_time"));
                            break;
                        case "enabledmark":
                            entitys.add(new ExcelExportEntity("禁用", "enabledmark"));
                            break;
                        case "remark":
                            entitys.add(new ExcelExportEntity("描述", "remark"));
                            break;
                        case "creatortime":
                            entitys.add(new ExcelExportEntity("创建时间", "creatortime"));
                            break;
                        case "creatoruserid":
                            entitys.add(new ExcelExportEntity("创建用户", "creatoruserid"));
                            break;
                        case "lastmodifytime":
                            entitys.add(new ExcelExportEntity("最后一次修改时间", "lastmodifytime"));
                            break;
                        case "lastmodifyuserid":
                            entitys.add(new ExcelExportEntity("最后一次修改用户", "lastmodifyuserid"));
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
    public ActionResult<ChargesInfoVO> info(@PathVariable("id") String id) {
        ChargesEntity entity = chargesService.getInfo(id);
        ChargesInfoVO vo = JsonUtil.getJsonToBean(entity, ChargesInfoVO.class);
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
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid ChargesUpForm chargesUpForm) throws DataException {
        ChargesEntity entity = chargesService.getInfo(id);
        if (entity != null) {
            chargesService.delete(entity);
            UserInfo userInfo = userProvider.get();
            chargesUpForm.setCreatortime(DateUtil.dateFormat(entity.getCreatortime()));
            chargesUpForm.setCreatoruserid(entity.getCreatoruserid());
            chargesUpForm.setLastmodifytime(DateUtil.dateFormat(new Date()));
            chargesUpForm.setLastmodifyuserid(userInfo.getUserId());
            entity = JsonUtil.getJsonToBean(chargesUpForm, ChargesEntity.class);
            entity.setId(id);
            chargesService.create(entity);
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
        ChargesEntity entity = chargesService.getInfo(id);
        if (entity != null) {
            chargesService.delete(entity);
        }
        return ActionResult.success("删除成功");
    }

}
