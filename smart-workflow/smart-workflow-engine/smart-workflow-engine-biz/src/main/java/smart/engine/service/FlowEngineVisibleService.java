package smart.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.engine.entity.FlowEngineVisibleEntity;

import java.util.List;

/**
 * 流程可见
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
public interface FlowEngineVisibleService extends IService<FlowEngineVisibleEntity> {

    /**
     * 列表
     *
     * @param flowId 流程主键
     * @return
     */
    List<FlowEngineVisibleEntity> getList(String flowId);

    /**
     * 可见流程列表
     *
     * @param userId 用户主键
     * @return
     */
    List<FlowEngineVisibleEntity> getVisibleFlowList(String userId);
}
