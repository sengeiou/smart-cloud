package smart.form.model.order;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 订单信息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 8:46
 */
@Data
public class OrderReceivableModel {
    @ApiModelProperty(value = "")
    private String remove;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "自然主键")
    private String id;
    @NotNull(message = "必填")
    @ApiModelProperty(value = "收款日期")
    private Long receivableDate;
    @NotNull(message = "必填")
    @ApiModelProperty(value = "收款比率")
    private int receivableRate;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "收款金额")
    private String receivableMoney;
    @NotBlank(message = "必填")
    @ApiModelProperty(value = "收款方式")
    private String receivableMode;
    @ApiModelProperty(value = "收款摘要")
    @JSONField(name = "abstract")
    private String fabstract;
    @ApiModelProperty(value = "")
    private String index;
    private String description;


}
