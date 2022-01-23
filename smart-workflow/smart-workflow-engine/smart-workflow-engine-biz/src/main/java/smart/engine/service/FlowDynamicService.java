package smart.engine.service;

import smart.engine.entity.FlowTaskEntity;
import smart.engine.model.flowtask.FlowTaskInfoVO;
import smart.exception.DataException;
import smart.exception.WorkFlowException;

import java.sql.SQLException;
import java.util.Map;

/**
 * 在线开发工作流
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 9:19
 */
public interface FlowDynamicService {

    /**
     * 表单信息
     *
     * @param entity 流程任务对象
     * @return
     * @throws WorkFlowException 异常
     * @throws DataException     异常
     * @throws SQLException      异常
     */
    FlowTaskInfoVO info(FlowTaskEntity entity) throws WorkFlowException, DataException, SQLException;

    /**
     * 保存
     *
     * @param id     主键值
     * @param flowId 引擎id
     * @param entity 实体对象
     * @throws WorkFlowException 异常
     * @throws DataException     异常
     * @throws SQLException      异常
     */
    void save(String id, String flowId, String entity) throws WorkFlowException, DataException, SQLException;

    /**
     * 提交
     *
     * @param id         主键值
     * @param flowId     引擎id
     * @param entity     实体对象
     * @param freeUserId 指定用户
     * @throws WorkFlowException 异常
     * @throws DataException     异常
     * @throws SQLException      异常
     */
    void submit(String id, String flowId, String entity, String freeUserId) throws WorkFlowException, DataException, SQLException;

    /**
     * 关联表单
     *
     * @param flowId 引擎id
     * @param id     数据id
     * @return
     * @throws WorkFlowException 异常
     * @throws SQLException      异常
     * @throws DataException     异常
     */
    Map<String, Object> getData(String flowId, String id) throws WorkFlowException, SQLException, DataException;
}
