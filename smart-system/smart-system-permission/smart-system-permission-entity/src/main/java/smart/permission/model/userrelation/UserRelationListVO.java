package smart.permission.model.userrelation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserRelationListVO {
    @ApiModelProperty(value = "id")
    private String id="";
    @ApiModelProperty(value = "成员id")
    private String userId="";
    @ApiModelProperty(value = "用户id")
    private String account="";
    @ApiModelProperty(value = "用户真实姓名")
    private String realName="";
    @ApiModelProperty(value = "性别")
    private String gender="";
    @ApiModelProperty(value = "所属公司")
    private String organize="";
    @ApiModelProperty(value = "所属部门")
    private String department="";
    @ApiModelProperty(value = "添加时间(时间戳)")
    private Long creatorTime;
}
