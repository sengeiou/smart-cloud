package smart.base.model.moduledataauthorizescheme;

import lombok.Data;

@Data
public class DataAuthorizeSchemeInfoVO {

    private String id;

    private String fullName;

    private String conditionText;

    private String conditionJson;

    private String moduleId;
}
