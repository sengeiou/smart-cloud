package smart.base.util;



import smart.base.model.Template6.ColumnListField;
import smart.util.JsonUtil;
import smart.util.StringUtil;
import smart.base.VisualdevEntity;
import smart.base.model.TableFields;
import smart.base.model.TableModel;
import smart.base.model.Template6.IndexGridField6Model;
import smart.onlinedev.model.fields.FieLdsModel;
import smart.onlinedev.model.fields.config.ConfigModel;

import java.util.List;
import java.util.Map;

public class VisualUtil {
    /**
     * @param entity
     * @return
     * @Description 删除F_, 且全转小写
     */
    public static VisualdevEntity delfKey(VisualdevEntity entity) {

        List<TableModel> list = JsonUtil.getJsonToList(entity.getTables(), TableModel.class);

        for (TableModel tableModel : list) {
            List<TableFields> fields = tableModel.getFields();
            if (StringUtil.isNotEmpty(tableModel.getTableField()) && "f_".equalsIgnoreCase(tableModel.getTableField().substring(0, 2))) {
                tableModel.setTableField(tableModel.getTableField().substring(2).toLowerCase());
            }
            if (StringUtil.isNotEmpty(tableModel.getRelationField()) && "f_".equalsIgnoreCase(tableModel.getRelationField().substring(0, 2))) {
                tableModel.setRelationField(tableModel.getRelationField().substring(2).toLowerCase());
            }
            for (TableFields tableFields : fields) {
                String feild = tableFields.getField().toLowerCase();
                if ("f_".equals(feild.substring(0, 2))) {
                    tableFields.setField(feild.substring(2).toLowerCase());
                } else {
                    tableFields.setField(feild.toLowerCase());
                }
                tableModel.setFields(fields);
            }
        }
        entity.setTables(JsonUtil.getObjectToString(list));

        //取出列表数据中的查询列表和数据列表
        Map<String, Object> columnDataMap = JsonUtil.stringToMap(entity.getColumnData());
        if (columnDataMap != null) {
            for (Map.Entry<String, Object> entry : columnDataMap.entrySet()) {
                if ("searchList".equals(entry.getKey())) {
                    List<FieLdsModel> fieLdsModelList = JsonUtil.getJsonToList(entry.getValue(), FieLdsModel.class);
                    for (FieLdsModel fieLdsModel : fieLdsModelList) {
                        String vModel = fieLdsModel.getVModel().toLowerCase();
                        String modelStr = fieLdsModel.getVModel();
                        //去除F_
                        if (!StringUtil.isEmpty(vModel) && "f_".equals(vModel.substring(0, 2))) {
                            fieLdsModel.setVModel(modelStr.substring(2).toLowerCase());
                        } else if (!StringUtil.isEmpty(vModel)) {
                            fieLdsModel.setVModel(modelStr.toLowerCase());
                        }

                    }
                    entry.setValue(fieLdsModelList);
                }
                if ("columnList".equals(entry.getKey())) {
                    List<ColumnListField> columnListFields = JsonUtil.getJsonToList(entry.getValue(), ColumnListField.class);
                    for (ColumnListField columnListField : columnListFields) {
                        String prop = columnListField.getProp().toLowerCase();
                        String modelStr = columnListField.getProp();
                        //去除F_
                        if (!StringUtil.isEmpty(prop) && "f_".equals(prop.substring(0, 2))) {
                            columnListField.setProp(modelStr.substring(2).toLowerCase());
                        } else if (!StringUtil.isEmpty(prop)) {
                            columnListField.setProp(modelStr.toLowerCase());
                        }
                    }
                    entry.setValue(columnListFields);
                }
            }
        }

        entity.setColumnData(JsonUtil.getObjectToString(columnDataMap));


        Map<String, Object> formData = JsonUtil.stringToMap(entity.getFormData());

        List<FieLdsModel> modelList = JsonUtil.getJsonToList(formData.get("fields" ).toString(), FieLdsModel.class);
        for (FieLdsModel fieLdsModel : modelList) {
            //去除F_
            if (!StringUtil.isEmpty(fieLdsModel.getVModel())) {
                if ("f_".equals(fieLdsModel.getVModel().substring(0, 2).toLowerCase())) {
                    String modelStr = fieLdsModel.getVModel();
                    fieLdsModel.setVModel(modelStr.substring(2).toLowerCase());
                } else {
                    String modelStr = fieLdsModel.getVModel();
                    fieLdsModel.setVModel(modelStr.toLowerCase());
                }
            }

            ConfigModel configModel = fieLdsModel.getConfig();
            //子表
            if ("table".equals(configModel.getJnpfKey())) {
                List<FieLdsModel> childlist = JsonUtil.getJsonToList(configModel.getChildren(), FieLdsModel.class);
                for (FieLdsModel childmodel : childlist) {
                    //前台界面的属性去掉前2个
                    if (StringUtil.isNotEmpty(childmodel.getVModel())) {
                        if ("f_".equals(childmodel.getVModel().substring(0, 2).toLowerCase())) {
                            String vmodel = childmodel.getVModel().substring(2).toLowerCase();
                            childmodel.setVModel(vmodel);
                        } else {
                            String vmodel = childmodel.getVModel().toLowerCase();
                            childmodel.setVModel(vmodel);
                        }
                    }
                }
                fieLdsModel.getConfig().setChildren(childlist);
            }
        }
        formData.put("fields" , JsonUtil.getObjectToString(modelList));
        entity.setFormData(JsonUtil.getObjectToString(formData));

        return entity;
    }
}
