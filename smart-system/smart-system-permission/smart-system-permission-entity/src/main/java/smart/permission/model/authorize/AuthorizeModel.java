package smart.permission.model.authorize;

import smart.util.treeutil.SumTree;
import lombok.Data;


@Data
public class AuthorizeModel extends SumTree {
    private String id;
    private String fullName;
    private String icon;
}
