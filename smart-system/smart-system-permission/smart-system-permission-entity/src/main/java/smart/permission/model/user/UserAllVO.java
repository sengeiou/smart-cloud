package smart.permission.model.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserAllVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "账号")
    private String account;
    @ApiModelProperty(value = "名称")
    private String realName;
    @ApiModelProperty(value = "用户头像")
    private String headIcon;
    @ApiModelProperty(value = "性别")//1,男。2女
    private String gender;
    @ApiModelProperty(value = "部门")
    private String department;
    @ApiModelProperty(value = "快速搜索")
    private String quickQuery;
}
