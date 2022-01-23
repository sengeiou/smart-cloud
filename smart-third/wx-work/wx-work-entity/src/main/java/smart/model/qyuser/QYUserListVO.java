package smart.model.qyuser;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QYUserListVO {
    @ApiModelProperty(value = "自然主键")
    private String id;
    @ApiModelProperty(value = "账户")
    private String account;
    @ApiModelProperty(value = "呢称")
    private String nickName;
    @ApiModelProperty(value = "性别")
    private Integer gender;
    @ApiModelProperty(value = "手机")
    private String mobilePhone;
    @ApiModelProperty(value = "部门")
    private String department;
    @ApiModelProperty(value = "岗位")
    private String position;
    @ApiModelProperty(value = "同步状态(1-已同步,0-未同步)")
    private Integer syncState;
    @ApiModelProperty(value = "备注")
    private String description;
    @ApiModelProperty(value = "状态")
    private Integer enabledMark;
}
