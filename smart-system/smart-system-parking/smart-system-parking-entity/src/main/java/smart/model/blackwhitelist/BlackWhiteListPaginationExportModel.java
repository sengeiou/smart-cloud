package smart.model.blackwhitelist;


import smart.base.Pagination;
import lombok.Data;

/**
 * BlackWhiteList模型
 *
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-12-17 17:24:37
 */
@Data
public class BlackWhiteListPaginationExportModel extends Pagination {

    private String selectKey;

    private String json;

    private String dataType;

    /**
     * 0:白名单，1:黑名单
     */
    private Integer listtype;

    /**
     * 停车场地ID,可以多个,用英文逗号,分隔
     */
    private String pids;

    /**
     * 车牌号
     */
    private String platenumber;

}
