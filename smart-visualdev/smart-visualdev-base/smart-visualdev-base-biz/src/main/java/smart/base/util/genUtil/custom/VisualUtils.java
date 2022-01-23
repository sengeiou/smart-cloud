package smart.base.util.genUtil.custom;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.DbType;
import smart.base.UserInfo;
import smart.base.VisualdevEntity;
import smart.base.model.ColumnDataModel;
import smart.base.model.FormDataModel;
import smart.base.model.TableFields;
import smart.config.ConfigValueUtil;
import smart.onlinedev.entity.VisualdevModelDataEntity;
import smart.exception.DataException;
import smart.base.vo.DownloadVO;
import smart.onlinedev.model.TimeControl;
import smart.onlinedev.model.fields.FieLdsModel;
import smart.onlinedev.model.fields.config.ConfigModel;
import smart.onlinedev.model.fields.props.PropsBeanModel;
import smart.util.*;
import smart.util.JsonUtil;
import smart.util.context.SpringContext;
import smart.util.visiual.DataTypeConst;
import smart.util.visiual.SmartKeyConsts;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 可视化工具类
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021年3月13日16:37:40
 */
@Slf4j
public class VisualUtils {


    private static DataSourceUtil dataSourceUtil;
    private static ConfigValueUtil configValueUtil;
    private static UserProvider userProvider;

    /**
     * 初始化
     */
    public static void init() {
        configValueUtil = SpringContext.getBean(ConfigValueUtil.class);
        userProvider = SpringContext.getBean(UserProvider.class);
        dataSourceUtil = SpringContext.getBean(DataSourceUtil.class);
    }


    /**
     * 去除无意义控件，并且字段转小写
     *
     * @return
     */
    public static List<FieLdsModel> deleteVmodel(List<FieLdsModel> modelList) {
        List<FieLdsModel> newModelList = new ArrayList<>();
        for (FieLdsModel model : modelList) {
            if (SmartKeyConsts.CHILD_TABLE.equals(model.getConfig().getJnpfKey())) {
                List<FieLdsModel> childModelList = JsonUtil.getJsonToList(model.getConfig().getChildren(), FieLdsModel.class);
                List<FieLdsModel> newchildModelList = new ArrayList<>();
                for (FieLdsModel childModel : childModelList) {
                    if (StringUtil.isNotEmpty(childModel.getVModel())) {
                        newchildModelList.add(childModel);
                    }
                }
                model.getConfig().setChildren(newchildModelList);
                newModelList.add(model);
            } else {
                if (StringUtil.isNotEmpty(model.getVModel())) {
                    newModelList.add(model);
                }
            }
        }
        return newModelList;
    }

    /**
     * 去除多级嵌套控件
     *
     * @return
     */
    public static List<FieLdsModel> deleteMoreVmodel(FieLdsModel model) {
        if ("".equals(model.getVModel()) && model.getConfig().getChildren() != null) {
            List<FieLdsModel> childModelList = JsonUtil.getJsonToList(model.getConfig().getChildren(), FieLdsModel.class);
            return childModelList;
        }
        return null;
    }

    public static List<FieLdsModel> deleteMore(List<FieLdsModel> modelList) {
        List<FieLdsModel> newModelList = new ArrayList<>();
        for (FieLdsModel model : modelList) {
            List<FieLdsModel> newList = deleteMoreVmodel(model);
            if (newList == null || SmartKeyConsts.CHILD_TABLE.equals(model.getConfig().getJnpfKey())) {
                newModelList.add(model);
            } else {
                newModelList.addAll(deleteMore(newList));
            }
        }
        return newModelList;
    }

    /**
     * 获取有表列表数据
     *
     * @param conn
     * @param sql
     * @param pKeyName
     * @return
     * @throws DataException
     */
    public static List<VisualdevModelDataEntity> getTableDataList(Connection conn, String sql, String pKeyName) throws DataException {
        List<VisualdevModelDataEntity> list = new ArrayList<>();
        ResultSet rs = JdbcUtil.query(conn, sql);
        List<Map<String, Object>> dataList = JdbcUtil.convertList(rs);
        for (Map<String, Object> dataMap : dataList) {
            VisualdevModelDataEntity dataEntity = new VisualdevModelDataEntity();
            dataMap = toLowerKey(dataMap);
            dataEntity.setData(JsonUtil.getObjectToStringDateFormat(dataMap, "yyyy-MM-dd HH:mm"));
            if (dataMap.containsKey(pKeyName.toUpperCase())) {
                dataEntity.setId(String.valueOf(dataMap.get(pKeyName.toUpperCase())));
            }
            list.add(dataEntity);
        }
        return list;
    }

    /**
     * 判断是否存在主键
     *
     * @param feilds
     * @param pKeyName
     * @return
     */
    public static Boolean existKey(String feilds, String pKeyName) {
        String[] strs = feilds.split(",");
        if (strs.length > 0) {
            for (String feild : strs) {
                if (feild.equals(pKeyName)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 获取不同情况下的插入语句
     *
     * @param table
     * @param feilds
     * @return
     */
    public static String getInsertSql(String table, String feilds, String mainpKeyName, String childPkName, String mainId) {
        StringBuilder insertSql = new StringBuilder();

        feilds = feilds.toLowerCase().trim();
        mainpKeyName = mainpKeyName.toLowerCase();
        childPkName = childPkName.toLowerCase();
        if (existKey(feilds, mainpKeyName) && existKey(feilds, childPkName)) {
            insertSql.append("INSERT INTO " + table + "(" + feilds + ") " + " VALUES (");
        } else if (existKey(feilds, mainpKeyName) && !existKey(feilds, childPkName)) {
            insertSql.append("INSERT INTO " + table + "(" + childPkName + "," + feilds + ") " + " VALUES ('" + RandomUtil.uuId() + "',");
        } else if (!existKey(feilds, mainpKeyName) && existKey(feilds, childPkName)) {
            insertSql.append("INSERT INTO " + table + "(" + mainpKeyName + "," + feilds + ") " + " VALUES ('" + mainId + "',");
        } else {
            insertSql.append("INSERT INTO " + table + "(" + childPkName + "," + mainpKeyName + "," + feilds + ") " + " VALUES ('" + RandomUtil.uuId() + "','" + mainId + "',");
        }
        return insertSql.toString();
    }

    /**
     * 获取不同情况下的插入语句
     *
     * @param baseSql
     * @return
     */
    public static String getRealSql(String baseSql, String mainId) {
        String[] sql = baseSql.split(",");
        String realSql = baseSql;
        if (sql.length == 2) {
            realSql = "('" + RandomUtil.uuId() + "'," + sql[1] + ",";
        } else if (sql.length == 1 && !baseSql.contains(mainId)) {
            realSql = "('" + RandomUtil.uuId() + "',";
        }
        return realSql;
    }

    /**
     * 返回主键名称
     *
     * @param conn
     * @param mainTable
     * @return
     */
    public static String getpKey(Connection conn, String mainTable) throws SQLException {
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


    /**
     * 获取有表单条数据
     * @param sql
     * @return
     * @throws DataException
     */
    public static List<Map<String, Object>> getTableDataInfo(String sql) throws DataException {
        Connection conn = getTableConn();
        ResultSet rs = JdbcUtil.query(conn, sql);
        List<Map<String, Object>> dataList = JdbcUtil.convertList(rs);
        //转换子表字符串成数组
        for (Map<String, Object> dataMap : dataList) {
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                //判断是否是字符串数组时间戳，如果是则转成Json数组时间戳
                if (entry.getValue() != null && String.valueOf(entry.getValue()).contains(",") && !"[\"".equals(String.valueOf(entry.getValue()).substring(0, 2))) {
                    JSONArray list = JsonUtil.getJsonToJsonArray(String.valueOf(entry.getValue()));
                    entry.setValue(list);
                }
            }
        }
        return dataList;
    }

    /**
     * 转换有表单条数据格式，子表多条数据
     *
     * @param modelList
     * @param dataMapList
     * @return
     * @throws ParseException
     */
    public static List<Map<String, Object>> swapTableDataInfoList(List<FieLdsModel> modelList, List<Map<String, Object>> dataMapList) throws ParseException {
        List<Map<String, Object>> newDataMapList = new ArrayList<>();
        for (Map<String, Object> dataMap : dataMapList) {
            Map<String, Object> newDataMap = new HashMap<>(16);
            for (FieLdsModel fieLdsModel : modelList) {
                for (Map.Entry<String, Object> entryMap : dataMap.entrySet()) {
                    if (entryMap.getKey().equals(fieLdsModel.getVModel())) {
                        if (SmartKeyConsts.UPLOADFZ.equals(fieLdsModel.getConfig().getJnpfKey()) || SmartKeyConsts.UPLOADIMG.equals(fieLdsModel.getConfig().getJnpfKey())) {
                            String value = String.valueOf(entryMap.getValue());
                            if (!"[]".equals(value)) {
                                List<Map<String, Object>> map = JsonUtil.getJsonToListMap(value);
                                newDataMap.put(entryMap.getKey(), map);
                            }
                        } else if ("checkbox".equals(fieLdsModel.getConfig().getJnpfKey())) {
                            List<String> list = JsonUtil.getJsonToList(String.valueOf(entryMap.getValue()), String.class);
                            newDataMap.put(entryMap.getKey(), list);
                        } else if (SmartKeyConsts.CREATETIME.equals(fieLdsModel.getConfig().getJnpfKey()) || SmartKeyConsts.MODIFYTIME.equals(fieLdsModel.getConfig().getJnpfKey())) {
                            newDataMap.put(entryMap.getKey(), String.valueOf(entryMap.getValue()));
                        } else if (fieLdsModel.getConfig().getJnpfKey().contains("date") && entryMap.getValue() != null) {
                            if (entryMap.getValue().toString().contains(",")) {
                                List<String> list = JsonUtil.getJsonToList(String.valueOf(entryMap.getValue()), String.class);
                                SimpleDateFormat sdf = new SimpleDateFormat(fieLdsModel.getFormat());
                                List<Object> newList = new ArrayList<>();
                                for (String dateStr : list) {
                                    try {
                                        newList.add(Long.valueOf(dateStr));
                                    } catch (Exception e) {
                                        Long s = sdf.parse(dateStr).getTime();
                                        newList.add(s.toString());
                                    }
                                }
                                newDataMap.put(entryMap.getKey(), newList);
                            } else {
                                SimpleDateFormat sdf = new SimpleDateFormat(fieLdsModel.getFormat());
                                Long s = sdf.parse(entryMap.getValue().toString()).getTime();
                                newDataMap.put(entryMap.getKey(), s);
                            }
                        } else {
                            newDataMap.put(entryMap.getKey(), entryMap.getValue());
                        }
                    }
                }
            }
            newDataMapList.add(newDataMap);
        }

        return newDataMapList;
    }


    /**
     * 转换有表数据格式,单条数据
     *
     * @param modelList
     * @param dataMap
     * @return
     * @throws ParseException
     */
    public static Map<String, Object> swapTableDataInfoOne(List<FieLdsModel> modelList, Map<String, Object> dataMap) throws ParseException {
        Map<String, Object> newDataMap = new HashMap<>(16);
        for (FieLdsModel fieLdsModel : modelList) {
            for (Map.Entry<String, Object> entryMap : dataMap.entrySet()) {
                if (entryMap.getKey().equals(fieLdsModel.getVModel())) {
                    if (SmartKeyConsts.UPLOADFZ.equals(fieLdsModel.getConfig().getJnpfKey()) || SmartKeyConsts.UPLOADIMG.equals(fieLdsModel.getConfig().getJnpfKey())) {
                        String value = String.valueOf(entryMap.getValue());
                        if (!"[]".equals(value)) {
                            List<Map<String, Object>> map = JsonUtil.getJsonToListMap(value);
                            newDataMap.put(entryMap.getKey(), map);
                        }
                    } else if (SmartKeyConsts.CHECKBOX.equals(fieLdsModel.getConfig().getJnpfKey())) {
                        List<String> list = JsonUtil.getJsonToList(String.valueOf(entryMap.getValue()), String.class);
                        newDataMap.put(entryMap.getKey(), list);
                    } else if (SmartKeyConsts.CREATETIME.equals(fieLdsModel.getConfig().getJnpfKey()) || SmartKeyConsts.MODIFYTIME.equals(fieLdsModel.getConfig().getJnpfKey())) {
                        newDataMap.put(entryMap.getKey(), String.valueOf(entryMap.getValue()));
                    } else if (fieLdsModel.getConfig().getJnpfKey().contains("date") && entryMap.getValue() != null) {
                        if (entryMap.getValue().toString().contains(",")) {
                            List<String> list = JsonUtil.getJsonToList(String.valueOf(entryMap.getValue()), String.class);
                            List<String> newList = new ArrayList<>();
                            SimpleDateFormat sdf = new SimpleDateFormat(fieLdsModel.getFormat());
                            for (String dateStr : list) {
                                try {
                                    Long.valueOf(dateStr);
                                } catch (Exception e) {
                                    Long s = sdf.parse(dateStr).getTime();
                                    newList.add(s.toString());
                                }
                            }
                            newDataMap.put(entryMap.getKey(), newList);
                        } else {
                            SimpleDateFormat sdf = new SimpleDateFormat(fieLdsModel.getFormat());
                            Long s = sdf.parse(entryMap.getValue().toString()).getTime();
                            newDataMap.put(entryMap.getKey(), s);
                        }
                    } else {
                        newDataMap.put(entryMap.getKey(), entryMap.getValue());
                    }
                }
            }
        }
        return newDataMap;
    }


    /**
     * 增加删除修改有表单条数据
     * @param sql
     */
    public static void opaTableDataInfo(String sql) throws DataException {
        Connection conn = getTableConn();
        JdbcUtil.custom(conn, sql);
    }

    /**
     * 获取有表的数据库连接
     * @return
     */
    public static Connection getTableConn() {
        init();
        String tenId;
        if (!Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
            tenId = dataSourceUtil.getDbName();
        } else {
            tenId = userProvider.get().getTenantDbConnectionString();
        }
        return JdbcUtil.getConn(dataSourceUtil.getUserName(), dataSourceUtil.getPassword(), dataSourceUtil.getUrl().replace("{dbName}", tenId));
    }


    /**
     * 获取表总记录数
     * @param sql
     * @return
     * @throws SQLException
     * @throws DataException
     */
    public static Integer getTableDataCount(String sql) throws SQLException, DataException {
        init();
        String tenId;
        if (!Boolean.parseBoolean(configValueUtil.getMultiTenancy())) {
            tenId = dataSourceUtil.getDbName();
        } else {
            tenId = userProvider.get().getTenantDbConnectionString();
        }

        Connection conn = JdbcUtil.getConn(dataSourceUtil.getUserName(), dataSourceUtil.getPassword(), dataSourceUtil.getUrl().replace("{dbName}", tenId));
        ResultSet rs = JdbcUtil.query(conn, sql);
        int count = 0;
        while (rs.next()) {
            count = rs.getInt("rec");
        }
        return count;
    }

    /**
     * 为字段数据字典赋值
     * @param fieldList
     * @param fieldStr
     * @param props
     * @param options
     * @param fieLdsModel
     * @return
     */
    public static String setDicValue(List<String> fieldList, String fieldStr, PropsBeanModel props, List<Map<String, Object>> options, FieLdsModel fieLdsModel) {
        if (fieldList != null && fieldList.size() > 0) {
            for (String fieStr : fieldList) {

                for (Map<String, Object> optMap : options) {
                    if (fieLdsModel.getSlot().getOptions() != null) {
                        String label;
                        String value;
                        if (props != null) {
                            label = props.getLabel();
                            value = props.getValue();
                            if (fieStr.equals(optMap.get(value).toString())) {
                                return optMap.get(label).toString();
                            }
                        }
                    }
                }
            }
        } else {
            for (Map<String, Object> optMap : options) {
                if (fieLdsModel.getSlot().getOptions() != null) {
                    //判断prop是否有值，是否有取别名
                    String label;
                    String value;
                    if (props != null) {
                        label = props.getLabel();
                        value = props.getValue();
                        if (fieldStr.equals(optMap.get(value).toString())) {
                            return optMap.get(label).toString();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 去除多余字段
     *
     * @return
     */
    public static List<VisualdevModelDataEntity> deleteKey(List<VisualdevModelDataEntity> list, String[] keys) {
        for (VisualdevModelDataEntity entity : list) {
            if (!StringUtil.isEmpty(entity.getData()) && keys.length > 0) {
                Map<String, Object> keyMap = JsonUtil.stringToMap(entity.getData());
                Map<String, Object> keyResult = new HashMap<>(16);

                for (String selkey : keys) {
                    for (Map.Entry<String, Object> entry : keyMap.entrySet()) {
                        String key = entry.getKey();
                        if (key.equals(selkey)) {
                            keyResult.put(key, entry.getValue());
                        }
                    }
                }
                entity.setData(JSON.toJSONString(keyResult));
            }
        }
        return list;
    }

    /**
     * 判断字段数据是数组还是字符串
     * @param field
     * @return
     */
    public static List<String> analysisField(String field) {
        List<String> keyList = new ArrayList<>();
        if (field != null) {
            try {
                keyList = JsonUtil.getJsonToList(field, String.class);
                if (keyList != null) {
                    return keyList;
                }
                return new ArrayList<>();
            } catch (Exception e) {
                return keyList;
            }
        }
        return keyList;
    }

    /**
     * 导出在线开发的表格
     * @param formData
     * @param path
     * @param list
     * @param keys
     * @param userInfo
     * @return
     */
    public static DownloadVO createModelExcel(String formData, String path, List<Map<String, Object>> list, String[] keys, UserInfo userInfo) {
        DownloadVO vo = DownloadVO.builder().build();
        try {
            FormDataModel formDataModel = JsonUtil.getJsonToBean(formData, FormDataModel.class);
            List<FieLdsModel> fieLdsModelList = JsonUtil.getJsonToList(formDataModel.getFields(), FieLdsModel.class);

            List<ExcelExportEntity> entitys = new ArrayList<>();
            for (FieLdsModel model : fieLdsModelList) {
                if (keys.length > 0) {
                    for (String key : keys) {
                        if (key.equals(model.getVModel())) {
                            entitys.add(new ExcelExportEntity(model.getConfig().getLabel(), model.getVModel()));
                        }
                    }
                }
            }
            ExportParams exportParams = new ExportParams(null, "表单信息");
            Workbook workbook = ExcelExportUtil.exportExcel(exportParams, entitys, list);
            String fileName = "表单信息" + DateUtil.dateNow("yyyyMMddHHmmss") + ".xls";
            vo.setName(fileName);
            vo.setUrl(UploaderUtil.uploaderFile(userInfo.getId() + "#" + fileName + "#" + "Temporary"));
            path = path + fileName;
            FileOutputStream fos = new FileOutputStream(path);
            workbook.write(fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vo;
    }

    public static List<String> getLabelMapSize(List<Map<String, Object>> optMapList, String label, String value, List<String> fieldList) {
        if (optMapList.size() > 0) {
            List<String> result = new ArrayList<>();
            for (Map<String, Object> optMap : optMapList) {
                List<Map<String, Object>> optChildMapList = JsonUtil.getJsonToListMap(JsonUtil.getObjectToString(optMap.get("children")));
                if (fieldList.contains(String.valueOf(optMap.get(value)))) {
                    result.add(String.valueOf(optMap.get(label)));
                }
                if (optChildMapList != null && optChildMapList.size() > 0) {
                    result.addAll(getLabelMapSize(optChildMapList, label, value, fieldList));
                    return result;
                }
            }
            return result;
        }
        return null;
    }

    public static List<String> getValueMapSize(List<Map<String, Object>> optMapList, String value, List<String> fieldList) {
        if (optMapList.size() > 0) {
            List<String> result = new ArrayList<>();
            for (Map<String, Object> optMap : optMapList) {
                List<Map<String, Object>> optChildMapList = JsonUtil.getJsonToListMap(JsonUtil.getObjectToString(optMap.get("children")));
                if (fieldList.contains(String.valueOf(optMap.get(value)))) {
                    result.add(String.valueOf(optMap.get(value)));
                }
                if (optChildMapList != null && optChildMapList.size() > 0) {
                    result.addAll(getValueMapSize(optChildMapList, value, fieldList));
                    return result;
                }
            }
            return result;
        }
        return null;
    }

    public static List<String> analysisLabelMap(Map<String, Object> optMap, FieLdsModel fieLdsModel, PropsBeanModel props, List<String> fieldList) {
        List<String> list = new ArrayList<>();
        if (fieLdsModel.getProps() != null && fieLdsModel.getProps().getProps() != null) {
            //判断prop是否有值，是否有取别名
            String label;
            String value;
            if (props != null) {
                label = props.getLabel();
                value = props.getValue();
                String lab = String.valueOf(optMap.get(label));
                String vau = String.valueOf(optMap.get(value));
                if (fieldList.contains(vau)) {
                    list.add(lab);
                }
                if (optMap.containsKey("children")) {
                    List<String> other = getLabelMapSize(JsonUtil.getJsonToListMap(JsonUtil.getObjectToString(optMap.get("children"))), label, value, fieldList);
                    if (other != null) {
                        list.addAll(other);
                    }
                }
            }
        }
        return list;
    }

    public static List<String> analysisValueMap(Map<String, Object> optMap, FieLdsModel fieLdsModel, PropsBeanModel props, List<String> fieldList) {
        List<String> list = new ArrayList<>();
        if (fieLdsModel.getProps() != null && fieLdsModel.getProps().getProps() != null) {
            //判断prop是否有值，是否有取别名
            String value;
            if (props != null) {
                value = props.getValue();
                String vau = String.valueOf(optMap.get(value));
                if (fieldList.contains(vau)) {
                    list.add(vau);
                }
                if (optMap.containsKey("children")) {
                    List<String> other = getValueMapSize(JsonUtil.getJsonToListMap(JsonUtil.getObjectToString(optMap.get("children"))), value, fieldList);
                    if (other != null) {
                        list.addAll(other);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 级联选择框和树形选择单独操作（静态）
     * @param fieLdsModel
     * @param fieldList
     * @param keyJsonMap
     * @return
     */
    public static Map<String, Object> cascaderOperation(FieLdsModel fieLdsModel, List<String> fieldList, Map<String, Object> keyJsonMap) {
        Map<String, Object> cascaderMap = new HashMap<>(16);
        //判断是不是级联选择框
        if (fieLdsModel.getOptions() != null) {
            List<String> cascaderList = new ArrayList<>();
            List<String> keyListLast = new ArrayList<>();
            List<Map<String, Object>> options = JsonUtil.getJsonToListMap(fieLdsModel.getOptions());
            PropsBeanModel props = JsonUtil.getJsonToBean(fieLdsModel.getProps().getProps(), PropsBeanModel.class);
            for (Map<String, Object> optMap : options) {
                List<String> labelList = VisualUtils.analysisLabelMap(optMap, fieLdsModel, props, fieldList);
                List<String> valueList = VisualUtils.analysisValueMap(optMap, fieLdsModel, props, fieldList);
                if (labelList.size() == fieldList.size()) {
                    for (int k = 0; k < labelList.size(); k++) {
                        if (fieldList.get(k).equals(valueList.get(k))) {
                            cascaderList.add(labelList.get(k));
                        }
                    }
                    //级联选择框查询字段转换
                    if (keyJsonMap != null && keyJsonMap.get(fieLdsModel.getVModel()) != null) {
                        List<String> keyList = JsonUtil.getJsonToList(keyJsonMap.get(fieLdsModel.getVModel()), String.class);
                        for (int k = 0; k < labelList.size(); k++) {
                            if (keyList.get(k).equals(valueList.get(k))) {
                                keyListLast.add(labelList.get(k));
                            }
                        }
                        if (keyListLast.size() == labelList.size()) {
                            keyJsonMap.put(fieLdsModel.getVModel(), keyListLast);

                        }
                    }
                    cascaderMap.put("keyJsonMap", keyJsonMap);
                    //级联选择框列表转换
                    if (cascaderList.size() == labelList.size()) {
                        cascaderMap.put("value", cascaderList);
                    }
                }
            }
        }
        return cascaderMap;
    }


    /**
     * 树形选择单独操作（静态）
     *
     * @param fieLdsModel
     * @param fieldStr
     * @return
     */
    public static String treeSelectOperation(FieLdsModel fieLdsModel, String fieldStr) {
        //判断是不是级联选择框
        if (fieLdsModel.getOptions() != null) {
            List<Map<String, Object>> options = JsonUtil.getJsonToListMap(fieLdsModel.getOptions());
            PropsBeanModel props = JsonUtil.getJsonToBean(fieLdsModel.getProps().getProps(), PropsBeanModel.class);
            String value = props.getValue();
            String label = props.getLabel();
            for (Map<String, Object> optMap : options) {
                if (optMap.get(value).toString().equals(fieldStr)) {
                    return optMap.get(label).toString();
                } else if (optMap.get("children") != null) {
                    List<Map<String, Object>> anotherOptions = (List<Map<String, Object>>) optMap.get("children");
                    String another = treeSelectGetValue(value, label, anotherOptions, fieldStr);
                    if (!fieldStr.equals(another)) {
                        return another;
                    }
                }
            }
        }
        return fieldStr;
    }

    /**
     * 获取树形选择值
     *
     * @param value
     * @param label
     * @param anotherOptions
     * @param fieldStr
     * @return
     */
    public static String treeSelectGetValue(String value, String label, List<Map<String, Object>> anotherOptions, String fieldStr) {
        for (Map<String, Object> optMap : anotherOptions) {
            if (optMap.get(value).toString().equals(fieldStr)) {
                return optMap.get(label).toString();
            } else if (optMap.get("children") != null) {
                List<Map<String, Object>> childOptions = (List<Map<String, Object>>) optMap.get("children");
                String another = treeSelectGetValue(value, label, childOptions, fieldStr);
                if (StringUtil.isNotEmpty(another)) {
                    return another;
                }
            }
        }
        return fieldStr;
    }


    /**
     * 得到时间范围的工具
     *
     * @return
     */
    public static List<Map<String, Object>> getRealList(Map<String, Object> keyJsonMap, List<VisualdevModelDataEntity> list, TimeControl timeControl) throws ParseException, ParseException {
        List<Map<String, Object>> realList = new ArrayList<>();
        for (VisualdevModelDataEntity entity : list) {
            Map<String, Object> m1 = keyJsonMap;
            Map<String, Object> m2 = JsonUtil.stringToMap(entity.getData());
            if (m1 != null && m1.size() != 0) {
                //添加关键词全匹配计数，全符合条件则添加
                int i = 0;
                for (Map.Entry<String, Object> entry1 : m1.entrySet()) {
                    Object m1value = entry1.getValue() == null ? "" : entry1.getValue();
                    Object m2value = m2.get(entry1.getKey()) == null ? "" : m2.get(entry1.getKey());
                    //若两个map中相同key对应的value相等
                    if (!StringUtil.isEmpty(m1value.toString()) && !StringUtil.isEmpty(m2value.toString())) {
                        if (m2value.toString().contains(m1value.toString()) && !DateUtil.isValidDate(m2value.toString()) && !entry1.getKey().contains("date")) {
                            m2.put("id", entity.getId());
                            i++;
                        }
                        if (timeControl != null) {
                            if (!StringUtil.isEmpty(timeControl.getDate()) && timeControl.getDate().contains(entry1.getKey())) {
                                //只判断到日期
                                String valuee = m1value.toString().substring(0, 10);
                                if (m2value.toString().contains(valuee)) {
                                    m2.put("id", entity.getId());
                                    i++;
                                }
                            }
                            if (!StringUtil.isEmpty(timeControl.getTimeRange()) && timeControl.getTimeRange().contains(entry1.getKey())) {

                                List<String> list1 = JsonUtil.getJsonToList(m1value, String.class);
                                List<String> list2 = JsonUtil.getJsonToList(m2value, String.class);
                                if (list1.size() == 2 && list2.size() == 2) {
                                    list1.add(0, list1.get(0).substring(0, list1.get(0).length() - 1));
                                    list2.add(0, list2.get(0).substring(0, list2.get(0).length() - 1));
                                    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                                    Date dayTimeStart1 = df.parse(list1.get(0));
                                    Date dayTimeEnd1 = df.parse(list1.get(2));

                                    Date dayTimeStart2 = df.parse(list2.get(0));
                                    Date dayTimeEnd2 = df.parse(list2.get(2));
                                    boolean cont = DateUtil.isOverlap(dayTimeStart1, dayTimeEnd1, dayTimeStart2, dayTimeEnd2);
                                    if (cont) {
                                        m2.put("id", entity.getId());
                                        i++;
                                    }
                                }
                            }
                            if (!StringUtil.isEmpty(timeControl.getDateRange()) && timeControl.getDateRange().contains(entry1.getKey())) {
                                List<String> list1 = JsonUtil.getJsonToList(m1value, String.class);
                                List<String> list2 = JsonUtil.getJsonToList(m2value, String.class);
                                if (list1.size() == 2 && list2.size() == 2) {
                                    list1.add(0, list1.get(0).substring(0, list1.get(0).length() - 1));
                                    list2.add(0, list2.get(0).substring(0, list2.get(0).length() - 1));
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                    if (list1.get(0).length() > 10) {
                                        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    }
                                    Date dayTimeStart1 = df.parse(list1.get(0));
                                    Date dayTimeEnd1 = df.parse(list2.get(0));

                                    Date dayTimeStart2 = df.parse(list2.get(0));
                                    Date dayTimeEnd2 = df.parse(list2.get(2));

                                    boolean cont = DateUtil.isOverlap(dayTimeStart1, dayTimeEnd1, dayTimeStart2, dayTimeEnd2);
                                    if (cont) {
                                        m2.put("id", entity.getId());
                                        i++;
                                    }
                                }
                            }
                        }
                    }

                    if (i == m1.size()) {
                        realList.add(m2);
                    }
                }
            } else {
                m2.put("id", entity.getId());
                realList.add(m2);
            }
        }
        return realList;
    }

    /**
     * String转数组
     *
     * @return
     */
    public static List<VisualdevModelDataEntity> stringToList(List<FieLdsModel> fieLdsModelList, List<VisualdevModelDataEntity> list) {
        for (FieLdsModel fieLdsModel : fieLdsModelList) {
            for (VisualdevModelDataEntity entity : list) {
                Map<String, Object> dataMap = JsonUtil.stringToMap(entity.getData());
                if (SmartKeyConsts.UPLOADFZ.equals(fieLdsModel.getConfig().getJnpfKey()) || SmartKeyConsts.UPLOADIMG.equals(fieLdsModel.getConfig().getJnpfKey())) {
                    for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                        if (entry.getKey().equals(fieLdsModel.getVModel())) {
                            entry.setValue(JsonUtil.getJsonToListMap(entry.getValue().toString()));
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 为选择框赋值（静态）
     * @param fieldList
     * @param fieldStr
     * @param fieLdsModel
     * @return
     */
    public static Object setSelect(List<String> fieldList, String fieldStr, FieLdsModel fieLdsModel) {
        //正常多选列表赋值
        if (fieLdsModel.getSlot() != null && fieLdsModel.getSlot().getOptions() != null) {
            String value = fieLdsModel.getConfig().getProps().getValue();
            String label = fieLdsModel.getConfig().getProps().getLabel();
            //模板选项集合
            List<Map<String, Object>> options = JsonUtil.getJsonToListMap(fieLdsModel.getSlot().getOptions());
            if (fieldList != null && fieldList.size() > 0) {
                //新建多选集合
                List<String> moreValue = new ArrayList<>();
                for (String fieStr : fieldList) {
                    for (Map<String, Object> optMap : options) {
                        if (fieStr.equals(optMap.get(value).toString())) {
                            moreValue.add(optMap.get(label).toString());
                        }
                    }
                }
                //将多个选项赋值给列表选项集合
                return moreValue;
            } else {
                for (Map<String, Object> optMap : options) {
                    if (fieLdsModel.getSlot() != null && fieLdsModel.getSlot().getOptions() != null) {
                        if (optMap.get(value) != null && fieldStr.equals(optMap.get(value).toString())) {
                            return optMap.get(label).toString();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 关联表单字段判断
     * @param value
     * @param type
     * @param configModel
     * @return
     */
    public static Map<String, Object> relaField(String value, String type, ConfigModel configModel) {
        HashSet<String> keyList = new HashSet<>(16);

        Map<String, Object> allKey = new HashMap<>(16);
        switch (type) {
            //单选框
            case SmartKeyConsts.RADIO:
                //下拉框
            case SmartKeyConsts.SELECT:
                if (DataTypeConst.DICTIONARY.equals(configModel.getDataType())) {
                    keyList.add(value);
                }
                break;
            //复选框
            case SmartKeyConsts.CHECKBOX:
                if (DataTypeConst.DICTIONARY.equals(configModel.getDataType())) {
                    //字段数据id
                    List<String> add = VisualUtils.analysisField(value);
                    String addStr = value;
                    if (add.size() > 0) {
                        for (String str : add) {
                            keyList.add(str);
                        }
                    } else {
                        keyList.add(addStr);
                    }
                }
                break;
            //公司
            case SmartKeyConsts.COMSELECT:
                //部门
            case SmartKeyConsts.DEPSELECT:
                if (value.contains(",")) {
                    String[] depSelects = value.split(",");
                    for (String depSelect : depSelects) {
                        keyList.add(depSelect);
                    }
                } else {
                    keyList.add(value);
                }
                break;
            //岗位
            case SmartKeyConsts.POSSELECT:
                if (value.contains(",")) {
                    String[] posSelects = value.split(",");
                    for (String posSelect : posSelects) {
                        keyList.add(posSelect);
                    }
                } else {
                    keyList.add(value);
                }
                break;
            //用户
            case SmartKeyConsts.USERSELECT:
                if (value.contains(",")) {
                    String[] userSelects = value.split(",");
                    for (String userSelect : userSelects) {
                        keyList.add(userSelect);
                    }
                } else {
                    keyList.add(value);
                }
                break;
            //数据字典
            case SmartKeyConsts.DICSELECT:
                keyList.add(value);
                break;
            //省市区
            case SmartKeyConsts.ADDRESS:
                List<String> add = JsonUtil.getJsonToList(value, String.class);
                for (String str : add) {
                    keyList.add(str);
                }
                break;
            default:
        }
        allKey.put(configModel.getJnpfKey(), keyList);
        return allKey;
    }

    /**
     * 关联表单字段赋值
     * @param dataMap
     * @param keyMap
     * @param type
     * @param key
     * @param value
     * @param configModel
     * @param model
     * @return
     * @throws IOException
     */
    public static Map<String, Object> relaFieldValue(Map<String, Object> dataMap, Map<String, Object> keyMap, String type, String key, String value, ConfigModel configModel, FieLdsModel model) throws IOException {
        Map<String, Object> result = new HashMap<>(16);
        switch (type) {
            //单选框
            case SmartKeyConsts.RADIO:
                //下拉框
            case SmartKeyConsts.SELECT:
                if (DataTypeConst.DICTIONARY.equals(configModel.getDataType())) {
                    if (keyMap.containsKey(key)) {
                        result.put("value", keyMap.get(key));
                    }
                }
                if (DataTypeConst.STATIC.equals(configModel.getDataType())) {
                    List<Map<String, Object>> modelOpt = JsonUtil.getJsonToListMap(model.getSlot().getOptions());
                    for (Map<String, Object> map : modelOpt) {
                        if (map.get(configModel.getProps().getValue()).toString().equals(value)) {
                            result.put("value", map.get(model.getConfig().getProps().getLabel()).toString());
                        }
                    }
                }
                if (DataTypeConst.DYNAMIC.equals(configModel.getDataType())) {
                    DynamicUtil dynamicUtil = new DynamicUtil();
                    dataMap = dynamicUtil.dynamicKeyData(model, dataMap);
                    result.put("dataMap", dataMap);
                }
                break;
            //复选框
            case SmartKeyConsts.CHECKBOX:
                if (DataTypeConst.DICTIONARY.equals(configModel.getDataType())) {
                    //字段数据id
                    List<String> add = VisualUtils.analysisField(value);
                    String addStr = value;
                    StringBuilder addName = new StringBuilder();
                    if (add.size() > 0) {
                        for (String str : add) {
                            if (keyMap.containsKey(str)) {
                                addName.append(keyMap.get(str));
                            }
                        }
                    } else {
                        if (keyMap.containsKey(addStr)) {
                            addName.append(keyMap.get(addStr));
                        }
                    }
                    if (addName.length() != 0) {
                        result.put("value", addName);
                    }
                }
                if (DataTypeConst.STATIC.equals(configModel.getDataType())) {
                    if (model.getSlot() != null && model.getSlot().getOptions() != null) {
                        List<Map<String, Object>> modelOpt = JsonUtil.getJsonToListMap(model.getSlot().getOptions());
                        for (Map<String, Object> map : modelOpt) {
                            if (map.get(model.getConfig().getProps().getValue()).toString().equals(value)) {
                                result.put("value", map.get(model.getConfig().getProps().getLabel()));
                            }

                        }
                    }
                }
                if (DataTypeConst.DYNAMIC.equals(configModel.getDataType())) {
                    //获取最新远端数据转换远端数据查询关键词
                    DynamicUtil dynamicUtil = new DynamicUtil();
                    dataMap = dynamicUtil.dynamicKeyData(model, dataMap);
                    result.put("dataMap", dataMap);
                }
                break;
            //公司
            case SmartKeyConsts.COMSELECT:
                //部门
            case SmartKeyConsts.DEPSELECT:
                if (value.contains(",")) {
                    String[] depSelects = value.split(",");
                    String[] newDepSelects = new String[depSelects.length];
                    int i = 0;
                    for (String depSelect : depSelects) {
                        if (keyMap.containsKey(depSelect)) {
                            newDepSelects[i] = String.valueOf(keyMap.get(depSelect));
                        }
                        i++;
                    }
                    result.put("value", newDepSelects);
                } else {
                    String str = value;
                    if (keyMap.containsKey(str)) {
                        result.put("value", keyMap.get(str));
                    }
                }
                break;
            //岗位
            case SmartKeyConsts.POSSELECT:
                if (value.contains(",")) {
                    String[] posSelects = value.split(",");
                    String[] newposSelects = new String[posSelects.length];
                    int i = 0;
                    for (String posSelect : posSelects) {
                        if (keyMap.containsKey(posSelect)) {
                            newposSelects[i] = String.valueOf(keyMap.get(posSelect));
                        }
                        i++;
                    }
                    result.put("value", newposSelects);
                } else {
                    if (keyMap.containsKey(value)) {
                        result.put("value", keyMap.get(value));
                    }
                }
                break;
            //用户
            case SmartKeyConsts.USERSELECT:
                if (value.contains(",")) {
                    String[] userSelects = value.split(",");
                    String[] newuserSelects = new String[userSelects.length];
                    int i = 0;
                    for (String userSelect : userSelects) {
                        if (keyMap.containsKey(userSelect)) {
                            newuserSelects[i] = String.valueOf(keyMap.get(userSelect));
                        }
                        i++;
                    }
                    result.put("value", newuserSelects);
                } else {
                    if (keyMap.containsKey(value)) {
                        result.put("value", keyMap.get(value));
                    }
                }
                break;
            //数据字典
            case SmartKeyConsts.DICSELECT:
                if (keyMap.containsKey(value)) {
                    result.put("value", keyMap.get(value));
                }
                break;
            //省市区
            case SmartKeyConsts.ADDRESS:
                List<String> add = JsonUtil.getJsonToList(value, String.class);
                StringBuilder addName = new StringBuilder();
                for (String str : add) {
                    if (keyMap.containsKey(str)) {
                        addName.append(keyMap.get(str)).append("/");
                    }
                }
                if (addName.length() != 0) {
                    addName.deleteCharAt(addName.length() - 1);
                    result.put("value", addName);
                }
                break;
            //时间范围
            case SmartKeyConsts.TIMERANGE:
                JSONArray jsonArrayTime = JsonUtil.getJsonToJsonArray(String.valueOf(value));
                jsonArrayTime = DateUtil.addCon(jsonArrayTime, SmartKeyConsts.TIMERANGE, "HH:mm:ss");
                result.put("value", jsonArrayTime.toJSONString());
                break;
            //日期选择
            case SmartKeyConsts.DATE:
                DateTimeFormatter ftf = DateTimeFormatter.ofPattern(model.getFormat());
                long time;
                try {
                    time = Long.parseLong(String.valueOf(value));
                    String values = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
                    result.put("value", values);
                } catch (Exception e) {
                    result.put("value", value);
                }
                break;
            //日期范围
            case SmartKeyConsts.DATERANGE:
                JSONArray jsonArray = JsonUtil.getJsonToJsonArray(String.valueOf(dataMap.get(key)));
                jsonArray = DateUtil.addCon(jsonArray, SmartKeyConsts.DATERANGE, model.getFormat());
                result.put("value", jsonArray.toJSONString());
                break;
            default:
        }
        return result;
    }

    /**
     * @param mapList
     * @return List<Map < String, Object>>
     * @Date 21:51 2020/11/11
     * @Description 将map中的所有key转化为小写
     */
    public static List<Map<String, Object>> toLowerKeyList(List<Map<String, Object>> mapList) {
        List<Map<String, Object>> newMapList = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            Map<String, Object> resultMap = new HashMap(16);
            Set<String> sets = map.keySet();
            for (String key : sets) {
                resultMap.put(key.toLowerCase(), map.get(key));
            }
            newMapList.add(resultMap);
        }
        return newMapList;
    }


    /**
     * @param map
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @Description 将map中的所有key转化为小写
     */
    public static Map<String, Object> toLowerKey(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>(16);
        Set<String> sets = map.keySet();
        for (String key : sets) {
            resultMap.put(key.toLowerCase(), map.get(key));
        }
        return resultMap;
    }


    /**
     * @param entity
     * @return
     * @Description 删除F_, 暴力转换
     */
    public static VisualdevEntity delAllfKey(VisualdevEntity entity) {

        if (entity.getTables() != null) {
            String tables = entity.getTables().trim().replaceAll(":\"f_", ":\"");
            entity.setTables(tables);
        }
        if (entity.getColumnData() != null) {
            String columnData = entity.getColumnData().trim().replaceAll(":\"f_", ":\"");
            entity.setColumnData(columnData);
        }
        if (entity.getFormData() != null) {
            String formData = entity.getFormData().trim();
            entity.setFormData(formData.replaceAll(":\"f_", ":\""));
        }
        return entity;
    }

    /**
     * @param entity
     * @return
     * @Description 删除模板字段下划线
     */
    public static VisualdevEntity delete(VisualdevEntity entity) {
        //取出列表数据中的查询列表和数据列表
        if (StringUtil.isNotEmpty(entity.getColumnData())) {
            //app的搜索不转换
            if (entity.getType() != 5) {
                Map<String, Object> columnDataMap = JsonUtil.stringToMap(entity.getColumnData());
                List<FieLdsModel> columnfield = JsonUtil.getJsonToList(columnDataMap.get("searchList"), FieLdsModel.class);
                columnDataMap.put("searchList", columnfield);
                entity.setColumnData(JsonUtil.getObjectToString(columnDataMap));
            }
        }
        Map<String, Object> formData = JsonUtil.stringToMap(entity.getFormData());
        List<FieLdsModel> modelList = JsonUtil.getJsonToList(formData.get("fields"), FieLdsModel.class);
        formData.put("fields", modelList);
        entity.setFormData(JsonUtil.getObjectToString(formData));
        return entity;
    }


    /**
     * @param entity
     * @return
     * @Description 针对模板大写转小写
     */
    public static VisualdevEntity changeType(VisualdevEntity entity) {

        List<Map<String, Object>> list = JsonUtil.getJsonToListMap(entity.getTables());
        if (list.size() > 0) {
            for (Map<String, Object> tableModel : list) {
                if (tableModel.get("fields") != null) {
                    List<TableFields> fields = JsonUtil.getJsonToList(JsonUtil.getObjectToString(tableModel.get("fields")), TableFields.class);
                    for (TableFields tableField : fields) {
                        String feildD = tableField.getField();
                        String feildL = tableField.getField().toLowerCase();
                        if (entity.getTables() != null) {
                            entity.setTables(entity.getTables().replaceAll(feildD, feildL));
                        }
                        if (entity.getColumnData() != null) {
                            entity.setColumnData(entity.getColumnData().replaceAll(feildD, feildL));
                        }
                        if (entity.getFormData() != null) {
                            entity.setFormData(entity.getFormData().replaceAll(feildD, feildL));
                        }
                    }
                }
            }
        }
        return entity;
    }

    /**
     * @param keyName
     * @param dataEntityList
     * @return
     */
    public static List<VisualdevModelDataEntity> setDataId(String keyName, List<VisualdevModelDataEntity> dataEntityList) {
        keyName = keyName.toLowerCase();
        for (VisualdevModelDataEntity entity : dataEntityList) {
            Map<String, Object> dataMap = JsonUtil.stringToMap(entity.getData());
            if (dataMap.get(keyName) != null) {
                entity.setId(String.valueOf(dataMap.get(keyName)));
            }
        }
        return dataEntityList;
    }

    /**
     * 获取列表结果查询语句
     *
     * @param keyFlag
     * @param feilds
     * @param mainTable
     * @param pKeyName
     * @param columnData
     * @return
     */
    public static String getListResultSql(Boolean keyFlag, String feilds, String mainTable, String pKeyName, ColumnDataModel columnData) {
        //dataSourceUtil初始化
        init();
        StringBuilder sql = new StringBuilder();
        if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.MYSQL.getDb())) {
            if (keyFlag) {
                sql.append("select " + feilds + " from" + " " + mainTable + " ORDER BY ");
            } else {
                sql.append("select " + pKeyName + "," + feilds + " from" + " " + mainTable + " ORDER BY ");
            }
            if (!StringUtil.isEmpty(columnData.getDefaultSidx())) {
                sql.append(columnData.getDefaultSidx() + " " + columnData.getSort());
            } else {
                sql.append(pKeyName + " " + columnData.getSort());
            }
        } else if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.SQL_SERVER.getDb())) {
            if (keyFlag) {
                sql.append("select " + feilds + " from" + " " + mainTable + " ORDER BY ");
            } else {
                sql.append("select " + pKeyName + "," + feilds + " from" + " " + mainTable + " ORDER BY ");
            }
            if (!StringUtil.isEmpty(columnData.getDefaultSidx())) {
                sql.append(columnData.getDefaultSidx() + " " + columnData.getSort());
            } else {
                sql.append(pKeyName + " " + columnData.getSort());
            }
        } else if (dataSourceUtil.getDataType().toLowerCase().equals(DbType.ORACLE.getDb())) {
            if (keyFlag) {
                sql.append("select " + feilds + " from" + " " + mainTable + " ORDER BY ");
            } else {
                sql.append("select " + pKeyName + "," + feilds + " from" + " " + mainTable + " ORDER BY ");
            }
            if (!StringUtil.isEmpty(columnData.getDefaultSidx())) {
                sql.append(columnData.getDefaultSidx() + " " + columnData.getSort());
            } else {
                sql.append(pKeyName + " " + columnData.getSort());
            }

        }
        log.info("sql:{}",sql.toString());
        return sql.toString();
    }

}
