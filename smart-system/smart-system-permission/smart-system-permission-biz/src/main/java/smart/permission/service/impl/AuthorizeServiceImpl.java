package smart.permission.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.UserInfo;
import smart.base.model.button.ButtonModel;
import smart.base.model.column.ColumnModel;
import smart.base.model.module.ModuleModel;
import smart.base.model.resource.ResourceModel;
import smart.emnus.DbType;
import smart.emnus.SearchMethodEnum;
import smart.permission.entity.*;
import smart.permission.mapper.AuthorizeMapper;
import smart.permission.model.authorize.AuthorizeVO;
import smart.permission.model.authorize.ConditionModel;
import smart.permission.model.authorize.SaveAuthForm;
import smart.permission.model.authorize.SaveBatchForm;
import smart.permission.model.user.UserAllModel;
import smart.permission.service.*;
import smart.util.*;
import smart.util.type.AuthorizeType;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作权限
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AuthorizeServiceImpl extends ServiceImpl<AuthorizeMapper, AuthorizeEntity> implements AuthorizeService {

    @Autowired
    private UserRelationService userRelationService;
    @Autowired
    private DataSourceUtil dataSourceUtils;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private PositionService positionService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @Override
    public AuthorizeVO getAuthorize(boolean isCache) {
        List<ModuleModel> moduleList = new ArrayList<>();
        List<ButtonModel> buttonList = new ArrayList<>();
        List<ColumnModel> columnList = new ArrayList<>();
        List<ResourceModel> resourceList = new ArrayList<>();
        AuthorizeVO authorizeModel = new AuthorizeVO(moduleList, buttonList, columnList, resourceList);
        UserInfo userInfo = userProvider.get();
        if (isCache == true) {
            String cacheKey = cacheKeyUtil.getUserAuthorize() + userInfo.getUserId();
            if (!redisUtil.exists(cacheKey)) {
                authorizeModel = getAuthorize(userInfo.getIsAdministrator(), userInfo.getUserId());
                if (authorizeModel.getModuleList().size() != 0) {
                    redisUtil.insert(cacheKey, authorizeModel, 60);
                }
            } else {
                authorizeModel = JsonUtil.getJsonToBean(redisUtil.getString(cacheKey).toString(), AuthorizeVO.class);
            }
        } else {
            authorizeModel = getAuthorize(userInfo.getIsAdministrator(), userInfo.getUserId());
        }
        return authorizeModel;
    }

    @Override
    public AuthorizeVO getAuthorize(boolean isAdmin, String userId) {
        List<ModuleModel> moduleList;
        List<ButtonModel> buttonList;
        List<ColumnModel> columnList;
        List<ResourceModel> resourceList;
        if (!isAdmin) {
            QueryWrapper<UserRelationEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(UserRelationEntity::getUserId, userId);
            List<UserRelationEntity> list = userRelationService.list(queryWrapper);
            List<String> userRelationList = list.stream().map(m -> m.getObjectId()).collect(Collectors.toList());
            userRelationList.add(userId);
            StringBuilder objectId = new StringBuilder();
            for (int i = 0; i < userRelationList.size(); i++) {
                if (i != userRelationList.size() - 1) {
                    objectId.append("'" + userRelationList.get(i) + "',");
                } else {
                    objectId.append("'" + userRelationList.get(i) + "'");
                }
            }
            moduleList = this.baseMapper.findModule(objectId.toString());
            buttonList = this.baseMapper.findButton(objectId.toString());
            columnList = this.baseMapper.findColumn(objectId.toString());
            resourceList = this.baseMapper.findResource(objectId.toString());
        } else {
            moduleList = this.baseMapper.findModuleAdmin("1");
            buttonList = this.baseMapper.findButtonAdmin("1");
            columnList = this.baseMapper.findColumnAdmin("1");
            resourceList = this.baseMapper.findResourceAdmin("1");
        }
        return new AuthorizeVO(moduleList, buttonList, columnList, resourceList);
    }

    @Override
    public void save(String objectId, List<AuthorizeEntity> authorizeList) {
        String creatorUserId = userProvider.get().getUserId();
        if (DbType.ORACLE.getMessage().equals(dataSourceUtils.getDataType().toLowerCase())) {
            List<AuthorizeEntity> list = new ArrayList();
            for (int i = 0; i < authorizeList.size(); i++) {
                AuthorizeEntity object = authorizeList.get(i);
                object.setSortCode(Long.valueOf(i));
                object.setCreatorTime(new Date());
                list.add(object);
            }
            QueryWrapper<AuthorizeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(AuthorizeEntity::getObjectId, objectId).ne(AuthorizeEntity::getItemType, "portal");
            this.remove(queryWrapper);
            if (list.size() > 0) {
                this.baseMapper.savaBatchList(list);
            }
        } else {
            StringBuilder values = new StringBuilder();
            for (int i = 0; i < authorizeList.size(); i++) {
                AuthorizeEntity object = authorizeList.get(i);
                values.append("('" + RandomUtil.uuId() + "', ");
                values.append("'" + object.getItemType() + "', ");
                values.append("'" + object.getItemId() + "', ");
                values.append("'" + object.getObjectType() + "', ");
                values.append("'" + object.getObjectId() + "', ");
                values.append("'" + i + "', ");
                values.append("'" + DateUtil.getNow() + "', ");
                values.append("'" + creatorUserId + "'),");
            }
            QueryWrapper<AuthorizeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(AuthorizeEntity::getObjectId, objectId).ne(AuthorizeEntity::getItemType, "portal");
            this.remove(queryWrapper);
            if (!StringUtil.isEmpty(values.toString())) {
                values.deleteCharAt(values.length() - 1);
                this.baseMapper.saveBatch(values.toString());
            }
        }
    }

    @Override
    public void saveBatch(SaveBatchForm saveBatchForm) {
        try {
            String dbName = userProvider.get().getTenantDbConnectionString() == null ? dataSourceUtils.getDbName() : userProvider.get().getTenantDbConnectionString();
            String urll = dataSourceUtils.getUrl().replace("{dbName}", dbName);
            @Cleanup Connection conn = JdbcUtil.getConn(dataSourceUtils.getUserName(), dataSourceUtils.getPassword(), urll);
            @Cleanup PreparedStatement pstm = null;
            String sql = "";
            if (DbType.ORACLE.getMessage().equals(dataSourceUtils.getDataType().toLowerCase())) {
                sql = "INSERT INTO base_authorize (F_ID, F_ITEMTYPE, F_ITEMID, F_OBJECTTYPE, F_OBJECTID, F_SORTCODE, F_CREATORTIME, F_CREATORUSERID ) VALUES  (?,?,?,?,?,?,TO_DATE(?,'yyyy-mm-dd hh24:mi:ss'),?)";
            }else {
                sql = "INSERT INTO base_authorize (F_ID, F_ITEMTYPE, F_ITEMID, F_OBJECTTYPE, F_OBJECTID, F_SORTCODE, F_CREATORTIME, F_CREATORUSERID ) VALUES  (?,?,?,?,?,?,?,?)";
            }
            pstm = conn.prepareStatement(sql);
            conn.setAutoCommit(false);
            String creatorTime = DateUtil.getNow();
            String creatorUserId = userProvider.get().getUserId();
            int k = 0;
            List<AuthorizeEntity> objectList = new ArrayList<>();
            List<AuthorizeEntity> authorizeList = new ArrayList<>();
            String[] types = {AuthorizeType.USER, AuthorizeType.POSITION, AuthorizeType.ROLE, AuthorizeType.BUTTON, AuthorizeType.MODULE, AuthorizeType.COLUMN, AuthorizeType.RESOURCE};
            for (String str : types) {
                String[] ids = {};
                String type = "";
                if (AuthorizeType.USER.equals(str)) {
                    ids = saveBatchForm.getUserIds() != null ? saveBatchForm.getUserIds() : new String[]{};
                    type = AuthorizeType.USER;
                }
                if (AuthorizeType.POSITION.equals(str)) {
                    ids = saveBatchForm.getPositionIds() != null ? saveBatchForm.getPositionIds() : new String[]{};
                    type = AuthorizeType.POSITION;
                }
                if (AuthorizeType.ROLE.equals(str)) {
                    ids = saveBatchForm.getRoleIds() != null ? saveBatchForm.getRoleIds() : new String[]{};
                    type = AuthorizeType.ROLE;
                }
                if (AuthorizeType.BUTTON.equals(str)) {
                    ids = saveBatchForm.getButton() != null ? saveBatchForm.getButton() : new String[]{};
                    type = AuthorizeType.BUTTON;
                }
                if (AuthorizeType.MODULE.equals(str)) {
                    ids = saveBatchForm.getModule() != null ? saveBatchForm.getModule() : new String[]{};
                    type = AuthorizeType.MODULE;
                }
                if (AuthorizeType.COLUMN.equals(str)) {
                    ids = saveBatchForm.getColumn() != null ? saveBatchForm.getColumn() : new String[]{};
                    type = AuthorizeType.COLUMN;
                }
                if (AuthorizeType.RESOURCE.equals(str)) {
                    ids = saveBatchForm.getResource() != null ? saveBatchForm.getResource() : new String[]{};
                    type = AuthorizeType.RESOURCE;
                }
                for (String str1 : ids) {
                    AuthorizeEntity entity = new AuthorizeEntity();
                    if (AuthorizeType.USER.equals(str) || AuthorizeType.POSITION.equals(str) || AuthorizeType.ROLE.equals(str)) {
                        entity.setObjectType(type);
                        entity.setObjectId(str1);
                        objectList.add(entity);
                    }
                    if (AuthorizeType.BUTTON.equals(str) || AuthorizeType.MODULE.equals(str) || AuthorizeType.COLUMN.equals(str) || AuthorizeType.RESOURCE.equals(str)) {
                        entity.setItemType(type);
                        entity.setItemId(str1);
                        authorizeList.add(entity);
                    }
                }
            }

            List<UserAllModel> userList = userService.getAll();
            for (AuthorizeEntity objectItem : objectList) {
                for (AuthorizeEntity entityItem : authorizeList) {
                    pstm.setString(1, RandomUtil.uuId());
                    pstm.setString(2, entityItem.getItemType());
                    pstm.setString(3, entityItem.getItemId());
                    pstm.setString(4, objectItem.getObjectType());
                    pstm.setString(5, objectItem.getObjectId());
                    pstm.setInt(6, k);
                    pstm.setString(7, creatorTime);
                    pstm.setString(8, creatorUserId);
                    pstm.addBatch();
                    k++;
                    if (AuthorizeType.ROLE.equals(objectItem.getObjectType())) {
                        RoleEntity role = roleService.getInfo(objectItem.getObjectId());
                        for (UserAllModel model : userList) {
                            if (role!=null&&role.getFullName().equals(model.getRoleName()) && redisUtil.exists(cacheKeyUtil.getLoginOnline() + model.getId())) {
                                String oldToken = String.valueOf(redisUtil.getString(cacheKeyUtil.getLoginOnline() + model.getId()));
                                redisUtil.remove(oldToken);
                            }
                        }
                    }
                    if (AuthorizeType.USER.equals(objectItem.getObjectType())) {
                        UserEntity userEntity = userService.getInfo(objectItem.getObjectId());
                        for (UserAllModel model : userList) {
                            if (userEntity.getId().equals(model.getId()) && redisUtil.exists(cacheKeyUtil.getLoginOnline() + model.getId())) {
                                String oldToken = String.valueOf(redisUtil.getString(cacheKeyUtil.getLoginOnline() + model.getId()));
                                redisUtil.remove(oldToken);
                            }
                        }
                    }
                    if (AuthorizeType.POSITION.equals(objectItem.getObjectType())) {
                        PositionEntity positionEntity = positionService.getInfo(objectItem.getObjectId());
                        for (UserAllModel model : userList) {
                            if (positionEntity.getFullName().equals(model.getPositionName()) && redisUtil.exists(cacheKeyUtil.getLoginOnline() + model.getId())) {
                                String oldToken = String.valueOf(redisUtil.getString(cacheKeyUtil.getLoginOnline() + model.getId()));
                                redisUtil.remove(oldToken);
                            }
                        }
                    }
                }
            }
            pstm.executeBatch();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("权限报错:" + e.getMessage());
        }
    }

    @Override
    public List<AuthorizeEntity> getListByUserId(boolean isAdmin, String userId) {
        if (!isAdmin) {
            QueryWrapper<UserRelationEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(UserRelationEntity::getUserId, userId);
            List<UserRelationEntity> list = userRelationService.list(queryWrapper);
            List<String> userRelationList = list.stream().map(u -> u.getObjectId()).collect(Collectors.toList());
            userRelationList.add(userId);
            QueryWrapper<AuthorizeEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().in(AuthorizeEntity::getObjectId, userRelationList);
            return this.list(wrapper);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<AuthorizeEntity> getListByObjectId(String objectId) {
        QueryWrapper<AuthorizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AuthorizeEntity::getObjectId, objectId);
        return this.list(queryWrapper);
    }

    @Override
    public List<AuthorizeEntity> getListByObjectAndItem(String itemId, String objectType) {
        QueryWrapper<AuthorizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AuthorizeEntity::getObjectType, objectType).eq(AuthorizeEntity::getItemId, itemId);
        return this.list(queryWrapper);
    }

    @Override
    public List<AuthorizeEntity> getListByItemId(String itemId) {
        QueryWrapper<AuthorizeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(AuthorizeEntity::getItemId, itemId);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional
    public void saveAuth(String itemId, SaveAuthForm saveAuthForm) {
        String creatorUserId = userProvider.get().getUserId();
        if (DbType.ORACLE.getMessage().equals(dataSourceUtils.getDataType().toLowerCase())) {
            List<AuthorizeEntity> list = new ArrayList<>();
            for (int i = 0; i < saveAuthForm.getObjectId().length; i++) {
                AuthorizeEntity authorizeEntity = new AuthorizeEntity();
                authorizeEntity.setId(RandomUtil.uuId());
                authorizeEntity.setItemType("portal");
                authorizeEntity.setItemId(itemId);
                authorizeEntity.setObjectType(saveAuthForm.getObjectType());
                authorizeEntity.setObjectId(saveAuthForm.getObjectId()[i]);
                authorizeEntity.setSortCode(Long.valueOf(i));
                authorizeEntity.setCreatorTime(new Date());
                authorizeEntity.setCreatorUserId(creatorUserId);
                list.add(authorizeEntity);
            }
            QueryWrapper<AuthorizeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(AuthorizeEntity::getItemId, itemId);
            this.remove(queryWrapper);
            if (list.size() > 0) {
                this.baseMapper.savaBatchList(list);
            }
        } else {
            StringBuilder values = new StringBuilder();
            for (int i = 0; i < saveAuthForm.getObjectId().length; i++) {
                values.append("('" + RandomUtil.uuId() + "', ");
                values.append("'portal', ");
                values.append("'" + itemId + "', ");
                values.append("'" + saveAuthForm.getObjectType() + "', ");
                values.append("'" + saveAuthForm.getObjectId()[i] + "', ");
                values.append("'" + i + "', ");
                values.append("'" + DateUtil.getNow() + "', ");
                values.append("'" + creatorUserId + "'),");
            }
            QueryWrapper<AuthorizeEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(AuthorizeEntity::getItemId, itemId);
            this.remove(queryWrapper);
            if (!StringUtil.isEmpty(values.toString())) {
                values.deleteCharAt(values.length() - 1);
                this.baseMapper.saveBatch(values.toString());
            }
        }
    }

    /**
     * 获取条件过滤
     *
     * @param userInfo 用户信息
     * @param moduleId 功能模块Id
     * @return
     */
    @Override
    public QueryWrapper<T> getCondition(Object obj, UserInfo userInfo, String moduleId) {
        QueryWrapper<T> queryWhere=(QueryWrapper<T>)obj;
        AuthorizeVO model = this.getAuthorize(true);
        List<ResourceModel> resourceList = model.getResourceList().stream().filter(m -> m.getModuleId().equals(moduleId)).collect(Collectors.toList());
        //拼接计数
        int t=0;
        for (ResourceModel item : resourceList) {
            List<ConditionModel> conditionModelList = JsonUtil.getJsonToList(item.getConditionJson(), ConditionModel.class);
            for (int i = 0; i < conditionModelList.size(); i++) {
                ConditionModel conditionItem = conditionModelList.get(i);
                for (int k = 0; k < conditionItem.getGroups().size(); k++) {
                    ConditionModel.ConditionItemModel fieldItem = conditionItem.getGroups().get(k);
                    String itemValue = fieldItem.getValue();
                    String itemMethod = fieldItem.getOp();
                    if ("@userId".equals(itemValue)
                            ||"@organizeId".equals(itemValue)
                            ||("@subordinateId".equals(itemValue)&&userInfo.getSubordinateIds() != null && userInfo.getSubordinateIds().length > 0)
                            ||itemMethod.equals(SearchMethodEnum.Equal.getMessage())
                            ||itemMethod.equals(SearchMethodEnum.NotEqual.getMessage())
                            ||itemMethod.equals(SearchMethodEnum.LessThan.getMessage())
                            ||itemMethod.equals(SearchMethodEnum.LessThanOrEqual.getMessage())
                            ||itemMethod.equals(SearchMethodEnum.GreaterThan.getMessage())
                            ||itemMethod.equals(SearchMethodEnum.GreaterThanOrEqual.getMessage())
                    ) { //当前用户Id
                        t=1;
                    }
                }
            }
        }
        if(t==1){
            queryWhere.and(tw->{
                for (ResourceModel item : resourceList) {
                    List<ConditionModel> conditionModelList = JsonUtil.getJsonToList(item.getConditionJson(), ConditionModel.class);
                    for (int i = 0; i < conditionModelList.size(); i++) {
                        ConditionModel conditionItem = conditionModelList.get(i);
                        for (int k = 0; k < conditionItem.getGroups().size(); k++) {
                            ConditionModel.ConditionItemModel fieldItem = conditionItem.getGroups().get(k);
                            String itemField = fieldItem.getField();
                            String itemValue = fieldItem.getValue();
                            String itemMethod = fieldItem.getOp();
                            if ("and".equals(conditionItem.getLogic())) {

                                if ("@userId".equals(itemValue)) { //当前用户Id
                                    tw.eq(itemField, userInfo.getUserId());
                                } else if ("@organizeId".equals(itemValue)) { //当前部门Id
                                    tw.eq(itemField, userInfo.getOrganizeId());
                                } else if ("@subordinateId".equals(itemValue)) { //我的下属
                                    if (userInfo.getSubordinateIds() != null && userInfo.getSubordinateIds().length > 0) {
                                        tw.in(itemField, userInfo.getSubordinateIds());
                                    }
                                } else {//任意文本
                                    if (itemMethod.equals(SearchMethodEnum.Equal.getMessage())) {
                                        tw.eq(itemField, itemValue);
                                    } else if (itemMethod.equals(SearchMethodEnum.NotEqual.getMessage())) {
                                        tw.ne(itemField, itemValue);
                                    } else if (itemMethod.equals(SearchMethodEnum.LessThan.getMessage())) {
                                        tw.lt(itemField, itemValue);
                                    } else if (itemMethod.equals(SearchMethodEnum.LessThanOrEqual.getMessage())) {
                                        tw.le(itemField, itemValue);
                                    } else if (itemMethod.equals(SearchMethodEnum.GreaterThan.getMessage())) {
                                        tw.gt(itemField, itemValue);
                                    } else if (itemMethod.equals(SearchMethodEnum.GreaterThanOrEqual.getMessage())) {
                                        tw.ge(itemField, itemValue);
                                    }
                                }
                            }else{
                                if ("@userId".equals(itemValue)) { //当前用户Id
                                    tw.or(
                                            qw -> qw.eq(itemField, userInfo.getUserId())
                                    );
                                } else if ("@organizeId".equals(itemValue)) { //当前部门Id
                                    tw.or(
                                            qw -> qw.eq(itemField, userInfo.getOrganizeId())
                                    );
                                } else if ("@subordinateId".equals(itemValue)) { //我的下属
                                    if (userInfo.getSubordinateIds() != null && userInfo.getSubordinateIds().length > 0) {
                                        tw.or(
                                                qw -> qw.in(itemField, userInfo.getSubordinateIds())
                                        );
                                    }
                                } else {//任意文本
                                    if (itemMethod.equals(SearchMethodEnum.Equal.getMessage())) {
                                        tw.or(
                                                qw -> qw.eq(itemField, userInfo.getSubordinateIds())
                                        );
                                    } else if (itemMethod.equals(SearchMethodEnum.NotEqual.getMessage())) {
                                        tw.or(
                                                qw -> qw.ne(itemField, itemValue)
                                        );
                                    } else if (itemMethod.equals(SearchMethodEnum.LessThan.getMessage())) {
                                        tw.or(
                                                qw -> qw.lt(itemField, itemValue)
                                        );
                                    } else if (itemMethod.equals(SearchMethodEnum.LessThanOrEqual.getMessage())) {
                                        tw.or(
                                                qw -> qw.le(itemField, itemValue)
                                        );
                                    } else if (itemMethod.equals(SearchMethodEnum.GreaterThan.getMessage())) {
                                        tw.or(
                                                qw -> qw.gt(itemField, itemValue)
                                        );
                                    } else if (itemMethod.equals(SearchMethodEnum.GreaterThanOrEqual.getMessage())) {
                                        tw.or(
                                                qw -> qw.ge(itemField, itemValue)
                                        );
                                    }
                                }
                            }
                        }

                    }
                }
            });
        }
        return queryWhere;
    }


    /**
     * 获取条件过滤
     *
     * @param userInfo 用户信息
     * @param moduleId 功能模块Id
     * @return
     */
    @Override
    public String getConditionSql(UserInfo userInfo, String moduleId) {
        StringBuilder queryWhere = new StringBuilder();
        AuthorizeVO model = this.getAuthorize(true);
        List<ResourceModel> resourceList = model.getResourceList().stream().filter(m -> m.getModuleId().equals(moduleId)).collect(Collectors.toList());
        for (ResourceModel item : resourceList) {
            StringBuilder itemWhere = new StringBuilder();
            List<ConditionModel> conditionModelList = JsonUtil.getJsonToList(item.getConditionJson(), ConditionModel.class);
            for (int i = 0; i < conditionModelList.size(); i++) {
                StringBuilder subWhere = new StringBuilder();
                ConditionModel conditionItem = conditionModelList.get(i);
                for (int k = 0; k < conditionItem.getGroups().size(); k++) {
                    ConditionModel.ConditionItemModel fieldItem = conditionItem.getGroups().get(k);
                    if (conditionItem.getGroups().indexOf(fieldItem) > 0) {
                        subWhere.append(" AND ");
                    }
                    String itemField = fieldItem.getField();
                    String itemValue = fieldItem.getValue();
                    String itemMethod = fieldItem.getOp();
                    //当前用户Id
                    if ("@userId".equals(itemValue)) {
                        subWhere.append(itemField + " = '" + userInfo.getUserId() + "' ");
                    } else if ("@organizeId".equals(itemValue)) {
                        //当前部门Id
                        subWhere.append(itemField + " = '" + userInfo.getOrganizeId() + "' ");
                    } else if ("@subordinateId".equals(itemValue)) {
                        //我的下属
                        StringBuilder ids = new StringBuilder();
                        for (int m = 0; m < userInfo.getSubordinateIds().length; m++) {
                            if (m != userInfo.getSubordinateIds().length - 1) {
                                ids.append("'" + userInfo.getSubordinateIds()[m] + "',");
                            } else {
                                ids.append("'" + userInfo.getSubordinateIds()[m] + "'");
                            }
                        }
                        if (ids.length() > 0) {
                            subWhere.append(itemField + " in( " + ids + " )" + " ");
                        }
                    } else {//任意文本
                        if (itemMethod.equals(SearchMethodEnum.Equal.getMessage())) {
                            subWhere.append(itemField + " = " + itemValue);
                        } else if (itemMethod.equals(SearchMethodEnum.NotEqual.getMessage())) {
                            subWhere.append(itemField + " <> " + itemValue);
                        } else if (itemMethod.equals(SearchMethodEnum.LessThan.getMessage())) {
                            subWhere.append(itemField + " < " + itemValue);
                        } else if (itemMethod.equals(SearchMethodEnum.LessThanOrEqual.getMessage())) {
                            subWhere.append(itemField + " <= " + itemValue);
                        } else if (itemMethod.equals(SearchMethodEnum.GreaterThan.getMessage())) {
                            subWhere.append(itemField + " > " + itemValue);
                        } else if (itemMethod.equals(SearchMethodEnum.GreaterThanOrEqual.getMessage())) {
                            subWhere.append(itemField + " >= " + itemValue);
                        }
                    }
                }
                if (conditionModelList.indexOf(conditionItem) > 0 && subWhere.length() != 0) {
                    itemWhere.append(" " + conditionItem.getLogic() + " ");
                }
                itemWhere.append(subWhere);
            }
            if (queryWhere.length() == 0) {
                queryWhere.append(" AND (" + itemWhere + ") ");
            } else {
                queryWhere.append(" OR (" + itemWhere + ") ");
            }
        }
        return queryWhere.toString();
    }

}
