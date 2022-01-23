package smart.base.model.module;

import lombok.Data;

/**
 * 功能
 */
@Data
public class ModuleModel {
    private String id;
    private String parentId;
    private String fullName;
    private String icon;
    //1-类别、2-页面
    private int type;
    private String urlAddress;
    private String linkTarget;
    private String category;
    private String description;
    private Long sortCode=999999L;
    private String enCode;
    private String propertyJson;
}
