package smart.base.model.button;


import smart.util.treeutil.SumTree;
import lombok.Data;

@Data
public class ButtonTreeListModel extends SumTree {
    private String  id;
    private String parentId;
    private String fullName;
    private String enCode;
    private String icon;
    private Integer enabledMark;
    private String description;
    private Long sortCode;
}
