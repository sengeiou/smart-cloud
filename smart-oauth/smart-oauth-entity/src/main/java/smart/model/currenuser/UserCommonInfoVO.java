package smart.model.currenuser;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserCommonInfoVO {
    @ApiModelProperty(value = "用户id")
    private String userId;
    @ApiModelProperty(value = "用户账号")
    private String userAccount;
    @ApiModelProperty(value = "用户姓名")
    private String userName;
    @ApiModelProperty(value = "用户头像")
    private String headIcon;
    @ApiModelProperty(value = "部门主键")
    private String departmentId;
    @ApiModelProperty(value = "部门名称")
    private String departmentName;
    @ApiModelProperty(value = "组织主键")
    private String organizeId;
    @ApiModelProperty(value = "组织名称")
    private String organizeName;
    @ApiModelProperty(value = "岗位")
    private List<UserPositionVO> positionIds;
    @ApiModelProperty(value = "上次登录")
    private Integer prevLogin;
    @ApiModelProperty(value = "上次登录时间",example = "1")
    private Long prevLoginTime;
    @ApiModelProperty(value = "上次登录IP")
    private String prevLoginIPAddress;
    @ApiModelProperty(value = "上次登录地址")
    private String prevLoginIPAddressName;
    @ApiModelProperty(value = "门户id")
    private String portalId;

}
