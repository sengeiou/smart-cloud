package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.VehicleApplyEntity;
import smart.form.model.vehicleapply.VehicleApplyForm;
import smart.form.model.vehicleapply.VehicleApplyInfoVO;
import smart.form.service.VehicleApplyService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 车辆申请
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Api(tags = "车辆申请", value = "VehicleApply")
@RestController
@RequestMapping("/Form/VehicleApply")
public class VehicleApplyController {

    @Autowired
    private VehicleApplyService vehicleApplyService;

    /**
     * 获取车辆申请信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取车辆申请信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        VehicleApplyEntity entity = vehicleApplyService.getInfo(id);
        VehicleApplyInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, VehicleApplyInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建车辆申请
     *
     * @param vehicleApplyForm 表单对象
     * @return
     */
    @ApiOperation("新建车辆申请")
    @PostMapping
    public ActionResult create(@RequestBody VehicleApplyForm vehicleApplyForm) throws WorkFlowException {
        VehicleApplyEntity entity = JsonUtil.getJsonToBean(vehicleApplyForm, VehicleApplyEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(vehicleApplyForm.getStatus())) {
            vehicleApplyService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        vehicleApplyService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 提交车辆申请
     *
     * @param vehicleApplyForm 表单对象
     * @param id               主键
     * @return
     */
    @ApiOperation("修改车辆申请")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody VehicleApplyForm vehicleApplyForm, @PathVariable("id") String id) throws WorkFlowException {
        VehicleApplyEntity entity = JsonUtil.getJsonToBean(vehicleApplyForm, VehicleApplyEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(vehicleApplyForm.getStatus())) {
            vehicleApplyService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        vehicleApplyService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
