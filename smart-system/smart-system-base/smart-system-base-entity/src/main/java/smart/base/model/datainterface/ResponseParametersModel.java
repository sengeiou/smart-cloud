package smart.base.model.datainterface;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 返回参数
 */
@Data
public class ResponseParametersModel {
    @ApiModelProperty(value = "参数名称")
    private String parameter;
    @ApiModelProperty(value = "绑定字段")
    private String field;
    @ApiModelProperty(value = "参数类型")
    private String type;
    @ApiModelProperty(value = "示例值")
    private String sample;
    @ApiModelProperty(value = "描述")
    private String remark;
    @ApiModelProperty(value = "是否分页(1-分页 ，0-不分页)")
    private String pagination;
}
