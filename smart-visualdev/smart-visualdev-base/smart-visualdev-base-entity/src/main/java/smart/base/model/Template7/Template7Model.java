package smart.base.model.Template7;

import smart.base.model.TableModel;
import lombok.Data;

import java.util.List;

/**
 * 多表开发配置
 */
@Data
public class Template7Model {
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
    //子类功能名称
    private String subClassName;
    //主类功能名称
    private String className;



    //表单页名
    private String formPageName;
    //列表页名
    private String indexPageName;
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
    //列表左边树 - 是否显示
    private int treeIsShow;
    //列表左边树 - 树形标题
    private String treeTitle;
    //列表左边树 - 数据来源
    private String treeDataSource;
    //列表左边树 - 数据字典
    private String treeDictionary;
    //列表左边树 - 数据选择
    private String treeDbTable;
    //列表左边树 - 主键字段
    private String treeDbTableField;
    //列表左边树 - 父级字段
    private String treeDbTableFieldParentId;
    //列表左边树 - 显示字段
    private String treeDbTableFieldShow;
    //列表左边树 - 关联字段
    private String treeDbTableFieldRelation;
    //按钮 - 新建
    private String indexBtnAddName;
    //按钮 - 编辑
    private String indexBtnEditName;
    //按钮 - 删除
    private String indexBtnRemoveName;
//    //按钮 - 上移
//    private String indexBtnFirstName;
//    //按钮 - 下移
//    private String indexBtnNextName;
    //列表 - 标题
    private String indexListTitle;
    //列表 - 分页
    private int indexGridIsPage;

    // 数据关联 - 集合
    private List<TableModel> dbTableRelation;
}
