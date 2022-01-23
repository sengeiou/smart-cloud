package smart.model.mptag;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class MPTagForm {
    @NotBlank(message = "必填")
    private String fullName;
}
