package smart.base.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.base.vo.PaginationVO;
import smart.base.model.datainterface.*;
import smart.base.entity.DataInterfaceEntity;
import smart.base.entity.DictionaryDataEntity;
import smart.exception.DataException;
import smart.base.service.DataInterfaceService;
import smart.base.service.DictionaryDataService;
import smart.permission.model.user.UserAllModel;
import smart.permission.service.UserService;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据接口
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-15 10:29
 */
@Api(tags = "数据接口", value = "DataInterface")
@RestController
@RequestMapping(value = "/Base/DataInterface")
public class DataInterfaceController {
    @Autowired
    private DataInterfaceService dataInterfaceService;
    @Autowired
    private UserService userService;
    @Autowired
    private DictionaryDataService dictionaryDataService;

    /**
     * 获取接口列表(分页)
     *
     * @param pagination
     * @return
     */
    @ApiOperation("获取接口列表(分页)")
    @GetMapping
    public ActionResult getList(PaginationDataInterface pagination) {
        List<DataInterfaceEntity> data = dataInterfaceService.getList(pagination);
        List<UserAllModel> userServiceAll = userService.getAll();
        for (DataInterfaceEntity entity : data) {
            UserAllModel userAllVO = userServiceAll.stream().filter(t -> t.getId().equals(entity.getCreatorUser())).findFirst().orElse(null);
            if (userAllVO != null) {
                entity.setCreatorUser(userAllVO.getRealName() + "/" + userAllVO.getAccount());
            }
        }
        List<DataInterfaceListVO> list = JsonUtil.getJsonToList(data, DataInterfaceListVO.class);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        return ActionResult.page(list, paginationVO);
    }

    /**
     * 获取接口列表下拉框
     *
     * @return
     */
    @ApiOperation("获取接口列表下拉框")
    @GetMapping("/Selector")
    public ActionResult getSelector() {
        List<DataInterfaceTreeModel> tree = new ArrayList<>();
        List<DataInterfaceEntity> data = dataInterfaceService.getList();
        for (DataInterfaceEntity entity : data) {
            DictionaryDataEntity dictionaryDataEntity = dictionaryDataService.getInfo(entity.getCategoryId());
            if (dictionaryDataEntity != null) {
                DataInterfaceTreeModel firstModel = JsonUtil.getJsonToBean(dictionaryDataEntity, DataInterfaceTreeModel.class);
                firstModel.setCategoryId("0");
                tree.add(firstModel);
                DataInterfaceTreeModel treeModel = JsonUtil.getJsonToBean(entity, DataInterfaceTreeModel.class);
                treeModel.setCategoryId("1");
                treeModel.setParentId(dictionaryDataEntity.getId());
                tree.add(treeModel);
            }
        }
        tree = tree.stream().distinct().collect(Collectors.toList());
        List<SumTree<DataInterfaceTreeModel>> sumTrees = TreeDotUtils.convertListToTreeDot(tree);
        List<DataInterfaceTreeVO> list = JsonUtil.getJsonToList(sumTrees, DataInterfaceTreeVO.class);
        ListVO vo = new ListVO();
        vo.setList(list);
        return ActionResult.success(list);
    }

    /**
     * 获取接口数据
     *
     * @param id
     * @return
     */
    @ApiOperation("获取接口数据")
    @GetMapping("/{id}")
    public ActionResult getInfo(@PathVariable("id") String id) throws DataException {
        DataInterfaceEntity entity = dataInterfaceService.getInfo(id);
        DataInterfaceVo vo = JsonUtil.getJsonToBeanEx(entity, DataInterfaceVo.class);
        return ActionResult.success(vo);
    }

    /**
     * 添加接口
     *
     * @param dataInterfaceCrForm
     * @return
     */
    @ApiOperation("添加接口")
    @PostMapping
    public ActionResult create(@RequestBody @Valid DataInterfaceCrForm dataInterfaceCrForm) throws DataException {
        DataInterfaceEntity entity = JsonUtil.getJsonToBean(dataInterfaceCrForm, DataInterfaceEntity.class);
        if (dataInterfaceService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ActionResult.fail("名称不能重复");
        }
        dataInterfaceService.create(entity);
        return ActionResult.success("接口创建成功");
    }

    /**
     * 修改接口
     *
     * @param dataInterfaceUpForm
     * @param id
     * @return
     */
    @ApiOperation("修改接口")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody @Valid DataInterfaceUpForm dataInterfaceUpForm, @PathVariable("id") String id) throws DataException {
        DataInterfaceEntity entity = JsonUtil.getJsonToBeanEx(dataInterfaceUpForm, DataInterfaceEntity.class);
        if (dataInterfaceService.isExistByFullName(entity.getFullName(), id)) {
            return ActionResult.fail("名称不能重复");
        }
        boolean flag = dataInterfaceService.update(entity, id);
        if (flag == false) {
            return ActionResult.fail("接口修改失败，数据不存在");
        }
        return ActionResult.success("接口修改成功");
    }

    /**
     * 删除接口
     *
     * @param id
     * @return
     */
    @ApiOperation("删除接口")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable String id) {
        DataInterfaceEntity entity = dataInterfaceService.getInfo(id);
        if (entity != null) {
            dataInterfaceService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /**
     * 更新接口状态
     *
     * @param id
     * @return
     */
    @ApiOperation("更新接口状态")
    @PutMapping("/{id}/Actions/State")
    public ActionResult update(@PathVariable("id") String id) throws DataException {
        DataInterfaceEntity entity = dataInterfaceService.getInfo(id);
        if (entity != null) {
            if (entity.getEnabledMark() == 0) {
                entity.setEnabledMark(1);
            } else {
                entity.setEnabledMark(0);
            }
            dataInterfaceService.update(entity, id);
            return ActionResult.success("更新接口状态成功");
        }
        return ActionResult.fail("更新接口状态失败，数据不存在");
    }

    /**
     * 访问接口
     *
     * @param id
     * @return
     */
    @ApiOperation("访问接口")
    @GetMapping("/{id}/Actions/Response")
    public ActionResult infoToId(@PathVariable("id") String id) {
       return dataInterfaceService.infoToId(id);
    }

}
