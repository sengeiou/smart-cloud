package smart.permission.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.util.JsonUtil;
import smart.util.RandomUtil;
import smart.util.StringUtil;
import smart.base.ActionResult;
import smart.base.entity.*;
import smart.base.service.*;
import smart.permission.entity.*;
import smart.permission.model.authorize.*;
import smart.permission.service.*;
import smart.util.treeutil.ListToTreeUtil;
import smart.util.treeutil.SumTree;
import smart.util.treeutil.TreeDotUtils;
import smart.util.CacheKeyUtil;
import smart.util.RedisUtil;
import smart.permission.model.user.UserAllModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 操作权限
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "操作权限", value = "Authorize")
@RestController
@RequestMapping("/Permission/Authority")
public class AuthorityController {

    @Autowired
    private ModuleService moduleService;
    @Autowired
    private ModuleButtonService buttonService;
    @Autowired
    private ModuleColumnService columnService;
    @Autowired
    private ModuleDataAuthorizeSchemeService schemeService;
    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private OrganizeService organizeService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;

    /**
     * 权限数据
     *
     * @param objectId 对象主键
     * @return
     */
    @ApiOperation("获取权限数据")
    @GetMapping("/{objectId}")
    public ActionResult data(@PathVariable("objectId") String objectId) {

        List<ModuleEntity> moduleList = moduleService.getList().stream().filter(
                m -> "1".equals(String.valueOf(m.getEnabledMark()))
        ).collect(Collectors.toList());

        List<ModuleButtonEntity> moduleButtonList = buttonService.getList().stream().filter(
                m -> "1".equals(String.valueOf(m.getEnabledMark()))
        ).collect(Collectors.toList());

        List<ModuleColumnEntity> moduleColumnList = columnService.getList().stream().filter(
                m -> "1".equals(String.valueOf(m.getEnabledMark()))
        ).collect(Collectors.toList());

        List<ModuleDataAuthorizeSchemeEntity> moduleDataSchemeList = schemeService.getList().stream().filter(
                m -> "1".equals(String.valueOf(m.getEnabledMark()))
        ).collect(Collectors.toList());

        AuthorizeVO authorizeModel = authorizeService.getAuthorize(true);
        List<AuthorizeEntity> list = authorizeService.list(new QueryWrapper<AuthorizeEntity>().lambda().eq(AuthorizeEntity::getObjectId, objectId));
        List<String> checkList = list.stream().filter(
                t -> t.getItemId() != null).map(t -> t.getItemId()
        ).collect(Collectors.toList());

        AuthorizeDataVO vo = new AuthorizeDataVO();
        vo.setModule(this.module(moduleList, checkList, authorizeModel));
        vo.setButton(this.moduleButton(moduleList, moduleButtonList, checkList, authorizeModel));
        vo.setColumn(this.moduleColumn(moduleList, moduleColumnList, checkList, authorizeModel));
        vo.setResource(this.resourceData(moduleList, moduleDataSchemeList, checkList, authorizeModel));
        return ActionResult.success(vo);
    }


    /**
     * 权限数据
     *
     * @param objectId 对象主键
     * @return
     */
    @ApiOperation("获取岗位/角色/用户权限树形结构")
    @GetMapping("/Data/{objectId}")
    public ActionResult getData(@PathVariable("objectId") String objectId) {

        List<ModuleEntity> moduleList = moduleService.getList().stream().filter(
                m -> "1".equals(String.valueOf(m.getEnabledMark()))
        ).collect(Collectors.toList());

        List<ModuleButtonEntity> moduleButtonList = buttonService.getList().stream().filter(
                m -> "1".equals(String.valueOf(m.getEnabledMark()))
        ).collect(Collectors.toList());

        List<ModuleColumnEntity> moduleColumnList = columnService.getList().stream().filter(
                m -> "1".equals(String.valueOf(m.getEnabledMark()))
        ).collect(Collectors.toList());

        List<ModuleDataAuthorizeSchemeEntity> moduleDataSchemeList = schemeService.getList().stream().filter(
                m -> "1".equals(String.valueOf(m.getEnabledMark()))
        ).collect(Collectors.toList());

        AuthorizeVO authorizeModel = authorizeService.getAuthorize(true);
        List<AuthorizeEntity> list = authorizeService.list(new QueryWrapper<AuthorizeEntity>().lambda().eq(AuthorizeEntity::getObjectId, objectId));
        List<String> checkList = list.stream().filter(
                t -> t.getItemId() != null).map(t -> t.getItemId()
        ).collect(Collectors.toList());

        AuthorizeDataVO vo = new AuthorizeDataVO();
        vo.setModule(this.module(moduleList, checkList, authorizeModel));
        vo.setButton(this.moduleButton(moduleList, moduleButtonList, checkList, authorizeModel));
        vo.setColumn(this.moduleColumn(moduleList, moduleColumnList, checkList, authorizeModel));
        vo.setResource(this.resourceData(moduleList, moduleDataSchemeList, checkList, authorizeModel));
        return ActionResult.success(vo);
    }


    /**
     * 权限数据
     *
     * @param objectId 对象主键
     * @return
     */
    @ApiOperation("获取岗位/角色/用户权限树形结构")
    @PostMapping("/Data/{objectId}/Values")
    public ActionResult getValuesData(@PathVariable("objectId") String objectId, @RequestBody DataValuesQuery dataValuesQuery) {

        AuthorizeVO authorizeModel = authorizeService.getAuthorize(true);
        List<AuthorizeEntity> list = authorizeService.list(new QueryWrapper<AuthorizeEntity>().lambda().eq(AuthorizeEntity::getObjectId, objectId));
        List<String> checkList = list.stream().filter(
                t -> t.getItemId() != null).map(t -> t.getItemId()
        ).collect(Collectors.toList());
        if (!StringUtil.isEmpty(dataValuesQuery.getType())) {
            switch (dataValuesQuery.getType()) {
                case "module":
                    List<ModuleEntity> moduleList = moduleService.getList().stream().filter(
                            m -> "1".equals(String.valueOf(m.getEnabledMark()))
                    ).collect(Collectors.toList());
                    AuthorizeDataReturnVO dataReturnVO = this.module(moduleList, checkList, authorizeModel);
                    return ActionResult.success(dataReturnVO);
                case "button":
                    List<ModuleEntity> moduleList1 = moduleService.getList().stream().filter(
                            m -> "1".equals(String.valueOf(m.getEnabledMark()))
                    ).collect(Collectors.toList());
                    //挑选出的list
                    List<ModuleEntity> selectList1 = new ArrayList<>();
                    if (!StringUtil.isEmpty(dataValuesQuery.getModuleIds())) {
                        String[] moduleId1 = dataValuesQuery.getModuleIds().split(",");
                        for (String id : moduleId1) {
                            selectList1.addAll(moduleList1.stream().filter(
                                    t -> id.equals(t.getId())
                            ).collect(Collectors.toList()));
                        }
                    }
                    List<ModuleButtonEntity> moduleButtonList = buttonService.getList().stream().filter(
                            m -> "1".equals(String.valueOf(m.getEnabledMark()))
                    ).collect(Collectors.toList());
                    AuthorizeDataReturnVO dataReturnVO1 = this.moduleButton(selectList1, moduleButtonList, checkList, authorizeModel);
                    return ActionResult.success(dataReturnVO1);


                case "column":
                    List<ModuleEntity> moduleList2 = moduleService.getList().stream().filter(
                            m -> "1".equals(String.valueOf(m.getEnabledMark()))
                    ).collect(Collectors.toList());
                    //挑选出的list
                    List<ModuleEntity> selectList2 = new ArrayList<>();
                    if (!StringUtil.isEmpty(dataValuesQuery.getModuleIds())) {
                        String[] moduleId1 = dataValuesQuery.getModuleIds().split(",");
                        for (String id : moduleId1) {
                            selectList2.addAll(moduleList2.stream().filter(
                                    t -> id.equals(t.getId())
                            ).collect(Collectors.toList()));
                        }
                    }
                    List<ModuleColumnEntity> moduleColumnList = columnService.getList().stream().filter(
                            m -> "1".equals(String.valueOf(m.getEnabledMark()))
                    ).collect(Collectors.toList());
                    AuthorizeDataReturnVO dataReturnVO2 = this.moduleColumn(selectList2, moduleColumnList, checkList, authorizeModel);
                    return ActionResult.success(dataReturnVO2);

                case "resource":
                    List<ModuleEntity> moduleList3 = moduleService.getList().stream().filter(
                            m -> "1".equals(String.valueOf(m.getEnabledMark()))
                    ).collect(Collectors.toList());
                    //挑选出的list
                    List<ModuleEntity> selectList3 = new ArrayList<>();
                    if (!StringUtil.isEmpty(dataValuesQuery.getModuleIds())) {
                        String[] moduleId1 = dataValuesQuery.getModuleIds().split(",");
                        for (String id : moduleId1) {
                            selectList3.addAll(moduleList3.stream().filter(
                                    t -> id.equals(t.getId())
                            ).collect(Collectors.toList()));
                        }
                    }
                    List<ModuleDataAuthorizeSchemeEntity> moduleDataSchemeList = schemeService.getList().stream().filter(
                            m -> "1".equals(String.valueOf(m.getEnabledMark()))
                    ).collect(Collectors.toList());
                    AuthorizeDataReturnVO dataReturnVO3 = this.resourceData(selectList3, moduleDataSchemeList, checkList, authorizeModel);
                    return ActionResult.success(dataReturnVO3);

                default:
            }
        }
        return ActionResult.fail("类型不能为空");
    }


    /**
     * 对象数据
     *
     * @return
     */
    @ApiOperation("获取对象权限数据")
    @GetMapping("/DataObject")
    public ActionResult dataObject() {
        List<RoleEntity> roleData = roleService.getList().stream().filter(m -> "1".equals(String.valueOf(m.getEnabledMark()))).collect(Collectors.toList());
        List<OrganizeEntity> organizeData = organizeService.getList().stream().filter(m -> "1".equals(String.valueOf(m.getEnabledMark()))).collect(Collectors.toList());
        List<PositionEntity> positionData = positionService.getList().stream().filter(m -> "1".equals(String.valueOf(m.getEnabledMark()))).collect(Collectors.toList());
        List<UserEntity> userData = userService.getList().stream().filter(m -> "1".equals(String.valueOf(m.getEnabledMark()))).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("role", this.roleTree(roleData));
        map.put("position", this.positionTree(organizeData, positionData));
        map.put("user", this.userTree(organizeData, userData));
        return ActionResult.success(map);
    }


    /**
     * 对象数据
     *
     * @return
     */
    @ApiOperation("获取功能权限数据")
    @GetMapping("/Model/{itemId}/{objectType}")
    public ActionResult getObjectAuth(@PathVariable("itemId") String itemId, @PathVariable("objectType") String objectType) {
        List<AuthorizeEntity> authorizeList = authorizeService.getListByObjectAndItem(itemId, objectType);
        List<String> ids = authorizeList.stream().map(u -> u.getObjectId()).collect(Collectors.toList());
        AuthorizeItemObjIdsVO vo = new AuthorizeItemObjIdsVO();
        vo.setIds(ids);
        return ActionResult.success(vo);
    }

    /**
     * 设置/更新功能权限
     *
     * @param itemId 对象主键
     * @return
     */
    @ApiOperation("设置/更新功能权限")
    @PutMapping("/Model/{itemId}")
    public ActionResult save(@PathVariable("itemId") String itemId, @RequestBody SaveAuthForm saveAuthForm) {
        authorizeService.saveAuth(itemId, saveAuthForm);
        return ActionResult.success("操作成功");
    }


    /**
     * 保存
     *
     * @param objectId 对象主键
     * @return
     */
    @ApiOperation("保存权限")
    @PutMapping("/Data/{objectId}")
    public ActionResult save(@PathVariable("objectId") String objectId, @RequestBody AuthorizeDataUpForm authorizeDataUpForm) {
        List<AuthorizeEntity> authorizeList = new ArrayList<>();
        for (String str : authorizeDataUpForm.getButton()) {
            AuthorizeEntity entity = new AuthorizeEntity();
            entity.setObjectId(objectId);
            entity.setId(RandomUtil.uuId());
            entity.setItemId(str);
            entity.setItemType("button");
            entity.setObjectType(authorizeDataUpForm.getObjectType());
            authorizeList.add(entity);
        }
        for (String str : authorizeDataUpForm.getModule()) {
            AuthorizeEntity entity = new AuthorizeEntity();
            entity.setObjectId(objectId);
            entity.setId(RandomUtil.uuId());
            entity.setItemId(str);
            entity.setItemType("module");
            entity.setObjectType(authorizeDataUpForm.getObjectType());
            authorizeList.add(entity);
        }
        for (String str : authorizeDataUpForm.getColumn()) {
            AuthorizeEntity entity = new AuthorizeEntity();
            entity.setObjectId(objectId);
            entity.setId(RandomUtil.uuId());
            entity.setItemId(str);
            entity.setItemType("column");
            entity.setObjectType(authorizeDataUpForm.getObjectType());
            authorizeList.add(entity);
        }
        for (String str : authorizeDataUpForm.getResource()) {
            AuthorizeEntity entity = new AuthorizeEntity();
            entity.setObjectId(objectId);
            entity.setId(RandomUtil.uuId());
            entity.setItemId(str);
            entity.setItemType("resource");
            entity.setObjectType(authorizeDataUpForm.getObjectType());
            authorizeList.add(entity);
        }
        authorizeService.save(objectId, authorizeList);
        List<UserAllModel> userList = userService.getAll();
        if ("Role".equals(authorizeDataUpForm.getObjectType())) {
            RoleEntity role = roleService.getInfo(objectId);
            for (UserAllModel model : userList) {
                if (role.getFullName().equals(model.getRoleName()) && redisUtil.exists(cacheKeyUtil.getLoginOnline() + model.getId())) {
                    String oldToken = String.valueOf(redisUtil.getString(cacheKeyUtil.getLoginOnline() + model.getId()));
                    redisUtil.remove(oldToken);
                }
            }
        }
        if ("User".equals(authorizeDataUpForm.getObjectType())) {
            UserEntity userEntity = userService.getInfo(objectId);
            for (UserAllModel model : userList) {
                if (userEntity.getId().equals(model.getId()) && redisUtil.exists(cacheKeyUtil.getLoginOnline() + model.getId())) {
                    String oldToken = String.valueOf(redisUtil.getString(cacheKeyUtil.getLoginOnline() + model.getId()));
                    redisUtil.remove(oldToken);
                }
            }
        }
        if ("Position".equals(authorizeDataUpForm.getObjectType())) {
            PositionEntity positionEntity = positionService.getInfo(objectId);
            for (UserAllModel model : userList) {
                if (positionEntity.getFullName().equals(model.getPositionName()) && redisUtil.exists(cacheKeyUtil.getLoginOnline() + model.getId())) {
                    String oldToken = String.valueOf(redisUtil.getString(cacheKeyUtil.getLoginOnline() + model.getId()));
                    redisUtil.remove(oldToken);
                }
            }
        }
        return ActionResult.success("操作成功");
    }

    /**
     * 保存批量
     *
     * @return
     */
    @ApiOperation("批量保存权限")
    @PostMapping("/Data/Batch")
    public ActionResult saveBatch(@RequestBody SaveBatchForm saveBatchForm) {
        authorizeService.saveBatch(saveBatchForm);
        return ActionResult.success("操作成功");
    }

    /**
     * 功能权限
     *
     * @param moduleList     功能
     * @param checkList      已有权限
     * @param authorizeModel 权限集合
     * @return
     */
    AuthorizeDataReturnVO module(List<ModuleEntity> moduleList, List<String> checkList, AuthorizeVO authorizeModel) {
        List<AuthorizeDataModel> treeList = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        List<String> all = new ArrayList<>();
        for (ModuleEntity entity : moduleList) {
            //添加所有id
            all.add(entity.getId());
            if (authorizeModel.getModuleList().stream().filter(t -> t.getId().equals(entity.getId())).count() != 0) {
                AuthorizeDataModel module = new AuthorizeDataModel();
                module.setId(entity.getId());
                module.setFullName(entity.getFullName());
                module.setParentId(entity.getParentId());
                module.setIcon(entity.getIcon());
                module.setSortCode(entity.getSortCode());
                module.setCheckstate(checkList.contains(module.getId()) ? 1 : 0);
                if (module.getCheckstate() == 1) {
                    ids.add(module.getId());
                }
                module.setShowcheck(true);
                treeList.add(module);
            }
        }
        treeList = treeList.stream().sorted(Comparator.comparing(AuthorizeDataModel::getSortCode)).collect(Collectors.toList());
        List<SumTree<AuthorizeDataModel>> list = TreeDotUtils.convertListToTreeDot(treeList);

        //去除菜单状态禁用下其子节点的显示
        Iterator<SumTree<AuthorizeDataModel>> iterator = list.iterator();
        while (iterator.hasNext()) {
            SumTree<AuthorizeDataModel> menuTreeVO = iterator.next();
            if (!"-1".equals(menuTreeVO.getParentId())) {
                iterator.remove();
            }
        }
        AuthorizeDataReturnVO vo = new AuthorizeDataReturnVO();
        List<AuthorizeDataReturnModel> listVO = JsonUtil.getJsonToList(list, AuthorizeDataReturnModel.class);
        listVO = listVO.stream().sorted(Comparator.comparing(AuthorizeDataReturnModel::getSortCode)).collect(Collectors.toList());
        vo.setList(listVO);
        vo.setIds(ids);
        vo.setAll(all);
        return vo;
    }

    /**
     * 按钮权限
     *
     * @param moduleList       功能
     * @param moduleButtonList 按钮
     * @param checkList        已有权限
     * @param authorizeModel   权限集合
     * @return
     */
    AuthorizeDataReturnVO moduleButton(List<ModuleEntity> moduleList, List<ModuleButtonEntity> moduleButtonList, List<String> checkList, AuthorizeVO authorizeModel) {

        List<ModuleEntity> data = new ArrayList<>();
        //过滤权限后的按钮
        List<ModuleButtonEntity> filterBtnList = JsonUtil.getJsonToList(authorizeModel.getButtonList(), ModuleButtonEntity.class);
        //获取过滤后按钮的直接上级
        for (int i = 0; i < filterBtnList.size(); i++) {
            ModuleButtonEntity button = filterBtnList.get(i);
            ModuleEntity entity = moduleList.stream().filter(t -> t.getId().equals(button.getModuleId())).findFirst().orElse(null);
            if (entity != null && data.stream().filter(t -> t.equals(entity)).count() == 0) {
                data.add(entity);
            }
        }
        //菜单第一次排序
        data=data.stream().sorted(Comparator.comparing(ModuleEntity::getSortCode)).collect(Collectors.toList());
        List<AuthorizeDataModel> treeList = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        List<String> all = new ArrayList<>();
        for (ModuleEntity entity : data) {
            //添加所有id
            all.add(entity.getId());
            if (authorizeModel.getModuleList().stream().filter(t -> t.getId().equals(entity.getId())).count() != 0) {
                AuthorizeDataModel module = new AuthorizeDataModel();
                module.setId(entity.getId());
                module.setFullName(entity.getFullName());
                module.setParentId(entity.getParentId());
                module.setIcon(entity.getIcon());
                module.setType("web");
                module.setCheckstate(checkList.contains(module.getId()) ? 1 : 0);
                module.setShowcheck(true);
                if (entity.getType() == 2 || entity.getType() == 3) {
                    List<ModuleButtonEntity> buttonList = moduleButtonList.stream().filter(t -> t.getModuleId().equals(entity.getId())).collect(Collectors.toList());
                    if (buttonList.size() == 0) {
                        continue;
                    }
                    for (ModuleButtonEntity buttonEntity : buttonList) {
                        if (authorizeModel.getButtonList().stream().filter(t -> t.getId().equals(buttonEntity.getId())).count() != 0) {
                            AuthorizeDataModel button = new AuthorizeDataModel();
                            button.setId(buttonEntity.getId());
                            button.setFullName(buttonEntity.getFullName());
                            button.setParentId("-1".equals(buttonEntity.getParentId()) ? entity.getId() : buttonEntity.getParentId());
                            button.setIcon(buttonEntity.getIcon());
                            button.setCheckstate(checkList.contains(button.getId()) ? 1 : 0);
                            if (button.getCheckstate() == 1) {
                                ids.add(button.getId());
                            }
                            //添加所有id
                            all.add(button.getId());
                            button.setShowcheck(true);
                            button.setModuleId(entity.getId());
                            treeList.add(button);
                        }
                    }
                }
                treeList.add(module);
            }
        }
        List<AuthorizeDataModel> result = JsonUtil.getJsonToList(ListToTreeUtil.treeWhere(data, moduleList), AuthorizeDataModel.class);
        List<AuthorizeDataModel> resultall = new ArrayList<>();
        resultall.addAll(treeList);
        for (int i = 0; i < result.size(); i++) {
            String id = result.get(i).getId();
            if (resultall.stream().filter(t -> t.getId().equals(id)).count() == 0) {
                resultall.add(result.get(i));
            }
        }
        List<SumTree<AuthorizeDataModel>> list = TreeDotUtils.convertListToTreeDot(resultall);

        //去除菜单状态禁用下其子节点的显示
//        Iterator<SumTree<AuthorizeDataModel>> iterator = list.iterator();
//        while (iterator.hasNext()) {
//            SumTree<AuthorizeDataModel> menuTreeVO = iterator.next();
//            if (!"-1".equals(menuTreeVO.getParentId())) {
//                iterator.remove();
//            }
//        }
        AuthorizeDataReturnVO vo = new AuthorizeDataReturnVO();
        List<AuthorizeDataReturnModel> listVO= JsonUtil.getJsonToList(list, AuthorizeDataReturnModel.class);
        //菜单第二次排序
        listVO=listVO.stream().sorted(Comparator.comparing(AuthorizeDataReturnModel::getSortCode)).collect(Collectors.toList());
        vo.setList(listVO);
        vo.setIds(ids);
        vo.setAll(all);
        return vo;
    }


    /**
     * 列表权限
     *
     * @param moduleList       功能
     * @param moduleColumnList 列表
     * @param checkList        已有权限
     * @param authorizeModel   权限集合
     * @return
     */
    AuthorizeDataReturnVO moduleColumn(List<ModuleEntity> moduleList, List<ModuleColumnEntity> moduleColumnList, List<String> checkList, AuthorizeVO authorizeModel) {
        List<ModuleEntity> data = new ArrayList<>();
        //过滤权限后的按钮
        List<ModuleButtonEntity> filterBtnList = JsonUtil.getJsonToList(authorizeModel.getButtonList(), ModuleButtonEntity.class);
        //获取过滤后按钮的直接上级
        for (int i = 0; i < filterBtnList.size(); i++) {
            ModuleButtonEntity button = filterBtnList.get(i);
            ModuleEntity entity = moduleList.stream().filter(t -> t.getId().equals(button.getModuleId())).findFirst().orElse(null);
            if (entity != null && data.stream().filter(t -> t.equals(entity)).count() == 0) {
                data.add(entity);
            }
        }
        //菜单第一次排序
        data=data.stream().sorted(Comparator.comparing(ModuleEntity::getSortCode)).collect(Collectors.toList());
        List<AuthorizeDataModel> treeList = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        List<String> all = new ArrayList<>();
        for (ModuleEntity entity : data) {
            //添加所有id
            all.add(entity.getId());
            if (authorizeModel.getModuleList().stream().filter(t -> t.getId().equals(entity.getId())).count() != 0) {
                AuthorizeDataModel module = new AuthorizeDataModel();
                module.setId(entity.getId());
                module.setFullName(entity.getFullName());
                module.setParentId(entity.getParentId());
                module.setIcon(entity.getIcon());
                module.setType("web");
                module.setCheckstate(checkList.contains(module.getId()) ? 1 : 0);
                module.setShowcheck(true);
                if (entity.getType() == 2||entity.getType() == 3) {
                    List<ModuleColumnEntity> columnList = moduleColumnList.stream().filter(t -> t.getModuleId().equals(entity.getId())).collect(Collectors.toList());
                    if (columnList.size() == 0) {
                        continue;
                    }
                    Map<String, String> bindTable = new HashMap<>();
                    for (ModuleColumnEntity columnEntity : columnList) {
                        //添加所有id
                        all.add(columnEntity.getId());
                        if (bindTable.get(columnEntity.getBindTable()) == null) {
                            bindTable.put(columnEntity.getBindTable(), columnEntity.getBindTableName());
                        }
                    }
                    if (bindTable.size() > 1) {
                        for (Map.Entry<String, String> entry : bindTable.entrySet()) {
                            AuthorizeDataModel table = new AuthorizeDataModel();
                            table.setId(RandomUtil.uuId());
                            table.setFullName(entry.getValue() == null ? entry.getKey() : entry.getValue());
                            table.setParentId(entity.getId());
                            table.setIcon("fa fa-tags column ");
                            table.setShowcheck(true);
                            table.setModuleId(entity.getId());
                            treeList.add(table);
                            List<ModuleColumnEntity> columnListSize = columnList.stream().filter(t -> t.getBindTable().equals(entry.getKey())).collect(Collectors.toList());
                            for (ModuleColumnEntity columnEntity : columnListSize) {
                                if (authorizeModel.getColumnList().stream().filter(t -> t.getId().equals(columnEntity.getId())).count() != 0) {
                                    AuthorizeDataModel column = new AuthorizeDataModel();
                                    column.setId(columnEntity.getId());
                                    column.setFullName(columnEntity.getFullName());
                                    column.setParentId(table.getId());
                                    column.setIcon("fa fa-filter column ");
                                    column.setCheckstate(checkList.contains(column.getId()) == true ? 1 : 0);
                                    if (column.getCheckstate() == 1) {
                                        ids.add(column.getId());
                                    }
                                    column.setShowcheck(true);
                                    column.setModuleId(entity.getId());
                                    treeList.add(column);
                                }
                            }
                        }
                    } else {
                        for (ModuleColumnEntity columnEntity : columnList) {
                            if (authorizeModel.getColumnList().stream().filter(t -> t.getId().equals(columnEntity.getId())).count() != 0) {
                                AuthorizeDataModel column = new AuthorizeDataModel();
                                column.setId(columnEntity.getId());
                                column.setFullName(columnEntity.getFullName());
                                column.setParentId(entity.getId());
                                column.setIcon("fa fa-filter column ");
                                column.setCheckstate(checkList.contains(column.getId()) == true ? 1 : 0);
                                if (column.getCheckstate() == 1) {
                                    ids.add(column.getId());
                                }
                                column.setShowcheck(true);
                                column.setModuleId(entity.getId());
                                treeList.add(column);
                            }
                        }
                    }
                }
                treeList.add(module);
            }
        }
        List<AuthorizeDataModel> result = JsonUtil.getJsonToList(ListToTreeUtil.treeWhere(data, moduleList), AuthorizeDataModel.class);
        List<AuthorizeDataModel> resultall = new ArrayList<>();
        resultall.addAll(treeList);
        for (int i = 0; i < result.size(); i++) {
            String id = result.get(i).getId();
            if (resultall.stream().filter(t -> t.getId().equals(id)).count() == 0) {
                resultall.add(result.get(i));
            }
        }
        List<SumTree<AuthorizeDataModel>> list = TreeDotUtils.convertListToTreeDot(resultall);

//        List<SumTree<AuthorizeDataModel>> list = TreeDotUtils.convertListToTreeDot(treeList);
//
//        //去除菜单状态禁用下其子节点的显示
//        Iterator<SumTree<AuthorizeDataModel>> iterator = list.iterator();
//        while (iterator.hasNext()) {
//            SumTree<AuthorizeDataModel> menuTreeVO = iterator.next();
//            if (!"-1".equals(menuTreeVO.getParentId())) {
//                iterator.remove();
//            }
//        }
        AuthorizeDataReturnVO vo = new AuthorizeDataReturnVO();
        List<AuthorizeDataReturnModel> listVO= JsonUtil.getJsonToList(list, AuthorizeDataReturnModel.class);
        //菜单第二次排序
        listVO=listVO.stream().sorted(Comparator.comparing(AuthorizeDataReturnModel::getSortCode)).collect(Collectors.toList());
        vo.setList(listVO);
        vo.setIds(ids);
        vo.setAll(all);
        return vo;
    }

    /**
     * 数据权限
     *
     * @param moduleList           功能
     * @param moduleDataSchemeList 数据方案
     * @param checkList            已有权限
     * @param authorizeModel       权限集合
     * @return
     */
    AuthorizeDataReturnVO resourceData(List<ModuleEntity> moduleList, List<ModuleDataAuthorizeSchemeEntity> moduleDataSchemeList, List<String> checkList, AuthorizeVO authorizeModel) {
        List<ModuleEntity> data = new ArrayList<>();
        //过滤权限后的按钮
        List<ModuleButtonEntity> filterBtnList = JsonUtil.getJsonToList(authorizeModel.getButtonList(), ModuleButtonEntity.class);
        //获取过滤后按钮的直接上级
        for (int i = 0; i < filterBtnList.size(); i++) {
            ModuleButtonEntity button = filterBtnList.get(i);
            ModuleEntity entity = moduleList.stream().filter(t -> t.getId().equals(button.getModuleId())).findFirst().orElse(null);
            if (entity != null && data.stream().filter(t -> t.equals(entity)).count() == 0) {
                data.add(entity);
            }
        }
        //菜单第一次排序
        data=data.stream().sorted(Comparator.comparing(ModuleEntity::getSortCode)).collect(Collectors.toList());
        List<AuthorizeDataModel> treeList = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        List<String> all = new ArrayList<>();
        for (ModuleEntity entity : data) {
            //添加所有id
            all.add(entity.getId());
            if (authorizeModel.getModuleList().stream().filter(t -> t.getId().equals(entity.getId())).count() != 0) {
                AuthorizeDataModel module = new AuthorizeDataModel();
                module.setId(entity.getId());
                module.setFullName(entity.getFullName());
                module.setParentId(entity.getParentId());
                module.setIcon(entity.getIcon());
                module.setType("web");
                module.setCheckstate(checkList.contains(module.getId()) == true ? 1 : 0);
                module.setShowcheck(true);
                if (entity.getType() == 2) {
                    List<ModuleDataAuthorizeSchemeEntity> dataSchemeList = moduleDataSchemeList.stream().filter(t -> t.getModuleId().equals(entity.getId())).collect(Collectors.toList());
                    if (dataSchemeList.size() == 0) {
                        continue;
                    }
                    for (ModuleDataAuthorizeSchemeEntity schemeEntity : dataSchemeList) {
                        //添加所有id
                        all.add(schemeEntity.getId());
                        if (authorizeModel.getResourceList().stream().filter(t -> t.getId().equals(schemeEntity.getId())).count() != 0) {
                            AuthorizeDataModel scheme = new AuthorizeDataModel();
                            scheme.setId(schemeEntity.getId());
                            scheme.setFullName(schemeEntity.getFullName());
                            scheme.setTitle(schemeEntity.getConditionText());
                            scheme.setParentId(entity.getId());
                            scheme.setIcon("fa fa-binoculars resource ");
                            scheme.setCheckstate(checkList.contains(scheme.getId()) == true ? 1 : 0);
                            if (scheme.getCheckstate() == 1) {
                                ids.add(scheme.getId());
                            }
                            scheme.setShowcheck(true);
                            scheme.setModuleId(entity.getId());
                            treeList.add(scheme);
                        }
                    }
                }
                treeList.add(module);
            }
        }
        List<AuthorizeDataModel> result = JsonUtil.getJsonToList(ListToTreeUtil.treeWhere(data, moduleList), AuthorizeDataModel.class);
        List<AuthorizeDataModel> resultall = new ArrayList<>();
        resultall.addAll(treeList);
        for (int i = 0; i < result.size(); i++) {
            String id = result.get(i).getId();
            if (resultall.stream().filter(t -> t.getId().equals(id)).count() == 0) {
                resultall.add(result.get(i));
            }
        }
        List<SumTree<AuthorizeDataModel>> list = TreeDotUtils.convertListToTreeDot(resultall);

//        List<SumTree<AuthorizeDataModel>> list = TreeDotUtils.convertListToTreeDot(treeList);

//        //去除菜单状态禁用下其子节点的显示
//        Iterator<SumTree<AuthorizeDataModel>> iterator = list.iterator();
//        while (iterator.hasNext()) {
//            SumTree<AuthorizeDataModel> menuTreeVO = iterator.next();
//            if (!"-1".equals(menuTreeVO.getParentId())) {
//                iterator.remove();
//            }
//        }
        AuthorizeDataReturnVO vo = new AuthorizeDataReturnVO();
        List<AuthorizeDataReturnModel> listVO= JsonUtil.getJsonToList(list, AuthorizeDataReturnModel.class);
        //菜单第二次排序
        listVO=listVO.stream().sorted(Comparator.comparing(AuthorizeDataReturnModel::getSortCode)).collect(Collectors.toList());
        vo.setList(listVO);
        vo.setIds(ids);
        vo.setAll(all);
        return vo;
    }

    /**
     * 角色信息
     *
     * @param data 数据
     * @return
     */
    AuthorizeDataReturnVO roleTree(List<RoleEntity> data) {
        List<AuthorizeDataModel> treeList = new ArrayList<>();
        List<DictionaryDataEntity> typeData = dictionaryDataService.getList("4501f6f26a384757bce12d4c4b03342c");
        for (DictionaryDataEntity entity : typeData) {
            AuthorizeDataModel dictionary = new AuthorizeDataModel();
            dictionary.setId(entity.getEnCode());
            dictionary.setFullName(entity.getFullName());
            dictionary.setShowcheck(false);
            dictionary.setParentId("-1");
            treeList.add(dictionary);
        }
        for (RoleEntity entity : data) {
            AuthorizeDataModel role = new AuthorizeDataModel();
            role.setId(entity.getId());
            role.setFullName(entity.getFullName());
            role.setParentId(entity.getType());
            role.setShowcheck(true);
            role.setIcon("fa fa-umbrella");
            treeList.add(role);
        }
        AuthorizeDataReturnVO vo = new AuthorizeDataReturnVO();
        vo.setList(JsonUtil.getJsonToList(TreeDotUtils.convertListToTreeDot(treeList), AuthorizeDataReturnModel.class));
        return vo;
    }

    /**
     * 岗位信息
     *
     * @param organizeData 机构
     * @param positionData 岗位
     * @return
     */
    AuthorizeDataReturnVO positionTree(List<OrganizeEntity> organizeData, List<PositionEntity> positionData) {
        List<AuthorizeDataModel> treeList = new ArrayList<>();
        for (OrganizeEntity entity : organizeData) {
            AuthorizeDataModel organize = new AuthorizeDataModel();
            organize.setId(entity.getId());
            organize.setShowcheck(false);
            organize.setFullName(entity.getFullName());
            organize.setParentId(entity.getParentId());
            treeList.add(organize);
        }
        for (PositionEntity entity : positionData) {
            AuthorizeDataModel position = new AuthorizeDataModel();
            position.setId(entity.getId());
            position.setFullName(entity.getFullName());
            position.setTitle(entity.getEnCode());
            position.setParentId(entity.getOrganizeId());
            position.setShowcheck(true);
            position.setIcon("fa fa-briefcase");
            treeList.add(position);
        }
        AuthorizeDataReturnVO vo = new AuthorizeDataReturnVO();
        vo.setList(JsonUtil.getJsonToList(TreeDotUtils.convertListToTreeDot(treeList), AuthorizeDataReturnModel.class));
        return vo;
    }

    /**
     * 用户信息
     *
     * @param organizeData 机构
     * @param userData     用户
     * @return
     */
    private AuthorizeDataReturnVO userTree(List<OrganizeEntity> organizeData, List<UserEntity> userData) {
        List<AuthorizeDataModel> treeList = new ArrayList<>();
        for (OrganizeEntity entity : organizeData) {
            AuthorizeDataModel organize = new AuthorizeDataModel();
            organize.setId(entity.getId());
            organize.setShowcheck(false);
            organize.setFullName(entity.getFullName());
            organize.setParentId(entity.getParentId());
            treeList.add(organize);
        }
        for (UserEntity entity : userData) {
            AuthorizeDataModel user = new AuthorizeDataModel();
            user.setId(entity.getId());
            user.setFullName(entity.getRealName() + "/" + entity.getAccount());
            user.setParentId(entity.getOrganizeId());
            user.setShowcheck(true);
            user.setIcon("fa fa-user");
            treeList.add(user);
        }
        AuthorizeDataReturnVO vo = new AuthorizeDataReturnVO();
        vo.setList(JsonUtil.getJsonToList(TreeDotUtils.convertListToTreeDot(treeList), AuthorizeDataReturnModel.class));
        return vo;
    }

    /**
     * 判断当前用户是不是管理员
     * @param isAdmin
     * @param userId
     * @return
     */
    @GetMapping("/permission/{isAdmin}/{userId}")
    public AuthorizeVO permissionAsUser(@PathVariable("isAdmin") boolean isAdmin, @PathVariable("userId") String userId) {
        return authorizeService.getAuthorize(isAdmin,userId);
    }

    /**
     * 获取权限（菜单、按钮、列表）
     *
     * @param isCache 是否存在redis
     * @return
     */
    @GetMapping("/permission/{isCache}")
    public AuthorizeVO getEntity(@PathVariable("isCache") boolean isCache){
        return authorizeService.getAuthorize(isCache);
    }

    /**
     * 根据对象Id获取列表
     *
     * @param objectId 对象主键
     * @return
     */
    @GetMapping("/GetListByObjectId/{objectId}")
    public List<AuthorizeEntity> getListByObjectId(@PathVariable("objectId") String objectId){
        List<AuthorizeEntity> list = authorizeService.getListByObjectId(objectId);
        return list;
    }

    /**
     * 将查出来的某个对象删除
     * @param queryWrapper
     * @return
     */
    @DeleteMapping("/remove")
    public void remove(QueryWrapper<AuthorizeEntity> queryWrapper){
        authorizeService.remove(queryWrapper);
    }

}
