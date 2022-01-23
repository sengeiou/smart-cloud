package smart.base.fallback;

import smart.base.DataInterFaceApi;
import smart.base.ActionResult;
import org.springframework.stereotype.Component;
/**
 * 调用数据接口Api降级处理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Component
public class DataInterFaceApiFallback implements DataInterFaceApi {

    @Override
    public ActionResult infoToId(String id) {
        return null;
    }
}
