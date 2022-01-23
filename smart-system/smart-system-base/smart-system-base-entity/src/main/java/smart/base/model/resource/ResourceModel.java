package smart.base.model.resource;

import lombok.Data;

/**
 * 资源
 */
@Data
public class ResourceModel {
    private String id;
    private String fullName;
    private String enCode;
    private String conditionJson;
    private String conditionText;
    private String moduleId;
}
