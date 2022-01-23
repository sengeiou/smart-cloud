package smart.model.mpmenu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MPMenuListVo {
    @ApiModelProperty(value = "菜单主键")
    private String id;
    @ApiModelProperty(value = "菜单名称")
    private String fullName;
    @ApiModelProperty(value = "菜单类型")
    private String type;
    @ApiModelProperty(value = "页面地址")
    private String url;
    @ApiModelProperty(value = "文字内容")
    private String content;
    @ApiModelProperty(value = "排序码")
    private String sortCode;
    @ApiModelProperty(value = "下级菜单列表")
    private List<MPMenuListVo> children = new ArrayList<>();
    @ApiModelProperty(value = "是否有下级菜单")
    private boolean hasChildren = true;
}
