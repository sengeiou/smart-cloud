package smart.model.activity;


import smart.base.Pagination;
import lombok.Data;

/**
 * Activity模型
 *
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-12-30 17:41:03
 */
@Data
public class ActivityPaginationExportModel extends Pagination {

    private String selectKey;

    private String json;

    private String dataType;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动类型：0充值赠送类;  1满减折扣类(数据字典)
     */
    private String activitytype;

    /**
     * 活动开始时间
     */
    private Long starttime;

    /**
     * 活动结束时间
     */
    private Long endtime;

    /**
     * 活动状态: 0未开始;  1进行中;  2已结束
     */
    private String status;

    /**
     * 活动发起人
     */
    private String originator;

    /**
     * 有效标志
     */
    private String enabledmark;

}
