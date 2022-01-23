package smart.base.model.dictionarytype;

import smart.util.treeutil.SumTree;
import lombok.Data;

@Data
public class DictionaryTypeSelectModel extends SumTree {
    private String id;
    private String parentId;
    private String fullName;
    private String enCode;
}
