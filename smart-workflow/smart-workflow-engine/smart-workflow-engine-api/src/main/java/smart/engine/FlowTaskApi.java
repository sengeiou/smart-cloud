package smart.engine;

import smart.engine.entity.FlowTaskEntity;
import smart.engine.fallback.FlowTaskApiFallback;
import smart.utils.FeignName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * api接口
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 11:55
 */
@FeignClient(name = FeignName.WORKFLOW_SERVER_NAME , fallback = FlowTaskApiFallback.class, path = "/Engine/FlowTask")
public interface FlowTaskApi {
    /**
     * 列表（待我审批）
     *
     * @return
     */
    @GetMapping("/GetWaitList")
    List<FlowTaskEntity> getWaitList();

    /**
     * 列表（我已审批）
     *
     * @return
     */
    @GetMapping("/GetTrialList")
    List<FlowTaskEntity> getTrialList();

    /**
     * 列表（待我审批）
     *
     * @return
     */
    @GetMapping("/GetAllWaitList")
    List<FlowTaskEntity> getAllWaitList();

}
