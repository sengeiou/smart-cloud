package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.DocumentSigningEntity;
import smart.form.model.documentsigning.DocumentSigningForm;
import smart.form.model.documentsigning.DocumentSigningInfoVO;
import smart.form.service.DocumentSigningService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 文件签阅表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "文件签阅表", value = "DocumentSigning")
@RestController
@RequestMapping("/Form/DocumentSigning")
public class DocumentSigningController {

    @Autowired
    private DocumentSigningService documentSigningService;

    /**
     * 获取文件签阅表信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取文件签阅表信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        DocumentSigningEntity entity = documentSigningService.getInfo(id);
        DocumentSigningInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, DocumentSigningInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建文件签阅表
     *
     * @param documentSigningForm 表单对象
     * @return
     */
    @ApiOperation("新建文件签阅表")
    @PostMapping
    public ActionResult create(@RequestBody @Valid DocumentSigningForm documentSigningForm) throws WorkFlowException {
        DocumentSigningEntity entity = JsonUtil.getJsonToBean(documentSigningForm, DocumentSigningEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(documentSigningForm.getStatus())) {
            documentSigningService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        documentSigningService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改文件签阅表
     *
     * @param documentSigningForm 表单对象
     * @param id                  主键
     * @return
     */
    @ApiOperation("修改文件签阅表")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody @Valid DocumentSigningForm documentSigningForm, @PathVariable("id") String id) throws WorkFlowException {
        DocumentSigningEntity entity = JsonUtil.getJsonToBean(documentSigningForm, DocumentSigningEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(documentSigningForm.getStatus())) {
            documentSigningService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        documentSigningService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
