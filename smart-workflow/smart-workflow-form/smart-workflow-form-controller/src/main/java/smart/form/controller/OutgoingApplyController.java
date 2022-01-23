package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.OutgoingApplyEntity;
import smart.form.model.outgoingapply.OutgoingApplyForm;
import smart.form.model.outgoingapply.OutgoingApplyInfoVO;
import smart.form.service.OutgoingApplyService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 外出申请单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "外出申请单", value = "OutgoingApply")
@RestController
@RequestMapping("/Form/OutgoingApply")
public class OutgoingApplyController {

    @Autowired
    private OutgoingApplyService outgoingApplyService;

    /**
     * 获取外出申请单信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取外出申请单信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        OutgoingApplyEntity entity = outgoingApplyService.getInfo(id);
        OutgoingApplyInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, OutgoingApplyInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建外出申请单
     *
     * @param outgoingApplyForm 表单对象
     * @return
     */
    @ApiOperation("新建外出申请单")
    @PostMapping
    public ActionResult create(@RequestBody OutgoingApplyForm outgoingApplyForm) throws WorkFlowException {
        if (outgoingApplyForm.getStartTime() > outgoingApplyForm.getEndTime()) {
            return ActionResult.fail("结束时间不能小于起始时间");
        }
        OutgoingApplyEntity entity = JsonUtil.getJsonToBean(outgoingApplyForm, OutgoingApplyEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(outgoingApplyForm.getStatus())) {
            outgoingApplyService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        outgoingApplyService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改外出申请单
     *
     * @param outgoingApplyForm 表单对象
     * @param id                主键
     * @return
     */
    @ApiOperation("修改外出申请单")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody OutgoingApplyForm outgoingApplyForm, @PathVariable("id") String id) throws WorkFlowException {
        if (outgoingApplyForm.getStartTime() > outgoingApplyForm.getEndTime()) {
            return ActionResult.fail("结束时间不能小于起始时间");
        }
        OutgoingApplyEntity entity = JsonUtil.getJsonToBean(outgoingApplyForm, OutgoingApplyEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(outgoingApplyForm.getStatus())) {
            outgoingApplyService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        outgoingApplyService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
