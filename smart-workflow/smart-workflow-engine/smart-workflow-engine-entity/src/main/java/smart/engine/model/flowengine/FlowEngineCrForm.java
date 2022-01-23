package smart.engine.model.flowengine;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 9:16
 */
@Data
public class FlowEngineCrForm {
    @NotBlank(message = "流程编码不能为空")
    private String enCode;
    @NotBlank(message = "流程名称不能为空")
    private String fullName;
    @NotNull(message = "流程类型不能为空")
    private int type;
    @NotBlank(message = "流程分类不能为空")
    private String category;
    @NotBlank(message = "流程表单不能为空")
    private String formData;
    @NotNull(message = "流程分类不能为空")
    private int formType;
    private Integer visibleType;
    private String icon;
    private String iconBackground;
    private String version;
    @NotBlank(message = "流程引擎不能为空")
    private String flowTemplateJson;
    private String description;
    private Integer enabledMark;
    @ApiModelProperty(value = "排序")
    private Long sortCode;
    @ApiModelProperty(value = "关联表")
    private String tables;
}
