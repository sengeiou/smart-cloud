package smart.engine.model.flowengine;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 9:17
 */
@Data
public class FlowEngineListVO {
    @ApiModelProperty(value = "主键id")
    private String id;
    @ApiModelProperty(value = "流程编码")
    private String enCode;
    @ApiModelProperty(value = "流程名称")
    private String fullName;
    @ApiModelProperty(value = "流程类型")
    private Integer type;
    @ApiModelProperty(value = "流程分类")
    private String category;
    @ApiModelProperty(value = "表单类型 1-系统表单、2-动态表单")
    private Integer formType;
    @ApiModelProperty(value = "可见类型 0-全部可见、1-指定经办")
    private Integer visibleType;
    @ApiModelProperty(value = "图标")
    private String icon;
    @ApiModelProperty(value = "图标背景色")
    private String iconBackground;
    @ApiModelProperty(value = "创建人")
    private String creatorUser;
    @ApiModelProperty(value = "创建时间")
    private Long creatorTime;
    @ApiModelProperty(value = "修改人")
    private String lastModifyUser;
    @ApiModelProperty(value = "修改时间")
    private Long lastModifyTime;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "有效标志")
    private Integer enabledMark;
    @ApiModelProperty(value = "排序码")
    private Long sortCode;
}
