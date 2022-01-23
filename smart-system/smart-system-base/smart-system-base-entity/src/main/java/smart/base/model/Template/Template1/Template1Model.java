package smart.base.model.Template.Template1;

import smart.base.model.Template.EntityFieldModel;
import lombok.Data;

import java.util.List;

/**
 * 通用开发配置
 */
@Data
public class Template1Model {
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
    //模块
    private String areasName;
    //功能名称
    private String className;
    //后端目录
    private String serviceDirectory;
    //数据表名称
    private String table;
    //数据表主键
    private String tableKey;
    //数据表字段
    private List<EntityFieldModel> tableFieldList;
    //分页
    private Boolean isListPageMethod;
    //列表
    private Boolean isListMethod;
    //信息
    private Boolean isInfoMethod;
    //创建
    private Boolean isCreateMethod;
    //修改
    private Boolean isUpdateMethod;
    //删除
    private Boolean isDeleteMethod;
    //总数
    private Boolean isCountMethod;
    //上移
    private Boolean isFirstMethod;
    //下移
    private Boolean isNextMethod;
}
