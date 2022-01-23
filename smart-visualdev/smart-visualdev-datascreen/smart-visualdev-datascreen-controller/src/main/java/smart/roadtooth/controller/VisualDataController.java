package smart.roadtooth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.model.map.MapSelectorVO;
import smart.base.vo.ListVO;
import smart.base.vo.PageListVO;
import smart.base.vo.PaginationVO;
import smart.util.FileUtil;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import smart.util.RandomUtil;
import smart.base.*;
import smart.base.entity.DictionaryDataEntity;
import smart.roadtooth.VisualDataEntity;
import smart.roadtooth.model.*;
import smart.roadtooth.service.VisualDataService;
import smart.roadtooth.util.VisualImageEnum;
import smart.exception.DataException;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import smart.config.ConfigValueUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 大屏数据
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "大屏数据", description = "Data")
@RestController
@RequestMapping("/DataScreen")
public class VisualDataController {

    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private VisualDataService visualDataService;
    @Autowired
    private DictionaryDataApi dictionaryDataService;

    /**
     * 获取大屏列表(分页)
     *
     * @param pagination 分页
     * @return
     */
    @ApiOperation("获取大屏列表")
    @GetMapping
    public ActionResult<PageListVO<DataListVO>> list(PaginationData pagination) {
        List<VisualDataEntity> data = visualDataService.getList(pagination);
        List<DataListVO> list = JsonUtil.getJsonToList(data, DataListVO.class);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        return ActionResult.page(list, paginationVO);
    }

    /**
     * 大屏下拉框列表
     *
     * @return
     */
    @ApiOperation("大屏下拉框列表")
    @GetMapping("/Selector")
    public ActionResult<MapSelectorVO> selector() {
        List<VisualDataEntity> list = visualDataService.getList().stream().filter(t -> "1".equals(String.valueOf(t.getEnabledMark()))).collect(Collectors.toList());
        List<DictionaryDataEntity> dataList = dictionaryDataService.getListAll().getData();
        List<DataTreeModel> treeModel = new ArrayList<>();
        for (VisualDataEntity children : list) {
            DictionaryDataEntity dataEntity = dataList.stream().filter(t -> t.getId().equals(children.getCategoryId())).findFirst().orElse(null);
            DataTreeModel chilModel = new DataTreeModel();
            if (dataEntity != null) {
                DataTreeModel model = new DataTreeModel();
                model.setId(dataEntity.getId());
                model.setParentId("-1");
                model.setFullName(dataEntity.getFullName());
                if (treeModel.stream().filter(t -> t.getId().equals(dataEntity.getId())).count() == 0) {
                    treeModel.add(model);
                }
                chilModel.setParentId(dataEntity.getId());
                chilModel.setId(children.getId());
                chilModel.setFullName(children.getFullName());
                treeModel.add(chilModel);
            }
        }
        List<SumTree<DataTreeModel>> tree = TreeDotUtils.convertListToTreeDot(treeModel);
        List<DataSelectorVO> listVos = JsonUtil.getJsonToList(tree, DataSelectorVO.class);
        ListVO vo = new ListVO();
        vo.setList(listVos);
        return ActionResult.success(vo);
    }

    /**
     * 获取大屏基本信息
     *
     * @param id 主键
     * @return
     */
    @ApiOperation("大屏信息")
    @GetMapping("/{id}")
    public ActionResult<DataInfoVO> info(@PathVariable("id") String id) throws DataException {
        VisualDataEntity entity = visualDataService.getInfo(id);
        DataInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, DataInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新增大屏
     *
     * @param dataCrForm 实体对象
     * @return
     */
    @ApiOperation("新增大屏")
    @PostMapping
    public ActionResult<DataCrVO> create(@RequestBody @Valid DataCrForm dataCrForm) {
        VisualDataEntity basic = JsonUtil.getJsonToBean(dataCrForm, VisualDataEntity.class);
        basic.setCopyId("0");
        basic.setEnabledMark(1);
        visualDataService.create(basic);
        DataCrVO vo = new DataCrVO();
        vo.setId(basic.getId());
        return ActionResult.success(vo);
    }

    /**
     * 修改大屏
     *
     * @param id          主键
     * @param basicUpForm 实体对象
     * @return
     */
    @ApiOperation("修改大屏")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid DataUpForm basicUpForm) {
        VisualDataEntity entity = JsonUtil.getJsonToBean(basicUpForm, VisualDataEntity.class);
        boolean flag = visualDataService.update(id, entity);
        if (flag == false) {
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除大屏
     *
     * @param id 主键
     * @return
     */
    @ApiOperation("删除大屏")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        VisualDataEntity entity = visualDataService.getInfo(id);
        if (entity != null) {
            visualDataService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /**
     * 拷贝大屏数据
     *
     * @param id
     * @return
     */
    @ApiOperation("拷贝大屏数据")
    @PostMapping("/{id}/Actions/Copy")
    public ActionResult copy(@PathVariable("id") String id) {
        VisualDataEntity basic = visualDataService.getInfo(id);
        if (basic != null) {
            List<VisualDataEntity> list = visualDataService.getList().stream().filter(t -> t.getCopyId().equals(basic.getId())).collect(Collectors.toList());
            String filePath = configValueUtil.getBiVisualPath() + File.separator + VisualImageEnum.SCREENSHOT.getMessage() + File.separator;
            String fileType = FileUtil.getFileType(basic.getScreenShot());
            String fileNewName = RandomUtil.uuId() + "." + fileType;
            FileUtil.copyFile(filePath + basic.getScreenShot(), filePath + fileNewName);
            basic.setScreenShot(fileNewName);
            Integer num = list.size() + 1;
            basic.setFullName(basic.getFullName() + "_副本" + num);
            basic.setCopyId(basic.getId());
            basic.setEnabledMark(0);
            visualDataService.create(basic);
            return ActionResult.success("拷贝成功");
        }
        return ActionResult.fail("拷贝失败");
    }

    /**
     * 更新大屏状态
     *
     * @param id 主键
     * @return
     */
    @ApiOperation("更新大屏状态")
    @PutMapping("/{id}/Actions/State")
    public ActionResult state(@PathVariable("id") String id) {
        VisualDataEntity entity = visualDataService.getInfo(id);
        if (entity != null) {
            entity.setEnabledMark("1".equals(String.valueOf(entity.getEnabledMark())) ? 0 : 1);
            visualDataService.update(id, entity);
            return ActionResult.success("更新大屏成功");
        }
        return ActionResult.fail("更新失败，数据不存在");
    }

}

