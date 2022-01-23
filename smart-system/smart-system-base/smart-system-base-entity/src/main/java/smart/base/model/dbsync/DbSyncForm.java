package smart.base.model.dbsync;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class DbSyncForm {
    @NotBlank(message = "必填")
    private String dbConnectionFrom;
    @NotBlank(message = "必填")
    private String dbConnectionTo;
    @NotBlank(message = "必填")
    private String dbTable;
}
