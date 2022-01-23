package smart.base.model.monitor;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MonitorListVO {
    @ApiModelProperty(value = "系统信息")
    private SystemModel system;
    @ApiModelProperty(value = "CPU信息")
    private CpuModel cpu;
    @ApiModelProperty(value = "内存信息")
    private MemoryModel memory;
    @ApiModelProperty(value = "硬盘信息")
    private DiskModel disk;
    @ApiModelProperty(value = "当前时间")
    private long time;
}
