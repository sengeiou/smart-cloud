package smart.model.mptag;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class MPTagSetForm {
    @NotBlank(message = "必填")
    private String tagId;
    @NotBlank(message = "必填")
    private String openId;
}
