package smart.permission.model.organize;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OraganizeDepartInfoVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "父主键")
    private String parentId;
    @ApiModelProperty(value = "名称")
    private String fullName;
    @ApiModelProperty(value = "编码")
    private String enCode;
    @ApiModelProperty(value = "状态")
    private int enabledMark;
    @ApiModelProperty(value = "备注")
    private String description;
    @ApiModelProperty(value = "主管id")
    @JSONField(name = "manager")
    private String managerId;
    @ApiModelProperty(value = "排序码")
    private long sortCode;
}
