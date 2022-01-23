package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.WorkContactSheetEntity;
import smart.form.model.workcontactsheet.WorkContactSheetForm;
import smart.form.model.workcontactsheet.WorkContactSheetInfoVO;
import smart.form.service.WorkContactSheetService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 工作联系单
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Api(tags = "工作联系单", value = "WorkContactSheet")
@RestController
@RequestMapping("/Form/WorkContactSheet")
public class WorkContactSheetController {

    @Autowired
    private WorkContactSheetService workContactSheetService;

    /**
     * 获取工作联系单信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取工作联系单信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        WorkContactSheetEntity entity = workContactSheetService.getInfo(id);
        WorkContactSheetInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, WorkContactSheetInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建工作联系单
     *
     * @param workContactSheetForm 表单对象
     * @return
     */
    @ApiOperation("新建工作联系单")
    @PostMapping
    public ActionResult create(@RequestBody WorkContactSheetForm workContactSheetForm) throws WorkFlowException {
        WorkContactSheetEntity entity = JsonUtil.getJsonToBean(workContactSheetForm, WorkContactSheetEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(workContactSheetForm.getStatus())) {
            workContactSheetService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        workContactSheetService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改工作联系单
     *
     * @param workContactSheetForm 表单对象
     * @param id                   主键
     * @return
     */
    @ApiOperation("修改工作联系单")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody WorkContactSheetForm workContactSheetForm, @PathVariable("id") String id) throws WorkFlowException {
        WorkContactSheetEntity entity = JsonUtil.getJsonToBean(workContactSheetForm, WorkContactSheetEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(workContactSheetForm.getStatus())) {
            workContactSheetService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        workContactSheetService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
