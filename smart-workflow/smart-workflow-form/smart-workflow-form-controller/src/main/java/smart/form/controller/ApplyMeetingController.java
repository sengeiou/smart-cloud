package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.ApplyMeetingEntity;
import smart.form.model.applymeeting.ApplyMeetingForm;
import smart.form.model.applymeeting.ApplyMeetingInfoVO;
import smart.form.service.ApplyMeetingService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import smart.util.RegexUtils;
import smart.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 会议申请
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "会议申请", value = "ApplyMeeting")
@RestController
@RequestMapping("/Form/ApplyMeeting")
public class ApplyMeetingController {

    @Autowired
    private ApplyMeetingService applyMeetingService;

    /**
     * 获取会议申请信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取会议申请信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        ApplyMeetingEntity entity = applyMeetingService.getInfo(id);
        ApplyMeetingInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, ApplyMeetingInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建会议申请
     *
     * @param applyMeetingForm 表单对象
     * @return
     */
    @ApiOperation("新建会议申请")
    @PostMapping
    public ActionResult create(@RequestBody @Valid ApplyMeetingForm applyMeetingForm) throws WorkFlowException {
        if (applyMeetingForm.getStartDate() > applyMeetingForm.getEndDate()) {
            return ActionResult.fail("结束时间不能小于开始时间");
        }
        if (applyMeetingForm.getEstimatePeople() != null && StringUtil.isNotEmpty(applyMeetingForm.getEstimatePeople()) && !RegexUtils.checkDigit2(applyMeetingForm.getEstimatePeople())) {
            return ActionResult.fail("预计人数只能输入正整数");
        }
        if (applyMeetingForm.getEstimatedAmount() != null && !RegexUtils.checkDecimals2(String.valueOf(applyMeetingForm.getEstimatedAmount()))) {
            return ActionResult.fail("预计金额必须大于0，最多精确小数点后两位");
        }
        ApplyMeetingEntity entity = JsonUtil.getJsonToBean(applyMeetingForm, ApplyMeetingEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(applyMeetingForm.getStatus())) {
            applyMeetingService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        applyMeetingService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改会议申请
     *
     * @param applyMeetingForm 表单对象
     * @param id               主键
     * @return
     */
    @ApiOperation("修改会议申请")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody @Valid ApplyMeetingForm applyMeetingForm, @PathVariable("id") String id) throws WorkFlowException {
        if (applyMeetingForm.getStartDate() > applyMeetingForm.getEndDate()) {
            return ActionResult.fail("结束时间不能小于开始时间");
        }
        if (applyMeetingForm.getEstimatePeople() != null && StringUtil.isNotEmpty(applyMeetingForm.getEstimatePeople()) && !RegexUtils.checkDigit2(applyMeetingForm.getEstimatePeople())) {
            return ActionResult.fail("预计人数只能输入正整数");
        }
        if (applyMeetingForm.getEstimatedAmount() != null && !RegexUtils.checkDecimals2(String.valueOf(applyMeetingForm.getEstimatedAmount()))) {
            return ActionResult.fail("预计金额必须大于0，最多精确小数点后两位");
        }
        ApplyMeetingEntity entity = JsonUtil.getJsonToBean(applyMeetingForm, ApplyMeetingEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(applyMeetingForm.getStatus())) {
            applyMeetingService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        applyMeetingService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
