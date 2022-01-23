package smart.engine.model;

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
public class FlowHandleModel {
    /**意见**/
    private String handleOpinion;
    /**指定人**/
    private String freeApproverUserId;
    /**表单数据**/
    private Object formData;
    /**编码**/
    private String enCode;
}
