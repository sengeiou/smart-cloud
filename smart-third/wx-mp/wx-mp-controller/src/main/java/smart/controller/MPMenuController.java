package smart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.MPEventContentEntity;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.base.Pagination;
import smart.exception.DataException;
import smart.exception.WxErrorException;
import smart.model.mpmenu.*;
import smart.util.treeutil.ListToTreeUtil;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import smart.service.MPEventContentService;
import smart.service.MPMenuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公众号菜单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "公众号菜单",description = "MPMenu")
@RestController
@RequestMapping("/WeChat/MPMenu")
public class MPMenuController {

    @Autowired
    private MPMenuService mpMenuService;
    @Autowired
    private MPEventContentService mpEventContentService;

    /**
     * 列表
     *
     * @param pagination
     * @return
     */
    @ApiOperation("公众号菜单列表")
    @GetMapping
    public ActionResult List(Pagination pagination) throws WxErrorException {
        List<MPMenuModel> data = mpMenuService.getList();
        List<MPMenuModel> dataAll = data;
        if (!StringUtils.isEmpty(pagination.getKeyword())) {
            data = data.stream().filter(t -> t.getFullName().contains(pagination.getKeyword())).collect(Collectors.toList());
        }
        List<MPMenuModel> list = JsonUtil.getJsonToList(ListToTreeUtil.treeWhere(data, dataAll), MPMenuModel.class);
        List<SumTree<MPMenuModel>> trees = TreeDotUtils.convertListToTreeDot(list);
        List<MPMenuListVo> result = JsonUtil.getJsonToList(trees,MPMenuListVo.class);
        ListVO vo = new ListVO();
        vo.setList(result);
        return ActionResult.success(vo);
    }

    /**
     * 列表
     *
     * @return
     */
    @ApiOperation("公众号菜单下拉框列表")
    @GetMapping("/Selector")
    public ActionResult Selector() throws WxErrorException {
        List<MPMenuModel> data = mpMenuService.getList();
        List<SumTree<MPMenuModel>> trees = TreeDotUtils.convertListToTreeDot(data);
        List<MPMenuSelectorVo> vos = JsonUtil.getJsonToList(trees,MPMenuSelectorVo.class);
        ListVO vo = new ListVO();
        vo.setList(vos);
        return ActionResult.success(vo);
    }

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取菜单信息")
    @GetMapping("/{id}")
    public ActionResult Info(@PathVariable("id") String id) throws WxErrorException, DataException {
        List<MPMenuModel> data = mpMenuService.getList();
        MPMenuModel model = data.stream().filter(t->t.getId().equals(id)).findFirst().orElse(new MPMenuModel());
        MPMenuInfoVO infoVO = new MPMenuInfoVO();
        if(model!=null){
            infoVO = JsonUtil.getJsonToBeanEx(model,MPMenuInfoVO.class);
            if("click".equals(infoVO.getType())){
                MPEventContentEntity contentEntity = mpEventContentService.getInfo(id);
                infoVO.setContent(contentEntity.getContent());
            }
        }
        return ActionResult.success(infoVO);
    }

    /**
     * 新建
     *
     * @param mpMenuForm 实体对象
     * @return
     */
    @ApiOperation("新建菜单")
    @PostMapping
    public ActionResult create(@RequestBody @Valid MPMenuForm mpMenuForm) throws WxErrorException {
        MPMenuModel entity = JsonUtil.getJsonToBean(mpMenuForm, MPMenuModel.class);
        int strLen = entity.getFullName().length();
        if ("0".equals(entity.getParentId())) {
            if (strLen > 16) {
                return ActionResult.fail("操作失败，一级菜单文字最多不能超过16字节");
            }
        } else {
            if (strLen > 40) {
                return ActionResult.fail("操作失败，二级菜单文字最多不能超过40字节");
            }
        }
        if ("view".equals(entity.getType())) {
            entity.setContent(null);
        } else if ("click".equals(entity.getType())) {
            entity.setUrl(null);
        } else {
            entity.setContent(null);
            entity.setUrl(null);
        }
        List<MPMenuModel> menuList = mpMenuService.getList();
        boolean isExist = menuList.stream().filter(m -> String.valueOf(m.getFullName()).equals(String.valueOf(mpMenuForm.getFullName()))).collect(Collectors.toList()).size()>0;
        if(isExist){
            return ActionResult.fail("操作失败，名称重复");
        }
        menuList.add(entity);
        if (menuList.stream().filter(t -> "-1".equals(t.getParentId())).count() > 3) {
            return ActionResult.fail("操作失败，最多3个一级菜单");
        } else if (menuList.stream().filter(t -> t.getParentId().equals(entity.getParentId())).count() > 5) {
            return ActionResult.fail("操作失败，最多5个二级菜单");
        } else {
            //同步List
            mpMenuService.SyncMenu(menuList);
            mpMenuService.create(entity);
            return ActionResult.success("操作成功");
        }
    }

    /**
     * 更新
     *
     * @param id  主键值
     * @param mpMenuForm 实体对象
     * @return
     */
    @ApiOperation("更新菜单")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid MPMenuForm mpMenuForm) throws WxErrorException {
        MPMenuModel entity = JsonUtil.getJsonToBean(mpMenuForm, MPMenuModel.class);
        if ("view".equals(entity.getType())) {
            entity.setContent(null);
        } else if ("click".equals(entity.getType())) {
            entity.setUrl(null);
        } else {
            entity.setContent(null);
            entity.setUrl(null);
        }
        List<MPMenuModel> menuList = mpMenuService.getList();
        if (!StringUtils.isEmpty(id)) {
            List<MPMenuModel> isExist = menuList.stream().filter(t -> !t.getId().equals(id) && t.getFullName().equals(mpMenuForm.getFullName())).collect(Collectors.toList());
            if(isExist.size()>0){
                return ActionResult.fail("操作失败，名称重复");
            }
        }
        MPMenuModel menuModel = menuList.stream().filter(m -> m.getId().equals(id)).findFirst().get();
        menuList.remove(menuModel);
        menuList.add(entity);
        //同步List
        mpMenuService.SyncMenu(menuList);
       boolean flag= mpMenuService.update(id, entity);
        if(flag==false){
            return ActionResult.fail("操作失败，数据不存在");
        }
        return ActionResult.success("操作成功");
    }

    /**
     * 删除
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) throws WxErrorException {
        List<MPMenuModel> menuList = mpMenuService.getList();
        MPMenuModel menuModel = menuList.stream().filter(m -> m.getId().equals(id)).findFirst().get();
        menuList.remove(menuModel);
        //同步List
        mpMenuService.SyncMenu(menuList);
        boolean flag=mpMenuService.delete(id);
        if (flag==false){
            return ActionResult.fail("删除失败，数据不存在");
        }
        return ActionResult.success("删除成功");
    }

}
