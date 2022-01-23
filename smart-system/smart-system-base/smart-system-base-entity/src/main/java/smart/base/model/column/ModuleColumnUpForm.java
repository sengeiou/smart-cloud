package smart.base.model.column;

import lombok.Data;

@Data
public class ModuleColumnUpForm {
    private String creatorUserId;

    private Integer enabledMark;

    private String fullName;

    private String description;

    private Long sortCode;

    private String enCode;

//    private String lastModifyTime;
//
//    private String lastModifyUserId;


    private String creatorTime;

    private String moduleId;

    private String bindTable;

    private String bindTableName;
}
