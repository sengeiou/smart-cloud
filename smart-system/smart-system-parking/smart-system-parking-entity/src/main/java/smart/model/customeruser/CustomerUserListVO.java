package smart.model.customeruser;


import lombok.Data;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class CustomerUserListVO{
    /** 主键 */
    private String id;

    /** 名称 */
    @JsonProperty("username")
    private String username;

    /** 手机号 */
    @JsonProperty("mobile")
    private String mobile;

    /** 昵称 */
    @JsonProperty("nickname")
    private String nickname;

    /** 钱包余额 */
    @JsonProperty("walletbalance")
    private String walletbalance;

    /** 小程序openid */
    @JsonProperty("openidsmall")
    private String openidsmall;

    /** 公众号openid */
    @JsonProperty("openidpublic")
    private String openidpublic;

    /** 性别 1：男 2：女 0：未知 */
    @JsonProperty("gender")
    private Long gender;

    /** 是否关注 0：否 1：是 */
    @JsonProperty("isfollow")
    private String isfollow;

    /** 关注时间 */
    //日期输出格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("followtime")
    private Date followtime;

    /** 取关时间 */
    //日期输出格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("unfollowtime")
    private Date unfollowtime;

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
    //日期输出格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("creatortime")
    private Date creatortime;

    /** 修改时间 */
    //日期输出格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("lastmodifytime")
    private Date lastmodifytime;

    /** 修改用户 */
    @JsonProperty("lastmodifyuserid")
    private String lastmodifyuserid;

}
