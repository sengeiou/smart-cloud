package smart.base.model.dictionarydata;

import smart.util.treeutil.SumTree;
import lombok.Data;

@Data
public class DictionaryDataAllModel extends SumTree {
    private String  fullName;
    private String  enCode;
}
