package smart.model.customeruser;


import lombok.Data;
import smart.base.Pagination;
import java.util.Date;
/**
 *
 * CustomerUser模型
 * @版本： V3.1.0
 * @版权： 智慧停车公司
 * @作者： 开发平台组
 * @日期： 2021-11-17 10:02:02
 */
@Data
public class CustomerUserPagination extends Pagination {
    /** 名称 */
    private String username;

    /** 手机号 */
    private String mobile;

    /** 昵称 */
    private String nickname;

    /** 性别 1：男 2：女 0：未知 */
    private Integer gender;

    /** 是否关注 0：否 1：是 */
    private String isfollow;

    /** 用户类型 0：普通用户 1：其它 */
    private String usertype;

    /** 注册来源 0：微信小程序 1：其它 */
    private String registsource;

}
