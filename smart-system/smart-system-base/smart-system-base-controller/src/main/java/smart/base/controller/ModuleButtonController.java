package smart.base.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.base.Pagination;
import smart.base.model.button.*;
import smart.base.entity.ModuleButtonEntity;
import smart.exception.DataException;
import smart.base.service.ModuleButtonService;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 按钮权限
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "按钮权限", value = "ModuleButton")
@RestController
@RequestMapping("/Base/ModuleButton")
public class ModuleButtonController {

    @Autowired
    private ModuleButtonService moduleButtonService;

    /**
     * 按钮按钮权限列表
     *
     * @param menuId 功能主键
     * @return
     */
    @ApiOperation("获取按钮权限列表")
    @GetMapping("/{menuId}/List")
    public ActionResult list(@PathVariable("menuId") String menuId, Pagination pagination) {
        List<ModuleButtonEntity> data = moduleButtonService.getList(menuId,pagination);
        List<ButtonTreeListModel> treeList = JsonUtil.getJsonToList(data, ButtonTreeListModel.class);
        List<SumTree<ButtonTreeListModel>> sumTrees = TreeDotUtils.convertListToTreeDot(treeList);
        if (data.size() > sumTrees.size()) {
            List<ButtonTreeListVO> list = JsonUtil.getJsonToList(sumTrees, ButtonTreeListVO.class);
            ListVO<ButtonTreeListVO> treeVo = new ListVO<>();
            treeVo.setList(list);
            return ActionResult.success(treeVo);
        }
        List<ButtonListVO> list = JsonUtil.getJsonToList(treeList, ButtonListVO.class);
        ListVO<ButtonListVO> treeVo1 = new ListVO<>();
        treeVo1.setList(list);
        return ActionResult.success(treeVo1);
    }


    /**
     * 按钮按钮权限列表
     *
     * @param menuId 功能主键
     * @return
     */
    @ApiOperation("获取按钮权限下拉框")
    @GetMapping("/{menuId}/Selector")
    public ActionResult selectList(@PathVariable("menuId") String menuId) {
        List<ModuleButtonEntity> data = moduleButtonService.getList(menuId);
        List<ButtonTreeListModel> treeList = JsonUtil.getJsonToList(data, ButtonTreeListModel.class);
        List<SumTree<ButtonTreeListModel>> sumTrees = TreeDotUtils.convertListToTreeDot(treeList);
        List<ButtonTreeListVO> list = JsonUtil.getJsonToList(sumTrees, ButtonTreeListVO.class);
        ListVO<ButtonTreeListVO> treeVo = new ListVO<>();
        treeVo.setList(list);
        return ActionResult.success(treeVo);
    }


    /**
     * 获取按钮权限信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取按钮权限信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id)throws DataException {
        ModuleButtonEntity entity = moduleButtonService.getInfo(id);
        ModuleButtonInfoVO vo = JsonUtil.getJsonToBeanEx(entity, ModuleButtonInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建按钮权限
     *
     * @param moduleButtonCrForm 实体对象
     * @return
     */
    @ApiOperation("新建按钮权限")
    @PostMapping
    public ActionResult create(@RequestBody ModuleButtonCrForm moduleButtonCrForm) {
        ModuleButtonEntity entity = JsonUtil.getJsonToBean(moduleButtonCrForm, ModuleButtonEntity.class);
        moduleButtonService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 更新按钮权限
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新按钮权限")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody ModuleButtonUpForm moduleButtonUpForm) {
        ModuleButtonEntity entity = JsonUtil.getJsonToBean(moduleButtonUpForm, ModuleButtonEntity.class);
        boolean flag=moduleButtonService.update(id, entity);
        if(flag==false){
            return ActionResult.success("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除按钮权限
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除按钮权限")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        ModuleButtonEntity entity = moduleButtonService.getInfo(id);
        if (entity != null) {
            moduleButtonService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /**
     * 更新菜单状态
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新菜单状态")
    @PutMapping("/{id}/Actions/State")
    public ActionResult upState(@PathVariable("id") String id) {
        ModuleButtonEntity entity = moduleButtonService.getInfo(id);
        if (entity.getEnabledMark() == null || entity.getEnabledMark() == 1) {
            entity.setEnabledMark(0);
        } else {
            entity.setEnabledMark(1);
        }
       boolean flag= moduleButtonService.update(id, entity);
        if(flag==false){
            return ActionResult.success("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

}
