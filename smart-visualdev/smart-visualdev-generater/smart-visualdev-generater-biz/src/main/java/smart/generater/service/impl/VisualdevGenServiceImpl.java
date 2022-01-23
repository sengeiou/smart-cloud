package smart.generater.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import smart.base.UserInfo;
import smart.base.VisualdevEntity;
import smart.base.entity.DictionaryDataEntity;
import smart.base.mapper.VisualdevMapper;
import smart.base.model.DownloadCodeForm;
import smart.base.model.FormDataModel;
import smart.base.model.SubClassModel;
import smart.base.model.TableModel;
import smart.base.model.Template6.ColumnListField;
import smart.base.model.Template6.IndexGridField6Model;
import smart.base.model.Template6.Template6Model;
import smart.base.model.Template7.ChildrenModel;
import smart.base.service.DictionaryDataService;
import smart.base.service.VisualdevService;
import smart.base.util.GetGenDataSourceUtil;
import smart.base.util.SourceUtil;
import smart.base.util.genUtil.AppIndexGenUtil;
import smart.base.util.genUtil.AppVueGenUtil;
import smart.base.util.genUtil.VueGenUtil;
import smart.base.util.genUtil.WorkVueGenUtil;
import smart.base.util.genUtil.custom.CustomGenerator;
import smart.base.util.genUtil.custom.VelocityEnum;
import smart.base.util.genUtil.custom.VisualUtils;
import smart.config.ConfigValueUtil;
import smart.generater.model.GenBaseInfo;
import smart.generater.model.GenFileNameSuffix;
import smart.generater.service.VisualdevGenService;
import smart.onlinedev.model.fields.FieLdsModel;
import smart.onlinedev.model.fields.config.ConfigModel;
import smart.onlinedev.model.fields.slot.SlotModel;
import smart.onlinedev.model.fields.slot.SlotOptionModel;
import smart.util.*;
import smart.util.visiual.DataTypeConst;
import smart.util.visiual.SmartKeyConsts;
import lombok.Cleanup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * 可视化开发功能表
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-04-02
 */
@Service
public class VisualdevGenServiceImpl extends ServiceImpl<VisualdevMapper, VisualdevEntity> implements VisualdevGenService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private DataSourceUtil dataSourceUtil;
    @Autowired
    private DictionaryDataService dictionaryDataService;
    @Autowired
    private VisualdevService visualdevService;

    @Override
    public String codeGengerate(String id, DownloadCodeForm downloadCodeForm) throws SQLException {
        UserInfo userInfo = userProvider.get();
        VisualdevEntity entity = visualdevService.getInfo(id);
        //去除模板中的F_
        entity = VisualUtils.delAllfKey(entity);
        //去除模板字段下划线
        entity = VisualUtils.delete(entity);

        VisualdevEntity htmlEntity = entity;
        if (entity != null) {
            if (!StringUtil.isEmpty(entity.getTables())) {
                DictionaryDataEntity dentity = dictionaryDataService.getById(downloadCodeForm.getModule());
                FormDataModel model = JsonUtil.getJsonToBean(entity.getFormData(), FormDataModel.class);
                model.setClassName(downloadCodeForm.getClassName().substring(0, 1).toUpperCase() + downloadCodeForm.getClassName().substring(1));
                model.setAreasName(dentity != null ? dentity.getEnCode() : downloadCodeForm.getModule());
                model.setServiceDirectory(configValueUtil.getServiceDirectoryPath());
                FormDataModel htmlModel = JsonUtil.getJsonToBean(model, FormDataModel.class);


                List<FieLdsModel> filterFeildList = JsonUtil.getJsonToList(model.getFields(), FieLdsModel.class);
                //先去除多级控件
                filterFeildList = VisualUtils.deleteMore(filterFeildList);
                //再去除无意义控件
                filterFeildList = VisualUtils.deleteVmodel(filterFeildList);

                model.setFields(JSON.toJSONString(filterFeildList));
                String fileName = RandomUtil.uuId();
                //初始化模板
                VelocityEnum.init.initVelocity(configValueUtil.getTemplateCodePath());

                List<TableModel> list = JsonUtil.getJsonToList(entity.getTables(), TableModel.class);
                //获取主键
                String mainTable = list.get(0).getTable();
                @Cleanup Connection conn = VisualUtils.getTableConn();
                //获取主键
                String pKeyName = VisualUtils.getpKey(conn, mainTable);

                //获取其他子表的主键
                Map<String, Object> childpKeyMap = new HashMap<>(16);
                for (TableModel tableModel : list) {
                    String childKey = VisualUtils.getpKey(conn, mainTable);
                    if ("f_".equals(childKey.substring(0, 2).toLowerCase())) {
                        childKey = childKey.substring(2);
                    }
                    childpKeyMap.put(tableModel.getTable(), childKey);
                }
                //判断子表名称
                List<String> childTb = new ArrayList();
                if (!StringUtil.isEmpty(downloadCodeForm.getSubClassName())) {
                    childTb = Arrays.asList(downloadCodeForm.getSubClassName().split(","));
                }

                Set<String> set = new HashSet<>(childTb);
                boolean result = childTb.size() == set.size() ? true : false;
                if (!result) {
                    return "名称不能重复";
                }
                if (entity.getType() == 4) {
                    this.htmlTemplates(fileName, htmlEntity, model, htmlModel, "TemplateCode6", childTb, pKeyName);
                    //生成模型
                    this.modelTemplates(fileName, entity, model, "TemplateCode6", childTb, pKeyName);
                    //生成功能类
                    downloadCodeForm.setModule(dentity != null ? dentity.getEnCode() : downloadCodeForm.getModule());
                    this.generate(entity, model, dataSourceUtil, configValueUtil.getTemplateCodePath(), fileName, downloadCodeForm, childTb, pKeyName, childpKeyMap);
                }
                if (entity.getType() == 2) {
                    AppVueGenUtil.htmlTemplates(fileName, entity, model, "TemplateCode5", userInfo, configValueUtil);
                    //生成模型
                    this.modelTemplates(fileName, entity, model, "TemplateCode6", childTb, pKeyName);

                    //生成功能类
                    downloadCodeForm.setModule(dentity != null ? dentity.getEnCode() : downloadCodeForm.getModule());
                    this.generate(entity, model, dataSourceUtil, configValueUtil.getTemplateCodePath(), fileName, downloadCodeForm, childTb, pKeyName, childpKeyMap);
                }
                if (entity.getType() == 3) {
                    //工作流模板
                    downloadCodeForm.setModule("form");
                    String templatePath = "TemplateCode7";
                    WorkVueGenUtil.htmlTemplates(fileName, entity, downloadCodeForm, model, templatePath, userInfo, configValueUtil, pKeyName);
                    WorkVueGenUtil.generate(entity, dataSourceUtil, fileName, templatePath, downloadCodeForm, userInfo, configValueUtil);
                }
                if (entity.getType() == 5) {
                    //app代码生成器
                    String templatePath = "TemplateCode8";
                    AppIndexGenUtil.htmlTemplates(fileName, entity, downloadCodeForm, model, templatePath, userInfo, configValueUtil, pKeyName);
                    AppIndexGenUtil.generate(entity, dataSourceUtil, fileName,templatePath, downloadCodeForm, userInfo, configValueUtil);
                }
                return fileName;
            }
        }
        return null;
    }


    @Override
    public void modelTemplates(String fileName, VisualdevEntity entity, FormDataModel model, String templatePath, List<String> childTable, String pKeyName) {
        List<TableModel> list = JsonUtil.getJsonToList(entity.getTables(), TableModel.class);

        //取出列表数据中的查询列表和数据列表，禁止用实体去接
        Map<String,Object> columnDataMap = JsonUtil.stringToMap(entity.getColumnData());
        //记录是否有分页
        Boolean hasPage = (Boolean) columnDataMap.get("hasPage");

        List<FieLdsModel> modelList = JsonUtil.getJsonToList(model.getFields(), FieLdsModel.class);

        List<FieLdsModel> searchList = JsonUtil.getJsonToList(columnDataMap.get("searchList"), FieLdsModel.class);
        List<ColumnListField> columnList = JsonUtil.getJsonToList(columnDataMap.get("columnList"), ColumnListField.class);

        Template6Model temModel = new Template6Model();
        temModel.setServiceDirectory(configValueUtil.getServiceDirectoryPath());
        temModel.setCreateDate(DateUtil.getNow());
        temModel.setCreateUser(GenBaseInfo.AUTHOR);
        temModel.setCopyright(GenBaseInfo.COPYRIGHT);
        temModel.setVersion(GenBaseInfo.VERSION);
        temModel.setDescription(model.getClassName() + "模型");

        Map<String, Object> modelMap = new HashMap<>(16);
        modelMap.put("tableModel", list.get(0));
        modelMap.put("searchList", searchList);
        modelMap.put("columnList", columnList);
        modelMap.put("formDataList", modelList);
        modelMap.put("className", model.getClassName());
        modelMap.put("areasName", model.getAreasName());
        modelMap.put("pKeyName", pKeyName);
        //表名与类名对应
        list.remove(0);
        List<SubClassModel> subList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            SubClassModel subClassModel = new SubClassModel();
            subClassModel.setSubKey(list.get(i).getTable());
            subClassModel.setSubValue(childTable.get(i));
            subList.add(subClassModel);
        }
        modelMap.put("subtables", subList);
        //设置包结构前缀
        modelMap.put("package", "smart");
        modelMap.put("genInfo", temModel);
        VueGenUtil.htmlTemplates(configValueUtil.getServiceDirectoryPath() + fileName, modelMap, templatePath, "model", model.getClassName(), hasPage);

    }

    @Override
    public void htmlTemplates(String fileName, VisualdevEntity entity, FormDataModel model, FormDataModel htmlModel, String templatePath, List<String> childTable, String pKeyName) {

        Map<String, Object> map = new HashMap<>(16);
        map.put("module", model.getAreasName());
        map.put("className", model.getClassName());
        Map<String, Object> modelMap = JsonUtil.entityToMap(model);
        map.put("pKeyName", pKeyName);
        map.putAll(modelMap);
        map.put("htmlFields", JsonUtil.getJsonToList(htmlModel.getFields(), FieLdsModel.class));

        //取出列表数据中的查询列表和数据列表，禁止用实体去接
        Map<String, Object> columnDataMap = JsonUtil.stringToMap(entity.getColumnData());
        map.put("columnData", columnDataMap);
        //子表坐标
        int num = 0;
        //添加判断默认值类型的字段
        List<FieLdsModel> list = JsonUtil.getJsonToList(model.getFields(), FieLdsModel.class);
        for (FieLdsModel model1 : list) {
            ConfigModel configModel = model1.getConfig();
            if (configModel.getDefaultValue() instanceof String) {
                configModel.setValueType("String");
            }
            if (configModel.getDefaultValue() == null) {
                configModel.setValueType("undefined");
            }
            //级联判断多选还是单选
            if ("cascader".equals(configModel.getJnpfKey())) {
                Map<String, Object> propsMap = JsonUtil.stringToMap(model1.getProps().getProps());
                model1.setMultiple(String.valueOf(propsMap.get("multiple")));
            }
            //子表
            if (SmartKeyConsts.CHILD_TABLE.equals(configModel.getJnpfKey())) {
                ChildrenModel child = new ChildrenModel();
                List<FieLdsModel> childlist = JsonUtil.getJsonToList(configModel.getChildren(), FieLdsModel.class);
                for (FieLdsModel childmodel : childlist) {
                    ConfigModel childconfig = childmodel.getConfig();
                    if (childconfig.getDefaultValue() instanceof String) {
                        childconfig.setValueType("String");
                    }
                    if (childconfig.getDefaultValue() == null) {
                        childconfig.setValueType("undefined");
                    }
                    //级联判断多选还是单选
                    if (SmartKeyConsts.CASCADER.equals(childconfig.getJnpfKey())) {
                        Map<String, Object> propsMap = JsonUtil.stringToMap(childmodel.getProps().getProps());
                        childmodel.setMultiple(String.valueOf(propsMap.get("multiple")));
                    }
                    //选择框都转成字符串
                    if (DataTypeConst.STATIC.equals(String.valueOf(childconfig.getDataType()))) {
                        SlotModel slotModel = childmodel.getSlot();
                        if (slotModel != null) {
                            List<SlotOptionModel> options = JsonUtil.getJsonToList(slotModel.getOptions(), SlotOptionModel.class);
                            slotModel.setOptions(JsonUtilEx.getObjectToString(options));
                            List<Map<String, Object>> appOptions = new ArrayList<>();
                            for (SlotOptionModel option : options) {
                                Map<String, Object> chilMap = new HashMap<>(16);
                                chilMap.put("label", option.getFullName());
                                chilMap.put("value", option.getId());
                                appOptions.add(chilMap);
                            }
                            slotModel.setAppOptions(JsonUtilEx.getObjectToString(appOptions));
                            childmodel.setSlot(slotModel);
                        }
                    }
                }
                configModel.setChildren(childlist);
                child.setChildrenList(childlist);
                child.setTableModel(model1.getVModel());
                String name = childTable.get(num);
                String className = name.substring(0, 1).toUpperCase() + name.substring(1);
                child.setClassName(className);
                num++;
                String vmodelName = name.substring(0, 1).toLowerCase() + name.substring(1) + "EntityList";
                model1.setConfig(configModel);
                model1.setVModel(vmodelName);
            }
        }
        map.put("fields", list);
        VueGenUtil.htmlTemplates(model.getServiceDirectory() + fileName, map, templatePath, "vue", model.getClassName(), null);
    }


    public void setCode(FormDataModel formDataModel, DataSourceUtil dataSourceUtil, String path, String fileName, DownloadCodeForm downloadCodeForm, VisualdevEntity entity, List<String> childTable, String pKeyName, Map<String, Object> childpKeyMap) throws SQLException {
        List<TableModel> tablesList = JsonUtil.getJsonToList(entity.getTables(), TableModel.class);

        //取出列表数据中的查询列表和数据列表，禁止用实体去接
        Map<String,Object> columnDataMap = JsonUtil.stringToMap(entity.getColumnData());

        List<FieLdsModel> filterFeildList = JsonUtil.getJsonToList(formDataModel.getFields(), FieLdsModel.class);

        UserInfo userInfo = userProvider.get();
        Template6Model model = new Template6Model();
        model.setColumnListFields(JsonUtil.getJsonToList(columnDataMap.get("columnList"), ColumnListField.class));
        String className = downloadCodeForm.getClassName();
        if (className.length() > 0) {
            className = className.substring(0, 1).toUpperCase() + className.substring(1);
            model.setClassName(className);
        }
        model.setServiceDirectory(configValueUtil.getServiceDirectoryPath());
        model.setCreateDate(DateUtil.getNow());
        model.setCreateUser(GenBaseInfo.AUTHOR);
        model.setVersion(GenBaseInfo.VERSION);
        model.setCopyright(GenBaseInfo.COPYRIGHT);
        model.setDescription(downloadCodeForm.getDescription());

        List<String> tableList = new ArrayList<>();
        for (TableModel tableModel : tablesList) {
            if ("1".equals(tableModel.getTypeId())) {
                tableList.add(tableModel.getTable());
            }
        }
        String tableName = tableList.get(0);

        columnDataMap.put("genInfo", model);
        columnDataMap.put("areasName", downloadCodeForm.getModule());
        columnDataMap.put("formList", filterFeildList);
        columnDataMap.put("subClassList", childTable);
        columnDataMap.put("pKeyName", pKeyName);
        columnDataMap.put("childPKeyMap", childpKeyMap);

        if (dataSourceUtil.getUrl().contains(DbType.ORACLE.getDb())) {
            columnDataMap.put("dbType", "oracle");
        }

        //去除主表
        tablesList.remove(0);
        //设置子表主键
        for (TableModel tableModel : tablesList) {
            for (Map.Entry<String, Object> entryMap : childpKeyMap.entrySet()) {
                if (tableModel.getTable().equals(entryMap.getKey())) {
                    tableModel.setTableKey(String.valueOf(entryMap.getValue()));
                }
            }
        }
        model.setDbTableRelation(JsonUtil.getJsonToList(tablesList, TableModel.class));
        List<SubClassModel> subList = new ArrayList<>();
        for (int i = 0; i < tablesList.size(); i++) {
            SubClassModel subClassModel = new SubClassModel();
            subClassModel.setSubKey(tablesList.get(i).getTable());
            subClassModel.setSubValue(childTable.get(i));
            subList.add(subClassModel);
        }
        columnDataMap.put("subtables", subList);
        //标识生成主表代码
        columnDataMap.put("main", new Object());


        CustomGenerator mpg = new CustomGenerator(columnDataMap);
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setFileOverride(true);
        // 不需要ActiveRecord特性的请改为false
        gc.setActiveRecord(false);
        // XML 二级缓存
        gc.setEnableCache(false);
        // XML ResultMap
        gc.setBaseResultMap(false);
        // XML columList
        gc.setBaseColumnList(false);
        gc.setAuthor(userInfo.getUserName() + "/" + userInfo.getUserAccount());
        gc.setOpen(false);

        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setEntityName(model.getClassName() + GenFileNameSuffix.ENTITY);
        gc.setMapperName(model.getClassName() + GenFileNameSuffix.MAPPER);
        gc.setXmlName(model.getClassName() + GenFileNameSuffix.MAPPER_XML);
        gc.setServiceName(model.getClassName() + GenFileNameSuffix.SERVICE);
        gc.setServiceImplName(model.getClassName() + GenFileNameSuffix.SERVICEIMPL);
        gc.setControllerName(model.getClassName() + GenFileNameSuffix.CONTROLLER);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = GetGenDataSourceUtil.getGenDataSource(downloadCodeForm.getDataSourceId());

        mpg.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setEntityLombokModel(true);
        // 表名生成策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 需要生成的表
        strategy.setInclude(tableName);
        strategy.setRestControllerStyle(true);
        mpg.setStrategy(strategy);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("smart");
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        List<FileOutConfig> focList = new ArrayList<>();
        String javaPath = model.getServiceDirectory();
        focList.add(new FileOutConfig("TemplateCode6" + File.separator + "java" + File.separator + "Controller.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "java" + File.separator + "controller" + File.separator + tableInfo.getControllerName() + StringPool.DOT_JAVA;
            }
        });
        focList.add(new FileOutConfig("TemplateCode6" + File.separator + "java" + File.separator + "Entity.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "java" + File.separator + "entity" + File.separator + tableInfo.getEntityName() + StringPool.DOT_JAVA;
            }
        });

        focList.add(new FileOutConfig("TemplateCode6" + File.separator + "java" + File.separator + "Mapper.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "java" + File.separator + "mapper" + File.separator + tableInfo.getMapperName() + StringPool.DOT_JAVA;
            }
        });
        focList.add(new FileOutConfig("TemplateCode6" + File.separator + "java" + File.separator + "Mapper.xml.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "resources" + File.separator + "mapper" + File.separator + tableInfo.getMapperName() + StringPool.DOT_XML;
            }
        });
        focList.add(new FileOutConfig("TemplateCode6" + File.separator + "java" + File.separator + "Service.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "java" + File.separator + "service" + File.separator + tableInfo.getServiceName() + StringPool.DOT_JAVA;
            }
        });
        focList.add(new FileOutConfig("TemplateCode6" + File.separator + "java" + File.separator + "ServiceImpl.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "java" + File.separator + "service" + File.separator + "impl" + File.separator + tableInfo.getServiceImplName() + StringPool.DOT_JAVA;
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setTemplate(new TemplateConfig().setXml(null).setMapper(null).setController(null).setEntity(null).setService(null).setServiceImpl(null));
        mpg.setCfg(cfg);
        // 执行生成
        mpg.execute(path);
    }

    /**
     * 子表的model
     * @param dataSourceUtil
     * @param path
     * @param fileName
     * @param entity
     * @param className
     * @param table
     * @param downloadCodeForm
     * @return
     * @throws SQLException
     */
    private String childTable(DataSourceUtil dataSourceUtil, String path, String fileName, VisualdevEntity entity, String className, String table, DownloadCodeForm downloadCodeForm) throws SQLException {
        Map<String, Object> columndata = JsonUtil.stringToMap(entity.getColumnData());

        @Cleanup Connection conn = VisualUtils.getTableConn();
        //获取主键
        String pKeyName = VisualUtils.getpKey(conn, table);
        columndata.put("pKeyName", pKeyName);


        UserInfo userInfo = userProvider.get();
        Template6Model model = new Template6Model();
        model.setColumnListFields(JsonUtil.getJsonToList(columndata.get("columnList"), ColumnListField.class));

        if (className.length() > 0) {
            className = className.substring(0, 1).toUpperCase() + className.substring(1);
            model.setClassName(className);
        }
        model.setServiceDirectory(configValueUtil.getServiceDirectoryPath());
        model.setCreateDate(DateUtil.getNow());
        model.setCreateUser(GenBaseInfo.AUTHOR);
        model.setVersion(GenBaseInfo.VERSION);
        model.setCopyright(GenBaseInfo.COPYRIGHT);
        model.setDescription(table);
        columndata.put("areasName", downloadCodeForm.getModule());
        columndata.put("genInfo", model);

        CustomGenerator mpg = new CustomGenerator(columndata);
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setFileOverride(true);
        // 不需要ActiveRecord特性的请改为false
        gc.setActiveRecord(false);
        // XML 二级缓存
        gc.setEnableCache(false);
        // XML ResultMap
        gc.setBaseResultMap(false);
        // XML columList
        gc.setBaseColumnList(false);
        gc.setAuthor(model.getCreateUser());
        gc.setOpen(false);

        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setEntityName(model.getClassName() + GenFileNameSuffix.ENTITY);
        gc.setMapperName(model.getClassName() + GenFileNameSuffix.MAPPER);
        gc.setXmlName(model.getClassName() + GenFileNameSuffix.MAPPER_XML);
        gc.setServiceName(model.getClassName() + GenFileNameSuffix.SERVICE);
        gc.setServiceImplName(model.getClassName() + GenFileNameSuffix.SERVICEIMPL);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        SourceUtil sourceUtil = new SourceUtil();
        DataSourceConfig dsc = sourceUtil.dbConfig(userInfo.getTenantDbConnectionString());
        if (dataSourceUtil.getUrl().contains(DbType.ORACLE.getDb())) {
            String schema = dataSourceUtil.getUserName();
            //oracle 默认 schema=username
            dsc.setSchemaName(schema.toUpperCase());
        }
        mpg.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setEntityLombokModel(true);
        // 表名生成策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        if (dataSourceUtil.getUrl().contains(DbType.MYSQL.getDb())) {
            // 需要生成的表
            strategy.setInclude(table);
        } else if (dataSourceUtil.getUrl().contains(DbType.SQL_SERVER.getDb())) {
            // 需要生成的表
            strategy.setInclude(table);
        } else if (dataSourceUtil.getUrl().contains(DbType.ORACLE.getDb())) {
            // 需要生成的表
            strategy.setInclude(table);
        }
        strategy.setRestControllerStyle(true);
        mpg.setStrategy(strategy);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("smart");
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        List<FileOutConfig> focList = new ArrayList<>();
        String javaPath = model.getServiceDirectory();
        focList.add(new FileOutConfig("TemplateCode6" + File.separator + "java" + File.separator + "Entity.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "java" + File.separator + "entity" + File.separator + tableInfo.getEntityName() + StringPool.DOT_JAVA;
            }
        });
        focList.add(new FileOutConfig("TemplateCode6" + File.separator + "java" + File.separator + "Mapper.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "java" + File.separator + "mapper" + File.separator + tableInfo.getMapperName() + StringPool.DOT_JAVA;
            }
        });
        focList.add(new FileOutConfig("TemplateCode6" + File.separator + "java" + File.separator + "Mapper.xml.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "resources" + File.separator + "mapper" + File.separator + tableInfo.getMapperName() + StringPool.DOT_XML;
            }
        });
        focList.add(new FileOutConfig("TemplateCode6" + File.separator + "java" + File.separator + "Service.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "java" + File.separator + "service" + File.separator + tableInfo.getServiceName() + StringPool.DOT_JAVA;
            }
        });
        focList.add(new FileOutConfig("TemplateCode6" + File.separator + "java" + File.separator + "ServiceImpl.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return javaPath + fileName + File.separator + "java" + File.separator + "service" + File.separator + "impl" + File.separator + tableInfo.getServiceImplName() + StringPool.DOT_JAVA;
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setTemplate(new TemplateConfig().setXml(null).setMapper(null).setController(null).setEntity(null).setService(null).setServiceImpl(null));
        mpg.setCfg(cfg);
        // 执行生成
        mpg.execute(path);
        return fileName;
    }


    /**
     * 生成java代码
     * @param entity
     * @param formDataModel
     * @param dataSourceUtil
     * @param templateCodePath
     * @param fileName
     * @param downloadCodeForm
     * @param childTable
     * @param pKeyName
     * @param childpKeyMap
     * @throws SQLException
     */
    @Override
    public void generate(VisualdevEntity entity, FormDataModel formDataModel, DataSourceUtil dataSourceUtil, String templateCodePath, String fileName, DownloadCodeForm downloadCodeForm, List<String> childTable, String pKeyName, Map<String, Object> childpKeyMap) throws SQLException {
        List<TableModel> list = JsonUtil.getJsonToList(entity.getTables(), TableModel.class);
        //生成代码
        int i = 0;
        for (TableModel model : list) {
            if ("1".equals(model.getTypeId())) {
                setCode(formDataModel, dataSourceUtil, templateCodePath, fileName, downloadCodeForm, entity, childTable, pKeyName, childpKeyMap);
            } else if ("0".equals(model.getTypeId())) {
                childTable(dataSourceUtil, templateCodePath, fileName, entity, downloadCodeForm.getSubClassName().split(",")[i], model.getTable(), downloadCodeForm);
                i++;
            }
        }
    }
}
