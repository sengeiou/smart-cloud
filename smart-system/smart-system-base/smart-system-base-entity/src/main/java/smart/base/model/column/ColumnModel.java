package smart.base.model.column;

import lombok.Data;

/**
 * 列表
 */
@Data
public class ColumnModel {
    private String id;
    private String parentId;
    private String fullName;
    private String enCode;
    private String bindTable;
    private String bindTableName;
    private String moduleId;
}
