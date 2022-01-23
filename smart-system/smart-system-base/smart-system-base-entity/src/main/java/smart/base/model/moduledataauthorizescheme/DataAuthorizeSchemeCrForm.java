package smart.base.model.moduledataauthorizescheme;

import lombok.Data;

@Data
public class DataAuthorizeSchemeCrForm {
    private String fullName;

    private Object conditionJson;

    private String conditionText;

    private String moduleId;
}
