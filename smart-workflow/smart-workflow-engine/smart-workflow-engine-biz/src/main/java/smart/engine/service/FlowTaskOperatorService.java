package smart.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.engine.entity.FlowTaskOperatorEntity;

import java.util.List;
import java.util.Set;

/**
 * 流程经办
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface FlowTaskOperatorService extends IService<FlowTaskOperatorEntity> {

    /**
     * 列表
     *
     * @param taskId 流程实例Id
     * @return
     */
    List<FlowTaskOperatorEntity> getList(String taskId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    FlowTaskOperatorEntity getInfo(String id);

    /**
     * 信息
     *
     * @param taskId 实例Id
     * @param nodeNo 节点编码
     * @return
     */
    FlowTaskOperatorEntity getInfo(String taskId, String nodeNo);

    /**
     * 删除（根据实例Id）
     *
     * @param taskId 任务主键
     */
    void deleteByTaskId(String taskId);

    /**
     * 删除
     *
     * @param nodeId 节点主键
     */
    void deleteByNodeId(String nodeId);

    /**
     * 创建
     *
     * @param entitys 实体对象
     */
    void create(List<FlowTaskOperatorEntity> entitys);

    /**
     * 更新
     *
     * @param entity 实体对象
     */
    void update(FlowTaskOperatorEntity entity);

    /**
     * 更新会签委托人的审核状态
     *
     * @param taskNodeId 流程节点id
     * @param userId     委托人id
     */
    void update(String taskNodeId, List<String> userId);

    /**
     * 更新流程经办审核状态
     *
     * @param taskNodeId 流程节点id
     * @param type       流程类型 会签、或签
     */
    void update(String taskNodeId, String type);

    /**
     * 更新驳回流程节点
     *
     * @param taskId 流程id
     */
    void update(String taskId);

    /**
     * 经办未审核人员
     *
     * @param nodeCode 当前节点
     * @param taskId   任务id
     * @return
     */
    List<FlowTaskOperatorEntity> press(String[] nodeCode, String taskId);

    /**
     * 驳回的节点之后的节点作废
     *
     * @param taskId
     * @param nodeId
     */
    void updateReject(String taskId, Set<String> nodeId);
}
