package smart.onlinedev.util;

import smart.base.VisualdevEntity;
import smart.base.model.TableFields;
import smart.base.util.genUtil.custom.VisualUtils;
import smart.exception.DataException;
import smart.onlinedev.model.fields.FieLdsModel;
import smart.util.*;
import smart.util.context.SpringContext;
import lombok.Cleanup;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021年3月22日10:58:29
 */
public class OnlineDevDbUtil {

    private static DataSourceUtil dataSourceUtil = SpringContext.getBean(DataSourceUtil.class);


    public static void insertTable(VisualdevEntity visualdevEntity,List<FieLdsModel> fieLdsModelList ,Map<String, Object> allDataMap ,Map<String, Object> newMainDataMap) throws SQLException, DataException {
        //生成一个主表Id
        String mainId = RandomUtil.uuId();

        List<FieLdsModel> modelList = fieLdsModelList;

        List<Map<String, Object>> tableMapList = JsonUtil.getJsonToListMap(visualdevEntity.getTables());
        String mainTable = String.valueOf(tableMapList.get(0).get("table"));
        @Cleanup Connection conn = VisualUtils.getTableConn();
        //获取主键
        String pKeyName = VisualUtils.getpKey(conn, mainTable);

        //主表字段集合
        StringBuilder mainFelid = new StringBuilder();
        List<String> mainFelidList = new ArrayList<>();
        //主表查询语句
        StringBuilder mainSql = new StringBuilder();
        StringBuilder allAddSql = new StringBuilder();
        for (FieLdsModel model : modelList) {
            if ("table".equals(model.getConfig().getJnpfKey())) {
                //遍历所有数据寻找子表
                // 返回所有的entry实体
                Iterator<Map.Entry<String, Object>> iterator = newMainDataMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> next1 = iterator.next();
                    String key = next1.getKey();
                    //判断子表是否有数据
                    if (key.equals(model.getVModel()) && next1.getValue() != null) {
                        StringBuilder feilds = new StringBuilder();
                        List<FieLdsModel> childModelList = JsonUtil.getJsonToList(model.getConfig().getChildren(), FieLdsModel.class);

                        for (Map<String, Object> tableMap : tableMapList) {
                            if (tableMap.get("table").toString().equals(model.getConfig().getTableName())) {
                                for (FieLdsModel model1 : childModelList) {
                                    feilds.append(model1.getVModel() + ",");
                                }
                            }
                        }
                        if (childModelList.size() > 0) {
                            feilds.deleteCharAt(feilds.length() - 1);
                        }
                        //查询子表数据sql
                        StringBuilder childSql = new StringBuilder();

                        String childTableName = model.getConfig().getTableName();
                        //获取主键
                        String childpKeyName = VisualUtils.getpKey(conn, childTableName);
                        for (Map<String, Object> tableMap : tableMapList) {
                            if (tableMap.get("table").toString().equals(model.getConfig().getTableName())) {
                                childSql.append(VisualUtils.getInsertSql(childTableName, feilds.toString(), String.valueOf(tableMap.get("tableField")), childpKeyName, mainId));
                            }
                        }
                        String baseSql = childSql.toString().split("VALUES")[1];
                        String headerSql = childSql.toString().split("VALUES")[0] + " VALUES";

                        childSql = new StringBuilder();
                        childSql.append(headerSql);
                        //tableMap Tables()
                        for (Map<String, Object> tableMap : tableMapList) {
                            if (tableMap.get("table").toString().equals(childTableName)) {
                                //添加主表查询字段
                                List<Map<String, Object>> childList = (List<Map<String, Object>>) allDataMap.get(key);
                                //记录子表关联的主键名称
                                String relaMainKey = String.valueOf(tableMap.get("tableField"));

                                //循环子表数据
                                for (Map<String, Object> childMap : childList) {
                                    childSql.append(VisualUtils.getRealSql(baseSql, mainId));
                                    //循环子表模型
                                    for (FieLdsModel model1 : childModelList) {
                                        List<TableFields> tableFieldList = JsonUtil.getJsonToList(tableMap.get("fields"), TableFields.class);
                                        for (TableFields childTableFields : tableFieldList) {
                                            if (childMap.get(model1.getVModel()) != null && childTableFields.getField().equals(model1.getVModel()) && ("date").equals(model1.getConfig().getJnpfKey())) {
                                                String dateTime = String.valueOf(childMap.get(model1.getVModel()));
                                                DateTimeFormatter ftf = DateTimeFormatter.ofPattern(model1.getFormat());
                                                long time = Long.parseLong(dateTime);
                                                String value = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
                                                childMap.put(model1.getVModel(), value);
                                            }
                                        }

                                        //判断字段值是否为关联主键以及是否为空
                                        if (childMap.containsKey(model1.getVModel()) && relaMainKey.equals(model1.getVModel())) {
                                            childSql.append(mainId + ",");
                                        } else if (!childMap.containsKey(model1.getVModel())) {
                                            childSql.append(null + ",");
                                        } else {
                                            if ("oracle".equals(dataSourceUtil.getDataType())) {
                                                if ("date".equals(model1.getConfig().getJnpfKey())
                                                        || "createTime".equals(model1.getConfig().getJnpfKey())
                                                        || "modifyTime".equals(model1.getConfig().getJnpfKey())) {
                                                    String dateValue = childMap.get(model1.getVModel()).toString();
                                                    if (String.valueOf(dateValue).length() < 11) {
                                                        dateValue = dateValue + " 00:00:00";
                                                    }
                                                    childSql.append("to_date('" + dateValue + "','yyyy-mm-dd HH24:mi:ss'),");
                                                } else {
                                                    String childValue = String.valueOf(childMap.get(model1.getVModel()));
                                                    if (!"null".equals(childValue) && !"".equals(childValue)) {
                                                        childSql.append("'" + childValue + "',");
                                                    } else {
                                                        childSql.append(null + ",");
                                                    }
                                                }
                                            } else {
                                                String childValue = String.valueOf(childMap.get(model1.getVModel()));
                                                if (!"null".equals(childValue) && !"".equals(childValue)) {
                                                    childSql.append("'" + childValue + "',");
                                                } else {
                                                    childSql.append(null + ",");
                                                }
                                            }
                                        }
                                    }
                                    childSql.deleteCharAt(childSql.length() - 1);
                                    childSql.append("),");
                                }
                                childSql.deleteCharAt(childSql.length() - 1);
                                String childSqlx = childSql.toString();
                                childSqlx = childSqlx.replaceAll(",'\\)", ")");
                                allAddSql.append(childSqlx + ";");
                                //清除子表字段
                                allDataMap.remove(key);
                            }
                        }
                    }
                }
            } else {
                //添加主表查询字段
                if (tableMapList.size() > 0) {
                    List<TableFields> tableFieldList = JsonUtil.getJsonToList(tableMapList.get(0).get("fields"), TableFields.class);
                    for (TableFields tableFields : tableFieldList) {
                        if (allDataMap.get(model.getVModel()) != null && tableFields.getField().equals(model.getVModel()) && model.getConfig().getJnpfKey().contains("date")) {
                            String dateTime = String.valueOf(allDataMap.get(model.getVModel()));
                            if (dateTime.contains(",")) {
                                List<String> dateList = JsonUtil.getJsonToList(allDataMap.get(model.getVModel()), String.class);
                                List<String> newDateList = new ArrayList<>();
                                for (String dateStr : dateList) {
                                    DateTimeFormatter ftf = DateTimeFormatter.ofPattern(model.getFormat());
                                    long time = Long.parseLong(dateStr);
                                    String value = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
                                    newDateList.add(value);
                                }
                                allDataMap.put(model.getVModel(), JsonUtilEx.getObjectToString(newDateList));
                            } else {
                                DateTimeFormatter ftf = DateTimeFormatter.ofPattern(model.getFormat());
                                long time;
                                try {
                                    time = Long.parseLong(dateTime);
                                    String value = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
                                    allDataMap.put(model.getVModel(), value);
                                } catch (Exception e) {
                                    allDataMap.put(model.getVModel(), dateTime);
                                }
                            }
                        }
                    }
                }
                mainFelid.append(model.getVModel() + ",");
                mainFelidList.add(model.getVModel());
            }
        }
        mainFelid.deleteCharAt(mainFelid.length() - 1);
        if (VisualUtils.existKey(mainFelid.toString().toLowerCase().trim(), pKeyName.toLowerCase())) {
            mainSql.append("INSERT INTO " + mainTable + "(" + mainFelid.toString() + ")" + "VALUES(");
        } else {
            mainSql.append("INSERT INTO " + mainTable + "(" + pKeyName + "," + mainFelid.toString() + ")" + "VALUES('" + mainId + "',");
        }
        //调整字段与值的顺序
        for (FieLdsModel mainModel : modelList) {
            for (String mainStr : mainFelidList) {
                if (mainStr.equals(mainModel.getVModel())) {
                    //判断字段值是否为空
                    if (!allDataMap.containsKey(mainModel.getVModel())) {
                        mainSql.append(null + ",");
                    } else {
                        if (allDataMap.get(mainModel.getVModel()) != null) {
                            if ("oracle".equals(dataSourceUtil.getDataType())) {
                                if ("date".equals(mainModel.getConfig().getJnpfKey())
                                        || "createTime".equals(mainModel.getConfig().getJnpfKey())
                                        || "modifyTime".equals(mainModel.getConfig().getJnpfKey())) {
                                    String dateValue = allDataMap.get(mainModel.getVModel()).toString();
                                    if (String.valueOf(dateValue).length() < 11) {
                                        dateValue = dateValue + " 00:00:00";
                                    }
                                    mainSql.append("to_date('" + dateValue + "','yyyy-mm-dd HH24:mi:ss'),");
                                } else {
                                    String mainValue = String.valueOf(allDataMap.get(mainModel.getVModel()));
                                    if (!"null".equals(mainValue) && !"".equals(mainValue)) {
                                        mainSql.append("'" + allDataMap.get(mainModel.getVModel()) + "',");
                                    } else {
                                        mainSql.append(null + ",");
                                    }
                                }
                            } else {
                                String mainValue = String.valueOf(allDataMap.get(mainModel.getVModel()));
                                if (!"null".equals(mainValue) && !"".equals(mainValue)) {
                                    mainSql.append("'" + allDataMap.get(mainModel.getVModel()) + "',");
                                } else {
                                    mainSql.append(null + ",");
                                }
                            }
                        } else {
                            mainSql.append(null + ",");
                        }

                    }
                }
            }
        }
        mainSql.deleteCharAt(mainSql.length() - 1);
        mainSql.append(")");
        allAddSql.append(mainSql);
        System.out.println("新建sql" + allAddSql);
        VisualUtils.opaTableDataInfo(allAddSql.toString());
    }

    public static boolean updateTable(String id,VisualdevEntity visualdevEntity,List<FieLdsModel> fieLdsModelList ,Map<String, Object> allDataMap ,Map<String, Object> newMainDataMap) throws DataException, SQLException {
        List<Map<String, Object>> tableMapList = JsonUtil.getJsonToListMap(visualdevEntity.getTables());
        String mainTable = String.valueOf(tableMapList.get(0).get("table"));
        @Cleanup Connection conn = VisualUtils.getTableConn();
        //获取主键
        String pKeyName = VisualUtils.getpKey(conn, mainTable);
        //循环表
        String delMain = "DELETE FROM " + mainTable + " WHERE " + pKeyName + "='" + id + "'";
        StringBuilder allDelSql = new StringBuilder();
        allDelSql.append(delMain + ";");

        String queryMain = "SELECT * FROM" + " " + mainTable + " WHERE " + pKeyName + "='" + id + "'";
        List<Map<String, Object>> mainMapList = VisualUtils.getTableDataInfo(queryMain);
        //数据转大写
        mainMapList = VisualUtils.toLowerKeyList(mainMapList);
        if (mainMapList.size() > 0) {
            if (tableMapList.size() > 1) {
                //去除主表,剩余的为子表，再进行子表删除语句生成
                tableMapList.remove(0);
                for (Map<String, Object> tableMap : tableMapList) {
                    //主表字段
                    String relationField = tableMap.get("relationField").toString();
                    String relationFieldValue = mainMapList.get(0).get(relationField).toString();
                    //子表字段
                    String tableField = tableMap.get("tableField").toString();
                    String childSql = "DELETE FROM " + tableMap.get("table") + " where " + tableField + "='" + relationFieldValue + "'";
                    allDelSql.append(childSql + ";");
                }
            }
            //添加数据
            //生成一个主表Id
            String mainId = id;
            List<Map<String, Object>> tableMapList1 = JsonUtil.getJsonToListMap(visualdevEntity.getTables());
            List<FieLdsModel> modelList = fieLdsModelList;
            //主表字段集合
            StringBuilder mainFelid = new StringBuilder();
            List<String> mainFelidList = new ArrayList<>();
            //主表查询语句
            StringBuilder mainSql = new StringBuilder();
            StringBuilder allAddSql = new StringBuilder();
            for (FieLdsModel model : modelList) {
                if ("table".equals(model.getConfig().getJnpfKey())) {
                    //遍历所有数据寻找子表
                    //返回所有的entry实体
                    Iterator<Map.Entry<String, Object>> iterator = newMainDataMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, Object> next1 = iterator.next();
                        String key = next1.getKey();
                        //判断子表是否有数据
                        if (key.equals(model.getVModel()) && next1.getValue() != null) {
                            StringBuilder feilds = new StringBuilder();
                            List<FieLdsModel> childModelList = JsonUtil.getJsonToList(model.getConfig().getChildren(), FieLdsModel.class);

                            for (Map<String, Object> tableMap : tableMapList1) {
                                if (tableMap.get("table").toString().equals(model.getConfig().getTableName())) {
                                    for (FieLdsModel model1 : childModelList) {
                                        feilds.append(model1.getVModel() + ",");
                                    }
                                }
                            }
                            if (childModelList.size() > 0) {
                                feilds.deleteCharAt(feilds.length() - 1);
                            }
                            //查询子表数据sql
                            StringBuilder childSql = new StringBuilder();

                            String childTableName = model.getConfig().getTableName();
                            //获取主键
                            String childpKeyName = VisualUtils.getpKey(conn, childTableName);
                            for (Map<String, Object> tableMap : tableMapList) {
                                if (tableMap.get("table").toString().equals(model.getConfig().getTableName())) {
                                    childSql.append(VisualUtils.getInsertSql(childTableName, feilds.toString(), String.valueOf(tableMap.get("tableField")), childpKeyName, mainId));
                                }
                            }
                            String baseSql = childSql.toString().split("VALUES")[1];
                            String headerSql = childSql.toString().split("VALUES")[0] + " VALUES";

                            childSql = new StringBuilder();
                            childSql.append(headerSql);
                            //tableMap Tables()
                            for (Map<String, Object> tableMap : tableMapList1) {
                                if (tableMap.get("table").toString().equals(model.getConfig().getTableName())) {
                                    List<Map<String, Object>> childList = (List<Map<String, Object>>) allDataMap.get(key);
                                    //记录子表关联的主键名称
                                    String relaMainKey = String.valueOf(tableMap.get("tableField"));

                                    //循环子表数据
                                    for (Map<String, Object> childMap : childList) {
                                        childSql.append(VisualUtils.getRealSql(baseSql, mainId));
                                        //循环子表模型
                                        for (FieLdsModel model1 : childModelList) {
                                            List<TableFields> tableFieldList = JsonUtil.getJsonToList(tableMap.get("fields"), TableFields.class);
                                            for (TableFields childTableFields : tableFieldList) {
                                                if (childMap.get(model1.getVModel()) != null && childTableFields.getField().equals(model1.getVModel()) && ("date").equals(model1.getConfig().getJnpfKey())) {
                                                    String dateTime = String.valueOf(childMap.get(model1.getVModel()));
                                                    DateTimeFormatter ftf = DateTimeFormatter.ofPattern(model1.getFormat());
                                                    long time = Long.parseLong(dateTime);
                                                    String value = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
                                                    childMap.put(model1.getVModel(), value);
                                                }
                                            }
                                            //判断字段值是否为关联主键以及是否为空
                                            if (childMap.containsKey(model1.getVModel()) && relaMainKey.equals(model1.getVModel())) {
                                                childSql.append(mainId + ",");
                                            } else if (!childMap.containsKey(model1.getVModel())) {
                                                childSql.append(null + ",");
                                            } else {
                                                if ("oracle".equals(dataSourceUtil.getDataType())) {
                                                    if ("date".equals(model1.getConfig().getJnpfKey())
                                                            || "createTime".equals(model1.getConfig().getJnpfKey())
                                                            || "modifyTime".equals(model1.getConfig().getJnpfKey())) {
                                                        String dateValue = childMap.get(model1.getVModel()).toString();
                                                        if (String.valueOf(dateValue).length() < 11) {
                                                            dateValue = dateValue + " 00:00:00";
                                                        }
                                                        childSql.append("to_date('" + dateValue + "','yyyy-mm-dd HH24:mi:ss'),");
                                                    } else {
                                                        String childValue = String.valueOf(childMap.get(model1.getVModel()));
                                                        if (!"null".equals(childValue) && !"".equals(childValue)) {
                                                            childSql.append("'" + childValue + "',");
                                                        } else {
                                                            childSql.append(null + ",");
                                                        }
                                                    }
                                                } else {
                                                    String childValue = String.valueOf(childMap.get(model1.getVModel()));
                                                    if (!"null".equals(childValue) && !"".equals(childValue)) {
                                                        childSql.append("'" + childValue + "',");
                                                    } else {
                                                        childSql.append(null + ",");
                                                    }
                                                }
                                            }
                                        }
                                        childSql.deleteCharAt(childSql.length() - 1);
                                        childSql.append("),");
                                    }
                                    childSql.deleteCharAt(childSql.length() - 1);
                                    String childSqlx = childSql.toString();
                                    childSqlx = childSqlx.replaceAll(",'\\)", ")");
                                    allAddSql.append(childSqlx + ";");
                                    //清除子表字段
                                    allDataMap.remove(key);
                                }
                            }
                        }
                    }
                } else {
                    //添加主表查询字段
                    if (tableMapList1.size() > 0) {
                        List<TableFields> tableFieldList = JsonUtil.getJsonToList(tableMapList1.get(0).get("fields"), TableFields.class);
                        //去除无意义控件
                        modelList = VisualUtils.deleteVmodel(modelList);
                        for (TableFields tableFields : tableFieldList) {
                            if (allDataMap.get(model.getVModel()) != null && tableFields.getField().equals(model.getVModel()) && model.getConfig().getJnpfKey().contains("date")) {
                                String dateTime = String.valueOf(allDataMap.get(model.getVModel()));
                                if (dateTime.contains(",")) {
                                    List<String> dateList = JsonUtil.getJsonToList(allDataMap.get(model.getVModel()), String.class);
                                    List<String> newDateList = new ArrayList<>();
                                    for (String dateStr : dateList) {
                                        DateTimeFormatter ftf = DateTimeFormatter.ofPattern(model.getFormat());
                                        long time = Long.parseLong(dateStr);
                                        String value = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of("+8")));
                                        newDateList.add(value);
                                    }
                                    allDataMap.put(model.getVModel(), JsonUtilEx.getObjectToString(newDateList));
                                } else {
                                    DateTimeFormatter ftf = DateTimeFormatter.ofPattern(model.getFormat());
                                    long time;
                                    try {
                                        time = Long.parseLong(dateTime);
                                        String value = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of("+8")));
                                        allDataMap.put(model.getVModel(), value);
                                    } catch (Exception e) {
                                        allDataMap.put(model.getVModel(), dateTime);
                                    }

                                }
                            }
                        }
                    }
                    mainFelid.append(model.getVModel() + ",");
                    mainFelidList.add(model.getVModel());
                }
            }
            mainFelid.deleteCharAt(mainFelid.length() - 1);
            if (VisualUtils.existKey(mainFelid.toString().toLowerCase().trim(), pKeyName.toLowerCase())) {
                mainSql.append("INSERT INTO " + mainTable + "(" + mainFelid.toString() + ") " + " VALUES (");
            } else {
                mainSql.append("INSERT INTO " + mainTable + "(" + pKeyName + "," + mainFelid.toString() + ") " + " VALUES ('" + mainId + "',");
            }
            //调整字段与值的顺序
            for (FieLdsModel mainModel : modelList) {
                for (String mainStr : mainFelidList) {
                    if (mainStr.equals(mainModel.getVModel())) {
                        //判断字段值是否为空
                        if (!allDataMap.containsKey(mainModel.getVModel())) {
                            mainSql.append(null + ",");
                        } else {
                            if (allDataMap.get(mainModel.getVModel()) != null) {
                                if ("oracle".equals(dataSourceUtil.getDataType())) {
                                    if ("date".equals(mainModel.getConfig().getJnpfKey())
                                            || "createTime".equals(mainModel.getConfig().getJnpfKey())
                                            || "modifyTime".equals(mainModel.getConfig().getJnpfKey())) {
                                        String dateValue = allDataMap.get(mainModel.getVModel()).toString();
                                        if (String.valueOf(dateValue).length() < 11) {
                                            dateValue = dateValue + " 00:00:00";
                                        }
                                        mainSql.append("to_date('" + dateValue + "','yyyy-mm-dd HH24:mi:ss'),");
                                    } else {
                                        String mainValue = String.valueOf(allDataMap.get(mainModel.getVModel()));
                                        if (!"null".equals(mainValue) && !"".equals(mainValue)) {
                                            mainSql.append("'" + allDataMap.get(mainModel.getVModel()) + "',");
                                        } else {
                                            mainSql.append(null + ",");
                                        }
                                    }
                                } else {
                                    String mainValue = String.valueOf(allDataMap.get(mainModel.getVModel()));
                                    if (!"null".equals(mainValue) && !"".equals(mainValue)) {
                                        mainSql.append("'" + allDataMap.get(mainModel.getVModel()) + "',");
                                    } else {
                                        mainSql.append(null + ",");
                                    }
                                }
                            } else {
                                mainSql.append(null + ",");
                            }

                        }
                    }
                }
            }
            mainSql.deleteCharAt(mainSql.length() - 1);
            mainSql.append(")");
            allAddSql.append(mainSql);
            System.out.println("修改sql" + allDelSql.toString() + allAddSql.toString());
            VisualUtils.opaTableDataInfo(allDelSql.toString() + allAddSql.toString());
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除有表单条数据
     * @param id
     * @param visualdevEntity
     * @return
     * @throws SQLException
     * @throws DataException
     */
    public static boolean deleteTable(String id,VisualdevEntity visualdevEntity) throws SQLException, DataException {
        List<Map<String, Object>> tableMapList = JsonUtil.getJsonToListMap(visualdevEntity.getTables());

        String mainTable = String.valueOf(tableMapList.get(0).get("table"));
        @Cleanup Connection conn = VisualUtils.getTableConn();
        //获取主键
        String pKeyName = VisualUtils.getpKey(conn, mainTable);
        //循环表
        String delMain = "DELETE FROM " + tableMapList.get(0).get("table") + " WHERE " + pKeyName + "='" + id + "'";
        StringBuilder allDelSql = new StringBuilder();
        allDelSql.append(delMain + ";");

        String queryMain = "SELECT * FROM" + " " + tableMapList.get(0).get("table") + " WHERE " + pKeyName + "='" + id + "'";
        List<Map<String, Object>> mainMapList = VisualUtils.getTableDataInfo(queryMain);
        mainMapList = VisualUtils.toLowerKeyList(mainMapList);
        if (mainMapList.size() > 0) {
            if (tableMapList.size() > 1) {
                //去除主表
                tableMapList.remove(0);
                for (Map<String, Object> tableMap : tableMapList) {
                    //主表字段
                    String relationField = tableMap.get("relationField").toString();
                    String relationFieldValue = mainMapList.get(0).get(relationField).toString();
                    //子表字段
                    String tableField = tableMap.get("tableField").toString();
                    String childSql = "DELETE FROM " + tableMap.get("table") + " WHERE " + tableField + "='" + relationFieldValue + "'";
                    allDelSql.append(childSql + ";");
                }
            }
            VisualUtils.opaTableDataInfo(allDelSql.toString());
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除有表多条数据
     * @param ids
     * @param visualdevEntity
     * @return
     * @throws SQLException
     * @throws DataException
     */
    public static boolean deleteTables(String ids,VisualdevEntity visualdevEntity) throws SQLException, DataException {
        List<Map<String, Object>> tableMapList = JsonUtil.getJsonToListMap(visualdevEntity.getTables());

        String[] idList=ids.split(",");
        StringBuilder fieldsSql=new StringBuilder();
        for(String id:idList){
            fieldsSql.append("'"+id+"'");
        }

        String mainTable = String.valueOf(tableMapList.get(0).get("table"));
        @Cleanup Connection conn = VisualUtils.getTableConn();
        //获取主键
        String pKeyName = VisualUtils.getpKey(conn, mainTable);
        //循环表
        String delMain = "DELETE FROM " + tableMapList.get(0).get("table") + " WHERE " + pKeyName + "in (" + fieldsSql + ")";
        StringBuilder allDelSql = new StringBuilder();
        allDelSql.append(delMain + ";");

        for(String id:idList) {
            String queryMain = "SELECT * FROM" + " " + tableMapList.get(0).get("table") + " WHERE " + pKeyName + "='" + id + "'";
            List<Map<String, Object>> mainMapList = VisualUtils.getTableDataInfo(queryMain);
            mainMapList = VisualUtils.toLowerKeyList(mainMapList);
            if (mainMapList.size() > 0) {
                if (tableMapList.size() > 1) {
                    //去除主表
                    tableMapList.remove(0);
                    for (Map<String, Object> tableMap : tableMapList) {
                        //主表字段
                        String relationField = tableMap.get("relationField").toString();
                        String relationFieldValue = mainMapList.get(0).get(relationField).toString();
                        //子表字段
                        String tableField = tableMap.get("tableField").toString();
                        String childSql = "DELETE FROM " + tableMap.get("table") + " WHERE " + tableField + "='" + relationFieldValue + "'";
                        allDelSql.append(childSql + ";");
                    }
                }
            }
        }
        VisualUtils.opaTableDataInfo(allDelSql.toString());
        return true;
    }
}
