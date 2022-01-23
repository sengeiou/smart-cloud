package smart.portal.model;

import com.alibaba.fastjson.annotation.JSONField;
import smart.util.treeutil.SumTree;
import lombok.Data;


@Data
public class PortalSelectModel extends SumTree {
    private String fullName;
    @JSONField(name="category")
    private String  parentId;
}
