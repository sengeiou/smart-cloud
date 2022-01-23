package smart.portal.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.EmailApi;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.vo.ListVO;
import smart.engine.FlowDelegateApi;
import smart.engine.FlowTaskApi;
import smart.message.NoticeApi;
import smart.portal.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 主页控制器
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "主页控制器", description = "Home")
@RestController
@RequestMapping("/Dashboard")
public class DashboardController {
    @Autowired
    private FlowTaskApi flowTaskApi;

    @Autowired
    private FlowDelegateApi flowDelegateApi;

    @Autowired
    private NoticeApi noticeApi;

    @Autowired
    private EmailApi emailApi;

    /**
     * 获取我的待办
     *
     * @return
     */
    @ApiOperation("获取我的待办")
    @GetMapping("/FlowTodoCount")
    public ActionResult getFlowTodoCount() {
        FlowTodoCountVO vo = new FlowTodoCountVO();
        vo.setToBeReviewed(flowTaskApi.getWaitList().size());
        vo.setEntrust(flowDelegateApi.getList().size());
        vo.setFlowDone(flowTaskApi.getTrialList().size());
        return ActionResult.success(vo);
    }

    /**
     * 获取通知公告
     *
     * @return
     */
    @ApiOperation("获取通知公告")
    @GetMapping("/Notice")
    public ActionResult getNotice() {
        List<NoticeVO> list = JsonUtil.getJsonToList(noticeApi.getNoticeList(), NoticeVO.class);
        ListVO<NoticeVO> voList = new ListVO();
        voList.setList(list);
        return ActionResult.success(voList);
    }

    /**
     * 获取未读邮件
     *
     * @return
     */
    @ApiOperation("获取未读邮件")
    @GetMapping("/Email")
    public ActionResult getEmail() {
        List<EmailVO> list = JsonUtil.getJsonToList(emailApi.getReceiveList(), EmailVO.class);
        ListVO<EmailVO> voList = new ListVO();
        voList.setList(list);
        return ActionResult.success(voList);
    }

    /**
     * 获取待办事项
     *
     * @return
     */
    @ApiOperation("获取待办事项")
    @GetMapping("/FlowTodo")
    public ActionResult getFlowTodo() {
        List<FlowTodoVO> list = JsonUtil.getJsonToList(flowTaskApi.getAllWaitList(), FlowTodoVO.class);
        ListVO<FlowTodoVO> voList = new ListVO();
        voList.setList(list);
        return ActionResult.success(voList);
    }

    /**
     * 获取我的待办事项
     *
     * @return
     */
    @ApiOperation("获取我的待办事项")
    @GetMapping("/MyFlowTodo")
    public ActionResult getMyFlowTodo() {
        List<MyFlowTodoVO> list = JsonUtil.getJsonToList(flowTaskApi.getWaitList(), MyFlowTodoVO.class);
        ListVO<MyFlowTodoVO> voList = new ListVO();
        voList.setList(list);
        return ActionResult.success(voList);
    }
}
