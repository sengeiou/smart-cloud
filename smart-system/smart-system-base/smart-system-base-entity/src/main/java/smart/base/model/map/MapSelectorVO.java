package smart.base.model.map;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MapSelectorVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "地图名称")
    private String fullName;
}
