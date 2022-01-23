package smart.engine.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.vo.PaginationVO;
import smart.engine.entity.FlowEngineEntity;
import smart.engine.entity.FlowTaskEntity;
import smart.engine.entity.FlowTaskNodeEntity;
import smart.engine.entity.FlowTaskOperatorEntity;
import smart.engine.enums.FlowMessageEnum;
import smart.engine.enums.FlowModuleEnum;
import smart.engine.model.FlowHandleModel;
import smart.engine.model.flowlaunch.FlowLaunchListVO;
import smart.engine.model.flowtask.PaginationFlowTask;
import smart.engine.service.FlowEngineService;
import smart.engine.service.FlowTaskNodeService;
import smart.engine.service.FlowTaskOperatorService;
import smart.engine.service.FlowTaskService;
import smart.exception.WorkFlowException;
import smart.form.service.OrderService;
import smart.message.NoticeApi;
import smart.message.model.SentMessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程发起
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "流程发起", value = "FlowLaunch")
@RestController
@RequestMapping("/Engine/FlowLaunch")
public class FlowLaunchController {

    @Autowired
    private FlowEngineService flowEngineService;
    @Autowired
    private FlowTaskNodeService flowTaskNodeService;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;
    @Autowired
    private NoticeApi noticeApi;

    /**
     * 获取流程发起列表
     *
     * @param paginationFlowTask
     * @return
     */
    @ApiOperation("获取流程发起列表(带分页)")
    @GetMapping
    public ActionResult list(PaginationFlowTask paginationFlowTask) {
        List<FlowEngineEntity> engineList = flowEngineService.getList();
        List<FlowTaskEntity> data = flowTaskService.getLaunchList(paginationFlowTask);
        List<FlowTaskNodeEntity> nodeList = flowTaskNodeService.getListAll();
        List<FlowLaunchListVO> listVO = new LinkedList<>();
        for (FlowTaskEntity taskEntity : data) {
            //用户名称赋值
            FlowLaunchListVO vo = JsonUtil.getJsonToBean(taskEntity, FlowLaunchListVO.class);
            FlowEngineEntity entity = engineList.stream().filter(t -> t.getId().equals(taskEntity.getFlowId())).findFirst().orElse(null);
            if (entity != null) {
                vo.setFormData(entity.getFormData());
                vo.setFormType(entity.getFormType());
            }
            listVO.add(vo);
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationFlowTask, PaginationVO.class);
        return ActionResult.page(listVO, paginationVO);
    }

    /**
     * 删除流程发起
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除流程发起")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) throws WorkFlowException {
        FlowTaskEntity entity = flowTaskService.getInfo(id);
        if (entity != null) {
            flowTaskService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /**
     * 待我审核催办
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("发起催办")
    @PostMapping("/Press/{id}")
    public ActionResult press(@PathVariable("id") String id) throws WorkFlowException {
        FlowTaskEntity flowTaskEntity = flowTaskService.getInfo(id);
        //单前流程节点
        String[] stepId = flowTaskEntity.getThisStepId().split(",");
        List<FlowTaskOperatorEntity> press = flowTaskOperatorService.press(stepId, id);
        List<String> processId = press.stream().map(t -> t.getHandleId()).distinct().collect(Collectors.toList());
        if (processId.size() > 0) {
            Map<String, Object> message = new HashMap<>(16);
            message.put("type", FlowMessageEnum.wait.getCode());
            message.put("id", processId);
            String bodyText = JsonUtil.getObjectToString(message);
            SentMessageModel model = new SentMessageModel();
            model.setBodyText(bodyText);
            model.setTitle(flowTaskEntity.getFullName() + "【催办】");
            model.setToUserIds(processId);
            noticeApi.sentMessage(model);
            return ActionResult.success("催办成功");
        }
        return ActionResult.fail("未找到催办人");
    }

    /**
     * 撤回流程发起
     * 注意：在撤销流程时要保证你的下一节点没有处理这条记录；如已处理则无法撤销流程。
     *
     * @param id              主键值
     * @param flowHandleModel 经办记录
     * @return
     */
    @ApiOperation("撤回流程发起")
    @PutMapping("/{id}/Actions/Withdraw")
    public ActionResult revoke(@PathVariable("id") String id, @RequestBody FlowHandleModel flowHandleModel) throws WorkFlowException {
        FlowTaskEntity flowTaskEntity = flowTaskService.getInfo(id);
        String crm = FlowModuleEnum.CRM_Order.getMessage();
        if (crm.equals(flowTaskEntity.getFlowCode())) {
            orderService.flowRevoke(flowTaskEntity, flowHandleModel);
        } else {
            flowTaskService.revoke(flowTaskEntity, flowHandleModel);
        }
        return ActionResult.success("撤回成功");
    }
}
