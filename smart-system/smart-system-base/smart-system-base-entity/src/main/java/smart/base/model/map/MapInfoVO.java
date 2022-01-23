package smart.base.model.map;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MapInfoVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "地图名称")
    private String fullName;
    @ApiModelProperty(value = "地图编码")
    private String enCode;
    @ApiModelProperty(value = "地图数据")
    private String data;
    @ApiModelProperty(value = "排序")
    private long sortCode;
    @ApiModelProperty(value = "状态")
    private Integer enabledMark;
}
