package smart.base.model.dblink;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;


/**
 * 新建
 *
 */
@Data
public class DbLinkCrForm {
    @NotBlank(message = "必填")
    private String password;
    @NotBlank(message = "必填")
    private String port;
    @NotBlank(message = "必填")
    private String host;
    @NotBlank(message = "必填")
    private String dbType;
    @NotBlank(message = "必填")
    private String fullName;
    @NotBlank(message = "必填")
    private String userName;
    @NotBlank(message = "必填")
    private String serviceName;
    @NotNull(message = "必填")
    private boolean enabledMark;
    @ApiModelProperty(value = "排序码")
    private long sortCode;
}
