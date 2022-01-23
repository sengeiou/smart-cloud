package smart.engine.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.base.vo.PaginationVO;
import smart.engine.entity.*;
import smart.engine.enums.FlowBeforeEnum;
import smart.engine.enums.FlowNodeEnum;
import smart.engine.enums.FlowTaskOperatorEnum;
import smart.engine.model.FlowHandleModel;
import smart.engine.model.flowbefore.*;
import smart.engine.model.flowengine.shuntjson.childnode.FormOperates;
import smart.engine.model.flowengine.shuntjson.childnode.Properties;
import smart.engine.model.flowengine.shuntjson.nodejson.ChildNodeList;
import smart.engine.model.flowengine.shuntjson.nodejson.DateProperties;
import smart.engine.model.flowtask.PaginationFlowTask;
import smart.engine.service.*;
import smart.engine.util.FlowJsonUtil;
import smart.engine.util.FlowNature;
import smart.exception.WorkFlowException;
import smart.permission.OrganizeApi;
import smart.permission.UserRelationApi;
import smart.permission.UsersApi;
import smart.permission.entity.OrganizeEntity;
import smart.permission.entity.UserRelationEntity;
import smart.permission.model.user.UserAllModel;
import smart.util.JsonUtil;
import smart.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 待我审核
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "待我审核", value = "FlowBefore")
@RestController
@RequestMapping("/Engine/FlowBefore")
public class FlowBeforeController {

    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private FlowTaskOperatorService flowTaskOperatorService;
    @Autowired
    private FlowTaskOperatorRecordService flowTaskOperatorRecordService;
    @Autowired
    private FlowTaskNodeService flowTaskNodeService;
    @Autowired
    private FlowEngineService flowEngineService;
    @Autowired
    private UsersApi usersApi;
    @Autowired
    private OrganizeApi organizeApi;
    @Autowired
    private UserRelationApi userRelationApi;

    /**
     * 获取待我审核列表
     *
     * @param category           分类
     * @param paginationFlowTask
     * @return
     */
    @ApiOperation("获取待我审核列表(有带分页)，1-待办事宜，2-已办事宜，3-抄送事宜")
    @GetMapping("/List/{category}")
    public ActionResult list(@PathVariable("category") String category, PaginationFlowTask paginationFlowTask) {
        List<FlowTaskEntity> data = new ArrayList<>();
        if (FlowNature.WAIT.equals(category)) {
            data = flowTaskService.getWaitList(paginationFlowTask);
        } else if (FlowNature.TRIAL.equals(category)) {
            data = flowTaskService.getTrialList(paginationFlowTask);
        } else if (FlowNature.CIRCULATE.equals(category)) {
            data = flowTaskService.getCirculateList(paginationFlowTask);
        }
        List<FlowBeforeListVO> listVO = new LinkedList<>();
        if (data.size() > 0) {
            List<FlowEngineEntity> engineList = flowEngineService.getList();
            List<UserAllModel> userList = usersApi.getAll().getData();
            for (FlowTaskEntity taskEntity : data) {
                //用户名称赋值
                FlowBeforeListVO vo = JsonUtil.getJsonToBean(taskEntity, FlowBeforeListVO.class);
                UserAllModel user = userList.stream().filter(t -> t.getId().equals(taskEntity.getCreatorUserId())).findFirst().orElse(null);
                if (user != null) {
                    vo.setUserName(user.getRealName() + "/" + user.getAccount());
                } else {
                    vo.setUserName("");
                }
                FlowEngineEntity engine = engineList.stream().filter(t -> t.getId().equals(taskEntity.getFlowId())).findFirst().orElse(null);
                if (engine != null) {
                    vo.setFormType(engine.getFormType());
                }
                listVO.add(vo);
            }
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationFlowTask, PaginationVO.class);
        return ActionResult.page(listVO, paginationVO);
    }

    /**
     * 获取待我审批信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取待我审批信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws WorkFlowException {
        List<UserAllModel> userAllModels = usersApi.getDbUserAll();
        FlowTaskEntity flowTaskEntity = flowTaskService.getInfo(id);
        List<FlowTaskNodeEntity> flowTaskNodeList = flowTaskNodeService.getList(flowTaskEntity.getId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState()) || FlowNodeEnum.Futility.getCode().equals(t.getState())).collect(Collectors.toList());
        Long freeApprover = flowEngineService.getFlowNodeList(flowTaskEntity.getThisStepId(), flowTaskNodeList);
        //流程经办
        List<FlowTaskOperatorEntity> flowTaskOperatorList = flowTaskOperatorService.getList(flowTaskEntity.getId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        //审核记录
        List<FlowTaskOperatorRecordEntity> flowTaskOperatorRecordList = flowTaskOperatorRecordService.getList(flowTaskEntity.getId());
        FlowBeforeInfoVO vo = new FlowBeforeInfoVO();
        vo.setFreeApprover(freeApprover);
        //当前节点
        String[] tepId = flowTaskEntity.getThisStepId().split(",");
        List<Long> collect = flowTaskNodeList.stream().filter(t -> flowTaskEntity.getThisStepId().contains(t.getNodeCode())).map(t -> t.getSortCode()).distinct().collect(Collectors.toList());
        Set<String> upAll = new HashSet<>();
        Set<String> nextAll = new HashSet<>();
        for (String node : tepId) {
            //当前完成节点
            Set<String> upList = new HashSet<>();
            FlowJsonUtil.upList(flowTaskNodeList, node, upList, tepId);
            upAll.addAll(upList);
            //当前之后节点
            Set<String> nextList = new HashSet<>();
            FlowJsonUtil.nextList(flowTaskNodeList, node, nextList, tepId);
            nextAll.addAll(nextList);
        }
        //替换当前节点
        Set<String> thisTepId = new HashSet<>();
        Set<String> thisTepName = new HashSet<>();
        List<FlowTaskNodeEntityInfoModel> flowTaskNodes = JsonUtil.getJsonToList(flowTaskNodeList, FlowTaskNodeEntityInfoModel.class);
        List<FlowTaskNodeEntityInfoModel> flowTaskNodesAll = new ArrayList<>();
        for (FlowTaskNodeEntityInfoModel modelList : flowTaskNodes) {
            ChildNodeList models = JsonUtil.getJsonToBean(modelList.getNodePropertyJson(), ChildNodeList.class);
            Properties properties = models.getProperties();
            String assType = String.valueOf(properties.getAssigneeType());
            String code = modelList.getNodeCode();
            //用户名称赋值
            List<String> userList = new ArrayList<>();
            List<String> assigList = new ArrayList<>();
            String thisType = FlowBeforeEnum.Futility.getCode();
            if (upAll.contains(code)) {
                thisType = FlowBeforeEnum.Pass.getCode();
            } else if (nextAll.contains(code)) {
                thisType = FlowBeforeEnum.Undone.getCode();
            } else if (Arrays.asList(tepId).contains(code)) {
                thisType = FlowBeforeEnum.Present.getCode();
            }
            //赋值名称
            if ("start".equals(modelList.getNodeType())) {
                UserAllModel userModel = userAllModels.stream().filter(t -> t.getId().equals(flowTaskEntity.getCreatorUserId())).findFirst().get();
                userList.add(userModel.getRealName() + "/" + userModel.getAccount());
            } else if (!"endround".equals(modelList.getNodeType())) {
                UserAllModel userModel = userAllModels.stream().filter(t -> t.getId().equals(flowTaskEntity.getCreatorUserId())).findFirst().get();
                if (assType.equals(String.valueOf(FlowTaskOperatorEnum.LaunchCharge.getCode()))) {
                    //发起者【发起主管】
                    String[] managerIdAll  = userModel.getManagerId().split(",");
                    for (String managerId : managerIdAll) {
                        UserAllModel manager = userAllModels.stream().filter(t -> t.getId().equals(managerId)).findFirst().orElse(null);
                        if (manager != null) {
                            userList.add(manager.getRealName() + "/" + manager.getAccount());
                        }
                    }
                    assigList.add(FlowTaskOperatorEnum.LaunchCharge.getMessage());
                } else if (assType.equals(String.valueOf(FlowTaskOperatorEnum.DepartmentCharge.getCode()))) {
                    //发起者【部门主管】
                    OrganizeEntity info = organizeApi.getById(userModel.getDepartmentId());
                    UserAllModel depUserModel = userAllModels.stream().filter(t -> t.getId().equals(info.getManager())).findFirst().orElse(null);
                    if (depUserModel != null) {
                        userList.add(depUserModel.getRealName() + "/" + depUserModel.getAccount());
                    }
                    assigList.add(FlowTaskOperatorEnum.DepartmentCharge.getMessage());
                } else if (assType.equals(String.valueOf(FlowTaskOperatorEnum.InitiatorMe.getCode()))) {
                    //发起者【发起本人】
                    userList.add(userModel.getRealName() + "/" + userModel.getAccount());
                    assigList.add(FlowTaskOperatorEnum.InitiatorMe.getMessage());
                } else if (assType.equals(String.valueOf(FlowTaskOperatorEnum.FreeApprover.getCode()))) {
                    //发起者【授权审批人】
                    List<FlowTaskOperatorEntity> freeAppList = flowTaskOperatorList.stream().filter(t -> t.getNodeCode().equals(models.getCustom().getNodeId())).collect(Collectors.toList());
                    if (freeAppList.size() > 0) {
                        FlowTaskOperatorEntity flowTask = freeAppList.get(0);
                        UserAllModel freeUser = userAllModels.stream().filter(t -> t.getId().equals(flowTask.getHandleId())).findFirst().get();
                        userList.add(freeUser.getRealName() + "/" + freeUser.getAccount());
                    } else {
                        userList.add("加签");
                    }
                    assigList.add(FlowTaskOperatorEnum.FreeApprover.getMessage());
                } else {
                    if (properties.getApproverPos() != null) {
                        if(properties.getApproverPos().length>0){
                            List<UserRelationEntity> list = userRelationApi.getObjectList(String.join("," , properties.getApproverPos()));
                            List<String> listByObjectIdAll = list.stream().map(t->t.getUserId()).collect(Collectors.toList());
                            for(String userId : listByObjectIdAll){
                                List<UserAllModel> approverPosUser = userAllModels.stream().filter(t -> t.getId().equals(userId)).collect(Collectors.toList());
                                for (UserAllModel userAllModel : approverPosUser) {
                                    userList.add(userAllModel.getRealName() + "/" + userAllModel.getAccount());
                                }
                            }
                        }
                    }
                    if (properties.getApprovers() != null) {
                        for (String approvers : properties.getApprovers()) {
                            UserAllModel approversUser = userAllModels.stream().filter(t -> t.getId().equals(approvers)).findFirst().orElse(new UserAllModel());
                            if (approversUser.getAccount() != null) {
                                String userName = approversUser.getRealName() + "/" + approversUser.getAccount();
                                if (userList.stream().filter(t -> t.equals(userName)).count() == 0) {
                                    userList.add(userName);
                                }
                            }
                        }
                    }
                    if (StringUtil.isNotEmpty(assType)) {
                        assigList.add(FlowTaskOperatorEnum.getMessageByCode(assType));
                    }
                }
            }
            //判断当前节点
            if (FlowBeforeEnum.Present.getCode().equals(thisType)) {
                List<DateProperties> timerAll = models.getTimerAll();
                Date date = new Date();
                //获取定时器的list
                DateProperties dateProperties = timerAll.stream().filter(t -> t.getDate().getTime() > date.getTime()).findFirst().orElse(null);
                String thistepId = modelList.getNodeCode();
                String thistepName = modelList.getNodeName();
                //判断定时器是否还没有结束
                if (collect.size() == 1 && dateProperties != null) {
                    thistepId = dateProperties.getNodeId();
                    thistepName = dateProperties.getTitle();
                    thisType = FlowBeforeEnum.Undone.getCode();
                    //添加定时器节点
                    FlowTaskNodeEntityInfoModel timer = new FlowTaskNodeEntityInfoModel();
                    timer.setType(FlowBeforeEnum.Present.getCode());
                    timer.setNodeCode(thistepId);
                    flowTaskNodesAll.add(timer);
                }
                thisTepId.add(thistepId);
                thisTepName.add(thistepName);
            }
            //赋值节点的类型和名称
            modelList.setType(thisType);
            modelList.setAssigneeName(String.join(",", assigList));
            modelList.setUserName(String.join(",", userList));
            flowTaskNodesAll.add(modelList);
        }
        FlowTaskEntityInfoModel inof = JsonUtil.getJsonToBean(flowTaskEntity, FlowTaskEntityInfoModel.class);
        inof.setThisStepId(String.join(",", thisTepId));
        inof.setThisStep(String.join(",", thisTepName));
        vo.setFlowTaskInfo(inof);
        vo.setFlowTaskNodeList(flowTaskNodesAll);
        List<FlowTaskOperatorEntityInfoModel> flowTaskOperator = JsonUtil.getJsonToList(flowTaskOperatorList, FlowTaskOperatorEntityInfoModel.class);
        vo.setFlowTaskOperatorList(flowTaskOperator);
        List<FlowTaskOperatorRecordEntityInfoModel> recordList = JsonUtil.getJsonToList(flowTaskOperatorRecordList, FlowTaskOperatorRecordEntityInfoModel.class);
        if (recordList.size() > 0) {
            for (FlowTaskOperatorRecordEntityInfoModel model : recordList) {
                UserAllModel user = userAllModels.stream().filter(t -> t.getId().equals(model.getHandleId())).findFirst().orElse(new UserAllModel());
                model.setUserName(user.getRealName() + "/" + user.getAccount());
            }
        }
        //表单数据(要传参数判断点击哪个节点)
        String nodeJson = flowTaskNodes.stream().filter(t -> t.getNodeCode().equals(inof.getThisStepId())).findFirst().orElse(new FlowTaskNodeEntityInfoModel()).getNodePropertyJson();
        if (!StringUtil.isEmpty(nodeJson)) {
            ChildNodeList childNodeList = JsonUtil.getJsonToBean(nodeJson, ChildNodeList.class);
            List<FormOperates> formOperates = childNodeList.getProperties().getFormOperates();
            vo.setFormOperates(formOperates);
        }
        vo.setFlowTaskOperatorRecordList(recordList);
        vo.setFlowFormInfo(inof.getFlowForm());
        return ActionResult.success(vo);
    }

    /**
     * 待我审核审核
     *
     * @param id              待办主键值
     * @param flowHandleModel 流程经办
     * @return
     */
    @ApiOperation("待我审核审核")
    @PostMapping("/Audit/{id}")
    public ActionResult audit(@PathVariable("id") String id, @RequestBody FlowHandleModel flowHandleModel) throws WorkFlowException {
        FlowTaskOperatorEntity flowTaskOperatorEntity = flowTaskOperatorService.getInfo(id);
        if (flowTaskOperatorEntity == null) {
            return ActionResult.fail("审核失败");
        } else {
            FlowTaskEntity flowTaskEntity = flowTaskService.getInfo(flowTaskOperatorEntity.getTaskId());
            if (flowTaskOperatorEntity.getCompletion() == 0) {
                flowTaskService.audit(flowTaskEntity, flowTaskOperatorEntity, flowHandleModel);
                return ActionResult.success("审核成功");
            }else {
                return ActionResult.fail("已审核完成");
            }
        }
    }

    /**
     * 待我审核驳回
     *
     * @param id              待办主键值
     * @param flowHandleModel 经办信息
     * @return
     */
    @ApiOperation("待我审核驳回")
    @PostMapping("/Reject/{id}")
    public ActionResult reject(@PathVariable("id") String id, @RequestBody FlowHandleModel flowHandleModel) throws WorkFlowException {
        FlowTaskOperatorEntity flowTaskOperatorEntity = flowTaskOperatorService.getInfo(id);
        if (flowTaskOperatorEntity == null) {
            return ActionResult.fail("驳回失败");
        } else {
            FlowTaskEntity flowTaskEntity = flowTaskService.getInfo(flowTaskOperatorEntity.getTaskId());
            if (flowTaskOperatorEntity.getCompletion() == 0) {
                flowTaskService.reject(flowTaskEntity, flowTaskOperatorEntity, flowHandleModel);
                return ActionResult.success("驳回成功");
            }else {
                return ActionResult.fail("已审核完成");
            }
        }
    }

    /**
     * 待我审核转办
     *
     * @param id              主键值
     * @param flowHandleModel 经办信息
     * @return
     */
    @ApiOperation("待我审核转办")
    @PostMapping("/Transfer/{id}")
    public ActionResult transfer(@PathVariable("id") String id, @RequestBody FlowHandleModel flowHandleModel) {
        FlowTaskOperatorEntity flowTaskOperatorEntity = flowTaskOperatorService.getInfo(id);
        if (flowTaskOperatorEntity == null) {
            return ActionResult.fail("转办失败");
        } else {
            flowTaskOperatorEntity.setHandleId(flowHandleModel.getFreeApproverUserId());
            flowTaskOperatorService.update(flowTaskOperatorEntity);
            return ActionResult.success("转办成功");
        }
    }


    /**
     * 待我审核撤回审核
     * 注意：在撤销流程时要保证你的下一节点没有处理这条记录；如已处理则无法撤销流程。
     *
     * @param id              主键值
     * @param flowHandleModel 实体对象
     * @return
     */
    @ApiOperation("待我审核撤回审核")
    @PostMapping("/Recall/{id}")
    public ActionResult recall(@PathVariable("id") String id, @RequestBody FlowHandleModel flowHandleModel) throws WorkFlowException {
        FlowTaskOperatorRecordEntity flowTaskOperatorRecordEntity = flowTaskOperatorRecordService.getInfo(id);
        FlowTaskEntity flowTaskEntity = flowTaskService.getInfo(flowTaskOperatorRecordEntity.getTaskId());
        List<FlowTaskNodeEntity> flowTaskNodeEntityList = flowTaskNodeService.getList(flowTaskOperatorRecordEntity.getTaskId()).stream().filter(t -> FlowNodeEnum.Process.getCode().equals(t.getState())).collect(Collectors.toList());
        FlowTaskNodeEntity flowTaskNodeEntity = flowTaskNodeEntityList.stream().filter(t -> t.getId().equals(flowTaskOperatorRecordEntity.getTaskNodeId())).findFirst().orElse(null);
        if (flowTaskNodeEntity != null) {
            String nextId = flowTaskNodeEntity.getNodeNext();
            flowTaskService.recall(flowTaskEntity, flowTaskNodeEntityList, flowTaskOperatorRecordEntity,  flowHandleModel);
//            HandleEvent(FlowHandleEventEnum.Recall, flowTaskEntity);
            return ActionResult.success("撤回成功");
        }
        return ActionResult.fail("撤回失败");
    }

    /**
     * 待我审核终止审核
     *
     * @param id              主键值
     * @param flowHandleModel 流程经办
     * @return
     */
    @ApiOperation("待我审核终止审核")
    @PostMapping("/Cancel/{id}")
    public ActionResult cancel(@PathVariable("id") String id, @RequestBody FlowHandleModel flowHandleModel) throws WorkFlowException {
        FlowTaskEntity flowTaskEntity = flowTaskService.getInfo(id);
        if (flowTaskEntity != null) {
            flowTaskService.cancel(flowTaskEntity, flowHandleModel);
//            HandleEvent(FlowHandleEventEnum.Cancel, flowTaskEntity);
            return ActionResult.success("终止成功");
        }
        return ActionResult.fail("终止失败，数据不存在");
    }

    /**
     * 流程事件
     *
     * @param flowHandleEvent 经办事件
     * @param flowTaskEntity  流程任务
     */
//    private void HandleEvent(FlowHandleEventEnum flowHandleEvent, FlowTaskEntity flowTaskEntity) {
//        if (flowTaskEntity.getFlowCode().equals(FlowModuleEnum.CRM_Order.getMessage())) {
//            orderService.FlowHandleEvent(flowHandleEvent, flowTaskEntity);
//        }
//    }

}
