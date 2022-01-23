package smart.permission.model.organize;

import com.alibaba.fastjson.annotation.JSONField;
import smart.util.treeutil.SumTree;
import lombok.Data;


@Data
public class OraganizeModel extends SumTree {
    private String fullName;
    private String enCode;
    private Long creatorTime;
    private String manager;
    private String description;
    private int enabledMark;
    private String icon;
    @JSONField(name="category")
    private String  type;
    private long sortCode;
}
