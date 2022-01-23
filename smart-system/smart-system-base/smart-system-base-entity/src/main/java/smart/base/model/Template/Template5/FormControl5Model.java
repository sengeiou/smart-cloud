package smart.base.model.Template.Template5;

import lombok.Data;

import java.util.List;

/**
 * 表单字段
 */
@Data
public class FormControl5Model {
    //表单Tab
    private String tab;
    //控件
    private String control;
    //标题
    private String title;
    //类的名称（添加的字段）
    private String className;
    //字段
    private String field;
    //验证
    private String[] check;
    //提示信息
    private String placeholder;
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
    //单据号码 - 单据规则
    private String billRule;
    //编辑表格 - 表名
    private String table;
    //编辑表格 - 字段列表  FormControlGridField
    private List<FormControlGridField5Model> gridFieldControls;
}
