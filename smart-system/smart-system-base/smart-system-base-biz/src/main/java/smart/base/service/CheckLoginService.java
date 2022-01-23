package smart.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import smart.base.entity.EmailConfigEntity;
/**
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021-03-23
 */
public interface CheckLoginService extends IService<EmailConfigEntity> {
    /**
     * 邮箱验证
     *
     * @param configEntity
     * @return
     */
    String checkLogin(EmailConfigEntity configEntity);
}
