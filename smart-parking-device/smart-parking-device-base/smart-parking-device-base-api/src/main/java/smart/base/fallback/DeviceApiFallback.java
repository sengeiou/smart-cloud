package smart.base.fallback;

import smart.base.ActionResult;
import smart.base.DeviceApi;
import smart.model.device.DeviceInfoVO;
import org.springframework.stereotype.Component;


/**
 * 获取行政区划降级处理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Component
public class DeviceApiFallback implements DeviceApi {
    @Override
    public ActionResult<DeviceInfoVO> getInfo(String id) {
        return null;
    }
}
