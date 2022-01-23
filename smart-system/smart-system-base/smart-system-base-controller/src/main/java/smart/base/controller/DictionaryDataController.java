package smart.base.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.util.StringUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.base.model.dictionarydata.*;
import smart.base.model.dictionarytype.DictionaryTypeSelectModel;
import smart.base.model.dictionarytype.DictionaryTypeSelectVO;
import smart.base.entity.DictionaryDataEntity;
import smart.base.entity.DictionaryTypeEntity;
import smart.exception.DataException;
import smart.base.service.DictionaryDataService;
import smart.base.service.DictionaryTypeService;
import smart.util.treeutil.ListToTreeUtil;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import smart.util.type.StringNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典数据
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "数据字典", value = "DictionaryData")
@RestController
@RequestMapping("/Base/DictionaryData")
public class DictionaryDataController {

    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private DictionaryTypeService dictionaryTypeService;

    /**
     * 获取数据字典列表
     *
     * @return
     */
    @ApiOperation("获取数据字典列表")
    @GetMapping("/{dictionaryTypeId}")
    public ActionResult bindDictionary(@PathVariable("dictionaryTypeId") String dictionaryTypeId, PageDictionaryData pageDictionaryData) {
        List<DictionaryDataEntity> data = dictionaryDataService.getList(dictionaryTypeId);
        List<DictionaryDataEntity> dataAll = data;
        if(StringUtil.isNotEmpty(pageDictionaryData.getKeyword())){
            data = data.stream().filter(t->t.getFullName().contains(pageDictionaryData.getKeyword()) || t.getEnCode().contains(pageDictionaryData.getKeyword())).collect(Collectors.toList());
        }
        if (pageDictionaryData.getIsTree() != null && StringNumber.ONE.equals(pageDictionaryData.getIsTree())) {
            List<DictionaryDataEntity> treeData = JsonUtil.getJsonToList(ListToTreeUtil.treeWhere(data, dataAll), DictionaryDataEntity.class);
            List<DictionaryDataModel> voListVO = JsonUtil.getJsonToList(treeData, DictionaryDataModel.class);
            List<SumTree<DictionaryDataModel>> sumTrees = TreeDotUtils.convertListToTreeDot(voListVO);
            List<DictionaryDataListVO> list = JsonUtil.getJsonToList(sumTrees, DictionaryDataListVO.class);
            ListVO<DictionaryDataListVO> treeVo = new ListVO<>();
            treeVo.setList(list);
            return ActionResult.success(treeVo);
        }
        List<DictionaryDataModel> voListVO = JsonUtil.getJsonToList(data, DictionaryDataModel.class);
        ListVO<DictionaryDataModel> treeVo = new ListVO<>();
        treeVo.setList(voListVO);
        return ActionResult.success(treeVo);
    }


    /**
     * 获取数据字典列表
     *
     * @return
     */
    @ApiOperation("获取数据字典列表(分类+内容)")
    @GetMapping("/All")
    public ActionResult allBindDictionary() {
        List<DictionaryTypeEntity> dictionaryTypeList = dictionaryTypeService.getList();
        List<DictionaryDataEntity> dictionaryDataList = dictionaryDataService.getList().stream().filter(t -> "1".equals(String.valueOf(t.getEnabledMark()))).collect(Collectors.toList());
        List<Map<String, Object>> list = new ArrayList<>();
        for (DictionaryTypeEntity dictionaryTypeEntity : dictionaryTypeList) {
            List<DictionaryDataEntity> childNodeList = dictionaryDataList.stream().filter(t -> dictionaryTypeEntity.getId().equals(t.getDictionaryTypeId())).collect(Collectors.toList());
            if (dictionaryTypeEntity.getIsTree().compareTo(1) == 0) {
                List<Map<String, Object>> selectList = new ArrayList<>();
                for (DictionaryDataEntity item : childNodeList) {
                    Map<String, Object> ht = new HashMap<>();
                    ht.put("fullName", item.getFullName());
                    ht.put("id", item.getId());
                    ht.put("parentId", item.getParentId());
                    selectList.add(ht);
                }
                //==============转换树
                List<SumTree<DictionaryDataAllModel>> list1 = TreeDotUtils.convertListToTreeDot(JsonUtil.getJsonToList(selectList, DictionaryDataAllModel.class));
                List<DictionaryDataAllVO> list2 = JsonUtil.getJsonToList(list1, DictionaryDataAllVO.class);
                //==============
                Map<String, Object> ht_item = new HashMap<>();
                ht_item.put("id", dictionaryTypeEntity.getId());
                ht_item.put("enCode", dictionaryTypeEntity.getEnCode());
                ht_item.put("dictionaryList", list2);
                ht_item.put("isTree", 1);
                list.add(ht_item);
            } else {
                List<Map<String, Object>> selectList = new ArrayList<>();
                for (DictionaryDataEntity item : childNodeList) {
                    Map<String, Object> ht = new HashMap<>();
                    ht.put("enCode", item.getEnCode());
                    ht.put("id", item.getId());
                    ht.put("fullName", item.getFullName());
                    selectList.add(ht);
                }
                Map<String, Object> ht_item = new HashMap<>();
                ht_item.put("id", dictionaryTypeEntity.getId());
                ht_item.put("enCode", dictionaryTypeEntity.getEnCode());
                ht_item.put("dictionaryList", selectList);
                ht_item.put("isTree", 0);
                list.add(ht_item);
            }
        }
        ListVO<Map<String, Object>> vo = new ListVO<>();
        vo.setList(list);
        return ActionResult.success(vo);
    }


    /**
     * 获取数据字典下拉框数据
     *
     * @param dictionaryTypeId 类别主键
     * @return
     */
    @ApiOperation("获取数据字典分类下拉框数据")
    @GetMapping("{dictionaryTypeId}/Selector")
    public ActionResult treeView(@PathVariable("dictionaryTypeId") String dictionaryTypeId, String isTree) {

        DictionaryTypeEntity typeEntity = dictionaryTypeService.getInfo(dictionaryTypeId);
        List<DictionaryDataModel> treeList = new ArrayList<>();
        DictionaryDataModel treeViewModel = new DictionaryDataModel();
        treeViewModel.setId("0");
        treeViewModel.setFullName(typeEntity.getFullName());
        treeViewModel.setParentId("-1");
        treeViewModel.setIcon("fa fa-tags");
        treeList.add(treeViewModel);
        if (isTree != null && StringNumber.ONE.equals(isTree)) {
            List<DictionaryDataEntity> data = dictionaryDataService.getList(dictionaryTypeId);
            for (DictionaryDataEntity entity : data) {
                DictionaryDataModel treeModel = new DictionaryDataModel();
                treeModel.setId(entity.getId());
                treeModel.setFullName(entity.getFullName());
                treeModel.setParentId("-1".equals(entity.getParentId()) ? entity.getDictionaryTypeId() : entity.getParentId());
                treeList.add(treeModel);
            }
        }
        List<SumTree<DictionaryDataModel>> sumTrees = TreeDotUtils.convertListToTreeDot(treeList);
        List<DictionaryDataSelectVO> list = JsonUtil.getJsonToList(sumTrees, DictionaryDataSelectVO.class);
        ListVO<DictionaryDataSelectVO> treeVo = new ListVO<>();
        treeVo.setList(list);
        return ActionResult.success(treeVo);
    }

    /**
     * 获取字典分类
     *
     * @return
     */
    @ApiOperation("获取某个字典数据下拉框列表")
    @GetMapping("/{dictionaryTypeId}/Data/Selector")
    public ActionResult selectorOneTreeView(@PathVariable("dictionaryTypeId") String dictionaryTypeId) {
        List<DictionaryDataEntity> data = dictionaryDataService.getList().stream().filter(t -> dictionaryTypeId.equals(t.getDictionaryTypeId())).collect(Collectors.toList());
        List<DictionaryTypeSelectModel> voListVO = JsonUtil.getJsonToList(data, DictionaryTypeSelectModel.class);
        List<SumTree<DictionaryTypeSelectModel>> sumTrees = TreeDotUtils.convertListToTreeDot(voListVO);
        List<DictionaryTypeSelectVO> list = JsonUtil.getJsonToList(sumTrees, DictionaryTypeSelectVO.class);
        ListVO<DictionaryTypeSelectVO> vo = new ListVO<>();
        vo.setList(list);
        return ActionResult.success(vo);
    }


    /**
     * 获取数据字典信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取数据字典信息")
    @GetMapping("/{id}/Info")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        DictionaryDataEntity entity = dictionaryDataService.getInfo(id);
        DictionaryDataInfoVO vo = JsonUtil.getJsonToBeanEx(entity, DictionaryDataInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 重复验证（名称）
     *
     * @param dictionaryTypeId 类别主键
     * @param fullName         名称
     * @param id               主键值
     * @return
     */
    @ApiOperation("（待定）重复验证（名称）")
    @GetMapping("/IsExistByFullName")
    public ActionResult isExistByFullName(String dictionaryTypeId, String fullName, String id) {
        boolean data = dictionaryDataService.isExistByFullName(dictionaryTypeId, fullName, id);
        return ActionResult.success(data);
    }

    /**
     * 重复验证（编码）
     *
     * @param dictionaryTypeId 类别主键
     * @param enCode           编码
     * @param id               主键值
     * @return
     */
    @ApiOperation("（待定）重复验证（编码）")
    @GetMapping("/IsExistByEnCode")
    public ActionResult isExistByEnCode(String dictionaryTypeId, String enCode, String id) {
        boolean data = dictionaryDataService.isExistByEnCode(dictionaryTypeId, enCode, id);
        return ActionResult.success(data);
    }


    /**
     * 添加数据字典
     *
     * @param dictionaryDataCrForm 实体对象
     * @return
     */
    @ApiOperation("添加数据字典")
    @PostMapping
    public ActionResult create(@RequestBody @Valid DictionaryDataCrForm dictionaryDataCrForm) {
        DictionaryDataEntity entity = JsonUtil.getJsonToBean(dictionaryDataCrForm, DictionaryDataEntity.class);
        if (dictionaryDataService.isExistByFullName(entity.getDictionaryTypeId(), entity.getFullName(), entity.getId())) {
            return ActionResult.fail("字典名称不能重复");
        }
        if (dictionaryDataService.isExistByEnCode(entity.getDictionaryTypeId(), entity.getEnCode(), entity.getId())) {
            return ActionResult.fail("字典编码不能重复");
        }
        dictionaryDataService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 修改数据字典
     *
     * @param dictionaryDataUpForm 实体对象
     * @param id                   主键值
     * @return
     */
    @ApiOperation("修改数据字典")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid DictionaryDataUpForm dictionaryDataUpForm) {
        DictionaryDataEntity entity = JsonUtil.getJsonToBean(dictionaryDataUpForm, DictionaryDataEntity.class);
        if (dictionaryDataService.isExistByFullName(entity.getDictionaryTypeId(), entity.getFullName(), id)) {
            return ActionResult.fail("字典名称不能重复");
        }
        if (dictionaryDataService.isExistByEnCode(entity.getDictionaryTypeId(), entity.getEnCode(), id)) {
            return ActionResult.fail("字典编码不能重复");
        }
        entity.setEnCode(null);
        boolean flag = dictionaryDataService.update(id, entity);
        if (flag == false) {
            return ActionResult.success("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");

    }

    /**
     * 删除数据字典
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除数据字典")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        DictionaryDataEntity entity = dictionaryDataService.getInfo(id);
        if (entity != null) {
            dictionaryDataService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /**
     * 更新字典状态
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新字典状态")
    @PutMapping("/{id}/Actions/State")
    public ActionResult update(@PathVariable("id") String id) {
        DictionaryDataEntity entity = dictionaryDataService.getInfo(id);
        if (entity != null) {
            if (entity.getEnabledMark() == 1) {
                entity.setEnabledMark(0);
            } else {
                entity.setEnabledMark(1);
            }
            boolean flag = dictionaryDataService.update(entity.getId(), entity);
            if (flag == false) {
                return ActionResult.success("更新失败，数据不存在");
            }
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 获取字典数据信息列表
     */
    @GetMapping("/getList/{dictionary}")
    public ActionResult getList(@PathVariable("dictionary") String dictionary){
        List<DictionaryDataEntity> list = dictionaryDataService.getList(dictionary);
        return ActionResult.success(list);
    }

    /**
     * 获取所有字典数据
     */
    @GetMapping("/getListAll")
    public ActionResult getListAll(){
        List<DictionaryDataEntity> list = dictionaryDataService.getList();
        return ActionResult.success(list);
    }

    /**
     * 获取数据字典信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取数据字典信息")
    @GetMapping("/{id}/info")
    public ActionResult getInfo(@PathVariable("id") String id) throws DataException {
        DictionaryDataEntity entity = dictionaryDataService.getInfo(id);
        return ActionResult.success(entity);
    }

}
