package smart.permission.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import smart.util.treeutil.SumTree;
import lombok.Data;

@Data
public class UserSelectorModel extends SumTree {
    @JSONField(name="category")
    private String type;
    private String fullName;
    @ApiModelProperty(value = "状态")
    private Integer enabledMark;
    @ApiModelProperty(value = "图标")
    private String icon;
}
