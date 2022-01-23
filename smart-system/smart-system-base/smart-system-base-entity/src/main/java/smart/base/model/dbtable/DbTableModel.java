package smart.base.model.dbtable;

import lombok.Data;

@Data
public class DbTableModel {
    // 标识
    private String id;
    // 表名
    private String table;
    private String newTable;
    // 表说明
    private String tableName;
    // 大小
    private String size;
    // 总数
    private Integer sum;
    // 说明
    private String description;
    // 主键
    private String primaryKey;
    // 数据源主键
    private String dataSourceId;
}
