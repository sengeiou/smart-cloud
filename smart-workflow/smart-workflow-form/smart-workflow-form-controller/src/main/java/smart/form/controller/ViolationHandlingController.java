package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.ViolationHandlingEntity;
import smart.form.model.violationhandling.ViolationHandlingForm;
import smart.form.model.violationhandling.ViolationHandlingInfoVO;
import smart.form.service.ViolationHandlingService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 违章处理申请表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Api(tags = "违章处理申请表", value = "ViolationHandling")
@RestController
@RequestMapping("/Form/ViolationHandling")
public class ViolationHandlingController {

    @Autowired
    private ViolationHandlingService violationHandlingService;

    /**
     * 获取违章处理申请表信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取违章处理申请表信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        ViolationHandlingEntity entity = violationHandlingService.getInfo(id);
        ViolationHandlingInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, ViolationHandlingInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建违章处理申请表
     *
     * @param violationHandlingForm 表单对象
     * @return
     */
    @ApiOperation("新建违章处理申请表")
    @PostMapping
    public ActionResult create(@RequestBody ViolationHandlingForm violationHandlingForm) throws WorkFlowException {
        ViolationHandlingEntity entity = JsonUtil.getJsonToBean(violationHandlingForm, ViolationHandlingEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(violationHandlingForm.getStatus())) {
            violationHandlingService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        violationHandlingService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改违章处理申请表
     *
     * @param violationHandlingForm 表单对象
     * @param id                    主键
     * @return
     */
    @ApiOperation("修改违章处理申请表")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody ViolationHandlingForm violationHandlingForm, @PathVariable("id") String id) throws WorkFlowException {
        ViolationHandlingEntity entity = JsonUtil.getJsonToBean(violationHandlingForm, ViolationHandlingEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(violationHandlingForm.getStatus())) {
            violationHandlingService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        violationHandlingService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
