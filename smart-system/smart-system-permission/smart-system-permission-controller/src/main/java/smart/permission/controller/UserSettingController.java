package smart.permission.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.ActionResult;
import smart.base.vo.PaginationVO;
import smart.base.UserInfo;
import smart.base.entity.LogEntity;
import smart.base.model.button.ButtonModel;
import smart.base.model.column.ColumnModel;
import smart.base.model.module.ModuleModel;
import smart.base.model.resource.ResourceModel;
import smart.base.service.LogService;
import smart.permission.model.user.*;
import smart.permission.service.AuthorizeService;
import smart.permission.service.OrganizeService;
import smart.permission.service.UserService;
import smart.util.*;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import smart.permission.entity.AuthorizeEntity;
import smart.permission.entity.OrganizeEntity;
import smart.permission.entity.UserEntity;
import smart.permission.model.authorize.AuthorizeModel;
import smart.permission.model.authorize.AuthorizeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 个人资料
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "个人资料", value = "CurrentUsersInfo")
@RestController
@RequestMapping("/Permission/Users/Current")
public class UserSettingController {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private LogService logService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;

    /**
     * 我的信息
     *
     * @return
     */
    @ApiOperation("个人资料")
    @GetMapping("/BaseInfo")
    public ActionResult get() {
        UserInfo userInfo = userProvider.get();
        UserEntity userEntity = userService.getInfo(userInfo.getUserId());

        String tenantId = StringUtil.isEmpty(userProvider.get().getTenantId()) ? "" : userProvider.get().getTenantId();
        String catchKey=cacheKeyUtil.getAllUser();
        if(redisUtil.exists(catchKey)){
            redisUtil.remove(catchKey);
        }
        List<UserAllModel> userList = userService.getAll();
        UserBaseInfoVO vo = JsonUtil.getJsonToBean(userEntity, UserBaseInfoVO.class);
        if (userEntity != null) {
            UserAllModel userAllVO = userList.stream().filter(
                    t -> t.getId().equals(userEntity.getId())
            ).findFirst().orElse(new UserAllModel());
            vo.setRoleId(userAllVO.getRoleName());
            vo.setPosition(userAllVO.getPositionName());
            vo.setOrganize(userAllVO.getDepartment());
            vo.setCompany(userAllVO.getOrganize());
            vo.setManager(userAllVO.getManagerName());
            //设置语言和主题
            vo.setLanguage(userEntity.getLanguage() != null ? userEntity.getLanguage() : "zh-CN");
            vo.setTheme(userEntity.getTheme() != null ? userEntity.getTheme() : "W-001");
        }
        if(!StringUtil.isEmpty(userInfo.getUserIcon())){
            vo.setAvatar(UploaderUtil.uploaderImg(userInfo.getUserIcon()));
        }
        return ActionResult.success(vo);
    }

    /**
     * 我的权限
     *
     * @return
     */
    @ApiOperation("系统权限")
    @GetMapping("/Authorize")
    public ActionResult getList() {
        UserInfo userInfo = userProvider.get();
        AuthorizeVO authorizeModel = authorizeService.getAuthorize(true);
        List<AuthorizeEntity> authorizeList = new ArrayList<>();
        //赋值图标
        Map<String, ModuleModel> moduleMap = this.moduleList(authorizeModel.getModuleList());
        List<ModuleModel> modelList = authorizeModel.getModuleList().stream().filter(
                t -> t.getType() == 2
        ).collect(Collectors.toList());
        UserAuthorizeVO vo = UserAuthorizeVO.builder()
                .button(this.moduleButton(modelList, authorizeModel.getButtonList(), authorizeList, moduleMap))
                .column(this.moduleColumn(modelList, authorizeModel.getColumnList(), authorizeList, moduleMap))
                .resource(this.resourceData(modelList, authorizeModel.getResourceList(), authorizeList, moduleMap))
                .module(this.module(authorizeModel.getModuleList(), authorizeList)).build();
        return ActionResult.success(vo);
    }

    /**
     * 系统日志
     *
     * @param userLogForm
     * @return
     */
    @ApiOperation("系统日志")
    @GetMapping("/SystemLog")
    public ActionResult getLogList(UserLogForm userLogForm) {
        List<LogEntity> data = logService.getList(userLogForm);
        List<UserLogVO> list = JsonUtil.getJsonToList(data, UserLogVO.class);
        PaginationVO paginationVO = JsonUtil.getJsonToBean(userLogForm, PaginationVO.class);
        return ActionResult.page(list,paginationVO);
    }

    /**
     * 修改用户资料
     *
     * @param userInfoForm
     * @return
     */
    @ApiOperation("修改用户资料")
    @PutMapping("/BaseInfo")
    public ActionResult updateInfo(@RequestBody UserInfoForm userInfoForm) {
        UserEntity entity = JsonUtil.getJsonToBean(userInfoForm, UserEntity.class);
        String userId = userProvider.get().getUserId();
        String id = userService.getInfo(userId).getId();
        userService.update(id, entity);
        return ActionResult.success("保存成功");
    }

    /**
     * 修改用户密码
     *
     * @return
     */
    @ApiOperation("修改用户密码")
    @PostMapping("/Actions/ModifyPassword")
    public ActionResult modifyPassword(@RequestBody @Valid UserModifyPasswordForm userModifyPasswordForm) {
        UserEntity userEntity = userService.getInfo(userProvider.get().getUserId());
        if (userEntity != null) {
            String timestamp = String.valueOf(redisUtil.getString(userModifyPasswordForm.getTimestamp()));
            if (!userModifyPasswordForm.getCode().equalsIgnoreCase(timestamp)) {
                return ActionResult.fail("验证码错误");
            }
            if (!Md5Util.getStringMd5((userModifyPasswordForm.getOldPassword().toLowerCase() + userEntity.getSecretkey().toLowerCase())).equals(userEntity.getPassword())) {
                return ActionResult.fail("旧密码错误");
            }
            userEntity.setPassword(userModifyPasswordForm.getPassword());
            userService.updatePassword(userEntity);
            userProvider.remove();
            return ActionResult.success("修改成功，请牢记新密码。");
        }
        return ActionResult.fail("修改失败，账号不存在。");

    }

    /**
     * 我的下属
     *
     * @param
     * @return
     */
    @ApiOperation("我的下属")
    @GetMapping("/Subordinate")
    public ActionResult getSubordinate() {
        UserInfo userInfo = userProvider.get();
        String[] subordinateIds = userInfo.getSubordinateIds();
        List<String> ids = new ArrayList<>(Arrays.asList(subordinateIds));
        List<UserEntity> userName = userService.getUserName(ids);
        List<String> department = userName.stream().map(t -> t.getOrganizeId()).collect(Collectors.toList());
        List<OrganizeEntity> departmentList = organizeService.getOrganizeName(department);
        List<UserSubordinateVO> list = new ArrayList<>();
        for (UserEntity user : userName) {
            String departName = departmentList.stream().filter(
                    t -> String.valueOf(user.getOrganizeId()).equals(String.valueOf(t.getId()))
            ).findFirst().get().getFullName();
            UserSubordinateVO subordinateVO = UserSubordinateVO.builder().avatar(UploaderUtil.uploaderImg(user.getHeadIcon())).department(departName).userName(user.getRealName() + "/" + user.getAccount()).build();
            list.add(subordinateVO);
        }
        return ActionResult.success(list);
    }

    /**
     * 修改系统主题
     *
     * @param userThemeForm
     * @return
     */
    @ApiOperation("修改系统主题")
    @PutMapping("/SystemTheme")
    public ActionResult updateTheme(@RequestBody @Valid UserThemeForm userThemeForm) {
        UserEntity entity = JsonUtil.getJsonToBean(userThemeForm, UserEntity.class);
        entity.setId(userProvider.get().getUserId());
        userService.updateById(entity);
        return ActionResult.success("设置成功");
    }

    /**
     * 修改头像
     *
     * @return
     */
    @ApiOperation("修改头像")
    @PutMapping("/Avatar/{name}")
    public ActionResult updateAvatar(@PathVariable("name") String name) {
        UserInfo userInfo = userProvider.get();
        UserEntity userEntity = userService.getInfo(userInfo.getUserId());
        userEntity.setHeadIcon(name);
        userService.update(userEntity.getId(), userEntity);
        if (!StringUtil.isEmpty(userInfo.getId())) {
            userInfo.setUserIcon(name);
            redisUtil.insert(userInfo.getId(), userInfo, DateUtil.getTime(userInfo.getOverdueTime()) - DateUtil.getTime(new Date()));
        }
        return ActionResult.success("修改成功");
    }

    /**
     * 修改系统语言
     *
     * @param userLanguageForm
     * @return
     */
    @ApiOperation("修改系统语言")
    @PutMapping("/SystemLanguage")
    public ActionResult updateLanguage(@RequestBody @Valid UserLanguageForm userLanguageForm) {
        UserEntity userEntity = userService.getInfo(userProvider.get().getUserId());
        UserEntity entity = JsonUtil.getJsonToBean(userLanguageForm, UserEntity.class);
        userService.update(userEntity.getId(), entity);
        return ActionResult.success("设置成功");
    }


    /**
     * 赋值图标
     *
     * @param moduleList
     * @return
     */
    private Map<String, ModuleModel> moduleList(List<ModuleModel> moduleList) {
        Map<String, ModuleModel> auth = new HashMap<>();
        for (ModuleModel module : moduleList) {
            auth.put(module.getId(), module);
            module.setIcon(module.getIcon());
        }
        return auth;
    }

    /**
     * 功能权限
     *
     * @param moduleList    功能
     * @param authorizeLiat 权限集合
     * @return
     */
    private List<UserAuthorizeModel> module(List<ModuleModel> moduleList, List<AuthorizeEntity> authorizeLiat) {
        List<AuthorizeModel> treeList = JsonUtil.getJsonToList(moduleList, AuthorizeModel.class);
        List<SumTree<AuthorizeModel>> trees = TreeDotUtils.convertListToTreeDot(treeList);
        Iterator<SumTree<AuthorizeModel>> iterator = trees.iterator();

        while (iterator.hasNext()) {
            SumTree<AuthorizeModel> menuTreeVO = iterator.next();
            if(!"-1".equals(menuTreeVO.getParentId())){
                iterator.remove();
            }
        }
        List<UserAuthorizeModel> vo = JsonUtil.getJsonToList(trees, UserAuthorizeModel.class);
        return vo;
    }

    /**
     * 按钮权限
     *
     * @param moduleList       功能
     * @param moduleButtonList 按钮
     * @param authorizeLiat    权限集合
     * @return
     */
    private List<UserAuthorizeModel> moduleButton(List<ModuleModel> moduleList, List<ButtonModel> moduleButtonList, List<AuthorizeEntity> authorizeLiat, Map<String, ModuleModel> moduleMap) {
        List<AuthorizeModel> treeList = new ArrayList<>();
        //获取页面集合
        for (ModuleModel model : moduleList) {
            List<ButtonModel> buttonList = moduleButtonList.stream().filter(
                    t -> t.getModuleId().equals(model.getId())
            ).collect(Collectors.toList());
            if (buttonList.size() == 0) {
                continue;
            }
            for (ButtonModel buttonModel : buttonList) {
                AuthorizeModel treeModel = new AuthorizeModel();
                treeModel.setId(buttonModel.getId());
                treeModel.setFullName(buttonModel.getFullName());
                treeModel.setParentId("-1".equals(buttonModel.getParentId()) ? model.getId() : buttonModel.getParentId());
                treeModel.setIcon(buttonModel.getIcon());
                treeList.add(treeModel);
                ModuleModel partMap = moduleMap.get(treeModel.getParentId());
                if (partMap != null && treeList.stream().filter(t -> t.getId().equals(partMap.getId())).count() == 0) {
                    AuthorizeModel partId = JsonUtil.getJsonToBean(partMap, AuthorizeModel.class);
                    treeList.add(partId);
                    if (!"-1".equals(String.valueOf(partId.getParentId()))) {
                        ModuleModel firstMap = moduleMap.get(partId.getParentId());
                        if (firstMap != null && treeList.stream().filter(t -> t.getId().equals(firstMap.getId())).count() == 0) {
                            AuthorizeModel firstId = JsonUtil.getJsonToBean(firstMap, AuthorizeModel.class);
                            treeList.add(firstId);
                        }
                    }
                }
            }
        }
        List<SumTree<AuthorizeModel>> trees = TreeDotUtils.convertListToTreeDot(treeList);
        Iterator<SumTree<AuthorizeModel>> iterator = trees.iterator();

        while (iterator.hasNext()) {
            SumTree<AuthorizeModel> menuTreeVO = iterator.next();
            if(!"-1".equals(menuTreeVO.getParentId())){
                iterator.remove();
            }
        }
        List<UserAuthorizeModel> vo = JsonUtil.getJsonToList(trees, UserAuthorizeModel.class);

        return vo;
    }

    /**
     * 列表权限
     *
     * @param moduleList       功能
     * @param moduleColumnList 列表
     * @param authorizeLiat    权限集合
     * @return
     */
    private List<UserAuthorizeModel> moduleColumn(List<ModuleModel> moduleList, List<ColumnModel> moduleColumnList, List<AuthorizeEntity> authorizeLiat, Map<String, ModuleModel> moduleMap) {
        List<AuthorizeModel> treeList = new ArrayList<>();
        //获取页面集合
        for (ModuleModel module : moduleList) {
            List<ColumnModel> columnList = moduleColumnList.stream().filter(
                    t -> t.getModuleId().equals(module.getId())
            ).collect(Collectors.toList());
            if (columnList.size() == 0) {
                continue;
            }
            Map<String, String> map = new HashMap<>();
            for (ColumnModel columnEntity : columnList) {
                if (map.get(columnEntity.getBindTable()) == null) {
                    map.put(columnEntity.getBindTable(), columnEntity.getBindTableName());
                }
            }
            if(map.size()>1){
                for (Map.Entry<String, String> item : map.entrySet()) {
                    AuthorizeModel treeModel1 = new AuthorizeModel();
                    treeModel1.setId(RandomUtil.uuId());
                    treeModel1.setFullName(item.getKey()==null?item.getKey():item.getValue());
                    treeModel1.setParentId(module.getId());
                    treeModel1.setIcon("fa fa-tags column");
                    treeList.add(treeModel1);
                    for (ColumnModel columnEntity : columnList.stream().filter(
                            t -> t.getBindTable().equals(item.getKey())
                    ).collect(Collectors.toList())) {
                        AuthorizeModel treeModel2 = new AuthorizeModel();
                        treeModel2.setId(columnEntity.getId());
                        treeModel2.setFullName(columnEntity.getFullName());
                        treeModel2.setParentId(treeModel1.getId());
                        treeModel2.setIcon("fa fa-filter column ");
                        treeList.add(treeModel2);
                    }
                    ModuleModel partMap = moduleMap.get(treeModel1.getParentId());
                    if (partMap != null && treeList.stream().filter(t -> t.getId().equals(partMap.getId())).count() == 0) {
                        AuthorizeModel partId = JsonUtil.getJsonToBean(partMap, AuthorizeModel.class);
                        treeList.add(partId);
                        ModuleModel firstMap = moduleMap.get(partId.getParentId());
                        if (firstMap != null && treeList.stream().filter(t -> t.getId().equals(firstMap.getId())).count() == 0) {
                            AuthorizeModel firstId = JsonUtil.getJsonToBean(firstMap, AuthorizeModel.class);
                            treeList.add(firstId);
                        }
                    }
                }
            }else {
                for (ColumnModel columnEntity : columnList) {
                    AuthorizeModel treeModel = new AuthorizeModel();
                    treeModel.setId(columnEntity.getId());
                    treeModel.setFullName(columnEntity.getFullName());
                    treeModel.setParentId(module.getId());
                    treeModel.setIcon("fa fa-filter column ");
                    treeList.add(treeModel);
                    ModuleModel partMap = moduleMap.get(treeModel.getParentId());
                    if (partMap != null && treeList.stream().filter(t -> t.getId().equals(partMap.getId())).count() == 0) {
                        AuthorizeModel partId = JsonUtil.getJsonToBean(partMap, AuthorizeModel.class);
                        treeList.add(partId);
                        ModuleModel firstMap = moduleMap.get(partId.getParentId());
                        if (firstMap != null && treeList.stream().filter(t -> t.getId().equals(firstMap.getId())).count() == 0) {
                            AuthorizeModel firstId = JsonUtil.getJsonToBean(firstMap, AuthorizeModel.class);
                            treeList.add(firstId);
                        }
                    }
                }
            }
        }
        List<SumTree<AuthorizeModel>> trees = TreeDotUtils.convertListToTreeDot(treeList);
        Iterator<SumTree<AuthorizeModel>> iterator = trees.iterator();

        while (iterator.hasNext()) {
            SumTree<AuthorizeModel> menuTreeVO = iterator.next();
            if(!"-1".equals(menuTreeVO.getParentId())){
                iterator.remove();
            }
        }
        List<UserAuthorizeModel> vo = JsonUtil.getJsonToList(trees, UserAuthorizeModel.class);
        return vo;
    }

    /**
     * 数据权限
     *
     * @param moduleList         功能
     * @param moduleResourceList 资源
     * @param authorizeLiat      权限集合
     * @return
     */
    private List<UserAuthorizeModel> resourceData(List<ModuleModel> moduleList, List<ResourceModel> moduleResourceList, List<AuthorizeEntity> authorizeLiat, Map<String, ModuleModel> moduleMap) {
        List<AuthorizeModel> treeList = new ArrayList<>();
        for (ModuleModel module : moduleList) {
            List<ResourceModel> resourceList = moduleResourceList.stream().filter(
                    t -> t.getModuleId().equals(module.getId())
            ).collect(Collectors.toList());
            if (resourceList.size() == 0) {
                continue;
            }
            for (ResourceModel resourceModel : resourceList) {
                AuthorizeModel treeModel = new AuthorizeModel();
                treeModel.setId(resourceModel.getId());
                treeModel.setFullName(resourceModel.getFullName());
                treeModel.setParentId(module.getId());
                treeModel.setIcon("fa fa-binoculars resource");
                treeList.add(treeModel);
                ModuleModel partMap = moduleMap.get(treeModel.getParentId());
                if (partMap != null && treeList.stream().filter(t -> t.getId().equals(partMap.getId())).count() == 0) {
                    AuthorizeModel partId = JsonUtil.getJsonToBean(partMap, AuthorizeModel.class);
                    treeList.add(partId);
                    ModuleModel firstMap = moduleMap.get(partId.getParentId());
                    if (firstMap != null && treeList.stream().filter(t -> t.getId().equals(firstMap.getId())).count() == 0) {
                        AuthorizeModel firstId = JsonUtil.getJsonToBean(firstMap, AuthorizeModel.class);
                        treeList.add(firstId);
                    }
                }
            }
        }
        List<SumTree<AuthorizeModel>> trees = TreeDotUtils.convertListToTreeDot(treeList);
        Iterator<SumTree<AuthorizeModel>> iterator = trees.iterator();

        while (iterator.hasNext()) {
            SumTree<AuthorizeModel> menuTreeVO = iterator.next();
            if(!"-1".equals(menuTreeVO.getParentId())){
                iterator.remove();
            }
        }
        List<UserAuthorizeModel> vo = JsonUtil.getJsonToList(trees, UserAuthorizeModel.class);
        return vo;
    }

}
