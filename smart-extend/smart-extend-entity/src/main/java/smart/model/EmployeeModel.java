package smart.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.List;

/**
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 *
 */
@Data
public class EmployeeModel {
    @Excel(name = "工号")
    private String enCode;
    @Excel(name = "姓名")
    private String fullName;
    @Excel(name = "性别")
    private String gender;
    @Excel(name = "部门")
    private String departmentName;
    @Excel(name = "职务")
    private String positionName;
    @Excel(name = "用工性质")
    private String workingNature;
    @Excel(name = "身份证号")
    private String idNumber;
    @Excel(name = "联系电话")
    private String telephone;
    @Excel(name = "参加工作")
    private String attendWorkTime;
    @Excel(name = "出生年月")
    private String birthday;
    @Excel(name = "最高学历")
    private String education;
    @Excel(name = "所学专业")
    private String major;
    @Excel(name = "毕业院校")
    private String graduationAcademy;
    @Excel(name = "毕业时间")
    private String graduationTime;
    private List<EmployeeModel> list;
}
