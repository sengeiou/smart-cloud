package smart.onlinedev.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.vo.PaginationVO;
import smart.base.VisualdevEntity;
import smart.base.service.VisualdevService;
import smart.exception.DataException;
import smart.onlinedev.model.PaginationModel;
import smart.onlinedev.model.VisualdevModelDataCrForm;
import smart.onlinedev.model.VisualdevModelDataInfoVO;
import smart.onlinedev.model.VisualdevModelDataUpForm;
import smart.onlinedev.model.visualdevmodelApp.AppDataInfoVO;
import smart.onlinedev.service.VisualdevModelAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
/**
 * 0代码app无表开发
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "0代码app无表开发", description = "OnlineDevApp")
@RestController
@RequestMapping("/OnlineDev/App")
public class VisualdevModelAppController {


    @Autowired
    private VisualdevModelAppService modelAppService;
    @Autowired
    private VisualdevService visualdevService;

    @ApiOperation("获取数据列表")
    @GetMapping("/{modelId}/List")
    public ActionResult list(@PathVariable("modelId") String modelId, PaginationModel paginationModel) throws DataException, ParseException, SQLException, IOException {
        List<Map<String, Object>> realList = modelAppService.resultList(modelId, paginationModel);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationModel, PaginationVO.class);
        return ActionResult.page(realList, paginationVO);
    }

    @ApiOperation("获取列表表单配置JSON")
    @GetMapping("/{modelId}/Config")
    public ActionResult getData(@PathVariable("modelId") String modelId) {
        VisualdevEntity entity = visualdevService.getInfo(modelId);
        AppDataInfoVO vo = JsonUtil.getJsonToBean(entity, AppDataInfoVO.class);
        if (vo == null) {
            return ActionResult.fail("功能不存在");
        }
        return ActionResult.success(vo);
    }

    @ApiOperation("添加数据")
    @PostMapping("/{modelId}")
    public ActionResult create(@PathVariable("modelId") String modelId, @RequestBody VisualdevModelDataCrForm visualdevModelDataCrForm) throws DataException, SQLException {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(modelId);
        modelAppService.create(visualdevEntity, visualdevModelDataCrForm.getData());
        return ActionResult.success("新建成功");
    }

    @ApiOperation("修改数据")
    @PutMapping("/{modelId}/{id}")
    public ActionResult update(@PathVariable("id") String id, @PathVariable("modelId") String modelId, @RequestBody VisualdevModelDataUpForm visualdevModelDataUpForm) throws DataException, SQLException {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(modelId);
        boolean flag = modelAppService.update(id, visualdevEntity, visualdevModelDataUpForm.getData());
        if (flag) {
            return ActionResult.success("更新成功");
        }
        return ActionResult.fail("更新失败，数据不存在");
    }

    @ApiOperation("删除数据")
    @DeleteMapping("/{modelId}/{id}")
    public ActionResult delete(@PathVariable("id") String id, @PathVariable("modelId") String modelId) throws DataException, SQLException {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(modelId);
        boolean result = modelAppService.delete(id, visualdevEntity);
        if (result) {
            return ActionResult.success("删除成功");
        } else {
            return ActionResult.fail("删除失败，数据不存在");
        }
    }

    @ApiOperation("获取数据信息")
    @GetMapping("/{modelId}/{id}")
    public ActionResult info(@PathVariable("modelId") String modelId, @PathVariable("id") String id) throws DataException, ParseException, SQLException, IOException {
        VisualdevEntity visualdevEntity = visualdevService.getInfo(modelId);
        Map<String, Object> info = modelAppService.info(id, visualdevEntity);
        VisualdevModelDataInfoVO vo = JsonUtil.getJsonToBean(info, VisualdevModelDataInfoVO.class);
        return ActionResult.success(vo);
    }

}

