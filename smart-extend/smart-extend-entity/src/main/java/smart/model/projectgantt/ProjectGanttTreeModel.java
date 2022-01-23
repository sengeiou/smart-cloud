package smart.model.projectgantt;

import smart.util.treeutil.SumTree;
import lombok.Data;

@Data
public class ProjectGanttTreeModel extends SumTree {

    private Integer schedule;

    private String fullName;

    private long startTime;

    private long endTime;

    private String signColor;
    private String sign;
}
