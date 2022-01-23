package smart.engine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import smart.engine.entity.FlowTaskOperatorEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 流程经办
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface FlowTaskOperatorMapper extends BaseMapper<FlowTaskOperatorEntity> {
    /**
     * 更新流程经办审核状态
     *
     * @param map 参数
     */
    void updateFixedapprover(@Param("map") Map<String, Object> map);

    /**
     * 更新会签委托人的审核状态
     *
     * @param taskNodeId 节点id
     * @param userId     用户id
     */
    void updateDelegateUser(@Param("taskNodeId") String taskNodeId, @Param("userId") String userId);

    /**
     * 更新驳回流程节点
     *
     * @param taskId 任务id
     */
    void updateState(@Param("taskId") String taskId);

    /**
     * 驳回的节点之后的节点作废
     *
     * @param taskId 任务id
     * @param nodeId 节点id
     */
    void updateReject(@Param("taskId") String taskId, @Param("nodeId") String nodeId);
}
