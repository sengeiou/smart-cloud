package smart.model.projectgantt;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProjectGanttListVO {
    private String id;
    private String enCode;
    private String fullName;
    private BigDecimal timeLimit;
    private long startTime;
    private long endTime;
    private Integer schedule;
    private String managerIds;
    private Integer state;
    private List<ProjectGanttManagerIModel> managersInfo;
}
