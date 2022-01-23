package smart.base.model.button;

import lombok.Data;

@Data
public class ButtonListVO {
    private Long sortCode;
    private String  id;
    private String parentId;
    private String fullName;
    private String enCode;
    private String icon;
    private Integer enabledMark;
    private String description;
}
