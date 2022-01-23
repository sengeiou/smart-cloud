package smart.base.model.Template.Template5;

import lombok.Data;

import java.util.List;

/**
 * 多表开发配置
 */
@Data
public class Template5Model {
    //版本
    private String version = "V3.0.0";
    //版权
    private String copyright;
    //创建人员
    private String createUser;
    //创建日期
    private String createDate;
    //功能描述
    private String description;
    //所在区域
    private String areasName;
    //功能名称
    private String[] className;
    //后端目录
    private String serviceDirectory;
    //前端目录
    private String webDirectory;
    //表单标题
    private String formTitle;
    //弹窗类型
    private String formDialog;
    //表单宽度
    private int formWidth;
    //表单高度
    private int formHeight;
    //表单Tabs
    private String[] formTabs;
    //列表 - 标题
    private String indexListTitle;
    //列表 - 分页
    private int indexGridIsPage;
    // 列表主表 - 字段集合
    private List<IndexGridField5Model> indexGridField;
    // 列表子表 - 子表集合
    private List<IndexGridEntry5Model> indexGridEntry;
    // 数据关联 - 集合
    private List<DbTableRelation5Model> dbTableRelation;
    // 表单控件 - 集合
    private List<FormControl5Model> formControls;
}
