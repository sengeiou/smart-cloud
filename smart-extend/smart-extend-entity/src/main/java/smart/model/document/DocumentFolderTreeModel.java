package smart.model.document;

import smart.util.treeutil.SumTree;
import lombok.Data;

@Data
public class DocumentFolderTreeModel extends SumTree {
    private String icon;
    private String fullName;
}
