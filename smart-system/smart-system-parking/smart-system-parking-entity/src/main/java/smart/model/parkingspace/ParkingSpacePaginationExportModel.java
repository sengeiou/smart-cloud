package smart.model.parkingspace;


import lombok.Data;
import smart.base.Pagination;
/**
 *
 * ParkingSpace模型
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-11-12 13:48:03
 */
@Data
public class ParkingSpacePaginationExportModel extends Pagination {

    private String selectKey;

    private String json;

    private String dataType;

    /** 停车场地ID */
    private String pid;

    /** 设备序列号 */
    private String device;

    /** 泊位名称 */
    private String name;

    /** 车位类型：0:默认 1：共享 */
    private String type;

    /** 是否充电桩车位 0:否 1：是 */
    private String ischarging;

    /** 有效标志 */
    private String enabledmark;

    /** 创建时间 */
    private Long creatortime;

}
