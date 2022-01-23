package smart.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.mapper.CheckLoginMapper;
import smart.base.service.CheckLoginService;
import smart.base.entity.EmailConfigEntity;
import smart.base.model.MailAccount;
import smart.base.util.EmailCheckUtil;
import smart.util.type.StringNumber;
import org.springframework.stereotype.Service;
/**
 * 邮箱验证业务接口实现类
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
@Service
public class CheckLoginServiceImpl extends ServiceImpl<CheckLoginMapper,EmailConfigEntity> implements CheckLoginService {
    @Override
    public String checkLogin(EmailConfigEntity configEntity) {
        MailAccount mailAccount = new MailAccount();
        mailAccount.setAccount(configEntity.getAccount());
        mailAccount.setPassword(configEntity.getPassword());
        mailAccount.setPop3Host(configEntity.getPop3Host());
        mailAccount.setPop3Port(configEntity.getPop3Port());
        mailAccount.setSmtpHost(configEntity.getSmtpHost());
        mailAccount.setSmtpPort(configEntity.getSmtpPort());
        if (StringNumber.ONE.equals(String.valueOf(configEntity.getEmailSsl()))) {
            mailAccount.setSsl(true);
        } else {
            mailAccount.setSsl(false);
        }
        if (mailAccount.getSmtpHost() != null) {
            return EmailCheckUtil.checkConnected(mailAccount);
        }
        if (mailAccount.getPop3Host() != null) {
            return EmailCheckUtil.checkConnected(mailAccount);
        }
        return "false";
    }
}
