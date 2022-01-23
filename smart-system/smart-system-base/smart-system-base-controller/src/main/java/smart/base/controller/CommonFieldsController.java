package smart.base.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.base.model.comfields.ComFieldsCrForm;
import smart.base.model.comfields.ComFieldsInfoVO;
import smart.base.model.comfields.ComFieldsListVO;
import smart.base.model.comfields.ComFieldsUpForm;
import smart.base.entity.ComFieldsEntity;
import smart.exception.DataException;
import smart.base.service.ComFieldsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 常用字段表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-15 10:29
 */
@RestController
@Api(tags = "常用字段", value = "CommonFields")
@RequestMapping("/Base/CommonFields")
public class CommonFieldsController {

    @Autowired
    private ComFieldsService comFieldsService;

    @ApiOperation("常用字段列表")
    @GetMapping
    public ActionResult list() {
        List<ComFieldsEntity> data = comFieldsService.getList();
        List<ComFieldsListVO> list= JsonUtil.getJsonToList(data,ComFieldsListVO.class);
        ListVO<ComFieldsListVO> vo=new ListVO<>();
        vo.setList(list);
        return ActionResult.success(vo);
    }

    @ApiOperation("常用字段详情")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        ComFieldsEntity entity = comFieldsService.getInfo(id);
        ComFieldsInfoVO vo= JsonUtil.getJsonToBeanEx(entity,ComFieldsInfoVO.class);
        return ActionResult.success(vo);
    }

    @ApiOperation("新建常用字段")
    @PostMapping
    public ActionResult create(@RequestBody @Valid ComFieldsCrForm comFieldsCrForm) {
        ComFieldsEntity entity = JsonUtil.getJsonToBean(comFieldsCrForm, ComFieldsEntity.class);
        if (comFieldsService.isExistByFullName(entity.getField(),entity.getId())){
            return ActionResult.fail("名称不能重复");
        }
        comFieldsService.create(entity);
        return ActionResult.success("新建成功");
    }

    @ApiOperation("修改常用字段")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid ComFieldsUpForm comFieldsUpForm) {
        ComFieldsEntity entity = JsonUtil.getJsonToBean(comFieldsUpForm, ComFieldsEntity.class);
        if (comFieldsService.isExistByFullName(entity.getField(),id)){
            return ActionResult.fail("名称不能重复");
        }
        boolean flag = comFieldsService.update(id, entity);
        if (flag==false){
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    @ApiOperation("删除常用字段")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        ComFieldsEntity entity = comFieldsService.getInfo(id);
        if (entity != null) {
            comFieldsService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败,数据不存在");
    }
}

