package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.OfficeSuppliesEntity;
import smart.form.model.officesupplies.OfficeSuppliesForm;
import smart.form.model.officesupplies.OfficeSuppliesInfoVO;
import smart.form.service.OfficeSuppliesService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 领用办公用品申请表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "领用办公用品申请表", value = "OfficeSupplies")
@RestController
@RequestMapping("/Form/OfficeSupplies")
public class OfficeSuppliesController {

    @Autowired
    private OfficeSuppliesService officeSuppliesService;

    /**
     * 获取领用办公用品申请表信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取领用办公用品申请表信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        OfficeSuppliesEntity entity = officeSuppliesService.getInfo(id);
        OfficeSuppliesInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, OfficeSuppliesInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建领用办公用品申请表
     *
     * @param officeSuppliesForm 表单对象
     * @return
     */
    @ApiOperation("新建领用办公用品申请表")
    @PostMapping
    public ActionResult create(@RequestBody OfficeSuppliesForm officeSuppliesForm) throws WorkFlowException {
        OfficeSuppliesEntity entity = JsonUtil.getJsonToBean(officeSuppliesForm, OfficeSuppliesEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(officeSuppliesForm.getStatus())) {
            officeSuppliesService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        officeSuppliesService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改领用办公用品申请表
     *
     * @param officeSuppliesForm 表单对象
     * @param id                 主键
     * @return
     */
    @ApiOperation("修改领用办公用品申请表")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody OfficeSuppliesForm officeSuppliesForm, @PathVariable("id") String id) throws WorkFlowException {
        OfficeSuppliesEntity entity = JsonUtil.getJsonToBean(officeSuppliesForm, OfficeSuppliesEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(officeSuppliesForm.getStatus())) {
            officeSuppliesService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        officeSuppliesService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
