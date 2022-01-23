package smart.model.employee;

import lombok.Data;

/**
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 *
 */
@Data
public class EmployeeExportVO {
    /**
     * 职员主键
     */
    private String id;

    /**
     * 工号
     */
    private String enCode;

    /**
     * 姓名
     */
    private String fullName;

    /**
     * 性别
     */
    private String gender;

    /**
     * 部门
     */
    private String departmentName;

    /**
     * 岗位
     */
    private String positionName;

    /**
     * 用工性质
     */
    private String workingNature;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 联系电话
     */
    private String telephone;

    /**
     * 参加工作
     */
    private String attendWorkTime;

    /**
     * 出生年月
     */
    private String birthday;

    /**
     * 最高学历
     */
    private String education;

    /**
     * 所学专业
     */
    private String major;

    /**
     * 毕业院校
     */
    private String graduationAcademy;

    /**
     * 毕业时间
     */
    private String graduationTime;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序码
     */
    private Long sortCode;

    /**
     * 有效标志
     */
    private Integer enabledMark;

    /**
     * 创建时间
     */
    private String creatorTime;

    /**
     * 创建用户
     */
    private String creatorUserId;

    /**
     * 修改时间
     */
    private String lastModifyTime;

    /**
     * 修改用户
     */
    private String lastModifyUserId;

    /**
     * 删除标志
     */
    private Integer deleteMark;

    /**
     * 删除时间
     */
    private String deleteTime;

    /**
     * 删除用户
     */
    private String deleteUserId;
}
