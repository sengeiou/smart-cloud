package smart.base;

import lombok.Data;

/**
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:51
 */
@Data
public class PaginationTime extends Pagination{
    private String startTime;
    private String endTime;
//    private String type;
}
