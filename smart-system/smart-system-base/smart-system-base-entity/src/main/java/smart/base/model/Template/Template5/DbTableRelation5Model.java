package smart.base.model.Template.Template5;

import lombok.Data;

/**
 * 数据关联
 */
@Data
public class DbTableRelation5Model {
    //类型：1-主表、0-子表
    private int typeId;
    //表名
    private String table;
    //类的名称（添加的字段）
    private String className;
    //说明
    private String tableName;
    //主键
    private String tableKey;
    //外键字段
    private String tableField;
    //关联主表
    private String relationTable;
    //关联主键
    private String relationField;
}
