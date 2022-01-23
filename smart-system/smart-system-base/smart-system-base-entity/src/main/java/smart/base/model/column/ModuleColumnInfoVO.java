package smart.base.model.column;

import lombok.Data;

@Data
public class ModuleColumnInfoVO {
    private Integer enabledMark;

    private String fullName;

    private String description;

    private String enCode;

    private String id;

    private String bindTable;

    private String bindTableName;
}
