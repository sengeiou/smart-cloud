package smart.message.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 新建
 *
 */

@Data
public class NoticeCrForm {
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "内容")
    private String bodyText;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "标题")
    private String title;

}
