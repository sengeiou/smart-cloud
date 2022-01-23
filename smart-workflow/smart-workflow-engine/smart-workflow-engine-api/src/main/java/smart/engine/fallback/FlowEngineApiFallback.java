package smart.engine.fallback;

import smart.engine.FlowEngineApi;
import smart.utils.FeignName;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.engine.model.flowengine.FlowEngineListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * api接口
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 11:55
 */
@Component
@Slf4j
public class FlowEngineApiFallback implements FlowEngineApi {
    @Override
    public ActionResult<ListVO<FlowEngineListVO>> listAll() {
        log.error(FeignName.WORKFLOW_SERVER_NAME+"服务未启动");
        return null;
    }
}
