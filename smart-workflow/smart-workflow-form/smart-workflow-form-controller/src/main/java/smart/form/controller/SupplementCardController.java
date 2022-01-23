package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.SupplementCardEntity;
import smart.form.model.supplementcard.SupplementCardForm;
import smart.form.model.supplementcard.SupplementCardInfoVO;
import smart.form.service.SupplementCardService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 补卡申请
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Api(tags = "补卡申请", value = "SupplementCard")
@RestController
@RequestMapping("/Form/SupplementCard")
public class SupplementCardController {

    @Autowired
    private SupplementCardService supplementCardService;

    /**
     * 获取补卡申请信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("补卡申请信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        SupplementCardEntity entity = supplementCardService.getInfo(id);
        SupplementCardInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, SupplementCardInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建补卡申请
     *
     * @param supplementCardForm 表单对象
     * @return
     */
    @ApiOperation("新建补卡申请")
    @PostMapping
    public ActionResult create(@RequestBody SupplementCardForm supplementCardForm) throws WorkFlowException {
        if (supplementCardForm.getStartTime() > supplementCardForm.getEndTime()) {
            return ActionResult.fail("结束时间不能小于起始时间");
        }
        SupplementCardEntity entity = JsonUtil.getJsonToBean(supplementCardForm, SupplementCardEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(supplementCardForm.getStatus())) {
            supplementCardService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        supplementCardService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改补卡申请
     *
     * @param supplementCardForm 表单对象
     * @param id                 主键
     * @return
     */
    @ApiOperation("修改补卡申请")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody SupplementCardForm supplementCardForm, @PathVariable("id") String id) throws WorkFlowException {
        if (supplementCardForm.getStartTime() > supplementCardForm.getEndTime()) {
            return ActionResult.fail("结束时间不能小于起始时间");
        }
        SupplementCardEntity entity = JsonUtil.getJsonToBean(supplementCardForm, SupplementCardEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(supplementCardForm.getStatus())) {
            supplementCardService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        supplementCardService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
