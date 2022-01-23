package smart.base.model.Template.Template2;

import lombok.Data;

/**
 * 表单字段
 */
@Data
public class FormControl2Model {
    //表单Tab
    private String tab;
    //控件
    private String control;
    //标题
    private String title;
    //字段
    private String field;
    //验证
    private String[] check;
    //提示信息
    private String placeholder;
    //跨列
    private int colspan;
    //是否隐藏
    private int isHide;
    //高度
    private int height;
    //数据来源
    private String dataSource;
    //数据来源 - 数据集合
    private String[] dataSourceList;
    //数据来源 - 数据字典
    private String dataSourceDictionary;
    //数据来源 - 数据字典IsTree
    private int dataSourceDictionaryIsTree;
    //数据集合
    private String[] list;
    //日期格式
    private String dateFormat;
    //默认值
    private String defaults;
    //文件格式
    private String[] extension;
    //当前数据
    private String provider;
}
