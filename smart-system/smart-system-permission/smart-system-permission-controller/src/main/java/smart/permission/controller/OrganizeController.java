package smart.permission.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.util.StringUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.base.Pagination;
import smart.exception.DataException;
import smart.permission.model.organize.*;
import smart.permission.service.OrganizeService;
import smart.permission.service.UserService;
import smart.util.treeutil.ListToTreeUtil;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import smart.permission.entity.OrganizeEntity;
import smart.permission.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 组织机构
 * 组织架构：公司》部门》岗位》用户
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "组织管理", value = "Organize")
@RestController
@RequestMapping("/Permission/Organize")
public class OrganizeController {

    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private UserService userService;

    //---------------------------组织管理--------------------------------------------

    /**
     * 获取组织列表
     *
     * @param pagination
     * @return
     */
    @ApiOperation("获取组织列表")
    @GetMapping
    public ActionResult getList(Pagination pagination) {
        List<OrganizeEntity> data = organizeService.getList();
        List<OrganizeEntity> dataAll = data;
        List<OrganizeEntity> list = JsonUtil.getJsonToList(ListToTreeUtil.treeWhere(data, dataAll), OrganizeEntity.class);
        list = list.stream().filter(t -> "company".equals(t.getCategory())).collect(Collectors.toList());
        List<OraganizeModel> oraganizeList = JsonUtil.getJsonToList(list, OraganizeModel.class);
        List<SumTree<OraganizeModel>> trees = TreeDotUtils.convertListToTreeDot(oraganizeList);
        List<OraganizeListVO> listVO = JsonUtil.getJsonToList(trees, OraganizeListVO.class);
        if (!StringUtil.isEmpty(pagination.getKeyword())) {
            listVO = listVO.stream().filter(t -> t.getFullName().toLowerCase().contains(pagination.getKeyword()) || t.getEnCode().toLowerCase().contains(pagination.getKeyword())).collect(Collectors.toList());
        }
        ListVO vo = new ListVO();
        vo.setList(listVO);
        return ActionResult.success(vo);
    }

    /**
     * 获取组织下拉框列表
     *
     * @return
     */
    @ApiOperation("获取组织下拉框列表")
    @GetMapping("/Selector")
    public ActionResult getSelector(Pagination pagination) {
        List<OrganizeEntity> allList = organizeService.getList();
        List<OrganizeEntity> data = allList.stream().filter(t -> "1".equals(String.valueOf(t.getEnabledMark()))).collect(Collectors.toList());
        List<OrganizeEntity> dataAll = data;
        if (!StringUtil.isEmpty(pagination.getKeyword())) {
            data = data.stream().filter(
                    t -> t.getFullName().contains(pagination.getKeyword())|| t.getEnCode().contains(pagination.getKeyword())
            ).collect(Collectors.toList());
        }
        List<OrganizeEntity> list = JsonUtil.getJsonToList(ListToTreeUtil.treeWhere(data, dataAll), OrganizeEntity.class);
        list = list.stream().filter(t -> "company".equals(t.getCategory())).collect(Collectors.toList());
        List<OraganizeModel> models = JsonUtil.getJsonToList(list, OraganizeModel.class);
        for (OraganizeModel model : models) {
            model.setIcon("icon-ym icon-ym-tree-organization3");
        }
        List<OraganizeModel> modelAll = new ArrayList<>();
        modelAll.addAll(models);
        List<SumTree<OraganizeModel>> trees = TreeDotUtils.convertListToTreeDot(modelAll);
        List<OraganizeSelectorVO> listVO = JsonUtil.getJsonToList(trees, OraganizeSelectorVO.class);

        //将子节点全部删除
        Iterator<OraganizeSelectorVO> iterator = listVO.iterator();
        while (iterator.hasNext()) {
            OraganizeSelectorVO oraganizeSelectorVO = iterator.next();
            if (!"-1".equals(oraganizeSelectorVO.getParentId())&&!"0".equals(oraganizeSelectorVO.getParentId())) {
                iterator.remove();
            }
        }
        ListVO vo = new ListVO();
        vo.setList(listVO);
        return ActionResult.success(vo);
    }

    /**
     * 组织树形
     *
     * @return
     */
    @ApiOperation("获取组织/公司树形")
    @GetMapping("/Tree")
    public ActionResult tree() {
        List<OrganizeEntity> allList = organizeService.getList();
        List<OrganizeEntity> list = allList.stream().filter(t -> "1".equals(String.valueOf(t.getEnabledMark()))).collect(Collectors.toList());
        list = list.stream().filter(t -> "company".equals(t.getCategory())).collect(Collectors.toList());
        List<OraganizeModel> models = JsonUtil.getJsonToList(list, OraganizeModel.class);
        for (OraganizeModel model : models) {
            model.setIcon("icon-ym icon-ym-tree-organization3");
        }
        List<SumTree<OraganizeModel>> trees = TreeDotUtils.convertListToTreeDot(models);
        List<OrananizeTreeVO> listVO = JsonUtil.getJsonToList(trees, OrananizeTreeVO.class);
        //将子节点全部删除
        Iterator<OrananizeTreeVO> iterator = listVO.iterator();
        while (iterator.hasNext()) {
            OrananizeTreeVO orananizeTreeVO = iterator.next();
            if (!"-1".equals(orananizeTreeVO.getParentId())) {
                iterator.remove();
            }
        }
        ListVO vo = new ListVO();
        vo.setList(listVO);
        return ActionResult.success(vo);
    }

    /**
     * 获取组织信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取组织信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        OrganizeEntity entity = organizeService.getInfo(id);
        OraganizeInfoVO vo = JsonUtil.getJsonToBeanEx(entity, OraganizeInfoVO.class);
        return ActionResult.success(vo);
    }


    /**
     * 新建组织
     *
     * @param oraganizeCrForm
     * @return
     */
    @ApiOperation("新建组织")
    @PostMapping
    public ActionResult create(@RequestBody @Valid OraganizeCrForm oraganizeCrForm) {
        OrganizeEntity entity = JsonUtil.getJsonToBean(oraganizeCrForm, OrganizeEntity.class);
        entity.setCategory("company");
        if (organizeService.isExistByFullName(oraganizeCrForm.getFullName(), entity.getId())) {
            return ActionResult.fail("组织名称不能重复");
        }
        if (organizeService.isExistByEnCode(oraganizeCrForm.getEnCode(), entity.getId())) {
            return ActionResult.fail("组织编码不能重复");
        }
        organizeService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 更新组织
     *
     * @param id              主键值
     * @param oraganizeUpForm 实体对象
     * @return
     */
    @ApiOperation("更新组织")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid OraganizeUpForm oraganizeUpForm) {
        OrganizeEntity entity = JsonUtil.getJsonToBean(oraganizeUpForm, OrganizeEntity.class);
        if (id.equals(entity.getParentId())) {
            return ActionResult.fail("上级公司和公司不能是同一个");
        }
        if (organizeService.isExistByFullName(oraganizeUpForm.getFullName(), id)) {
            return ActionResult.fail("组织名称不能重复");
        }
        if (organizeService.isExistByEnCode(oraganizeUpForm.getEnCode(), id)) {
            return ActionResult.fail("组织编码不能重复");
        }
        boolean flag = organizeService.update(id, entity);
        if (flag == false) {
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除组织
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除组织")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        if (organizeService.allowdelete(id)) {
            OrganizeEntity organizeEntity = organizeService.getInfo(id);
            if (organizeEntity != null) {
                organizeService.delete(organizeEntity);
                return ActionResult.success("删除成功");
            }
            return ActionResult.fail("删除失败，数据不存在");
        } else {
            return ActionResult.fail("此记录被关联引用,不允许被删除");
        }
    }

    /**
     * 更新组织状态
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新组织状态")
    @PutMapping("/{id}/Actions/State")
    public ActionResult update(@PathVariable("id") String id) {
        OrganizeEntity organizeEntity = organizeService.getInfo(id);
        if (organizeEntity != null) {
            if (organizeEntity.getEnabledMark() == 1) {
                organizeEntity.setEnabledMark(0);
            } else {
                organizeEntity.setEnabledMark(1);
            }
            organizeService.update(organizeEntity.getId(), organizeEntity);
            return ActionResult.success("更新成功");
        }
        return ActionResult.success("更新失败，数据不存在");
    }


    //---------------------------部门管理--------------------------------------------

    /**
     * 获取部门列表
     *
     * @param companyId
     * @return
     */
    @ApiOperation("获取部门列表")
    @GetMapping("/{companyId}/Department")
    public ActionResult getListDepartment(@PathVariable("companyId") String companyId, Pagination pagination) {
        List<OrganizeEntity> dataAll = organizeService.getList();
        String parentId = dataAll.stream().filter(t -> t.getId().equals(companyId)).findFirst().orElse(new OrganizeEntity()).getId();
        List<OrganizeEntity> data = new ArrayList<>();
        //将部门之下的子集搜索出来
        List<OrganizeEntity> dataCopy = dataAll.stream().filter(t -> "department".equals(t.getCategory())).collect(Collectors.toList());
        for (OrganizeEntity entity : dataCopy) {
            List<OrganizeEntity> data1 = new ArrayList<>();
            data1.add(entity);
            String id = JsonUtil.getJsonToList(ListToTreeUtil.treeWhere(data1, dataAll), OrganizeEntity.class).stream().filter(t -> companyId.equals(t.getId())).findFirst().orElse(new OrganizeEntity()).getId();
            if (parentId.equals(id)) {
                data.add(entity);
            }
        }
        if (!StringUtil.isEmpty(pagination.getKeyword())) {
            data = data.stream().filter(
                    t -> t.getFullName().contains(pagination.getKeyword()) || t.getEnCode().contains(pagination.getKeyword())
            ).collect(Collectors.toList());
        }
        List<OraganizeModel> models = JsonUtil.getJsonToList(data, OraganizeModel.class);

//        给部门经理赋值
        for(OraganizeModel model:models){
            if(!StringUtil.isEmpty(model.getManager())){
                UserEntity entity=userService.getById(model.getManager());
                model.setManager(entity.getRealName()+"/"+entity.getAccount());
            }
        }
        List<SumTree<OraganizeModel>> trees = TreeDotUtils.convertListToTreeDot(models);
        //去掉子公司的部门
        trees= trees.stream().filter(t->parentId.equals(t.getParentId())).collect(Collectors.toList());
        List<OraganizeDepartListVO> listvo = JsonUtil.getJsonToList(trees, OraganizeDepartListVO.class);
        ListVO vo = new ListVO();
        vo.setList(listvo);
        return ActionResult.success(vo);
    }

    /**
     * 获取部门下拉框列表
     *
     * @return
     */
    @ApiOperation("获取部门下拉框列表")
    @GetMapping("/Department/Selector")
    public ActionResult getListDepartment() {
        List<OrganizeEntity> allList = organizeService.getList();
        List<OrganizeEntity> data = allList.stream().filter(t -> "1".equals(String.valueOf(t.getEnabledMark()))).collect(Collectors.toList());
        List<OraganizeModel> models = JsonUtil.getJsonToList(data, OraganizeModel.class);
        for (OraganizeModel model : models) {
            if("department".equals(model.getType())){
                model.setIcon("icon-ym icon-ym-tree-department1");
            }else if("company".equals(model.getType())){
                model.setIcon("icon-ym icon-ym-tree-organization3");
            }
        }
        List<SumTree<OraganizeModel>> trees = TreeDotUtils.convertListToTreeDot(models);
        List<OraganizeDepartSelectorListVO> listVO = JsonUtil.getJsonToList(trees, OraganizeDepartSelectorListVO.class);

        //将子节点全部删除
        Iterator<OraganizeDepartSelectorListVO> iterator = listVO.iterator();
         while (iterator.hasNext()) {
            OraganizeDepartSelectorListVO oraganizeDepartSelectorListVO = iterator.next();
           if(!"-1".equals(oraganizeDepartSelectorListVO.getParentId())){
               iterator.remove();
           }
        }

        ListVO vo = new ListVO();
        vo.setList(listVO);
        return ActionResult.success(vo);
    }


    /**
     * 新建部门
     *
     * @param oraganizeDepartCrForm
     * @return
     */
    @ApiOperation("新建部门")
    @PostMapping("/Department")
    public ActionResult createDepartment(@RequestBody @Valid OraganizeDepartCrForm oraganizeDepartCrForm) {
        OrganizeEntity entity = JsonUtil.getJsonToBean(oraganizeDepartCrForm, OrganizeEntity.class);
        entity.setCategory("department");
        if (organizeService.isExistByEnCode(oraganizeDepartCrForm.getEnCode(), entity.getId())) {
            return ActionResult.fail("部门编码不能重复");
        }
        organizeService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 更新部门
     *
     * @param id                    主键值
     * @param oraganizeDepartUpForm
     * @return
     */
    @ApiOperation("更新部门")
    @PutMapping("/Department/{id}")
    public ActionResult updateDepartment(@PathVariable("id") String id, @RequestBody @Valid OraganizeDepartUpForm oraganizeDepartUpForm) {
        OrganizeEntity entity = JsonUtil.getJsonToBean(oraganizeDepartUpForm, OrganizeEntity.class);
        if (id.equals(entity.getParentId())) {
            return ActionResult.fail("上级部门和部门不能是同一个");
        }
        if (organizeService.isExistByFullName(oraganizeDepartUpForm.getFullName(), id)) {
            return ActionResult.fail("部门名称不能重复");
        }
        if (organizeService.isExistByEnCode(oraganizeDepartUpForm.getEnCode(), id)) {
            return ActionResult.fail("部门编码不能重复");
        }
        boolean flag = organizeService.update(id, entity);
        if (flag == false) {
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除部门
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除部门")
    @DeleteMapping("/Department/{id}")
    public ActionResult deleteDepartment(@PathVariable("id") String id) {
        if (organizeService.allowdelete(id)) {
            OrganizeEntity organizeEntity = organizeService.getInfo(id);
            if (organizeEntity != null) {
                organizeService.delete(organizeEntity);
                return ActionResult.success("删除成功");
            }
            return ActionResult.fail("删除失败，数据不存在");
        } else {
            return ActionResult.fail("此记录被关联引用,不允许被删除");
        }
    }

    /**
     * 更新部门状态
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新部门状态")
    @PutMapping("/Department/{id}/Actions/State")
    public ActionResult updateDepartment(@PathVariable("id") String id) {
        OrganizeEntity organizeEntity = organizeService.getInfo(id);
        if (organizeEntity != null) {
            if (organizeEntity.getEnabledMark() == 1) {
                organizeEntity.setEnabledMark(0);
            } else {
                organizeEntity.setEnabledMark(1);
            }
            organizeService.update(organizeEntity.getId(), organizeEntity);
            return ActionResult.success("更新成功");
        }
        return ActionResult.fail("更新失败，数据不存在");
    }

    /**
     * 获取部门信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取部门信息")
    @GetMapping("/Department/{id}")
    public ActionResult infoDepartment(@PathVariable("id") String id) throws DataException {
        OrganizeEntity entity = organizeService.getInfo(id);
        OraganizeDepartInfoVO vo = JsonUtil.getJsonToBeanEx(entity, OraganizeDepartInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 通过id获取OrganizeEntity
     * @param organizeId
     * @return
     * @throws DataException
     */
    @GetMapping("/getById/{organizeId}")
    public OrganizeEntity getById(@PathVariable("organizeId") String organizeId) {
        OrganizeEntity entity = organizeService.getById(organizeId);
        return entity;
    }

    @GetMapping("/getList")
    public List<OrganizeEntity> getList(){
        List<OrganizeEntity> list = organizeService.getList();
        return list;
    }

}
