package smart.permission.model.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserInfoVO {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "账户")
    private String account;
    @ApiModelProperty(value = "户名")
    private String realName;
    @ApiModelProperty(value = "部门id")
    private String organizeId;
    @ApiModelProperty(value = "主管id")
    private String managerId;
    @ApiModelProperty(value = "岗位id")
    private String positionId;
    @ApiModelProperty(value = "角色id")
    private String roleId;
    @ApiModelProperty(value = "备注")
    private String description;
    @ApiModelProperty(value = "性别")
    private String gender;
    @ApiModelProperty(value = "民族")
    private String nation;
    @ApiModelProperty(value = "籍贯")
    private String nativePlace;
    @ApiModelProperty(value = "入职时间")
    private long entryDate;
    @ApiModelProperty(value = "证件类型")
    private String certificatesType;
    @ApiModelProperty(value = "证件号码")
    private String certificatesNumber;
    @ApiModelProperty(value = "学历")
    private String education;
    @ApiModelProperty(value = "出生年月")
    private long birthday;
    @ApiModelProperty(value = "办公电话")
    private String telePhone;
    @ApiModelProperty(value = "办公座机")
    private String landline;
    @ApiModelProperty(value = "手机号码")
    private String mobilePhone;
    @ApiModelProperty(value = "电子邮箱")
    private String email;
    @ApiModelProperty(value = "紧急联系人")
    private String urgentContacts;
    @ApiModelProperty(value = "紧急联系人电话")
    private String urgentTelePhone;
    @ApiModelProperty(value = "通信地址")
    private String postalAddress;
    @ApiModelProperty(value = "用户头像")
    private String headIcon;
    @ApiModelProperty(value = "排序")
    private Long sortCode;
    @ApiModelProperty(value = "状态")
    private Integer enabledMark;
}
