package smart.model.parkingarea;


import lombok.Data;
import smart.base.Pagination;
import java.util.Date;
/**
 *
 * ParkingArea模型
 * @版本： V3.1.0
 * @版权： SmartCloud项目开发组
 * @作者： SmartCloud
 * @日期： 2021-11-15 15:56:54
 */
@Data
public class ParkingAreaPagination extends Pagination {
    /** 名称 */
    private String name;

    /** 片区巡检员 */
    private String contactuserid;

    /** 有效标志 */
    private String enabledmark;

}
