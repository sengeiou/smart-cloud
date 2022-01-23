package smart.base.model.logmodel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RequestLogVO {
    @ApiModelProperty(value = "id")
    private String id;
    @ApiModelProperty(value = "请求时间",example = "1")
    private Long creatorTime;
    @ApiModelProperty(value = "请求用户名")
    private String userName;
    @ApiModelProperty(value = "请求IP")
    private String iPAddress;
    @ApiModelProperty(value = "请求设备")
    private String platForm;
    @ApiModelProperty(value = "请求地址")
    private String requestURL;
    @ApiModelProperty(value = "请求类型")
    private String requestMethod;
    @ApiModelProperty(value = "请求耗时",example = "1")
    private Long requestDuration;
}
