package smart.base.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.base.Pagination;
import smart.base.model.column.*;
import smart.base.entity.ModuleColumnEntity;
import smart.exception.DataException;
import smart.base.service.ModuleColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 列表权限
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "列表权限", value = "ModuleColumn")
@Validated
@RestController
@RequestMapping("/Base/ModuleColumn")
public class ModuleColumnController {

    @Autowired
    private ModuleColumnService moduleColumnService;

    /**
     * 获取列表权限信息列表
     *
     * @param moduleId 功能主键
     * @return
     */
    @ApiOperation("获取列表权限列表")
    @GetMapping("/{moduleId}/Fields")
    public ActionResult getList(@PathVariable("moduleId") String moduleId, Pagination pagination) {
        List<ModuleColumnEntity> list = moduleColumnService.getList(moduleId,pagination);
        List<ColumnListVO> listVos = JsonUtil.getJsonToList(list,ColumnListVO.class);
        ListVO<ColumnListVO> vo=new ListVO<>();
        vo.setList(listVos );
        return ActionResult.success(vo);
    }

    /**
     * 获取列表权限信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取列表权限信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id)throws DataException {
        ModuleColumnEntity entity = moduleColumnService.getInfo(id);
        ModuleColumnInfoVO vo= JsonUtil.getJsonToBeanEx(entity,ModuleColumnInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建列表权限
     *
     * @param moduleColumnCrForm 实体对象
     * @return
     */
    @ApiOperation("新建列表权限")
    @PostMapping
    public ActionResult create(@RequestBody @Valid ModuleColumnCrForm moduleColumnCrForm) {
        ModuleColumnEntity entity = JsonUtil.getJsonToBean(moduleColumnCrForm, ModuleColumnEntity.class);
        moduleColumnService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 更新列表权限
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新列表权限")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id,@RequestBody @Valid ModuleColumnUpForm moduleColumnUpForm) {
        ModuleColumnEntity entity = JsonUtil.getJsonToBean(moduleColumnUpForm, ModuleColumnEntity.class);
        boolean flag=moduleColumnService.update(id, entity);
        if(flag==false){
            return ActionResult.success("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除列表权限
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除列表权限")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        ModuleColumnEntity entity = moduleColumnService.getInfo(id);
        if (entity != null) {
            moduleColumnService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /**
     * 更新列表权限状态
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新列表权限状态")
    @PutMapping("/{id}/Actions/State")
    public ActionResult upState(@PathVariable("id") String id) {
        ModuleColumnEntity entity = moduleColumnService.getInfo(id);
        if (entity.getEnabledMark() == null || entity.getEnabledMark() == 1) {
            entity.setEnabledMark(0);
        } else {
            entity.setEnabledMark(1);
        }
        boolean flag=moduleColumnService.update(id, entity);
        if(flag==false){
            return ActionResult.success("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
         * 批量新建{"moduleId":"12f57de476a34980b80d2a8b21d1f11a","bindTable":"","bindTableName":"","columnJson":[{"fullName":"名称","enCode":"fullName"}]}
     */
    @ApiOperation("批量新建列表权限")
    @PostMapping("/Actions/Batch")
    public ActionResult batchcreate(@RequestBody @Valid ColumnBatchForm columnBatchForm) {
        List<ModuleColumnEntity> entitys =columnBatchForm.getColumnJson()!=null? JsonUtil.getJsonToList(columnBatchForm.getColumnJson(), ModuleColumnEntity.class):new ArrayList<>();
        for(ModuleColumnEntity entity:entitys){
            entity.setBindTable(columnBatchForm.getBindTable());
            entity.setBindTableName(columnBatchForm.getBindTableName());
            entity.setModuleId(columnBatchForm.getModuleId());
        }
        moduleColumnService.create(entitys);
        return ActionResult.success("新建成功");
    }
}
