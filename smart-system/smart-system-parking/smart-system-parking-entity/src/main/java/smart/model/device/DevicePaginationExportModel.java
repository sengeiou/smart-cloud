package smart.model.device;


import lombok.Data;
import smart.base.Pagination;
import java.util.Date;
/**
 *
 * Device模型
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-11-28 12:22:21
 */
@Data
public class DevicePaginationExportModel extends Pagination {

    private String selectKey;

    private String json;

    private String dataType;

    /** 设备名称 */
    private String name;

    /** 系统编号 */
    private String code;

    /** 设备SN */
    private String sn;

    /** 上网方式 */
    private String networktype;

    /** 设备状态 */
    private String devicestatus;

    /** 在线状态 */
    private String onlinestatus;

    /** 告警状态 */
    private String alarmstatus;

    /** 有效标志 */
    private String enabledmark;

}
