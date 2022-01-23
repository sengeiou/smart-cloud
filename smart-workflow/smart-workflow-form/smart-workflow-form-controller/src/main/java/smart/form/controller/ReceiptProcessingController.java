package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.ReceiptProcessingEntity;
import smart.form.model.receiptprocessing.ReceiptProcessingForm;
import smart.form.model.receiptprocessing.ReceiptProcessingInfoVO;
import smart.form.service.ReceiptProcessingService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 收文处理表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Api(tags = "收文处理表", value = "ReceiptProcessing")
@RestController
@RequestMapping("/Form/ReceiptProcessing")
public class ReceiptProcessingController {

    @Autowired
    private ReceiptProcessingService receiptProcessingService;

    /**
     * 获取收文处理表信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取收文处理表信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        ReceiptProcessingEntity entity = receiptProcessingService.getInfo(id);
        ReceiptProcessingInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, ReceiptProcessingInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建收文处理表
     *
     * @param receiptProcessingForm 表单对象
     * @return
     */
    @ApiOperation("新建收文处理表")
    @PostMapping
    public ActionResult create(@RequestBody ReceiptProcessingForm receiptProcessingForm) throws WorkFlowException {
        ReceiptProcessingEntity entity = JsonUtil.getJsonToBean(receiptProcessingForm, ReceiptProcessingEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(receiptProcessingForm.getStatus())) {
            receiptProcessingService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        receiptProcessingService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改收文处理表
     *
     * @param receiptProcessingForm 表单对象
     * @param id                    主键
     * @return
     */
    @ApiOperation("修改收文处理表")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody ReceiptProcessingForm receiptProcessingForm, @PathVariable("id") String id) throws WorkFlowException {
        ReceiptProcessingEntity entity = JsonUtil.getJsonToBean(receiptProcessingForm, ReceiptProcessingEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(receiptProcessingForm.getStatus())) {
            receiptProcessingService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        receiptProcessingService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
