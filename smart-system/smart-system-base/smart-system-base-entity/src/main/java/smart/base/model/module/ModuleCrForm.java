package smart.base.model.module;

import lombok.Data;

@Data
public class ModuleCrForm {
    private String parentId;
    private String fullName;
    private Integer isButtonAuthorize;
    private Integer isColumnAuthorize;
    private Integer isDataAuthorize;
    private String enCode;
    private String icon;
    private Integer type;
    private String urlAddress;
    private String linkTarget;
    private String category;
    private String description;
    private Integer enabledMark;
    private long sortCode;
    private Object propertyJson;
}
