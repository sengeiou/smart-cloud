package smart.controller;

import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.LoginService;
import smart.base.UserInfo;
import smart.config.ConfigValueUtil;
import smart.util.data.DataSourceContextHolder;
import smart.util.JsonUtil;
import smart.util.Md5Util;
import smart.permission.UsersApi;
import smart.model.LoginForm;
import smart.model.LoginVO;
import smart.model.currenuser.PCUserVO;
import smart.base.ActionResult;
import smart.exception.LoginException;
import smart.permission.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * 登录控制器
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Slf4j
@RestController
@Api(tags = "登陆数据", value = "Login")
@GlobalTransactional
public class LoginController {

    @Autowired
    private UsersApi usersApi;
    @Autowired
    private TokenEndpoint tokenEndpoint;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ConfigValueUtil configValueUtil;

    @ApiOperation("登陆")
    @PostMapping("/Login")
    public ActionResult Login(Principal principal, @RequestParam Map<String, String> parameters, @RequestBody LoginForm loginForm) throws LoginException {
        UserInfo userInfo = null;
        UserEntity userEntity = null;
        //判断是否为多租户
        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
            userInfo = loginService.checkTenant(loginForm);
            //设置租户
            DataSourceContextHolder.setDatasource(userInfo.getTenantId(), userInfo.getTenantDbConnectionString());
            userEntity= usersApi.isExistUser(loginForm.getAccount().trim(), loginForm.getPassword().trim(),userInfo.getTenantId(),userInfo.getTenantDbConnectionString());
        }else {
            userEntity= usersApi.isExistUser(loginForm.getAccount().trim(), loginForm.getPassword().trim(),"1","1");
        }
        //验证账号密码
        Map<String, String> map = JsonUtil.entityToMaps(loginForm);
        map.putAll(parameters);
        map.put("username", loginForm.getAccount());
        map.put("userId",userEntity.getId());
        System.out.println("databases="+DataSourceContextHolder.getDatasourceName());
        OAuth2AccessToken oAuth2AccessToken = null;
        try {
            oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, map).getBody();
        } catch (Exception e) {
            throw new LoginException("登录失败");
        }
        //获取主题
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(oAuth2AccessToken.getTokenType() + " " + oAuth2AccessToken.getValue());
        loginVO.setTheme(userEntity.getTheme() == null ? "classic" : userEntity.getTheme());
        return ActionResult.success(loginVO);
    }

    /**
     * 验证密码
     *
     * @return
     */
    @ApiOperation("锁屏解锁登录")
    @PostMapping("/LockScreen")
    public ActionResult lockScreen(@RequestBody LoginForm loginForm) throws LoginException {
        UserEntity userEntity = null;
        //判断是否为多租户
        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())){
            userEntity = usersApi.checkUser(loginForm.getAccount(), DataSourceContextHolder.getDatasourceId(), DataSourceContextHolder.getDatasourceName());
        }else {
            userEntity = usersApi.checkUser(loginForm.getAccount(), "1", "1");
        }
        if (!Md5Util.getStringMd5(loginForm.getPassword().toLowerCase() + userEntity.getSecretkey().toLowerCase()).equals(userEntity.getPassword())) {
            throw new LoginException("账户或密码错误，请重新输入。");
        }
        return ActionResult.success("验证成功");
    }

    /**
     * 登录注销
     *
     * @return
     */
    @ApiOperation("退出")
    @GetMapping("/Logout")
    public ActionResult logout() {
        return ActionResult.success("注销成功");
    }

    /**
     * 获取用户登录信息
     *
     * @return
     */
    @ApiOperation("获取用户登录信息")
    @GetMapping("/CurrentUser")
    public ActionResult data() throws LoginException {
        PCUserVO pcUserVO = loginService.getCurrentUser();
        if (pcUserVO==null){
            throw new LoginException("账户异常");
        }
        return ActionResult.success(pcUserVO);
    }

    /**
     * 主页数据
     *
     * @return
     */
//    @ApiOperation("主页数据")
//    @GetMapping("/APPData")
//    public ActionResult GetAPPData() {
//        UserInfo userInfo = userProvider.get();
//        UserEntity userEntity = userService.getInfo(userInfo.getUserId());
//        AuthorizeVO authorizeModel = authorizeService.GetAuthorize(true);
//        Map<String, Object> map = new HashMap<>();
//        map.put("userId", userEntity.getFId());
//        map.put("userAccount", userEntity.getFAccount());
//        map.put("nickName", userEntity.getFNickName());
//        map.put("realName", userEntity.getFRealName());
//        map.put("userIcon", userEntity.getFHeadIcon());
//        map.put("roleId", userEntity.getFRoleId());
//        map.put("positionId", userEntity.getFPositionId());
//        map.put("managerId", userEntity.getFManagerId());
//        map.put("organizeId", userEntity.getFOrganizeId());
//        map.put("email", userEntity.getFEmail());
//        map.put("userGender", userEntity.getFGender());
//        map.put("birthday", userEntity.getFBirthday());
//        map.put("mobilePhone", userEntity.getFMobilePhone());
//        Map<String, Object> object = new HashMap<>();
//        object.put("userProvider", map);
//        //工作区
//        object.put("flowForm", JSONUtil.ListToJSONField(flowEngineService.GetFlowFormList()));
//        //应用菜单
//        object.put("applyMenu", JSONUtil.ListToJSONField(authorizeModel.getMenuList().stream().filter(t -> "App".equals(t.getCategory())).collect(Collectors.toList())));
//        return ActionResult.success(object);
//    }

    /**
     * 请求记录（打开功能）
     *
     * @param moduleId   功能主键
     * @param moduleName 功能名称
     * @return
     */
//    @ApiOperation("请求记录")
//    @GetMapping("/RequestLog/{moduleId}/{moduleName}")
//    public ActionResult RequestLog(@PathVariable("moduleId") String moduleId, @PathVariable("moduleName") String moduleName) {
//        UserInfo userInfo = userProvider.get();
//        LogEntity entity = new LogEntity();
//        entity.setId(RandomUtil.uuId());
//        entity.setCategory(LogSortEnum.Visit.getCode());
//        entity.setUserId(userInfo.getUserId());
//        entity.setUserName(userInfo.getUserName() + "/" + userInfo.getUserAccount());
//        entity.setModuleId(moduleId);
//        entity.setRequestURL(ServletUtil.getServletPath());
//        entity.setRequestMethod(ServletUtil.getRequest().getMethod());
//        entity.setIPAddress(IPUtil.getIpAddr());
//        entity.setModuleName(moduleName);
//        entity.setPlatForm(ServletUtil.getUserAgent());
//        logService.save(entity);
//        logFeignService.
//        return ActionResult.success("请求成功");
//    }


    /**
     * 常用菜单
     *
     * @param userInfo
     * @return
     */
//    private Object CommonMenuList(UserInfo userInfo) {
//        String commonMenuId = userService.getOne(new QueryWrapper<UserEntity>().lambda().eq(UserEntity::getId, userInfo.getUserId())).getCommonMenu();
//        return commonMenuId == null ? new String[]{} : commonMenuId.split(",");
//    }

    /**
     * 获取Url权限
     */
//    private Object UrlList(AuthorizeVO authorizeInfo) {
//        Map<String, Object> urlData = new HashMap<>();
//        List<ModuleModel> moduleList = authorizeInfo.getModuleList().stream().filter(t -> t.getType() == 2).collect(Collectors.toList());
//        for (ModuleModel moduleModel : moduleList) {
//            if (!StringUtils.isEmpty(moduleModel.getUrlAddress())) {
//                String modelUrl = moduleModel.getUrlAddress();
//                Map<String, Object> address = new HashMap<>();
//                address.put("id", moduleModel.getId());
//                address.put("text", moduleModel.getFullName());
//                urlData.put(modelUrl, address);
//                List<ButtonModel> buttonList = authorizeInfo.getButtonList().stream().filter(t -> String.valueOf(t.getModuleId()).equals(String.valueOf(moduleModel.getId()))).collect(Collectors.toList());
//                for (ButtonModel buttonModel : buttonList) {
//                    if (!StringUtils.isEmpty(buttonModel.getUrlAddress()) && buttonModel.getUrlAddress() != null) {
//                        String[] buttonUrl = buttonModel.getUrlAddress().split(",");
//                        for (String item : buttonUrl) {
//                            if (item.contains(".html") && urlData.get(item) == null) {
//                                Map<String, Object> html = new HashMap<>();
//                                html.put("id", "");
//                                html.put("text", "");
//                                urlData.put(item, html);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return urlData;
//    }*/
}
