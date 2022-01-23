package smart.base.model.monitor;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CpuModel {
    @ApiModelProperty(value = "cpu名称")
    private String name;
    @ApiModelProperty(value = "物理CPU个数")
    @JSONField(name="package")
    private String packageName;
    @ApiModelProperty(value = "CPU内核个数")
    private String core;
    @ApiModelProperty(value = "内核个数")
    private int coreNumber;
    @ApiModelProperty(value = "逻辑CPU个数")
    private String logic;
    @ApiModelProperty(value = "CPU已用百分比")
    private String used;
    @ApiModelProperty(value = "未用百分比")
    private String idle;
}
