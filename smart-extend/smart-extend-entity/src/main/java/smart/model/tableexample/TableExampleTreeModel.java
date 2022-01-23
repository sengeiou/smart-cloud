package smart.model.tableexample;

import smart.util.treeutil.SumTree;
import lombok.Data;

import java.util.Map;

@Data
public class TableExampleTreeModel extends SumTree {
    private Boolean loaded;
    private Boolean expanded;
    private Map<String, Object> ht;
    private String text;
}
