package smart.engine.fallback;

import smart.engine.FlowTaskApi;
import smart.engine.entity.FlowTaskEntity;
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
public class FlowTaskApiFallback implements FlowTaskApi {
    @Override
    public List<FlowTaskEntity> getWaitList() {
        return null;
    }

    @Override
    public List<FlowTaskEntity> getTrialList() {
        return null;
    }

    @Override
    public List<FlowTaskEntity> getAllWaitList() {
        return null;
    }
}
