package smart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.entity.EmailConfigEntity;
import smart.mapper.EmailConfigMapper;
import smart.service.EmailConfigService;
import org.springframework.stereotype.Service;


/**
 * 邮件配置
 *
 * @copyright 智慧停车公司
 * @author 开发平台组
 * @date 2019年9月26日 上午9:18
 */
@Service
public class EmailConfigServiceImpl extends ServiceImpl<EmailConfigMapper, EmailConfigEntity> implements EmailConfigService {

}
