package smart.base.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.ActionResult;
import smart.base.model.datainterface.PaginationDataInterface;
import smart.base.mapper.DataInterfaceMapper;
import smart.base.service.DataInterfaceService;
import smart.base.service.DblinkService;
import smart.base.util.AnnotationType;
import smart.util.*;
import smart.base.entity.DataInterfaceEntity;
import smart.base.entity.DbLinkEntity;
import smart.exception.DataException;
import smart.permission.model.user.UserAllModel;
import smart.permission.service.UserService;
import smart.util.type.CompareType;
import smart.util.type.IntegerNumber;
import smart.util.type.MethodType;
import smart.util.type.RequestType;
import smart.util.wxutil.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * 数据接口业务实现类
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Service
public class DataInterfaceServiceImpl extends ServiceImpl<DataInterfaceMapper, DataInterfaceEntity> implements DataInterfaceService {
    @Autowired
    private UserService userService;
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private DblinkService dblinkService;
    @Autowired
    private DataSourceUtil dataSourceUtils;

    @Override
    public List<DataInterfaceEntity> getList(PaginationDataInterface pagination) {
        QueryWrapper<DataInterfaceEntity> queryWrapper = new QueryWrapper<>();
        //关键字
        if (!StringUtils.isEmpty(pagination.getKeyword())) {
            queryWrapper.lambda().and(
                    t -> t.like(DataInterfaceEntity::getFullName, pagination.getKeyword())
                            .or().like(DataInterfaceEntity::getEnCode, pagination.getKeyword())
            );
        }
        //分类
        queryWrapper.lambda().eq(DataInterfaceEntity::getCategoryId, pagination.getCategoryId());
        //排序
        queryWrapper.lambda().orderByAsc(DataInterfaceEntity::getSortCode);
        Page<DataInterfaceEntity> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<DataInterfaceEntity> userIPage = this.page(page, queryWrapper);
        return pagination.setData(userIPage.getRecords(), userIPage.getTotal());
    }

    @Override
    public List<DataInterfaceEntity> getList() {
        QueryWrapper<DataInterfaceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DataInterfaceEntity::getEnabledMark, IntegerNumber.ONE);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public DataInterfaceEntity getInfo(String id) {
        QueryWrapper<DataInterfaceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DataInterfaceEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void create(DataInterfaceEntity entity) throws DataException {
        entity.setCreatorUser(userProvider.get().getUserId());
        entity.setCreatorTime(DateUtil.getNowDate());
        entity.setLastModifyTime(DateUtil.getNowDate());
        entity.setId(RandomUtil.uuId());
        this.save(entity);
    }

    @Override
    public boolean update(DataInterfaceEntity entity, String id) throws DataException {
        entity.setId(id);
        entity.setLastModifyUser(userProvider.get().getUserId());
        entity.setLastModifyTime(DateUtil.getNowDate());
        return this.updateById(entity);
    }

    @Override
    public void delete(DataInterfaceEntity entity) {
        this.removeById(entity.getId());
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        QueryWrapper<DataInterfaceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DataInterfaceEntity::getFullName, fullName);
        if (!StringUtils.isEmpty(id)) {
            queryWrapper.lambda().ne(DataInterfaceEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }

    @Override
    public List<Map<String, Object>> get(String id, String sql) throws DataException {
        DataInterfaceEntity entity = this.getInfo(id);
        ResultSet resultSet = connection(entity.getDbLinkId(), sql);
        List<Map<String, Object>> maps = JdbcUtil.convertList2(resultSet);
        return maps;
    }

    @Override
    public ActionResult infoToId(String id) {
        DataInterfaceEntity entity = this.getInfo(id);
        List<Map<String, Object>> mapList;
        //静态数据
        try {
            if (entity.getDataType() == IntegerNumber.TWO) {
                Map<String, Object> map = JsonUtil.stringToMap(entity.getQuery());
                return ActionResult.success(map);
            }
        } catch (Exception e) {
            try {
                List<Map<String, Object>> list = JsonUtil.getJsonToListMap(entity.getQuery());
                return ActionResult.success(list);
            } catch (Exception exception) {
                Object obj = entity.getQuery();
                return ActionResult.success(obj);
            }
        }
        //通过API查询
        try {
            if (entity.getDataType() == IntegerNumber.THREE) {
                String path = entity.getPath();
                if (RequestType.HTTPS.equals(path.substring(IntegerNumber.ONE, IntegerNumber.FIVE).toLowerCase())) {
                    Map<String, Object> map = JsonUtil.stringToMap(entity.getRequestParameters());
                    if (map != null) {
                        path += "?";
                        for (String key : map.keySet()) {
                            path = path + key + CompareType.EQUALS + map.get(key) + "&";
                        }
                    }
                    JSONObject get = HttpUtil.httpsRequest(entity.getPath(), MethodType.GET.getMethod(), null);
                    return ActionResult.success(get);
                } else if (RequestType.HTTP.equals(path.substring(IntegerNumber.ZERO, IntegerNumber.FOUR).toLowerCase())) {
                    List<Map<String, Object>> jsonToListMap = JsonUtil.getJsonToListMap(entity.getRequestParameters());
                    if (jsonToListMap != null) {
                        path += "?";
                        for (Map<String, Object> map : jsonToListMap) {
                            if (map != null) {
                                String field= String.valueOf(map.get("field"));
                                String value= String.valueOf(map.get("value"));
                                path = path + field + CompareType.EQUALS + value + "&";
                            }
                        }
                    }
                    JSONObject get = HttpUtil.httpRequest(path, MethodType.GET.getMethod(), null);
                    return ActionResult.success(get);
                } else {
                    return ActionResult.fail("外部接口暂时只支持HTTP和HTTPS方式");
                }
            }
        } catch (Exception e) {
            return ActionResult.fail("调用接口失败，请检查接口路径和参数");
        }
        //通过SQL查询
        try {
            //判断只能使用select
            if (entity.getQuery().length() < IntegerNumber.SIX || !"select".equals(entity.getQuery().trim().substring(IntegerNumber.ZERO, IntegerNumber.SIX).toLowerCase())) {
                return ActionResult.fail("该功能只支持Select语句");
            }
            //判断返回值不能为*
            if ("*".equals(entity.getQuery().trim().substring(IntegerNumber.SIX, IntegerNumber.SEVEN))) {
                return ActionResult.fail("请指定返回字段");
            }
            //判断只有一个SQL语句
            if (entity.getQuery().trim().contains(";")) {
                int i = entity.getQuery().indexOf(";");
                if (!"".equals(entity.getQuery().trim().substring(i + 1).trim())) {
                    return ActionResult.fail("只能输入一个sql语句哦");
                }
            }
            //判断注解前是否有等号
            if (entity.getQuery().contains(AnnotationType.USER)) {
                if (!CompareType.EQUALS.equals(entity.getQuery().substring(entity.getQuery().trim().split(AnnotationType.USER)[0].length() - 1, entity.getQuery().trim().split(AnnotationType.USER)[0].length()))) {
                    return ActionResult.fail(AnnotationType.USER + "前少了等号哦");
                }
            } else if (entity.getQuery().contains(AnnotationType.DEPARTMENT)) {
                if (!CompareType.EQUALS.equals(entity.getQuery().substring(entity.getQuery().trim().split(AnnotationType.DEPARTMENT)[0].length() - 1, entity.getQuery().trim().split(AnnotationType.DEPARTMENT)[0].length()))) {
                    return ActionResult.fail(AnnotationType.DEPARTMENT + "前少了等号哦");
                }
            } else if (entity.getQuery().contains(AnnotationType.ORGANIZE)) {
                if (!CompareType.EQUALS.equals(entity.getQuery().substring(entity.getQuery().trim().split(AnnotationType.ORGANIZE)[0].length() - 1, entity.getQuery().trim().split(AnnotationType.ORGANIZE)[0].length()))) {
                    return ActionResult.fail(AnnotationType.ORGANIZE + "前少了等号哦");
                }
            } else if (entity.getQuery().contains(AnnotationType.POSTION)) {
                if (!CompareType.EQUALS.equals(entity.getQuery().substring(entity.getQuery().trim().split(AnnotationType.POSTION)[0].length() - 1, entity.getQuery().trim().split(AnnotationType.POSTION)[0].length()))) {
                    return ActionResult.fail(AnnotationType.POSTION + "前少了等号哦");
                }
            }
            mapList = this.get(id, entity.getQuery());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ActionResult.fail("调用接口失败，请检查SQL语句是否有误");
        }
        return ActionResult.success(mapList);
    }

    /**
     * 拼接SQL语句并执行
     * @param dbLinkId
     * @param sql
     * @return
     * @throws DataException
     */
    public ResultSet connection(String dbLinkId, String sql) throws DataException {
        DbLinkEntity linkEntity = dblinkService.getInfo(dbLinkId);
        Connection conn = null;
        if (linkEntity != null) {
            conn = JdbcUtil.getConn(linkEntity.getDbType(), linkEntity.getUserName(), linkEntity.getPassword(), linkEntity.getHost(), linkEntity.getPort(), linkEntity.getServiceName());
        } else {
            String url = dataSourceUtils.getUrl().replace("{dbName}", dataSourceUtils.getDbName());
            conn = JdbcUtil.getConn(dataSourceUtils.getUserName(), dataSourceUtils.getPassword(), url);
        }
        List<UserAllModel> model = userService.getAll();
        List<UserAllModel> list = model.stream().filter(t -> t.getId().equals(userProvider.get().getUserId())).collect(Collectors.toList());
        UserAllModel userAllModel = list.get(0);
        StringBuffer stringBuffer = new StringBuffer();
        //判断是否有注解
        stringBuffer.append(sql.replaceAll(AnnotationType.USER, "'" + userAllModel.getId() + "'")
                .replaceAll(AnnotationType.DEPARTMENT, "'" + userAllModel.getDepartment() + "'")
                .replaceAll(AnnotationType.ORGANIZE, "'" + userAllModel.getOrganizeId() + "'")
                .replaceAll(AnnotationType.POSTION, "'" + userAllModel.getPositionId() + "'"));
        return JdbcUtil.query(conn, stringBuffer.toString());
    }

}
