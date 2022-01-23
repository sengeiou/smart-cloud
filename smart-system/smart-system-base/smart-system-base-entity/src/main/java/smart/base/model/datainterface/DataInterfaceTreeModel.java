package smart.base.model.datainterface;

import smart.util.treeutil.SumTree;
import lombok.Data;

@Data
public class DataInterfaceTreeModel extends SumTree {
//    private String id;
//    private String parentId;
    private String fullName;
    private String categoryId;
}
