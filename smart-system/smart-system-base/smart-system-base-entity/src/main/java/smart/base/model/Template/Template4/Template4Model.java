package smart.base.model.Template.Template4;

import lombok.Data;

import java.util.List;

/**
 * 多表开发配置
 */
@Data
public class Template4Model {
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
    //表单单据 - 单据规则
    private String formBillNumber;
    // 数据关联 - 集合
    private List<DbTableRelation4Model> dbTableRelation;
    // 表单控件 - 集合
    private List<FormControl4Model> formControls;
}
