package smart.base.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.base.model.moduledataauthorize.DataAuthorizeCrForm;
import smart.base.model.moduledataauthorize.DataAuthorizeInfoVO;
import smart.base.model.moduledataauthorize.DataAuthorizeListVO;
import smart.base.model.moduledataauthorize.DataAuthorizeUpForm;
import smart.base.entity.ModuleDataAuthorizeEntity;
import smart.exception.DataException;
import smart.base.service.ModuleDataAuthorizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 数据权限配置
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "数据权限字段管理", value = "ModuleDataAuthorize")
@RestController
@RequestMapping("/Base/ModuleDataAuthorize")
public class ModuleDataAuthorizeController {

    @Autowired
    private ModuleDataAuthorizeService dataAuthorizeService;

    /**
     * 获取数据权限配置信息列表
     *
     * @param moduleId 功能主键
     * @return
     */
    @ApiOperation("获取字段列表")
    @GetMapping("/{moduleId}/List")
    public ActionResult list(@PathVariable("moduleId") String moduleId) {
        List<ModuleDataAuthorizeEntity> data = dataAuthorizeService.getList(moduleId);
        List<DataAuthorizeListVO> list= JsonUtil.getJsonToList(data,DataAuthorizeListVO.class);
        ListVO<DataAuthorizeListVO> vo=new ListVO<>();
        vo.setList(list);
        return ActionResult.success(vo);
    }

    /**
     * 获取数据权限配置信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取数据权限配置信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id)throws DataException {
        ModuleDataAuthorizeEntity entity = dataAuthorizeService.getInfo(id);
        DataAuthorizeInfoVO vo= JsonUtil.getJsonToBeanEx(entity,DataAuthorizeInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建数据权限配置
     *
     * @param dataAuthorizeCrForm 实体对象
     * @return
     */
    @ApiOperation("新建数据权限配置")
    @PostMapping
    public ActionResult create(@RequestBody @Valid DataAuthorizeCrForm dataAuthorizeCrForm) {
        ModuleDataAuthorizeEntity entity = JsonUtil.getJsonToBean(dataAuthorizeCrForm, ModuleDataAuthorizeEntity.class);
        dataAuthorizeService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 更新数据权限配置
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新数据权限配置")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid DataAuthorizeUpForm dataAuthorizeUpForm) {
        ModuleDataAuthorizeEntity entity = JsonUtil.getJsonToBean(dataAuthorizeUpForm, ModuleDataAuthorizeEntity.class);
        boolean flag=dataAuthorizeService.update(id, entity);
        if(flag==false){
            return ActionResult.success("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除数据权限配置
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除数据权限配置")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        ModuleDataAuthorizeEntity entity = dataAuthorizeService.getInfo(id);
        if (entity != null) {
            dataAuthorizeService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

}
