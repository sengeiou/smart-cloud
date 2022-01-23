package smart.permission.model.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserBaseInfoVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "账户")
    private String account;
    @ApiModelProperty(value = "户名")
    private String realName;
    @ApiModelProperty(value = "部门")
    private String organize;
    @ApiModelProperty(value = "公司名称")
    private String company;
    @ApiModelProperty(value = "岗位")
    private String position;
    @ApiModelProperty(value = "主管")
    private String manager;
    @ApiModelProperty(value = "角色")
    private String roleId;
    @ApiModelProperty(value = "注册时间")
    private long creatorTime;
    @ApiModelProperty(value = "上次登录时间")
    private long prevLogTime;
    @ApiModelProperty(value = "自我介绍")
    private String signature;
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
    private String birthday;
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
    private String PostalAddress;
    @ApiModelProperty(value = "用户头像")
    private String avatar;
    @ApiModelProperty(value = "主题")
    private String theme;
    @ApiModelProperty(value = "语言")
    private String language;
}
