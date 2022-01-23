package smart.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
@Data
public class MPConfigModel{
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "公众号App")
    public String wx_GZH_APPID;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "公众号A应用凭证")
    public String wx_GZH_APPSECRET;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "公众号服务器地址")
    public String wx_GZH_URL;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "令牌token")
    public String wx_GZH_TOKEN;
}
