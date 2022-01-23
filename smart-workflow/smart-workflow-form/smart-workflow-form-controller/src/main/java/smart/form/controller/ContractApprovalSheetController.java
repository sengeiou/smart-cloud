package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.util.RegexUtils;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.ContractApprovalSheetEntity;
import smart.form.model.contractapprovalsheet.ContractApprovalSheetForm;
import smart.form.model.contractapprovalsheet.ContractApprovalSheetInfoVO;
import smart.form.service.ContractApprovalSheetService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 合同申请单表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "合同申请单表", value = "ContractApprovalSheet")
@RestController
@RequestMapping("/Form/ContractApprovalSheet")
public class ContractApprovalSheetController {

    @Autowired
    private ContractApprovalSheetService contractApprovalSheetService;

    /**
     * 获取合同申请单表信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取合同申请单表信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        ContractApprovalSheetEntity entity = contractApprovalSheetService.getInfo(id);
        ContractApprovalSheetInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, ContractApprovalSheetInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建合同申请单表
     *
     * @param contractApprovalSheetForm 表单对象
     * @return
     */
    @ApiOperation("新建合同申请单表")
    @PostMapping
    public ActionResult create(@RequestBody @Valid ContractApprovalSheetForm contractApprovalSheetForm) throws WorkFlowException {
        if (contractApprovalSheetForm.getStartContractDate() > contractApprovalSheetForm.getEndContractDate()) {
            return ActionResult.fail("结束时间不能小于开始时间");
        }
        if (contractApprovalSheetForm.getIncomeAmount() != null && !"".equals(String.valueOf(contractApprovalSheetForm.getIncomeAmount())) && !RegexUtils.checkDecimals2(String.valueOf(contractApprovalSheetForm.getIncomeAmount()))) {
            return ActionResult.fail("收入金额必须大于0，最多可以输入两位小数点");
        }
        if (contractApprovalSheetForm.getTotalExpenditure() != null && !"".equals(String.valueOf(contractApprovalSheetForm.getIncomeAmount())) && !RegexUtils.checkDecimals2(String.valueOf(contractApprovalSheetForm.getTotalExpenditure()))) {
            return ActionResult.fail("支出金额必须大于0，最多可以输入两位小数点");
        }
        ContractApprovalSheetEntity entity = JsonUtil.getJsonToBean(contractApprovalSheetForm, ContractApprovalSheetEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(contractApprovalSheetForm.getStatus())) {
            contractApprovalSheetService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        contractApprovalSheetService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改合同申请单表
     *
     * @param contractApprovalSheetForm 表单对象
     * @param id                        主键
     * @return
     */
    @ApiOperation("修改合同申请单表")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody @Valid ContractApprovalSheetForm contractApprovalSheetForm, @PathVariable("id") String id) throws WorkFlowException {
        if (contractApprovalSheetForm.getStartContractDate() > contractApprovalSheetForm.getEndContractDate()) {
            return ActionResult.fail("结束时间不能小于开始时间");
        }
        if (contractApprovalSheetForm.getIncomeAmount() != null && !"".equals(String.valueOf(contractApprovalSheetForm.getIncomeAmount())) && !RegexUtils.checkDecimals2(String.valueOf(contractApprovalSheetForm.getIncomeAmount()))) {
            return ActionResult.fail("收入金额必须大于0，最多可以输入两位小数点");
        }
        if (contractApprovalSheetForm.getTotalExpenditure() != null && !"".equals(String.valueOf(contractApprovalSheetForm.getTotalExpenditure())) && !RegexUtils.checkDecimals2(String.valueOf(contractApprovalSheetForm.getTotalExpenditure()))) {
            return ActionResult.fail("支出金额必须大于0，最多可以输入两位小数点");
        }
        ContractApprovalSheetEntity entity = JsonUtil.getJsonToBean(contractApprovalSheetForm, ContractApprovalSheetEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(contractApprovalSheetForm.getStatus())) {
            contractApprovalSheetService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        contractApprovalSheetService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
