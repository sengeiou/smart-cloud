package smart.base.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.base.Page;
import smart.base.model.UserOnlineModel;
import smart.base.model.UserOnlineVO;
import smart.base.service.UserOnlineService;
import smart.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在线用户
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "在线用户", value = "Online")
@RestController
@RequestMapping("/Base/OnlineUser")
public class OnlineUserController {

    @Autowired
    private UserOnlineService userOnlineService;

    /**
     * 列表
     *
     * @param page
     * @return
     */
    @ApiOperation("获取在线用户列表")
    @GetMapping
    public ActionResult list(Page page) {
        List<UserOnlineModel> data = userOnlineService.getList(page);
        List<UserOnlineVO> vo= JsonUtil.getJsonToList(data,UserOnlineVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 注销
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("强制下线")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        userOnlineService.delete(id);
        return ActionResult.success("操作成功");
    }

    /**
     * 获取所有在线用户
     * @param page
     * @return
     */
    @GetMapping("/getList")
    public ActionResult getList(Page page) {
        List<UserOnlineModel> list = userOnlineService.getList(page);
        return ActionResult.success(list);
    }
}
