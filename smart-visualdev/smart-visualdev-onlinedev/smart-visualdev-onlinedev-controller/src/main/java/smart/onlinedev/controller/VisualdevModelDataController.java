package smart.onlinedev.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


import smart.base.ActionResult;
import smart.base.vo.DownloadVO;
import smart.base.vo.PaginationVO;
import smart.base.UserInfo;
import smart.base.VisualdevEntity;
import smart.base.service.VisualdevService;
import smart.base.util.genUtil.custom.VisualUtils;
import smart.config.ConfigValueUtil;
import smart.exception.DataException;
import smart.onlinedev.entity.VisualdevModelDataEntity;
import smart.onlinedev.model.*;
import smart.onlinedev.model.fields.FieLdsModel;
import smart.onlinedev.service.VisualdevModelDataService;
import smart.onlinedev.util.AutoFeildsUtil;
import smart.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 0代码无表开发
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Slf4j
@Api(tags = "0代码无表开发", description = "OnlineDev")
@RestController
@RequestMapping("/OnlineDev")
public class VisualdevModelDataController {
    @Autowired
    private VisualdevModelDataService visualdevModelDataService;
    @Autowired
    private VisualdevService visualdevService;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private RedisUtil redisUtil;


    @ApiOperation("获取数据列表")
    @GetMapping("/{modelId}/List")
    public ActionResult list(@PathVariable("modelId") String modelId, PaginationModel paginationModel) throws ParseException, IOException, SQLException, DataException {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(modelId);
        List<Map<String, Object>> realList = visualdevModelDataService.getListResult(visualdevEntity, paginationModel);

        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationModel, PaginationVO.class);

        return ActionResult.page(realList, paginationVO);
    }

    @ApiOperation("获取列表表单配置JSON")
    @GetMapping("/{modelId}/Config")
    public ActionResult getData(@PathVariable("modelId") String modelId) {
        VisualdevEntity entity = visualdevService.getInfo(modelId);
        DataInfoVO vo = JsonUtil.getJsonToBean(entity, DataInfoVO.class);
        if (vo == null) {
            return ActionResult.fail("功能不存在");
        }
        return ActionResult.success(vo);
    }


    @ApiOperation("获取列表配置JSON")
    @GetMapping("/{modelId}/ColumnData")
    public ActionResult getColumnData(@PathVariable("modelId") String modelId) {
        VisualdevEntity entity = visualdevService.getInfo(modelId);
        FormDataInfoVO vo = JsonUtil.getJsonToBean(entity, FormDataInfoVO.class);
        return ActionResult.success(vo);
    }


    @ApiOperation("获取表单配置JSON")
    @GetMapping("/{modelId}/FormData")
    public ActionResult getFormData(@PathVariable("modelId") String modelId) {
        VisualdevEntity entity = visualdevService.getInfo(modelId);
        ColumnDataInfoVO vo = JsonUtil.getJsonToBean(entity, ColumnDataInfoVO.class);
        return ActionResult.success(vo);
    }


    @ApiOperation("获取数据信息")
    @GetMapping("/{modelId}/{id}")
    public ActionResult info(@PathVariable("id") String id, @PathVariable("modelId") String modelId) throws DataException, ParseException, SQLException, IOException {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(modelId);
        //有表
        if (!StringUtil.isEmpty(visualdevEntity.getTables()) && !OnlineDevData.TABLE_CONST.equals(visualdevEntity.getTables())) {
            VisualdevModelDataInfoVO vo = visualdevModelDataService.tableInfo(id, visualdevEntity);
            return ActionResult.success(vo);
        }
        //无表
        VisualdevModelDataEntity entity = visualdevModelDataService.getInfo(id);
        Map<String, Object> formData = JsonUtil.stringToMap(visualdevEntity.getFormData());
        List<FieLdsModel> modelList = JsonUtil.getJsonToList(formData.get("fields").toString(), FieLdsModel.class);
        //去除模板多级控件
        modelList = VisualUtils.deleteMore(modelList);
        String data = AutoFeildsUtil.autoFeilds(modelList, entity.getData());
        entity.setData(data);
        VisualdevModelDataInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, VisualdevModelDataInfoVO.class);
        return ActionResult.success(vo);
    }

    @ApiOperation("获取数据信息(带转换数据)")
    @GetMapping("/{modelId}/{id}/DataChange")
    public ActionResult infoWithDataChange(@PathVariable("modelId") String modelId, @PathVariable("id") String id) throws DataException, ParseException, IOException, SQLException {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(modelId);
        if (redisUtil.exists(CacheKeyUtil.VISIUALDATA + modelId)) {
            redisUtil.remove(CacheKeyUtil.VISIUALDATA + modelId);
        }
        //有表
        if (!StringUtil.isEmpty(visualdevEntity.getTables()) && !OnlineDevData.TABLE_CONST.equals(visualdevEntity.getTables())) {
            VisualdevModelDataInfoVO vo = visualdevModelDataService.tableInfoDataChange(id, visualdevEntity);
            return ActionResult.success(vo);
        }
        //无表
        VisualdevModelDataInfoVO vo = visualdevModelDataService.infoDataChange(id, visualdevEntity);
        return ActionResult.success(vo);
    }


    @ApiOperation("添加数据")
    @PostMapping("/{modelId}")
    public ActionResult create(@PathVariable("modelId") String modelId, @RequestBody VisualdevModelDataCrForm visualdevModelDataCrForm) throws DataException, SQLException {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(modelId);

        visualdevModelDataService.create(visualdevEntity, visualdevModelDataCrForm);
        return ActionResult.success("新建成功");
    }


    @ApiOperation("修改数据")
    @PutMapping("/{modelId}/{id}")
    public ActionResult update(@PathVariable("id") String id, @PathVariable("modelId") String modelId, @RequestBody VisualdevModelDataUpForm visualdevModelDataUpForm) throws DataException, SQLException {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(modelId);
        boolean flag = visualdevModelDataService.update(id, visualdevEntity, visualdevModelDataUpForm);
        if (flag) {
            return ActionResult.success("更新成功");
        }
        return ActionResult.fail("更新失败，数据不存在");
    }


    @ApiOperation("删除数据")
    @DeleteMapping("/{modelId}/{id}")
    public ActionResult delete(@PathVariable("id") String id, @PathVariable("modelId") String modelId) throws DataException, SQLException {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(modelId);
        if (!StringUtil.isEmpty(visualdevEntity.getTables()) && !OnlineDevData.TABLE_CONST.equals(visualdevEntity.getTables())) {
            boolean result = visualdevModelDataService.tableDelete(id, visualdevEntity);
            if (result) {
                return ActionResult.success("删除成功");
            } else {
                return ActionResult.fail("删除失败，数据不存在");
            }
        }

        VisualdevModelDataEntity entity = visualdevModelDataService.getInfo(id);
        if (entity != null) {
            visualdevModelDataService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /*@ApiOperation("批量删除数据")
    @DeleteMapping("/{modelId}/{ids}")
    public ActionResult beachDelete(@PathVariable("ids") String ids, @PathVariable("modelId") String modelId) throws DataException, SQLException {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(modelId);
        if (!StringUtil.isEmpty(visualdevEntity.getTables()) && !OnlineDevData.TABLE_CONST.equals(visualdevEntity.getTables())) {
            boolean result = visualdevModelDataService.tableDeleteMore(ids, visualdevEntity);
            if (result) {
                return ActionResult.success("删除成功");
            } else {
                return ActionResult.fail("删除失败，数据不存在");
            }
        }
        String[] idList = ids.split(",");
        if (visualdevModelDataService.removeByIds(Arrays.asList(idList))) {
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }*/


    @ApiOperation("导入")
    @PostMapping("/Model/{modelId}/Actions/Import")
    public ActionResult imports(@PathVariable("modelId") String modelId) {
        VisualdevModelDataEntity entity = visualdevModelDataService.getInfo(modelId);
        List<MultipartFile> list = UpUtil.getFileAll();
        MultipartFile file = list.get(0);
        if (file.getOriginalFilename().contains(".xlsx")) {
            String filePath = configValueUtil.getTemporaryFilePath();
            String fileName = RandomUtil.uuId() + "." + UpUtil.getFileType(file);
            //保存文件
            FileUtil.upFile(file, filePath, fileName);
            File temporary = new File(filePath + fileName);
            return ActionResult.success("导入成功");
        } else {
            return ActionResult.fail("选择文件不符合导入");
        }
    }

    @ApiOperation("导出")
    @PostMapping("/{modelId}/Actions/Export")
    public ActionResult export(@PathVariable("modelId") String modelId, @RequestBody PaginationModelExport paginationModelExport) throws ParseException, IOException, SQLException, DataException {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(modelId);
        String[] keys = !StringUtil.isEmpty(paginationModelExport.getSelectKey()) ? paginationModelExport.getSelectKey().split(",") : new String[0];
        //关键字过滤
        List<Map<String, Object>> realList = visualdevModelDataService.exportData(keys, paginationModelExport, visualdevEntity);
        UserInfo userInfo = userProvider.get();
        smart.base.vo.DownloadVO vo = VisualUtils.createModelExcel(visualdevEntity.getFormData(), configValueUtil.getTemporaryFilePath(), realList, keys, userInfo);
        return ActionResult.success(vo);
    }


    /**
     * 模板下载
     *
     * @return
     */
    @ApiOperation("模板下载")
    @GetMapping("/TemplateDownload")
    public ActionResult<smart.base.vo.DownloadVO> templateDownload() {
        UserInfo userInfo = userProvider.get();
//        String path = configValueUtil.getTemplateFilePath() + "employee_import_template.xlsx";
        smart.base.vo.DownloadVO vo = DownloadVO.builder().build();
        try {
            vo.setName("职员信息.xlsx");
            vo.setUrl(UploaderUtil.uploaderFile("/api/file/DownloadModel?encryption=", userInfo.getId() + "#" + "职员信息.xlsx" + "#" + "Temporary"));
        } catch (Exception e) {
            log.error("信息导出Excel错误:{}", e.getMessage());
        }
        return ActionResult.success(vo);
    }

    /**
     * 在线开发大写转小写
     *
     * @return
     */
    @ApiOperation("在线开发大写转小写")
    @GetMapping("/changeTypeToLowOne")
    public void changeTypeToLow(String modelId) {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(modelId);
        visualdevEntity = VisualUtils.changeType(visualdevEntity);
        visualdevService.update(visualdevEntity.getId(), visualdevEntity);
        List<VisualdevModelDataEntity> list = visualdevModelDataService.getList(modelId);
        if (list != null && list.size() > 0) {
            List<Map<String, Object>> dataList = VisualUtils.toLowerKeyList(JsonUtil.getJsonToListMap(JsonUtilEx.getObjectToString(list)));
            list = JsonUtil.getJsonToList(dataList, VisualdevModelDataEntity.class);
            visualdevModelDataService.saveBatch(list);
        }
    }

    /**
     * 在线开发大写转小写
     *
     * @return
     */
    @ApiOperation("全部在线开发大写转小写")
    @GetMapping("/changeTypeToLowBatch")
    public void changeTypeToLowBatch() {
        List<VisualdevEntity> list = visualdevService.getList();
        for (VisualdevEntity entity : list) {
            entity = VisualUtils.changeType(entity);
            visualdevService.update(entity.getId(), entity);
            List<VisualdevModelDataEntity> modellist = visualdevModelDataService.getList(entity.getId());
            if (list != null && list.size() > 0) {
                List<Map<String, Object>> dataList = VisualUtils.toLowerKeyList(JsonUtil.getJsonToListMap(JsonUtilEx.getObjectToString(modellist)));
                modellist = JsonUtil.getJsonToList(dataList, VisualdevModelDataEntity.class);
                visualdevModelDataService.updateBatchById(modellist);
            }
        }

    }

}

