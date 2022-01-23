package smart.base.model.dictionarydata;

import smart.util.treeutil.SumTree;
import lombok.Data;

@Data
public class DictionaryDataModel extends SumTree {
    private String  id;
    private String parentId;
    private String  fullName;
    private String  enCode;
    private Integer  enabledMark;
    private String icon;
    private long sortCode;
}
