package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.IncomeRecognitionEntity;
import smart.form.model.incomerecognition.IncomeRecognitionForm;
import smart.form.model.incomerecognition.IncomeRecognitionInfoVO;
import smart.form.service.IncomeRecognitionService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 收入确认分析表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "收入确认分析表", value = "IncomeRecognition")
@RestController
@RequestMapping("/Form/IncomeRecognition")
public class IncomeRecognitionController {

    @Autowired
    private IncomeRecognitionService incomeRecognitionService;

    /**
     * 获取收入确认分析表信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取收入确认分析表信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        IncomeRecognitionEntity entity = incomeRecognitionService.getInfo(id);
        IncomeRecognitionInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, IncomeRecognitionInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建收入确认分析表
     *
     * @param incomeRecognitionForm 表单对象
     * @return
     */
    @ApiOperation("新建收入确认分析表")
    @PostMapping
    public ActionResult create(@RequestBody @Valid IncomeRecognitionForm incomeRecognitionForm) throws WorkFlowException {
        IncomeRecognitionEntity entity = JsonUtil.getJsonToBean(incomeRecognitionForm, IncomeRecognitionEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(incomeRecognitionForm.getStatus())) {
            incomeRecognitionService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        incomeRecognitionService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改收入确认分析表
     *
     * @param incomeRecognitionForm 表单对象
     * @param id                    主键
     * @return
     */
    @ApiOperation("修改收入确认分析表")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody @Valid IncomeRecognitionForm incomeRecognitionForm, @PathVariable("id") String id) throws WorkFlowException {
        IncomeRecognitionEntity entity = JsonUtil.getJsonToBean(incomeRecognitionForm, IncomeRecognitionEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(incomeRecognitionForm.getStatus())) {
            incomeRecognitionService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        incomeRecognitionService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
