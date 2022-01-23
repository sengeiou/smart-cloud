package smart.base.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.base.model.moduledataauthorizescheme.DataAuthorizeSchemeCrForm;
import smart.base.model.moduledataauthorizescheme.DataAuthorizeSchemeInfoVO;
import smart.base.model.moduledataauthorizescheme.DataAuthorizeSchemeListVO;
import smart.base.model.moduledataauthorizescheme.DataAuthorizeSchemeUpForm;
import smart.base.entity.ModuleDataAuthorizeSchemeEntity;
import smart.exception.DataException;
import smart.base.service.ModuleDataAuthorizeSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 数据权限方案
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "数据权限方案", value = "ModuleDataAuthorizeScheme")
@RestController
@RequestMapping("/Base/ModuleDataAuthorizeScheme")
public class ModuleDataAuthorizeSchemeController {

    @Autowired
    private ModuleDataAuthorizeSchemeService schemeService;

    /**
     * 列表
     *
     * @param moduleId 功能主键
     * @return
     */
    @ApiOperation("方案列表")
    @GetMapping("/{moduleId}/List")
    public ActionResult list(@PathVariable("moduleId") String moduleId) {
        List<ModuleDataAuthorizeSchemeEntity> data = schemeService.getList(moduleId);
        List<DataAuthorizeSchemeListVO> list= JsonUtil.getJsonToList(data,DataAuthorizeSchemeListVO.class);
        ListVO<DataAuthorizeSchemeListVO> vo=new ListVO<>();
        vo.setList(list);
        return ActionResult.success(vo);
    }

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取方案信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id)throws DataException {
        ModuleDataAuthorizeSchemeEntity entity = schemeService.getInfo(id);
        DataAuthorizeSchemeInfoVO vo= JsonUtil.getJsonToBeanEx(entity,DataAuthorizeSchemeInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建
     *
     * @param dataAuthorizeSchemeCrForm 实体对象
     * @return
     */
    @ApiOperation("新建方案")
    @PostMapping
    public ActionResult create(@RequestBody @Valid DataAuthorizeSchemeCrForm dataAuthorizeSchemeCrForm) {
        ModuleDataAuthorizeSchemeEntity entity = JsonUtil.getJsonToBean(dataAuthorizeSchemeCrForm, ModuleDataAuthorizeSchemeEntity.class);
        schemeService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 更新
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新方案")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid DataAuthorizeSchemeUpForm dataAuthorizeSchemeUpForm) {
        ModuleDataAuthorizeSchemeEntity entity = JsonUtil.getJsonToBean(dataAuthorizeSchemeUpForm, ModuleDataAuthorizeSchemeEntity.class);
        boolean flag=schemeService.update(id, entity);
        if(flag==false){
            return ActionResult.success("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除方案")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        ModuleDataAuthorizeSchemeEntity entity = schemeService.getInfo(id);
        if (entity != null) {
            schemeService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

}
