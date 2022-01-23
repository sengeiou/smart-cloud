package smart.roadtooth.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataSelectorVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "名称")
    private String fullName;
    @ApiModelProperty(value = "是否有下级")
    private boolean hasChildren;
    @ApiModelProperty(value = "下级")
    private List<DataSelectorVO> children = new ArrayList<>();
}
