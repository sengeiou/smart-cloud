package smart.base.util;

import smart.base.model.MailAccount;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import javax.mail.Session;
import javax.mail.Transport;
import java.util.Properties;

/**
 * 邮箱验证工具类
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Slf4j
public class EmailCheckUtil {
    /**
     * 邮箱验证
     *
     * @param mailAccount
     * @return
     */
    public static String checkConnected(MailAccount mailAccount) {
        try {
            Properties props = getProperties(mailAccount.getSsl());
            Session session = getSession(props);
            @Cleanup Transport transport = getTransport(session, mailAccount);
            return "true";
        } catch (Exception e) {
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

    /**
     * 获取Properties
     *
     * @param ssl
     */
    private static Properties getProperties(boolean ssl) {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.timeout", "2500");
        // 设置接收超时时间
        props.put("mail.smtp.connectiontimeout", "5000");
        // 设置写入超时时间
        props.put("mail.smtp.writetimeout", "25000");
        if (ssl) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        return props;
    }

    /**
     * 获取Session
     *
     * @param props
     */
    private static Session getSession(Properties props) {
        Session session = Session.getInstance(props);
        session.setDebug(true);
        return session;
    }

    /**
     * 获取Transport
     */
    private static Transport getTransport(Session session, MailAccount mailAccount) throws Exception {
        Transport transport = session.getTransport();
        transport.connect(mailAccount.getSmtpHost(), mailAccount.getSmtpPort(), mailAccount.getAccount(), mailAccount.getPassword());
        return transport;
    }
}
