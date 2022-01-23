package smart.impl;

import com.alibaba.fastjson.JSONObject;
import smart.LoginService;
import smart.base.model.column.ColumnModel;
import smart.base.model.module.ModuleModel;
import smart.base.model.resource.ResourceModel;
import smart.config.ConfigValueUtil;
import smart.emnus.FileTypeEnum;
import smart.entity.TenantEntity;
import smart.file.FileApi;
import smart.model.BaseSystemInfo;
import smart.model.LoginForm;
import smart.base.SysConfigApi;
import smart.base.UserInfo;
import smart.exception.LoginException;
import smart.model.UserMenuModel;
import smart.base.model.button.ButtonModel;
import smart.model.currenuser.*;
import smart.permission.*;
import smart.permission.model.authorize.AuthorizeVO;
import smart.permission.model.user.UserAllModel;
import smart.util.*;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import smart.util.type.IntegerNumber;
import smart.util.type.RequestType;
import smart.util.type.StringNumber;
import smart.util.wxutil.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 登陆业务层实现类
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private Props props;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UsersApi usersApi;
    @Autowired
    private AuthorizeApi authorizeApi;
    @Autowired
    private SysConfigApi sysConfigApi;
    @Autowired
    private FileApi fileApi;

    @Override
    public UserInfo checkTenant(LoginForm loginForm) throws LoginException {
        TenantEntity tenantEntity = new TenantEntity();
        UserInfo userInfo = new UserInfo();
        String tenantId = "";
        //判断是否为多租户
        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {

            /***
             * 注释掉，有坑
             */
           /* String[] tenantAccount = loginForm.getAccount().split("\\@");
            tenantId = tenantAccount.length == 1 ? loginForm.getAccount() : tenantAccount[0];
            loginForm.setAccount(tenantAccount.length == 1 ? "admin" : tenantAccount[1]);
            if (tenantAccount.length > IntegerNumber.TWO || tenantId.length() > IntegerNumber.ELEVEN) {
                throw new LoginException("账号有误，请重新输入！");
            }*/

            tenantId = loginForm.getAccount();
            Map<String, Object> resulList;
            //切换成租户库
//        if("localhost".equals(props.getPortUrl())){
//            Connection connection = JdbcUtil.getConn(dataSourceUtil.getMysqlUserNameTenant(), dataSourceUtil.getMysqlPasswordTenant(), dataSourceUtil.getMysqlUrlTenant());
//            String sql = "select * from base_tenant where F_EnCode ='" + tenantAccount[0] + "'";
//            if (connection != null) {
//                ResultSet result = JdbcUtil.query(connection, sql);
//                resulList = JdbcUtil.convertMap(result);
//                tenantEntity.setDbServiceName(String.valueOf(resulList.get("F_DbServiceName")));
//            } else {
//                throw new LoginException("租户库不存在");
//            }
//        }else{
            JSONObject object;
            if (props.getPortUrl().contains(RequestType.HTTPS)) {
                object = HttpUtil.httpsRequest(props.getPortUrl() + tenantId, "GET", null);
            } else {
                object = HttpUtil.httpRequest(props.getPortUrl() + tenantId, "GET", null);
            }
            if (object == null || StringNumber.FAILCODE.equals(object.get("code").toString())) {
                throw new LoginException("登陆繁忙，请稍后再试");
            }
            if (StringNumber.EXCEPTIONCODE.equals(object.getString("code"))) {
                throw new LoginException(object.getString("msg"));
            }
            resulList = JsonUtil.stringToMap(object.getString("data"));
            if(null == resulList){
                throw new LoginException("租户库不存在");
            }
            String name = resulList.get("java") != null ? String.valueOf(resulList.get("java")) : String.valueOf(resulList.get("dbName"));
            tenantEntity.setDbServiceName(name);
//        }
            userInfo.setTenantId(tenantId);
            userInfo.setTenantDbConnectionString(tenantEntity.getDbServiceName());
        }
        return userInfo;
    }


    /**
     * 获取用户登陆信息
     *
     * @return
     */
    @Override
    public PCUserVO getCurrentUser() {
        BaseSystemInfo baseSystemInfo = sysConfigApi.getSysInfo("1","1");
        UserInfo userInfo = userProvider.get();
        AuthorizeVO authorizeModel = authorizeApi.getEntity(true);

        //获取用户的信息
        UserCommonInfoVO infoVO = JsonUtil.getJsonToBean(userInfo(userInfo, baseSystemInfo), UserCommonInfoVO.class);
        //转成tree的方法
        List<ModuleModel> menuList = authorizeModel.getModuleList().stream().filter(t -> "Web".equals(t.getCategory())).collect(Collectors.toList());
        List<UserMenuModel> menu = JsonUtil.getJsonToList(menuList, UserMenuModel.class);
        //外层菜单排序
        menu = menu.stream().sorted(Comparator.comparing(UserMenuModel::getSortCode)).collect(Collectors.toList());
        List<SumTree<UserMenuModel>> menus = TreeDotUtils.convertListToTreeDot(menu);
        //返回前台tree的list
        List<MenuTreeVO> list = JsonUtil.getJsonToList(menus, MenuTreeVO.class);
        //保证内层排序
        list = list.stream().sorted(Comparator.comparing(MenuTreeVO::getSortCode)).collect(Collectors.toList());
        //获取全部用户
        List<UserAllModel> userList = usersApi.getAll().getData();
        UserAllModel userAllVO = userList.stream().filter(t -> t.getId().equals(infoVO.getUserId())).findFirst().orElse(new UserAllModel());
        //赋值门户id
        infoVO.setPortalId(userAllVO.getPortalId());
        //赋值部门
        infoVO.setDepartmentId(userAllVO.getOrganizeId());
        infoVO.setDepartmentName(userAllVO.getDepartment());
        //赋值公司
        infoVO.setOrganizeName(userAllVO.getOrganize());
        //赋值岗位
        String[] positionId = userAllVO.getPositionId() != null ? userAllVO.getPositionId().split(",") : new String[]{};
        String[] positionName = userAllVO.getPositionName() != null ? userAllVO.getPositionName().split(",") : new String[]{};
        List<UserPositionVO> positionVO = new ArrayList<>();
        //判断数组长度是否越界
        if (positionId.length != positionName.length) {
            return null;
        }
        for (int i = 0; i < positionId.length; i++) {
            UserPositionVO userPositionVO = new UserPositionVO();
            userPositionVO.setId(positionId[i]);
            userPositionVO.setName(positionName[i]);
            positionVO.add(userPositionVO);
        }
        List<PermissionModel> models = new ArrayList<>();
        for (ModuleModel moduleModel : menuList) {
            PermissionModel model = new PermissionModel();
            model.setModelId(moduleModel.getId());
            model.setModuleName(moduleModel.getFullName());
            List<ButtonModel> buttonModels = authorizeModel.getButtonList().stream().filter(t -> moduleModel.getId().equals(t.getModuleId())).collect(Collectors.toList());
            List<ColumnModel> columnModels = authorizeModel.getColumnList().stream().filter(t -> moduleModel.getId().equals(t.getModuleId())).collect(Collectors.toList());
            List<ResourceModel> resourceModels = authorizeModel.getResourceList().stream().filter(t -> moduleModel.getId().equals(t.getModuleId())).collect(Collectors.toList());
            model.setButton(JsonUtil.getJsonToList(buttonModels, PermissionVO.class));
            model.setColumn(JsonUtil.getJsonToList(columnModels, PermissionVO.class));
            model.setResource(JsonUtil.getJsonToList(resourceModels, PermissionVO.class));
            if (moduleModel.getType() != 1) {
                models.add(model);
            }
        }
        Iterator<MenuTreeVO> iterator = list.iterator();

        while (iterator.hasNext()) {
            MenuTreeVO menuTreeVO = iterator.next();
            if (!"-1".equals(menuTreeVO.getParentId())) {
                iterator.remove();
            }
        }

        infoVO.setPositionIds(positionVO);
        PCUserVO userVO = new PCUserVO(list, models, infoVO);
        userVO.setPermissionList(models);
        userVO.getUserInfo().setHeadIcon(UploaderUtil.uploaderImg(userInfo.getUserIcon()));
        return userVO;
    }



    /**
     * 登录信息
     *
     * @param userInfo   回话信息
     * @param systemInfo 系统信息
     * @return
     */
    private Map<String, Object> userInfo(UserInfo userInfo, BaseSystemInfo systemInfo) {
        Map<String, Object> dictionary = new HashMap<>(16);
        dictionary.put("userId", userInfo.getUserId());
        dictionary.put("userAccount", userInfo.getUserAccount());
        dictionary.put("userName", userInfo.getUserName());
        dictionary.put("icon", userInfo.getUserIcon());
        dictionary.put("gender", userInfo.getUserGender());
        dictionary.put("organizeId", userInfo.getOrganizeId());
        dictionary.put("prevLogin", systemInfo.getLastLoginTimeSwitch() == 1 ? 1 : 0);
        dictionary.put("prevLoginTime", userInfo.getPrevLoginTime());
        dictionary.put("prevLoginIPAddress", userInfo.getPrevLoginIpAddress());
        dictionary.put("prevLoginIPAddressName", userInfo.getPrevLoginIpAddressName());
        dictionary.put("serviceDirectory", fileApi.getPath(FileTypeEnum.SERVICEDIRECTORY));
        dictionary.put("webDirectory", fileApi.getPath(FileTypeEnum.WEBDIRECTORY));
        return dictionary;
    }

}
