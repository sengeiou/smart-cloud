package smart.form.model.order;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单信息
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 8:46
 */
@Data
public class OrderInfoOrderReceivableModel {
    @ApiModelProperty(value = "自然主键")
    private String id;
    @ApiModelProperty(value = "收款日期")
    private Long receivableDate;
    @ApiModelProperty(value = "收款比率", example = "1")
    private int receivableRate;
    @ApiModelProperty(value = "收款金额")
    private String receivableMoney;
    @ApiModelProperty(value = "收款方式")
    private String receivableMode;
    @ApiModelProperty(value = "收款摘要")
    @JSONField(name = "abstract")
    private String fabstract;
    @ApiModelProperty(value = "排序", example = "1")
    private int sortCode;
    @ApiModelProperty(value = "收款状态")
    private String receivableState;
    @ApiModelProperty(value = "订单主键")
    private String orderId;
    @ApiModelProperty(value = "描述")
    private String description;

}
