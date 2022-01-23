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
import smart.entity.BlackWhiteListEntity;
import smart.exception.DataException;
import smart.model.blackwhitelist.*;
import smart.service.BlackWhiteListService;
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
import java.util.List;
import java.util.Map;


/**
 * p_black_white_list
 *
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-12-17 17:24:37
 */
@Slf4j
@RestController
@Api(tags = "p_black_white_list", description = "parking")
@RequestMapping("/BlackWhiteList")
public class BlackWhiteListController {
    @Autowired
    private GeneraterSwapUtil generaterSwapUtil;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private UserProvider userProvider;

    @Autowired
    private BlackWhiteListService blackWhiteListService;
    @Autowired
    private DynDicUtil dynDicUtil;

    /**
     * 列表
     *
     * @param blackWhiteListPagination
     * @return
     */
    @GetMapping
    public ActionResult list(BlackWhiteListPagination blackWhiteListPagination) throws IOException {
        List<BlackWhiteListEntity> list = blackWhiteListService.getList(blackWhiteListPagination);
        //处理id字段转名称，若无需转或者为空可删除

        for (BlackWhiteListEntity entity : list) {
            entity.setPids(dynDicUtil.getDynName("40332ea23d72460d85519c8b37d7ff9d", "fullName", "id", entity.getPids()));
        }
        List<String> arrayFieldList = new ArrayList<>();
        arrayFieldList.add("pids");
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
        List<BlackWhiteListListVO> listVO = JsonUtil.getJsonToList(JsonUtil.getObjectToString(newMapListVO), BlackWhiteListListVO.class);
        PageListVO vo = new PageListVO();
        vo.setList(listVO);
        PaginationVO page = JsonUtil.getJsonToBean(blackWhiteListPagination, PaginationVO.class);
        vo.setPagination(page);
        return ActionResult.success(vo);
    }


    /**
     * 创建
     *
     * @param blackWhiteListCrForm
     * @return
     */
    @PostMapping
    @Transactional
    public ActionResult create(@RequestBody @Valid BlackWhiteListCrForm blackWhiteListCrForm) throws DataException {
        UserInfo userInfo = userProvider.get();
        BlackWhiteListEntity entity = JsonUtil.getJsonToBean(blackWhiteListCrForm, BlackWhiteListEntity.class);
        entity.setId(RandomUtil.uuId());
        blackWhiteListService.create(entity);
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
    public ActionResult Export(BlackWhiteListPaginationExportModel blackWhiteListPaginationExportModel) throws IOException {
        BlackWhiteListPagination blackWhiteListPagination = JsonUtil.getJsonToBean(blackWhiteListPaginationExportModel, BlackWhiteListPagination.class);
        List<BlackWhiteListEntity> list = blackWhiteListService.getTypeList(blackWhiteListPagination, blackWhiteListPaginationExportModel.getDataType());
        //处理id字段转名称，若无需转或者为空可删除

        for (BlackWhiteListEntity entity : list) {
            entity.setPids(dynDicUtil.getDynName("40332ea23d72460d85519c8b37d7ff9d", "fullName", "id", entity.getPids()));
        }
        List<String> arrayFieldList = new ArrayList<>();
        arrayFieldList.add("pids");
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
        List<BlackWhiteListListVO> listVO = JsonUtil.getJsonToList(JsonUtil.getObjectToString(newMapListVO), BlackWhiteListListVO.class);
        //转换为map输出
        List<Map<String, Object>> mapList = JsonUtil.getJsonToListMap(JsonUtil.getObjectToStringDateFormat(listVO, "yyyy-MM-dd HH:mm:ss"));
        String[] keys = !StringUtil.isEmpty(blackWhiteListPaginationExportModel.getSelectKey()) ? blackWhiteListPaginationExportModel.getSelectKey().split(",") : new String[0];
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
                        case "listtype":
                            entitys.add(new ExcelExportEntity("名单类型", "listtype"));
                            break;
                        case "pids":
                            entitys.add(new ExcelExportEntity("停车场地ID", "pids"));
                            break;
                        case "platenumber":
                            entitys.add(new ExcelExportEntity("车牌号", "platenumber"));
                            break;
                        case "starttime":
                            entitys.add(new ExcelExportEntity("名单有效开始时间", "starttime"));
                            break;
                        case "endtime":
                            entitys.add(new ExcelExportEntity("名单有效结束时间", "endtime"));
                            break;
                        case "enabledmark":
                            entitys.add(new ExcelExportEntity("有效标志", "enabledmark"));
                            break;
                        case "creatortime":
                            entitys.add(new ExcelExportEntity("创建时间", "creatortime"));
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
    public ActionResult<BlackWhiteListInfoVO> info(@PathVariable("id") String id) {
        BlackWhiteListEntity entity = blackWhiteListService.getInfo(id);
        BlackWhiteListInfoVO vo = JsonUtil.getJsonToBean(entity, BlackWhiteListInfoVO.class);
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
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid BlackWhiteListUpForm blackWhiteListUpForm) throws DataException {
        BlackWhiteListEntity entity = blackWhiteListService.getInfo(id);
        if (entity != null) {
            blackWhiteListService.delete(entity);
            UserInfo userInfo = userProvider.get();
            entity = JsonUtil.getJsonToBean(blackWhiteListUpForm, BlackWhiteListEntity.class);
            entity.setId(id);
            blackWhiteListService.create(entity);
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
        BlackWhiteListEntity entity = blackWhiteListService.getInfo(id);
        if (entity != null) {
            blackWhiteListService.delete(entity);
        }
        return ActionResult.success("删除成功");
    }

}
