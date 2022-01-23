package smart.base.vo;

import lombok.Data;

/**
 * 需要分页的模型
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-15 09:50
 */
@Data
public class PaginationVO {
    private Long currentPage;
    private Long pageSize;
    private Integer total;
}
