package smart.engine.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.util.StringUtil;
import smart.base.DictionaryDataApi;
import smart.base.entity.DictionaryDataEntity;
import smart.base.model.FormDataField;
import smart.base.model.FormDataModel;
import smart.base.model.dictionarydata.DictionaryDataModel;
import smart.engine.model.flowengine.*;
import smart.permission.UsersApi;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.engine.entity.FlowEngineEntity;
import smart.engine.entity.FlowEngineVisibleEntity;
import smart.engine.entity.FlowTaskEntity;
import smart.engine.enums.FlowTaskOperatorEnum;
import smart.engine.model.flowdynamic.FormAllModel;
import smart.engine.model.flowdynamic.FormEnum;
import smart.engine.model.flowengine.shuntjson.childnode.ChildNode;
import smart.engine.model.flowengine.shuntjson.childnode.Properties;
import smart.engine.service.FlowEngineService;
import smart.engine.service.FlowTaskService;
import smart.engine.util.FormCloumnUtil;
import smart.exception.DataException;
import smart.exception.WorkFlowException;
import smart.permission.model.user.UserAllModel;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import smart.onlinedev.model.fields.FieLdsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程设计
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "流程引擎", value = "FlowEngine")
@RestController
@RequestMapping("/Engine/FlowEngine")
public class FlowEngineController {

    @Autowired
    private UsersApi usersApi;
    @Autowired
    private FlowEngineService flowEngineService;
    @Autowired
    private FlowTaskService flowTaskService;
    @Autowired
    private DictionaryDataApi dictionaryDataApi;

    /**
     * 获取流程设计列表
     *
     * @return
     */
    @ApiOperation("获取流程引擎列表")
    @GetMapping
    public ActionResult list(PaginationFlowEngine pagination) {
        List<FlowEngineEntity> list = flowEngineService.getList(pagination);
        List<FlowEngineListVO> result = JsonUtil.getJsonToList(list, FlowEngineListVO.class);
        if (result.size() > 0) {
            List<UserAllModel> userList = usersApi.getAll().getData();
            for (FlowEngineListVO model : result) {
                UserAllModel creatorUser = userList.stream().filter(t -> t.getId().equals(model.getCreatorUser())).findFirst().orElse(null);
                if (creatorUser != null) {
                    model.setCreatorUser(creatorUser.getRealName() + "/" + creatorUser.getAccount());
                } else {
                    model.setCreatorUser("");
                }
                UserAllModel lastModifyUser = userList.stream().filter(t -> t.getId().equals(model.getLastModifyUser())).findFirst().orElse(null);
                if (lastModifyUser != null) {
                    model.setLastModifyUser(lastModifyUser.getRealName() + "/" + lastModifyUser.getAccount());
                } else {
                    model.setLastModifyUser("");
                }
            }
        }
        ListVO vo = new ListVO();
        vo.setList(result);
        return ActionResult.success(vo);
    }

    /**
     * 获取流程设计列表
     *
     * @return
     */
    @ApiOperation("流程引擎下拉框")
    @GetMapping("/Selector")
    public ActionResult listSelect(Integer type) {
        List<FlowEngineEntity> flowlist = flowEngineService.getList().stream().filter(t -> "1".equals(String.valueOf(t.getEnabledMark()))).collect(Collectors.toList());
        if (type != null) {
            flowlist = flowlist.stream().filter(t -> type.equals(t.getFormType())).collect(Collectors.toList());
        }
        List<DictionaryDataEntity> dictionList = dictionaryDataApi.getList("507f4f5df86b47588138f321e0b0dac7").getData();
        List<DictionaryDataModel> data = new ArrayList<>();
        for (DictionaryDataEntity dataEntity : dictionList) {
            List<FlowEngineEntity> flowData = flowlist.stream().filter(t -> String.valueOf(t.getCategory()).equals(dataEntity.getEnCode())).collect(Collectors.toList());
            if (flowData.size() > 0) {
                DictionaryDataModel model = new DictionaryDataModel();
                model.setParentId(dataEntity.getParentId());
                model.setId(dataEntity.getId());
                model.setFullName(dataEntity.getFullName());
                data.add(model);
                for (FlowEngineEntity engineEntity : flowData) {
                    DictionaryDataModel childModel = new DictionaryDataModel();
                    childModel.setParentId(dataEntity.getId());
                    childModel.setId(engineEntity.getId());
                    childModel.setFullName(engineEntity.getFullName());
                    data.add(childModel);
                }
            }
        }
        //转换
        List<SumTree<DictionaryDataModel>> sumTrees = TreeDotUtils.convertListToTreeDot(data);
        List<FlowEngineListSelectVO> result = JsonUtil.getJsonToList(sumTrees, FlowEngineListSelectVO.class);
        ListVO vo = new ListVO();
        vo.setList(result);
        return ActionResult.success(vo);
    }

    /**
     * 主表属性
     *
     * @return
     */
    @ApiOperation("表单主表属性")
    @GetMapping("/{id}/FormDataFields")
    public ActionResult getFormDataField(@PathVariable("id") String id) throws WorkFlowException {
        FlowEngineEntity entity = flowEngineService.getInfo(id);
        //formTempJson
        FormDataModel formData = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
        List<FieLdsModel> list = JsonUtil.getJsonToList(formData.getFields(), FieLdsModel.class);
        List<FormAllModel> formAllModel = new ArrayList<>();
        FormCloumnUtil.recursionForm(list, formAllModel);
        //主表数据
        List<FormAllModel> mast = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormDataField> formDataFieldList = new ArrayList<>();
        for (FormAllModel model : mast) {
            FieLdsModel fieLdsModel = model.getFormColumnModel().getFieLdsModel();
            String vmodel = fieLdsModel.getVModel();
            String jnpfKey = fieLdsModel.getConfig().getJnpfKey();
            if (StringUtil.isNotEmpty(vmodel) && !"relationForm".equals(jnpfKey) && !"relationFlow".equals(jnpfKey)) {
                FormDataField formDataField = new FormDataField();
                formDataField.setLabel(fieLdsModel.getConfig().getLabel());
                formDataField.setVModel(fieLdsModel.getVModel());
                formDataFieldList.add(formDataField);
            }
        }
        ListVO<FormDataField> listVO = new ListVO();
        listVO.setList(formDataFieldList);
        return ActionResult.success(listVO);
    }

    /**
     * 列表
     *
     * @return
     */
    @ApiOperation("表单列表")
    @GetMapping("/{id}/FieldDataSelect")
    public ActionResult getFormData(@PathVariable("id") String id) {
        List<FlowTaskEntity> flowTaskList = flowTaskService.getTaskList(id).stream().filter(t -> t.getStatus() == 2).collect(Collectors.toList());
        List<FlowEngineSelectVO> vo = new ArrayList<>();
        for (FlowTaskEntity taskEntity : flowTaskList) {
            FlowEngineSelectVO selectVO = JsonUtil.getJsonToBean(taskEntity, FlowEngineSelectVO.class);
            selectVO.setFullName(taskEntity.getFullName() + "/" + taskEntity.getEnCode());
            vo.add(selectVO);
        }
        ListVO listVO = new ListVO();
        listVO.setList(vo);
        return ActionResult.success(listVO);
    }

    /**
     * 列表
     *
     * @return
     */
    @ApiOperation("获取可见流程引擎列表")
    @GetMapping("/ListAll")
    public ActionResult listAll() {
        List<FlowEngineEntity> data = flowEngineService.getFlowFormList();
        List<FlowEngineListVO> result = JsonUtil.getJsonToList(data, FlowEngineListVO.class);
        ListVO vo = new ListVO();
        vo.setList(result);
        return ActionResult.success(vo);
    }

    /**
     * 获取流程引擎信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取流程引擎信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException, WorkFlowException {
        FlowEngineEntity flowEntity = flowEngineService.getInfo(id);
        FlowEngineInfoVO vo = JsonUtil.getJsonToBeanEx(flowEntity, FlowEngineInfoVO.class);
        ChildNode childNode = JsonUtil.getJsonToBean(flowEntity.getFlowTemplateJson(), ChildNode.class);
        //判断下一节点是否是授权审批人
        int freeApprover = 0;
        if (childNode.getChildNode() != null) {
            String type = childNode.getChildNode().getProperties().getAssigneeType();
            if (String.valueOf(FlowTaskOperatorEnum.FreeApprover.getCode()).equals(type)) {
                freeApprover = 1;
            }
        }
        vo.setFreeApprover(freeApprover);
        return ActionResult.success(vo);
    }

    /**
     * 新建流程设计
     *
     * @return
     */
    @ApiOperation("新建流程引擎")
    @PostMapping
    public ActionResult create(@RequestBody @Valid FlowEngineCrForm flowEngineCrForm) {
        FlowEngineEntity flowEngineEntity = JsonUtil.getJsonToBean(flowEngineCrForm, FlowEngineEntity.class);
        if (flowEngineService.isExistByFullName(flowEngineEntity.getFullName(), flowEngineEntity.getId())) {
            return ActionResult.fail("流程名称不能重复");
        }
        if (flowEngineService.isExistByEnCode(flowEngineEntity.getEnCode(), flowEngineEntity.getId())) {
            return ActionResult.fail("流程编码不能重复");
        }
        ChildNode childNode = JsonUtil.getJsonToBean(flowEngineEntity.getFlowTemplateJson(), ChildNode.class);
        Properties properties = childNode.getProperties();
        List<FlowEngineVisibleEntity> flowVisibleList = new ArrayList<>();
        //可见的用户
        for (String user : properties.getInitiator()) {
            FlowEngineVisibleEntity entity = new FlowEngineVisibleEntity();
            entity.setOperatorId(user);
            entity.setOperatorType("user");
            flowVisibleList.add(entity);
        }
        //可见的部门
        for (String position : properties.getInitiatePos()) {
            FlowEngineVisibleEntity entity = new FlowEngineVisibleEntity();
            entity.setOperatorId(position);
            entity.setOperatorType("position");
            flowVisibleList.add(entity);
        }
        flowEngineService.create(flowEngineEntity, flowVisibleList);
        return ActionResult.success("新建成功");
    }

    /**
     * 更新流程设计
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新流程引擎")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid FlowEngineUpForm flowEngineUpForm) {
        FlowEngineEntity flowEngineEntity = JsonUtil.getJsonToBean(flowEngineUpForm, FlowEngineEntity.class);
        if (flowEngineService.isExistByFullName(flowEngineUpForm.getFullName(), id)) {
            return ActionResult.fail("流程名称不能重复");
        }
        if (flowEngineService.isExistByEnCode(flowEngineUpForm.getEnCode(), id)) {
            return ActionResult.fail("流程编码不能重复");
        }
        ChildNode childNode = JsonUtil.getJsonToBean(flowEngineEntity.getFlowTemplateJson(), ChildNode.class);
        Properties properties = childNode.getProperties();
        List<FlowEngineVisibleEntity> flowVisibleList = new ArrayList<>();
        for (String user : properties.getInitiator()) {
            FlowEngineVisibleEntity entity = new FlowEngineVisibleEntity();
            entity.setOperatorId(user);
            entity.setOperatorType("user");
            flowVisibleList.add(entity);
        }
        for (String position : properties.getInitiatePos()) {
            FlowEngineVisibleEntity entity = new FlowEngineVisibleEntity();
            entity.setOperatorId(position);
            entity.setOperatorType("position");
            flowVisibleList.add(entity);
        }
        boolean flag = flowEngineService.update(id, flowEngineEntity, flowVisibleList);
        if (flag == false) {
            return ActionResult.success("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除流程设计
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除流程引擎")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) throws WorkFlowException {
        FlowEngineEntity entity = flowEngineService.getInfo(id);
        List<FlowTaskEntity> taskNodeList = flowTaskService.getTaskList(entity.getId());
        if (taskNodeList.size() > 0) {
            return ActionResult.fail("引擎在使用，不可删除");
        }
        if (entity != null) {
            flowEngineService.delete(entity);
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }

    /**
     * 复制流程表单
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("复制流程表单")
    @PostMapping("/{id}/Actions/Copy")
    public ActionResult copy(@PathVariable("id") String id) throws WorkFlowException {
        FlowEngineEntity flowEngineEntity = flowEngineService.getInfo(id);
        if (flowEngineEntity != null) {
            ChildNode childNode = JsonUtil.getJsonToBean(flowEngineEntity.getFlowTemplateJson(), ChildNode.class);
            Properties properties = childNode.getProperties();
            List<FlowEngineVisibleEntity> flowVisibleList = new ArrayList<>();
            //可见的用户
            if (properties.getInitiator() != null) {
                for (String user : properties.getInitiator()) {
                    FlowEngineVisibleEntity entity = new FlowEngineVisibleEntity();
                    entity.setOperatorId(user);
                    entity.setOperatorType("user");
                    flowVisibleList.add(entity);
                }
            }
            //可见的部门
            if (properties.getInitiatePos() != null) {
                for (String position : properties.getInitiatePos()) {
                    FlowEngineVisibleEntity entity = new FlowEngineVisibleEntity();
                    entity.setOperatorId(position);
                    entity.setOperatorType("position");
                    flowVisibleList.add(entity);
                }
            }
            long time = System.currentTimeMillis();
            flowEngineEntity.setFullName(flowEngineEntity.getFullName() + "_" + time);
            flowEngineEntity.setEnCode(flowEngineEntity.getEnCode() + "_" + time);
            flowEngineEntity.setCreatorTime(new Date());
            flowEngineService.create(flowEngineEntity, flowVisibleList);
            return ActionResult.success("复制成功");
        }
        return ActionResult.fail("复制失败，数据不存在");
    }

    /**
     * 流程表单状态
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新流程表单状态")
    @PutMapping("/{id}/Actions/State")
    public ActionResult state(@PathVariable("id") String id) throws WorkFlowException {
        FlowEngineEntity entity = flowEngineService.getInfo(id);
        if (entity != null) {
            entity.setEnabledMark("1".equals(String.valueOf(entity.getEnabledMark())) ? 0 : 1);
            flowEngineService.update(id, entity);
            return ActionResult.success("更新表单成功");
        }
        return ActionResult.fail("更新失败，数据不存在");
    }

    /**
     * 发布流程引擎
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("发布流程设计")
    @PostMapping("/Release/{id}")
    public ActionResult release(@PathVariable("id") String id) throws WorkFlowException {
        FlowEngineEntity entity = flowEngineService.getInfo(id);
        if (entity != null) {
            entity.setEnabledMark(1);
            flowEngineService.update(id, entity);
            return ActionResult.success("发布成功");
        }
        return ActionResult.fail("发布失败，数据不存在");
    }

    /**
     * 停止流程引擎
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("停止流程设计")
    @PostMapping("/Stop/{id}")
    public ActionResult stop(@PathVariable("id") String id) throws WorkFlowException {
        FlowEngineEntity entity = flowEngineService.getInfo(id);
        if (entity != null) {
            entity.setEnabledMark(0);
            flowEngineService.update(id, entity);
            return ActionResult.success("停止成功");
        }
        return ActionResult.fail("停止失败，数据不存在");
    }
}
