package smart.permission.model.position;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import smart.util.treeutil.SumTree;
import lombok.Data;


@Data
public class PosOrgModel extends SumTree {
   private String  fullName;
   @ApiModelProperty(value = "状态")
   private Integer enabledMark;
   @JSONField(name="category")
   private String  type;
   @ApiModelProperty(value = "图标")
    private String icon;
}
