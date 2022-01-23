package smart.base.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.vo.ListVO;
import smart.base.vo.PaginationVO;
import smart.util.JsonUtil;
import smart.base.*;
import smart.base.model.map.*;
import smart.base.entity.VisualDataMapEntity;
import smart.exception.DataException;
import smart.base.service.VisualDataMapService;
import smart.permission.model.user.UserAllModel;
import smart.permission.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


/**
 * 数据地图
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "数据地图", value = "Map")
@RestController
@RequestMapping("/Base/DataMap")
public class DataMapController {

    @Autowired
    private VisualDataMapService visualDataMapService;
    @Autowired
    private UserService userService;

    /**
     * 地图列表
     *
     * @param pagination 分页
     * @return
     */
    @ApiOperation("地图列表")
    @GetMapping
    public ActionResult list(Pagination pagination) {
        List<UserAllModel> user = userService.getAll();
        List<VisualDataMapEntity> data = visualDataMapService.getList(pagination);
        List<MapListVO> list = JsonUtil.getJsonToList(data, MapListVO.class);
        for (MapListVO mapListVO : list) {
            UserAllModel model = user.stream().filter(t -> t.getId().equals(String.valueOf(mapListVO.getCreatorUser()))).findFirst().orElse(null);
            if (model != null) {
                mapListVO.setCreatorUser(model.getRealName());
            }
        }
        PaginationVO pageModel = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        return ActionResult.page(list, pageModel);
    }

    /**
     * 地图下拉框列表
     *
     * @param
     * @return
     */
    @ApiOperation("地图下拉框列表")
    @GetMapping("/Selector")
    public ActionResult selector() {
        List<VisualDataMapEntity> list = visualDataMapService.getList();
        List<MapSelectorVO> listVOS = JsonUtil.getJsonToList(list, MapSelectorVO.class);
        ListVO vo = new ListVO();
        vo.setList(listVOS);
        return ActionResult.success(vo);
    }

    /**
     * 地图信息
     *
     * @param id 主键
     * @return
     */
    @ApiOperation("地图信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        VisualDataMapEntity entity = visualDataMapService.getInfo(id);
        MapInfoVO vo = JsonUtil.getJsonToBeanEx(entity, MapInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 获取地图数据
     *
     * @param id 主键
     * @return
     */
    @ApiOperation("获取地图数据")
    @GetMapping("/{id}/Data")
    public Map<String, Object> data(@PathVariable("id") String id) throws DataException {
        VisualDataMapEntity entity = visualDataMapService.getInfo(id);
        MapInfoVO mapVO = JsonUtil.getJsonToBeanEx(entity, MapInfoVO.class);
        Map<String, Object> vo = JsonUtil.stringToMap(mapVO.getData());
        return vo;
    }

    /**
     * 更新地图状态
     *
     * @param id
     * @return
     */
    @ApiOperation("更新地图状态")
    @PutMapping("/{id}/Actions/State")
    public ActionResult state(@PathVariable("id") String id) {
        VisualDataMapEntity entity = visualDataMapService.getInfo(id);
        if (entity != null) {
            entity.setEnabledMark("1".equals(String.valueOf(entity.getEnabledMark())) ? 0 : 1);
            visualDataMapService.update(id, entity);
            return ActionResult.success("更新地图成功");
        }
        return ActionResult.fail("更新失败，数据不存在");
    }

    /**
     * 新增
     *
     * @param mapCrForm 实体对象
     * @return
     */
    @ApiOperation("新增地图")
    @PostMapping
    public ActionResult create(@RequestBody @Valid MapCrForm mapCrForm) {
        VisualDataMapEntity entity = JsonUtil.getJsonToBean(mapCrForm, VisualDataMapEntity.class);
        if (visualDataMapService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ActionResult.fail("名称不能重复");
        }
        visualDataMapService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 修改
     *
     * @param id        主键
     * @param mapUpForm 实体对象
     * @return
     */
    @ApiOperation("修改地图")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody  @Valid MapUpForm mapUpForm) {
        VisualDataMapEntity entity = JsonUtil.getJsonToBean(mapUpForm, VisualDataMapEntity.class);
        if (visualDataMapService.isExistByFullName(entity.getFullName(), id)) {
            return ActionResult.fail("名称不能重复");
        }
        boolean flag = visualDataMapService.update(id, entity);
        if (flag == false) {
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return
     */
    @ApiOperation("删除地图")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        VisualDataMapEntity entity = visualDataMapService.getInfo(id);
        if (entity != null) {
            visualDataMapService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }
}

