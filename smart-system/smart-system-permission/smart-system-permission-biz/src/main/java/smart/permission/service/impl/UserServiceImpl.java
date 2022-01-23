package smart.permission.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.Pagination;
import smart.base.SysConfigApi;
import smart.base.UserInfo;
import smart.config.ConfigValueUtil;
import smart.emnus.DbType;
import smart.exception.LoginException;
import smart.model.BaseSystemInfo;
import smart.permission.entity.*;
import smart.permission.mapper.UserMapper;
import smart.permission.model.user.UserAllModel;
import smart.permission.service.*;
import smart.util.*;
import smart.util.data.DataSourceContextHolder;
import smart.util.treeutil.ListToTreeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;
    @Autowired
    private DataSourceUtil dataSourceUtil;
    @Autowired
    private SysConfigApi sysConfigApi;
    @Autowired
    private ConfigValueUtil configValueUtil;

    @Override
    public List<UserEntity> getList() {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(UserEntity::getSortCode);
        return this.list(queryWrapper);
    }

    @Override
    public List<UserEntity> getList(Pagination pagination, String organizeId) {
        String userId = userProvider.get().getUserId();
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ne(UserEntity::getId, userId);
        //组织机构
        if (!StringUtil.isEmpty(organizeId)) {
            String[] organizeIds = organizeId.split(",");
            if (DbType.ORACLE.equals(dataSourceUtil.getDataType().toLowerCase())) {
                queryWrapper.in("to_char(F_ORGANIZEID)", organizeIds);
            } else {
                queryWrapper.lambda().in(UserEntity::getOrganizeId, organizeIds);
            }
        }
        //关键字（账户、姓名、手机）
        if (!StringUtil.isEmpty(pagination.getKeyword())) {
            queryWrapper.lambda().and(
                    t -> t.like(UserEntity::getAccount, pagination.getKeyword())
                            .or().like(UserEntity::getRealName, pagination.getKeyword())
                            .or().like(UserEntity::getMobilePhone, pagination.getKeyword())
            );
        }
        //排序
        queryWrapper.lambda().orderByAsc(UserEntity::getSortCode).orderByDesc(UserEntity::getCreatorTime);
        queryWrapper.lambda().orderByAsc(UserEntity::getSortCode);
        Page<UserEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<UserEntity> userIPage = this.page(page, queryWrapper);
        return pagination.setData(userIPage.getRecords(), userIPage.getTotal());
    }

    @Override
    public List<UserEntity> getListByPositionId(String positionId) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserEntity::getPositionId, positionId);
        return this.list(queryWrapper);
    }

    @Override
    public List<UserEntity> getListByManagerId(String managerId) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserEntity::getManagerId, managerId);
        return this.list(queryWrapper);
    }

    @Override
    public UserEntity getInfo(String id) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public String getUserName(String id) {
        UserEntity entity = this.getInfo(id);
        if (entity != null) {
            return entity.getRealName() + "(" + entity.getAccount() + ")";
        } else {
            return "";
        }
    }

    @Override
    public boolean isExistByAccount(String account) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserEntity::getAccount, account);
        UserEntity entity = this.getOne(queryWrapper);
        if (entity != null) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(UserEntity entity) {
        //添加用户
        entity.setId(RandomUtil.uuId());
        entity.setQuickQuery(PinYinUtil.getFirstSpell(entity.getRealName()));
        entity.setSecretkey(RandomUtil.uuId());
        entity.setPassword(Md5Util.getStringMd5(entity.getPassword().toLowerCase() + entity.getSecretkey().toLowerCase()));
        entity.setIsAdministrator(0);
        entity.setCreatorUserId(userProvider.get().getUserId());
        this.save(entity);
        //添加用户关系
        List<UserRelationEntity> relationList = new ArrayList<>();
        //关联岗位
        if (entity.getPositionId() != null) {
            String[] position = entity.getPositionId().split(",");
            for (int i = 0; i < position.length; i++) {
                UserRelationEntity relationEntity = new UserRelationEntity();
                relationEntity.setId(RandomUtil.uuId());
                relationEntity.setObjectType("Position");
                relationEntity.setObjectId(position[i]);
                relationEntity.setSortCode(Long.parseLong(i + ""));
                relationEntity.setUserId(entity.getId());
                relationEntity.setCreatorTime(entity.getCreatorTime());
                relationEntity.setCreatorUserId(entity.getCreatorUserId());
                relationList.add(relationEntity);
            }
        }
        //关联角色
        if (entity.getRoleId() != null) {
            String[] position = entity.getRoleId().split(",");
            for (int i = 0; i < position.length; i++) {
                UserRelationEntity relationEntity = new UserRelationEntity();
                relationEntity.setId(RandomUtil.uuId());
                relationEntity.setObjectType("Role");
                relationEntity.setObjectId(position[i]);
                relationEntity.setSortCode(Long.parseLong(i + ""));
                relationEntity.setUserId(entity.getId());
                relationEntity.setCreatorTime(entity.getCreatorTime());
                relationEntity.setCreatorUserId(entity.getCreatorUserId());
                relationList.add(relationEntity);
            }
        }
        for (UserRelationEntity userRelationEntity : relationList) {
            userRelationService.save(userRelationEntity);
        }
//        if (relationList.size() > 0) {
//            userRelationService.saveBatch(relationList);
//        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(String id, UserEntity entity) {
        //更新用户
        entity.setId(id);
        entity.setQuickQuery(PinYinUtil.getFirstSpell(entity.getRealName()));
        entity.setLastModifyTime(DateUtil.getNowDate());
        entity.setLastModifyUserId(userProvider.get().getUserId());
        entity.setBirthday("".equals(entity.getBirthday()) ? null : entity.getBirthday());
        entity.setEntryDate("".equals(entity.getEntryDate()) ? null : entity.getEntryDate());
        if (!this.updateById(entity)) {
            return false;
        }
        //更新用户关系
        List<UserRelationEntity> relationList = new ArrayList<>();
        //删除用户关联
        QueryWrapper<UserRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserRelationEntity::getUserId, entity.getId());
        userRelationService.remove(queryWrapper);
        //关联岗位
        if (entity.getPositionId() != null) {
            String[] position = entity.getPositionId().split(",");
            for (int i = 0; i < position.length; i++) {
                UserRelationEntity relationEntity = new UserRelationEntity();
                relationEntity.setId(RandomUtil.uuId());
                relationEntity.setObjectType("Position");
                relationEntity.setObjectId(position[i]);
                relationEntity.setSortCode(Long.parseLong(i + ""));
                relationEntity.setUserId(entity.getId());
                relationEntity.setCreatorTime(entity.getCreatorTime());
                relationEntity.setCreatorUserId(entity.getCreatorUserId());
                relationList.add(relationEntity);
            }
        }
        //关联角色
        if (entity.getRoleId() != null) {
            String[] position = entity.getRoleId().split(",");
            for (int i = 0; i < position.length; i++) {
                UserRelationEntity relationEntity = new UserRelationEntity();
                relationEntity.setId(RandomUtil.uuId());
                relationEntity.setObjectType("Role");
                relationEntity.setObjectId(position[i]);
                relationEntity.setSortCode(Long.parseLong(i + ""));
                relationEntity.setUserId(entity.getId());
                relationEntity.setCreatorTime(entity.getCreatorTime());
                relationEntity.setCreatorUserId(entity.getCreatorUserId());
                relationList.add(relationEntity);
            }
        }
        for (UserRelationEntity userRelationEntity : relationList) {
            userRelationService.save(userRelationEntity);
        }
        return true;
//        if (relationList.size() > 0) {
//            userRelationService.saveBatch(relationList);
//        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(UserEntity entity) {
        this.removeById(entity.getId());
        //删除用户关联
        QueryWrapper<UserRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserRelationEntity::getUserId, entity.getId());
        userRelationService.remove(queryWrapper);
    }


    @Override
    public void updatePassword(UserEntity entity) {
        entity.setSecretkey(RandomUtil.uuId());
        entity.setPassword(Md5Util.getStringMd5(entity.getPassword().toLowerCase() + entity.getSecretkey().toLowerCase()));
        entity.setChangePasswordDate(DateUtil.getNowDate());
        this.updateById(entity);
    }

    @Override
    public void settingMenu(String id, String menuId) {
        UserEntity entity = this.getInfo(id);
        if (entity != null) {
            entity.setCommonMenu(menuId);
            this.updateById(entity);
        }
    }

    @Override
    public List<UserEntity> getUserName(List<String> id) {
        List<UserEntity> list = new ArrayList<>();
        if (id.size() > 0) {
            QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(UserEntity::getId, id);
            list = this.list(queryWrapper);
        }
        return list;
    }

    /**
     * 有判断redis来获取所有用户信息
     *
     * @return
     */
    @Override
    public List<UserAllModel> getAll() {
        String catchKey = cacheKeyUtil.getAllUser();
        if (redisUtil.exists(catchKey)) {
            return JsonUtil.getJsonToList(redisUtil.getString(catchKey).toString(), UserAllModel.class);
        }
        List<UserEntity> list = this.getList().stream().filter(t -> "1".equals(String.valueOf(t.getEnabledMark()))).collect(Collectors.toList());
        //获取全部部门信息
        List<OrganizeEntity> departmentList = organizeService.getList();
        //获取全部岗位信息
        List<PositionEntity> positionList = positionService.getList();
        //获取全部角色信息
        List<RoleEntity> roleList = roleService.getList();
        List<UserAllModel> models = JsonUtil.getJsonToList(list, UserAllModel.class);
        for (UserAllModel model : models) {
            //部门名称
            OrganizeEntity deptEntity = departmentList.stream().filter(t -> t.getId().equals(model.getOrganizeId())).findFirst().orElse(new OrganizeEntity());
            if (StringUtil.isNotEmpty(deptEntity.getFullName())) {
                model.setDepartment(deptEntity.getFullName());
                model.setDepartmentId(deptEntity.getId());
            }
            //组织名称
            OrganizeEntity organizeEntity = departmentList.stream().filter(t -> t.getId().equals(String.valueOf(deptEntity.getParentId()))).findFirst().orElse(new OrganizeEntity());
            if (organizeEntity != null) {
                model.setOrganizeId(organizeEntity.getId());
                model.setOrganize(organizeEntity.getFullName());
            }
            //岗位名称(多个)
            if (model.getPositionId() != null) {
                List<String> positionName = new ArrayList<>();
                for (String id : model.getPositionId().split(",")) {
                    String name = positionList.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(new PositionEntity()).getFullName();
                    if (!StringUtil.isEmpty(name)) {
                        positionName.add(name);
                    }
                }
                model.setPositionName(String.join(",", positionName));
            }
            //角色名称(多个)
            if (model.getRoleId() != null) {
                List<String> roleName = new ArrayList<>();
                for (String id : model.getRoleId().split(",")) {
                    String name = roleList.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(new RoleEntity()).getFullName();
                    if (!StringUtil.isEmpty(name)) {
                        roleName.add(name);
                    }
                }
                model.setRoleName(String.join(",", roleName));
            }
            //主管名称
            String managerName = list.stream().filter(t -> t.getId().equals(model.getManagerId())).findFirst().orElse(new UserEntity()).getRealName();
            if (StringUtil.isNotEmpty(managerName)) {
                model.setManagerName(managerName);
            }
            model.setHeadIcon(UploaderUtil.uploaderImg(model.getHeadIcon()));
        }
        String allUser = JsonUtil.getObjectToString(models);
        redisUtil.insert(cacheKeyUtil.getAllUser(), allUser, 300);
        return models;
    }

    /**
     * 直接从数据库获取所有用户信息（不过滤冻结账号）
     *
     * @return
     */
    @Override
    public List<UserAllModel> getDbUserAll() {
        List<UserEntity> list = this.getList();
        //获取全部部门信息
        List<OrganizeEntity> departmentList = organizeService.getList();
        //获取全部岗位信息
        List<PositionEntity> positionList = positionService.getList();
        //获取全部角色信息
        List<RoleEntity> roleList = roleService.getList();
        List<UserAllModel> models = JsonUtil.getJsonToList(list, UserAllModel.class);
        for (UserAllModel model : models) {
            //部门名称
            OrganizeEntity organize = departmentList.stream().filter(t -> t.getId().equals(model.getOrganizeId())).findFirst().orElse(new OrganizeEntity());
            if (StringUtil.isNotEmpty(organize.getFullName())) {
                model.setDepartment(organize.getFullName());
            }
            //组织名称
            String organizeName = departmentList.stream().filter(t -> t.getId().equals(String.valueOf(organize.getParentId()))).findFirst().orElse(new OrganizeEntity()).getFullName();
            if (StringUtil.isNotEmpty(organizeName)) {
                model.setOrganize(organizeName);
            }
            //岗位名称(多个)
            if (model.getPositionId() != null) {
                List<String> positionName = new ArrayList<>();
                for (String id : model.getPositionId().split(",")) {
                    String name = positionList.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(new PositionEntity()).getFullName();
                    positionName.add(name);
                }
                model.setPositionName(String.join(",", positionName));
            }
            //角色名称(多个)
            if (model.getRoleId() != null) {
                List<String> roleName = new ArrayList<>();
                for (String id : model.getRoleId().split(",")) {
                    String name = roleList.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(new RoleEntity()).getFullName();
                    roleName.add(name);
                }
                model.setRoleName(String.join(",", roleName));
            }
            //主管名称
            String managerName = list.stream().filter(t -> t.getId().equals(model.getManagerId())).findFirst().orElse(new UserEntity()).getRealName();
            if (StringUtil.isNotEmpty(managerName)) {
                model.setManagerName(managerName);
            }
            model.setHeadIcon(UploaderUtil.uploaderImg(model.getHeadIcon()));
        }
        return models;
    }

    @Override
    public UserEntity checkLogin(String account) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserEntity::getAccount, account);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public UserEntity isExistUser(String account, String password) throws LoginException {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserEntity::getAccount, account);
        UserEntity userEntity = this.checkLogin(account);
        if (userEntity == null) {
            throw new LoginException("无效的账号");
        }
        //设置用户公司为总公司
        if (userEntity.getIsAdministrator() == 0) {
            userEntity.setOrganizeId(organizeService.getById(userEntity.getOrganizeId()).getParentId());
        }
        //判断用户所属的角色是否被禁用
        RoleEntity roleEntity;
        if (!StringUtil.isEmpty(userEntity.getRoleId())) {
            String[] roleIds = userEntity.getRoleId().split(",");
            int i = 0;
            if (userEntity.getIsAdministrator() == 0) {
                for (String roleId : roleIds) {
                    roleEntity = roleService.getInfo(roleId);
                    if (roleEntity != null && roleEntity.getEnabledMark() != null && roleEntity.getEnabledMark() != 0) {
                        i = 1;
                    }
                }
                if (i != 1) {
                    throw new LoginException("权限不足");
                }
            }

        }
        if (userEntity.getIsAdministrator() == 0) {
            if (userEntity.getEnabledMark() == null) {
                throw new LoginException("账号未被激活");
            }
            if (userEntity.getEnabledMark() == 0) {
                throw new LoginException("账号被禁用");
            }
        }
        if (userEntity.getDeleteMark() != null && userEntity.getDeleteMark() == 1) {
            throw new LoginException("账号已被删除");
        }
        return userEntity;
    }

    @Override
    public UserInfo userInfo(UserInfo userInfo, UserEntity userEntity) {
        userInfo.setIsAdministrator(BooleanUtil.toBoolean(String.valueOf(userEntity.getIsAdministrator())));
        userInfo.setUserId(userEntity.getId());
        userInfo.setUserAccount(userEntity.getAccount());
        userInfo.setUserName(userEntity.getRealName());
        userInfo.setUserIcon(userEntity.getHeadIcon());
        userInfo.setDepartmentId(userEntity.getOrganizeId());
        //公司Id
        String organizeId = userEntity.getOrganizeId();
        userInfo.setOrganizeId(organizeId);
        userInfo.setManagerId(userEntity.getManagerId());
        boolean b = BooleanUtil.toBoolean(String.valueOf((userEntity.getIsAdministrator())));
        userInfo.setSubOrganizeIds(this.getSubOrganizeIds(userEntity.getOrganizeId(), b));
        userInfo.setSubordinateIds(this.getSubordinateId(userEntity.getId()));
        userInfo.setPositionIds(this.getPositionId(userEntity.getId()));
        userInfo.setRoleIds(this.getRoleId(userEntity.getId()));
        userInfo.setLoginIpAddress(IpUtil.getIpAddr());
        userInfo.setLoginTime(DateUtil.getmmNow());
        userInfo.setLoginPlatForm(ServletUtil.getUserAgent());
        userInfo.setPrevLoginTime(userEntity.getPrevLogTime());
        userInfo.setPrevLoginIpAddress(userEntity.getPrevLogIP());
        userInfo.setPrevLoginIpAddressName(IpUtil.getIpCity(userEntity.getPrevLogIP()));
        Integer minu = 0;
        if (Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
            minu = Integer.valueOf(sysConfigApi.getSysInfo(DataSourceContextHolder.getDatasourceId(), DataSourceContextHolder.getDatasourceName()).getTokenTimeout());
        } else {
            minu = Integer.valueOf(sysConfigApi.getSysInfo("1", "1").getTokenTimeout());
        }
        userInfo.setTokenTimeout(minu);
        userInfo.setOverdueTime(DateUtil.dateAddMinutes(null, minu));
        return userInfo;
    }

    /**
     * 获取角色
     *
     * @param userId
     * @return
     */
    private String[] getRoleId(String userId) {
        List<UserRelationEntity> data = userRelationService.getListByUserId(userId);
        List<String> list = data.stream().filter(m -> "Role".equals(m.getObjectType())).map(t -> t.getObjectId()).collect(Collectors.toList());
        return list.toArray(new String[list.size()]);
    }

    /**
     * 获取下属
     *
     * @param userId
     * @return
     */
    private String[] getSubordinateId(String userId) {
        List<UserEntity> data = this.getListByManagerId(userId);
        List<String> list = data.stream().map(t -> t.getId()).collect(Collectors.toList());
        return list.toArray(new String[list.size()]);
    }

    /**
     * 获取下属机构
     *
     * @param organizeId
     * @param isAdmin
     * @return
     */
    private String[] getSubOrganizeIds(String organizeId, boolean isAdmin) {
        List<OrganizeEntity> data = organizeService.getList();
        if (!isAdmin) {
            data = JsonUtil.getJsonToList(ListToTreeUtil.treeWhere(organizeId, data), OrganizeEntity.class);
        }
        List<String> list = data.stream().map(t -> t.getId()).collect(Collectors.toList());
        return list.toArray(new String[list.size()]);
    }

    /**
     * 获取岗位
     *
     * @param userId
     * @return
     */
    private String[] getPositionId(String userId) {
        List<UserRelationEntity> data = userRelationService.getListByUserId(userId);
        List<String> list = data.stream().filter(m -> "Position".equals(m.getObjectType())).map(t -> t.getObjectId()).collect(Collectors.toList());
        return list.toArray(new String[list.size()]);
    }

}
