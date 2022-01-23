package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.MaterialEntryEntity;
import smart.form.entity.MaterialRequisitionEntity;
import smart.form.model.materialrequisition.MaterialEntryEntityInfoModel;
import smart.form.model.materialrequisition.MaterialRequisitionForm;
import smart.form.model.materialrequisition.MaterialRequisitionInfoVO;
import smart.form.service.MaterialRequisitionService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 领料单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "领料单", value = "MaterialRequisition")
@RestController
@RequestMapping("/Form/MaterialRequisition")
public class MaterialRequisitionController {

    @Autowired
    private MaterialRequisitionService materialRequisitionService;

    /**
     * 获取领料单信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取领料单信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        MaterialRequisitionEntity entity = materialRequisitionService.getInfo(id);
        List<MaterialEntryEntity> entityList = materialRequisitionService.getMaterialEntryList(id);
        MaterialRequisitionInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, MaterialRequisitionInfoVO.class);
        vo.setEntryList(JsonUtil.getJsonToList(entityList, MaterialEntryEntityInfoModel.class));
        return ActionResult.success(vo);
    }

    /**
     * 新建领料单
     *
     * @param materialRequisitionForm 表单对象
     * @return
     * @throws WorkFlowException
     */
    @ApiOperation("新建领料单")
    @PostMapping
    public ActionResult create(@RequestBody @Valid MaterialRequisitionForm materialRequisitionForm) throws WorkFlowException {
        MaterialRequisitionEntity material = JsonUtil.getJsonToBean(materialRequisitionForm, MaterialRequisitionEntity.class);
        List<MaterialEntryEntity> materialEntryList = JsonUtil.getJsonToList(materialRequisitionForm.getEntryList(), MaterialEntryEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(materialRequisitionForm.getStatus())) {
            materialRequisitionService.save(material.getId(), material, materialEntryList);
            return ActionResult.success("保存成功");
        }
        materialRequisitionService.submit(material.getId(), material, materialEntryList);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改领料单
     *
     * @param materialRequisitionForm 表单对象
     * @param id                      主键
     * @return
     * @throws WorkFlowException
     */
    @ApiOperation("修改领料单")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody @Valid MaterialRequisitionForm materialRequisitionForm, @PathVariable("id") String id) throws WorkFlowException {
        MaterialRequisitionEntity material = JsonUtil.getJsonToBean(materialRequisitionForm, MaterialRequisitionEntity.class);
        List<MaterialEntryEntity> materialEntryList = JsonUtil.getJsonToList(materialRequisitionForm.getEntryList(), MaterialEntryEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(materialRequisitionForm.getStatus())) {
            materialRequisitionService.save(id, material, materialEntryList);
            return ActionResult.success("保存成功");
        }
        materialRequisitionService.submit(id, material, materialEntryList);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
