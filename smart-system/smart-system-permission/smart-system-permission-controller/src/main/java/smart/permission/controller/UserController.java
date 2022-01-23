package smart.permission.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.service.SysconfigService;
import smart.base.vo.ListVO;
import smart.base.vo.PaginationVO;
import smart.config.ConfigValueUtil;
import smart.util.data.DataSourceContextHolder;
import smart.exception.LoginException;
import smart.model.BaseSystemInfo;
import smart.util.*;
import smart.base.*;
import smart.permission.entity.OrganizeEntity;
import smart.permission.entity.PositionEntity;
import smart.permission.entity.RoleEntity;
import smart.permission.entity.UserEntity;
import smart.exception.DataException;
import smart.permission.model.user.*;
import smart.permission.service.*;
import smart.util.treeutil.ListToTreeUtil;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import smart.util.type.StringNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "用户管理", value = "Users")
@Slf4j
@RestController
@RequestMapping("/Permission/Users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;
    @Autowired
    private SysconfigService sysconfigService;
    @Autowired
    private ConfigValueUtil configValueUtil;

    /**
     * 获取用户列表
     *
     * @param pagination
     * @return
     */
    @ApiOperation("获取用户列表")
    @GetMapping
    public ActionResult getList(PaginationUser pagination) {
        List<OrganizeEntity> organizeList = organizeService.getList().stream().filter(t -> "1".equals(String.valueOf(t.getEnabledMark()))).collect(Collectors.toList());
        List<OrganizeEntity> dataAll = JsonUtil.getJsonToList(ListToTreeUtil.treeWhere(pagination.getOrganizeId(), organizeList), OrganizeEntity.class);
        List<String> organizeIds = dataAll.stream().map(t -> t.getId()).collect(Collectors.toList());
        organizeIds.add(pagination.getOrganizeId());
        pagination.setOrganizeId(String.join(",", organizeIds));
        List<UserEntity> data = userService.getList(pagination, pagination.getOrganizeId());
        List<UserAllModel> userList = userService.getDbUserAll();
        List<UserListVO> list = new ArrayList<>();
        for (UserEntity entity : data) {
            UserListVO user = JsonUtil.getJsonToBean(entity, UserListVO.class);
            UserAllModel userAllVO = userList.stream().filter(
                    t -> t.getId().equals(entity.getId())
            ).findFirst().orElse(new UserAllModel());
            if (userAllVO.getPositionName().contains(",null")) {
                userAllVO.setPositionName(userAllVO.getPositionName().replaceAll(",null", ""));
            }
            if (userAllVO.getPositionName().contains("null")) {
                userAllVO.setPositionName(userAllVO.getPositionName().replaceAll("null", ""));
            }
            user.setPosition(userAllVO.getPositionName());
            user.setDepartment(userAllVO.getDepartment());
            user.setRoleName(userAllVO.getRoleName());
            list.add(user);
        }
        PaginationVO paginationVO = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        return ActionResult.page(list, paginationVO);
    }

    /**
     * 获取用户列表
     *
     * @return
     */
    @ApiOperation("获取所有用户列表")
    @GetMapping("/All")
    public ActionResult getAllUsers(Pagination pagination) {
        List<UserAllModel> uservo = userService.getAll().stream().filter(
                t -> !StringUtil.isEmpty(pagination.getKeyword()) ? t.getRealName().toLowerCase().contains(pagination.getKeyword())
                        || t.getAccount().toLowerCase().contains(pagination.getKeyword()) : t.getRealName() != null
        ).collect(Collectors.toList());
        List<UserAllVO> user = JsonUtil.getJsonToList(uservo, UserAllVO.class);
        ListVO vo = new ListVO();
        vo.setList(user);
        return ActionResult.success(vo);
    }


    /**
     * 获取用户列表
     *
     * @return
     */
    @GetMapping("/modelAll")
    public ActionResult getModelAll() {
        List<UserAllModel> uservo = userService.getAll();
        return ActionResult.success(uservo);
    }

    /**
     * 获取用户下拉框列表
     *
     * @return
     */
    @ApiOperation("获取用户下拉框列表")
    @GetMapping("/Selector")
    public ActionResult selector() {
        List<OrganizeEntity> allOrganizeData = organizeService.getList();
        List<OrganizeEntity> organizeData = organizeService.getList().stream().filter(
                t -> "1".equals(String.valueOf(t.getEnabledMark()))
        ).collect(Collectors.toList());
        List<UserEntity> userData = userService.getList().stream().filter(
                t -> "1".equals(String.valueOf(t.getEnabledMark()))
        ).collect(Collectors.toList());
        List<UserSelectorModel> treeList = JsonUtil.getJsonToList(organizeData, UserSelectorModel.class);
        for (UserSelectorModel entity1 : treeList) {
            if ("department".equals(entity1.getType())) {
                entity1.setIcon("icon-ym icon-ym-tree-department1");
            } else if ("company".equals(entity1.getType())) {
                entity1.setIcon("icon-ym icon-ym-tree-organization3");
            }
        }
        for (UserEntity entity : userData) {
            UserSelectorModel treeModel = new UserSelectorModel();
            treeModel.setId(entity.getId());
            treeModel.setParentId(entity.getOrganizeId());
            treeModel.setFullName(entity.getRealName() + "/" + entity.getAccount());
            treeModel.setType("user");
            treeModel.setIcon("icon-ym icon-ym-tree-user2");
            treeList.add(treeModel);
        }
        List<SumTree<UserSelectorModel>> trees = TreeDotUtils.convertListToTreeDot(treeList);
        List<UserSelectorVO> listvo = JsonUtil.getJsonToList(trees, UserSelectorVO.class);
        List<OrganizeEntity> entities = allOrganizeData.stream().filter(
                t -> t.getEnabledMark() == 0 && "-1".equals(t.getParentId())
        ).collect(Collectors.toList());
        Iterator<UserSelectorVO> iterator = listvo.iterator();
        while (iterator.hasNext()) {
            UserSelectorVO userSelectorVO = iterator.next();
            for (OrganizeEntity entity : entities) {
                if (entity.getId().equals(userSelectorVO.getParentId())) {
                    iterator.remove();//使用迭代器的删除方法删除
                }
            }
        }
        ListVO vo = new ListVO();
        vo.setList(listvo);
        return ActionResult.success(vo);
    }

    /**
     * 获取用户信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取用户信息")
    @GetMapping("/{id}")
    public ActionResult getInfo(@PathVariable("id") String id) throws DataException {
        UserEntity entity = userService.getInfo(id);
        if (entity != null && !StringUtil.isEmpty(entity.getRoleId())) {
            String[] roledIds = entity.getRoleId().split(",");
            StringBuilder newRoleId = new StringBuilder();
            for (String roleId : roledIds) {
                RoleEntity roleEntity = roleService.getInfo(roleId);
                if (roleEntity != null && roleEntity.getEnabledMark() == 1) {
                    newRoleId.append(roleId + ",");
                }
            }
            if (newRoleId.length() == 0) {
                entity.setRoleId("");
            }
            if (newRoleId.length() > 0) {
                entity.setRoleId(newRoleId.substring(0, newRoleId.length() - 1));
            }
        }
        if (entity != null && !StringUtil.isEmpty(entity.getPositionId())) {
            String[] positionIds = entity.getPositionId().split(",");
            StringBuilder newPositionIds = new StringBuilder();
            for (String positionId : positionIds) {
                PositionEntity positionEntity = positionService.getInfo(positionId);
                if (positionEntity != null && positionEntity.getEnabledMark() == 1) {
                    newPositionIds.append(positionId + ",");
                }
            }
            if (newPositionIds.length() == 0) {
                entity.setPositionId("");
            }
            if (newPositionIds.length() > 0) {
                entity.setPositionId(newPositionIds.substring(0, newPositionIds.length() - 1));
            }
        }
        //去除字段为空字符串
        if (StringUtil.isEmpty(entity.getRoleId())) {
            entity.setRoleId(null);
        }
        if (StringUtil.isEmpty(entity.getPositionId())) {
            entity.setPositionId(null);
        }
        UserInfoVO vo = JsonUtil.getJsonToBeanEx(entity, UserInfoVO.class);
        return ActionResult.success(vo);
    }


    /**
     * 新建用户
     *
     * @param userCrForm
     * @return
     */
    @ApiOperation("新建用户")
    @PostMapping
    public ActionResult create(@RequestBody @Valid UserCrForm userCrForm) {
        UserEntity entity = JsonUtil.getJsonToBean(userCrForm, UserEntity.class);
        entity.setPassword("4a7d1ed414474e4033ac29ccb8653d9b");
        if (StringUtil.isEmpty(entity.getHeadIcon())) {
            entity.setHeadIcon("001.png");
        }
        if (userService.isExistByAccount(userCrForm.getAccount())) {
            return ActionResult.fail("账户名称不能重复");
        }
        userService.create(entity);
        String catchKey = cacheKeyUtil.getAllUser();
        if (redisUtil.exists(catchKey)) {
            redisUtil.remove(catchKey);
        }
        return ActionResult.success("新建成功");
    }

    /**
     * 修改用户
     *
     * @param userUpForm
     * @param id         主键值
     * @return
     */
    @ApiOperation("修改用户")
    @PutMapping("/{id}")
    public ActionResult update(@PathVariable("id") String id, @RequestBody @Valid UserUpForm userUpForm) {
        UserEntity entity = JsonUtil.getJsonToBean(userUpForm, UserEntity.class);
        //将禁用的id加进数据
        UserEntity entity1 = userService.getInfo(id);
        if (StringNumber.ONE.equals(String.valueOf(entity1.getIsAdministrator()))) {
            return ActionResult.fail("无法修改管理员账户");
        }
        if (userService.isExistByAccount(id)) {
            return ActionResult.fail("账户名称不能重复");
        }

        if (entity1.getRoleId() != null) {
            String roleIds = (userUpForm.getRoleId() + "," + entity1.getRoleId());
            if (",".equals(roleIds.substring(0, 1))) {
                roleIds = roleIds.substring(1);
            }
            userUpForm.setRoleId(roleIds);
        }
        if (entity1.getPositionId() != null) {
            String positionIds = (userUpForm.getPositionId() + "," + entity1.getPositionId());
            if (",".equals(positionIds.substring(0, 1))) {
                positionIds = positionIds.substring(1);
            }
            userUpForm.setPositionId(positionIds);
        }
        boolean flag = userService.update(id, entity);
        if (flag == false) {
            return ActionResult.fail("更新失败，数据不存在");
        }
        return ActionResult.success("更新成功");
    }

    /**
     * 删除用户
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
        UserEntity entity = userService.getInfo(id);
        if (entity != null) {
            if (StringNumber.ONE.equals(String.valueOf(entity.getIsAdministrator()))) {
                return ActionResult.fail("无法删除管理员账户");
            }
            String tenantId = StringUtil.isEmpty(userProvider.get().getTenantId()) ? "" : userProvider.get().getTenantId();
            String catchKey = tenantId + "allUser";
            if (redisUtil.exists(catchKey)) {
                redisUtil.remove(catchKey);
            }
            userService.delete(entity);
            userProvider.removeOnLine(entity.getId());
            return ActionResult.success("删除成功");
        }
        return ActionResult.fail("删除失败，数据不存在");
    }


    /**
     * 修改用户密码
     *
     * @return
     */
    @ApiOperation("修改用户密码")
    @PostMapping("/{id}/Actions/ResetPassword")
    public ActionResult modifyPassword(@PathVariable("id") String id, @RequestBody @Valid UserResetPasswordForm userResetPasswordForm) {
        UserEntity entity = userService.getInfo(id);
        if (entity != null) {
            if (StringNumber.ONE.equals(String.valueOf(entity.getIsAdministrator()))) {
                return ActionResult.fail("无法修改管理员账户密码");
            }
            entity.setPassword(userResetPasswordForm.getUserPassword());
            userService.updatePassword(entity);
            userProvider.removeOnLine(entity.getId());
            return ActionResult.success("操作成功");
        }
        return ActionResult.success("操作失败,用户不存在");
    }

    /**
     * 更新用户状态
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("更新用户状态")
    @PutMapping("/{id}/Actions/State")
    public ActionResult disable(@PathVariable("id") String id) {
        UserEntity entity = userService.getInfo(id);
        if (entity != null) {
            if (StringNumber.ONE.equals(String.valueOf(entity.getIsAdministrator()))) {
                return ActionResult.fail("无法修改管理员账户状态");
            }
            if (entity.getEnabledMark() != null) {
                if (entity.getEnabledMark() == 1) {
                    entity.setEnabledMark(0);
                    userProvider.removeOnLine(entity.getId());
                    userService.update(id, entity);
                } else {
                    entity.setEnabledMark(1);
                    userService.update(id, entity);
                }
            } else {
                entity.setEnabledMark(1);
                userService.update(id, entity);
            }
            return ActionResult.success("操作成功");
        }
        return ActionResult.success("操作失败,用户不存在");
    }

    /**
     * 通过account获取user
     *
     * @param account
     * @return
     */
    @NoDataSourceBind
    @GetMapping("/checkUser/{account}/{dbId}/{dbName}")
    public UserEntity checkUser(@PathVariable("account") String account, @PathVariable("dbId") String dbId, @PathVariable("dbName") String dbName) {
        //判断是否为多租户
        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
            DataSourceContextHolder.setDatasource(dbId, dbName);
        }
        System.out.println("databases="+DataSourceContextHolder.getDatasourceName());
        UserEntity userEntity = userService.checkLogin(account);
        return userEntity;
    }

    /**
     * 获取所有用户
     *
     * @return
     */
    @GetMapping("/Users/modelAll")
    public ActionResult getAll() {
        List<UserAllModel> all = userService.getAll();
        return ActionResult.success(all);
    }

    @GetMapping("/getListByManagerId/{userId}")
    public List<UserEntity> getListByManagerId(@PathVariable("userId") String userId) {
        List<UserEntity> list = userService.getListByManagerId(userId);
        return list;
    }

    @GetMapping("/getListByPositionId/{userId}")
    public List<UserEntity> getListByPositionId(@PathVariable("userId") String userId) {
        List<UserEntity> list = userService.getListByPositionId(userId);
        return list;
    }

    /**
     * 列表
     *
     * @return
     */
    @GetMapping("/getUserList")
    public List<UserEntity> getUserList() {
        List<UserEntity> list = userService.getList();
        return list;
    }

    /**
     * 直接从数据库获取所有用户信息（不过滤冻结账号）
     *
     * @return
     */
    @GetMapping("/getDbUserAll")
    public List<UserAllModel> getDbUserAll() {
        List<UserAllModel> list = userService.getDbUserAll();
        return list;
    }

    /**
     * 信息
     *
     * @param userId 主键值
     * @return
     */
    @GetMapping("/getInfoById/{userId}")
    public UserEntity getInfoById(@PathVariable("userId") String userId) {
        UserEntity entity = userService.getInfo(userId);
        return entity;
    }

    /**
     * 通过id修改
     *
     * @param userEntity
     */
    @GetMapping("/updateById")
    public void updateById(UserEntity userEntity) {
        userService.updateById(userEntity);
    }

    /**
     * 验证账号是否可以使用
     *
     * @param account
     * @param password
     * @return
     * @throws LoginException
     */
    @NoDataSourceBind
    @GetMapping("/isExistUser/{account}/{password}/{tenantId}/{dbName}")
    public UserEntity isExistUser(@PathVariable("account") String account, @PathVariable("password") String password, @PathVariable("tenantId") String tenantId, @PathVariable("dbName") String dbName) throws LoginException {
        UserInfo userInfo = new UserInfo();
        UserEntity entity = null;
        //判断是否为多租户
        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
            userInfo.setTenantId(tenantId);
            userInfo.setTenantDbConnectionString(dbName);
            //设置租户
            DataSourceContextHolder.setDatasource(tenantId, dbName);
        }
        entity = userService.isExistUser(account, password);
        userInfo = userService.userInfo(userInfo, entity);
        userProvider.add(userInfo);
        BaseSystemInfo sysConfigInfo = sysconfigService.getSysInfo();
        //安全验证
        if (StringNumber.ONE.equals(sysConfigInfo.getWhitelistSwitch())) {
            List<String> iplist = Arrays.asList(sysConfigInfo.getWhitelistIp().split(","));
            if (!iplist.contains(IpUtil.getIpAddr())) {
                throw new LoginException("此IP未在白名单中，请联系管理员");
            }
        }
        return entity;
    }

}
