package smart.model.projectgantt;

import lombok.Data;

import java.util.List;

@Data
public class ProjectGanttTaskTreeVO {
    private String id;
    private String parentId;
    private Boolean hasChildren;
    private String fullName;
    private Integer schedule;
    private String projectId;
    private long startTime;
    private long endTime;
    private String signColor;
    private String sign;
    private List<ProjectGanttTaskTreeVO> children;
}
