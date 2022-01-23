package smart.model.employee;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 *
 */
@Data
public class EmployeeInfoModel {
    @ApiModelProperty(value = "出生年月")
    private Date birthday;
    @ApiModelProperty(value = "参加工作")
    private String attendWorkTime;
    @ApiModelProperty(value = "创建时间")
    private Date creatorTime;
    @ApiModelProperty(value = "创建用户")
    private String creatorUserId;
    @ApiModelProperty(value = "删除标志")
    private String deleteMark;
    @ApiModelProperty(value = "删除时间")
    private Date deleteTime;
    @ApiModelProperty(value = "删除用户")
    private String deleteUserId;
    @ApiModelProperty(value = "部门")
    private String departmentName;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "最高学历")
    private String education;
    @ApiModelProperty(value = "工号")
    private String enCode;
    @ApiModelProperty(value = "有效标志")
    private Integer enabledMark;
    @ApiModelProperty(value = "姓名")
    private String fullName;
    @ApiModelProperty(value = "性别")
    private String gender;
    @ApiModelProperty(value = "毕业院校")
    private String graduationAcademy  ;
    @ApiModelProperty(value = "毕业时间")
    private Date graduationTime;
    @ApiModelProperty(value = "自然主键")
    private String id;
    @ApiModelProperty(value = "身份证号")
    private String idNumber;
    @ApiModelProperty(value = "修改时间")
    private Date lastModifyTime;
    @ApiModelProperty(value = "修改用户")
    private String lastModifyUserId;
    @ApiModelProperty(value = "所学专业")
    private String major;
    @ApiModelProperty(value = "岗位")
    private String positionName;
    @ApiModelProperty(value = "排序")
    private String sortCode;
    @ApiModelProperty(value = "联系电话")
    private String telephone;
    @ApiModelProperty(value = "用工性质")
    private String workingNature;
}
