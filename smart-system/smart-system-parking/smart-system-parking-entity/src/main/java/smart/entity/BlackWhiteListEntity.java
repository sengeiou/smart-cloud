package smart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * 黑白名单表
 *
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-12-17 17:24:37
 */
@Data
@TableName("p_black_white_list")
public class BlackWhiteListEntity {
    /**
     * 编号
     */
    @TableId("F_ID")
    @JsonProperty("id")
    private String id;

    /**
     * 0:白名单，1:黑名单
     */
    @TableField("F_LISTTYPE")
    @JsonProperty("listtype")
    private String listtype;

    /**
     * 停车场地ID,可以多个,用英文逗号,分隔
     */
    @TableField("F_PIDS")
    @JsonProperty("pids")
    private String pids;

    /**
     * 车牌号
     */
    @TableField("F_PLATENUMBER")
    @JsonProperty("platenumber")
    private String platenumber;

    /**
     * 名单有效开始时间
     */
    @TableField("F_STARTTIME")
    @JsonProperty("starttime")
    private String starttime;

    /**
     * 名单有效结束时间
     */
    @TableField("F_ENDTIME")
    @JsonProperty("endtime")
    private String endtime;

    /**
     * 有效标志
     */
    @TableField("F_ENABLEDMARK")
    @JsonProperty("enabledmark")
    private String enabledmark;

    /**
     * 创建时间
     */
    @TableField("F_CREATORTIME")
    @JsonProperty("creatortime")
    private Date creatortime;

    /**
     * 创建用户
     */
    @TableField("F_CREATORUSERID")
    @JsonProperty("creatoruserid")
    private String creatoruserid;

    /**
     * 修改时间
     */
    @TableField("F_LASTMODIFYTIME")
    @JsonProperty("lastmodifytime")
    private Date lastmodifytime;

    /**
     * 修改用户
     */
    @TableField("F_LASTMODIFYUSERID")
    @JsonProperty("lastmodifyuserid")
    private String lastmodifyuserid;

}
