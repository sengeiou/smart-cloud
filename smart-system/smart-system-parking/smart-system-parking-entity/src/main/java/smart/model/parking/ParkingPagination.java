package smart.model.parking;


import lombok.Data;
import smart.base.Pagination;
/**
 *
 * Parking模型
 * @版本： V3.1.0
 * @版权： SmartCloud项目开发组
 * @作者： SmartCloud
 * @日期： 2021-11-15 14:30:27
 */
@Data
public class ParkingPagination extends Pagination {
    /** 编号 */
    private String paid;

    /** 名称 */
    private String name;

    /** 0:路侧停车场 1：商业园区 2：住宅社区 3：写字楼 4：交通枢纽 */
    private String type;

    /** 省份 */
    private String leveladdress;

    /** 车场管理员 */
    private String contactuserid;

    /** 是否自营 0：否 1：是 */
    private String isselfsupport;

    /** 是否支持预支付 0：否 1：是 */
    private String issupportadvancepayment;

    /** 有效标志 */
    private String enabledmark;

}
