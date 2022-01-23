package smart.engine;

import smart.engine.entity.FlowDelegateEntity;
import smart.engine.fallback.FlowDelegateApiFallback;
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
@FeignClient(name = FeignName.WORKFLOW_SERVER_NAME , fallback = FlowDelegateApiFallback.class, path = "/Engine/FlowDelegate")
public interface FlowDelegateApi {
    /**
     * 列表
     *
     * @return
     */
    @GetMapping("/getList")
    List<FlowDelegateEntity> getList();

}
