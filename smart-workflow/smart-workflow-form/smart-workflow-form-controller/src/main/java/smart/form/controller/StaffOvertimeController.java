package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.StaffOvertimeEntity;
import smart.form.model.staffovertime.StaffOvertimeForm;
import smart.form.model.staffovertime.StaffOvertimeInfoVO;
import smart.form.service.StaffOvertimeService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 员工加班申请表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Api(tags = "员工加班申请表", value = "StaffOvertime")
@RestController
@RequestMapping("/Form/StaffOvertime")
public class StaffOvertimeController {

    @Autowired
    private StaffOvertimeService staffOvertimeService;

    /**
     * 获取员工加班申请表信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取员工加班申请表信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        StaffOvertimeEntity entity = staffOvertimeService.getInfo(id);
        StaffOvertimeInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, StaffOvertimeInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建员工加班申请表
     *
     * @param staffOvertimeForm 表单对象
     * @return
     */
    @ApiOperation("新建员工加班申请表")
    @PostMapping
    public ActionResult create(@RequestBody StaffOvertimeForm staffOvertimeForm) throws WorkFlowException {
        if (staffOvertimeForm.getStartTime() > staffOvertimeForm.getEndTime()) {
            return ActionResult.fail("结束时间不能小于起始时间");
        }
        StaffOvertimeEntity entity = JsonUtil.getJsonToBean(staffOvertimeForm, StaffOvertimeEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(staffOvertimeForm.getStatus())) {
            staffOvertimeService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        staffOvertimeService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改
     *
     * @param staffOvertimeForm 表单对象
     * @param id                主键
     * @return
     */
    @ApiOperation("修改员工加班申请表")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody StaffOvertimeForm staffOvertimeForm, @PathVariable("id") String id) throws WorkFlowException {
        if (staffOvertimeForm.getStartTime() > staffOvertimeForm.getEndTime()) {
            return ActionResult.fail("结束时间不能小于起始时间");
        }
        StaffOvertimeEntity entity = JsonUtil.getJsonToBean(staffOvertimeForm, StaffOvertimeEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(staffOvertimeForm.getStatus())) {
            staffOvertimeService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        staffOvertimeService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
