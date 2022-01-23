package smart.permission.model.userrelation;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import smart.util.treeutil.SumTree;
import lombok.Data;

@Data
public class UserRelationTreeModel extends SumTree {

    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "名称")
    private String fullName;
    @ApiModelProperty(value = "是否有子节点")
    private Boolean hasChildren;
//    @ApiModelProperty(value = "子节点")
//    private List<UserRelationTreeModel> children = new ArrayList<>();
    @JSONField(name="category")
    private String type;
}
