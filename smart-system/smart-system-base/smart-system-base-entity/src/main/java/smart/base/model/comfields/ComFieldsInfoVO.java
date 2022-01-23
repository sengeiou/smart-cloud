package smart.base.model.comfields;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class ComFieldsInfoVO {
    private String id;
    private String fieldName;
    private String dataType;
    @NotBlank(message = "必填")
    private String field;
    private String dataLength;
    private Integer allowNull;
    private long creatorTime;
}
