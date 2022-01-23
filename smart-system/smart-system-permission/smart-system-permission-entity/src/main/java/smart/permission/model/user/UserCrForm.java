package smart.permission.model.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class UserCrForm {
    @NotBlank(message = "必填")
    @ApiModelProperty("账户")
    private String account;
    @NotBlank(message = "必填")
    @ApiModelProperty("户名")
    private String realName;
    @NotBlank(message = "必填")
    @ApiModelProperty("部门")
    private String organizeId;
    @ApiModelProperty("主管")
    private String managerId;
    @NotBlank(message = "必填")
    @ApiModelProperty("岗位")
    private String positionId;
    @NotBlank(message = "必填")
    @ApiModelProperty("角色")
    private String roleId;
    private String description;
    @NotNull(message = "性别不能为空")
    @ApiModelProperty("性别")
    private int gender;
    private String nation;
    private String nativePlace;
    private String certificatesType;
    private String certificatesNumber;
    private String education;
    private String birthday;
    private String telePhone;
    private String landline;
    private String mobilePhone;
    private String email;
    private String urgentContacts;
    private String urgentTelePhone;
    private String postalAddress;
    private String headIcon;
    @ApiModelProperty(value = "排序")
    private Long sortCode;
    private long entryDate;
    @ApiModelProperty(value = "状态")
    private Integer enabledMark;
}
