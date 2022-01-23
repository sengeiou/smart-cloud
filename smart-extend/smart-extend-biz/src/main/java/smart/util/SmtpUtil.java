package smart.util;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import smart.base.model.MailAccount;
import smart.model.MailFile;
import smart.model.MailModel;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Properties;
/**
 * 邮箱工具类
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Slf4j
public class SmtpUtil {

    private String host = null;
    private Integer port = null;
    private String username = null;
    private String password = null;
    private String emailform = null;
    private String timeout = "2500";
    private String personal = null;
    private JavaMailSenderImpl mailSender = null;

    //------------------------------原始发送邮件--------------------------------------
    /**
     * 邮箱验证
     *
     * @param mailAccount
     * @return
     */
    public static boolean checkConnected(MailAccount mailAccount) {
        try {
            Properties props = getProperties(mailAccount.getSsl());
            Session session = getSession(props);
            @Cleanup Transport transport = getTransport(session, mailAccount);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
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
     * 获取Transport
     */
    private static Transport getTransport(Session session, MailAccount mailAccount) throws Exception {
        Transport transport = session.getTransport();
        transport.connect(mailAccount.getSmtpHost(), mailAccount.getSmtpPort(), mailAccount.getAccount(), mailAccount.getPassword());
        return transport;
    }

    /**
     * springboot发送邮件
     * @param mailAccount
     */
    public SmtpUtil(MailAccount mailAccount) {
        host = mailAccount.getSmtpHost();
        port = mailAccount.getSmtpPort();
        username = mailAccount.getAccount();
        password = mailAccount.getPassword();
        emailform = mailAccount.getAccount();
        personal = mailAccount.getAccountName();
        mailSender = createMailSender();
    }

    /**
     * 邮件发送器
     *
     * @return 配置好的工具
     */
    private JavaMailSenderImpl createMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(password);
        sender.setDefaultEncoding(Constants.UTF_8);
        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.timeout", timeout);
        // 设置接收超时时间
        props.put("mail.smtp.connectiontimeout", "5000");
        // 设置写入超时时间
        props.put("mail.smtp.writetimeout", "25000");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.starttls.required", "true");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        sender.setJavaMailProperties(props);
        return sender;
    }

    /**
     * 发送邮件
     *
     * @param mailModel 邮件实体
     * @throws Exception 异常
     */
    public void sendMail(String path, MailModel mailModel) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, Constants.UTF_8);
        messageHelper.setFrom(emailform,mailModel.getFromName());
        messageHelper.setTo(mailModel.getRecipient());
        if (mailModel.getBcc() != null) {
            messageHelper.setBcc(mailModel.getBcc());
        }
        if (mailModel.getCc() != null) {
            messageHelper.setCc(mailModel.getCc());
        }
        messageHelper.setSubject(mailModel.getSubject());
        messageHelper.setText(mailModel.getBodyText(), true);
        if (mailModel.getAttachment().size() > 0) {
            for (MailFile mailFile : mailModel.getAttachment()) {
                File file = new File(path + mailFile.getFileId());
                String fileName = mailFile.getFileName();
                messageHelper.addAttachment(fileName, file);
            }
        }
        mailSender.send(mimeMessage);
    }
}
