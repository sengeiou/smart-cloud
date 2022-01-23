package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.util.RegexUtils;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.ConBillingEntity;
import smart.form.model.conbilling.ConBillingForm;
import smart.form.model.conbilling.ConBillingInfoVO;
import smart.form.service.ConBillingService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 合同开票流程
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "合同开票流程", value = "ConBilling")
@RestController
@RequestMapping("/Form/ConBilling")
public class ConBillingController {

    @Autowired
    private ConBillingService conBillingService;

    /**
     * 获取合同开票流程信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取合同开票流程信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        ConBillingEntity entity = conBillingService.getInfo(id);
        ConBillingInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, ConBillingInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建合同开票流程
     *
     * @param conBillingForm 表单对象
     * @return
     */
    @ApiOperation("新建合同开票流程")
    @PostMapping
    public ActionResult create(@RequestBody @Valid ConBillingForm conBillingForm) throws WorkFlowException {
        if (conBillingForm.getBillAmount() != null && !"".equals(String.valueOf(conBillingForm.getBillAmount())) && !RegexUtils.checkDecimals2(String.valueOf(conBillingForm.getBillAmount()))) {
            return ActionResult.fail("开票金额必须大于0，最多可以精确到小数点后两位");
        }
        if (conBillingForm.getPayAmount() != null && !"".equals(String.valueOf(conBillingForm.getPayAmount())) && !RegexUtils.checkDecimals2(String.valueOf(conBillingForm.getPayAmount()))) {
            return ActionResult.fail("付款金额必须大于0，最多可以精确到小数点后两位");
        }
        ConBillingEntity entity = JsonUtil.getJsonToBean(conBillingForm, ConBillingEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(conBillingForm.getStatus())) {
            conBillingService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        conBillingService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改合同开票流程
     *
     * @param conBillingForm 表单对象
     * @param id             主键
     * @return
     */
    @ApiOperation("修改合同开票流程")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody @Valid ConBillingForm conBillingForm, @PathVariable("id") String id) throws WorkFlowException {
        if (conBillingForm.getBillAmount() != null &&  !"".equals(conBillingForm.getBillAmount()) && !RegexUtils.checkDecimals2(String.valueOf(conBillingForm.getBillAmount()))) {
            return ActionResult.fail("开票金额必须大于0，最多可以精确到小数点后两位");
        }
        if (conBillingForm.getPayAmount() != null && !"".equals(conBillingForm.getPayAmount()) && !RegexUtils.checkDecimals2(String.valueOf(conBillingForm.getPayAmount()))) {
            return ActionResult.fail("付款金额必须大于0，最多可以精确到小数点后两位");
        }
        ConBillingEntity entity = JsonUtil.getJsonToBean(conBillingForm, ConBillingEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(conBillingForm.getStatus())) {
            conBillingService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        conBillingService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
