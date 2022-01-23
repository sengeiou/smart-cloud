package smart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.util.StringUtil;
import smart.base.ActionResult;
import smart.base.Pagination;
import smart.base.vo.PaginationVO;
import smart.exception.WxErrorException;
import smart.model.mpuser.MPUserListVO;
import smart.model.mpuser.MPUserModel;
import smart.model.mpuser.MPUserRemarkForm;
import smart.service.MPUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 公众号用户
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "公众号用户", description = "MPUser")
@RestController
@RequestMapping("/WeChat/MPUser")
public class MPUserController {

    @Autowired
    private MPUserService mpUserService;

    /**
     * 获取标签用户列表
     *
     * @param type       用户：user-已关注、unblack-黑名单、标签用户列表
     * @param pagination
     * @return
     */
    @ApiOperation("用户列表")
    @GetMapping("/{type}")
    public ActionResult getList(Pagination pagination, @PathVariable("type") String type) throws WxErrorException {
        List<MPUserModel> data;
        if ("user".equals(type)) {
            data = mpUserService.getList(pagination);
        } else if ("unblack".equals(type)) {
            data = mpUserService.GetBlackList(pagination);
        } else {
            data = mpUserService.GetListByTagId(pagination, type);
        }
        if (!StringUtil.isEmpty(pagination.getKeyword())) {
            data = data.stream().filter(t -> t.getNickname().contains(pagination.getKeyword())).collect(Collectors.toList());
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        List<MPUserListVO> listVO = JsonUtil.getJsonToList(data, MPUserListVO.class);
        return ActionResult.page(listVO, paginationVO);
    }

    /**
     * 修改关注者备注信息
     *
     * @return
     */
    @ApiOperation("修改备注")
    @PutMapping("/{openId}/Actions/Remark")
    public ActionResult UpdateRemark(@PathVariable("openId") String openId, @RequestBody MPUserRemarkForm mpUserRemarkForm) throws WxErrorException {
        MPUserModel userModel = new MPUserModel();
        userModel.setOpenid(openId);
        userModel.setRemark(mpUserRemarkForm.getRemark());
        boolean flag = mpUserService.UpdateRemark(userModel);
        if (flag == false) {
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 加入黑名单
     *
     * @param openId 关注者Id
     * @return
     */
    @ApiOperation("加入黑名单")
    @PostMapping("/{openId}/Actions/BlackList")
    public ActionResult AddBatchBlack(@PathVariable("openId") String openId) throws WxErrorException {
        boolean flag = mpUserService.AddBatchBlack(openId);
        if (flag == false) {
            return ActionResult.fail("操作失败，数据不存在");
        }
        return ActionResult.success("操作成功");
    }

    /**
     * 移除黑名单
     *
     * @param openId 关注者Id
     * @return
     */
    @ApiOperation("移除黑名单")
    @DeleteMapping("/{openId}/Actions/BlackList")
    public ActionResult DeleteBatchUnBlack(@PathVariable("openId") String openId) throws WxErrorException {
        boolean flag = mpUserService.DeleteBatchUnBlack(openId);
        if (flag == false) {
            return ActionResult.fail("操作失败，数据不存在");
        }
        return ActionResult.success("操作成功");
    }
}
