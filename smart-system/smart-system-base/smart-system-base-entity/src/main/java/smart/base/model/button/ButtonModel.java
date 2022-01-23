package smart.base.model.button;

import lombok.Data;

/**
 * 按钮
 */
@Data
public class ButtonModel {
    private String id;
    private String parentId;
    private String fullName;
    private String enCode;
    private String icon;
    private String urlAddress;
    private String moduleId;
}
