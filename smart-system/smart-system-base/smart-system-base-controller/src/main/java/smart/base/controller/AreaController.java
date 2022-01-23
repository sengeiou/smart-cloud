package smart.base.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.util.StringUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.base.Page;
import smart.base.model.province.*;
import smart.base.entity.ProvinceEntity;
import smart.exception.DataException;
import smart.base.service.ProvinceService;
import smart.util.treeutil.ListToTreeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 行政区划
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "行政区划", value = "Area")
@RestController
@RequestMapping("/Base/Area")
public class AreaController {

    @Autowired
    private ProvinceService provinceService;

    /**
     * 列表（异步加载）
     *
     * @return
     */
    @ApiOperation("列表（异步加载）")
    @GetMapping("/{nodeId}")
    public ActionResult list(@PathVariable("nodeId") String nodeId, Page page) {
        List<ProvinceEntity> data = provinceService.getList(nodeId);
        List<ProvinceEntity> dataAll = data;
        if(StringUtil.isNotEmpty(page.getKeyword())){
            data = data.stream().filter(t->t.getFullName().contains(page.getKeyword()) || t.getEnCode().contains(page.getKeyword())).collect(Collectors.toList());
        }
        List<ProvinceEntity> result = JsonUtil.getJsonToList(ListToTreeUtil.treeWhere(data, dataAll), ProvinceEntity.class);
        List<ProvinceListVO> treeList = JsonUtil.getJsonToList(result, ProvinceListVO.class);
        int i = 0;
        for (ProvinceListVO entity : treeList) {
            boolean childNode = provinceService.getList(entity.getId()).size() <= 0;
            ProvinceListVO provinceListVO = JsonUtil.getJsonToBean(entity, ProvinceListVO.class);
            provinceListVO.setIsLeaf(childNode);
            provinceListVO.setHasChildren(childNode == true ? false : true);
            treeList.set(i, provinceListVO);
            i++;
        }
        ListVO<ProvinceListVO> vo = new ListVO<>();
        vo.setList(treeList);
        return ActionResult.success(vo);
    }

    /**
     * 获取行政区划下拉框数据
     *
     * @return
     */
    @ApiOperation("获取行政区划下拉框数据")
    @GetMapping("/{id}/Selector")
    public ActionResult selectList(@PathVariable("id") String id) {
        List<ProvinceEntity> data = provinceService.getList(id);
        List<ProvinceSelectListVO> treeList = JsonUtil.getJsonToList(data, ProvinceSelectListVO.class);
        int i = 0;
        for (ProvinceSelectListVO entity : treeList) {
            boolean childNode = provinceService.getList(entity.getId()).size() <= 0;
            ProvinceSelectListVO provinceListVO = JsonUtil.getJsonToBean(entity, ProvinceSelectListVO.class);
            provinceListVO.setIsLeaf(childNode);
            treeList.set(i, provinceListVO);
            i++;
        }
        ListVO<ProvinceSelectListVO> vo = new ListVO<>();
        vo.setList(treeList);
        return ActionResult.success(vo);
    }

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取行政区划信息")
    @GetMapping("/{id}/Info")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        ProvinceEntity entity = provinceService.getInfo(id);
        ProvinceInfoVO vo = JsonUtil.getJsonToBeanEx(entity, ProvinceInfoVO.class);
        if(!"-1".equals(entity.getParentId())){
            ProvinceEntity parent = provinceService.getInfo(entity.getParentId());
            vo.setParentName(parent.getFullName());
        }
        return ActionResult.success(vo);
    }

    /**
     * 新建
     *
     * @param provinceCrForm 实体对象
     * @return
     */
    @ApiOperation("添加行政区划")
    @PostMapping
    public ActionResult create(@RequestBody @Valid ProvinceCrForm provinceCrForm) {
        ProvinceEntity entity = JsonUtil.getJsonToBean(provinceCrForm, ProvinceEntity.class);
        if (provinceService.isExistByFullName(provinceCrForm.getFullName(),entity.getId())){
            return ActionResult.fail("区域名称不能重复");
        }
        if (provinceService.isExistByEnCode(provinceCrForm.getEnCode(),entity.getId())){
            return ActionResult.fail("区域编码不能重复");
        }
        if(StringUtil.isEmpty(provinceCrForm.getParentId())){
            entity.setParentId("-1");
        }
        provinceService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 更新
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("修改行政区划")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid ProvinceUpForm provinceUpForm) {
        ProvinceEntity entity = JsonUtil.getJsonToBean(provinceUpForm, ProvinceEntity.class);
        if (provinceService.isExistByFullName(provinceUpForm.getFullName(),id)){
            return ActionResult.fail("区域名称不能重复");
        }
        if (provinceService.isExistByEnCode(provinceUpForm.getEnCode(),id)){
            return ActionResult.fail("区域编码不能重复");
        }
        boolean flag = provinceService.update(id, entity);
        if (flag==false){
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        if (provinceService.getList(id).size() == 0) {
            ProvinceEntity entity = provinceService.getInfo(id);
            if (entity != null) {
                provinceService.delete(entity);
                return ActionResult.success("删除成功");
            }
            return ActionResult.fail("删除失败，数据不存在");
        } else {
            return ActionResult.fail("删除失败，当前有子节点数据");
        }
    }

    /**
     * 更新行政区划状态
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新行政区划状态")
    @PutMapping("/{id}/Actions/State")
    public ActionResult upState(@PathVariable("id") String id) {
        ProvinceEntity entity = provinceService.getInfo(id);
        if (entity.getEnabledMark() == null || entity.getEnabledMark() == 1) {
            entity.setEnabledMark(0);
        } else {
            entity.setEnabledMark(1);
        }
        boolean flag = provinceService.update(id, entity);
        if (flag==false){
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 获取行政区划列表
     * @param id
     * @return
     */
    @GetMapping("/getList/{id}")
    public ActionResult getList(@PathVariable("id") String id){
        List<ProvinceEntity> list = provinceService.getList(id);
        return ActionResult.success(list);
    }

}
