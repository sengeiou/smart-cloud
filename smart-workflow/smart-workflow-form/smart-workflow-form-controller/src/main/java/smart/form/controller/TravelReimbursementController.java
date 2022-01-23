package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.TravelReimbursementEntity;
import smart.form.model.travelreimbursement.TravelReimbursementForm;
import smart.form.model.travelreimbursement.TravelReimbursementInfoVO;
import smart.form.service.TravelReimbursementService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 差旅报销申请表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Api(tags = "差旅报销申请表", value = "TravelReimbursement")
@RestController
@RequestMapping("/Form/TravelReimbursement")
public class TravelReimbursementController {

    @Autowired
    private TravelReimbursementService travelReimbursementService;

    /**
     * 获取差旅报销申请表信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取差旅报销申请表信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        TravelReimbursementEntity entity = travelReimbursementService.getInfo(id);
        TravelReimbursementInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, TravelReimbursementInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建差旅报销申请表
     *
     * @param travelReimbursementForm 表单对象
     * @return
     */
    @ApiOperation("新建差旅报销申请表")
    @PostMapping
    public ActionResult create(@RequestBody TravelReimbursementForm travelReimbursementForm) throws WorkFlowException {
        if (travelReimbursementForm.getSetOutDate() > travelReimbursementForm.getReturnDate()) {
            return ActionResult.fail("结束时间不能小于起始时间");
        }
        TravelReimbursementEntity entity = JsonUtil.getJsonToBean(travelReimbursementForm, TravelReimbursementEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(travelReimbursementForm.getStatus())) {
            travelReimbursementService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        travelReimbursementService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改差旅报销申请表
     *
     * @param travelReimbursementForm 表单对象
     * @param id                      主键
     * @return
     */
    @ApiOperation("修改差旅报销申请表")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody TravelReimbursementForm travelReimbursementForm, @PathVariable("id") String id) throws WorkFlowException {
        if (travelReimbursementForm.getSetOutDate() > travelReimbursementForm.getReturnDate()) {
            return ActionResult.fail("结束时间不能小于起始时间");
        }
        TravelReimbursementEntity entity = JsonUtil.getJsonToBean(travelReimbursementForm, TravelReimbursementEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(travelReimbursementForm.getStatus())) {
            travelReimbursementService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        travelReimbursementService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
