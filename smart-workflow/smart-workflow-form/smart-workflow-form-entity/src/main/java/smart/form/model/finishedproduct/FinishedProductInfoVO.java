package smart.form.model.finishedproduct;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 成品入库单
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 8:46
 */
@Data
public class FinishedProductInfoVO {
    @ApiModelProperty(value = "主键")
    private String id;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "流程主键")
    private String flowId;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "流程标题")
    private String flowTitle;
    @NotNull(message = "紧急程度不能为空")
    @ApiModelProperty(value = "紧急程度")
    private Integer flowUrgent;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "流程单据")
    private String billNo;
    @ApiModelProperty(value = "入库人")
    private String warehouseName;
    @ApiModelProperty(value = "仓库")
    private String warehouse;
    @NotNull(message = "入库时间不能为空")
    @ApiModelProperty(value = "入库时间")
    private Long reservoirDate;
    @ApiModelProperty(value = "备注")
    private String description;
    @ApiModelProperty(value = "明细")
    List<FinishedProductEntryEntityInfoModel> entryList;
}
