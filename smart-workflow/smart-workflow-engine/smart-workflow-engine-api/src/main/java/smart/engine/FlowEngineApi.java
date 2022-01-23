package smart.engine;

import smart.engine.fallback.FlowEngineApiFallback;
import smart.engine.model.flowengine.FlowEngineListVO;
import smart.utils.FeignName;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * api接口
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 11:55
 */
@FeignClient(name = FeignName.WORKFLOW_SERVER_NAME , fallback = FlowEngineApiFallback.class, path = "/Engine/FlowEngine")
public interface FlowEngineApi {
    /**
     * 列表
     *
     * @return
     */
    @GetMapping("/ListAll")
    ActionResult<ListVO<FlowEngineListVO>> listAll();

}
