package smart.base.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.vo.ListVO;
import smart.base.vo.PaginationVO;
import smart.util.JsonUtil;
import smart.base.*;
import smart.base.model.billrule.BillRuleCrForm;
import smart.base.model.billrule.BillRuleInfoVO;
import smart.base.model.billrule.BillRuleListVO;
import smart.base.model.billrule.BillRuleUpForm;
import smart.base.entity.BillRuleEntity;
import smart.exception.DataException;
import smart.base.service.BillRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 单据规则
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "单据规则", value = "BillRule")
@RestController
@RequestMapping("/Base/BillRule")
public class BillRuleController {

    @Autowired
    private BillRuleService billRuleService;

    /**
     * 列表
     *
     * @param pagination
     * @return
     */
    @ApiOperation("获取单据规则列表(带分页)")
    @GetMapping
    public ActionResult list(Pagination pagination) {
        List<BillRuleEntity> list = billRuleService.getList(pagination);
        List<BillRuleListVO> listVO = JsonUtil.getJsonToList(list, BillRuleListVO.class);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        return ActionResult.page(listVO, paginationVO);
    }

    /**
     * 列表
     *
     * @param
     * @return
     */
    @ApiOperation("获取单据规则下拉框")
    @GetMapping("/Selector")
    public ActionResult selectList() {
        List<BillRuleEntity> list = billRuleService.getList().stream().filter(t->"1".equals(String.valueOf(t.getEnabledMark()))).collect(Collectors.toList());
        List<BillRuleListVO> vo= JsonUtil.getJsonToList(list, BillRuleListVO.class);
        ListVO listVO=new ListVO();
        listVO.setList(vo);
        return ActionResult.success(listVO);
    }


    /**
     * 更新组织状态
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新单据规则状态")
    @PutMapping("/{id}/Actions/State")
    public ActionResult update(@PathVariable("id") String id) {
        BillRuleEntity entity = billRuleService.getInfo(id);
        if (entity != null) {
            if (entity.getEnabledMark() == 1) {
                entity.setEnabledMark(0);
            } else {
                entity.setEnabledMark(1);
            }
            billRuleService.update(entity.getId(), entity);
            return ActionResult.success("更新成功");
        }
        return ActionResult.fail("更新失败，数据不存在");
    }

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取单据规则信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        BillRuleEntity entity = billRuleService.getInfo(id);
        BillRuleInfoVO vo = JsonUtil.getJsonToBeanEx(entity, BillRuleInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 获取单据流水号
     *
     * @param enCode 参数编码
     * @return
     */
    @ApiOperation("获取单据流水号(工作流调用)")
    @GetMapping("/BillNumber/{enCode}")
    public ActionResult GetBillNumber(@PathVariable("enCode") String enCode) throws DataException {
        String data = billRuleService.getBillNumber(enCode, true);
        return ActionResult.success("获取成功", data);
    }

    /**
     * 新建
     *
     * @param billRuleCrForm 实体对象
     * @return
     */
    @ApiOperation("添加单据规则")
    @PostMapping
    public ActionResult create(@RequestBody @Valid BillRuleCrForm billRuleCrForm) {
        BillRuleEntity entity = JsonUtil.getJsonToBean(billRuleCrForm, BillRuleEntity.class);
        if (billRuleService.isExistByFullName(entity.getFullName(), entity.getId())) {
            return ActionResult.fail("名称不能重复");
        }
        if (billRuleService.isExistByEnCode(entity.getEnCode(), entity.getId())) {
            return ActionResult.fail("编码不能重复");
        }
        billRuleService.create(entity);
        return ActionResult.success("新建成功");
    }

    /**
     * 更新
     *
     * @param billRuleUpForm 实体对象
     * @param id             主键值
     * @return
     */
    @ApiOperation("修改单据规则")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody BillRuleUpForm billRuleUpForm) {
        BillRuleEntity entity = JsonUtil.getJsonToBean(billRuleUpForm, BillRuleEntity.class);
        if (billRuleService.isExistByFullName(entity.getFullName(), id)) {
            return ActionResult.fail("名称不能重复");
        }
        if (billRuleService.isExistByEnCode(entity.getFullName(), id)) {
            return ActionResult.fail("编码不能重复");
        }
        boolean flag = billRuleService.update(id, entity);
        if (flag == false) {
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
    @ApiOperation("删除单据规则")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        BillRuleEntity entity = billRuleService.getInfo(id);
        if (entity != null) {
            if (!StringUtils.isEmpty(entity.getOutputNumber())) {
                return ActionResult.fail("单据已经被使用,不允许被删除");
            } else {
                billRuleService.delete(entity);
                return ActionResult.success("删除成功");
            }
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /**
     * 获取单据缓存
     *
     * @param enCode 参数编码
     * @return
     */
    @ApiOperation("获取单据缓存")
    @GetMapping("/useBillNumber/{enCode}")
    public ActionResult useBillNumber(@PathVariable("enCode") String enCode) {
        billRuleService.useBillNumber(enCode);
        return ActionResult.success();
    }

    /**
     * 获取单据流水号
     *
     * @param enCode 参数编码
     * @return
     */
    @ApiOperation("获取单据流水号(工作流调用)")
    @GetMapping("/getBillNumber/{enCode}")
    public ActionResult getBillNumber(@PathVariable("enCode") String enCode) throws DataException {
        Object data = billRuleService.getBillNumber(enCode, false);
        return ActionResult.success(data);
    }

}
