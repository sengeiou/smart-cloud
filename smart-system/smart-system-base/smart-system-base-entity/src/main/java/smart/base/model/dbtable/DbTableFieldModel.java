package smart.base.model.dbtable;

import lombok.Data;

@Data
public class DbTableFieldModel {
    //字段名
    private String field;
    // 字段说明
    private String fieldName;
    // 数据类型
    private String dataType;
    // 数据长度
    private String dataLength;
    // 自增
    private String identity;
    // 主键
    private Integer primaryKey;
    // 允许null值
    private Integer allowNull;
    // 默认值
    private String defaults;
    // 说明
    private String description;
}
