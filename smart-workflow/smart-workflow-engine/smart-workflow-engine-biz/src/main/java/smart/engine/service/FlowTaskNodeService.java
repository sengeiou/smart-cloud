package smart.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.engine.entity.FlowTaskNodeEntity;

import java.util.List;

/**
 * 流程节点
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface FlowTaskNodeService extends IService<FlowTaskNodeEntity> {

    /**
     * 列表
     *
     * @return
     */
    List<FlowTaskNodeEntity> getListAll();

    /**
     * 列表
     *
     * @param taskId 任务主键
     * @return
     */
    List<FlowTaskNodeEntity> getList(String taskId);

    /**
     * 信息
     *
     * @param id 主键值
     * @return
     */
    FlowTaskNodeEntity getInfo(String id);

    /**
     * 删除（根据实例Id）
     *
     * @param taskId 任务主键
     */
    void deleteByTaskId(String taskId);

    /**
     * 创建
     *
     * @param entitys 实体对象
     */
    void create(List<FlowTaskNodeEntity> entitys);

    /**
     * 更新
     *
     * @param entity 实体对象
     */
    void update(FlowTaskNodeEntity entity);

    /**
     * 更新驳回开始流程节点
     *
     * @param taskId 流程id
     */
    void update(String taskId);
}
