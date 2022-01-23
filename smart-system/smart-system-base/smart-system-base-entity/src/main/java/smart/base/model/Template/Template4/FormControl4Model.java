package smart.base.model.Template.Template4;

import lombok.Data;

import java.util.List;

/**
 * 表单字段
 */
@Data
public class FormControl4Model {
    //控件
    private String control;
    //标题
    private String title;
    //字段
    private String field;
    //类的名称（添加的字段）
    private String className;
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
    //编辑表格 - 表名
    private String table;
    //编辑表格 - 字段列表  FormControlGridField
    private List<FormControlGridField4Model> gridFieldControls;
}
