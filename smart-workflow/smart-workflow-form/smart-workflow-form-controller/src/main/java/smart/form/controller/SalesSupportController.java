package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.SalesSupportEntity;
import smart.form.model.salessupport.SalesSupportForm;
import smart.form.model.salessupport.SalesSupportInfoVO;
import smart.form.service.SalesSupportService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 销售支持表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Api(tags = "销售支持表", value = "SalesSupport")
@RestController
@RequestMapping("/Form/SalesSupport")
public class SalesSupportController {

    @Autowired
    private SalesSupportService salesSupportService;

    /**
     * 获取销售支持表信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取销售支持表信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        SalesSupportEntity entity = salesSupportService.getInfo(id);
        SalesSupportInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, SalesSupportInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建销售支持表
     *
     * @param salesSupportForm 表单对象
     * @return
     */
    @ApiOperation("新建保存销售支持表")
    @PostMapping
    public ActionResult create(@RequestBody SalesSupportForm salesSupportForm) throws WorkFlowException {
        SalesSupportEntity entity = JsonUtil.getJsonToBean(salesSupportForm, SalesSupportEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(salesSupportForm.getStatus())) {
            salesSupportService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        salesSupportService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改销售支持表
     *
     * @param salesSupportForm 表单对象
     * @param id               主键
     * @return
     */
    @ApiOperation("修改销售支持表")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody SalesSupportForm salesSupportForm, @PathVariable("id") String id) throws WorkFlowException {
        SalesSupportEntity entity = JsonUtil.getJsonToBean(salesSupportForm, SalesSupportEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(salesSupportForm.getStatus())) {
            salesSupportService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        salesSupportService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
