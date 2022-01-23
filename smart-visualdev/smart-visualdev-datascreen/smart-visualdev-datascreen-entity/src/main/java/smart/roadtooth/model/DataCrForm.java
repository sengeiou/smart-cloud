package smart.roadtooth.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class DataCrForm {
    private Integer enabledMark;
    private String password;
    @NotBlank(message = "必填")
    private String categoryId;
    @NotBlank(message = "必填")
    private String fullName;
    private String detail;
    private String component;
    private String screenShot;
}
