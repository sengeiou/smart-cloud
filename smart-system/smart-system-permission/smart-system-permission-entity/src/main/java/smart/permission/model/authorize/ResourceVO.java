package smart.permission.model.authorize;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 资源
 */
@Data
public class ResourceVO {
    @ApiModelProperty(value = "资源主键")
    private String id;
    @ApiModelProperty(value = "资源名称")
    private String fullName;
    @ApiModelProperty(value = "资源编码")
    private String enCode;
    @ApiModelProperty(value = "条件规则")
    private String conditionJson;
    @ApiModelProperty(value = "规则描述")
    private String conditionText;
    @ApiModelProperty(value = "功能主键")
    private String moduleId;
}
