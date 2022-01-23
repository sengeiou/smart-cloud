package smart.base.model.dictionarytype;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class DictionaryTypeCrForm {
    @NotBlank(message = "必填")
   private String parentId;
    @NotBlank(message = "必填")
    private String fullName;
    @NotBlank(message = "必填")
    private String enCode;
    @NotNull(message = "必填")
    private Integer isTree;
    private String description;
    private long sortCode;
}
