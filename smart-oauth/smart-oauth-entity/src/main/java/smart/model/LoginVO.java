package smart.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
//@Builder
public class LoginVO {
    @ApiModelProperty(value = "token")
    private String token;
    @ApiModelProperty(value = "主题")
    private String theme;
}
