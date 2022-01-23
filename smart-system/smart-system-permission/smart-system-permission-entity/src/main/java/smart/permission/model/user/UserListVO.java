package smart.permission.model.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserListVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "账号")
    private String account;
    @ApiModelProperty(value = "姓名")
    private String realName;
    @ApiModelProperty(value = "性别")
    private String gender;
    @ApiModelProperty(value = "手机")
    private String mobilePhone;
    @ApiModelProperty(value = "岗位")
    private String position;
    @ApiModelProperty(value = "部门")
    private String department;
    @ApiModelProperty(value = "角色")
    private String roleName;
    @ApiModelProperty(value = "说明")
    private String description;
    @ApiModelProperty(value = "状态")
    private int enabledMark;
    @ApiModelProperty(value = "添加时间",example = "1")
    private long creatorTime;
    @ApiModelProperty(value = "排序")
    private Long sortCode;
    @ApiModelProperty(value = "是否管理员")
    private Integer isAdministrator;
}
