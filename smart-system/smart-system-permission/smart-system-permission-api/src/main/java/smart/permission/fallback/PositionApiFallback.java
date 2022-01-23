package smart.permission.fallback;

import smart.base.ActionResult;
import smart.permission.PositionApi;
import smart.permission.entity.PositionEntity;
import smart.permission.model.position.PositionInfoVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 获取岗位信息Api降级处理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Component
public class PositionApiFallback implements PositionApi {

    @Override
    public ActionResult<List<PositionEntity>> getListAll() {
        return null;
    }

    @Override
    public ActionResult<PositionInfoVO> getInfo(String id) {
        return null;
    }

}
