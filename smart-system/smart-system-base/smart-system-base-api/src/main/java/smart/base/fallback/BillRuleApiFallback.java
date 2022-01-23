package smart.base.fallback;

import smart.base.BillRuleApi;
import smart.base.ActionResult;
import org.springframework.stereotype.Component;

/**
 * 获取单据规则Api降级处理
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-24
 */
@Component
public class BillRuleApiFallback implements BillRuleApi {

    @Override
    public ActionResult useBillNumber(String enCode) {
        return null;
    }

    @Override
    public ActionResult<String> getBillNumber(String enCode) {
        return null;
    }
}
