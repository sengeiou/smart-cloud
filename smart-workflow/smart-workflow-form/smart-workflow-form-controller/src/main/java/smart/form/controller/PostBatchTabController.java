package smart.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.engine.enums.FlowStatusEnum;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.form.entity.PostBatchTabEntity;
import smart.form.model.postbatchtab.PostBatchTabForm;
import smart.form.model.postbatchtab.PostBatchTabInfoVO;
import smart.form.service.PostBatchTabService;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 发文呈批表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月29日 上午9:18
 */
@Api(tags = "发文呈批表", value = "PostBatchTab")
@RestController
@RequestMapping("/Form/PostBatchTab")
public class PostBatchTabController {

    @Autowired
    private PostBatchTabService postBatchTabService;

    /**
     * 获取发文呈批表信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取发文呈批表信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        PostBatchTabEntity entity = postBatchTabService.getInfo(id);
        PostBatchTabInfoVO vo = JsonUtilEx.getJsonToBeanEx(entity, PostBatchTabInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 新建发文呈批表
     *
     * @param postBatchTabForm 表单对象
     * @return
     */
    @ApiOperation("新建发文呈批表")
    @PostMapping
    public ActionResult create(@RequestBody PostBatchTabForm postBatchTabForm) throws WorkFlowException {
        PostBatchTabEntity entity = JsonUtil.getJsonToBean(postBatchTabForm, PostBatchTabEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(postBatchTabForm.getStatus())) {
            postBatchTabService.save(entity.getId(), entity);
            return ActionResult.success("保存成功");
        }
        postBatchTabService.submit(entity.getId(), entity);
        return ActionResult.success("提交成功，请耐心等待");
    }

    /**
     * 修改发文呈批表
     *
     * @param postBatchTabForm 表单对象
     * @param id               主键
     * @return
     */
    @ApiOperation("修改发文呈批表")
    @PutMapping("/{id}")
    public ActionResult update(@RequestBody PostBatchTabForm postBatchTabForm, @PathVariable("id") String id) throws WorkFlowException {
        PostBatchTabEntity entity = JsonUtil.getJsonToBean(postBatchTabForm, PostBatchTabEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(postBatchTabForm.getStatus())) {
            postBatchTabService.save(id, entity);
            return ActionResult.success("保存成功");
        }
        postBatchTabService.submit(id, entity);
        return ActionResult.success("提交成功，请耐心等待");
    }
}
