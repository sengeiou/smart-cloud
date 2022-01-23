package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.QuotationApprovalEntity;
import smart.form.model.quotationapproval.QuotationApprovalForm;
import smart.form.model.quotationapproval.QuotationApprovalInfoVO;
import smart.form.service.QuotationApprovalService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 报价审批表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Api(tags = "报价审批表", value = "QuotationApproval")
@RestController
@RequestMapping("/Form/QuotationApproval")
public class QuotationApprovalController {


    @Autowired
    private QuotationApprovalService quotationApprovalService;

    /**
     * 获取报价审批表信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取报价审批表信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        QuotationApprovalEntity entity = quotationApprovalService.getInfo(id);
        QuotationApprovalInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, QuotationApprovalInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建报价审批表
     *
     * @param quotationApprovalForm 表单对象
     * @return
     */
    @ApiOperation("新建报价审批表")
    @PostMapping
    public ActionResult create(@RequestBody QuotationApprovalForm quotationApprovalForm) throws WorkFlowException {
        QuotationApprovalEntity entity = JsonUtil.getJsonToBean(quotationApprovalForm, QuotationApprovalEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(quotationApprovalForm.getStatus())) {
            quotationApprovalService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        quotationApprovalService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改报价审批表
     *
     * @param quotationApprovalForm 表单对象
     * @param id                    主键
     * @return
     */
    @ApiOperation("修改报价审批表")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody QuotationApprovalForm quotationApprovalForm, @PathVariable("id") String id) throws WorkFlowException {
        QuotationApprovalEntity entity = JsonUtil.getJsonToBean(quotationApprovalForm, QuotationApprovalEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(quotationApprovalForm.getStatus())) {
            quotationApprovalService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        quotationApprovalService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
