package smart.model;

import lombok.Data;

/**
 * 系统的核心基础信息
 *
 * @author SmartCloud项目开发组
 * @version V1.2.191207
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Data
public class BaseSystemInfo {
    /**
     *  单一登录：1-后登录踢出先登录、2-已登录禁止再登录
     */
    private String singleLogin;
    /**
     *  超时登出时间小时
     */
    private String tokenTimeout;
    /**
     *  上次登录时间提示开关
     */
    private Integer lastLoginTimeSwitch=0;
    /**
     * 公司电话
     */
    private String companyTelePhone;
    /**
     * appid
     */
    private String wxGzhAppId;
    /**
     * 公司地址
     */
    private String companyAddress;

    private String wxGzhAppSecret;

    private String qyhCorpSecret;

    private String isLog;

    private String emailSmtpPort;

    private String emailPop3Host;

    private String emailSenderName;
    /**
     * 公司邮箱
     */
    private String companyEmail;

    private String sysName;
    /**
     * 版权信息
     */
    private String copyright;

    private String qyhAgentId;

    private String lastLoginTime;

    private String emailAccount;

    private String qyhJoinUrl;

    private String whitelistSwitch;

    private String pageSize;
    /**
     * 系统描述
     */
    private String sysDescription;

    private String emailPassword;
    /**
     * 公司法人
     */
    private String companyContacts;
    /**
     * 系统主题
     */
    private String sysTheme;

    private String qyhAgentSecret;

    private String whitelistIp;
    /**
     * 公司简称
     */
    private String companyCode;

    private String emailSsl;

    private String emailSmtpHost;

    private String registerKey;

    private String wxGzhToken;

    private String qyhJoinTitle;

    private String qyhCorpId;
    /**
     * 系统版本
     */
    private String sysVersion;

    private String emailPop3Port;
    /**
     * 公司名称
     */
    private String companyName;

    private String wxGzhUrl;
}
