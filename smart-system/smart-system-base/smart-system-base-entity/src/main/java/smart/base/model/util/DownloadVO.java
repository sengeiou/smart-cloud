package smart.base.model.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DownloadVO {
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "请求接口")
    private String url;
}
