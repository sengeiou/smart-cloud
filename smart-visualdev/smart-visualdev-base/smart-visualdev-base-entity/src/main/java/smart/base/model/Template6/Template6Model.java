package smart.base.model.Template6;


import smart.base.model.TableModel;
import lombok.Data;

import java.util.List;

/**
 * 多表开发配置
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @author 开发平台组
 * @date 2021/3/16
 */
@Data
public class Template6Model {
    /**
     * 版本
     */
    private String version = "V3.0.0";
    /**
     * 版权
     */
    private String copyright;
    /**
     * 创建人员
     */
    private String createUser;
    /**
     * 创建日期
     */
    private String createDate;
    /**
     * 功能描述
     */
    private String description;
    /**
     * 子类功能名称
     */
    private String subClassName;
    /**
     * 主类功能名称
     */
    private String className;

    /**
     * tables
     */
    /**
     *  列表主表 - 字段集合
     */
    private List<ColumnListField> columnListFields;


    private String serviceDirectory;


    /**
     *  数据关联 - 集合
     */
    private List<TableModel> dbTableRelation;
}
