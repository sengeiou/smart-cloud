package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.PaymentApplyEntity;
import smart.form.model.paymentapply.PaymentApplyForm;
import smart.form.model.paymentapply.PaymentApplyInfoVO;
import smart.form.service.PaymentApplyService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 付款申请单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Api(tags = "付款申请单", value = "PaymentApply")
@RestController
@RequestMapping("/Form/PaymentApply")
public class PaymentApplyController {

    @Autowired
    private PaymentApplyService paymentApplyService;

    /**
     * 获取付款申请单信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取付款申请单信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        PaymentApplyEntity entity = paymentApplyService.getInfo(id);
        PaymentApplyInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, PaymentApplyInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建付款申请单
     *
     * @param paymentApplyForm 表单对象
     * @return
     */
    @ApiOperation("新建付款申请单")
    @PostMapping
    public ActionResult create(@RequestBody PaymentApplyForm paymentApplyForm) throws WorkFlowException {
        PaymentApplyEntity entity = JsonUtil.getJsonToBean(paymentApplyForm, PaymentApplyEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(paymentApplyForm.getStatus())) {
            paymentApplyService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        paymentApplyService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改付款申请单
     *
     * @param paymentApplyForm 表单对象
     * @param id               主键
     * @return
     */
    @ApiOperation("修改付款申请单")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody PaymentApplyForm paymentApplyForm, @PathVariable("id") String id) throws WorkFlowException {
        PaymentApplyEntity entity = JsonUtil.getJsonToBean(paymentApplyForm, PaymentApplyEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(paymentApplyForm.getStatus())) {
            paymentApplyService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        paymentApplyService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
