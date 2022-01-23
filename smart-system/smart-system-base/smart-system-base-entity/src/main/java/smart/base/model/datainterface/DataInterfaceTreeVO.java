package smart.base.model.datainterface;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DataInterfaceTreeVO {
    @ApiModelProperty(value = "主键Id")
    private String categoryId;
    @ApiModelProperty(value = "接口名称")
    private String fullName;
    private String id;
    private Boolean hasChildren;
    private List<DataInterfaceTreeModel> children;
}
