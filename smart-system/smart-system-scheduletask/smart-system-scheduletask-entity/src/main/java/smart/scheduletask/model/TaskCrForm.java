package smart.scheduletask.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class TaskCrForm {
    @NotBlank(message = "必填")
    private String fullName;
    @NotBlank(message = "必填")
    private String executeType;
    private String description;
    @NotBlank(message = "必填")
    private String executeContent;
    private long sortCode;
    private String enCode;
}
