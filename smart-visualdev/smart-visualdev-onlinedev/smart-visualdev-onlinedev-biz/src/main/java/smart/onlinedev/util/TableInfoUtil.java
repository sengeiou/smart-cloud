package smart.onlinedev.util;

import com.alibaba.fastjson.JSON;
import smart.base.util.genUtil.custom.VisualUtils;
import smart.exception.DataException;
import smart.onlinedev.entity.VisualdevModelDataEntity;
import smart.onlinedev.model.VisualdevModelDataInfoVO;
import smart.onlinedev.model.fields.FieLdsModel;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import smart.util.visiual.DataTypeConst;
import smart.util.visiual.SmartKeyConsts;
import lombok.Cleanup;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 处理获取单条数据
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-04-07
 */
public class TableInfoUtil {

    public static VisualdevModelDataInfoVO getTableInfoDataChange(String id, List<FieLdsModel> modelList, List<Map<String, Object>> tableMapList, String mainTable) throws DataException, SQLException, ParseException, IOException {
        @Cleanup Connection conn = VisualUtils.getTableConn();
        //获取主键
        String pKeyName = VisualUtils.getpKey(conn, mainTable);

        StringBuilder mainfeild = new StringBuilder();
        String main = "select * from" + " " + tableMapList.get(0).get("table") + " where " + pKeyName + "='" + id + "'";
        List<Map<String, Object>> mainMap = VisualUtils.getTableDataInfo(main);
        //记录全部主表数据
        if (mainMap != null && mainMap.size() > 0) {
            Map<String, Object> dataMap = mainMap.get(0);
            //转换全大写
            dataMap = VisualUtils.toLowerKey(dataMap);
            //记录列表展示的主表数据
            Map<String, Object> newDataMap = new HashMap<>(16);
            Map<String, Object> newMainDataMap = new HashMap<>(16);
            for (FieLdsModel model : modelList) {
                if ("table".equals(model.getConfig().getJnpfKey())) {
                    StringBuilder feilds = new StringBuilder();
                    List<FieLdsModel> childModelList = JsonUtil.getJsonToList(model.getConfig().getChildren(), FieLdsModel.class);
                    for (FieLdsModel childModel : childModelList) {
                        feilds.append(childModel.getVModel() + ",");
                    }
                    if (childModelList.size() > 0) {
                        feilds.deleteCharAt(feilds.length() - 1);
                    }
                    //子表字段
                    String relationFeild;
                    //主表字段
                    String relationMainFeild;
                    String relationMainFeildValue;
                    //查询子表数据
                    StringBuilder childSql = new StringBuilder();
                    childSql.append("select " + feilds + " from" + " " + model.getConfig().getTableName() + " where 1=1");
                    for (Map<String, Object> tableMap : tableMapList) {
                        if (tableMap.get("table").toString().equals(model.getConfig().getTableName())) {
                            relationFeild = tableMap.get("tableField").toString();
                            relationMainFeild = tableMap.get("relationField").toString();
                            if (dataMap.containsKey(relationMainFeild)) {
                                relationMainFeildValue = dataMap.get(relationMainFeild).toString();
                                childSql.append(" And " + relationFeild + "='" + relationMainFeildValue + "'");
                            }
                            //子表字段全转换成小写
                            List<Map<String, Object>> childList = VisualUtils.getTableDataInfo(childSql.toString());
                            childList = VisualUtils.toLowerKeyList(childList);
                            newDataMap.put(model.getVModel(), childList);
                        }
                    }
                } else {
                    mainfeild.append(model.getVModel() + ",");
                }
            }
            if (modelList.size() > 0) {
                mainfeild.deleteCharAt(mainfeild.length() - 1);
            }
            for (FieLdsModel fieLdsModel : modelList) {
                for (Map.Entry<String, Object> entryMap : dataMap.entrySet()) {
                    if (entryMap.getKey().equals(fieLdsModel.getVModel())) {
                        if (mainfeild.toString().contains(entryMap.getKey())) {
                            if (SmartKeyConsts.UPLOADFZ.equals(fieLdsModel.getConfig().getJnpfKey()) || SmartKeyConsts.UPLOADIMG.equals(fieLdsModel.getConfig().getJnpfKey())) {
                                List<Map<String, Object>> map = JsonUtil.getJsonToListMap(String.valueOf(entryMap.getValue()));
                                newMainDataMap.put(entryMap.getKey(), map);
                            } else if (SmartKeyConsts.CHECKBOX.equals(fieLdsModel.getConfig().getJnpfKey())) {
                                List<String> list = JsonUtil.getJsonToList(String.valueOf(entryMap.getValue()), String.class);
                                newMainDataMap.put(entryMap.getKey(), list);
                            } else if (SmartKeyConsts.CREATETIME.equals(fieLdsModel.getConfig().getJnpfKey()) || SmartKeyConsts.MODIFYTIME.equals(fieLdsModel.getConfig().getJnpfKey())) {
                                newMainDataMap.put(entryMap.getKey(), String.valueOf(entryMap.getValue()));
                            } else if (fieLdsModel.getConfig().getJnpfKey().contains("date") && entryMap.getValue() != null) {
                                if (entryMap.getValue().toString().contains(",")) {
                                    List<String> list = JsonUtil.getJsonToList(String.valueOf(entryMap.getValue()), String.class);
                                    List<String> newList = new ArrayList<>();
                                    SimpleDateFormat sdf = new SimpleDateFormat(fieLdsModel.getFormat());
                                    for (String dateStr : list) {
                                        Long s = sdf.parse(dateStr).getTime();
                                        newList.add(s.toString());
                                    }
                                    newMainDataMap.put(entryMap.getKey(), newList);
                                } else {
                                    SimpleDateFormat sdf = new SimpleDateFormat(fieLdsModel.getFormat());
                                    Long s = sdf.parse(entryMap.getValue().toString()).getTime();
                                    newMainDataMap.put(entryMap.getKey(), s);
                                }
                            } else {
                                newMainDataMap.put(entryMap.getKey(), entryMap.getValue());
                            }
                        }
                    }
                }
            }
            //添加主表数据
            newDataMap.put("main", JSON.toJSONString(newMainDataMap));
            int t = 0;
            //转换主表和子表数据
            for (FieLdsModel fieLdsModel : modelList) {
                for (Map.Entry<String, Object> entryMap : newDataMap.entrySet()) {
                    //转换为真实数据
                    if (entryMap.getKey().equals(fieLdsModel.getVModel()) || "main".equals(entryMap.getKey())) {
                        List<VisualdevModelDataEntity> list = new ArrayList<>();
                        //将查询的关键字json转成map
                        Map<String, Object> keyJsonMap = new HashMap<>(16);
                        List<FieLdsModel> formDatas;
                        if ("main".equals(entryMap.getKey()) && t == 0) {
                            formDatas = modelList;
                            VisualdevModelDataEntity entity = new VisualdevModelDataEntity();
                            entity.setData(entryMap.getValue().toString());
                            list.add(entity);
                            t++;
                            //将关键字查询传输的id转换成名称
                            Map<String, Object> keyAndList = KeyDataUtil.getKeyData(formDatas, keyJsonMap, list, id);

                            //替换静态数据模板选项值
                            keyAndList = ListDataHandler.getRealData(formDatas, keyJsonMap, JsonUtil.getJsonToList(keyAndList.get(DataTypeConst.LIST), VisualdevModelDataEntity.class));
                            //系统自动生成字段转换
                            list = VisualUtils.stringToList(formDatas, JsonUtil.getJsonToList(keyAndList.get(DataTypeConst.LIST), VisualdevModelDataEntity.class));
                            //字符串转数组
                            list = AutoFeildsUtil.autoFeildsList(formDatas, list);
                            entryMap.setValue(list);
                        } else if (entryMap.getKey().equals(fieLdsModel.getVModel())) {
                            formDatas = JsonUtil.getJsonToList(fieLdsModel.getConfig().getChildren(), FieLdsModel.class);
                            List<Map<String, Object>> childMapList = (List<Map<String, Object>>) entryMap.getValue();
                            for (Map<String, Object> objectMap : childMapList) {
                                VisualdevModelDataEntity entity = new VisualdevModelDataEntity();
                                entity.setData(JsonUtilEx.getObjectToString(objectMap));
                                list.add(entity);
                            }
                            //将关键字查询传输的id转换成名称
                            Map<String, Object> keyAndList = KeyDataUtil.getKeyData(formDatas, keyJsonMap, list, id);

                            //替换静态数据模板选项值
                            keyAndList = ListDataHandler.getRealData(formDatas, keyJsonMap, JsonUtil.getJsonToList(keyAndList.get(DataTypeConst.LIST), VisualdevModelDataEntity.class));
                            //系统自动生成字段转换
                            list = VisualUtils.stringToList(formDatas, JsonUtil.getJsonToList(keyAndList.get(DataTypeConst.LIST), VisualdevModelDataEntity.class));
                            //字符串转数组
                            list = AutoFeildsUtil.autoFeildsList(formDatas, list);
                            entryMap.setValue(list);
                        }
                    }
                }
            }

            List<VisualdevModelDataEntity> mainList = (List<VisualdevModelDataEntity>) newDataMap.get("main");

            Map<String, Object> realDataMap = JsonUtil.stringToMap(mainList.get(0).getData());
            newDataMap.remove("main");

            for (Map.Entry<String, Object> entryMap : newDataMap.entrySet()) {
                List<VisualdevModelDataEntity> childList = (List<VisualdevModelDataEntity>) entryMap.getValue();
                List<String> array = new ArrayList<>();
                for (VisualdevModelDataEntity childEntity : childList) {
                    array.add(childEntity.getData());
                }
                entryMap.setValue(array);
            }
            realDataMap.putAll(newDataMap);
            VisualdevModelDataInfoVO vo = new VisualdevModelDataInfoVO();
            String tmp1 = StringEscapeUtils.unescapeJavaScript(JsonUtilEx.getObjectToString(realDataMap)).trim();
            tmp1 = tmp1.replace("\"{", "{");
            tmp1 = tmp1.replace("}\"", "}");
            tmp1 = tmp1.replace("\"[", "[");
            tmp1 = tmp1.replace("]\"", "]");
            vo.setData(tmp1);
            vo.setId(dataMap.get(pKeyName.toLowerCase()).toString());
            return vo;
        }
        return null;
    }
    public static VisualdevModelDataInfoVO getTableInfo(String pKeyName,StringBuilder mainfeild,List<FieLdsModel> modelList,List<Map<String, Object>> mainMap, List<Map<String, Object>> tableMapList) throws DataException, SQLException, ParseException, IOException {
        if (mainMap != null & mainMap.size() > 0) {
            //记录全部主表数据
            Map<String, Object> dataMap = mainMap.get(0);
            //转换全小写
            dataMap = VisualUtils.toLowerKey(dataMap);
            //记录列表展示的主表数据
            Map<String, Object> newDataMap = new HashMap<>(16);
            for (FieLdsModel model : modelList) {
                if (SmartKeyConsts.CHILD_TABLE.equals(model.getConfig().getJnpfKey())) {
                    StringBuilder feilds = new StringBuilder();
                    List<FieLdsModel> childModelList = JsonUtil.getJsonToList(model.getConfig().getChildren(), FieLdsModel.class);
                    for (FieLdsModel childModel : childModelList) {
                        feilds.append(childModel.getVModel() + ",");
                    }
                    if (childModelList.size() > 0) {
                        feilds.deleteCharAt(feilds.length() - 1);
                    }
                    //子表字段
                    String relationFeild = "";
                    //主表字段
                    String relationMainFeild = "";
                    String relationMainFeildValue = "";
                    //查询子表数据
                    StringBuilder childSql = new StringBuilder();
                    childSql.append("select " + feilds + " from" + " " + model.getConfig().getTableName() + " where 1=1");
                    for (Map<String, Object> tableMap : tableMapList) {
                        if (tableMap.get("table").toString().equals(model.getConfig().getTableName())) {
                            relationFeild = tableMap.get("tableField").toString();
                            relationMainFeild = tableMap.get("relationField").toString();
                            if (dataMap.containsKey(relationMainFeild)) {
                                relationMainFeildValue = dataMap.get(relationMainFeild).toString();
                                childSql.append(" And " + relationFeild + "='" + relationMainFeildValue + "'");
                            }
                            //子表字段全转换成小写
                            List<Map<String, Object>> childList = VisualUtils.getTableDataInfo(childSql.toString());
                            childList = VisualUtils.toLowerKeyList(childList);
                            newDataMap.put(model.getVModel(), childList);
                        }
                    }
                } else {
                    mainfeild.append(model.getVModel() + ",");
                }
            }
            if (modelList.size() > 0) {
                mainfeild.deleteCharAt(mainfeild.length() - 1);
            }
            //主表数据和子表分开转换
            //1.转换子表
            for (FieLdsModel fieLdsModel : modelList) {
                for (Map.Entry<String, Object> entryMap : newDataMap.entrySet()) {
                    if (SmartKeyConsts.CHILD_TABLE.equals(fieLdsModel.getConfig().getJnpfKey()) && entryMap.getKey().equals(fieLdsModel.getVModel())) {
                        List<FieLdsModel> childFormList = JsonUtil.getJsonToList(fieLdsModel.getConfig().getChildren(), FieLdsModel.class);
                        List<Map<String, Object>> childDataMap = (List<Map<String, Object>>) entryMap.getValue();
                        newDataMap.put(fieLdsModel.getVModel(), VisualUtils.swapTableDataInfoList(childFormList, childDataMap));
                    }
                }
            }
            //2.去除模板里的子表字段,转换主表数据
            List<FieLdsModel> mainFormList = modelList.stream().filter(t -> !SmartKeyConsts.CHILD_TABLE.equals(t.getConfig().getJnpfKey())).collect(Collectors.toList());
            newDataMap.putAll(VisualUtils.swapTableDataInfoOne(mainFormList, dataMap));

            String data = AutoFeildsUtil.autoFeilds(modelList, JsonUtilEx.getObjectToString(newDataMap));
            VisualdevModelDataInfoVO vo = new VisualdevModelDataInfoVO();
            vo.setData(data);
            vo.setId(dataMap.get(pKeyName.toLowerCase()).toString());
            return vo;
        }
        return null;
    }

}
