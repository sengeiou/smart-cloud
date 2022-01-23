package smart.model.tableexample;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TableExampleListDTO {

    @ApiModelProperty(value = "负责人")
    private String principal;

    @ApiModelProperty(value = "交互日期")
    private Date interactionDate;

    @ApiModelProperty(value = "立顶人")
    private String jackStands;

    @ApiModelProperty(value = "项目编码")
    private String projectCode;

    @ApiModelProperty(value = "项目阶段")
    private String projectPhase;

    @ApiModelProperty(value = "已用金额")
    private Long tunesAmount;

    @ApiModelProperty(value = "项目类型")
    private String projectType;

    @ApiModelProperty(value = "费用金额")
    private Long costAmount;

    @ApiModelProperty(value = "预计收入")
    private Long projectedIncome;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "客户名称")
    private String customerName;




    @ApiModelProperty(value = "批注总数")
    private String postilCount;

    @ApiModelProperty(value = "批注列表Json")
    private String postilJson;

    @ApiModelProperty(value = "编辑时间")
    private Date lastModifyTime;

    @ApiModelProperty(value = "编辑用户")
    private String lastModifyUserId;



    @ApiModelProperty(value = "标记")
    private String sign;

    @ApiModelProperty(value = "登记人")
    private String registrant;

    @ApiModelProperty(value = "登记时间")
    private Date registerDate;

    @ApiModelProperty(value = "自然主键")
    private String id;

    @ApiModelProperty(value = "排序码")
    private String sortCode;

    @ApiModelProperty(value = "有效标志")
    private Integer enabledMark;

}
