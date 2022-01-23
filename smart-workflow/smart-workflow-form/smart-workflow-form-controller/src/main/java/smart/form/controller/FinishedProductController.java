package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.FinishedProductEntity;
import smart.form.entity.FinishedProductEntryEntity;
import smart.form.model.finishedproduct.FinishedProductEntryEntityInfoModel;
import smart.form.model.finishedproduct.FinishedProductForm;
import smart.form.model.finishedproduct.FinishedProductInfoVO;
import smart.form.service.FinishedProductService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 成品入库单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "成品入库单", value = "FinishedProduct")
@RestController
@RequestMapping("/Form/FinishedProduct")
public class FinishedProductController {

    @Autowired
    private FinishedProductService finishedProductService;

    /**
     * 获取成品入库单信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取成品入库单信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        FinishedProductEntity entity = finishedProductService.getInfo(id);
        List<FinishedProductEntryEntity> entityList = finishedProductService.getFinishedEntryList(id);
        FinishedProductInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, FinishedProductInfoVO.class);
        vo.setEntryList(JsonUtil.getJsonToList(entityList, FinishedProductEntryEntityInfoModel.class));
        return ActionResult.success(vo);
    }

    /**
     * 新建成品入库单
     *
     * @param finishedProductForm 表单对象
     * @return
     * @throws WorkFlowException
     */
    @ApiOperation("新建成品入库单")
    @PostMapping
    public ActionResult create(@RequestBody @Valid FinishedProductForm finishedProductForm) throws WorkFlowException {
        FinishedProductEntity finished = JsonUtil.getJsonToBean(finishedProductForm, FinishedProductEntity.class);
        List<FinishedProductEntryEntity> finishedEntryList = JsonUtil.getJsonToList(finishedProductForm.getEntryList(), FinishedProductEntryEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(finishedProductForm.getStatus())) {
            finishedProductService.save(finished.getId(), finished, finishedEntryList);
            return ActionResult.success("保存成功");
        }
        finishedProductService.submit(finished.getId(), finished, finishedEntryList);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改成品入库单
     *
     * @param finishedProductForm 表单对象
     * @param id                  主键
     * @return
     * @throws WorkFlowException
     */
    @ApiOperation("修改成品入库单")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody @Valid FinishedProductForm finishedProductForm, @PathVariable("id") String id) throws WorkFlowException {
        FinishedProductEntity finished = JsonUtil.getJsonToBean(finishedProductForm, FinishedProductEntity.class);
        List<FinishedProductEntryEntity> finishedEntryList = JsonUtil.getJsonToList(finishedProductForm.getEntryList(), FinishedProductEntryEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(finishedProductForm.getStatus())) {
            finishedProductService.save(id, finished, finishedEntryList);
            return ActionResult.success("保存成功");
        }
        finishedProductService.submit(id, finished, finishedEntryList);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
