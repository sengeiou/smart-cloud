package smart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ModuleApi;
import smart.base.entity.ModuleEntity;
import smart.engine.FlowEngineApi;
import smart.engine.model.flowengine.FlowEngineListVO;
import smart.model.app.AppFlowFormModel;
import smart.model.app.AppInfoModel;
import smart.model.app.AppMenuModel;
import smart.model.app.AppUserVO;
import smart.base.ActionResult;
import smart.base.UserInfo;
import smart.permission.UsersApi;
import smart.permission.entity.UserEntity;
import smart.permission.model.user.UserAllModel;
import smart.util.JsonUtil;
import smart.util.StringUtil;
import smart.util.UserProvider;
import smart.util.type.StringNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 * APP登陆接口
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Api(tags = "App登陆数据", value = "Data")
@RestController
public class AppController {

    @Autowired
    private UsersApi usersApi;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private ModuleApi moduleApi;
    @Autowired
    private FlowEngineApi flowEngineApi;

    /**
     * 主页数据
     *
     * @return
     */
    @ApiOperation("主页数据")
    @GetMapping("/App/CurrentUser")
    public ActionResult data() {
        UserInfo userInfo = userProvider.get();
        AppUserVO userVO = new AppUserVO();
        UserEntity userEntity = usersApi.getInfoById(userInfo.getUserId());
        List<UserAllModel> userList = usersApi.getAll().getData();
        UserAllModel model = userList.stream().filter(t -> t.getId().equals(userEntity.getId())).findFirst().get();
        AppInfoModel infoVO = JsonUtil.getJsonToBean(model, AppInfoModel.class);
        infoVO.setDepartmentName(model.getDepartment());
        infoVO.setOrganizeName(model.getOrganize());
        infoVO.setMobilePhone(StringUtil.isNotEmpty(userEntity.getMobilePhone()) ? Long.parseLong(userEntity.getMobilePhone()) : null);
        infoVO.setBirthday(userEntity.getBirthday() != null ? userEntity.getBirthday().getTime() : 0);
        userVO.setUserInfo(infoVO);
        //工作区
        List<FlowEngineListVO> data = flowEngineApi.listAll().getData().getList();
        List<AppFlowFormModel> flowListVO = JsonUtil.getJsonToList(data, AppFlowFormModel.class);
        userVO.setFlowFormList(flowListVO);
        //应用菜单
        List<ModuleEntity> menuList = moduleApi.getList();
        //app菜单子节点
        List<ModuleEntity> childMenuList = menuList.stream().filter(t -> "App".equals(t.getCategory()) && t.getType() != 1 && t.getEnabledMark() == 1).collect(Collectors.toList());
        //app顶级菜单
        ModuleEntity appFirst = menuList.stream().filter(t -> "App".equals(t.getCategory()) && t.getType() == 1).findFirst().get();
        List<AppMenuModel> menu = JsonUtil.getJsonToList(childMenuList, AppMenuModel.class);
        userVO.setMenuList(menu);
        if (StringNumber.ZERO.equals(appFirst.getEnabledMark())){
            userVO.setMenuList(new ArrayList<>());
        }
        return ActionResult.success(userVO);
    }

}
