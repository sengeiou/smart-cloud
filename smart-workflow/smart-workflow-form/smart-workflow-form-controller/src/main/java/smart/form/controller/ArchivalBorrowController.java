package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.ArchivalBorrowEntity;
import smart.form.model.archivalborrow.ArchivalBorrowForm;
import smart.form.model.archivalborrow.ArchivalBorrowInfoVO;
import smart.form.service.ArchivalBorrowService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 档案借阅申请
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "档案借阅申请", value = "ArchivalBorrow")
@RestController
@RequestMapping("/Form/ArchivalBorrow")
public class ArchivalBorrowController {

    @Autowired
    private ArchivalBorrowService archivalBorrowService;

    /**
     * 获取档案借阅申请信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取档案借阅申请信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        ArchivalBorrowEntity entity = archivalBorrowService.getInfo(id);
        ArchivalBorrowInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, ArchivalBorrowInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建档案借阅申请
     *
     * @param archivalBorrowForm 表单对象
     * @return
     */
    @ApiOperation("新建档案借阅申请")
    @PostMapping
    public ActionResult create(@RequestBody @Valid ArchivalBorrowForm archivalBorrowForm) throws WorkFlowException {
        if (archivalBorrowForm.getBorrowingDate() > archivalBorrowForm.getReturnDate()) {
            return ActionResult.fail("归还时间不能小于借阅时间");
        }
        ArchivalBorrowEntity entity = JsonUtil.getJsonToBean(archivalBorrowForm, ArchivalBorrowEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(archivalBorrowForm.getStatus())) {
            archivalBorrowService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        archivalBorrowService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改档案借阅申请
     *
     * @param archivalBorrowForm 表单对象
     * @param id                 主键
     * @return
     */
    @ApiOperation("修改档案借阅申请")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody @Valid ArchivalBorrowForm archivalBorrowForm, @PathVariable("id") String id) throws WorkFlowException {
        if (archivalBorrowForm.getBorrowingDate() > archivalBorrowForm.getReturnDate()) {
            return ActionResult.fail("归还时间不能小于借阅时间");
        }
        ArchivalBorrowEntity entity = JsonUtil.getJsonToBean(archivalBorrowForm, ArchivalBorrowEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(archivalBorrowForm.getStatus())) {
            archivalBorrowService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        archivalBorrowService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
