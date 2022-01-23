package smart.onlinedev.model.fields.config;


import smart.onlinedev.model.fields.FieLdsModel;
import lombok.Data;

import java.util.List;

@Data
public class ConfigModel {
    private String label;
    private String labelWidth;
    private Boolean showLabel;
    private Boolean changeTag;
    private Boolean border;
    private String tag;
    private String tagIcon;
    private Boolean required;
    private String layout;
    private String dataType;
    private Integer span;
    private String jnpfKey;
    private String dictionaryType;
    private Integer formId;
    private Long renderKey;
    private List<RegListModel> regList;
    private Object defaultValue;
    //app静态数据
    private String options;
    //判断defaultValue类型
    private String valueType;
    private String propsUrl;
    private String optionType;
    private ConfigPropsModel props;
    //子表添加字段
    private String showTitle;
    private String tableName;
    private List<FieLdsModel> children;
    //单据规则使用
    private String rule;
    //隐藏
    private Boolean noShow=false;
}
