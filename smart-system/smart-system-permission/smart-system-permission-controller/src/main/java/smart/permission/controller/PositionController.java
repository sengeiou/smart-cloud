package smart.permission.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.base.vo.PaginationVO;
import smart.base.entity.DictionaryDataEntity;
import smart.base.service.DictionaryDataService;
import smart.permission.entity.OrganizeEntity;
import smart.permission.entity.PositionEntity;
import smart.permission.entity.UserEntity;
import smart.permission.entity.UserRelationEntity;
import smart.exception.DataException;
import smart.permission.model.position.*;
import smart.permission.service.OrganizeService;
import smart.permission.service.PositionService;
import smart.permission.service.UserRelationService;
import smart.permission.service.UserService;
import smart.util.treeutil.ListToTreeUtil;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位信息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "岗位管理", value = "Position")
@RestController
@RequestMapping("/Permission/Position")
public class PositionController {
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private UserService userService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private DictionaryDataService dictionaryDataService;

    /**
     * 获取岗位管理信息列表
     *
     * @param paginationPosition
     * @return
     */
    @ApiOperation("获取岗位列表（分页）")
    @GetMapping
    public ActionResult list(PaginationPosition paginationPosition) {
        List<OrganizeEntity> list = organizeService.getList().stream().filter(t -> "1".equals(String.valueOf(t.getEnabledMark()))).collect(Collectors.toList());
        List<OrganizeEntity> dataAll = JsonUtil.getJsonToList(ListToTreeUtil.treeWhere(paginationPosition.getOrganizeId(), list), OrganizeEntity.class);
        List<String> organizeIds = dataAll.stream().map(t -> t.getId()).collect(Collectors.toList());
        organizeIds.add(paginationPosition.getOrganizeId());
        paginationPosition.setOrganizeId(String.join(",", organizeIds));
        List<PositionEntity> data = positionService.getList(paginationPosition);
        List<PositionListVO> voList = JsonUtil.getJsonToList(data, PositionListVO.class);
        //添加部门信息，部门映射到organizeId
        //添加部门信息
        for (PositionListVO entity1 : voList) {
            OrganizeEntity entity = list.stream().filter(t -> t.getId().equals(entity1.getDepartment())).findFirst().orElse(null);
            if (entity.getId().equals(entity1.getDepartment())) {
                entity1.setDepartment(entity.getFullName());
            }
        }
        //将type成中文名
        List<DictionaryDataEntity> dictionaryDataEntities= dictionaryDataService.getList().stream().filter(
                t->"dae93f2fd7cd4df999d32f8750fa6a1e".equals(t.getDictionaryTypeId())
        ).collect(Collectors.toList());
        for (PositionListVO entity1 : voList) {
            DictionaryDataEntity entity = dictionaryDataEntities.stream().filter(t -> t.getEnCode().equals(entity1.getType())).findFirst().orElse(null);
            if (entity != null) {
                entity1.setType(entity.getFullName());
            }
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationPosition, PaginationVO.class);
        return ActionResult.page(voList, paginationVO);
    }

    /**
     * 列表
     *
     * @return
     */
    @ApiOperation("列表")
    @GetMapping("/All")
    public ActionResult listAll() {
        List<PositionEntity> list = positionService.getList().stream().filter(
                t -> "1".equals(String.valueOf(t.getEnabledMark()))
        ).collect(Collectors.toList());
        List<PositionListAllVO> allVOS = JsonUtil.getJsonToList(list, PositionListAllVO.class);
        ListVO<PositionListAllVO> vo = new ListVO<>();
        vo.setList(allVOS);
        return ActionResult.success(vo);
    }

    /**
     * 列表
     *
     * @return
     */
    @ApiOperation("列表")
    @GetMapping("/getListAll")
    public ActionResult getListAll() {
        List<PositionEntity> list = positionService.getList();
        return ActionResult.success(list);
    }

    /**
     * 树形（机构+岗位）
     *
     * @return
     */
    @ApiOperation("获取岗位下拉列表（公司+部门+岗位）")
    @GetMapping("/Selector")
    public ActionResult selector() {
        List<PositionEntity> positionData = positionService.getList().stream().filter(
                t -> "1".equals(String.valueOf(t.getEnabledMark()))
        ).collect(Collectors.toList());
        List<OrganizeEntity> allOrganizeData = organizeService.getList();
        List<OrganizeEntity> organizeData = organizeService.getList().stream().filter(
                t -> "1".equals(String.valueOf(t.getEnabledMark()))
        ).collect(Collectors.toList());
        List<PosOrgModel> posOrgModels = JsonUtil.getJsonToList(organizeData, PosOrgModel.class);
        for (PosOrgModel entity1 : posOrgModels) {
            if ("department".equals(entity1.getType())) {
                entity1.setIcon("icon-ym icon-ym-tree-department1");
            } else if ("company".equals(entity1.getType())) {
                entity1.setIcon("icon-ym icon-ym-tree-organization3");
            }
        }
        for (PositionEntity entity : positionData) {
            PosOrgModel posOrgModel = JsonUtil.getJsonToBean(entity, PosOrgModel.class);
            posOrgModel.setParentId(entity.getOrganizeId());
            posOrgModel.setType("position");
            posOrgModel.setIcon("icon-ym icon-ym-tree-position1");
            posOrgModels.add(posOrgModel);
        }
        List<SumTree<PosOrgModel>> trees = TreeDotUtils.convertListToTreeDot(posOrgModels);
        List<PositionSelectorVO> listVO = JsonUtil.getJsonToList(trees, PositionSelectorVO.class);
        List<OrganizeEntity> entities = allOrganizeData.stream().filter(
                t -> t.getEnabledMark() == 0 && "-1".equals(t.getParentId())
        ).collect(Collectors.toList());
        Iterator<PositionSelectorVO> iterator = listVO.iterator();
        while (iterator.hasNext()) {
            PositionSelectorVO positionSelectorVO = iterator.next();
            for (OrganizeEntity entity : entities) {
                if (entity.getId().equals(positionSelectorVO.getParentId())) {
                    //使用迭代器的删除方法删除
                    iterator.remove();
                }
            }
        }
        ListVO vo = new ListVO();
        vo.setList(listVO);
        return ActionResult.success(vo);
    }

    /**
     * 获取岗位管理信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取岗位管理信息")
    @GetMapping("/{id}")
    public ActionResult getInfo(@PathVariable("id") String id) throws DataException {
        PositionEntity entity = positionService.getInfo(id);
        PositionInfoVO vo = JsonUtil.getJsonToBeanEx(entity, PositionInfoVO.class);
        return ActionResult.success(vo);
    }


    /**
     * 新建岗位管理
     *
     * @param positionCrForm 实体对象
     * @return
     */
    @ApiOperation("新建岗位管理")
    @PostMapping
    public ActionResult create(@RequestBody @Valid PositionCrForm positionCrForm) {
        PositionEntity entity = JsonUtil.getJsonToBean(positionCrForm, PositionEntity.class);
        if (positionService.isExistByFullName(positionCrForm.getFullName(), entity.getId())) {
            return ActionResult.fail("岗位名称不能重复");
        }
        if (positionService.isExistByEnCode(positionCrForm.getEnCode(), entity.getId())) {
            return ActionResult.fail("岗位编码不能重复");
        }
        positionService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 更新岗位管理
     *
     * @param id             主键值
     * @param positionUpForm 实体对象
     * @return
     */
    @ApiOperation("更新岗位管理")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid PositionUpForm positionUpForm) {
        PositionEntity entity = JsonUtil.getJsonToBean(positionUpForm, PositionEntity.class);
        if (positionService.isExistByFullName(positionUpForm.getFullName(), id)) {
            return ActionResult.fail("岗位名称不能重复");
        }
        if (positionService.isExistByEnCode(positionUpForm.getEnCode(), id)) {
            return ActionResult.fail("岗位编码不能重复");
        }
        boolean flag = positionService.update(id, entity);
        if (flag == false) {
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除岗位管理
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除岗位管理")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        PositionEntity entity = positionService.getInfo(id);
        if (entity != null) {
            List<UserRelationEntity> userRelList = userRelationService.getListByObjectId(id);
            for (UserRelationEntity entity1 : userRelList) {
                UserEntity entity2 = userService.getById(entity1.getUserId());
                if (entity2 != null) {
                    String newPositionId = entity2.getPositionId().replace(id, "");
                    if (entity2.getPositionId().contains(id)) {
                        if (newPositionId.length() != 0 && newPositionId.substring(0, 1) == ",") {
                            entity2.setPositionId(newPositionId.substring(1));
                        } else if (newPositionId.length() != 0) {
                            entity2.setPositionId(newPositionId.replace(",,", ","));
                        }
                    }
                }
            }
            userRelationService.deleteListByObjectId(id);
            positionService.delete(entity);
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
        PositionEntity entity = positionService.getInfo(id);
        if (entity != null) {
            if (entity.getEnabledMark() == null || entity.getEnabledMark() == 1) {
                entity.setEnabledMark(0);
            } else {
                entity.setEnabledMark(1);
            }
            positionService.update(id, entity);
            return ActionResult.success("更新成功");
        }
        return ActionResult.fail("更新失败,数据不存在");
    }

}
