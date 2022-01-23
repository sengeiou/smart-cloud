package smart.base.model.button;

import lombok.Data;

@Data
public class ModuleButtonInfoVO {
    private String enCode;
    private Integer enabledMark;
    private String fullName;
    private String icon;
    private String id;
    private String parentId;
    private String description;
    private Long sortCode;
}
