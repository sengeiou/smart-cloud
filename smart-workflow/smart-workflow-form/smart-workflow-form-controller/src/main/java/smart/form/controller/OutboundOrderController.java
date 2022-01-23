package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.OutboundEntryEntity;
import smart.form.entity.OutboundOrderEntity;
import smart.form.model.outboundorder.OutboundEntryEntityInfoModel;
import smart.form.model.outboundorder.OutboundOrderForm;
import smart.form.model.outboundorder.OutboundOrderInfoVO;
import smart.form.service.OutboundOrderService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 出库单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "出库单", value = "OutboundOrder")
@RestController
@RequestMapping("/Form/OutboundOrder")
public class OutboundOrderController {

    @Autowired
    private OutboundOrderService outboundOrderService;

    /**
     * 获取出库单信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取出库单信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        OutboundOrderEntity entity = outboundOrderService.getInfo(id);
        List<OutboundEntryEntity> entityList = outboundOrderService.getOutboundEntryList(id);
        OutboundOrderInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, OutboundOrderInfoVO.class);
        vo.setEntryList(JsonUtil.getJsonToList(entityList, OutboundEntryEntityInfoModel.class));
        return ActionResult.success(vo);
    }

    /**
     * 新建出库单
     *
     * @param outboundOrderForm 表单对象
     * @return
     * @throws WorkFlowException
     */
    @ApiOperation("新建出库单")
    @PostMapping
    public ActionResult create(@RequestBody OutboundOrderForm outboundOrderForm) throws WorkFlowException {
        OutboundOrderEntity outbound = JsonUtil.getJsonToBean(outboundOrderForm, OutboundOrderEntity.class);
        List<OutboundEntryEntity> outboundEntryList = JsonUtil.getJsonToList(outboundOrderForm.getEntryList(), OutboundEntryEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(outboundOrderForm.getStatus())) {
            outboundOrderService.save(outbound.getId(), outbound, outboundEntryList);
            return ActionResult.success("保存成功");
        }
        outboundOrderService.submit(outbound.getId(), outbound, outboundEntryList);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改出库单
     *
     * @param outboundOrderForm 表单对象
     * @param id                主键
     * @return
     * @throws WorkFlowException
     */
    @ApiOperation("修改出库单")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody OutboundOrderForm outboundOrderForm, @PathVariable("id") String id) throws WorkFlowException {
        OutboundOrderEntity outbound = JsonUtil.getJsonToBean(outboundOrderForm, OutboundOrderEntity.class);
        List<OutboundEntryEntity> outboundEntryList = JsonUtil.getJsonToList(outboundOrderForm.getEntryList(), OutboundEntryEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(outboundOrderForm.getStatus())) {
            outboundOrderService.save(id, outbound, outboundEntryList);
            return ActionResult.success("保存成功");
        }
        outboundOrderService.submit(id, outbound, outboundEntryList);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
