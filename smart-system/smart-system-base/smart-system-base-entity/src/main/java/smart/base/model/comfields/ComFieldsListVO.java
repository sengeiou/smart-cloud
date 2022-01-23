package smart.base.model.comfields;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class ComFieldsListVO {
   private String id;
   private String fieldName;
    private String dataType;
    private String dataLength;
    private Integer allowNull;
    @NotBlank(message = "必填")
    private String field;
    private long creatorTime;
}
