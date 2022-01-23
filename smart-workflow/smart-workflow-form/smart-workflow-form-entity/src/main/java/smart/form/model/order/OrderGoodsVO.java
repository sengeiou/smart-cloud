package smart.form.model.order;

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
public class OrderGoodsVO {
    private String id;
    private String code;
    private String text;
    private String specifications;
    private String unit;
    private String price;
}
