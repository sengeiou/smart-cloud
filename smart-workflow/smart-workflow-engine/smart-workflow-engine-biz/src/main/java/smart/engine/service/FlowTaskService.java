package smart.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.engine.entity.FlowTaskEntity;
import smart.engine.entity.FlowTaskNodeEntity;
import smart.engine.entity.FlowTaskOperatorEntity;
import smart.engine.entity.FlowTaskOperatorRecordEntity;
import smart.engine.model.FlowHandleModel;
import smart.engine.model.flowtask.PaginationFlowTask;
import smart.exception.WorkFlowException;

import java.util.List;

/**
 * 流程任务
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface FlowTaskService extends IService<FlowTaskEntity> {

    /**
     * 列表（流程监控）
     *
     * @param paginationFlowTask
     * @return
     */
    List<FlowTaskEntity> getMonitorList(PaginationFlowTask paginationFlowTask);

    /**
     * 列表（我发起的）
     *
     * @param paginationFlowTask
     * @return
     */
    List<FlowTaskEntity> getLaunchList(PaginationFlowTask paginationFlowTask);

    /**
     * 列表（待我审批）
     *
     * @param paginationFlowTask
     * @return
     */
    List<FlowTaskEntity> getWaitList(PaginationFlowTask paginationFlowTask);

    /**
     * 列表（我已审批）
     *
     * @return
     */
    List<FlowTaskEntity> getTrialList();


    /**
     * 列表（待我审批）
     *
     * @return
     */
    List<FlowTaskEntity> getWaitList();


    /**
     * 列表（待我审批）
     *
     * @return
     */
    List<FlowTaskEntity> getAllWaitList();

    /**
     * 列表（我已审批）
     *
     * @param paginationFlowTask
     * @return
     */
    List<FlowTaskEntity> getTrialList(PaginationFlowTask paginationFlowTask);


    /**
     * 列表（抄送我的）
     *
     * @param paginationFlowTask
     * @return
     */
    List<FlowTaskEntity> getCirculateList(PaginationFlowTask paginationFlowTask);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    FlowTaskEntity getInfo(String id) throws WorkFlowException;

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    FlowTaskEntity getInfoSubmit(String id);

    /**
     * 删除
     *
     * @param entity 实体对象
     * @throws WorkFlowException 异常
     */
    void delete(FlowTaskEntity entity) throws WorkFlowException;

    /**
     * 批量删除流程
     *
     * @param ids
     */
    void delete(String[] ids);

    /**
     * 保存
     *
     * @param id         主键值
     * @param flowId     流程主键
     * @param processId  实例Id
     * @param flowTitle  流程标题
     * @param flowUrgent 紧急程度
     * @param billNo     流水号
     * @throws WorkFlowException 异常
     */
    void save(String id, String flowId, String processId, String flowTitle, int flowUrgent, String billNo) throws WorkFlowException;

    /**
     * 保存
     *
     * @param id         主键值
     * @param flowId     流程主键
     * @param processId  实例Id
     * @param flowTitle  流程标题
     * @param flowUrgent 紧急程度
     * @param billNo     流水号
     * @param formEntity 表单实体
     * @throws WorkFlowException 异常
     */
    void save(String id, String flowId, String processId, String flowTitle, int flowUrgent, String billNo, String formEntity) throws WorkFlowException;

    /**
     * 提交
     *
     * @param id                 主键值
     * @param flowId             流程主键
     * @param processId          流程主键
     * @param flowTitle          流程标题
     * @param flowUrgent         紧急程度
     * @param billNo             流水号
     * @param formEntity         表单对象
     * @param freeApproverUserId 授权审批人
     * @throws WorkFlowException 异常
     */
    void submit(String id, String flowId, String processId, String flowTitle, int flowUrgent, String billNo, Object formEntity, String freeApproverUserId) throws WorkFlowException;

    /**
     * 提交
     *
     * @param id         主键值
     * @param flowId     流程主键
     * @param processId  流程主键
     * @param flowTitle  流程标题
     * @param flowUrgent 紧急程度
     * @param billNo     流水号
     * @param formEntity 表单对象
     * @throws WorkFlowException 异常
     */
    void submit(String id, String flowId, String processId, String flowTitle, int flowUrgent, String billNo, Object formEntity) throws WorkFlowException;

    /**
     * 提交
     *
     * @param processId  业务主键
     * @param flowId     流程主键
     * @param flowTitle  流程标题
     * @param flowUrgent 紧急程度
     * @param billNo     流水号
     * @param formEntity 表单对象
     * @throws WorkFlowException 异常
     */
    void submit(String processId, String flowId, String flowTitle, int flowUrgent, String billNo, Object formEntity) throws WorkFlowException;

    /**
     * 提交
     *
     * @param processId          业务主键
     * @param flowId             流程主键
     * @param flowTitle          流程标题
     * @param flowUrgent         紧急程度
     * @param billNo             流水号
     * @param formEntity         表单对象
     * @param freeApproverUserId 授权审批人
     * @throws WorkFlowException 异常
     */
    void submit(String processId, String flowId, String flowTitle, int flowUrgent, String billNo, Object formEntity, String freeApproverUserId) throws WorkFlowException;

    /**
     * 撤回
     *
     * @param flowTaskEntity  流程实例
     * @param flowHandleModel 经办记录
     */
    void revoke(FlowTaskEntity flowTaskEntity, FlowHandleModel flowHandleModel);

    /**
     * 审核
     *
     * @param flowTaskEntity         流程实例
     * @param flowTaskOperatorEntity 流程经办
     * @param flowHandleModel        流程经办
     * @throws WorkFlowException 异常
     */
    void audit(FlowTaskEntity flowTaskEntity, FlowTaskOperatorEntity flowTaskOperatorEntity, FlowHandleModel flowHandleModel) throws WorkFlowException;

    /**
     * 驳回
     *
     * @param flowTaskEntity         流程实例
     * @param flowTaskOperatorEntity 流程经办
     * @param flowHandleModel        经办记录
     * @throws WorkFlowException 异常
     */
    void reject(FlowTaskEntity flowTaskEntity, FlowTaskOperatorEntity flowTaskOperatorEntity, FlowHandleModel flowHandleModel) throws WorkFlowException;

    /**
     * 召回（已办事宜）
     *
     * @param flowTaskEntity               流程实例
     * @param flowTaskNodeList             所有流程节点
     * @param flowTaskOperatorRecordEntity 经办记录
     * @param flowHandleModel              撤回原因
     * @throws WorkFlowException 异常
     */
    void recall(FlowTaskEntity flowTaskEntity, List<FlowTaskNodeEntity> flowTaskNodeList, FlowTaskOperatorRecordEntity flowTaskOperatorRecordEntity, FlowHandleModel flowHandleModel) throws WorkFlowException;

    /**
     * 作废（流程监控）
     *
     * @param flowTaskEntity  流程实例
     * @param flowHandleModel 经办记录
     */
    void cancel(FlowTaskEntity flowTaskEntity, FlowHandleModel flowHandleModel);

    /**
     * 通过流程引擎id获取流程列表
     *
     * @param id
     * @return
     */
    List<FlowTaskEntity> getTaskList(String id);

    /**
     * 查询订单状态
     *
     * @param id
     * @return
     */
    List<FlowTaskEntity> getOrderStaList(List<String> id);
}
