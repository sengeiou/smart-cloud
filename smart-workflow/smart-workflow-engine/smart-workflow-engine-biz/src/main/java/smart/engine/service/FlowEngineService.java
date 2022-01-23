package smart.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.engine.entity.FlowEngineEntity;
import smart.engine.entity.FlowEngineVisibleEntity;
import smart.engine.entity.FlowTaskNodeEntity;
import smart.engine.model.flowengine.PaginationFlowEngine;
import smart.exception.WorkFlowException;

import java.util.List;

/**
 * 流程引擎
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface FlowEngineService extends IService<FlowEngineEntity> {

    /**
     * 列表
     * @param pagination 分页
     * @return
     */
    List<FlowEngineEntity> getList(PaginationFlowEngine pagination);

    /**
     * 列表
     *
     * @return
     */
    List<FlowEngineEntity> getList();

    /**
     * 列表
     *
     * @return
     */
    List<FlowEngineEntity> getFlowFormList();

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     * @throws WorkFlowException 异常
     */
    FlowEngineEntity getInfo(String id) throws WorkFlowException;

    /**
     * 信息
     *
     * @param enCode 流程编码
     * @return
     * @throws WorkFlowException 异常
     */
    FlowEngineEntity getInfoByEnCode(String enCode) throws WorkFlowException;

    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     * @return
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 验证编码
     *
     * @param enCode 编码
     * @param id     主键值
     * @return
     */
    boolean isExistByEnCode(String enCode, String id);

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    void delete(FlowEngineEntity entity);

    /**
     * 创建
     *
     * @param entity      实体对象
     * @param visibleList 可见范围
     */
    void create(FlowEngineEntity entity, List<FlowEngineVisibleEntity> visibleList);

    /**
     * 更新
     *
     * @param id          主键值
     * @param entity      实体对象
     * @param visibleList 可见范围
     * @return
     */
    boolean update(String id, FlowEngineEntity entity, List<FlowEngineVisibleEntity> visibleList);

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     */
    void update(String id, FlowEngineEntity entity);

    /**
     * 上移
     *
     * @param id 主键值
     * @return
     */
    boolean first(String id);

    /**
     * 下移
     *
     * @param id 主键值
     * @return
     */
    boolean next(String id);

    /**
     * 获取流程节点
     *
     * @param stepId           当前节点
     * @param flowTaskNodeList 全部节点
     * @return
     */
    long getFlowNodeList(String stepId, List<FlowTaskNodeEntity> flowTaskNodeList);
}
