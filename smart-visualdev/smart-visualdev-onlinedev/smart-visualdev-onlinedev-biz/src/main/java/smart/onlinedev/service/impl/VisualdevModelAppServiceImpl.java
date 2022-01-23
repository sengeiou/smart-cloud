package smart.onlinedev.service.impl;

import smart.onlinedev.util.AutoFeildsUtil;
import smart.util.JsonUtil;
import smart.util.JsonUtilEx;
import smart.util.StringUtil;
import smart.base.VisualdevEntity;
import smart.base.model.ColumnDataModel;
import smart.base.model.FormDataModel;
import smart.base.model.TableModel;
import smart.base.service.VisualdevService;
import smart.engine.model.flowdynamic.FormAllModel;
import smart.engine.model.flowdynamic.FormColumnModel;
import smart.engine.model.flowdynamic.FormEnum;
import smart.engine.util.FormCloumnUtil;
import smart.onlinedev.service.VisualdevModelAppService;
import smart.onlinedev.service.VisualdevModelDataService;
import smart.onlinedev.model.fields.config.ConfigModel;
import smart.onlinedev.entity.VisualdevModelDataEntity;
import smart.onlinedev.model.PaginationModel;
import smart.onlinedev.model.VisualdevModelDataCrForm;
import smart.onlinedev.model.VisualdevModelDataInfoVO;
import smart.onlinedev.model.VisualdevModelDataUpForm;
import smart.onlinedev.model.fields.FieLdsModel;
import smart.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VisualdevModelAppServiceImpl implements VisualdevModelAppService {
    @Autowired
    private VisualdevService visualdevService;
    @Autowired
    private VisualdevModelDataService visualdevModelDataService;

    @Override
    public List<Map<String, Object>> resultList(String modelId, PaginationModel paginationModel) throws DataException, ParseException, SQLException, IOException {
        VisualdevEntity entity = visualdevService.getInfo(modelId);
        //赋值type
        ColumnDataModel columnDataModel = JsonUtil.getJsonToBean(entity.getColumnData(), ColumnDataModel.class);
        columnDataModel.setType(1);
        entity.setColumnData(JsonUtilEx.getObjectToString(columnDataModel));
        common(entity);
        List<Map<String, Object>> realList = visualdevModelDataService.getListResult(entity, paginationModel);
        //排序
        if (StringUtil.isNotEmpty(paginationModel.getSidx()) && realList.size() > 0) {
            Object value = realList.get(0).get(paginationModel.getSidx());
            if (value != null) {
                if ("desc".equals(paginationModel.getSort())) {
                    realList.sort(Comparator.comparing((Map<String, Object> h) -> ((String) h.get(paginationModel.getSidx()))).reversed());
                } else {
                    realList.sort(Comparator.comparing((Map<String, Object> h) -> ((String) h.get(paginationModel.getSidx()))));
                }
            }
        }
        return realList;
    }

    @Override
    public void create(VisualdevEntity entity, String data) throws DataException, SQLException {
        VisualdevModelDataCrForm form = new VisualdevModelDataCrForm();
        form.setData(data);
        common(entity);
        visualdevModelDataService.create(entity, form);
    }

    @Override
    public boolean update(String id, VisualdevEntity entity, String data) throws DataException, SQLException {
        VisualdevModelDataUpForm form = new VisualdevModelDataUpForm();
        form.setData(data);
        common(entity);
        boolean flag = visualdevModelDataService.update(id, entity, form);
        return flag;
    }

    @Override
    public boolean delete(String id, VisualdevEntity entity) throws DataException, SQLException {
        boolean flag = false;
        List<TableModel> tableModelList = JsonUtil.getJsonToList(entity.getTables(), TableModel.class);
        if (tableModelList.size() > 0) {
            flag = visualdevModelDataService.tableDelete(id, entity);
        } else {
            VisualdevModelDataEntity dataEntity = visualdevModelDataService.getInfo(id);
            if (dataEntity != null) {
                visualdevModelDataService.delete(dataEntity);
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public Map<String, Object> info(String id, VisualdevEntity entity) throws DataException, ParseException, SQLException, IOException {
        List<TableModel> tableModelList = JsonUtil.getJsonToList(entity.getTables(), TableModel.class);
        VisualdevModelDataInfoVO vo = new VisualdevModelDataInfoVO();
        if (tableModelList.size() > 0) {
            common(entity);
            vo = visualdevModelDataService.tableInfo(id, entity);
        } else {
            VisualdevModelDataEntity dataEntity = visualdevModelDataService.getInfo(id);
            List<FieLdsModel> list = info(entity);
            if (dataEntity != null) {
                String data = AutoFeildsUtil.autoFeilds(list, dataEntity.getData());
                vo.setData(data);
                vo.setId(id);
            }
        }
        Map<String, Object> result = JsonUtil.entityToMap(vo);
        return result;
    }

    /**
     * 信息去多余控件
     * @param entity
     * @return
     */
    private List<FieLdsModel> info(VisualdevEntity entity) {
        //修改app属性没有默认值
        FormDataModel formDataModel = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
        List<FieLdsModel> formModel = JsonUtil.getJsonToList(formDataModel.getFields(), FieLdsModel.class);
        List<FieLdsModel> fieldsAll = fieldsAll(formModel);
        return fieldsAll;
    }

    /**
     * 查询、新增和修改属性
     * @param entity
     */
    private void common(VisualdevEntity entity) {
        //修改app属性没有默认值
        FormDataModel formDataModel = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
        List<FieLdsModel> formModel = JsonUtil.getJsonToList(formDataModel.getFields(), FieLdsModel.class);
        List<FieLdsModel> fieldsAll = fieldsAll(formModel);
        Map<String, Object> map = new HashMap<>(16);
        map.put("fields", fieldsAll);
        entity.setFormData(JsonUtil.getObjectToString(map));
    }

    /**
     * app的默认值
     * @param formModel
     * @return
     */
    private List<FieLdsModel> fieldsAll(List<FieLdsModel> formModel) {
        //修改app属性没有默认值
        List<FormAllModel> formAllModel = new ArrayList<>();
        FormCloumnUtil.recursionForm(formModel, formAllModel);
        //赋值主表的日期类型
        List<FormAllModel> formAll = formAllModel.stream().filter(t -> FormEnum.mast.getMessage().equals(t.getJnpfKey()) || FormEnum.table.getMessage().equals(t.getJnpfKey())).collect(Collectors.toList());
        List<FieLdsModel> fieldsAll = new ArrayList<>();
        for (FormAllModel model : formAll) {
            if (FormEnum.mast.getMessage().equals(model.getJnpfKey())) {
                FieLdsModel fieLdsModel = model.getFormColumnModel().getFieLdsModel();
                model(fieLdsModel);
                fieldsAll.add(fieLdsModel);
            } else {
                String tabVmodel = model.getChildList().getTableModel();
                FieLdsModel child = new FieLdsModel();
                child.setVModel(tabVmodel);
                String tableName = model.getChildList().getTableName();
                ConfigModel configModel = new ConfigModel();
                configModel.setTableName(tableName);
                configModel.setJnpfKey(FormEnum.table.getMessage());
                List<FieLdsModel> childAll = new ArrayList<>();
                List<FormColumnModel> childList = model.getChildList().getChildList();
                for (FormColumnModel column : childList) {
                    FieLdsModel fieLdsModel = column.getFieLdsModel();
                    model(fieLdsModel);
                    childAll.add(fieLdsModel);
                }
                configModel.setChildren(childAll);
                child.setConfig(configModel);
                fieldsAll.add(child);
            }
        }
        return fieldsAll;
    }

    /**
     * app日期赋默认属性
     * @param fieLdsModel
     */
    private void model(FieLdsModel fieLdsModel) {
        String jnpfkey = fieLdsModel.getConfig().getJnpfKey();
        if (StringUtil.isNotEmpty(fieLdsModel.getVModel())) {
            if ("date".equals(jnpfkey) || "dateRange".equals(jnpfkey)) {
                fieLdsModel.setFormat("yyyy-MM-dd");
                fieLdsModel.setValueformat("timestamp");
            } else if ("timeRange".equals(jnpfkey) || "time".equals(jnpfkey)) {
                fieLdsModel.setFormat("HH:mm:ss");
                fieLdsModel.setValueformat("HH:mm:ss");
            }
        }
    }

}
