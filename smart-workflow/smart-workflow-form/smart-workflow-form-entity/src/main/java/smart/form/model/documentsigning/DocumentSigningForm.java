package smart.form.model.documentsigning;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 文件签阅表
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 8:46
 */
@Data
public class DocumentSigningForm {
    @ApiModelProperty(value = "相关附件")
    private String fileJson;
    @ApiModelProperty(value = "文件名称")
    private String fileName;
    @NotNull(message = "紧急程度不能为空")
    @ApiModelProperty(value = "紧急程度")
    private Integer flowUrgent;
    @ApiModelProperty(value = "签阅人")
    private String reader;
    @ApiModelProperty(value = "文件拟办")
    private String fillPreparation;
    @ApiModelProperty(value = "文件内容")
    private String documentContent;
    @NotNull(message = "签阅时间不能为空")
    @ApiModelProperty(value = "签阅时间")
    private Long  checkDate;
    @ApiModelProperty(value = "文件编码")
    private String fillNum;
    @ApiModelProperty(value = "拟稿人")
    private String draftedPerson;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "流程标题")
    private String flowTitle;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "流程主键")
    private String flowId;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "流程单据")
    private String billNo;
    @NotNull(message = "必填")
    @ApiModelProperty(value = "发稿日期")
    private Long  publicationDate;
    @ApiModelProperty(value = "建议栏")
    private Long  adviceColumn;
    @ApiModelProperty(value = "提交/保存 0-1")
    private String status;
}
