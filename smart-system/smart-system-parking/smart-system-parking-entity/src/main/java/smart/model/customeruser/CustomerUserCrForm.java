package smart.model.customeruser;


import lombok.Data;
import java.util.List;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * CustomerUser模型
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-11-17 10:02:02
 */
@Data
public class CustomerUserCrForm  {
    /** 名称 */
    @JsonProperty("username")
    private String username;

    /** 手机号 */
    @JsonProperty("mobile")
    private String mobile;

    /** 昵称 */
    @JsonProperty("nickname")
    private String nickname;

    /** 头像 */
    @JsonProperty("avatar")
    private String avatar;

    /** 钱包余额 */
    @JsonProperty("walletbalance")
    private BigDecimal walletbalance;

    /** 小程序openid */
    @JsonProperty("openidsmall")
    private String openidsmall;

    /** 公众号openid */
    @JsonProperty("openidpublic")
    private String openidpublic;

    /** 性别 1：男 2：女 0：未知 */
    @JsonProperty("gender")
    private Integer gender;

    /** 是否关注 0：否 1：是 */
    @JsonProperty("isfollow")
    private String isfollow;

    /** 关注时间 */
    @JsonProperty("followtime")
    private String followtime;

    /** 取关时间 */
    @JsonProperty("unfollowtime")
    private String unfollowtime;

    /** 所在国家 */
    @JsonProperty("country")
    private String country;

    /** 省份 */
    @JsonProperty("province")
    private String province;

    /** 城市 */
    @JsonProperty("city")
    private String city;

    /** 用户类型 0：普通用户 1：其它 */
    @JsonProperty("usertype")
    private String usertype;

    /** 注册来源 0：微信小程序 1：其它 */
    @JsonProperty("registsource")
    private String registsource;

    /** 创建时间 */
    @JsonProperty("creatortime")
    private String creatortime;

    /** 修改时间 */
    @JsonProperty("lastmodifytime")
    private String lastmodifytime;

    /** 修改用户 */
    @JsonProperty("lastmodifyuserid")
    private String lastmodifyuserid;


}
