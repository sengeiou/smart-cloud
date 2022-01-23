package smart.model.projectgantt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProjectGanttTaskInfoVO {
    @ApiModelProperty(value = "开始时间")
    private long startTime;
    @ApiModelProperty(value = "完成进度")
    private String schedule;
    @ApiModelProperty(value = "项目工期")
    private String timeLimit;
    @ApiModelProperty(value = "项目名称")
    private String fullName;
    @ApiModelProperty(value = "父级id")
    private String parentId;
    @ApiModelProperty(value = "主键id")
    private String id;
    @ApiModelProperty(value = "结束时间")
    private long endTime;
    @ApiModelProperty(value = "参与人员")
    private String managerIds;
    @ApiModelProperty(value = "项目描述")
    private String description;
    private String projectId;
    @ApiModelProperty(value = "标记颜色")
    private String signColor;
    @ApiModelProperty(value = "标记")
    private String sign;
}
