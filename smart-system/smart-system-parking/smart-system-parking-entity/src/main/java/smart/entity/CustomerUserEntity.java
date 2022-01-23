package smart.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 *
 * 客户用户表
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-11-17 10:02:02
 */
@Data
@TableName("p_customer_user")
public class CustomerUserEntity  {
    /** 编号 */
    @TableId("F_ID")
    @JsonProperty("id")
    private String id;

    /** 名称 */
    @TableField("F_USERNAME")
    @JsonProperty("username")
    private String username;

    /** 密码 */
    @TableField("F_PASSWORD")
    @JsonProperty("password")
    private String password;

    /** 手机号 */
    @TableField("F_MOBILE")
    @JsonProperty("mobile")
    private String mobile;

    /** 昵称 */
    @TableField("F_NICKNAME")
    @JsonProperty("nickname")
    private String nickname;

    /** 头像 */
    @TableField("F_AVATAR")
    @JsonProperty("avatar")
    private String avatar;

    /** 钱包余额 */
    @TableField("F_WALLETBALANCE")
    @JsonProperty("walletbalance")
    private String walletbalance;

    /** 小程序openid */
    @TableField("F_OPENIDSMALL")
    @JsonProperty("openidsmall")
    private String openidsmall;

    /** 公众号openid */
    @TableField("F_OPENIDPUBLIC")
    @JsonProperty("openidpublic")
    private String openidpublic;

    /** 公众号小程序关联ID */
    @TableField("F_UNIONID")
    @JsonProperty("unionid")
    private String unionid;

    /** 性别 1：男 2：女 0：未知 */
    @TableField("F_GENDER")
    @JsonProperty("gender")
    private String gender;

    /** 是否关注 0：否 1：是 */
    @TableField("F_ISFOLLOW")
    @JsonProperty("isfollow")
    private String isfollow;

    /** 关注时间 */
    @TableField("F_FOLLOWTIME")
    @JsonProperty("followtime")
    private Date followtime;

    /** 取关时间 */
    @TableField("F_UNFOLLOWTIME")
    @JsonProperty("unfollowtime")
    private Date unfollowtime;

    /** 所在国家 */
    @TableField("F_COUNTRY")
    @JsonProperty("country")
    private String country;

    /** 省份 */
    @TableField("F_PROVINCE")
    @JsonProperty("province")
    private String province;

    /** 城市 */
    @TableField("F_CITY")
    @JsonProperty("city")
    private String city;

    /** 用户类型 0：普通用户 1：其它 */
    @TableField("F_USERTYPE")
    @JsonProperty("usertype")
    private String usertype;

    /** 注册来源 0：微信小程序 1：其它 */
    @TableField("F_REGISTSOURCE")
    @JsonProperty("registsource")
    private String registsource;

    /** 乐观索版本号 */
    @TableField("F_VERSION")
    @JsonProperty("version")
    private String version;

    /** 有效标志 */
    @TableField("F_ENABLEDMARK")
    @JsonProperty("enabledmark")
    private String enabledmark;

    /** 创建时间 */
    @TableField("F_CREATORTIME")
    @JsonProperty("creatortime")
    private Date creatortime;

    /** 创建用户 */
    @TableField("F_CREATORUSERID")
    @JsonProperty("creatoruserid")
    private String creatoruserid;

    /** 修改时间 */
    @TableField("F_LASTMODIFYTIME")
    @JsonProperty("lastmodifytime")
    private Date lastmodifytime;

    /** 修改用户 */
    @TableField("F_LASTMODIFYUSERID")
    @JsonProperty("lastmodifyuserid")
    private String lastmodifyuserid;

}
