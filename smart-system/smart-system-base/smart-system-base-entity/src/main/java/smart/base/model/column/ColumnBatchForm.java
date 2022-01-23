package smart.base.model.column;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;


@Data
public class ColumnBatchForm {
    @NotBlank(message = "必填")
    private String moduleId;
    @NotBlank(message = "必填")
    private String bindTable;
    @NotBlank(message = "必填")
    private String bindTableName;
    private Object columnJson;
}
