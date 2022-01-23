package smart.engine.model.flowtask;

import smart.base.PaginationTime;
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
public class PaginationFlowTask extends PaginationTime {
  /**所属流程**/
  private String flowId;
  /**所属分类**/
  private String flowCategory;
  private String creatorUserId;
}
