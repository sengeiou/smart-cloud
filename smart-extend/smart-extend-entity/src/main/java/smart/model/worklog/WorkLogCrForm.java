package smart.model.worklog;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;


@Data
public class WorkLogCrForm {
    @NotBlank(message = "必填")
    private String title;
    @NotBlank(message = "必填")
    private String question;
    @NotBlank(message = "必填")
    private String todayContent;
    @NotBlank(message = "必填")
    private String tomorrowContent;
    @NotBlank(message = "必填")
    private String toUserId;
}
