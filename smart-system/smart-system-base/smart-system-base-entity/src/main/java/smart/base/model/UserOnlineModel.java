package smart.base.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 在线用户
 */
@Data
public class UserOnlineModel {
    @JSONField(name="UserId")
    private String userId;
    @JSONField(name = "UserAccount")
    private String userAccount;
    @JSONField(name = "UserName")
    private String userName;
    @JSONField(name = "LoginTime")
    private String loginTime;
    @JSONField(name = "LoginIPAddress")
    private String loginIPAddress;
    @JSONField(name = "LoginPlatForm")
    private String loginPlatForm;
    @JSONField(name = "TenantId")
    private String tenantId;
    @JSONField(name = "Token")
    private String token;
}
