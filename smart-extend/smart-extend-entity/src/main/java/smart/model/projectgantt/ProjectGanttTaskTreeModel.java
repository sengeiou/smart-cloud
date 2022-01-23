package smart.model.projectgantt;

import smart.util.treeutil.SumTree;
import lombok.Data;

@Data
public class ProjectGanttTaskTreeModel extends SumTree {
    private String fullName;
}
