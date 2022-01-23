package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.ContractApprovalEntity;
import smart.form.model.contractapproval.ContractApprovalForm;
import smart.form.model.contractapproval.ContractApprovalInfoVO;
import smart.form.service.ContractApprovalService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 合同审批
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "合同审批", value = "ContractApproval")
@RestController
@RequestMapping("/Form/ContractApproval")
public class ContractApprovalController {

    @Autowired
    private ContractApprovalService contractApprovalService;

    /**
     * 获取合同审批信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取合同审批信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        ContractApprovalEntity entity = contractApprovalService.getInfo(id);
        ContractApprovalInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, ContractApprovalInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建合同审批
     *
     * @param contractApprovalForm 表单对象
     * @return
     */
    @ApiOperation("新建合同审批")
    @PostMapping
    public ActionResult create(@RequestBody @Valid ContractApprovalForm contractApprovalForm) throws WorkFlowException {
        ContractApprovalEntity entity = JsonUtil.getJsonToBean(contractApprovalForm, ContractApprovalEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(contractApprovalForm.getStatus())) {
            contractApprovalService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        contractApprovalService.submit(entity.getId(), entity, contractApprovalForm.getFreeApproverUserId());
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改合同审批
     *
     * @param contractApprovalForm 表单对象
     * @param id                   主键
     * @return
     */
    @ApiOperation("修改合同审批")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody @Valid ContractApprovalForm contractApprovalForm, @PathVariable("id") String id) throws WorkFlowException {
        ContractApprovalEntity entity = JsonUtil.getJsonToBean(contractApprovalForm, ContractApprovalEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(contractApprovalForm.getStatus())) {
            contractApprovalService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        contractApprovalService.submit(id, entity, contractApprovalForm.getFreeApproverUserId());
        return ActionResult.success("提交成功，请耐心等待");
    }
}
