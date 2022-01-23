package smart.engine.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import smart.base.BillRuleApi;
import smart.base.DataInterFaceApi;
import smart.base.DictionaryDataApi;
import smart.base.UserInfo;
import smart.base.entity.DictionaryDataEntity;
import smart.config.ConfigValueUtil;
import smart.engine.entity.FlowTaskEntity;
import smart.engine.model.flowdynamic.FormAllModel;
import smart.engine.model.flowdynamic.FormColumnModel;
import smart.engine.model.flowdynamic.FormEnum;
import smart.engine.model.flowtask.FlowTableModel;
import smart.exception.DataException;
import smart.onlinedev.model.VMDEnum;
import smart.onlinedev.model.fields.FieLdsModel;
import smart.onlinedev.model.fields.config.ConfigModel;
import smart.onlinedev.model.fields.props.PropsBeanModel;
import smart.permission.OrganizeApi;
import smart.permission.PositionApi;
import smart.permission.UsersApi;
import smart.permission.entity.OrganizeEntity;
import smart.permission.entity.PositionEntity;
import smart.permission.model.position.PositionInfoVO;
import smart.permission.model.user.UserAllModel;
import smart.permission.model.user.UserInfoVO;
import smart.util.*;
import smart.util.visiual.SmartKeyConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 在线工作流开发
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Component
public class FlowDataUtil {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private DataSourceUtil dataSourceUtil;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private OrganizeApi organizeApi;
    @Autowired
    private PositionApi positionApi;
    @Autowired
    private UsersApi usersApi;
    @Autowired
    private BillRuleApi billRuleApi;
    @Autowired
    private DictionaryDataApi dictionaryDataApi;
    @Autowired
    private DataInterFaceApi dataInterFaceApi;
//    @Autowired
//    private ProvinceService provinceService;

    /**
     * 获取有表的数据库连接
     *
     * @return
     */
    private Connection getTableConn() {
        String tenId = "";
        if (!Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
            tenId = dataSourceUtil.getDbName();
        } else {
            tenId = userProvider.get().getTenantDbConnectionString();
        }
        Connection conn = JdbcUtil.getConn(dataSourceUtil.getUserName(), dataSourceUtil.getPassword(), dataSourceUtil.getUrl().replace("{dbName}", tenId));
        return conn;
    }

    /**
     * 获取有子表数据
     *
     * @param sql sql语句
     * @return
     * @throws DataException
     */
    private List<Map<String, Object>> getTableList(Connection conn, String sql) throws DataException {
        ResultSet rs = JdbcUtil.query(conn, sql);
        List<Map<String, Object>> dataList = JdbcUtil.convertListString(rs);
        List<Map<String, Object>> childData = new ArrayList<>();
        for (Map<String, Object> data : dataList) {
            Map<String, Object> child = new HashMap<>(16);
            for (String key : data.keySet()) {
                child.put(key.toLowerCase(), data.get(key));
            }
            childData.add(child);
        }
        return childData;
    }

    /**
     * 获取主表数据
     *
     * @param sql sql语句
     * @return
     * @throws DataException
     */
    private Map<String, Object> getMast(Connection conn, String sql) throws DataException {
        ResultSet rs = JdbcUtil.query(conn, sql);
        Map<String, Object> mast = JdbcUtil.convertMapString(rs);
        Map<String, Object> mastData = new HashMap<>(16);
        for (String key : mast.keySet()) {
            mastData.put(key.toLowerCase(), mast.get(key));
        }
        return mastData;
    }

    /**
     * 返回主键名称
     *
     * @param conn
     * @param mainTable
     * @return
     */
    public String getPKey(Connection conn, String mainTable) throws SQLException {
        String pKeyName = "f_id";
        //catalog 数据库名
        String catalog = conn.getCatalog();
        ResultSet primaryKeyResultSet = conn.getMetaData().getPrimaryKeys(catalog, null, mainTable);
        while (primaryKeyResultSet.next()) {
            pKeyName = primaryKeyResultSet.getString("COLUMN_NAME");
        }
        primaryKeyResultSet.close();
        return pKeyName;
    }

    //--------------------------------------------信息-----------------------------------------------------

    /**
     * 详情查询
     **/
    public Map<String, Object> info(List<FieLdsModel> fieLdslist, FlowTaskEntity entity, List<FlowTableModel> tableList, boolean convert) throws SQLException, DataException {
        List<FormAllModel> formAllModel = new ArrayList<>();
        //递归遍历模板
        FormCloumnUtil.recursionForm(fieLdslist, formAllModel);
        //处理好的数据
        Map<String, Object> result = new HashMap<>(16);
        if (tableList.size() > 0) {
            result = tableInfo(entity, tableList, formAllModel, convert);
        } else {
            Map<String, Object> dataMap = JsonUtil.stringToMap(entity.getFlowFormContentJson());
            result = info(dataMap, formAllModel, convert);
        }
        return result;
    }

    /**
     * 有表详情
     **/
    private Map<String, Object> tableInfo(FlowTaskEntity entity, List<FlowTableModel> tableList, List<FormAllModel> formAllModel, boolean convert) throws SQLException, DataException {
        //处理好的数据
        Map<String, Object> result = new HashMap<>(16);
        List<OrganizeEntity> orgMapList = organizeApi.getList();
        List<UserAllModel> allUser = usersApi.getAll().getData();
        List<PositionEntity> mapList = positionApi.getListAll().getData();
        List<DictionaryDataEntity> list = dictionaryDataApi.getListAll().getData();
        List<FormAllModel> mastForm = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> tableForm = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        String mainId = entity.getId();
        Connection conn = getTableConn();
        String mastTableName = tableList.stream().filter(t -> "1".equals(t.getTypeId())).findFirst().get().getTable();
        List<String> mastFile = mastForm.stream().map(t -> t.getFormColumnModel().getFieLdsModel().getVModel()).collect(Collectors.toList());
        String pKeyName = getPKey(conn, mastTableName);
        //主表数据
        String mastInfo = " select " + String.join(",", mastFile) + " from " + mastTableName + " where " + pKeyName + " = '" + mainId + "'";
        Map<String, Object> mastData = getMast(conn, mastInfo);
        for (String key : mastData.keySet()) {
            FormAllModel model = mastForm.stream().filter(t -> key.equals(t.getFormColumnModel().getFieLdsModel().getVModel())).findFirst().orElse(null);
            if (model != null) {
                FieLdsModel fieLdsModel = model.getFormColumnModel().getFieLdsModel();
                String format = fieLdsModel.getFormat();
                String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
                Object data = mastData.get(key);
                data = info(jnpfkey, data, orgMapList, allUser, mapList);
                data = infoTable(jnpfkey, data, format);
                if (convert) {
                    data = converData(fieLdsModel, data, list);
                }
                result.put(key, data);
            }
        }
        //子表数据
        if (!convert) {
            List<FlowTableModel> tableListAll = tableList.stream().filter(t -> !"1".equals(t.getTypeId())).collect(Collectors.toList());
            for (FlowTableModel tableModel : tableListAll) {
                String tableName = tableModel.getTable();
                FormAllModel childModel = tableForm.stream().filter(t -> tableName.toLowerCase().equals(t.getChildList().getTableName().toLowerCase())).findFirst().orElse(null);
                if (childModel != null) {
                    String childKey = childModel.getChildList().getTableModel();
                    List<String> childFile = childModel.getChildList().getChildList().stream().map(t -> t.getFieLdsModel().getVModel()).collect(Collectors.toList());
                    String tableInfo = "select " + String.join(",", childFile) + " from " + tableName + " where " + tableModel.getTableField() + "='" + mainId + "'";
                    List<Map<String, Object>> tableData = getTableList(conn, tableInfo);
                    List<Map<String, Object>> childdataAll = new ArrayList<>();
                    for (Map<String, Object> data : tableData) {
                        Map<String, Object> tablValue = new HashMap<>(16);
                        for (String key : data.keySet()) {
                            FormColumnModel columnModel = childModel.getChildList().getChildList().stream().filter(t -> key.equals(t.getFieLdsModel().getVModel())).findFirst().orElse(null);
                            if (columnModel != null) {
                                FieLdsModel fieLdsModel = columnModel.getFieLdsModel();
                                String format = fieLdsModel.getFormat();
                                String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
                                Object childValue = data.get(key);
                                childValue = info(jnpfkey, childValue, orgMapList, allUser, mapList);
                                childValue = infoTable(jnpfkey, childValue, format);
                                tablValue.put(key, childValue);
                            }
                        }
                        childdataAll.add(tablValue);
                    }
                    result.put(childKey, childdataAll);
                }
            }
        }
        return result;
    }

    /**
     * 无表详情
     **/
    private Map<String, Object> info(Map<String, Object> dataMap, List<FormAllModel> formAllModel, boolean convert) {
        //处理好的数据
        Map<String, Object> result = new HashMap<>(16);
        List<OrganizeEntity> orgMapList = organizeApi.getList();
        List<UserAllModel> allUser = usersApi.getAll().getData();
        List<PositionEntity> mapList = positionApi.getListAll().getData();
        List<DictionaryDataEntity> list = dictionaryDataApi.getListAll().getData();
        List<FormAllModel> mastForm = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> tableForm = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        for (String key : dataMap.keySet()) {
            FormAllModel model = mastForm.stream().filter(t -> key.equals(t.getFormColumnModel().getFieLdsModel().getVModel())).findFirst().orElse(null);
            if (model != null) {
                FieLdsModel fieLdsModel = model.getFormColumnModel().getFieLdsModel();
                String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
                Object data = dataMap.get(key);
                data = info(jnpfkey, data, orgMapList, allUser, mapList);
                if (convert) {
                    data = converData(fieLdsModel, data, list);
                }
                result.put(key, data);
            } else {
                if (!convert) {
                    FormAllModel childModel = tableForm.stream().filter(t -> key.equals(t.getChildList().getTableModel())).findFirst().orElse(null);
                    if (childModel != null) {
                        String childKeyName = childModel.getChildList().getTableModel();
                        List<Map<String, Object>> childDataMap = (List<Map<String, Object>>) dataMap.get(key);
                        List<Map<String, Object>> childdataAll = new ArrayList<>();
                        for (Map<String, Object> child : childDataMap) {
                            Map<String, Object> tablValue = new HashMap<>(16);
                            for (String childKey : child.keySet()) {
                                FormColumnModel columnModel = childModel.getChildList().getChildList().stream().filter(t -> childKey.equals(t.getFieLdsModel().getVModel())).findFirst().orElse(null);
                                if (columnModel != null) {
                                    FieLdsModel fieLdsModel = columnModel.getFieLdsModel();
                                    String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
                                    Object childValue = child.get(childKey);
                                    childValue = info(jnpfkey, childValue, orgMapList, allUser, mapList);
                                    tablValue.put(childKey, childValue);
                                }
                            }
                            childdataAll.add(tablValue);
                        }
                        result.put(childKeyName, childdataAll);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 详情转换成中文
     **/
    private Object converData(FieLdsModel fieLdsModel, Object dataValue, List<DictionaryDataEntity> list) {
        Object value = dataValue;
        ConfigModel config = fieLdsModel.getConfig();
        String dataType = config.getDataType();
        String jnpfKey = config.getJnpfKey();
        if (SmartKeyConsts.RADIO.equals(jnpfKey) || SmartKeyConsts.SELECT.equals(jnpfKey) || SmartKeyConsts.CHECKBOX.equals(jnpfKey) || SmartKeyConsts.CASCADER.equals(jnpfKey) || SmartKeyConsts.TREESELECT.equals(jnpfKey)) {
            String props = "";
            if (SmartKeyConsts.CASCADER.equals(jnpfKey) || SmartKeyConsts.TREESELECT.equals(jnpfKey)) {
                props = fieLdsModel.getProps().getProps();
            } else {
                props = JsonUtilEx.getObjectToString(fieLdsModel.getConfig().getProps());
            }
            PropsBeanModel pro = JsonUtil.getJsonToBean(props, PropsBeanModel.class);
            String proFullName = pro.getLabel();
            String proId = pro.getValue();
            String proChildren = pro.getChildren();
            List<String> dataAll = new ArrayList<>();
            List<String> box = new ArrayList<>();
            if (dataValue instanceof String) {
                box = Arrays.asList((String.valueOf(dataValue)).split(","));
            } else if (dataValue instanceof List) {
                box = (List<String>) dataValue;
            }
            //获取list数据
            if (VMDEnum.DICTIONARY.equals(dataType)) {
                for (String id : box) {
                    List<String> name = list.stream().filter(t -> t.getId().equals(String.valueOf(id))).map(t -> t.getFullName()).collect(Collectors.toList());
                    dataAll.addAll(name);
                }
                value = String.join(",", dataAll);
            } else if (VMDEnum.STATIC.equals(dataType)) {
                List<Map<String, Object>> staticList = new ArrayList<>();
                if (SmartKeyConsts.CASCADER.equals(jnpfKey)) {
                    staticList = JsonUtil.getJsonToListMap(fieLdsModel.getOptions());
                } else {
                    staticList = JsonUtil.getJsonToListMap(fieLdsModel.getSlot().getOptions());
                }
                if (SmartKeyConsts.CASCADER.equals(jnpfKey) || SmartKeyConsts.TREESELECT.equals(jnpfKey)) {
                    JSONArray data = JsonUtil.getListToJsonArray(staticList);
                    staticList = new ArrayList<>();
                    treeToList(proId, proFullName, proChildren, data, staticList);
                }
                for (String id : box) {
                    List<String> name = staticList.stream().filter(t -> String.valueOf(t.get(proId)).equals(String.valueOf(id))).map(t -> String.valueOf(t.get(proFullName))).collect(Collectors.toList());
                    dataAll.addAll(name);
                }
                value = String.join(",", dataAll);
            } else if (VMDEnum.DYNAMIC.equals(dataType)) {
                String dynId = config.getPropsUrl();
                //获取远端数据
                Object object = dataInterFaceApi.infoToId(dynId);
                Map<String, Object> dynamicMap = JsonUtil.entityToMap(object);
                String dataJson = "data";
                if (dynamicMap.get(dataJson) != null) {
                    List<Map<String, Object>> dataList = JsonUtil.getJsonToListMap(dynamicMap.get("data").toString());
                    if (SmartKeyConsts.CASCADER.equals(jnpfKey) || SmartKeyConsts.TREESELECT.equals(jnpfKey)) {
                        JSONArray data = JsonUtil.getListToJsonArray(dataList);
                        dataList = new ArrayList<>();
                        treeToList(proId, proFullName, proChildren, data, dataList);
                    }
                    for (String id : box) {
                        List<String> name = dataList.stream().filter(t -> String.valueOf(t.get(proId)).equals(String.valueOf(id))).map(t -> String.valueOf(t.get(proFullName))).collect(Collectors.toList());
                        dataAll.addAll(name);
                    }
                    value = String.join(",", dataAll);
                }
            }
        }
        //省市区
        if (SmartKeyConsts.ADDRESS.equals(jnpfKey)) {
            List<String> box = (List<String>) dataValue;
            List<String> dataAll = new ArrayList<>();
            if (box != null) {
//                List<ProvinceEntity> data = provinceService.getAllList();
//                for (String id : box) {
//                    List<String> name = data.stream().filter(t -> t.getId().equals(String.valueOf(id))).map(t -> String.valueOf(t.getFullName())).collect(Collectors.toList());
//                    dataAll.addAll(name);
//                }
//                value = String.join(",", dataAll);
            }
        }
        return value;
    }

    /**
     * 树转成list
     **/
    private static void treeToList(String id, String fullName, String children, JSONArray data, List<Map<String, Object>> result) {
        for (int i = 0; i < data.size(); i++) {
            JSONObject ob = data.getJSONObject(i);
            Map<String, Object> tree = new HashMap<>(16);
            tree.put(id, String.valueOf(ob.get(id)));
            tree.put(fullName, (String.valueOf(ob.get(fullName))));
            result.add(tree);
            if (ob.get(children) != null) {
                JSONArray childArray = ob.getJSONArray(children);
                treeToList(id, fullName, children, childArray, result);
            }
        }
    }

    /**
     * 修改有表赋值
     **/
    private Object infoTable(String jnpfKey, Object dataValue, String format) {
        Object value = dataValue;
        switch (jnpfKey) {
            case SmartKeyConsts.UPLOADFZ:
            case SmartKeyConsts.UPLOADIMG:
                value = JsonUtil.getJsonToListMap(String.valueOf(value));
                break;
            case SmartKeyConsts.CHECKBOX:
            case SmartKeyConsts.ADDRESS:
            case SmartKeyConsts.DATERANGE:
            case SmartKeyConsts.TIMERANGE:
            case SmartKeyConsts.CASCADER:
                value = JsonUtil.getJsonToList(String.valueOf(value), String.class);
                break;
            case SmartKeyConsts.DATE:
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    value = sdf.parse(String.valueOf(value)).getTime();
                } catch (Exception e) {
                    value = dataValue;
                }
                break;
            case SmartKeyConsts.SLIDER:
            case SmartKeyConsts.SWITCH:
                try{
                    value = Integer.valueOf(String.valueOf(value));
                }catch (Exception e){
                    value = dataValue;
                }
                break;
            default:
                break;
        }
        return value;
    }

    /**
     * 修改系统赋值
     **/
    private Object info(String jnpfKey, Object dataValue, List<OrganizeEntity> orgMapList, List<UserAllModel> allUser, List<PositionEntity> mapList) {
        Object value = dataValue;
        switch (jnpfKey) {
            case SmartKeyConsts.CURRORGANIZE:
            case SmartKeyConsts.CURRDEPT:
                if (StringUtil.isNotEmpty(String.valueOf(dataValue))) {
                    OrganizeEntity organizeEntity = orgMapList.stream().filter(t -> t.getId().equals(String.valueOf(dataValue))).findFirst().orElse(null);
                    if (organizeEntity != null) {
                        value = organizeEntity.getFullName();
                    }
                }
                break;
            case SmartKeyConsts.CREATEUSER:
            case SmartKeyConsts.MODIFYUSER:
                if (StringUtil.isNotEmpty(String.valueOf(dataValue))) {
                    UserAllModel userAllModel = allUser.stream().filter(t -> t.getId().equals(String.valueOf(dataValue))).findFirst().orElse(null);
                    if (userAllModel != null) {
                        value = userAllModel.getRealName();
                    }
                }
                break;
            case SmartKeyConsts.CURRPOSITION:
                if (StringUtil.isNotEmpty(String.valueOf(dataValue))) {
                    PositionEntity positionEntity = mapList.stream().filter(t -> t.getId().equals(String.valueOf(dataValue))).findFirst().orElse(null);
                    if (positionEntity != null) {
                        value = positionEntity.getFullName();
                    }
                }
                break;
            default:
                break;
        }
        return value;
    }

    //--------------------------------------------修改-----------------------------------------------------

    /**
     * 修改数据处理
     **/
    public Map<String, Object> update(Map<String, Object> allDataMap, List<FieLdsModel> fieLdsModelList, List<FlowTableModel> tableModelList, String mainId) throws SQLException {
        List<FormAllModel> formAllModel = new ArrayList<>();
        //递归遍历模板
        FormCloumnUtil.recursionForm(fieLdsModelList, formAllModel);
        //处理好的数据
        Map<String, Object> result = new HashMap<>(16);
        if (tableModelList.size() > 0) {
            result = tableUpdate(allDataMap, formAllModel, tableModelList, mainId);
        } else {
            result = update(allDataMap, formAllModel);
        }
        return result;
    }

    /**
     * 修改有表数据
     **/
    private Map<String, Object> tableUpdate(Map<String, Object> allDataMap, List<FormAllModel> formAllModel, List<FlowTableModel> tableModelList, String mainId) throws SQLException {
        //处理好的数据
        Map<String, Object> result = new HashMap<>(16);
        //系统数据
        List<OrganizeEntity> orgMapList = organizeApi.getList();
        List<UserAllModel> allUser = usersApi.getAll().getData();
        List<PositionEntity> mapList = positionApi.getListAll().getData();
        List<DictionaryDataEntity> list = dictionaryDataApi.getListAll().getData();
        Connection conn = getTableConn();
        String mastTableName = tableModelList.stream().filter(t -> "1".equals(t.getTypeId())).findFirst().get().getTable();
        String pKeyName = getPKey(conn, mastTableName);
        List<FormAllModel> mastForm = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> tableForm = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        //主表的语句
        StringBuffer mastSql = new StringBuffer("INSERT INTO " + mastTableName + " ");
        StringBuffer mastFile = new StringBuffer("(" + pKeyName + ",");
        StringBuffer mastFileValue = new StringBuffer("(?,");
        List<Object> mastValue = new LinkedList<>();
        for (String key : allDataMap.keySet()) {
            FormAllModel model = mastForm.stream().filter(t -> key.equals(t.getFormColumnModel().getFieLdsModel().getVModel())).findFirst().orElse(null);
            if (model != null) {
                FieLdsModel fieLdsModel = model.getFormColumnModel().getFieLdsModel();
                String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
                String format = fieLdsModel.getFormat();
                Object data = allDataMap.get(key);
                //处理字段
                String file = "?,";
                //添加字段
                mastFile.append(key + ",");
                //处理系统自动生成
                data = update(jnpfkey, data, orgMapList, allUser, mapList);
                data = temp(jnpfkey, data, format);
                mastValue.add(data);
                if (dataSourceUtil.getDataType().toLowerCase().contains("oracle") && "date".equals(jnpfkey)) {
                    if (String.valueOf(data).length() < 11) {
                        file = "to_date(?,'yyyy-mm-dd HH24:mi:ss'),";
                    }
                }
                mastFileValue.append(file);
                result.put(key, data);
            } else {
                FormAllModel childModel = tableForm.stream().filter(t -> key.equals(t.getChildList().getTableModel())).findFirst().orElse(null);
                if (childModel != null) {
                    Map<String, Object> childData = childTableUpdate(allDataMap, conn, childModel, tableModelList, key, mainId, orgMapList, allUser, mapList);
                    result.putAll(childData);
                }
            }
        }
        //主表去掉最后
        mastFile = mastFile.deleteCharAt(mastFile.length() - 1).append(")");
        mastFileValue = mastFileValue.deleteCharAt(mastFileValue.length() - 1).append(")");
        mastSql.append(mastFile + " VALUES " + mastFileValue);
        String delSql = "delete from " + mastTableName + " where " + pKeyName + " =?";
        //插入主表数据
        mastSql(mastSql, mastValue, mainId, delSql, conn);
        return result;
    }

    /**修改子表数据**/
    private Map<String,Object> childTableUpdate(Map<String, Object> allDataMap, Connection conn, FormAllModel childModel, List<FlowTableModel> tableModelList, String key, String mainId, List<OrganizeEntity> orgMapList, List<UserAllModel> allUser, List<PositionEntity> mapList) throws SQLException {
        //处理好的子表数据
        Map<String, Object> result = new HashMap<>(16);
        //子表主键
        List<FormColumnModel> childList = childModel.getChildList().getChildList();
        String childTable = childModel.getChildList().getTableName();
        String childKeyName = getPKey(conn, childTable);
        //关联字段
        String mastKeyName = tableModelList.stream().filter(t -> t.getTable().equals(childTable)).findFirst().get().getTableField();
        StringBuffer childFile = new StringBuffer();
        List<List<Object>> childData = new LinkedList<>();
        List<Map<String, Object>> childDataMap = (List<Map<String, Object>>) allDataMap.get(key);
        //子表处理的数据
        List<Map<String, Object>> childResult = new ArrayList<>();
        //子表的字段
        Map<String, String> child = new HashMap<>(16);
        for (FormColumnModel columnModel : childList) {
            String vmodel = columnModel.getFieLdsModel().getVModel();
            String jnpfKey = columnModel.getFieLdsModel().getConfig().getJnpfKey();
            child.put(vmodel, jnpfKey);
        }
        int num = 0;
        for (Map<String, Object> objectMap : childDataMap) {
            //子表处理的数据
            StringBuffer fileAll = new StringBuffer("(");
            StringBuffer fileValueAll = new StringBuffer("(");
            List<Object> value = new LinkedList<>();
            //子表主键
            value.add(RandomUtil.uuId());
            fileAll.append(childKeyName + ",");
            fileValueAll.append("?,");
            //关联字段
            value.add(mainId);
            fileAll.append(mastKeyName + ",");
            fileValueAll.append("?,");
            //子表单体处理的数据
            Map<String, Object> childOneResult = new HashMap<>(16);
            for (String childKey : child.keySet()) {
                FormColumnModel columnModel = childList.stream().filter(t -> childKey.equals(t.getFieLdsModel().getVModel())).findFirst().orElse(null);
                if (columnModel != null) {
                    FieLdsModel fieLdsModel = columnModel.getFieLdsModel();
                    String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
                    String format = fieLdsModel.getFormat();
                    Object data = objectMap.get(childKey);
                    //处理系统自动生成
                    data = update(jnpfkey, data, orgMapList, allUser, mapList);
                    data = temp(jnpfkey, data, format);
                    //添加字段
                    fileAll.append(childKey + ",");
                    fileValueAll.append("?,");
                    if (dataSourceUtil.getDataType().toLowerCase().contains("oracle") && "date".equals(jnpfkey)) {
                        if (format.length() < 11) {
                            data = data + " 00:00:00";
                        }
                        value.add("to_date('" + data + "','YYYY-MM-DD HH24:MI:SS'");
                    } else {
                        value.add(data);
                    }
                    childOneResult.put(childKey, data);
                }
            }
            childResult.add(childOneResult);
            //子表去掉最后
            if (num == 0) {
                fileAll = fileAll.deleteCharAt(fileAll.length() - 1).append(")");
                fileValueAll = fileValueAll.deleteCharAt(fileValueAll.length() - 1).append(")");
                //添加单行的数据
                childFile.append(fileAll.toString() + " VALUES " + fileValueAll);
                num++;
            }
            childData.add(value);
        }
        //删除子表
        String delSql = "delete from " + childTable + " where " + mastKeyName + "=?";
        String[] del = new String[]{delSql, mainId};
        //插入子表数据
        tableSql(childFile, childData, childTable, del, conn);
        result.put(key, childResult);
        return  result;
    }

    /**
     * 修改无表数据
     **/
    private Map<String, Object> update(Map<String, Object> allDataMap, List<FormAllModel> formAllModel) {
        //处理好的数据
        Map<String, Object> result = new HashMap<>(16);
        //系统数据
        List<OrganizeEntity> orgMapList = organizeApi.getList();
        List<UserAllModel> allUser = usersApi.getAll().getData();
        List<PositionEntity> mapList = positionApi.getListAll().getData();
        List<FormAllModel> mastForm = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> tableForm = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        for (String key : allDataMap.keySet()) {
            FormAllModel model = mastForm.stream().filter(t -> key.equals(t.getFormColumnModel().getFieLdsModel().getVModel())).findFirst().orElse(null);
            if (model != null) {
                FieLdsModel fieLdsModel = model.getFormColumnModel().getFieLdsModel();
                String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
                Object data = allDataMap.get(key);
                //处理系统自动生成
                data = update(jnpfkey, data, orgMapList, allUser, mapList);
                result.put(key, data);
            } else {
                FormAllModel childModel = tableForm.stream().filter(t -> key.equals(t.getChildList().getTableModel())).findFirst().orElse(null);
                if (childModel != null) {
                    List<Map<String, Object>> childDataMap = (List<Map<String, Object>>) allDataMap.get(key);
                    //子表处理的数据
                    List<Map<String, Object>> childResult = new ArrayList<>();
                    for (Map<String, Object> objectMap : childDataMap) {
                        //子表单体处理的数据
                        Map<String, Object> childOneResult = new HashMap<>(16);
                        for (String childKey : objectMap.keySet()) {
                            FormColumnModel columnModel = childModel.getChildList().getChildList().stream().filter(t -> childKey.equals(t.getFieLdsModel().getVModel())).findFirst().orElse(null);
                            if (columnModel != null) {
                                FieLdsModel fieLdsModel = columnModel.getFieLdsModel();
                                String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
                                Object data = objectMap.get(childKey);
                                data = update(jnpfkey, data, orgMapList, allUser, mapList);
                                childOneResult.put(childKey, data);
                            }
                        }
                        childResult.add(childOneResult);
                    }
                    result.put(key, childResult);
                }
            }
        }
        return result;
    }

    /**
     * 修改系统赋值
     **/
    private Object update(String jnpfKey, Object dataValue, List<OrganizeEntity> orgMapList, List<UserAllModel> allUser, List<PositionEntity> mapList) {
        UserInfo userInfo = userProvider.get();
        Object value = dataValue;
        switch (jnpfKey) {
            case SmartKeyConsts.CURRORGANIZE:
            case SmartKeyConsts.CURRDEPT:
                if (StringUtil.isNotEmpty(String.valueOf(dataValue))) {
                    OrganizeEntity organizeEntity = orgMapList.stream().filter(t -> t.getFullName().equals(String.valueOf(dataValue))).findFirst().orElse(null);
                    if (organizeEntity != null) {
                        value = organizeEntity.getId();
                    }
                }
                break;
            case SmartKeyConsts.CREATEUSER:
                if (StringUtil.isNotEmpty(String.valueOf(dataValue))) {
                    UserAllModel userAllModel = allUser.stream().filter(t -> t.getId().equals(String.valueOf(dataValue))).findFirst().orElse(null);
                    if (userAllModel != null) {
                        value = userAllModel.getId();
                    }
                }
                break;
            case SmartKeyConsts.MODIFYUSER:
                if (StringUtil.isNotEmpty(String.valueOf(dataValue))) {
                    UserAllModel userAllModel = allUser.stream().filter(t -> t.getId().equals(String.valueOf(dataValue))).findFirst().orElse(null);
                    if (userAllModel != null) {
                        value = userAllModel.getId();
                    }
                } else {
                    value = userInfo.getUserId();
                }
                break;
            case SmartKeyConsts.MODIFYTIME:
                value = DateUtil.getNow("+8");
                break;
            case SmartKeyConsts.CURRPOSITION:
                if (StringUtil.isNotEmpty(String.valueOf(dataValue))) {
                    PositionEntity positionEntity = mapList.stream().filter(t -> t.getFullName().equals(String.valueOf(dataValue))).findFirst().orElse(null);
                    if (positionEntity != null) {
                        value = positionEntity.getId();
                    }
                }
                break;
            default:
                break;
        }
        return value;
    }

    //--------------------------------------------新增-------------------------------------------------------

    /**
     * 新增数据处理
     **/
    public Map<String, Object> create(Map<String, Object> allDataMap, List<FieLdsModel> fieLdsModelList, List<FlowTableModel> tableModelList, String mainId, Map<String, String> billData) throws SQLException, DataException {
        List<FormAllModel> formAllModel = new ArrayList<>();
        //递归遍历模板
        FormCloumnUtil.recursionForm(fieLdsModelList, formAllModel);
        //处理好的数据
        Map<String, Object> result = new HashMap<>(16);
        if (tableModelList.size() > 0) {
            result = tableCreate(allDataMap, formAllModel, tableModelList, mainId, billData);
        } else {
            result = create(allDataMap, formAllModel, billData);
        }
        return result;
    }

    /**
     * 有表插入数据
     **/
    private Map<String, Object> tableCreate(Map<String, Object> allDataMap, List<FormAllModel> formAllModel, List<FlowTableModel> tableModelList, String mainId, Map<String, String> billData) throws SQLException, DataException {
        //处理好的数据
        Map<String, Object> result = new HashMap<>(16);
        Connection conn = getTableConn();
        String mastTableName = tableModelList.stream().filter(t -> "1".equals(t.getTypeId())).findFirst().get().getTable();
        String pKeyName = getPKey(conn, mastTableName);
        List<FormAllModel> mastForm = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> tableForm = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        //主表的语句
        StringBuffer mastSql = new StringBuffer("INSERT INTO " + mastTableName + " ");
        StringBuffer mastFile = new StringBuffer("(" + pKeyName + ",");
        StringBuffer mastFileValue = new StringBuffer("(?,");
        List<Object> mastValue = new LinkedList<>();
        for (String key : allDataMap.keySet()) {
            FormAllModel model = mastForm.stream().filter(t -> key.equals(t.getFormColumnModel().getFieLdsModel().getVModel())).findFirst().orElse(null);
            if (model != null) {
                FieLdsModel fieLdsModel = model.getFormColumnModel().getFieLdsModel();
                String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
                String rule = fieLdsModel.getConfig().getRule();
                String format = fieLdsModel.getFormat();
                Object data = allDataMap.get(key);
                //处理字段
                String file = "?,";
                //添加字段
                mastFile.append(key + ",");
                //单据规则有值判断
                if (billData.get(key) != null) {
                    mastValue.add(billData.get(key));
                } else {
                    //处理系统自动生成
                    data = create(jnpfkey, data, rule);
                    data = temp(jnpfkey, data, format);
                    mastValue.add(data);
                }
                if (dataSourceUtil.getDataType().toLowerCase().contains("oracle") && "date".equals(jnpfkey)) {
                    if (String.valueOf(data).length() < 11) {
                        file = "to_date(?,'yyyy-mm-dd HH24:mi:ss'),";
                    }
                }
                mastFileValue.append(file);
                result.put(key, data);
            } else {
                Map<String, Object> childData = childCreate(allDataMap, conn, tableForm, tableModelList, mainId, key);
                result.putAll(childData);
            }
        }
        //主表去掉最后
        mastFile = mastFile.deleteCharAt(mastFile.length() - 1).append(")");
        mastFileValue = mastFileValue.deleteCharAt(mastFileValue.length() - 1).append(")");
        mastSql.append(mastFile + " VALUES " + mastFileValue);
        //插入主表数据
        mastSql(mastSql, mastValue, mainId, null, conn);
        return result;
    }

    /**新增子表数据**/
    private Map<String,Object> childCreate(Map<String, Object> allDataMap, Connection conn , List<FormAllModel> tableForm, List<FlowTableModel> tableModelList, String mainId, String key) throws SQLException, DataException {
        //处理好的数据
        Map<String, Object> result = new HashMap<>(16);
        FormAllModel childModel = tableForm.stream().filter(t -> key.equals(t.getChildList().getTableModel())).findFirst().orElse(null);
        if (childModel != null) {
            //子表主键
            List<FormColumnModel> childList = childModel.getChildList().getChildList();
            String childTable = childModel.getChildList().getTableName();
            String childKeyName = getPKey(conn, childTable);
            //关联字段
            String mastKeyName = tableModelList.stream().filter(t -> t.getTable().equals(childTable)).findFirst().get().getTableField();
            StringBuffer childFile = new StringBuffer();
            List<List<Object>> childData = new LinkedList<>();
            List<Map<String, Object>> childDataMap = (List<Map<String, Object>>) allDataMap.get(key);
            //子表处理的数据
            List<Map<String, Object>> childResult = new ArrayList<>();
            //子表的字段
            Map<String, String> child = new HashMap<>(16);
            for (FormColumnModel columnModel : childList) {
                String vmodel = columnModel.getFieLdsModel().getVModel();
                String jnpfKey = columnModel.getFieLdsModel().getConfig().getJnpfKey();
                child.put(vmodel, jnpfKey);
            }
            int num = 0;
            for (Map<String, Object> objectMap : childDataMap) {
                //子表处理的数据
                StringBuffer fileAll = new StringBuffer("(");
                StringBuffer fileValueAll = new StringBuffer("(");
                List<Object> value = new LinkedList<>();
                //子表主键
                value.add(RandomUtil.uuId());
                fileAll.append(childKeyName + ",");
                fileValueAll.append("?,");
                //关联字段
                value.add(mainId);
                fileAll.append(mastKeyName + ",");
                fileValueAll.append("?,");
                //子表单体处理的数据
                Map<String, Object> childOneResult = new HashMap<>(16);
                for (String childKey : child.keySet()) {
                    FormColumnModel columnModel = childList.stream().filter(t -> childKey.equals(t.getFieLdsModel().getVModel())).findFirst().orElse(null);
                    if (columnModel != null) {
                        FieLdsModel fieLdsModel = columnModel.getFieLdsModel();
                        String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
                        String rule = fieLdsModel.getConfig().getRule();
                        String format = fieLdsModel.getFormat();
                        Object data = objectMap.get(childKey);
                        //处理系统自动生成
                        data = create(jnpfkey, data, rule);
                        data = temp(jnpfkey, data, format);
                        //添加字段
                        fileAll.append(childKey + ",");
                        fileValueAll.append("?,");
                        if (dataSourceUtil.getDataType().toLowerCase().contains("oracle") && "date".equals(jnpfkey)) {
                            if (format.length() < 11) {
                                data = data + " 00:00:00";
                            }
                            value.add("to_date('" + data + "','YYYY-MM-DD HH24:MI:SS'");
                        } else {
                            value.add(data);
                        }
                        childOneResult.put(childKey, data);
                    }
                }
                childResult.add(childOneResult);
                //子表去掉最后
                if (num == 0) {
                    fileAll = fileAll.deleteCharAt(fileAll.length() - 1).append(")");
                    fileValueAll = fileValueAll.deleteCharAt(fileValueAll.length() - 1).append(")");
                    //添加单行的数据
                    childFile.append(fileAll.toString() + " VALUES " + fileValueAll);
                    num++;
                }
                childData.add(value);
            }
            String[] delSql = new String[]{};
            //插入子表数据
            tableSql(childFile, childData, childTable, delSql, conn);
            result.put(key, childResult);
        }
        return result;
    }

    /**
     * 无表插入数据
     **/
    private Map<String, Object> create(Map<String, Object> allDataMap, List<FormAllModel> formAllModel, Map<String, String> billData) throws DataException {
        //处理好的数据
        Map<String, Object> result = new HashMap<>(16);
        List<FormAllModel> mastForm = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FormAllModel> tableForm = formAllModel.stream().filter(t -> FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        for (String key : allDataMap.keySet()) {
            FormAllModel model = mastForm.stream().filter(t -> key.equals(t.getFormColumnModel().getFieLdsModel().getVModel())).findFirst().orElse(null);
            if (model != null) {
                FieLdsModel fieLdsModel = model.getFormColumnModel().getFieLdsModel();
                String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
                String rule = fieLdsModel.getConfig().getRule();
                Object data = allDataMap.get(key);
                //单据规则有值判断
                String bill = billData.get(key.toLowerCase());
                if (bill != null) {
                    result.put(key, bill);
                } else {
                    //处理系统自动生成
                    data = create(jnpfkey, data, rule);
                    result.put(key, data);
                }
            } else {
                FormAllModel childModel = tableForm.stream().filter(t -> key.equals(t.getChildList().getTableModel())).findFirst().orElse(null);
                if (childModel != null) {
                    //子表主键
                    List<FormColumnModel> childList = childModel.getChildList().getChildList();
                    List<Map<String, Object>> childDataMap = (List<Map<String, Object>>) allDataMap.get(key);
                    //子表处理的数据
                    List<Map<String, Object>> childResult = new ArrayList<>();
                    for (Map<String, Object> objectMap : childDataMap) {
                        //子表单体处理的数据
                        Map<String, Object> childOneResult = new HashMap<>(16);
                        for (String childKey : objectMap.keySet()) {
                            FormColumnModel columnModel = childList.stream().filter(t -> childKey.equals(t.getFieLdsModel().getVModel())).findFirst().orElse(null);
                            if (columnModel != null) {
                                FieLdsModel fieLdsModel = columnModel.getFieLdsModel();
                                String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
                                String rule = fieLdsModel.getConfig().getRule();
                                Object data = objectMap.get(childKey);
                                //处理系统自动生成
                                data = create(jnpfkey, data, rule);
                                childOneResult.put(childKey, data);
                            }
                        }
                        childResult.add(childOneResult);
                    }
                    result.put(key, childResult);
                }
            }
        }
        return result;
    }

    /**
     * 子表插入数据
     **/
    private void tableSql(StringBuffer childFile, List<List<Object>> childData, String childTable, String[] del, Connection conn) throws SQLException {
        if (del.length > 0) {
            PreparedStatement delete = conn.prepareStatement(del[0]);
            delete.setObject(1, del[1]);
            delete.addBatch();
            delete.executeBatch();
        }
        for (int i = 0; i < childData.size(); i++) {
            conn.setAutoCommit(false);
            List<Object> data = childData.get(i);
            boolean result = data.size() > 2;
            String sql = "INSERT INTO " + childTable + " " + childFile.toString();
            PreparedStatement save = conn.prepareStatement(sql);
            if (result) {
                for (int k = 0; k < data.size(); k++) {
                    save.setObject(k + 1, data.get(k));
                }
                save.addBatch();
            }
            save.executeBatch();
        }
        conn.commit();
    }

    /**
     * 主表插入语句
     **/
    private void mastSql(StringBuffer mastSql, List<Object> mastValue, String mainId, String delteSql, Connection conn) throws SQLException {
        conn.setAutoCommit(false);
        if (StringUtil.isNotEmpty(delteSql)) {
            PreparedStatement delete = conn.prepareStatement(delteSql);
            delete.setObject(1, mainId);
            delete.addBatch();
            delete.executeBatch();
        }
        PreparedStatement save = conn.prepareStatement(mastSql.toString());
        int num = 1;
        save.setObject(num, mainId);
        num++;
        for (Object data : mastValue) {
            save.setObject(num, data);
            num++;
        }
        save.addBatch();
        save.executeBatch();
        conn.commit();
        conn.close();
    }

    /**
     * 新增系统赋值
     **/
    private Object create(String jnpfKey, Object dataValue, String rule) throws DataException {
        UserInfo userInfo = userProvider.get();
        Object value = dataValue;
        switch (jnpfKey) {
            case SmartKeyConsts.CREATEUSER:
                value = userInfo.getUserId();
                break;
            case SmartKeyConsts.CREATETIME:
                value = DateUtil.getNow("+8");
                break;
            case SmartKeyConsts.CURRORGANIZE:
                value = userInfo.getOrganizeId();
                break;
            case SmartKeyConsts.CURRDEPT:
                value = userInfo.getDepartmentId();
                break;
            case SmartKeyConsts.MODIFYTIME:
                value = null;
                break;
            case SmartKeyConsts.MODIFYUSER:
                value = null;
                break;
            case SmartKeyConsts.CURRPOSITION:
                UserInfoVO userEntity = usersApi.getInfo(userInfo.getUserId()).getData();
                PositionInfoVO positionEntity = positionApi.getInfo(userEntity.getPositionId().split(",")[0]).getData();
                value = positionEntity != null ? positionEntity.getId() : "";
                break;
            case SmartKeyConsts.BILLRULE:
                value = billRuleApi.getBillNumber(rule).getData();
                break;
            default:
                break;
        }
        return value;
    }

    /**
     * 转换时间和其他的类型
     **/
    private Object temp(String jnpfKey, Object dataValue, String format) {
        if (SmartKeyConsts.DATE.equals(jnpfKey)) {
            if (dataValue != null) {
                dataValue = DateUtil.dateToString(new Date(Long.valueOf(String.valueOf(dataValue))), format);
            }
        } else if (dataValue != null) {
            dataValue = String.valueOf(dataValue);
        }
        return dataValue;
    }

}
