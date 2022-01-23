package smart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.QYDepartmentEntity;
import smart.QYUserEntity;
import smart.base.ActionResult;
import smart.base.Page;
import smart.base.vo.PaginationVO;
import smart.exception.WxErrorException;
import smart.model.qyuser.QYUserListVO;
import smart.permission.UsersApi;
import smart.permission.entity.UserEntity;
import smart.service.QYDepartmentService;
import smart.service.QYUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 企业号用户
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Api(tags = "企业号用户", value = "QYUser")
@RestController
@RequestMapping("/WeChat/QYUser")
public class QYUserController {

    @Autowired
    private QYUserService qyUserService;
    @Autowired
    private QYDepartmentService qyDepartmentService;
    @Autowired
    private UsersApi usersApi;

    /**
     * 列表
     *
     * @return
     */
    @ApiOperation("用户列表")
    @GetMapping
    public ActionResult list(Page page) {
        List<QYUserEntity> data = qyUserService.getList(page);
        List<QYUserListVO> listVO = new ArrayList<>();
        for (QYUserEntity entity : data) {
            QYUserListVO vo = JsonUtil.getJsonToBean(entity, QYUserListVO.class);
            vo.setNickName(entity.getRealName());
            vo.setDepartment(entity.getOrganizeId());
            vo.setPosition(entity.getPositionId());
            listVO.add(vo);
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(page, PaginationVO.class);
        return ActionResult.page(listVO,paginationVO);
    }

    /**
     * 同步用户
     *
     * @return
     */
    @ApiOperation("同步用户")
    @PostMapping("/Actions/Synchronization")
    public ActionResult synchronization() throws WxErrorException {
        List<QYDepartmentEntity> department = qyDepartmentService.getSyncList();
        if (department.size() == 0) {
            return ActionResult.fail("请先同步部门!");
        } else {
            List<UserEntity> users = usersApi.getUserList();
            qyUserService.synchronization(users,department);
            return ActionResult.success("同步成功");
        }
    }
}
