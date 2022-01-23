package smart.base.model.Template.Template3;

import lombok.Data;

/**
 * 数据关联
 */
@Data
public class DbTableRelation3Model {
    //类型：1-主表、0-子表
    private int typeId;
    //表名
    private String table;
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
