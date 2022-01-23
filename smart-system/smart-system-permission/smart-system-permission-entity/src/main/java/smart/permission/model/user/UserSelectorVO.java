package smart.permission.model.user;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserSelectorVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "父主键")
    private String parentId;
    @ApiModelProperty(value = "名称")
    private String fullName;
    @ApiModelProperty(value = "是否有子节点")
    private Boolean hasChildren;
    @ApiModelProperty(value = "状态")
    private Integer enabledMark;
    @ApiModelProperty(value = "子节点")
    private List<UserSelectorVO> children = new ArrayList<>();
    @JSONField(name="category")
    private String type;
    @ApiModelProperty(value = "图标")
    private String icon;
}
