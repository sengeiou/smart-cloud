package smart.base.util.genUtil.custom;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import smart.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Slf4j
public class CustomGenerator {

    public ConfigBuilder config;
    public InjectionConfig injectionConfig;
    public DataSourceConfig dataSource;
    public StrategyConfig strategy;
    public PackageConfig packageInfo;
    public TemplateConfig template;
    public GlobalConfig globalConfig;
    public AbstractTemplateEngine templateEngine;

    private Map<String, Object> customParams;

    public CustomGenerator(Map<String, Object> customParams) {
        this.customParams = customParams;
    }

    public void execute(String path) {
        if (null == this.config) {
            this.config = new ConfigBuilder(this.packageInfo, this.dataSource, this.strategy, this.template, this.globalConfig);
            if (null != this.injectionConfig) {
                this.injectionConfig.setConfig(this.config);
            }
        }
        if (null == this.templateEngine) {
            if(customParams != null){
                this.templateEngine = new CustomTemplateEngine(customParams,path);
            }else{
                this.templateEngine = new CustomTemplateEngine(path);
            }
        }
        this.templateEngine.init(this.pretreatmentConfigBuilder(this.config)).mkdirs().batchOutput().open();
    }

    protected List<TableInfo> getAllTableInfoList(ConfigBuilder config) {
        return config.getTableInfoList();
    }

    protected ConfigBuilder pretreatmentConfigBuilder(ConfigBuilder config) {
        if (null != this.injectionConfig) {
            this.injectionConfig.initMap();
            config.setInjectionConfig(this.injectionConfig);
        }
        List<TableInfo> tableList = this.getAllTableInfoList(config);
        Iterator var3 = tableList.iterator();
        while(var3.hasNext()) {
            TableInfo tableInfo = (TableInfo)var3.next();
            if (config.getGlobalConfig().isActiveRecord()) {
                tableInfo.setImportPackages(Model.class.getCanonicalName());
            }
            if (tableInfo.isConvert()) {
                tableInfo.setImportPackages(TableName.class.getCanonicalName());
            }
            if (config.getStrategyConfig().getLogicDeleteFieldName() != null && tableInfo.isLogicDelete(config.getStrategyConfig().getLogicDeleteFieldName())) {
                tableInfo.setImportPackages(TableLogic.class.getCanonicalName());
            }
            if (StringUtil.isNotEmpty(config.getStrategyConfig().getVersionFieldName())) {
                tableInfo.setImportPackages(Version.class.getCanonicalName());
            }
            if (StringUtil.isNotEmpty(config.getSuperEntityClass())) {
                tableInfo.setImportPackages(config.getSuperEntityClass());
            } else {
                tableInfo.setImportPackages(Serializable.class.getCanonicalName());
            }
            if (config.getStrategyConfig().isEntityBooleanColumnRemoveIsPrefix()) {
                tableInfo.getFields().stream().filter((field) -> {
                    return "boolean".equalsIgnoreCase(field.getPropertyType());
                }).filter((field) -> {
                    return field.getPropertyName().startsWith("is");
                }).forEach((field) -> {
                    field.setPropertyName(config.getStrategyConfig(), StringUtils.removePrefixAfterPrefixToLower(field.getPropertyName(), 2));
                });
            }
        }
        return config.setTableInfoList(tableList);
    }

    public DataSourceConfig getDataSource() {
        return this.dataSource;
    }

    public CustomGenerator setDataSource(DataSourceConfig dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public StrategyConfig getStrategy() {
        return this.strategy;
    }

    public CustomGenerator setStrategy(StrategyConfig strategy) {
        this.strategy = strategy;
        return this;
    }

    public PackageConfig getPackageInfo() {
        return this.packageInfo;
    }

    public CustomGenerator setPackageInfo(PackageConfig packageInfo) {
        this.packageInfo = packageInfo;
        return this;
    }

    public TemplateConfig getTemplate() {
        return this.template;
    }

    public CustomGenerator setTemplate(TemplateConfig template) {
        this.template = template;
        return this;
    }

    public ConfigBuilder getConfig() {
        return this.config;
    }

    public CustomGenerator setConfig(ConfigBuilder config) {
        this.config = config;
        return this;
    }

    public GlobalConfig getGlobalConfig() {
        return this.globalConfig;
    }

    public CustomGenerator setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        return this;
    }

    public InjectionConfig getCfg() {
        return this.injectionConfig;
    }

    public CustomGenerator setCfg(InjectionConfig injectionConfig) {
        this.injectionConfig = injectionConfig;
        return this;
    }

    public AbstractTemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    public CustomGenerator setTemplateEngine(AbstractTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        return this;
    }
}
