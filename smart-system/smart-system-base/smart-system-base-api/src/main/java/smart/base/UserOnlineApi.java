package smart.base;

import smart.base.fallback.UserOnlineApiFallback;
import smart.base.model.UserOnlineModel;
import smart.utils.FeignName;
import smart.base.ActionResult;
import smart.base.Page;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
/**
 * 调用在线用户Api
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@FeignClient(name = FeignName.SYSTEM_SERVER_NAME , fallback = UserOnlineApiFallback.class, path = "/Permission/OnlineUser")
public interface UserOnlineApi {


    /**
     * 查询所有在线用户
     *
     * @param page
     * @return
     */
    @GetMapping("/getList")
    ActionResult<List<UserOnlineModel>> getList(Page page);
}
