package smart.util;

import smart.base.model.MailAccount;
import smart.emnus.FileTypeEnum;
import smart.entity.EmailReceiveEntity;
import smart.file.FileApi;
import smart.model.MailFile;
import smart.util.type.StringNumber;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 邮箱工具类
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Slf4j
@Component
public class Pop3Util {
    @Autowired
    private FileApi fileApi;

    /**
     * 邮箱验证
     *
     * @param mailAccount
     * @return
     */
    public boolean checkConnected(MailAccount mailAccount) {
        try {
            Properties props = getProperties(mailAccount.getSsl());
            Session session = getSession(props);
            @Cleanup Store store = getStore(session, mailAccount);
            return true;
        } catch (Exception e) {
            e.getStackTrace();
            return false;
        }
    }

    /**
     * 接收邮件
     */
    public Map<String, Object> popMail(MailAccount mailAccount) {
        List<EmailReceiveEntity> entity = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        try {
            Properties props = getProperties(mailAccount.getSsl());
            Session session = getSession(props);
            Store store = getStore(session, mailAccount);
            Folder folder = getFolder(store);
            log.info("邮件总数: " + folder.getMessageCount());
            int receiveCount = folder.getMessageCount();
            Message[] messages = folder.getMessages();
            entity = parseMessage(messages);
            folder.close(true);
            store.close();
            map.put("receiveCount", receiveCount);
            map.put("mailList", entity);
            return map;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return map;
    }

    /**
     * 删除邮件
     *
     * @param mailAccount
     * @param mid
     */
    public void deleteMessage(MailAccount mailAccount, String mid) {
        try {
            Properties props = getProperties(false);
            Session session = getSession(props);
            Store store = getStore(session, mailAccount);
            Folder folder = getFolder(store);
            Message[] messages = folder.getMessages();
            deleteMessage(messages, mid);
            //释放资源
            folder.close(true);
            store.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 获取Properties
     *
     * @param ssl
     */
    private Properties getProperties(boolean ssl) {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "pop3");
        props.setProperty("mail.pop3.auth", "true");
        // 设置连接超时时间
        props.put("mail.pop3.connectiontimeout", "35000");
        // 设置读取超时时间
        props.put("mail.pop3.timeout", "10000");
        // 设置写入超时时间
        props.put("mail.pop3.writetimeout", "10000");
        if (ssl) {
            props.put("mail.pop3.ssl.enable", "true");
            props.put("mail.pop3.socketFactory.fallback", "false");
            props.setProperty( "mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        return props;
    }

    /**
     * 获取Session
     *
     * @param props
     */
    private Session getSession(Properties props) {
        Session session = Session.getInstance(props);
        session.setDebug(true);
        return session;
    }

    /**
     * 获取Store
     */
    private Store getStore(Session session, MailAccount mailAccount) throws Exception {
        Store store = session.getStore();
        store.connect(mailAccount.getPop3Host(), mailAccount.getPop3Port(), mailAccount.getAccount(), mailAccount.getPassword());
        return store;
    }

    /**
     * 获取Folder
     */
    private Folder getFolder(Store store) throws Exception {
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        return folder;
    }

    /**
     * 解析邮件
     *
     * @param messages 要解析的邮件列表
     */
    private List<EmailReceiveEntity> parseMessage(Message... messages) throws MessagingException, IOException {
        List<EmailReceiveEntity> receiveEntity = new ArrayList<>();
        if (messages == null || messages.length < 1) {
            throw new MessagingException("未找到要解析的邮件!");
        }
        List<MailFile> mailFiles = new ArrayList<>();
        String mailfiles = "";
        for (int i = 0, count = messages.length; i < count; i++) {
            MimeMessage msg = (MimeMessage) messages[i];
            mailfiles = null;
            boolean isContainerAttachment = isContainAttachment(msg);
            if (isContainerAttachment) {
                //邮件保存路径
                String path = fileApi.getPath(FileTypeEnum.MAIL);
                //保存附件
                mailFiles = saveAttachment(msg, path);
                mailfiles = JsonUtil.getObjectToString(mailFiles);
            }else {
                mailfiles="[]";
            }
            StringBuffer content = new StringBuffer(30);
            getMailTextContent(msg, content);
            EmailReceiveEntity entity = new EmailReceiveEntity();
            entity.setId(RandomUtil.uuId());
            entity.setMaccount(getReceiveAddress(msg, null));
            entity.setMID(getMessageId(msg));
            if(getFrom(msg)==null){
                entity.setSender("00000");
                entity.setSenderName("匿名");
            }else {
                entity.setSender(getFrom(msg).split("_")[0]);
                entity.setSenderName(getFrom(msg).split("_")[1]);
            }
            entity.setSubject(getSubject(msg));
            entity.setBodyText(content.toString());
            entity.setAttachment(mailfiles);
            entity.setFdate(msg.getSentDate());
            entity.setIsRead(0);
            receiveEntity.add(entity);
        }
        return receiveEntity;
    }

    /**
     * 解析邮件
     *
     * @param messages 要解析的邮件列表
     */
    private void deleteMessage(Message[] messages, String mid) throws MessagingException {
        if (messages == null || messages.length < 1) {
            throw new MessagingException("未找到要解析的邮件!");
        }
        for (int i = 0; i < messages.length; i++) {
            Message message = messages[i];
            MimeMessage msg = (MimeMessage) messages[i];
            if (deleteMid(msg, mid)) {
                message.setFlag(Flags.Flag.DELETED, true);
            }
        }
    }

    /**
     * 判断mid是否一致
     *
     * @param msg
     * @param mid
     * @return
     * @throws MessagingException
     */
    private boolean deleteMid(MimeMessage msg, String mid) throws MessagingException {
        String messageId = msg.getMessageID();
        messageId = messageId.replace("<", "");
        messageId = messageId.replace(">", "");
        if (messageId.equals(mid)) {
            return true;
        }
        return false;
    }

    /**
     * 获得邮件主题
     *
     * @param msg 邮件内容
     * @return 解码后的邮件主题
     */
    private String getSubject(MimeMessage msg) throws UnsupportedEncodingException, MessagingException {
        return MimeUtility.decodeText(msg.getSubject());
    }

    /**
     * 获得邮件发件人
     *
     * @param msg 邮件内容
     * @return 姓名 <Email地址>
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    private String getFrom(MimeMessage msg) throws MessagingException, UnsupportedEncodingException {
        String from = "";
        Address[] froms = msg.getFrom();
        InternetAddress address = (InternetAddress) froms[0];
        String person = address.getPersonal();
        if (person != null) {
            person = MimeUtility.decodeText(person) + " ";
        } else {
            person = "";
        }
        from = person + "_" + address.getAddress();
        return from;
    }

    /**
     * 获取邮件的id
     */
    private String getMessageId(MimeMessage msg) throws MessagingException {
        String messageId = msg.getMessageID();
        messageId = messageId.replace("<", "");
        messageId = messageId.replace(">", "");
        return messageId;
    }

    /**
     * 根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人
     * <p>Message.RecipientType.TO  收件人</p>
     * <p>Message.RecipientType.CC  抄送</p>
     * <p>Message.RecipientType.BCC 密送</p>
     *
     * @param msg  邮件内容
     * @param type 收件人类型
     * @return 收件人1 <邮件地址1>, 收件人2 <邮件地址2>, ...
     * @throws MessagingException
     */
    private String getReceiveAddress(MimeMessage msg, Message.RecipientType type) throws MessagingException {
        StringBuffer receiveAddress = new StringBuffer();
        Address[] addresss = null;
        if (type == null) {
            addresss = msg.getAllRecipients();
        } else {
            addresss = msg.getRecipients(type);
        }
        if (addresss == null || addresss.length < 1) {
            return null;
        }
        for (Address address : addresss) {
            InternetAddress internetAddress = (InternetAddress) address;
            receiveAddress.append(internetAddress.toUnicodeString()).append(",");
        }
        //删除最后一个逗号
        receiveAddress.deleteCharAt(receiveAddress.length() - 1);
        return receiveAddress.toString();
    }

    /**
     * 获得邮件发送时间
     *
     * @param msg 邮件内容
     * @return yyyy年mm月dd日 星期X HH:mm
     * @throws MessagingException
     */
    private String getSentDate(MimeMessage msg, String pattern) throws MessagingException {
        Date receivedDate = msg.getSentDate();
        if (receivedDate == null) {
            return "";
        }
        if (pattern == null || "".equals(pattern)) {
            pattern = "yyyy年MM月dd日 E HH:mm ";
        }
        return new SimpleDateFormat(pattern).format(receivedDate);
    }

    /**
     * 判断邮件中是否包含附件
     *
     * @return 邮件中存在附件返回true，不存在返回false
     * @throws MessagingException
     * @throws IOException
     */
    private boolean isContainAttachment(Part part) throws MessagingException, IOException {
        boolean flag = false;
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    flag = true;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    flag = isContainAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.contains("application")) {
                        flag = true;
                    }
                    if (contentType.contains("name")) {
                        flag = true;
                    }
                }
                if (flag) {
                    break;
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = isContainAttachment((Part) part.getContent());
        }
        return flag;
    }

    /**
     * 判断邮件是否已读
     *
     * @param msg 邮件内容
     * @return 如果邮件已读返回true, 否则返回false
     * @throws MessagingException
     */
    private boolean isSeen(MimeMessage msg) throws MessagingException {
        return msg.getFlags().contains(Flags.Flag.SEEN);
    }

    /**
     * 判断邮件是否需要阅读回执
     *
     * @param msg 邮件内容
     * @return 需要回执返回true, 否则返回false
     * @throws MessagingException
     */
    private boolean isReplySign(MimeMessage msg) throws MessagingException {
        boolean replySign = false;
        String[] headers = msg.getHeader("Disposition-Notification-To");
        if (headers != null) {
            replySign = true;
        }
        return replySign;
    }

    /**
     * 获得邮件的优先级
     *
     * @param msg 邮件内容
     * @return 1(High):紧急  3:普通(Normal)  5:低(Low)
     * @throws MessagingException
     */
    private String getPriority(MimeMessage msg) throws MessagingException {
        String priority = "普通";
        String[] headers = msg.getHeader("X-Priority");
        if (headers != null) {
            String headerPriority = headers[0];
            if (headerPriority.indexOf(StringNumber.ONE) != -1 || headerPriority.indexOf("High") != -1) {
                priority = "紧急";
            } else if (headerPriority.indexOf(StringNumber.FIVE) != -1 || headerPriority.indexOf("Low") != -1) {
                priority = "低";
            } else {
                priority = "普通";
            }
        }
        return priority;
    }

    /**
     * 获得邮件文本内容
     *
     * @param part    邮件体
     * @param content 存储邮件文本内容的字符串
     * @throws MessagingException
     * @throws IOException
     */
    private void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/html") && !isContainTextAttach) {
            content.append(part.getContent().toString());
        } else if (part.isMimeType("message/rfc822")) {
            getMailTextContent((Part) part.getContent(), content);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent(bodyPart, content);
            }
        }
    }

    /**
     * 保存附件
     *
     * @param part    邮件中多个组合体中的其中一个组合体
     * @param destDir 附件保存目录
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private List<MailFile> saveAttachment(Part part, String destDir) throws UnsupportedEncodingException, MessagingException,
            FileNotFoundException, IOException {
        List<MailFile> mailFiles = new ArrayList<>();
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    MailFile mailFile = new MailFile();
                    InputStream is = bodyPart.getInputStream();
                    //保存了错误的文件名，导致文件会重复
//                  saveFile(is, destDir, decodeText(bodyPart.getFileName()));
                    //解决附件中文乱码
                    String fileName=MimeUtility.decodeText(bodyPart.getFileName());
                    String fileType=fileName.split("\\.")[1];

                    mailFile.setFileId(RandomUtil.uuId()+"."+fileType);
                    saveFile(is, destDir, decodeText(mailFile.getFileId()));
                    File file = new File(destDir + decodeText(fileName));
                    mailFile.setFileName(fileName);
                    mailFile.setFileSize(String.valueOf(file.length()));
                    mailFile.setFileState("-1");
                    mailFile.setFileTime(DateUtil.getNow());
                    mailFiles.add(mailFile);
                } else if (bodyPart.isMimeType("multipart/*")) {
                    saveAttachment(bodyPart, destDir);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
                        saveFile(bodyPart.getInputStream(), destDir, decodeText(bodyPart.getFileName()));
                        File file = new File(destDir + decodeText(bodyPart.getFileName()));
                        MailFile mailFile = new MailFile();
                        mailFile.setFileId(RandomUtil.uuId());
                        mailFile.setFileName(file.getName());
                        mailFile.setFileSize(String.valueOf(file.length()));
                        mailFile.setFileState("-1");
                        mailFile.setFileTime(DateUtil.getNow());
                        mailFiles.add(mailFile);
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachment((Part) part.getContent(), destDir);
        }
        return mailFiles;
    }

    /**
     * 读取输入流中的数据保存至指定目录
     *
     * @param is       输入流
     * @param fileName 文件名
     * @param destDir  文件存储目录
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void saveFile(InputStream is, String destDir, String fileName)
            throws FileNotFoundException, IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(destDir + fileName)));
        int len = -1;
        while ((len = bis.read()) != -1) {
            bos.write(len);
            bos.flush();
        }
        bos.close();
        bis.close();
    }

    /**
     * 文本解码
     *
     * @param encodeText 解码MimeUtility.encodeText(String text)方法编码后的文本
     * @return 解码后的文本
     * @throws UnsupportedEncodingException
     */
    private String decodeText(String encodeText) throws UnsupportedEncodingException {
        if (encodeText == null || "".equals(encodeText)) {
            return "";
        } else {
            return MimeUtility.decodeText(encodeText);
        }
    }
}
