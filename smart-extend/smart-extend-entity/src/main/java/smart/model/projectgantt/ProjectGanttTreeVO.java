package smart.model.projectgantt;

import lombok.Data;

import java.util.List;

@Data
public class ProjectGanttTreeVO {
    private String id;
    private String parentId;
    private String fullName;
    private String startTime;
    private String endTime;
    private String sign;
    private String signColor;
    private String schedule;
    private Boolean hasChildren;
    private List<ProjectGanttTreeVO> children;
}
