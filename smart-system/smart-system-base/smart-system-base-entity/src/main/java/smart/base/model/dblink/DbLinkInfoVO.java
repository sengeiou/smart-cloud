package smart.base.model.dblink;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DbLinkInfoVO extends DbLinkUpForm{
    @ApiModelProperty(value = "主键")
    private String id;
}
