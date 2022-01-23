package smart.model.charges;


import lombok.Data;
import smart.base.Pagination;
import java.util.Date;
/**
 *
 * Charges模型
 * @版本： V3.1.0
 * @版权： SmartCloud项目开发组
 * @作者： SmartCloud
 * @日期： 2021-12-10 15:18:55
 */
@Data
public class ChargesPaginationExportModel extends Pagination {

    private String selectKey;

    private String json;

    private String dataType;

    /** 有效结束时间 */
    private Long end_time;

    /** 收费标准名称 */
    private String standard_name;

    /** 收费类型 1:自然日24小时 2:连续24小时 */
    private Integer standard_type;

    /** 有效开始时间 */
    private Long start_time;

}
