package smart.model.qydepart;

import smart.util.treeutil.SumTree;
import lombok.Data;

import java.util.Date;


@Data
public class QYDepartTreeModel extends SumTree {
    private String fullName;
    private String enCode;
    private String category;
    private String syncState;
    private String submitState;
    private String description;
    private Date lastModifyTime;
}
