package smart.engine.model.flowtask;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 9:17
 */
@Data
public class FlowTaskInfoVO {
    @ApiModelProperty(value = "主键id")
    private String id;
    @ApiModelProperty(value = "引擎id")
    private String flowId;
    @ApiModelProperty(value = "界面数据")
    private String data;
}
