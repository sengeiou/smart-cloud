package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.ApplyBanquetEntity;
import smart.form.model.applybanquet.ApplyBanquetForm;
import smart.form.model.applybanquet.ApplyBanquetInfoVO;
import smart.form.service.ApplyBanquetService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import smart.util.RegexUtils;
import smart.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 宴请申请
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "宴请申请", value = "ApplyBanquet")
@RestController
@RequestMapping("/Form/ApplyBanquet")
public class ApplyBanquetController {

    @Autowired
    private ApplyBanquetService applyBanquetService;

    /**
     * 获取宴请申请信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取宴请申请信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        ApplyBanquetEntity entity = applyBanquetService.getInfo(id);
        ApplyBanquetInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, ApplyBanquetInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建宴请申请
     *
     * @param applyBanquetForm 表单对象
     * @return
     */
    @ApiOperation("新建宴请申请")
    @PostMapping
    public ActionResult create(@RequestBody @Valid ApplyBanquetForm applyBanquetForm) throws WorkFlowException {
        if (applyBanquetForm.getBanquetNum() != null && StringUtil.isNotEmpty(applyBanquetForm.getBanquetNum()) && !RegexUtils.checkDigit2(applyBanquetForm.getBanquetNum())) {
            return ActionResult.fail("宴请人数必须大于0");
        }
        if (applyBanquetForm.getTotal() != null && StringUtil.isNotEmpty(applyBanquetForm.getTotal()) && !RegexUtils.checkDigit2(applyBanquetForm.getTotal())) {
            return ActionResult.fail("人员总数必须大于0");
        }
        if (applyBanquetForm.getExpectedCost() != null && !RegexUtils.checkDecimals2(String.valueOf(applyBanquetForm.getExpectedCost()))) {
            return ActionResult.fail("预计费用必须大于0，最多只能有两位小数");
        }
        ApplyBanquetEntity entity = JsonUtil.getJsonToBean(applyBanquetForm, ApplyBanquetEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(applyBanquetForm.getStatus())) {
            applyBanquetService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        applyBanquetService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改宴请申请
     *
     * @param applyBanquetForm 表单对象
     * @param id               主键
     * @return
     */
    @ApiOperation("修改宴请申请")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody @Valid ApplyBanquetForm applyBanquetForm, @PathVariable("id") String id) throws WorkFlowException {
        if (applyBanquetForm.getBanquetNum() != null && StringUtil.isNotEmpty(applyBanquetForm.getBanquetNum()) && !RegexUtils.checkDigit2(applyBanquetForm.getBanquetNum())) {
            return ActionResult.fail("宴请人数必须大于0");
        }
        if (applyBanquetForm.getTotal() != null && StringUtil.isNotEmpty(applyBanquetForm.getTotal()) && !RegexUtils.checkDigit2(applyBanquetForm.getTotal())) {
            return ActionResult.fail("人员总数必须大于0");
        }
        if (applyBanquetForm.getExpectedCost() != null && !RegexUtils.checkDecimals2(String.valueOf(applyBanquetForm.getExpectedCost()))) {
            return ActionResult.fail("预计费用必须大于0，最多只能有两位小数");
        }
        ApplyBanquetEntity entity = JsonUtil.getJsonToBean(applyBanquetForm, ApplyBanquetEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(applyBanquetForm.getStatus())) {
            applyBanquetService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        applyBanquetService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
