package smart.engine.model.flowbefore;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 9:18
 */
@Data
public class FlowTaskOperatorRecordEntityInfoModel {
    @ApiModelProperty(value = "节点流转主键")
    private String id;
    @ApiModelProperty(value = "节点编码")
    private String nodeCode;
    @ApiModelProperty(value = "节点名称")
    private String nodeName;
    @ApiModelProperty(value = "经办状态 0-拒绝、1-同意、2-提交、3-撤回、4-终止")
    private Integer handleStatus;
    @ApiModelProperty(value = "经办人员")
    private String handleId;
    @ApiModelProperty(value = "经办时间")
    private Long handleTime;
    @ApiModelProperty(value = "经办理由")
    private String handleOpinion;
    @ApiModelProperty(value = "经办主键")
    private String taskOperatorId;
    @ApiModelProperty(value = "节点主键")
    private String taskNodeId;
    @ApiModelProperty(value = "任务主键")
    private String taskId;
    @ApiModelProperty(value = "用户名称")
    private String userName;
}
