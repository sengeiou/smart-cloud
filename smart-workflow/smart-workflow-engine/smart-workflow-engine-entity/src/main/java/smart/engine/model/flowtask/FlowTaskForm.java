package smart.engine.model.flowtask;

import lombok.Data;

/**
 *
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/15 9:17
 */
@Data
public class FlowTaskForm {
    /**引擎id**/
    private String flowId;
    /**界面数据**/
    private String data;
    /**0.提交 1.保存**/
    private String status;
    /**指定用户**/
    private String freeApproverUserId;
}
