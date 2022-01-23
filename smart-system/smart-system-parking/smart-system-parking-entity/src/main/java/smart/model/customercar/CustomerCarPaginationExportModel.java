package smart.model.customercar;


import lombok.Data;
import smart.base.Pagination;
import java.util.Date;
/**
 *
 * CustomerCar模型
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-11-17 11:44:41
 */
@Data
public class CustomerCarPaginationExportModel extends Pagination {

    private String selectKey;

    private String json;

    private String dataType;

    /** 所属车主用户id */
    private String cuid;

    /** 车牌号 */
    private String platenumber;

    /** 号牌类型:1,小型汽车	,2大型汽车,3专用汽车,3特种车,3新能源汽车 */
    private String platetype;

    /** 车辆类型:1临时车 2月租车 3储值车 4免费车 */
    private String cartype;

    /** 车辆识别代号VIN */
    private String vin;

    /** 有效标志 */
    private String enabledmark;

}
