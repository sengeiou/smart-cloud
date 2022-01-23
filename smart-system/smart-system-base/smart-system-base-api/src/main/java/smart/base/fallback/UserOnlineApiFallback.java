package smart.base.fallback;

import smart.base.UserOnlineApi;
import smart.base.ActionResult;
import smart.base.Page;
import smart.base.model.UserOnlineModel;
import org.springframework.stereotype.Component;

import java.util.List;
/**
 * 调用在线用户Api降级处理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Component
public class UserOnlineApiFallback implements UserOnlineApi {
    @Override
    public ActionResult<List<UserOnlineModel>> getList(Page page) {
        return null;
    }
}
