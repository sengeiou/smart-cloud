package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.ProcurementEntryEntity;
import smart.form.entity.ProcurementMaterialEntity;
import smart.form.model.procurementmaterial.ProcurementEntryEntityInfoModel;
import smart.form.model.procurementmaterial.ProcurementMaterialForm;
import smart.form.model.procurementmaterial.ProcurementMaterialInfoVO;
import smart.form.service.ProcurementMaterialService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采购原材料
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Api(tags = "采购原材料", value = "ProcurementMaterial")
@RestController
@RequestMapping("/Form/ProcurementMaterial")
public class ProcurementMaterialController {

    @Autowired
    private ProcurementMaterialService procurementMaterialService;

    /**
     * 获取采购原材料信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取采购原材料信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        ProcurementMaterialEntity entity = procurementMaterialService.getInfo(id);
        List<ProcurementEntryEntity> entityList = procurementMaterialService.getProcurementEntryList(id);
        ProcurementMaterialInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, ProcurementMaterialInfoVO.class);
        vo.setEntryList(JsonUtil.getJsonToList(entityList, ProcurementEntryEntityInfoModel.class));
        return ActionResult.success(vo);
    }

    /**
     * 新建采购原材料
     *
     * @param procurementMaterialForm 表单对象
     * @return
     * @throws WorkFlowException
     */
    @ApiOperation("新建采购原材料")
    @PostMapping
    public ActionResult create(@RequestBody ProcurementMaterialForm procurementMaterialForm) throws WorkFlowException {
        ProcurementMaterialEntity procurement = JsonUtil.getJsonToBean(procurementMaterialForm, ProcurementMaterialEntity.class);
        List<ProcurementEntryEntity> procurementEntryList = JsonUtil.getJsonToList(procurementMaterialForm.getEntryList(), ProcurementEntryEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(procurementMaterialForm.getStatus())) {
            procurementMaterialService.save(procurement.getId(), procurement, procurementEntryList);
            return ActionResult.success("保存成功");
        }
        procurementMaterialService.submit(procurement.getId(), procurement, procurementEntryList);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改采购原材料
     *
     * @param procurementMaterialForm 表单对象
     * @param id                      主键
     * @return
     * @throws WorkFlowException
     */
    @ApiOperation("修改采购原材料")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody ProcurementMaterialForm procurementMaterialForm, @PathVariable("id") String id) throws WorkFlowException {
        ProcurementMaterialEntity procurement = JsonUtil.getJsonToBean(procurementMaterialForm, ProcurementMaterialEntity.class);
        List<ProcurementEntryEntity> procurementEntryList = JsonUtil.getJsonToList(procurementMaterialForm.getEntryList(), ProcurementEntryEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(procurementMaterialForm.getStatus())) {
            procurementMaterialService.save(id, procurement, procurementEntryList);
            return ActionResult.success("保存成功");
        }
        procurementMaterialService.submit(id, procurement, procurementEntryList);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
