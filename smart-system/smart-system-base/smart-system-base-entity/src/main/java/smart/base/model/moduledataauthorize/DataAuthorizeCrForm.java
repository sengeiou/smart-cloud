package smart.base.model.moduledataauthorize;

import lombok.Data;

@Data
public class DataAuthorizeCrForm {
    private String fullName;

    private String enCode;

    private String type;

    private String conditionSymbol;

    private String conditionText;

    private String description;

    private String moduleId;
}
