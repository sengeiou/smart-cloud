package smart.engine.fallback;

import smart.engine.FlowDelegateApi;
import smart.engine.entity.FlowDelegateEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * api接口
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 11:55
 */
@Component
public class FlowDelegateApiFallback implements FlowDelegateApi {
    @Override
    public List<FlowDelegateEntity> getList() {
        return null;
    }
}
