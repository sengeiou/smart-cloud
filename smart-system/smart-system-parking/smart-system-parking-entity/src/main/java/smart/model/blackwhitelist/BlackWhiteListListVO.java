package smart.model.blackwhitelist;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * BlackWhiteList模型
 *
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-12-17 17:24:37
 */
@Data
public class BlackWhiteListListVO {
    /**
     * 主键
     */
    private String id;

    /**
     * 0:白名单，1:黑名单
     */
    @JsonProperty("listtype")
    private Long listtype;

    /**
     * 停车场地ID,可以多个,用英文逗号,分隔
     */
    @JsonProperty("pids")
    private String[] pids;

    /**
     * 车牌号
     */
    @JsonProperty("platenumber")
    private String platenumber;

    /**
     * 名单有效开始时间
     */
    @JsonProperty("starttime")
    private String starttime;

    /**
     * 名单有效结束时间
     */
    @JsonProperty("endtime")
    private String endtime;

    /**
     * 有效标志
     */
    @JsonProperty("enabledmark")
    private String enabledmark;

    /**
     * 创建时间
     */
    //日期输出格式化
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("creatortime")
    private Date creatortime;

}
