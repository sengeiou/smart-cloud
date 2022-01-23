package smart.base.fallback;

import smart.base.AreaApi;
import smart.base.ActionResult;
import smart.base.entity.ProvinceEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 获取行政区划降级处理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Component
public class AreaApiFallback implements AreaApi {
    @Override
    public ActionResult<List<ProvinceEntity>> getList(String id) {
        return null;
    }
}
