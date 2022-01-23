package smart.base.model.province;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 新建
 *
 */

@Data
public class ProvinceCrForm {
    @NotBlank(message = "必填")
    private String enCode;

    private Integer enabledMark;

    @NotBlank(message = "必填")
    private String fullName;

    private String description;

    @NotBlank(message = "必填")
    private String parentId;
    private long sortCode;
}
