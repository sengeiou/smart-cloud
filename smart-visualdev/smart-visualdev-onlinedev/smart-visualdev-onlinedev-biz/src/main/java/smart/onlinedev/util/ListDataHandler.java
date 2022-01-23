package smart.onlinedev.util;

import com.alibaba.fastjson.JSON;
import smart.base.VisualdevEntity;
import smart.base.model.FormDataModel;
import smart.base.util.genUtil.custom.DynamicUtil;
import smart.base.util.genUtil.custom.VisualUtils;
import smart.exception.DataException;
import smart.onlinedev.entity.VisualdevModelDataEntity;
import smart.onlinedev.model.TimeControl;
import smart.onlinedev.model.fields.FieLdsModel;
import smart.onlinedev.model.fields.props.PropsBeanModel;
import smart.util.CacheKeyUtil;
import smart.util.JsonUtil;
import smart.util.RedisUtil;
import smart.util.StringUtil;
import smart.util.context.SpringContext;
import smart.util.visiual.DataTypeConst;
import smart.util.visiual.SmartKeyConsts;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-15
 */
public class ListDataHandler {

    /**
     * 在线开发数据处理
     * @param keyJsonMap
     * @param visualdevEntity
     * @param list
     * @param realList
     * @return
     * @throws SQLException
     * @throws DataException
     * @throws ParseException
     * @throws IOException
     */
    public static List<Map<String, Object>> swapListData(Map<String, Object> keyJsonMap, VisualdevEntity visualdevEntity, List<VisualdevModelDataEntity> list, List<Map<String, Object>> realList) throws SQLException, DataException, ParseException, IOException {
        FormDataModel formDataModel = JsonUtil.getJsonToBean(visualdevEntity.getFormData(), FormDataModel.class);
        List<FieLdsModel> formData = JsonUtil.getJsonToList(JsonUtil.getJsonToJsonArray(formDataModel.getFields()), FieLdsModel.class);

        //去除模板多级控件
        formData = VisualUtils.deleteMore(formData);
        //将关键字查询传输的id转换成名称
        Map<String, Object> keyAndList = KeyDataUtil.getKeyData(formData, keyJsonMap, list, visualdevEntity.getId());
        //保存需要转换的时间字段
        TimeControl timeControl = (TimeControl) keyAndList.get(DataTypeConst.TIME_CONTROL);

        //替换静态数据模板选项值
        keyAndList = getRealData(formData, (Map<String, Object>) keyAndList.get(DataTypeConst.KEY_JSON_MAP), JsonUtil.getJsonToList(keyAndList.get(DataTypeConst.LIST), VisualdevModelDataEntity.class));
        //系统自动生成字段转换
        list = VisualUtils.stringToList(formData, JsonUtil.getJsonToList(keyAndList.get(DataTypeConst.LIST), VisualdevModelDataEntity.class));
        //字符串转数组
        list = AutoFeildsUtil.autoFeildsList(formData, list);
        keyJsonMap = JsonUtil.entityToMap(keyAndList.get(DataTypeConst.KEY_JSON_MAP));
        //关键字过滤
        realList = VisualUtils.getRealList(keyJsonMap, list, timeControl);
        return realList;
    }


    /**
     * 替换列表的选项值
     * @param formData
     * @param keyJsonMap
     * @param list
     * @return
     * @throws IOException
     */
    public static Map<String, Object> getRealData(List<FieLdsModel> formData, Map<String, Object> keyJsonMap, List<VisualdevModelDataEntity> list) throws IOException {
        RedisUtil redisUtil = SpringContext.getBean(RedisUtil.class);
        CacheKeyUtil cacheKeyUtil=SpringContext.getBean(CacheKeyUtil.class);

        for (FieLdsModel formModel : formData) {
            if (cacheKeyUtil.getDynamic().equals(formModel.getConfig().getDataType())) {
                redisUtil.remove(cacheKeyUtil.getDynamic() + formModel.getConfig().getPropsUrl());
            }
        }
        //存储远端数据的字段数据
        Map<String, FieLdsModel> dynamicDataMap = new HashMap<>(16);
        //添加远端数据
        for (FieLdsModel fieLdsModel : formData) {
            String type = fieLdsModel.getConfig().getDataType();
            String dynamicId = fieLdsModel.getConfig().getPropsUrl();
            if (DataTypeConst.DYNAMIC.equals(type) && StringUtil.isNotEmpty(dynamicId)) {
                dynamicDataMap.put(dynamicId, fieLdsModel);
            }
        }
        for (Map.Entry<String, FieLdsModel> entry : dynamicDataMap.entrySet()) {
            DynamicUtil dynamicUtil = new DynamicUtil();
            entry.setValue(dynamicUtil.dynamicData(entry.getValue()));
        }

        for (FieLdsModel fieLdsModel : formData) {
            for (VisualdevModelDataEntity entity : list) {
                //真实数据
                Map<String, Object> dataMap = JsonUtil.stringToMap(entity.getData());
                for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                    String type = fieLdsModel.getConfig().getDataType();
                    if (DataTypeConst.DICTIONARY.equals(type) && entry.getValue() != null) {
                        if (entry.getKey().equals(fieLdsModel.getVModel())) {
                            //字段数据id
                            List<String> fieldList = VisualUtils.analysisField(String.valueOf(entry.getValue()));
                            String fieldStr = String.valueOf(entry.getValue());

                            if (SmartKeyConsts.CASCADER.equals(fieLdsModel.getConfig().getJnpfKey())) {
                                //为级联选择框的列表和查询字段赋值
                                Map<String, Object> cascaderMap = VisualUtils.cascaderOperation(fieLdsModel, fieldList, keyJsonMap);
                                if (cascaderMap.get(DataTypeConst.KEY_JSON_MAP) != null) {
                                    keyJsonMap = JsonUtil.entityToMap(cascaderMap.get(DataTypeConst.KEY_JSON_MAP));
                                }
                                if (cascaderMap.get(DataTypeConst.VALUE) != null) {
                                    entry.setValue(cascaderMap.get(DataTypeConst.VALUE));
                                }
                            }
                            if (SmartKeyConsts.TREESELECT.equals(fieLdsModel.getConfig().getJnpfKey())) {
                                //给字段数据字段赋值
                                String value = VisualUtils.treeSelectOperation(fieLdsModel, fieldStr);
                                if (StringUtil.isNotEmpty(value)) {
                                    entry.setValue(value);
                                    if(keyJsonMap!=null&&keyJsonMap.containsValue(fieldStr)){
                                        keyJsonMap.put(fieLdsModel.getVModel(),value);
                                    }
                                }
                            }
                            //模板选项集合
                            else if (fieLdsModel.getSlot() != null && StringUtil.isNotEmpty(fieLdsModel.getSlot().getOptions())) {
                                List<Map<String, Object>> options = JsonUtil.getJsonToListMap(fieLdsModel.getSlot().getOptions());
                                PropsBeanModel props = JsonUtil.getJsonToBean(fieLdsModel.getConfig().getProps(), PropsBeanModel.class);
                                //转换
                                String value = VisualUtils.setDicValue(fieldList, fieldStr, props, options, fieLdsModel);
                                if (value != null) {
                                    entry.setValue(value);
                                    if(keyJsonMap!=null&&keyJsonMap.containsValue(fieldStr)){
                                        keyJsonMap.put(fieLdsModel.getVModel(),value);
                                    }
                                }
                            }
                        }
                    } else if (DataTypeConst.STATIC.equals(type) && entry.getValue() != null) {
                        if (entry.getKey().equals(fieLdsModel.getVModel())) {
                            //字段数据id
                            List<String> fieldList = VisualUtils.analysisField(String.valueOf(entry.getValue()));
                            String fieldStr = String.valueOf(entry.getValue());

                            if (SmartKeyConsts.CASCADER.equals(fieLdsModel.getConfig().getJnpfKey())) {
                                //为级联选择框的列表和查询字段赋值
                                Map<String, Object> cascaderMap = VisualUtils.cascaderOperation(fieLdsModel, fieldList, keyJsonMap);
                                if (cascaderMap.get(DataTypeConst.KEY_JSON_MAP) != null) {
                                    keyJsonMap = JsonUtil.entityToMap(cascaderMap.get(DataTypeConst.KEY_JSON_MAP));
                                }
                                if (cascaderMap.get(DataTypeConst.VALUE) != null) {
                                    entry.setValue(cascaderMap.get(DataTypeConst.VALUE));
                                }
                            } else if (SmartKeyConsts.TREESELECT.equals(fieLdsModel.getConfig().getJnpfKey())) {
                                String value = VisualUtils.treeSelectOperation(fieLdsModel, fieldStr);
                                if (StringUtil.isNotEmpty(value)) {
                                    entry.setValue(value);
                                    if(keyJsonMap!=null&&keyJsonMap.containsValue(fieldStr)){
                                        keyJsonMap.put(fieLdsModel.getVModel(),value);
                                    }
                                }
                            }

                            //正常多选列表赋值
                            Object selectObj = VisualUtils.setSelect(fieldList, fieldStr, fieLdsModel);
                            if (selectObj != null) {
                                entry.setValue(selectObj);
                                if(keyJsonMap!=null&&keyJsonMap.containsValue(fieldStr)){
                                    keyJsonMap.put(fieLdsModel.getVModel(),selectObj);
                                }
                            }
                        }
                    } else if (DataTypeConst.DYNAMIC.equals(type) && entry.getValue() != null) {
                        String dynamicId = fieLdsModel.getConfig().getPropsUrl();
                        if (dynamicDataMap.containsKey(dynamicId)) {
                            fieLdsModel = dynamicDataMap.get(dynamicId);
                            if (entry.getKey().equals(fieLdsModel.getVModel())) {
                                //字段数据id
                                List<String> fieldList = VisualUtils.analysisField(String.valueOf(entry.getValue()));
                                String fieldStr = String.valueOf(entry.getValue());

                                //为级联选择框的列表和查询字段赋值
                                Map<String, Object> cascaderMap = VisualUtils.cascaderOperation(fieLdsModel, fieldList, keyJsonMap);
                                if (cascaderMap.get(DataTypeConst.KEY_JSON_MAP) != null) {
                                    keyJsonMap = JsonUtil.entityToMap(cascaderMap.get(DataTypeConst.KEY_JSON_MAP));
                                }
                                if (cascaderMap.get(DataTypeConst.VALUE) != null) {
                                    entry.setValue(cascaderMap.get(DataTypeConst.VALUE));
                                }
                                if (SmartKeyConsts.TREESELECT.equals(fieLdsModel.getConfig().getJnpfKey())) {
                                    //给字段数据字段赋值
                                    String value = VisualUtils.treeSelectOperation(fieLdsModel, fieldStr);
                                    if (StringUtil.isNotEmpty(value)) {
                                        entry.setValue(value);
                                        if(keyJsonMap!=null&&keyJsonMap.containsValue(fieldStr)){
                                            keyJsonMap.put(fieLdsModel.getVModel(),value);
                                        }
                                    }
                                }
                                //正常多选列表赋值
                                Object selectObj = VisualUtils.setSelect(fieldList, fieldStr, fieLdsModel);
                                if (selectObj != null) {
                                    entry.setValue(selectObj);
                                    if(keyJsonMap!=null&&keyJsonMap.containsValue(fieldStr)){
                                        keyJsonMap.put(fieLdsModel.getVModel(),selectObj);
                                    }
                                }
                            }
                        }
                    }
                }
                entity.setData(JSON.toJSON(dataMap).toString());
            }
        }
        Map<String, Object> map = new HashMap<>(16);
        map.put(DataTypeConst.LIST, list);
        map.put(DataTypeConst.KEY_JSON_MAP, keyJsonMap);
        return map;
    }


}
