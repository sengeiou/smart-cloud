package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.DebitBillEntity;
import smart.form.model.debitbill.DebitBillForm;
import smart.form.model.debitbill.DebitBillInfoVO;
import smart.form.service.DebitBillService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import smart.util.RegexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 借支单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "借支单", value = "DebitBill")
@RestController
@RequestMapping("/Form/DebitBill")
public class DebitBillController {

    @Autowired
    private DebitBillService debitBillService;

    /**
     * 获取借支单信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取借支单信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        DebitBillEntity entity = debitBillService.getInfo(id);
        DebitBillInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, DebitBillInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建借支单
     *
     * @param debitBillForm 表单对象
     * @return
     */
    @ApiOperation("新建借支单")
    @PostMapping
    public ActionResult create(@RequestBody @Valid DebitBillForm debitBillForm) throws WorkFlowException {
        if (debitBillForm.getAmountDebit() != null && !"".equals(String.valueOf(debitBillForm.getAmountDebit())) && !RegexUtils.checkDecimals2(String.valueOf(debitBillForm.getAmountDebit()))) {
            return ActionResult.fail("借支金额必须大于0，最多可以输入两位小数点");
        }
        DebitBillEntity entity = JsonUtil.getJsonToBean(debitBillForm, DebitBillEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(debitBillForm.getStatus())) {
            debitBillService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        debitBillService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改借支单
     *
     * @param debitBillForm 表单对象
     * @param id            主键
     * @return
     */
    @ApiOperation("修改借支单")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody @Valid DebitBillForm debitBillForm, @PathVariable("id") String id) throws WorkFlowException {
        if (debitBillForm.getAmountDebit() != null && !"".equals(String.valueOf(debitBillForm.getAmountDebit())) && !RegexUtils.checkDecimals2(String.valueOf(debitBillForm.getAmountDebit()))) {
            return ActionResult.fail("借支金额必须大于0，最多可以输入两位小数点");
        }
        DebitBillEntity entity = JsonUtil.getJsonToBean(debitBillForm, DebitBillEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(debitBillForm.getStatus())) {
            debitBillService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        debitBillService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
