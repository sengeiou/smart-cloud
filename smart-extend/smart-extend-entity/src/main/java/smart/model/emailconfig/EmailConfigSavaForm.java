package smart.model.emailconfig;

import lombok.Data;

import java.util.Date;

/**
 * 保存邮箱配置
 */
@Data
public class EmailConfigSavaForm {

    private String creatorUserId;

    private String password;

    private String senderName;

    private String smtpHost;

    private Integer pop3Port;

    private String id;

    private Date creatorTime;

    private Integer smtpPort;

    private Integer emailSsl;

    private String account;

    private String pop3Host;
}
