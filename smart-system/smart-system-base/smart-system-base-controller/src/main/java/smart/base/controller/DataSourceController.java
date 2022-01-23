package smart.base.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.base.Page;
import smart.base.model.dblink.*;
import smart.base.entity.DbLinkEntity;
import smart.exception.DataException;
import smart.base.service.DblinkService;
import smart.permission.model.user.UserAllModel;
import smart.permission.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 数据连接
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "数据连接", value = "DataSource")
@RestController
@RequestMapping("/Base/DataSource")
public class DataSourceController {

    @Autowired
    private DblinkService dblinkService;
    @Autowired
    private UserService userService;
    /**
     * 列表
     * @return
     */
    @GetMapping("/Selector")
    @ApiOperation("获取数据连接下拉框列表")
    public ActionResult selectorList() {
        List<DbLinkEntity> list = dblinkService.getList();
        List<DbLinkSelectorListVO> result = JsonUtil.getJsonToList(list, DbLinkSelectorListVO.class);
        ListVO vo= new ListVO();
        vo.setList(result);
        return ActionResult.success(vo);
    }


    /**
     * 列表
     * @param page 关键字
     * @return
     */
    @GetMapping
    @ApiOperation("获取数据连接列表")
    public ActionResult getList(Page page) {
        List<DbLinkEntity> list = dblinkService.getList(page.getKeyword());
        List<DbLinkListVO> result = JsonUtil.getJsonToList(list, DbLinkListVO.class);
        List<UserAllModel> userAllVOS=userService.getAll();
        for(DbLinkListVO vo:result){
            for(UserAllModel userVo:userAllVOS){
                if(userVo.getId().equals(vo.getCreatorUser())){
                    vo.setCreatorUser(userVo.getRealName()+"/"+userVo.getAccount());
                }
                if(userVo.getId().equals(vo.getLastModifyUser())){
                    vo.setLastModifyUser(userVo.getRealName()+"/"+userVo.getAccount());
                }
            }
        }
        ListVO vo= new ListVO();
        vo.setList(result);
        return ActionResult.success(vo);
    }

    /**
     * 信息
     * @param id 主键
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("获取数据连接")
    public ActionResult get(@PathVariable("id") String id) throws DataException {
        DbLinkEntity entity = dblinkService.getInfo(id);
        DbLinkInfoVO vo = JsonUtil.getJsonToBeanEx(entity, DbLinkInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建
     * @param dbLinkCrForm dto实体
     * @return
     */
    @PostMapping
    @ApiOperation("添加数据连接")
    public ActionResult create(@RequestBody @Valid DbLinkCrForm dbLinkCrForm) {
        DbLinkEntity entity = JsonUtil.getJsonToBean(dbLinkCrForm, DbLinkEntity.class);
        if (dblinkService.isExistByFullName(entity.getFullName(),entity.getId())){
            return ActionResult.fail("名称不能重复");
        }
        dblinkService.create(entity);
        return ActionResult.success("创建成功");
    }

    /**
     * 更新
     * @param id 主键
     * @param dbLinkUpForm dto实体
     * @return
     */
    @PutMapping("/{id}")
    @ApiOperation("修改数据连接")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid DbLinkUpForm dbLinkUpForm) {
        DbLinkEntity entity = JsonUtil.getJsonToBean(dbLinkUpForm, DbLinkEntity.class);
        if (dblinkService.isExistByFullName(entity.getFullName(),id)){
            return ActionResult.fail("名称不能重复");
        }
        boolean flag = dblinkService.update(id, entity);
        if (flag==false){
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除
     * @param id 主键
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除数据连接")
    public ActionResult delete(@PathVariable("id") String id) {
        DbLinkEntity entity = dblinkService.getInfo(id);
        if (entity != null) {
            dblinkService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    @PostMapping("/Actions/Test")
    @ApiOperation("测试连接")
    public ActionResult test(@RequestBody DbLinkTestForm dbLinkTestForm) {
        DbLinkEntity entity = JsonUtil.getJsonToBean(dbLinkTestForm, DbLinkEntity.class);
        boolean data = dblinkService.testDbConnection(entity);
        if(data){
            return ActionResult.success("连接成功");
        }else {
            return ActionResult.fail("连接失败");
        }
    }
}
