package smart.permission.model.authorize;

import smart.util.treeutil.SumTree;
import lombok.Data;

@Data
public class AuthorizeDataModel extends SumTree {
    private  String id;
    private String fullName;
    private String icon;
    private Boolean showcheck;
    private Integer checkstate;
    private String title;
    private String moduleId;
    private String type;
    private Long sortCode=9999L;
}
