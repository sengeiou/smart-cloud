package smart.engine.model.flowengine;

import smart.base.Pagination;
import lombok.Data;

/**
 *
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 9:17
 */
@Data
public class PaginationFlowEngine extends Pagination {
    private String formType;
    private String enabledMark;
}
