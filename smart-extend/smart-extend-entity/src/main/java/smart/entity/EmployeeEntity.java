package smart.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 职员信息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 *
 */
@Data
@TableName("ext_employee")
public class EmployeeEntity {
    /**
     * 职员主键
     */
    @TableId("F_ID")
    private String id;

    /**
     * 工号
     */
    @TableField("F_ENCODE")
    private String enCode;

    /**
     * 姓名
     */
    @TableField("F_FULLNAME")
    private String fullName;

    /**
     * 性别
     */
    @TableField("F_GENDER")
    private String gender;

    /**
     * 部门
     */
    @TableField("F_DEPARTMENTNAME")
    private String departmentName;

    /**
     * 岗位
     */
    @TableField("F_POSITIONNAME")
    private String positionName;

    /**
     * 用工性质
     */
    @TableField("F_WORKINGNATURE")
    private String workingNature;

    /**
     * 身份证号
     */
    @TableField("F_IDNUMBER")
    private String idNumber;

    /**
     * 联系电话
     */
    @TableField("F_TELEPHONE")
    private String telephone;

    /**
     * 参加工作
     */
    @TableField("F_ATTENDWORKTIME")
    private Date attendWorkTime;

    /**
     * 出生年月
     */
    @TableField("F_BIRTHDAY")
    private Date birthday;

    /**
     * 最高学历
     */
    @TableField("F_EDUCATION")
    private String education;

    /**
     * 所学专业
     */
    @TableField("F_MAJOR")
    private String major;

    /**
     * 毕业院校
     */
    @TableField("F_GRADUATIONACADEMY")
    private String graduationAcademy;

    /**
     * 毕业时间
     */
    @TableField("F_GRADUATIONTIME")
    private Date graduationTime;

    /**
     * 描述
     */
    @TableField("F_DESCRIPTION")
    private String description;

    /**
     * 排序码
     */
    @TableField("F_SORTCODE")
    private Long sortCode;

    /**
     * 有效标志
     */
    @TableField("F_ENABLEDMARK")
    private Integer enabledMark;

    /**
     * 创建时间
     */
    @TableField(value = "F_CREATORTIME",fill = FieldFill.INSERT)
    private Date creatorTime;

    /**
     * 创建用户
     */
    @TableField(value = "F_CREATORUSERID",fill = FieldFill.INSERT)
    private String creatorUserId;

    /**
     * 修改时间
     */
    @TableField(value = "F_LASTMODIFYTIME",fill = FieldFill.UPDATE)
    private Date lastModifyTime;

    /**
     * 修改用户
     */
    @TableField(value = "F_LASTMODIFYUSERID",fill = FieldFill.UPDATE)
    private String lastModifyUserId;

    /**
     * 删除标志
     */
    @TableField("F_DELETEMARK")
    private Integer deleteMark;

    /**
     * 删除时间
     */
    @TableField("F_DELETETIME")
    private Date deleteTime;

    /**
     * 删除用户
     */
    @TableField("F_DELETEUSERID")
    private String deleteUserId;
}
