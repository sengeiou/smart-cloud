package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.DocumentApprovalEntity;
import smart.form.model.documentapproval.DocumentApprovalForm;
import smart.form.model.documentapproval.DocumentApprovalInfoVO;
import smart.form.service.DocumentApprovalService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 文件签批意见表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "文件签批意见表", value = "DocumentApproval")
@RestController
@RequestMapping("/Form/DocumentApproval")
public class DocumentApprovalController {

    @Autowired
    private DocumentApprovalService documentApprovalService;

    /**
     * 获取文件签批意见表信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取文件签批意见表信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        DocumentApprovalEntity entity = documentApprovalService.getInfo(id);
        DocumentApprovalInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, DocumentApprovalInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建文件签批意见表
     *
     * @param documentApprovalForm 表单对象
     * @return
     */
    @ApiOperation("新建文件签批意见表")
    @PostMapping
    public ActionResult create(@RequestBody @Valid DocumentApprovalForm documentApprovalForm) throws WorkFlowException {
        DocumentApprovalEntity entity = JsonUtil.getJsonToBean(documentApprovalForm, DocumentApprovalEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(documentApprovalForm.getStatus())) {
            documentApprovalService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        documentApprovalService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改文件签批意见表
     *
     * @param documentApprovalForm 表单对象
     * @param id                   主键
     * @return
     */
    @ApiOperation("修改文件签批意见表")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody @Valid DocumentApprovalForm documentApprovalForm, @PathVariable("id") String id) throws WorkFlowException {
        DocumentApprovalEntity entity = JsonUtil.getJsonToBean(documentApprovalForm, DocumentApprovalEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(documentApprovalForm.getStatus())) {
            documentApprovalService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        documentApprovalService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
